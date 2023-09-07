package org.cardano.foundation.voting.service.merkle_tree;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.entity.VoteMerkleProof;
import org.cardano.foundation.voting.repository.VoteMerkleProofRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class VoteMerkleProofService {

    @Autowired
    private VoteMerkleProofRepository voteMerkleProofRepository;

    @Transactional(readOnly = true)
    @Timed(value = "service.merkle.findLatestProof", histogram = true)
    public Optional<VoteMerkleProof> findLatestProof(String eventId, String voteId) {
        return voteMerkleProofRepository.findLatestProof(eventId, voteId);
    }

    @Transactional
    @Timed(value = "service.merkle.store", histogram = true)
    public VoteMerkleProof store(VoteMerkleProof voteMerkleProof) {
        return voteMerkleProofRepository.saveAndFlush(voteMerkleProof);
    }

    @Transactional
    @Timed(value = "service.merkle.store.all", histogram = true)
    public void storeAll(List<VoteMerkleProof> voteMerkleProofs) {
        voteMerkleProofRepository.saveAll(voteMerkleProofs);
        voteMerkleProofRepository.flush();
    }

    @Transactional
    @Timed(value = "service.merkle.softDeleteAllProofsAfterSlot", histogram = true)
    public void softDeleteAllProofsAfterSlot(long slot) {
        log.info("Soft deleting all proofs after slot:{}", slot);

        voteMerkleProofRepository.invalidateMerkleProofsAfterSlot(slot);
        voteMerkleProofRepository.flush();
    }

}
