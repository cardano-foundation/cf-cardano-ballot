package org.cardano.foundation.voting.service.auth.web3;

import io.vavr.control.Either;
import org.zalando.problem.Problem;

import java.util.Map;
import java.util.Optional;

public interface Web3ConcreteDetails {

    Web3CommonDetails getWeb3CommonDetails();

    Either<Problem, Long> getRequestSlot();

    Map<String, Object> getData();

    String getSignature();

    Optional<String> getPayload();

    Optional<String> getPublicKey();

    String getSignedJson();

}
