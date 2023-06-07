package org.cardano.foundation.voting.service;

import io.micrometer.core.annotation.Timed;
import org.cardano.foundation.voting.domain.VoteReceipt;
import org.cardano.foundation.voting.domain.VoteReceiptRequest;
import org.cardano.foundation.voting.domain.CastVoteRequest;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.Vote;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

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
    public boolean castVote(CastVoteRequest castVoteRequest) {
        // lookup event info from the reference data
        // stote vote in the db
        // store the vote in the merkle proof db

        return true;
    }

    // get merkle proof of the vote along with vote information

    // TODO should the user sign vote receipt request via CIP-30 or we simply deliver this to anybody that wants this?
    public Optional<VoteReceipt> voteReceipt(VoteReceiptRequest voteReceiptRequest) {
        // find up latest merkle tree root hash
        // check vote if this matches with the root hash

        return Optional.empty();
    }

}

// commitment protocols

// user can perform the verification

// 1 - Level 1 - https://voteaire.io
// 2 - Level 2 - Sundae Swap Governance + Hydra Tallying (Frankestein
// 3 - Level 3 - Hydra First Voting