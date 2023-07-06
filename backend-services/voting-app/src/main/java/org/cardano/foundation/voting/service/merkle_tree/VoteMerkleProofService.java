package org.cardano.foundation.voting.service.merkle_tree;

import io.micrometer.core.annotation.Timed;
import org.cardano.foundation.voting.domain.entity.VoteMerkleProof;
import org.cardano.foundation.voting.repository.VoteMerkleProofRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class VoteMerkleProofService {

    @Autowired
    private VoteMerkleProofRepository voteMerkleProofRepository;

    @Transactional
    @Timed(value = "service.merkle.findLatestProof", percentiles = { 0.3, 0.5, 0.95 })
    public Optional<VoteMerkleProof> findLatestProof(String eventId, String voteId) {
        return voteMerkleProofRepository.findLatestProof(eventId, voteId);
    }

    @Transactional
    @Timed(value = "service.merkle.store", percentiles = { 0.3, 0.5, 0.95 })
    public VoteMerkleProof store(VoteMerkleProof voteMerkleProof) {
        return voteMerkleProofRepository.saveAndFlush(voteMerkleProof);
    }

}
