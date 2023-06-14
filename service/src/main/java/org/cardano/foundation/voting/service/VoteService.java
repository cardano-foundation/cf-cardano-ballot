package org.cardano.foundation.voting.service;

import com.google.common.base.Enums;
import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.Network;
import org.cardano.foundation.voting.domain.VoteReceipt;
import org.cardano.foundation.voting.domain.VoteVerificationReceipt;
import org.cardano.foundation.voting.domain.Web3Action;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.RootHash;
import org.cardano.foundation.voting.domain.entity.Vote;
import org.cardano.foundation.voting.domain.request.CastVoteSignedWeb3Request;
import org.cardano.foundation.voting.domain.request.VerifyVoteSignedWeb3Request;
import org.cardano.foundation.voting.domain.request.VoteReceiptSignedWeb3Request;
import org.cardano.foundation.voting.repository.ProposalRepository;
import org.cardano.foundation.voting.repository.RootHashRepository;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.cardano.foundation.voting.utils.Bech32;
import org.cardano.foundation.voting.utils.Json;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.Web3Action.CAST_VOTE;
import static org.cardanofoundation.cip30.Format.TEXT;
import static org.zalando.problem.Status.*;

@Service
@Slf4j
public class VoteService {

    private final static int SLOT_BUFFER = 300;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private ReferenceDataService referenceDataService;

    @Autowired
    private RootHashRepository rootHashRepository;

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private BlockchainDataService blockchainDataService;

    public List<Vote> findAll(Event event) {
        return voteRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().filter(vote -> vote.getEventId().equals(event.getId())).toList();
    }

    @Transactional
    @Timed(value = "service.vote.isVoteAlreadyCast", percentiles = { 0.3, 0.5, 0.95 })
    public boolean isVoteAlreadyCast(String eventId, String categoryId, String stakeAddress) {
        return voteRepository.findByEventIdAndCategoryIdAndVoterStakingAddress(eventId, categoryId, stakeAddress).isPresent();
    }

