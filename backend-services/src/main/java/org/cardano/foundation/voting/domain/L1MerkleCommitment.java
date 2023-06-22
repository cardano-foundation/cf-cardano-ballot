package org.cardano.foundation.voting.domain;

import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.Vote;
import org.cardanofoundation.merkle.MerkleElement;

import java.util.List;

// L1 Merkle commitment for an event
public record L1MerkleCommitment(List<Vote> votes, MerkleElement<Vote> root, Event event) { }