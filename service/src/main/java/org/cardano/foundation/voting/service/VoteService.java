package org.cardano.foundation.voting.service;

import com.google.common.base.Enums;
import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.*;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.RootHash;
import org.cardano.foundation.voting.domain.entity.Vote;
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

import static org.cardanofoundation.cip30.Format.TEXT;
import static org.zalando.problem.Status.*;

@Service
@Slf4j
public class VoteService {

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private ReferenceDataService referenceDataService;

    @Autowired
    private RootHashRepository rootHashRepository;

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

        var cip30Verifier = new CIP30Verifier(castVoteRequest.getCosePayload(), castVoteRequest.getCosePublicKey());
        var cipVerificationResult = cip30Verifier.verify();

        if (!cipVerificationResult.isValid()) {
            log.warn("CIP30 data sign for casting vote verification failed!");

            return Either.left(
                    Problem.builder()
                        .withTitle("Invalid cast vote cose signature!")
                        .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var maybeAddress = cipVerificationResult.getAddress();
        if (maybeAddress.isEmpty()) {
            log.warn("Address not found in the signed data");

            return Either.left(

                    Problem.builder()
                            .withTitle("Bech32 address not found in the signed data.")
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
                    .withTitle("Invalid network, supported networks:" + Network.supportedNetworks())
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var network = maybeNetwork.orElseThrow();

        // TODO uri check using HttpServletRequest???
        //var uri = castVoteRequestBodyJson.get("uri").asText();
        var actionText = castVoteRequestBodyJson.get("action").asText();

        var maybeAction = Enums.getIfPresent(Web3Action.class, actionText).toJavaUtil();
        if (maybeAction.isEmpty()) {
            log.warn("Unknown action, action:{}", actionText);

            return Either.left(Problem.builder()
                    .withTitle("Action not found, expected action:" + Web3Action.CAST_VOTE.name())
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }
        var action = maybeAction.orElseThrow();
        if (action != Web3Action.CAST_VOTE) {
            return Either.left(Problem.builder()
                    .withTitle("Cast Action not found, expected action:" + Web3Action.CAST_VOTE.name())
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var eventName = castVoteRequestBodyJson.get("vote").get("eventName").asText();
        var maybeEvent = referenceDataService.findEventByName(eventName);
        if (maybeEvent.isEmpty()) {
            log.warn("Unrecognised event, eventName:{}", eventName);

            return Either.left(Problem.builder()
                    .withTitle("Unrecognised event, eventName:" + eventName)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var event = maybeEvent.get();
        if (event.isInActive(blockchainDataService.getCurrentAbsoluteSlot(network))) {
            log.warn("Event is not active, eventName:{}", eventName);

            return Either.left(Problem.builder()
                    .withTitle("Event is not active, eventName:" + eventName)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var categoryName = castVoteRequestBodyJson.get("vote").get("categoryName").asText();
        var maybeCategory = event.findCategory(categoryName);
        if (maybeCategory.isEmpty()) {
            log.warn("Unrecognised category, categoryName:{}", eventName);

            return Either.left(Problem.builder()
                    .withTitle("Unrecognised category, categoryName:" + eventName)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var category = maybeCategory.orElseThrow();

        var maybeProposal = proposalRepository.findByName(castVoteRequestBodyJson.get("vote").get("proposalName").asText());
        if (maybeProposal.isEmpty()) {
            log.warn("Unrecognised proposal, proposalName:{}", eventName);

            return Either.left(Problem.builder()
                    .withTitle("Unrecognised proposal, proposalName:" + eventName)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var proposal = maybeProposal.orElseThrow();

        var slot = castVoteRequestBodyJson.get("slot").asLong();
        if (isSlotExpired(network, slot)) {
            log.warn("Invalid request slot, slot:{}", slot);

            return Either.left(Problem.builder()
                    .withTitle("Invalid request slot, slot:" + slot)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var votedAt = castVoteRequestBodyJson.get("vote").get("votedAt").asLong();
        if (isSlotExpired(network, votedAt)) {
            log.warn("Invalid votedAt slot, votedAt slot:{}", votedAt);

            return Either.left(Problem.builder()
                    .withTitle("Invalid votedAt slot, votedAt slot:" + votedAt)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var maybeVotingPower = blockchainDataService.getVotingPower(network, event.getSnapshotEpoch(), stakeAddress);
        if (maybeVotingPower.isEmpty()) {
            log.warn("Unrecognised voting power, stakeAddress:{}", stakeAddress);

            return Either.left(Problem.builder()
                    .withTitle("Voting power not found for the address:" + stakeAddress)
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build());
        }
        long envelopeVotingPower = castVoteRequestBodyJson.get("vote").get("votingPower").asLong();
        long actualVotingPower = maybeVotingPower.orElseThrow();
        if (envelopeVotingPower != actualVotingPower) {
            return Either.left(Problem.builder()
                    .withTitle("Voting power mismatch, signed vote voting power:" + envelopeVotingPower + ", actual voting power:" + actualVotingPower)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        if (voteRepository.findByEventIdAndCategoryIdAndVoterStakingAddress(event.getId(), category.getId(), stakeAddress).isPresent()) {
            log.warn("Cote already cast for the stake address: " + stakeAddress);

            return Either.left(
                    Problem.builder()
                            .withTitle("Vote already cast for the stake address: " + stakeAddress)
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
        vote.setVotedAtSlot(votedAt);
        vote.setNetwork(network);
        vote.setCoseSignature(vote.getCoseSignature());
        vote.setCosePublicKey(vote.getCosePublicKey());
        vote.setVotingPower(actualVotingPower);

        return Either.right(voteRepository.saveAndFlush(vote));
    }

    // get merkle proof of the vote along with vote information

    // TODO should the user sign vote receipt request via CIP-30 or we simply deliver this to anybody that wants this?
    @Transactional
    @Timed(value = "service.vote.voteReceipt", percentiles = { 0.3, 0.5, 0.95 })
    public Either<Problem, VoteReceipt> voteReceipt(VoteReceiptSignedWeb3Request voteReceiptRequest) {
        var cip30Verifier = new CIP30Verifier(voteReceiptRequest.getCosePayload(), voteReceiptRequest.getCosePublicKey());
        var cipVerificationResult = cip30Verifier.verify();

        if (!cipVerificationResult.isValid()) {
            log.error("CIP30 data sign vote receipt verification failed!");

            return Either.left(
                    Problem.builder()
                            .withTitle("Invalid vote receipt cose signature!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        var maybeAddress = cipVerificationResult.getAddress();
        if (maybeAddress.isEmpty()) {
            log.warn("Address not found in the signed data!");

            return Either.left(
                    Problem.builder()
                            .withTitle("Bech32 address not found in the signed data.")
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
                    .withTitle("Invalid network, supported networks:" + Network.supportedNetworks())
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var network = maybeNetwork.orElseThrow();

        var slot = voteReceiptBodyJson.get("slot").asLong();
        if (isSlotExpired(network, slot)) {
            log.warn("Invalid request slot, slot:{}", slot);

            return Either.left(Problem.builder()
                    .withTitle("Invalid request slot for network, slot:" + slot)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var actionText = voteReceiptBodyJson.get("action").asText();

        var maybeAction = Enums.getIfPresent(Web3Action.class, actionText).toJavaUtil();
        if (maybeAction.isEmpty()) {
            log.warn("Unknown action, action:{}", actionText);

            return Either.left(Problem.builder()
                    .withTitle("Action not found, expected action:" + Web3Action.VOTE_RECEIPT.name())
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }
        var action = maybeAction.orElseThrow();
        if (action != Web3Action.VOTE_RECEIPT) {
            return Either.left(Problem.builder()
                    .withTitle("Voter Receipt Action not found, expected action:" + Web3Action.VOTE_RECEIPT.name())
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var eventName = voteReceiptBodyJson.get("request").get("eventName").asText();
        var maybeEvent = referenceDataService.findEventByName(eventName);
        if (maybeEvent.isEmpty()) {
            log.warn("Unrecognised event, eventName:{}", eventName);

            return Either.left(Problem.builder()
                    .withTitle("Unrecognised event, eventName:" + eventName)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var event = maybeEvent.get();

        var categoryName = voteReceiptBodyJson.get("request").get("categoryName").asText();
        var maybeCategory = event.findCategory(categoryName);
        if (maybeCategory.isEmpty()) {
            log.warn("Unrecognised category, categoryName:{}", eventName);

            return Either.left(Problem.builder()
                    .withTitle("Unrecognised category, categoryName:" + eventName)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var category = maybeCategory.orElseThrow();

        Optional<Vote> maybeVote = voteRepository.findByEventIdAndCategoryIdAndVoterStakingAddress(event.getId(), category.getName(), stakeAddress);
        if (maybeVote.isEmpty()) {
            return Either.left(
                    Problem.builder()
                            .withTitle("Not voted yet for stakeKey:" + stakeAddress)
                            .withStatus(NOT_FOUND)
                            .build()
            );
        }
        var vote = maybeVote.orElseThrow();

        return Either.right(VoteReceipt.builder().vote(vote).build());

        // TODO
        // 1. voter receipt with merkle proof
        // 2. voter receipt without merkle proof
    }

    @Transactional
    @Timed(value = "service.vote.verifyVote", percentiles = { 0.3, 0.5, 0.95 })
    public Either<Problem, VoteVerificationReceipt> verifyVote(VerifyVoteSignedWeb3Request verifyVoteRequest) {
        getRootHash(verifyVoteRequest.getEventId())
                .map(rootHash -> {
                    // verify proof of inclusion against root hash

                    return true;
                });

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
     * Return true if the vote is invalid for the network
     *
     * @param slot
     * @return
     */
    protected boolean isSlotExpired(Network network, long slot) {
        var currentSlot = blockchainDataService.getCurrentAbsoluteSlot(network);
        var slotBuffer = 300; // 300 slots = 5 mins

        var range = Range.from(Range.Bound.inclusive(currentSlot - slotBuffer))
                .to(Range.Bound.inclusive(currentSlot + slotBuffer));

        return range.contains(slot);
    }

}
