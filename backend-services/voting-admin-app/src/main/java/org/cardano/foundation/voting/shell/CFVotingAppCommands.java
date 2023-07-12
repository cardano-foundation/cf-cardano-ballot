package org.cardano.foundation.voting.shell;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@ShellComponent
@Slf4j
public class CFVotingAppCommands {

    @Autowired
    private HttpClient httpClient;

    @Value("${voting.app.base.url:http://localhost:8080}")
    private String votingAppBaseUrl;

    @ShellMethod(key = "full-metadata-scan", value = "Invokes full metadata scan on voting-app")
    public String votingAppFullMetadataScan() throws IOException, InterruptedException {
        log.info("Invoking full metadata scan on voting-app...");

        var metadataScanRequest = HttpRequest.newBuilder()
                .uri(URI.create(votingAppBaseUrl + "/api/admin/full-metadata-scan") )
                .POST(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json")
                .build();

        var response = httpClient.send(metadataScanRequest, HttpResponse.BodyHandlers.discarding());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return "Successfully en-queued metadata scan request.";
        }

        return "Error en-queuing metadata scan request: " + response.statusCode() + " " + response.body();
    }

}
