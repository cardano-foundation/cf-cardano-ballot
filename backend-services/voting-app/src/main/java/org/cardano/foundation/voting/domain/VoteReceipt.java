package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Data;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.web3.WalletType;

import java.util.List;
import java.util.Optional;

@Data
@Builder
public class VoteReceipt {

    private String id;

    private String event;

    private String category;

    private String proposal;

    @Builder.Default
    private Optional<String> votingPower = Optional.empty();

    private String walletId;

    private WalletType walletType;

    private String signature;

    @Builder.Default
    private Optional<String> payload = Optional.empty();

    @Builder.Default
    private Optional<String> publicKey = Optional.empty();

    private Status status;

    private MerkleProof merkleProof;

    @Builder.Default
    private Optional<ChainFollowerClient.FinalityScore> finalityScore = Optional.empty();

    private String votedAtSlot;

    public enum Status {
        BASIC, // without merkle proof committed to L1 yet

        PARTIAL, // there is a merkle proof but L1 commitment is not visible on chain yet

        // with merkle proof committed to L1 but on L1 there has been a rollback - no longer visible on chain
        //, in fact it has been on chain and it's gome
        ROLLBACK,

        FULL // with merkle proof committed to L1 and visible on chain
    }

    @Data
    @Builder
    public static class MerkleProof {

        private String transactionHash;

        @Builder.Default
        private Optional<Long> absoluteSlot = Optional.empty();

        @Builder.Default
        private Optional<String> blockHash = Optional.empty();

        private String rootHash;

        private List<MerkleProofItem> steps;

    }

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
