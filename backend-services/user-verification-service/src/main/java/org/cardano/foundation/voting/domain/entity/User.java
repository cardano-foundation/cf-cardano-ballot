package org.cardano.foundation.voting.domain.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
@Builder
@Slf4j
public class User extends AbstractTimestampEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String voterStakeAddress;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

}