    @Transactional
    @Timed(value = "service.vote.castVote", percentiles = { 0.3, 0.5, 0.95 })
    public Either<Problem, Vote> castVote(CastVoteSignedWeb3Request castVoteRequest) {
        // TODO check if vote is in the canonical form???

        var cip30Verifier = new CIP30Verifier(castVoteRequest.getCoseSignature(), castVoteRequest.getCosePublicKey());
        var cipVerificationResult = cip30Verifier.verify();

        if (!cipVerificationResult.isValid()) {
            log.warn("CIP30 data sign for casting vote verification failed!");

            return Either.left(
                    Problem.builder()
                        .withTitle("INVALID_CIP30_DATA_SIGNATURE")
                        .withDetail("Invalid cast vote cose signature!")
                        .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var maybeAddress = cipVerificationResult.getAddress();
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

        var castVoteRequestBody = cipVerificationResult.getCosePayload(TEXT);
        var castVoteRequestBodyJsonE = Json.decode(castVoteRequestBody);
        if (castVoteRequestBodyJsonE.isLeft()) {
            log.warn("Invalid json format, json:{}", castVoteRequestBody);

            return Either.left(castVoteRequestBodyJsonE.getLeft());
        }
        var castVoteRequestBodyJson = castVoteRequestBodyJsonE.get();
        var maybeNetwork = Network.fromName(castVoteRequestBodyJson.get("vote").get("network").asText());
        if (maybeNetwork.isEmpty()) {
            log.warn("Invalid network, network:{}", castVoteRequestBodyJson.asText());

            return Either.left(Problem.builder()
                    .withTitle("INVALID_NETWORK")
                    .withDetail("Invalid network, supported networks:" + Network.supportedNetworks())
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var network = maybeNetwork.orElseThrow();

        var blockchainDataE = blockchainDataService.getBlockchainData(network);
        if (blockchainDataE.isLeft()) {
            log.error("Unable to get blockchain data for network:{}", network);
            return Either.left(Problem
                    .builder()
                    .withTitle("INTERNAL_SERVER_ERROR")
                    .withDetail("Unable to get blockchain data for network:" + network)
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build()
            );
        }
        var blockchainData = blockchainDataE.get();

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
        if (event.isInactive(blockchainData.getEpochNo())) {
            log.warn("Event is not active, eventName:{}", eventName);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_IS_NOT_ACTIVE")
                    .withDetail("Event is not active, eventName:" + eventName)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var categoryName = castVoteRequestBodyJson.get("vote").get("categoryName").asText();
        var maybeCategory = event.findCategory(categoryName);
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
        if (isSlotExpired(cip93Slot, blockchainData.getAbsoluteSlot())) {
            log.warn("Invalid request slot, slot:{}", cip93Slot);

            return Either.left(Problem.builder()
                    .withTitle("INVALID_REQUEST_SLOT")
                    .withDetail("Invalid request slot, slot:" + cip93Slot)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var votedAtSlot = castVoteRequestBodyJson.get("vote").get("votedAt").asLong();
        if (isSlotExpired(votedAtSlot, blockchainData.getAbsoluteSlot())) {
            log.warn("Invalid votedAt slot, votedAt slot:{}", votedAtSlot);

            return Either.left(Problem.builder()
                    .withTitle("INVALID_VOTED_AT_SLOT")
                    .withDetail("Invalid votedAt slot, votedAt slot:" + votedAtSlot)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var votingPowerE = blockchainDataService.getVotingPower(network, event.getSnapshotEpoch(), stakeAddress);
        if (votingPowerE.isLeft()) {
            return Either.left(votingPowerE.getLeft());
        }
        var maybeVotingPower = votingPowerE.get();

        if (maybeVotingPower.isEmpty()) {
            log.warn("Unrecognised voting power, stakeAddress:{}", stakeAddress);

            return Either.left(Problem.builder()
                    .withTitle("VOTING_POWER_NOT_FOUND")
                    .withDetail("Voting power not found for the address:" + stakeAddress)
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build());
        }
        long envelopeVotingPower = castVoteRequestBodyJson.get("vote").get("votingPower").asLong();
        long actualVotingPower = maybeVotingPower.orElseThrow();

        if (envelopeVotingPower <= 0) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_VOTING_POWER")
                    .withDetail("Voting power must be greater than 0, voting power:" + envelopeVotingPower)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        if (envelopeVotingPower != actualVotingPower) {
            return Either.left(Problem.builder()
                    .withTitle("VOTING_POWER_MISMATCH")
                    .withDetail("Voting power mismatch, signed voting power:" + envelopeVotingPower + ", actual voting power:" + actualVotingPower)
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

        Vote vote = new Vote();
        vote.setId(castVoteRequestBodyJson.get("vote").get("id").asText());
        vote.setEventId(event.getId());
        vote.setCategoryId(category.getId());
        vote.setProposalId(proposal.getId());
        vote.setVoterStakingAddress(stakeAddress);
        vote.setVotedAtSlot(votedAtSlot);
        vote.setNetwork(network);
        vote.setCoseSignature(vote.getCoseSignature());
        vote.setCosePublicKey(vote.getCosePublicKey());
        vote.setVotingPower(actualVotingPower);

        return Either.right(voteRepository.saveAndFlush(vote));
    }

    // get merkle proof of the vote along with vote information

    @Transactional
    @Timed(value = "service.vote.voteReceipt", percentiles = { 0.3, 0.5, 0.95 })
    public Either<Problem, VoteReceipt> voteReceipt(VoteReceiptSignedWeb3Request voteReceiptRequest) {
        var cip30Verifier = new CIP30Verifier(voteReceiptRequest.getCoseSignature(), voteReceiptRequest.getCosePublicKey());
        var cipVerificationResult = cip30Verifier.verify();

        if (!cipVerificationResult.isValid()) {
            log.warn("CIP30 vote receipt verification failed!");

            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_CIP30_DATA_SIGNATURE")
                            .withDetail("Invalid vote receipt signature!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        var maybeAddress = cipVerificationResult.getAddress();
        if (maybeAddress.isEmpty()) {
            log.warn("Address not found in the signed data!");

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
            return Either.left(stakeAddressE.getLeft());
        }
        var stakeAddress = stakeAddressE.get();

        var voteReceiptBody = cipVerificationResult.getCosePayload(TEXT);

        var voteReceiptBodyJsonE = Json.decode(voteReceiptBody);
        if (voteReceiptBodyJsonE.isLeft()) {
            log.warn("Invalid json format, json:{}", voteReceiptRequest);

            return Either.left(voteReceiptBodyJsonE.getLeft());
        }

        var voteReceiptBodyJson = voteReceiptBodyJsonE.get();

        var maybeNetwork = Network.fromName(voteReceiptBodyJson.get("request").get("network").asText());
        if (maybeNetwork.isEmpty()) {
            log.warn("Invalid network, network:{}", voteReceiptBodyJson.asText());

            return Either.left(Problem.builder()
                    .withTitle("INVALID_NETWORK")
                    .withDetail("Invalid network, supported networks:" + Network.supportedNetworks())
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var network = maybeNetwork.orElseThrow();

        var blockchainDataE = blockchainDataService.getBlockchainData(network);
        if (blockchainDataE.isLeft()) {
            log.error("Unable to get blockchain data for network:{}", network);
            return Either.left(Problem
                    .builder()
                    .withTitle("INTERNAL_SERVER_ERROR")
                    .withDetail("Unable to get blockchain data for network:" + network)
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build()
            );
        }
        var blockchainData = blockchainDataE.get();

        var cip93Slot = voteReceiptBodyJson.get("slot").asLong();
        if (isSlotExpired(cip93Slot, blockchainData.getAbsoluteSlot())) {
            log.warn("Invalid request slot, slot:{}", cip93Slot);

            return Either.left(Problem.builder()
                    .withTitle("INVALID_REQUEST_SLOT")
                    .withDetail("Invalid request slot for network, slot:" + cip93Slot)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var actionText = voteReceiptBodyJson.get("action").asText();

        var maybeAction = Enums.getIfPresent(Web3Action.class, actionText).toJavaUtil();
        if (maybeAction.isEmpty()) {
            log.warn("Unknown action, action:{}", actionText);

            return Either.left(Problem.builder()
                    .withTitle("UNKNOWN_ACTION")
                    .withDetail("Action not found, expected action:" + Web3Action.VOTE_RECEIPT.name())
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }
        var action = maybeAction.orElseThrow();
        if (action != Web3Action.VOTE_RECEIPT) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_ACTION")
                    .withDetail("Voter Receipt Action not found, expected action:" + Web3Action.VOTE_RECEIPT.name())
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var eventName = voteReceiptBodyJson.get("request").get("eventName").asText();
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

        var categoryName = voteReceiptBodyJson.get("request").get("categoryName").asText();
        var maybeCategory = event.findCategory(categoryName);
        if (maybeCategory.isEmpty()) {
            log.warn("Unrecognised category, categoryName:{}", eventName);

            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_CATEGORY")
                    .withDetail("Unrecognised category, categoryName:" + eventName)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var category = maybeCategory.orElseThrow();

        Optional<Vote> maybeVote = voteRepository.findByEventIdAndCategoryIdAndVoterStakingAddress(event.getId(), category.getName(), stakeAddress);
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

        return Either.right(VoteReceipt.builder()
                .id(vote.getId())
                .votingPower(vote.getVotingPower())
                .votedAtSlot(vote.getVotedAtSlot())
                .event(event.getName())
                .category(category.getName())
                .proposal(proposalRepository.findById(vote.getId()).orElseThrow().getName())
                .coseSignature(vote.getCoseSignature())
                .cosePublicKey(vote.getCosePublicKey())
                .votedAtSlot(vote.getVotedAtSlot())
                 .voterStakingAddress(vote.getVoterStakingAddress())
                 .network(network)
                .build());

        // TODO
        // voter receipt with merkle proof if it exists (only when committed to the L1 blockchain)
    }

    @Transactional
    @Timed(value = "service.vote.verifyVote", percentiles = { 0.3, 0.5, 0.95 })
    public Either<Problem, VoteVerificationReceipt> verifyVote(VerifyVoteSignedWeb3Request verifyVoteRequest) {
//        getRootHash(verifyVoteRequest.getEventId())
//                .map(rootHash -> {
//                    // verify proof of inclusion against root hash
//
//                    return true;
//                });

        // find up latest merkle tree root hash
        // check vote if this matches with the root hash

        return Either.right(VoteVerificationReceipt.builder().build());
    }

    @Transactional
    public RootHash storeLatestRootHash(Event event) {
        log.info("Running posting root hash job...");

        List<Vote> allVotes = findAll(event);

        // create merkle tree from all votes

        // get root hash from the merkle tree

        return rootHashRepository.saveAndFlush(new RootHash(event.getId(), "new-root-hash"));
    }

    /**
     * Retrieve latest root hash stored on chain as a serilised hex entry
     * @return
     */
    @Transactional
    public Optional<RootHash> getRootHash(String eventId) {
        // find last merkle root hash

        // access root hash from indexes metadata entries
        // we listen on particular key in the metadata map

        return rootHashRepository.findAll().stream().findFirst();
    }

    /**
     * Return true if the slot is within permissible range
     */
    protected static boolean isSlotExpired(long slot, long currentSlot) {
        var range = Range.from(Range.Bound.inclusive(currentSlot - SLOT_BUFFER))
                .to(Range.Bound.inclusive(currentSlot + SLOT_BUFFER));

        return !range.contains(slot);
    }

}
