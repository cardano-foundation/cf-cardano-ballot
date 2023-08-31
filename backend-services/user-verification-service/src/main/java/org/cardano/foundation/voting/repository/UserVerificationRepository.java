package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVerificationRepository extends JpaRepository<UserVerification, String> {

    @Query("SELECT uv FROM UserVerification uv WHERE uv.status = 'VERIFIED' AND uv.eventId = :eventId AND uv.stakeAddress = :stakeAddress")
    List<UserVerification> findAllCompleted(@Param("eventId") String eventId, @Param("stakeAddress") String stakeAddress);

    @Query("SELECT uv FROM UserVerification uv WHERE uv.status = 'PENDING' AND uv.eventId = :eventId")
    List<UserVerification> findAllPending(@Param("eventId") String eventId);

    @Query("SELECT COUNT(*) FROM UserVerification uv WHERE uv.status = 'PENDING' AND uv.eventId = :eventId AND uv.stakeAddress = :stakeAddress AND uv.phoneNumberHash = :phoneNumberHash")
    int findPendingPerStakeAddressPerPhoneCount(@Param("eventId") String eventId, @Param("stakeAddress") String stakeAddress, @Param("phoneNumberHash") String phoneNumberHash);

    @Query("SELECT uv FROM UserVerification uv WHERE uv.status = 'PENDING'" +
            " AND uv.eventId = :eventId" +
            " AND uv.stakeAddress = :stakeAddress" +
            " AND uv.requestId = :requestId")
    Optional<UserVerification> findPendingVerificationsByEventIdAndStakeAddressAndRequestId(@Param("eventId") String eventId,
                                                                                            @Param("stakeAddress") String stakeAddress,
                                                                                            @Param("requestId") String requestId
    );

}
