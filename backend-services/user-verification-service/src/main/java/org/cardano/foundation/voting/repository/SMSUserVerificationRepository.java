package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.SMSUserVerification;
import org.cardano.foundation.voting.domain.WalletType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SMSUserVerificationRepository extends JpaRepository<SMSUserVerification, String> {

    @Query("SELECT uv FROM SMSUserVerification uv WHERE uv.eventId = :eventId")
    List<SMSUserVerification> findAllByEventId(@Param("eventId") String eventId);

    @Query("SELECT uv FROM SMSUserVerification uv WHERE uv.status = 'VERIFIED' AND uv.eventId = :eventId AND uv.phoneNumberHash = :phoneNumberHash")
    List<SMSUserVerification> findAllCompletedPerPhone(@Param("eventId") String eventId, @Param("phoneNumberHash") String phoneNumberHash);

    @Query("SELECT uv FROM SMSUserVerification uv WHERE uv.status = 'VERIFIED' AND uv.eventId = :eventId AND uv.walletType = :walletType AND uv.walletId = :walletId")
    List<SMSUserVerification> findAllCompletedPerWalletId(@Param("eventId") String eventId, @Param("walletType") WalletType walletType, @Param("walletId") String walletId);

    @Query("SELECT uv FROM SMSUserVerification uv WHERE uv.status = 'PENDING' AND uv.eventId = :eventId")
    List<SMSUserVerification> findAllPending(@Param("eventId") String eventId);

    @Query("SELECT COUNT(*) FROM SMSUserVerification uv WHERE uv.status = 'PENDING' AND uv.eventId = :eventId AND uv.walletId = :walletId AND uv.phoneNumberHash = :phoneNumberHash")
    int findPendingPerWalletIdPerPhoneCount(@Param("eventId") String eventId, @Param("walletId") String walletId, @Param("phoneNumberHash") String phoneNumberHash);

    @Query("SELECT uv FROM SMSUserVerification uv WHERE uv.status = 'PENDING'" +
            " AND uv.eventId = :eventId" +
            " AND uv.walletId = :walletId" +
            " AND uv.requestId = :requestId")
    Optional<SMSUserVerification> findPendingVerificationsByEventIdAndWalletIdAndRequestId(@Param("eventId") String eventId,
                                                                                           @Param("walletId") String walletId,
                                                                                           @Param("requestId") String requestId
    );

}
