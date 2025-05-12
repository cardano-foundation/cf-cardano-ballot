package com.cardano.foundation.candidateapp.repository.candidate;

import com.cardano.foundation.candidateapp.model.candidate.ConsortiumMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsortiumMemberRepository extends JpaRepository<ConsortiumMember, Long> {
    List<ConsortiumMember> findByConsortiumCandidateId(Long consortiumCandidateId);
}
