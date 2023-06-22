package org.cardano.foundation.voting.service.vote;

import com.bloxbean.cardano.client.util.HexUtil;
import com.google.common.base.Enums;
import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.VoteReceipt;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.Vote;
import org.cardano.foundation.voting.domain.entity.VoteMerkleProof;
import org.cardano.foundation.voting.domain.web3.SignedWeb3Request;
import org.cardano.foundation.voting.domain.web3.Web3Action;
import org.cardano.foundation.voting.repository.ProposalRepository;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.cardano.foundation.voting.service.ExpirationService;
import org.cardano.foundation.voting.service.VotingPowerService;
import org.cardano.foundation.voting.service.merkle_tree.MerkleProofSerdeService;
import org.cardano.foundation.voting.service.merkle_tree.VoteMerkleProofService;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardano.foundation.voting.utils.Bech32;
import org.cardano.foundation.voting.utils.Json;
import org.cardano.foundation.voting.utils.UUID;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.cardanofoundation.merkle.ProofItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.Optional;

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
    private ProposalRepository proposalRepository;

    @Autowired
    private ExpirationService expirationService;

    @Autowired
    private VoteMerkleProofService voteMerkleProofService;

    @Autowired
    private MerkleProofSerdeService merkleProofSerdeService;

    @Autowired
    private VotingPowerService votingPowerService;

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
    @Timed(value = "service.vote.isVoteAlreadyCast", percentiles = { 0.3, 0.5, 0.95 })
    public boolean isVoteAlreadyCast(String eventId, String categoryId, String stakeAddress) {
        return voteRepository.findByEventIdAndCategoryIdAndVoterStakingAddress(eventId, categoryId, stakeAddress).isPresent();
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

        var castVoteRequestBody = cip30VerificationResult.getMessage(TEXT);
        var castVoteRequestBodyJsonE = Json.decode(castVoteRequestBody);
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
        var castVoteRequestBodyJson = castVoteRequestBodyJsonE.get();
        var maybeNetwork = CardanoNetwork.fromName(castVoteRequestBodyJson.get("vote").get("network").asText());
        if (maybeNetwork.isEmpty()) {
            log.warn("Invalid network, network:{}", castVoteRequestBodyJson.asText());

            return Either.left(Problem.builder()
                    .withTitle("INVALID_NETWORK")
                    .withDetail("Invalid network, supported networks:" + CardanoNetwork.supportedNetworks())
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var network = maybeNetwork.orElseThrow();

        var actionText = castVoteRequestBodyJson.get("action").asText();

        var maybeAction = Enums.getIfPresent(Web3Action.class, actionText).toJavaUtil();
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

        var eventName = castVoteRequestBodyJson.get("vote").get("eventName").asText();
        var maybeEvent = referenceDataService.findEventByName(eventName);
        if (maybeEvent.isEmpty()) {
            log.warn("Unrecognised event, eventName:{}", eventName);

            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, eventName:" + eventName)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var event = maybeEvent.get();
        if (expirationService.isEventInactive(event)) {
            log.warn("Event is not active, eventName:{}", eventName);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_IS_NOT_ACTIVE")
                    .withDetail("Event is not active, eventName:" + eventName)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var categoryName = castVoteRequestBodyJson.get("vote").get("categoryName").asText();
        var maybeCategory = event.findCategoryByName(categoryName);
        if (maybeCategory.isEmpty()) {
            log.warn("Unrecognised category, categoryName:{}", eventName);

            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_CATEGORY")
                    .withDetail("Unrecognised category, categoryName:" + categoryName)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var category = maybeCategory.orElseThrow();

        String proposalName = castVoteRequestBodyJson.get("vote").get("proposalName").asText();
        var maybeProposal = proposalRepository.findByName(proposalName);
        if (maybeProposal.isEmpty()) {
            log.warn("Unrecognised proposal, proposalName:{}", eventName);

            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_PROPOSAL")
                    .withDetail("Unrecognised proposal, proposalName:" + proposalName)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var proposal = maybeProposal.orElseThrow();

        var cip93Slot = castVoteRequestBodyJson.get("slot").asLong();
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

        var votedAtSlot = castVoteRequestBodyJson.get("vote").get("votedAt").asLong();
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

        if (voteRepository.findByEventIdAndCategoryIdAndVoterStakingAddress(event.getId(), category.getId(), stakeAddress).isPresent()) {
            log.warn("Cote already cast for the stake address: " + stakeAddress);

            return Either.left(
                    Problem.builder()
                            .withTitle("VOTE_ALREADY_CAST")
                            .withDetail("Vote already cast for the stake address: " + stakeAddress)
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        String voteId = castVoteRequestBodyJson.get("vote").get("id").asText();
        if (!UUID.isUUIDv4(voteId)) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_VOTE_ID")
                            .withDetail("Invalid vote voteId: " + voteId)
                            .withStatus(BAD_REQUEST)
                            .build());
        }

        var votingPower = votingPowerService.getVotingPower(event, stakeAddress);
        if (votingPower == 0) {
            log.warn("Voting power is 0 for the stake address: " + stakeAddress);

            return Either.left(
                    Problem.builder()
                            .withTitle("VOTING_POWER_IS_ZERO")
                            .withDetail("Voting power is 0 for the stake address: " + stakeAddress)
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
        vote.setVotingPower(votingPower);

        var storedVote = voteRepository.saveAndFlush(vote);

        return Either.right(storedVote);
    }

    // get merkle proof of the vote along with vote information

    @Override
    @Transactional
    @Timed(value = "service.vote.voteReceipt", percentiles = { 0.3, 0.5, 0.95 })
    public Either<Problem, VoteReceipt> voteReceipt(String eventName, String categoryName, String stakeAddress) {
        var maybeEvent = referenceDataService.findEventByName(eventName);
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

        var maybeProposal = proposalRepository.findById(vote.getId());
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



        var latestVoteMerkleProof = voteMerkleProofService.findLatestProof(vote);

        return latestVoteMerkleProof.map(proof -> {
            log.info("Merkle proof found for voteId:{}", vote.getId());

            return Either.<Problem, VoteReceipt>right(VoteReceipt.builder()
                    .id(vote.getId())
                    .votedAtSlot(vote.getVotedAtSlot())
                    .event(event.getName())
                    .category(category.getName())
                    .proposal(proposal.getName())
                    .coseSignature(vote.getCoseSignature())
                    .cosePublicKey(vote.getCosePublicKey())
                    .votedAtSlot(vote.getVotedAtSlot())
                    .voterStakingAddress(vote.getVoterStakingAddress())
                    .cardanoNetwork(vote.getNetwork())
                    .status(VoteReceipt.Status.FULL)
                    .merkleProof(convertMerkleProof(proof))
                    .build()
            );
        }).orElseGet(() -> {
            log.info("Merkle proof not found yet for voteId:{}", vote.getId());

            return Either.right(VoteReceipt.builder()
                    .id(vote.getId())
                    .votedAtSlot(vote.getVotedAtSlot())
                    .event(event.getName())
                    .category(category.getName())
                    .proposal(proposal.getName())
                    .coseSignature(vote.getCoseSignature())
                    .cosePublicKey(vote.getCosePublicKey())
                    .votedAtSlot(vote.getVotedAtSlot())
                    .voterStakingAddress(vote.getVoterStakingAddress())
                    .cardanoNetwork(vote.getNetwork())
                    .status(VoteReceipt.Status.BASIC)
                    .build()
            );
        });
    }

    private VoteReceipt.MerkleProof convertMerkleProof(VoteMerkleProof proof) {
        return VoteReceipt.MerkleProof.builder()
                .blockHash(proof.getBlockHash())
                .absoluteSlot(proof.getAbsoluteSlot())
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
