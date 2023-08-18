package org.cardano.foundation.voting.service.vote;

import com.bloxbean.cardano.client.util.HexUtil;
import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.TransactionDetails;
import org.cardano.foundation.voting.domain.VoteReceipt;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.Proposal;
import org.cardano.foundation.voting.domain.entity.Vote;
import org.cardano.foundation.voting.domain.entity.VoteMerkleProof;
import org.cardano.foundation.voting.domain.web3.SignedWeb3Request;
import org.cardano.foundation.voting.domain.web3.Web3Action;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.cardano.foundation.voting.service.address.StakeAddressVerificationService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;
import org.cardano.foundation.voting.service.expire.ExpirationService;
import org.cardano.foundation.voting.service.json.JsonService;
import org.cardano.foundation.voting.service.merkle_tree.MerkleProofSerdeService;
import org.cardano.foundation.voting.service.merkle_tree.VoteMerkleProofService;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardano.foundation.voting.service.voting_power.VotingPowerService;
import org.cardano.foundation.voting.utils.Enums;
import org.cardano.foundation.voting.utils.MoreUUID;
import org.cardanofoundation.cip30.AddressFormat;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.cardanofoundation.merkle.ProofItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.VoteReceipt.Status.*;
import static org.cardano.foundation.voting.domain.VotingEventType.*;
import static org.cardano.foundation.voting.domain.web3.Web3Action.CAST_VOTE;
import static org.cardano.foundation.voting.domain.web3.Web3Action.VIEW_VOTE_RECEIPT;
import static org.cardano.foundation.voting.utils.MoreNumber.isNumeric;
import static org.cardanofoundation.cip30.MessageFormat.TEXT;
import static org.cardanofoundation.cip30.ValidationError.UNKNOWN;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.NOT_FOUND;

