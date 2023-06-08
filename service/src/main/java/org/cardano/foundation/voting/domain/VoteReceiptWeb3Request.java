package org.cardano.foundation.voting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoteReceiptWeb3Request extends Web3Request {

    @NotNull
    private String eventId;

    @NotNull
    private String categoryId;

    @NotNull
    private String voterStakeAddress;

}
