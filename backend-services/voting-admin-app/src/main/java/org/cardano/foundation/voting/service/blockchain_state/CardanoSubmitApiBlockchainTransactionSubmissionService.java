package org.cardano.foundation.voting.service.blockchain_state;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@RequiredArgsConstructor
public class CardanoSubmitApiBlockchainTransactionSubmissionService implements BlockchainTransactionSubmissionService {

    private final HttpClient httpClient;

    @Value("${cardano.tx.submit.api.url}")
    private String cardanoSubmitApiUrl;

    @Override
    @SneakyThrows
    public String submitTransaction(byte[] txData) {
        var txTransactionSubmitPostRequest = HttpRequest.newBuilder()
                .uri(URI.create(cardanoSubmitApiUrl))
                .POST(HttpRequest.BodyPublishers.ofByteArray(txData))
                .header("Content-Type", "application/cbor")
                .build();

        var r = httpClient.send(txTransactionSubmitPostRequest, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() >= 200 && r.statusCode() < 300) {
            var txId = r.body();
            log.info("txId:{}", txId);

            return txId;
        }

        log.error("Error submitting transaction, status code: {}, body: {}", r.statusCode(), r.body());

        throw new RuntimeException("Error submitting transaction: " + r.body());
    }

}
