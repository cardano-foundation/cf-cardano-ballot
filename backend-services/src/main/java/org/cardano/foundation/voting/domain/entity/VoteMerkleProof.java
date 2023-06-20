package org.cardano.foundation.voting.domain.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
@Table(name = "vote_merkle_proof")
public class VoteMerkleProof extends AbstractTimestampEntity {

    @Id
    @Column(name = "vote_id")
    private String voteId;

    @Column(name = "root_hash")
    private String rootHash;

    @Column(name = "l1_transaction_hash")
    private String l1TransactionHash;

    @Column(name = "absolute_slot")
    private long absoluteSlot;

    @Column(name = "block_hash")
    private String blockHash;

    @Column(name = "merkle_proof_json")
    private String merkleProofJson;

    @Column(name = "invalidated") // when there is a rollback event we soft delete it, invalidate the merkle proof
    private boolean invalidated;

}
