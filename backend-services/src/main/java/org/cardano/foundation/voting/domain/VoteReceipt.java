package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VoteReceipt {

    private String id;

    private String event;

    private String category;

    private String proposal;

    private String voterStakingAddress;

    private String coseSignature;

    private String cosePublicKey;

    private CardanoNetwork cardanoNetwork;

    private Status status;

    private MerkleProof merkleProof;

    private long votedAtSlot;

    public static enum Status {
        BASIC, // without merkle proof committed to L1 yet
        FULL // with merkle proof committed to L1
    }

    @Data
    @Builder
    public static class MerkleProof {

        private String transactionHash;
        private long absoluteSlot;
        private String blockHash;
        private String rootHash;
        private List<MerkleProofItem> steps;

    }

    @Data
    @Builder
    public static class MerkleProofItem {

        private MerkleProofType type;
        private String hash;

    }

    public static enum MerkleProofType {
        Left, Right
    }

}
