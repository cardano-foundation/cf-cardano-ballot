package org.cardano.foundation.voting.domain;

import org.cardano.foundation.voting.repository.VoteRepository;
import org.cardanofoundation.merkle.MerkleElement;

import java.util.List;

// L1 Merkle commitment for an event
public record L1MerkleCommitment(List<VoteRepository.CompactVote> signedVotes,
                                 MerkleElement<VoteRepository.CompactVote> root,
                                 String eventId) { }