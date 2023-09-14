package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.DiscordUserVerification;
import org.cardano.foundation.voting.domain.entity.SMSUserVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscordUserVerificationRepository extends JpaRepository<SMSUserVerification, String> {

    @Query("SELECT uv FROM DiscordUserVerification uv WHERE uv.status = 'VERIFIED' AND uv.eventId = :eventId AND uv.stakeAddress = :stakeAddress")
    Optional<DiscordUserVerification> finCompletedVerification(@Param("eventId") String eventId, @Param("stakeAddress") String stakeAddress);

}
