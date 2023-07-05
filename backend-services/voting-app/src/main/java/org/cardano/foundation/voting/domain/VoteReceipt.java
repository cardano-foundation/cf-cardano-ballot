package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Data;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Data
@Builder
public class VoteReceipt {

    private String id;

    private String event;

    private String category;

    private String proposal;

    private String proposalText;

    private String votingPower;

    private VotingPowerFormat votingPowerFormat;

    private String voterStakingAddress;

    private String coseSignature;

    private String cosePublicKey;

    private CardanoNetwork cardanoNetwork;

    private Status status;

    private MerkleProof merkleProof;

    private Optional<TransactionDetails.FinalityScore> finalityScore;

    private long votedAtSlot;

    public static enum Status {
        BASIC, // without merkle proof committed to L1 yet

        PARTIAL, // there is a merkle proof but L1 commitment is not visible on chain yet or already rolled back

        FULL // with merkle proof committed to L1 and visible on chain
    }

    @Data
    @Builder
    public static class MerkleProof {

        private String transactionHash;
        private Optional<Long> absoluteSlot;
        private Optional<String> blockHash;
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
