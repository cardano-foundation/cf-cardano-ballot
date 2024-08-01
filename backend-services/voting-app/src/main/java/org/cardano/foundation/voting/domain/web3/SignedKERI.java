package org.cardano.foundation.voting.domain.web3;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class SignedKERI {

    @NotBlank
    protected String signature;

    @NotBlank
    protected String payload;

    @NotBlank
    protected String aid;

}
