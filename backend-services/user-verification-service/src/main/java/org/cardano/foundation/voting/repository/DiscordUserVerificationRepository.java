package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.DiscordUserVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscordUserVerificationRepository extends JpaRepository<DiscordUserVerification, String> {

    @Query("SELECT uv FROM DiscordUserVerification uv WHERE uv.status = 'VERIFIED' AND uv.eventId = :eventId AND uv.stakeAddress = :stakeAddress")
    Optional<DiscordUserVerification> findCompletedVerification(@Param("eventId") String eventId,
                                                                @Param("stakeAddress") String stakeAddress
    );

    @Query("SELECT uv FROM DiscordUserVerification uv WHERE uv.status = 'VERIFIED' AND uv.eventId = :eventId AND uv.discordIdHash = :discordIdHash")
    Optional<DiscordUserVerification> findCompletedVerificationBasedOnDiscordUserHash(@Param("eventId") String eventId,
                                                                                      @Param("discordIdHash") String discordIdHash);

    @Query("SELECT uv FROM DiscordUserVerification uv WHERE uv.status = 'PENDING'" +
                                                            " AND uv.eventId = :eventId" +
                                                            " AND uv.discordIdHash = :discordIdHash" +
                                                            " AND uv.secretCode = :secretCode")
    Optional<DiscordUserVerification> findPendingVerification(@Param("eventId") String eventId,
                                                              @Param("discordIdHash") String discordIdHash,
                                                              @Param("secretCode") String secretCode
    );

}
