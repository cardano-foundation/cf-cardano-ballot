package org.cardano.foundation.voting.service.auth.web3;

import io.vavr.control.Either;
import lombok.Builder;
import lombok.Getter;
import org.cardano.foundation.voting.domain.web3.KERIEnvelope;
import org.cardano.foundation.voting.domain.web3.SignedKERI;
import org.zalando.problem.Problem;

import java.util.Map;
import java.util.Optional;

@Getter
@Builder
public class KeriWeb3Details implements Web3ConcreteDetails {

    private Web3CommonDetails web3CommonDetails;

    private SignedKERI signedKERI;

    private KERIEnvelope<Map<String, Object>> envelope;

    public String getUri() {
        return envelope.getUri();
    }

    @Override
    public Either<Problem, Long> getRequestSlot() {
        return envelope.getSlotAsLong();
    }

    @Override
    public Map<String, Object> getData() {
        return envelope.getData();
    }

    @Override
    public String getSignature() {
        return signedKERI.getSignature();
    }

    @Override
    public Optional<String> getPayload() {
        return Optional.of(signedKERI.getPayload());
    }

    @Override
    public Optional<String> getPublicKey() {
        return Optional.of(signedKERI.getAid());
    }

    @Override
    public String getSignedJson() {
         return signedKERI.getPayload();
    }

}
