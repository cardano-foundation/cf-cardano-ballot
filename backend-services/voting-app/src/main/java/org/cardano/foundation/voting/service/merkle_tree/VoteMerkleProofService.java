package org.cardano.foundation.voting.service.merkle_tree;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.entity.VoteMerkleProof;
import org.cardano.foundation.voting.repository.VoteMerkleProofRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoteMerkleProofService {

    private final VoteMerkleProofRepository voteMerkleProofRepository;

    @Transactional(readOnly = true)
    @Timed(value = "service.merkle.findLatestProof", histogram = true)
    public Optional<VoteMerkleProof> findLatestProof(String eventId, String voteId) {
        return voteMerkleProofRepository.findLatestProof(eventId, voteId);
    }

}
