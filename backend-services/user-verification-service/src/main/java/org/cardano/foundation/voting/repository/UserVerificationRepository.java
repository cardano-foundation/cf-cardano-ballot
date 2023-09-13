package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.SMSUserVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVerificationRepository extends JpaRepository<SMSUserVerification, String> {

    @Query("SELECT uv FROM SMSUserVerification uv WHERE uv.eventId = :eventId")
    List<SMSUserVerification> findAllByEventId(@Param("eventId") String eventId);

    @Query("SELECT uv FROM SMSUserVerification uv WHERE uv.status = 'VERIFIED' AND uv.eventId = :eventId AND uv.phoneNumberHash = :phoneNumberHash")
    List<SMSUserVerification> findAllCompletedPerPhone(@Param("eventId") String eventId, @Param("phoneNumberHash") String phoneNumberHash);

    @Query("SELECT uv FROM SMSUserVerification uv WHERE uv.status = 'VERIFIED' AND uv.eventId = :eventId AND uv.stakeAddress = :stakeAddress")
    List<SMSUserVerification> findAllCompletedPerStake(@Param("eventId") String eventId, @Param("stakeAddress") String stakeAddress);

    @Query("SELECT uv FROM SMSUserVerification uv WHERE uv.status = 'PENDING' AND uv.eventId = :eventId")
    List<SMSUserVerification> findAllPending(@Param("eventId") String eventId);

    @Query("SELECT COUNT(*) FROM SMSUserVerification uv WHERE uv.status = 'PENDING' AND uv.eventId = :eventId AND uv.stakeAddress = :stakeAddress AND uv.phoneNumberHash = :phoneNumberHash")
    int findPendingPerStakeAddressPerPhoneCount(@Param("eventId") String eventId, @Param("stakeAddress") String stakeAddress, @Param("phoneNumberHash") String phoneNumberHash);

    @Query("SELECT uv FROM SMSUserVerification uv WHERE uv.status = 'PENDING'" +
            " AND uv.eventId = :eventId" +
            " AND uv.stakeAddress = :stakeAddress" +
            " AND uv.requestId = :requestId")
    Optional<SMSUserVerification> findPendingVerificationsByEventIdAndStakeAddressAndRequestId(@Param("eventId") String eventId,
                                                                                               @Param("stakeAddress") String stakeAddress,
                                                                                               @Param("requestId") String requestId
    );

}
