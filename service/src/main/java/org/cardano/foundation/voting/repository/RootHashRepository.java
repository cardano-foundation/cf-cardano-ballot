package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.RootHash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RootHashRepository extends JpaRepository<RootHash, String> {

}
