package org.cardano.foundation.voting.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
@Builder
@AllArgsConstructor
public class WrappedVote {

    private String walletId;

    private WalletType walletType;

    @Builder.Default
    private Optional<String> payload = Optional.empty();

    private String signature;

    @Builder.Default
    private Optional<String> publicKey = Optional.empty();

    public static WrappedVote createCardanoVote(String walletId,
                                                String signature,
                                                Optional<String> publicKey) {
        return WrappedVote.builder()
                .walletId(walletId)
                .walletType(WalletType.CARDANO)
                .signature(signature)
                .payload(Optional.empty())
                .publicKey(publicKey)
                .build();
    }

    public static WrappedVote createKERIVote(String walletId,
                                             String signature,
                                             String payload) {
        return WrappedVote.builder()
                .walletId(walletId)
                .walletType(WalletType.KERI)
                .signature(signature)
                .payload(Optional.of(payload))
                .publicKey(Optional.empty())
                .build();
    }

}
