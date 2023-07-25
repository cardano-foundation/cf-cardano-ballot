package org.cardano.foundation.voting.shell;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.address.Address;
import com.bloxbean.cardano.client.cip.cip30.CIP30DataSigner;
import com.bloxbean.cardano.client.cip.cip30.DataSignError;
import com.bloxbean.cardano.client.cip.cip30.DataSignature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;

@ShellComponent
@Slf4j
public class CFVotingAppCommands {

    @Autowired
    private HttpClient httpClient;

    @Value("${voting.app.base.url:http://localhost:8080}")
    private String votingAppBaseUrl;

    @Autowired
    @Qualifier("organiser_account")
    private Account organiserAccount;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CardanoNetwork cardanoNetwork;

    @Autowired
    private BlockchainDataChainTipService blockchainDataChainTipService;

    @ShellMethod(key = "full-metadata-scan", value = "Invokes full metadata scan on voting-app")
    public String votingAppFullMetadataScan() throws IOException, InterruptedException, DataSignError {
        log.info("Invoking full metadata scan on voting-app...");


        String uri = votingAppBaseUrl + "/api/admin/full-metadata-scan";

        var stakeAddress = organiserAccount.stakeAddress();
        var stakeAddressAccount = new Address(stakeAddress);

        var dataJson = objectMapper.createObjectNode();
        dataJson.put("address", stakeAddress);
        dataJson.put("network", cardanoNetwork.name());

        var cip93Envelope = objectMapper.createObjectNode();
        cip93Envelope.put("action", "FULL_METADATA_SCAN");
        cip93Envelope.put("actionText", "FULL_METADATA_SCAN");
        cip93Envelope.put("slot", String.valueOf(blockchainDataChainTipService.getChainTip().getAbsoluteSlot()));
        cip93Envelope.put("uri", uri);
        cip93Envelope.set("data", dataJson);

        DataSignature dataSignature = CIP30DataSigner.INSTANCE.signData(
            stakeAddressAccount.getBytes(),
            cip93Envelope.toString().getBytes(UTF_8),
            organiserAccount.stakeHdKeyPair().getPrivateKey().getKeyData(),
            organiserAccount.stakeHdKeyPair().getPublicKey().getKeyData()
        );

        var httpPostRequestBody = objectMapper.createObjectNode();
        httpPostRequestBody.put("coseSignature", dataSignature.signature());
        httpPostRequestBody.put("cosePublicKey", dataSignature.key());

        System.out.println(httpPostRequestBody.toPrettyString());

        var metadataScanRequest = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .POST(HttpRequest.BodyPublishers.ofString(httpPostRequestBody.toString()))
                .header("Content-Type", "application/json")
                .build();

        var response = httpClient.send(metadataScanRequest, HttpResponse.BodyHandlers.discarding());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return "Successfully en-queued metadata scan request.";
        }

        return "Error en-queuing metadata scan request: " + response.statusCode() + " " + response.body();
    }

}
