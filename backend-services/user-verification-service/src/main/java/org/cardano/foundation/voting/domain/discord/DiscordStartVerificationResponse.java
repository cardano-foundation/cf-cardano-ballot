package org.cardano.foundation.voting.domain.discord;

import lombok.Builder;
import lombok.Getter;
import org.cardano.foundation.voting.domain.VerificationStatus;

@Builder
@Getter
public class DiscordStartVerificationResponse {

    String eventId;
    String discordIdHash;
    VerificationStatus status;

}
