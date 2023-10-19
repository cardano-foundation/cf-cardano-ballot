package org.cardano.foundation.voting.service.merkle_tree;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.entity.VoteMerkleProof;
import org.cardano.foundation.voting.repository.VoteMerkleProofRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoteMerkleProofService {

    private final VoteMerkleProofRepository voteMerkleProofRepository;

    @Transactional
    @Timed(value = "service.merkle.store", histogram = true)
    public VoteMerkleProof store(VoteMerkleProof voteMerkleProof) {
        return voteMerkleProofRepository.saveAndFlush(voteMerkleProof);
    }

    @Transactional
    @Timed(value = "service.merkle.softDeleteAllProofsAfterSlot", histogram = true)
    public int softDeleteAllProofsAfterSlot(String eventId, long slot) {
        log.info("Soft deleting all proofs for eventId: {}, after slot:{}", eventId, slot);

        return voteMerkleProofRepository.invalidateMerkleProofsAfterSlot(eventId, slot);
    }

    @Transactional
    @Timed(value = "service.merkle.findTop1InvalidatedByEvent", histogram = true)
    public List<VoteMerkleProof> findTop1InvalidatedByEventId(String eventId) {
        log.info("Finding top 1 invalidated proof for eventId:{}", eventId);

        return voteMerkleProofRepository.findTop1ByEventIdAndInvalidatedOrderByCreatedAtDesc(eventId, true);
    }

}
