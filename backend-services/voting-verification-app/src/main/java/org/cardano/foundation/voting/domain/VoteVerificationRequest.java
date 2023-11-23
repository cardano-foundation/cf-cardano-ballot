package org.cardano.foundation.voting.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@Builder
@Setter
@ToString
@Schema(description = "Request object for vote verification")
public class VoteVerificationRequest {

    @NotBlank
    @Schema(description = "Root hash of the merkle tree", required = true)
    private String rootHash;

    @NotBlank
    @Schema(description = "COSE signature of the vote", required = true)
    protected String voteCoseSignature;

    @Schema(description = "Public key for the vote")
    protected Optional<@NotBlank String> voteCosePublicKey;

    @Builder.Default
    @Schema(description = "Merkle proof")
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
