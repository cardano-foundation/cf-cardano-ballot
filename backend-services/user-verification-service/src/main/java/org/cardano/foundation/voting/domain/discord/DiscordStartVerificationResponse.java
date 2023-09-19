package org.cardano.foundation.voting.domain.discord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.cardano.foundation.voting.domain.VerificationStatus;

@Builder
@Getter
@AllArgsConstructor
public class DiscordStartVerificationResponse {

    private String eventId;

    private String discordIdHash;

    private VerificationStatus status;

}