@Service
@Slf4j
public class DefaultVoteService implements VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private ReferenceDataService referenceDataService;

    @Autowired
    private ExpirationService expirationService;

    @Autowired
    private VoteMerkleProofService voteMerkleProofService;

    @Autowired
    private MerkleProofSerdeService merkleProofSerdeService;

    @Autowired
    private VotingPowerService votingPowerService;

    @Autowired
    private CardanoNetwork cardanoNetwork;

    @Autowired
    private JsonService jsonService;

    @Autowired
    private StakeAddressVerificationService stakeAddressVerificationService;

    @Autowired
    private BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService;

    @Override
    @Transactional
    public List<Vote> findAll(Event event) {
        return voteRepository.findAllByEventId(event.getId());
    }

    @Override
    public Optional<Vote> findById(String voteId) {
        return voteRepository.findById(voteId);
    }

    @Transactional
    @Timed(value = "service.vote.isVoteCastingStillPossible", percentiles = { 0.3, 0.5, 0.95 })
    public Either<Problem, Boolean> isVoteCastingStillPossible(String eventId, String voteId) {
        var maybeEvent = referenceDataService.findValidEventByName(eventId);
        if (maybeEvent.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("EVENT_NOT_FOUND")
                    .withDetail("Event not found, eventId:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var event = maybeEvent.orElseThrow();

        boolean isInactive = expirationService.isEventInactive(event);
        if (isInactive) {
            return Either.left(Problem.builder()
                    .withTitle("EVENT_INACTIVE")
                    .withDetail("Event is inactive, eventId:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var maybeExistingVote = voteRepository.findById(voteId);
        if (maybeExistingVote.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("VOTE_NOT_FOUND")
                    .withDetail("Vote not found, voteId:" + voteId)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var maybeExistingProof = voteMerkleProofService.findLatestProof(eventId, voteId);
        if (maybeExistingProof.isPresent()) {
            return Either.left(Problem.builder()
                    .withTitle("VOTE_CANNOT_BE_CHANGED")
                    .withDetail("Vote cannot be changed, voteId:" + voteId)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        return Either.right(true);
    }

    @Override
    @Transactional
    @Timed(value = "service.vote.castVote", percentiles = { 0.3, 0.5, 0.95 })
    public Either<Problem, Vote> castVote(SignedWeb3Request castVoteRequest) {
        var cip30Verifier = new CIP30Verifier(castVoteRequest.getCoseSignature(), castVoteRequest.getCosePublicKey());
        var cip30VerificationResult = cip30Verifier.verify();

        if (!cip30VerificationResult.isValid()) {
            log.warn("CIP-30 data sign for casting vote verification failed, validationError:{}", cip30VerificationResult.getValidationError().orElse(UNKNOWN));

            return Either.left(
                    Problem.builder()
                        .withTitle("INVALID_CIP30_DATA_SIGNATURE")
                        .withDetail("Invalid cast vote cose signature!")
                        .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var maybeAddress = cip30VerificationResult.getAddress(AddressFormat.TEXT);
        if (maybeAddress.isEmpty()) {
            log.warn("Address not found in the signed data");

            return Either.left(
                    Problem.builder()
                            .withTitle("ADDRESS_NOT_FOUND")
                            .withDetail("Bech32 address not found in the signed data.")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }
        var stakeAddress = maybeAddress.orElseThrow();

        var stakeAddressCheckE = stakeAddressVerificationService.checkIfAddressIsStakeAddress(stakeAddress);
        if (stakeAddressCheckE.isLeft()) {
            return Either.left(stakeAddressCheckE.getLeft());
        }

        var stakeAddressNetworkCheck = stakeAddressVerificationService.checkStakeAddressNetwork(stakeAddress);
        if (stakeAddressNetworkCheck.isLeft()) {
            return Either.left(stakeAddressNetworkCheck.getLeft());
        }

        var castVoteRequestBodyJsonE = jsonService.decodeCIP93VoteEnvelope(cip30VerificationResult.getMessage(TEXT));
        if (castVoteRequestBodyJsonE.isLeft()) {
            if (castVoteRequestBodyJsonE.isLeft()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CIP30_DATA_SIGNATURE")
                                .withDetail("Invalid cast vote signature!")
                                .withStatus(BAD_REQUEST)
                                .build()
                );
            }
        }
        var cip93VoteEnvelope = castVoteRequestBodyJsonE.get();
        var maybeNetwork = Enums.getIfPresent(CardanoNetwork.class, cip93VoteEnvelope.getData().getNetwork());
        if (maybeNetwork.isEmpty()) {
            log.warn("Invalid network, network:{}", cip93VoteEnvelope.getData().getNetwork());

            return Either.left(Problem.builder()
                    .withTitle("INVALID_NETWORK")
                    .withDetail("Invalid network, supported networks:" + CardanoNetwork.supportedNetworks())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var network = maybeNetwork.orElseThrow();

        if (network != cardanoNetwork) {
            log.warn("Invalid network, network:{}", cip93VoteEnvelope.getData().getNetwork());

            return Either.left(Problem.builder()
                    .withTitle("NETWORK_MISMATCH")
                    .withDetail("Invalid network, backend configured with network:" + cardanoNetwork + ", however request is with network:" + network)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        String cip30StakeAddress = cip93VoteEnvelope.getData().getAddress();
        if (!stakeAddress.equals(cip30StakeAddress)) {
            return Either.left(Problem.builder()
                    .withTitle("STAKE_ADDRESS_MISMATCH")
                    .withDetail("Invalid stake address, expected stakeAddress:" + stakeAddress + ", actual stakeAddress:" + cip30StakeAddress)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var actionText = cip93VoteEnvelope.getAction();

        var maybeAction = Enums.getIfPresent(Web3Action.class, actionText);
        if (maybeAction.isEmpty()) {
            log.warn("Unknown action, action:{}", actionText);

            return Either.left(Problem.builder()
                    .withTitle("ACTION_NOT_FOUND")
                    .withDetail("Action not found!")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }
        var action = maybeAction.orElseThrow();
        if (action != CAST_VOTE) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_ACTION")
                    .withDetail("Action is not CAST_VOTE, expected action:" + CAST_VOTE.name())
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var eventId = cip93VoteEnvelope.getData().getEvent();
        var maybeEvent = referenceDataService.findValidEventByName(eventId);
        if (maybeEvent.isEmpty()) {
            log.warn("Unrecognised event, eventId:{}", eventId);

            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, eventId:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var event = maybeEvent.get();

        if (expirationService.isEventInactive(event)) {
            log.warn("Event is not active, eventId:{}", eventId);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_IS_NOT_ACTIVE")
                    .withDetail("Event is not active (not started or already finished), eventId:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var categoryId = cip93VoteEnvelope.getData().getCategory();
        var maybeCategory = event.findCategoryByName(categoryId);
        if (maybeCategory.isEmpty()) {
            log.warn("Unrecognised category, categoryId:{}", eventId);

            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_CATEGORY")
                    .withDetail("Unrecognised category, categoryId:" + categoryId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var category = maybeCategory.orElseThrow();

        var proposalIdOrName = cip93VoteEnvelope.getData().getProposal();

        Proposal proposal = null;
        if (category.isGdprProtection()) {
            var maybeProposal = referenceDataService.findProposalById(proposalIdOrName);
            if (maybeProposal.isEmpty()) {
                log.warn("Unrecognised proposal, proposalId:{}", eventId);

                return Either.left(Problem.builder()
                        .withTitle("UNRECOGNISED_PROPOSAL")
                        .withDetail("Unrecognised proposal, proposal:" + proposalIdOrName)
                        .withStatus(BAD_REQUEST)
                        .build());
            }
            proposal = maybeProposal.orElseThrow();
        } else {
            var maybeProposal = referenceDataService.findProposalByName(category, proposalIdOrName);
            if (maybeProposal.isEmpty()) {
                log.warn("Unrecognised proposal, proposalId:{}", eventId);

                return Either.left(Problem.builder()
                        .withTitle("UNRECOGNISED_PROPOSAL")
                        .withDetail("Unrecognised proposal, proposal:" + proposalIdOrName)
                        .withStatus(BAD_REQUEST)
                        .build());
            }
            proposal = maybeProposal.orElseThrow();
        }

        var cip93SlotStr = cip93VoteEnvelope.getSlot();

        if (!isNumeric(cip93SlotStr)) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_SLOT")
                            .withDetail("CIP-93 envelope slot is not numeric!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }
        var cip93Slot = Long.parseLong(cip93SlotStr);

        if (expirationService.isSlotExpired(cip93Slot)) {
            log.warn("Invalid request slot, slot:{}", cip93Slot);

            return Either.left(
                    Problem.builder()
                            .withTitle("EXPIRED_SLOT")
                            .withDetail("CIP-93's envelope slot is expired!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        var votedAtSlotStr = cip93VoteEnvelope.getData().getVotedAt();
        if (!isNumeric(votedAtSlotStr)) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_SLOT")
                            .withDetail("Vote's votedAt slot is not numeric!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }
        var votedAtSlot = Long.parseLong(votedAtSlotStr);

        if (expirationService.isSlotExpired(votedAtSlot)) {
            log.warn("Invalid votedAt slot, votedAt slot:{}", votedAtSlot);

            return Either.left(
                    Problem.builder()
                            .withTitle("EXPIRED_SLOT")
                            .withDetail("votedAt slot is expired!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }
        if (votedAtSlot != cip93Slot) {
            log.warn("Slots mismatch, votedAt CIP-93 slot:{}, slot:{}", votedAtSlot, cip93Slot);

            return Either.left(
                    Problem.builder()
                            .withTitle("SLOT_MISMATCH")
                            .withDetail("CIP93 envelope slot and votedAt slot mismatch!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        String voteId = cip93VoteEnvelope.getData().getId();
        if (!MoreUUID.isUUIDv4(voteId)) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_VOTE_ID")
                            .withDetail("Invalid vote voteId: " + voteId)
                            .withStatus(BAD_REQUEST)
                            .build());
        }

        var maybeExistingVote = voteRepository.findByEventIdAndCategoryIdAndVoterStakingAddress(event.getId(), category.getId(), stakeAddress);
        if (maybeExistingVote.isPresent()) {

            if (!event.isAllowVoteChanging()) {
                return Either.left(Problem.builder()
                        .withTitle("VOTE_CANNOT_BE_CHANGED")
                        .withDetail("Vote cannot be changed for the stake address: " + stakeAddress + ", within category: " + category.getId() + ", for event: " + eventId)
                        .withStatus(BAD_REQUEST)
                        .build()
                );
            }
            var existingVote = maybeExistingVote.orElseThrow();

            var maybeLatestProof = voteMerkleProofService.findLatestProof(eventId, maybeExistingVote.orElseThrow().getId());
            if (maybeLatestProof.isPresent()) {
                log.warn("Cannot change existing vote for the stake address: " + stakeAddress, ", within category: " + category.getId() + ", for event: " + eventId);

                return Either.left(
                        Problem.builder()
                                .withTitle("VOTE_CANNOT_BE_CHANGED")
                                .withDetail("Vote cannot be changed for the stake address: " + stakeAddress + ", within category: " + category.getId() + ", for event: " + eventId)
                                .withStatus(BAD_REQUEST)
                                .build()
                );
            }
            existingVote.setId(existingVote.getId());
            existingVote.setProposalId(proposal.getId());
            existingVote.setVotedAtSlot(votedAtSlot);
            existingVote.setCoseSignature(castVoteRequest.getCoseSignature());
            existingVote.setCosePublicKey(castVoteRequest.getCosePublicKey());

            return Either.right(voteRepository.saveAndFlush(existingVote));
        }

        var vote = new Vote();
        vote.setId(voteId);
        vote.setEventId(event.getId());
        vote.setCategoryId(category.getId());
        vote.setProposalId(proposal.getId());
        vote.setVoterStakingAddress(stakeAddress);
        vote.setVotedAtSlot(votedAtSlot);
        vote.setCoseSignature(castVoteRequest.getCoseSignature());
        vote.setCosePublicKey(castVoteRequest.getCosePublicKey());

        if (List.of(STAKE_BASED, BALANCE_BASED).contains(event.getVotingEventType())) {
            var blockchainVotingPower = votingPowerService.getVotingPower(event, stakeAddress).orElse(-1L);
            if (blockchainVotingPower <= 0) {
                log.warn("Voting power is less than equal 0 for the stake address: " + stakeAddress);

                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_VOTING_POWER")
                                .withDetail("Voting power is 0 or less for the stake address: " + stakeAddress)
                                .withStatus(BAD_REQUEST)
                                .build()
                );
            }

            if (!isNumeric(cip93VoteEnvelope.getData().getVotingPower().orElseThrow())) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_VOTING_POWER")
                                .withDetail("CIP-93's envelope votingPower is not numeric for the stake address: " + stakeAddress)
                                .withStatus(BAD_REQUEST)
                                .build()
                );
            }
            var signedVotingPower = Long.parseLong(cip93VoteEnvelope.getData().getVotingPower().orElseThrow());
            if (signedVotingPower != blockchainVotingPower) {
                return Either.left(
                        Problem.builder()
                                .withTitle("VOTING_POWER_MISMATCH")
                                .withDetail("Signed voting power is not equal to blockchain voting power for the stake address: " + stakeAddress)
                                .withStatus(BAD_REQUEST)
                                .build()
                );
            }

            vote.setVotingPower(Optional.of(blockchainVotingPower));
        }

        if (event.getVotingEventType() == USER_BASED) {
            if (vote.getVotingPower().isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("VOTING_POWER_NOT_SUPPORTED")
                                .withDetail("Voting power makes no sense for USER_BASED events, please remove it from the vote envelope.")
                                .withStatus(BAD_REQUEST)
                                .build()
                );
            }
        }

        return Either.right(voteRepository.saveAndFlush(vote));
    }

    @Override
    @Transactional
    @Timed(value = "service.vote.voteReceipt", percentiles = { 0.3, 0.5, 0.95 })
    public Either<Problem, VoteReceipt> voteReceipt(SignedWeb3Request viewVoteReceiptSignedWeb3Request) {
        log.info("Fetching voter's receipt for the signed data: {}", viewVoteReceiptSignedWeb3Request);

        var cip30Verifier = new CIP30Verifier(
            viewVoteReceiptSignedWeb3Request.getCoseSignature(),
            viewVoteReceiptSignedWeb3Request.getCosePublicKey()
        );

        var cip30VerificationResult = cip30Verifier.verify();

        if (!cip30VerificationResult.isValid()) {
            log.warn("CIP-30 data sign for viewing voter's receipt failed, validationError:{}", cip30VerificationResult.getValidationError().orElse(UNKNOWN));

            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_CIP30_DATA_SIGNATURE")
                            .withDetail("Invalid cast vote cose signature!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        var maybeAddress = cip30VerificationResult.getAddress(AddressFormat.TEXT);
        if (maybeAddress.isEmpty()) {
            log.warn("Address not found in the signed data");

            return Either.left(
                    Problem.builder()
                            .withTitle("ADDRESS_NOT_FOUND")
                            .withDetail("Bech32 address not found in the signed data.")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }
        var stakeAddress = maybeAddress.orElseThrow();

        var stakeAddressCheckE = stakeAddressVerificationService.checkIfAddressIsStakeAddress(stakeAddress);
        if (stakeAddressCheckE.isLeft()) {
            return Either.left(stakeAddressCheckE.getLeft());
        }

        var stakeAddressNetworkCheck = stakeAddressVerificationService.checkStakeAddressNetwork(stakeAddress);
        if (stakeAddressNetworkCheck.isLeft()) {
            return Either.left(stakeAddressNetworkCheck.getLeft());
        }

        var viewVoteReceiptEnvelope = jsonService.decodeCIP93ViewVoteReceiptEnvelope(cip30VerificationResult.getMessage(TEXT));
        if (viewVoteReceiptEnvelope.isLeft()) {
            if (viewVoteReceiptEnvelope.isLeft()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CIP30_DATA_SIGNATURE")
                                .withDetail("Invalid view vote receipt signature!")
                                .withStatus(BAD_REQUEST)
                                .build()
                );
            }
        }
        var cip93ViewVoteReceiptEnvelope = viewVoteReceiptEnvelope.get();
        var maybeNetwork = Enums.getIfPresent(CardanoNetwork.class, cip93ViewVoteReceiptEnvelope.getData().getNetwork());
        if (maybeNetwork.isEmpty()) {
            log.warn("Invalid network, network:{}", cip93ViewVoteReceiptEnvelope.getData().getNetwork());

            return Either.left(Problem.builder()
                    .withTitle("INVALID_NETWORK")
                    .withDetail("Invalid network, supported networks:" + CardanoNetwork.supportedNetworks())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var network = maybeNetwork.orElseThrow();

        if (network != cardanoNetwork) {
            log.warn("Invalid network, network:{}", cip93ViewVoteReceiptEnvelope.getData().getNetwork());

            return Either.left(Problem.builder()
                    .withTitle("NETWORK_MISMATCH")
                    .withDetail("Invalid network, backed configured with network:" + cardanoNetwork + ", however request is with network:" + network)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        String cip30StakeAddress = cip93ViewVoteReceiptEnvelope.getData().getAddress();
        if (!stakeAddress.equals(cip30StakeAddress)) {
            return Either.left(Problem.builder()
                    .withTitle("STAKE_ADDRESS_MISMATCH")
                    .withDetail("Invalid stake address, expected stakeAddress:" + stakeAddress + ", actual stakeAddress:" + cip30StakeAddress)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var actionText = cip93ViewVoteReceiptEnvelope.getAction();

        var maybeAction = Enums.getIfPresent(Web3Action.class, actionText);
        if (maybeAction.isEmpty()) {
            log.warn("Unknown action, action:{}", actionText);

            return Either.left(Problem.builder()
                    .withTitle("ACTION_NOT_FOUND")
                    .withDetail("Action not found!")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }
        var action = maybeAction.orElseThrow();
        if (action != VIEW_VOTE_RECEIPT) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_ACTION")
                    .withDetail("Action is not VIEW_VOTE_RECEIPT, action:" + action.name())
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var eventId = cip93ViewVoteReceiptEnvelope.getData().getEvent();
        var maybeEvent = referenceDataService.findValidEventByName(eventId);
        if (maybeEvent.isEmpty()) {
            log.warn("Unrecognised event, eventId:{}", eventId);

            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, eventId:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var event = maybeEvent.get();

        var categoryId = cip93ViewVoteReceiptEnvelope.getData().getCategory();
        var maybeCategory = event.findCategoryByName(categoryId);
        if (maybeCategory.isEmpty()) {
            log.warn("Unrecognised category, categoryId:{}", eventId);

            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_CATEGORY")
                    .withDetail("Unrecognised category, categoryId:" + categoryId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var category = maybeCategory.orElseThrow();

        var maybeVote = voteRepository.findByEventIdAndCategoryIdAndVoterStakingAddress(event.getId(), category.getId(), stakeAddress);
        if (maybeVote.isEmpty()) {
            return Either.left(
                    Problem.builder()
                            .withTitle("VOTE_NOT_FOUND")
                            .withDetail("Not voted yet for stakeKey:" + stakeAddress)
                            .withStatus(NOT_FOUND)
                            .build()
            );
        }
        var vote = maybeVote.orElseThrow();

        var maybeProposal = referenceDataService.findProposalById(vote.getProposalId());
        if (maybeProposal.isEmpty()) {
            return Either.left(
                    Problem.builder()
                            .withTitle("PROPOSAL_NOT_FOUND")
                            .withDetail("Proposal not found for voteId:" + vote.getId())
                            .withStatus(NOT_FOUND)
                            .build()
            );
        }
        var proposal = maybeProposal.orElseThrow();
        var proposalIdOrName = category.isGdprProtection() ? proposal.getId() : proposal.getName();

        var latestVoteMerkleProof = voteMerkleProofService.findLatestProof(event.getId(), vote.getId());

        return latestVoteMerkleProof.map(proof -> {
            log.info("Latest merkle proof found for voteId:{}", vote.getId());

            var td = blockchainDataTransactionDetailsService.getTransactionDetails(proof.getL1TransactionHash());
            var isL1CommitmentOnChain = td.map(TransactionDetails::getFinalityScore);

            return Either.<Problem, VoteReceipt>right(VoteReceipt.builder()
                    .id(vote.getId())
                    .event(event.getId())
                    .category(category.getId())
                    .proposal(proposalIdOrName)
                    .coseSignature(vote.getCoseSignature())
                    .cosePublicKey(vote.getCosePublicKey())
                    .votedAtSlot(Long.valueOf(vote.getVotedAtSlot()).toString())
                    .voterStakingAddress(vote.getVoterStakingAddress())
                    .votingPower(vote.getVotingPower().map(String::valueOf))
                    .status(readMerkleProofStatus(proof, isL1CommitmentOnChain))
                    .finalityScore(isL1CommitmentOnChain)
                    .merkleProof(convertMerkleProof(proof, td))
                    .build()
            );

        }).orElseGet(() -> {
            log.info("Merkle proof not found yet for voteId:{}", vote.getId());

            return Either.right(VoteReceipt.builder()
                    .id(vote.getId())
                    .event(event.getId())
                    .category(category.getId())
                    .proposal(proposalIdOrName)
                    .coseSignature(vote.getCoseSignature())
                    .cosePublicKey(vote.getCosePublicKey())
                    .votedAtSlot(Long.valueOf(vote.getVotedAtSlot()).toString())
                    .voterStakingAddress(vote.getVoterStakingAddress())
                    .votingPower(vote.getVotingPower().map(String::valueOf))
                    .status(BASIC)
                    .build()
            );
        });
    }

    private static VoteReceipt.Status readMerkleProofStatus(VoteMerkleProof merkleProof, Optional<TransactionDetails.FinalityScore> isL1CommitmentOnChain) {
        if (merkleProof.isInvalidated()) {
            return ROLLBACK;
        }

        return isL1CommitmentOnChain.isEmpty() ? PARTIAL : FULL;
    }

    private VoteReceipt.MerkleProof convertMerkleProof(VoteMerkleProof proof, Optional<TransactionDetails> transactionDetails) {
        return VoteReceipt.MerkleProof.builder()
                .blockHash(transactionDetails.map(TransactionDetails::getBlockHash))
                .absoluteSlot(transactionDetails.map(TransactionDetails::getAbsoluteSlot))
                .rootHash(proof.getRootHash())
                .transactionHash(proof.getL1TransactionHash())
                .steps(convertSteps(proof))
                .build();

    }

    private List<VoteReceipt.MerkleProofItem> convertSteps(VoteMerkleProof proof) {
        return merkleProofSerdeService.deserialise(proof.getProofItemsJson()).stream().map(item -> {

            if (item instanceof ProofItem.Left pl) {
                return VoteReceipt.MerkleProofItem.builder()
                        .type(VoteReceipt.MerkleProofType.Left)
                        .hash(HexUtil.encodeHexString(pl.hash()))
                        .build();
            }

            if (item instanceof ProofItem.Right pr) {
                return VoteReceipt.MerkleProofItem.builder()
                        .type(VoteReceipt.MerkleProofType.Right)
                        .hash(HexUtil.encodeHexString(pr.hash()))
                        .build();
            }

            throw new RuntimeException("Unknown proof item type:" + item.getClass().getName());
        }).toList();
    }

}
