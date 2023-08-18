package org.cardano.foundation.voting.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@Builder
@Setter
@ToString
public class VoteVerificationRequest {

    @NotBlank
    private String rootHash;

    @NotBlank
    protected String voteCoseSignature;

    protected Optional<@NotBlank String> voteCosePublicKey;

    @Builder.Default
    private Optional<List<MerkleProofItem>> steps = Optional.empty();

    @Data
    @Builder
    public static class MerkleProofItem {

        private MerkleProofType type;
        private String hash;

    }

    public enum MerkleProofType {
        L, R
    }

}
