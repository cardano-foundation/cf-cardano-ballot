package org.cardano.foundation.voting.service.blockchain_state.cardano_submit_api;

import com.bloxbean.cardano.client.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainTransactionSubmissionService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@RequiredArgsConstructor
public class CardanoSubmitApiBlockchainTransactionSubmissionService implements BlockchainTransactionSubmissionService {

    private final String cardanoSubmitApiUrl;

    private final HttpClient httpClient;

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
            var json = r.body();

            var jNode = JsonUtil.parseJson(json);
            var txId  = jNode.asText();

            log.info("txId:{}", txId);

            return txId;
        }

        log.error("Error submitting transaction, status code: {}, body: {}", r.statusCode(), r.body());

        throw new RuntimeException("Error submitting transaction: " + r.body());
    }

}
