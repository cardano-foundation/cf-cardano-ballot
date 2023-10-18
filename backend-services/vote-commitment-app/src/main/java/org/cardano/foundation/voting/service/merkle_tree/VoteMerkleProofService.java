package org.cardano.foundation.voting.service.merkle_tree;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.entity.VoteMerkleProof;
import org.cardano.foundation.voting.repository.VoteMerkleProofRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void softDeleteAllProofsAfterSlot(long slot) {
        log.info("Soft deleting all proofs after slot:{}", slot);

        voteMerkleProofRepository.invalidateMerkleProofsAfterSlot(slot);
    }

}
