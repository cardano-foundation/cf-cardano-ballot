package org.cardano.foundation.voting.service;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.RootHash;
import org.cardano.foundation.voting.domain.entity.Vote;
import org.cardano.foundation.voting.repository.RootHashRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class VoteCommitmentService {

    @Autowired
    private VoteService voteService;

    @Autowired
    private RootHashRepository rootHashRepository;

    @Transactional
    public void storeLatestRootHash(Event event) {
        log.info("Running posting root hash job...");

        //List<Vote> allVotes = voteService.findAll();

        // create merkle tree from all votes

        // get root hash from the merkle tree

        // commit root hash to Cardano as metadata transaction using BloxBean library

        rootHashRepository.save(new RootHash(event.getId(), "new-root-hash"));
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
