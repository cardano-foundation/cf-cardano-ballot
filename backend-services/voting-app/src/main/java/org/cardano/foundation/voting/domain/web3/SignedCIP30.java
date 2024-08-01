package org.cardano.foundation.voting.domain.web3;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class SignedCIP30 {

    @NotBlank
    protected String signature;

    @Builder.Default
    protected Optional<@NotBlank String> publicKey = Optional.empty();

}
