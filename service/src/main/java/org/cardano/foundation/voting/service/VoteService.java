package org.cardano.foundation.voting.service;

import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.*;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.RootHash;
import org.cardano.foundation.voting.domain.entity.Vote;
import org.cardano.foundation.voting.repository.RootHashRepository;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class VoteService {

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
    public boolean isVoteAlreadyCast(String voteId) {
        return voteRepository.findById(voteId).isPresent();
    }

    @Transactional
    @Timed(value = "service.vote.findAllUserVotes", percentiles = { 0.3, 0.5, 0.95 })
    public List<Vote> findAllUserVotes(String eventId, String stakeKey) {
        return List.of();
    }

    @Transactional
    @Timed(value = "service.vote.castVote", percentiles = { 0.3, 0.5, 0.95 })
    public Either<Problem, Vote> castVote(CastVoteWeb3Request castVoteRequest) {
        // lookup event info from the reference data
        // do not allow voting for events / proposals and categories that are invalid
        // stote vote in the db
        // do not allow vote casting if the vote has been cast already
        // check if voting power is there and if yes retrieve it, if it is not there -> bye bye

        // TODO
        // check if vote has been cast
        // check if vote is valid
        // check if vote is valid for the event
        // check if vote is valid for the category
        // check if votedAt slot is close to the current slot
        // check if the vote is valid for the network
        // check if event is still running or expired
        // check canonical form of the signed vote

        var maybeEvent = referenceDataService.findEvent(castVoteRequest.getVote().getEventId());
        if (maybeEvent.isEmpty()) {
            return Either.left(Problem.builder().build());
        }
        var event = maybeEvent.get();

        Vote vote = new Vote();
        vote.setId(castVoteRequest.getVote().getVoteId());
        vote.setCategoryId(castVoteRequest.getVote().getCategoryId());
        vote.setEventId(castVoteRequest.getVote().getEventId());
        vote.setVoterStakingAddress(castVoteRequest.getVote().getVoterStakeAddress());
        vote.setVotedAtSlot(castVoteRequest.getVote().getVotedAtSlot());
        vote.setNetwork(castVoteRequest.getVote().getNetwork());
        blockchainDataService.getVotingPower(castVoteRequest.getVote().getVoterStakeAddress()).ifPresent(vote::setVotingPower);

        voteRepository.saveAndFlush(vote);

        return Either.right(vote);
    }

    // get merkle proof of the vote along with vote information

    // TODO should the user sign vote receipt request via CIP-30 or we simply deliver this to anybody that wants this?
    @Transactional
    @Timed(value = "service.vote.voteReceipt", percentiles = { 0.3, 0.5, 0.95 })
    public Either<Problem, VoteReceipt> voteReceipt(VoteReceiptWeb3Request voteReceiptRequest) {
        // check in our db if we have vote receipt
        getRootHash(voteReceiptRequest.getEventId())
                .map(rootHash -> {
                    // verify proof of inclusion against root hash

                    return true;
                });

        // find up latest merkle tree root hash
        // check vote if this matches with the root hash

        var maybeVote = voteRepository.findByVoterStakingAddress(voteReceiptRequest.getStakeAddress());
        if (maybeVote.isEmpty()) {
            return Either.left(Problem.builder().build());
        }
        var vote = maybeVote.orElseThrow();

        return Either.right(VoteReceipt.builder().vote(vote).build());
    }

    @Transactional
    @Timed(value = "service.vote.verifyVote", percentiles = { 0.3, 0.5, 0.95 })
    public Either<Problem, VoteVerificationReceipt> verifyVote(VerifyVoteWeb3Request verifyVoteRequest) {
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

}
