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
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;
import org.cardano.foundation.voting.service.expire.ExpirationService;
import org.cardano.foundation.voting.service.json.JsonService;
import org.cardano.foundation.voting.service.merkle_tree.MerkleProofSerdeService;
import org.cardano.foundation.voting.service.merkle_tree.VoteMerkleProofService;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardano.foundation.voting.service.voting_power.VotingPowerService;
import org.cardano.foundation.voting.utils.Bech32;
import org.cardano.foundation.voting.utils.Enums;
import org.cardano.foundation.voting.utils.UUID;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.cardanofoundation.merkle.ProofItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.VoteReceipt.Status.*;
import static org.cardano.foundation.voting.domain.web3.Web3Action.CAST_VOTE;
import static org.cardanofoundation.cip30.Format.TEXT;
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
    private JsonService jsonService;

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
        // TODO check if vote is in the canonical form???

        var cip30Verifier = new CIP30Verifier(castVoteRequest.getCoseSignature(), Optional.ofNullable(castVoteRequest.getCosePublicKey()));
        var cip30VerificationResult = cip30Verifier.verify();

        if (!cip30VerificationResult.isValid()) {
            log.warn("CIP30 data sign for casting vote verification failed, validationError:{}", cip30VerificationResult.getValidationError().orElse(UNKNOWN));

            return Either.left(
                    Problem.builder()
                        .withTitle("INVALID_CIP30_DATA_SIGNATURE")
                        .withDetail("Invalid cast vote cose signature!")
                        .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var maybeAddress = cip30VerificationResult.getAddress();
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
        var address = maybeAddress.orElseThrow();

        var stakeAddressE = Bech32.decode(address);
        if (stakeAddressE.isLeft()) {
            log.warn("Invalid bech32 address, address:{}", address);

            return Either.left(stakeAddressE.getLeft());
        }
        var stakeAddress = stakeAddressE.get();

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
        var cip90VoteEnvelope = castVoteRequestBodyJsonE.get();
        var maybeNetwork = Enums.getIfPresent(CardanoNetwork.class, cip90VoteEnvelope.getData().getNetwork());
        if (maybeNetwork.isEmpty()) {
            log.warn("Invalid network, network:{}", cip90VoteEnvelope.getData().getNetwork());

            return Either.left(Problem.builder()
                    .withTitle("INVALID_NETWORK")
                    .withDetail("Invalid network, supported networks:" + CardanoNetwork.supportedNetworks())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        String cip30StakeAddress = cip90VoteEnvelope.getData().getAddress();
        if (!stakeAddress.equals(cip30StakeAddress)) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_STAKE_ADDRESS")
                    .withDetail("Invalid stake address, expected stakeAddress:" + stakeAddress + ", actual stakeAddress:" + cip30StakeAddress)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var network = maybeNetwork.orElseThrow();

        var actionText = cip90VoteEnvelope.getAction();

        var maybeAction = Enums.getIfPresent(Web3Action.class, actionText);
        if (maybeAction.isEmpty()) {
            log.warn("Unknown action, action:{}", actionText);

            return Either.left(Problem.builder()
                    .withTitle("ACTION_NOT_FOUND")
                    .withDetail("Action not found, expected action:" + CAST_VOTE.name())
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

        var eventId = cip90VoteEnvelope.getData().getEvent();
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
                    .withDetail("Event is not active, eventId:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var categoryId = cip90VoteEnvelope.getData().getCategory();
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

        var proposalIdOrName = cip90VoteEnvelope.getData().getProposal();

        log.info("Category GDPR protection: {}", category.isGdprProtection());

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
            var maybeProposal = referenceDataService.findProposalByName(proposalIdOrName);
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

        var cip93Slot = cip90VoteEnvelope.getSlot();
        if (expirationService.isSlotExpired(cip93Slot)) {
            log.warn("Invalid request slot, slot:{}", cip93Slot);

            return Either.left(
                    Problem.builder()
                            .withTitle("EXPIRED_SLOT")
                            .withDetail("Login's envelope slot is expired!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        var votedAtSlot = cip90VoteEnvelope.getData().getVotedAt();
        if (expirationService.isSlotExpired(votedAtSlot)) {
            log.warn("Invalid votedAt slot, votedAt slot:{}", votedAtSlot);

            return Either.left(
                    Problem.builder()
                            .withTitle("EXPIRED_SLOT")
                            .withDetail("Login's envelope slot is expired!")
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

        String voteId = cip90VoteEnvelope.getData().getId();
        if (!UUID.isUUIDv4(voteId)) {
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
            existingVote.setNetwork(network);
            existingVote.setCoseSignature(castVoteRequest.getCoseSignature());
            existingVote.setCosePublicKey(castVoteRequest.getCosePublicKey());

            return Either.right(voteRepository.saveAndFlush(existingVote));
        }

        var blockchainVotingPower = votingPowerService.getVotingPower(event, stakeAddress);
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

        var signedVotingPower = cip90VoteEnvelope.getData().getVotingPower();
        if (signedVotingPower != blockchainVotingPower) {
            return Either.left(
                    Problem.builder()
                            .withTitle("VOTING_POWER_MISMATCH")
                            .withDetail("Signed voting power is not equal to blockchain voting power for the stake address: " + stakeAddress)
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }


        Vote vote = new Vote();
        vote.setId(voteId);
        vote.setEventId(event.getId());
        vote.setCategoryId(category.getId());
        vote.setProposalId(proposal.getId());
        vote.setVoterStakingAddress(stakeAddress);
        vote.setVotedAtSlot(votedAtSlot);
        vote.setNetwork(network);
        vote.setCoseSignature(castVoteRequest.getCoseSignature());
        vote.setCosePublicKey(castVoteRequest.getCosePublicKey());
        vote.setVotingPower(blockchainVotingPower);

        return Either.right(voteRepository.saveAndFlush(vote));
    }

    @Override
    @Transactional
    @Timed(value = "service.vote.voteReceipt", percentiles = { 0.3, 0.5, 0.95 })
    public Either<Problem, VoteReceipt> voteReceipt(String eventName, String categoryName, String stakeAddress) {
        var maybeEvent = referenceDataService.findValidEventByName(eventName);
        if (maybeEvent.isEmpty()) {
            log.warn("Unrecognised event, event:{}", eventName);

            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, event:" + eventName)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var event  = maybeEvent.orElseThrow();

        var maybeCategory = referenceDataService.findCategoryByName(categoryName);
        if (maybeCategory.isEmpty()) {
            log.warn("Unrecognised category, category:{}", categoryName);

            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_CATEGORY")
                    .withDetail("Unrecognised category, category:" + categoryName)
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

        var latestVoteMerkleProof = voteMerkleProofService.findLatestProof(event.getId(), vote.getId());

        return latestVoteMerkleProof.map(proof -> {
            log.info("Latest merkle proof found for voteId:{}", vote.getId());

            var td = blockchainDataTransactionDetailsService.getTransactionDetails(proof.getL1TransactionHash());
            var isL1CommitmentOnChain = td.map(TransactionDetails::getFinalityScore);

            var status = isL1CommitmentOnChain.isEmpty() ? PARTIAL : FULL;

            return Either.<Problem, VoteReceipt>right(VoteReceipt.builder()
                    .id(vote.getId())
                    .votedAtSlot(vote.getVotedAtSlot())
                    .event(event.getId())
                    .category(category.getId())
                    .proposal(proposal.getId())
                    .coseSignature(vote.getCoseSignature())
                    .cosePublicKey(vote.getCosePublicKey())
                    .votedAtSlot(vote.getVotedAtSlot())
                    .voterStakingAddress(vote.getVoterStakingAddress())
                    .cardanoNetwork(vote.getNetwork())
                    .status(status)
                    .finalityScore(isL1CommitmentOnChain)
                    .merkleProof(convertMerkleProof(proof, td))
                    .build()
            );
        }).orElseGet(() -> {
            log.info("Merkle proof not found yet for voteId:{}", vote.getId());

            return Either.right(VoteReceipt.builder()
                    .id(vote.getId())
                    .votedAtSlot(vote.getVotedAtSlot())
                    .event(event.getId())
                    .category(category.getId())
                    .proposal(proposal.getId())
                    .proposalText(proposal.getName())
                    .coseSignature(vote.getCoseSignature())
                    .cosePublicKey(vote.getCosePublicKey())
                    .votedAtSlot(vote.getVotedAtSlot())
                    .voterStakingAddress(vote.getVoterStakingAddress())
                    .cardanoNetwork(vote.getNetwork())
                    .status(BASIC)
                    .build()
            );
        });
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
