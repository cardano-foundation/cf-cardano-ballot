package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.OnchainMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataRepository extends JpaRepository<OnchainMetadata, String> {
}
