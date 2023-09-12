package org.cardano.foundation.voting.domain;

import org.cardano.foundation.voting.domain.web3.SignedWeb3Request;
import org.cardanofoundation.merkle.MerkleElement;

import java.util.List;

// L1 Merkle commitment for an event
public record L1MerkleCommitment(List<SignedWeb3Request> signedVotes, MerkleElement<SignedWeb3Request> root, String eventId) { }