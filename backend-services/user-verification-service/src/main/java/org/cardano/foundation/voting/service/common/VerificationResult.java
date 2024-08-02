package org.cardano.foundation.voting.service.common;
import java.util.Optional;

public class VerificationResult {
    private final String message;
    private final Optional<String> address;

    public VerificationResult(String message, Optional<String> address) {
        this.message = message;
        this.address = address;
    }

    public String getMessage() {
        return message;
    }

    public Optional<String> getAddress() {
        return address;
    }
}
