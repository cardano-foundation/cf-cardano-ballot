package org.cardano.foundation.voting.domain;

import org.cardano.foundation.voting.repository.VoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VoteSerialisationsTest {

    @Test
    @DisplayName("Test CARDANO wallet type with valid verification result")
    public void testCardanoWalletTypeWithValidVerificationResult() {
        // Arrange
        VoteRepository.CompactVote vote = mock(VoteRepository.CompactVote.class);
        Mockito.when(vote.getWalletType()).thenReturn(WalletType.CARDANO);
        Mockito.when(vote.getSignature()).thenReturn("cafebabe");
        Mockito.when(vote.getPublicKey()).thenReturn(Optional.of("deadbeef"));

        // Act
        byte[] result = VoteSerialisations.VOTE_SERIALISER.apply(vote);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test CARDANO wallet type with null verification message")
    public void testCardanoWalletTypeWithNullVerificationMessage() {
        // Arrange
        VoteRepository.CompactVote vote = mock(VoteRepository.CompactVote.class);
        Mockito.when(vote.getWalletType()).thenReturn(WalletType.CARDANO);
        Mockito.when(vote.getSignature()).thenReturn("cafebabe");
        Mockito.when(vote.getPublicKey()).thenReturn(Optional.of("cafebabecafebabecafebabecafebabe"));

        // Act
        byte[] result = VoteSerialisations.VOTE_SERIALISER.apply(vote);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test KERI wallet type with non-empty payload")
    public void testKeriWalletTypeWithPayload() {
        // Arrange
        String signature = "signature";
        String payload = "payload";
        VoteRepository.CompactVote vote = mock(VoteRepository.CompactVote.class);
        Mockito.when(vote.getWalletType()).thenReturn(WalletType.KERI);
        Mockito.when(vote.getSignature()).thenReturn(signature);
        Mockito.when(vote.getPayload()).thenReturn(Optional.of(payload));

        // Act
        byte[] result = VoteSerialisations.VOTE_SERIALISER.apply(vote);

        // Assert
        assertThat(result).isNotNull();
        // Optionally, verify the content of the result
    }

    @Test
    @DisplayName("Test KERI wallet type with empty payload")
    public void testKeriWalletTypeWithEmptyPayload() {
        // Arrange
        String signature = "signature";
        VoteRepository.CompactVote vote = mock(VoteRepository.CompactVote.class);
        Mockito.when(vote.getWalletType()).thenReturn(WalletType.KERI);
        Mockito.when(vote.getSignature()).thenReturn(signature);
        Mockito.when(vote.getPayload()).thenReturn(Optional.empty());

        // Act
        byte[] result = VoteSerialisations.VOTE_SERIALISER.apply(vote);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test KERI wallet type with large payload")
    public void testKeriWalletTypeWithLargePayload() {
        // Arrange
        String signature = "signature";
        StringBuilder largePayloadBuilder = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largePayloadBuilder.append("a");
        }
        String largePayload = largePayloadBuilder.toString();
        VoteRepository.CompactVote vote = mock(VoteRepository.CompactVote.class);
        Mockito.when(vote.getWalletType()).thenReturn(WalletType.KERI);
        Mockito.when(vote.getSignature()).thenReturn(signature);
        Mockito.when(vote.getPayload()).thenReturn(Optional.of(largePayload));

        // Act
        byte[] result = VoteSerialisations.VOTE_SERIALISER.apply(vote);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test KERI wallet type with null signature")
    public void testKeriWalletTypeWithNullSignature() {
        // Arrange
        VoteRepository.CompactVote vote = mock(VoteRepository.CompactVote.class);
        Mockito.when(vote.getWalletType()).thenReturn(WalletType.KERI);
        Mockito.when(vote.getSignature()).thenReturn(null);
        Mockito.when(vote.getPayload()).thenReturn(Optional.of("payload"));

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            VoteSerialisations.VOTE_SERIALISER.apply(vote);
        }, "Applying the serialiser with null signature should throw NullPointerException");
    }

    @Test
    @DisplayName("Test KERI wallet type with null payload")
    public void testKeriWalletTypeWithNullPayload() {
        // Arrange
        String signature = "signature";
        VoteRepository.CompactVote vote = mock(VoteRepository.CompactVote.class);
        when(vote.getWalletType()).thenReturn(WalletType.KERI);
        when(vote.getSignature()).thenReturn(signature);
        when(vote.getPayload()).thenReturn(Optional.empty());

        // Act
        byte[] result = VoteSerialisations.VOTE_SERIALISER.apply(vote);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test unknown wallet type")
    public void testUnknownWalletType() {
        // Arrange
        VoteRepository.CompactVote vote = mock(VoteRepository.CompactVote.class);
        when(vote.getWalletType()).thenReturn(null); // Unknown or unsupported wallet type

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            VoteSerialisations.VOTE_SERIALISER.apply(vote);
        }, "Applying the serialiser with unknown wallet type should throw NullPointerException");
    }

}