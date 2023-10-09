package org.cardano.foundation.voting.domain;

import lombok.Getter;

import javax.annotation.Nullable;

@Getter
public class TxResult {

    private String txId;
    private boolean isValid;

    @Nullable
    private String message;

    public TxResult(String txId, boolean isValid, String message) {
        this.txId = txId;
        this.isValid = isValid;
        this.message = message;
    }

    public TxResult(String txId, boolean isValid) {
        this.txId = txId;
        this.isValid = isValid;
    }
}
