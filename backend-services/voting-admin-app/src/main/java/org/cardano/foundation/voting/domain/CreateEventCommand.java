package org.cardano.foundation.voting.domain;//package org.cardano.foundation.voting.domain.web3;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nullable;

@Getter
@Builder
@ToString
public class CreateEventCommand {

    private String id; // e.g. Voltaire_Pre_Ratification

    private String team; // e.g. CF Team // TODO what about team spoofing - do we need that team has private / public key

    @Builder.Default
    private boolean gdprProtection = true; // GDPR protection is enabled by default and it protects proposal ids not being stored on chain

    private VotingEventType votingEventType;

    @Nullable
    private Integer startEpoch;

    @Nullable
    private Integer endEpoch;

    @Nullable
    private Long startSlot;

    @Nullable
    private Long endSlot;

    @Nullable
    private Integer snapshotEpoch;

    private SchemaVersion version;

}
