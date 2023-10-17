package org.cardano.foundation.voting.service;

import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.bloxbean.cardano.client.transaction.spec.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.hydra.core.utils.HexUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@RequiredArgsConstructor
public class CardanoSubmitApiBlockchainTransactionSubmissionService implements TransactionSubmissionService {

    private final HttpClient httpClient;

    @Value("${cardano.tx.submit.api.url}")
    private String cardanoSubmitApiUrl;

    @Override
    public Result<String> submitTransaction(Transaction transaction) throws CborSerializationException {
        return submitTransaction(transaction.serializeToHex());
    }

    @Override
    public Result<String> submitTransaction(String cborHex) {
        var txTransactionSubmitPostRequest = HttpRequest.newBuilder()
                .uri(URI.create(cardanoSubmitApiUrl))
                .POST(HttpRequest.BodyPublishers.ofByteArray(HexUtils.decodeHexString(cborHex)))
                .header("Content-Type", "application/cbor")
                .build();
        try {
            var r = httpClient.send(txTransactionSubmitPostRequest, HttpResponse.BodyHandlers.ofString());

            if (r.statusCode() >= 200 && r.statusCode() < 300) {
                var txId = r.body();
                log.info("Submitted TxId: {}", txId);

                return Result.success(txId).withValue(txId);
            }

            log.error("Error submitting transaction, status code: {}, body: {}", r.statusCode(), r.body());

            return Result.error("Error submitting transaction, status code: " + r.statusCode() + ", body: " + r.body());
        } catch (IOException | InterruptedException e) {
            return Result.error("Error submitting transaction, reason:" + e.getMessage());
        }
    }

}
