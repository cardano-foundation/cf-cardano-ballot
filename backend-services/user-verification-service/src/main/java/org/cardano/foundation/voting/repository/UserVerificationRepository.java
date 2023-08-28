package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserVerificationRepository extends JpaRepository<UserVerification, String> {

    @Query("SELECT uv FROM UserVerification uv WHERE uv.status = 'PENDING'")
    List<UserVerification> findAllPending();

}
