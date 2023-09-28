package org.cardano.foundation.voting.client;

import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.common.HttpStatusAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
@Slf4j
@RegisterReflectionForBinding({
        CardanoNetwork.class,
        ChainFollowerClient.ChainTipResponse.class,
        ChainFollowerClient.TransactionDetailsResponse.class,
        ChainFollowerClient.FinalityScore.class,
        ChainFollowerClient.EventSummary.class,
})
public class ChainFollowerClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ledger.follower.app.base.url}")
    private String ledgerFollowerBaseUrl;

    public Either<Problem, ChainTipResponse> getChainTip() {
        var url = String.format("%s/api/blockchain/tip", ledgerFollowerBaseUrl);

        try {
            return Either.right(restTemplate.getForObject(url, ChainTipResponse.class));
        } catch (HttpClientErrorException e) {
            // TODO pass on the exact error serialised by chain-tip follower service (title, detail)?
            return Either.left(Problem.builder()
                    .withTitle("CHAIN_TIP_ERROR")
                    .withDetail("Unable to get chain tip from chain-tip follower service, reason:" + e.getMessage())
                    .withStatus(new HttpStatusAdapter(e.getStatusCode()))
                    .build());
        }
    }

    public Either<Problem, List<EventSummary>> findAllCommitmentWindowOpenEvents() {
        var url = String.format("%s/api/reference/event", ledgerFollowerBaseUrl);

        try {
            var allEventSummaries = Optional.ofNullable(restTemplate.getForObject(url, EventSummary[].class))
                    .map(Arrays::asList).orElse(List.of());

            var allCommitmentsOpenWindowEvents = allEventSummaries.stream()
                    .filter(EventSummary::commitmentsWindowOpen)
                    .toList();

            return Either.right(allCommitmentsOpenWindowEvents);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == NOT_FOUND) {
                return Either.right(List.of());
            }

            return Either.left(Problem.builder()
                    .withTitle("REFERENCE_ERROR")
                    .withDetail("Unable to get event details from ledger follower service, reason:" + e.getMessage())
                    .withStatus(new HttpStatusAdapter(e.getStatusCode()))
                    .build());
        }
    }

    public Either<Problem, Optional<TransactionDetailsResponse>> getTransactionDetails(String txHash) {
        var url = String.format("%s/api/blockchain/tx-details/{txHash}", ledgerFollowerBaseUrl);

        try {
            var txResponse = restTemplate.getForObject(url, TransactionDetailsResponse.class, txHash);

            return Either.right(Optional.of(txResponse));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == NOT_FOUND) {
                return Either.right(Optional.empty());
            }

            return Either.left(Problem.builder()
                    .withTitle("TRANSACTION_DETAILS_ERROR")
                    .withDetail("Unable to get account details from chain-tip follower service, reason:" + e.getMessage())
                    .withStatus(new HttpStatusAdapter(e.getStatusCode()))
                    .build());
        }
    }

    public record EventSummary(String id,
                               boolean finished,
                               boolean notStarted,
                               boolean started,
                               boolean active,
                               boolean proposalsReveal,
                               boolean commitmentsWindowOpen) {

    }

    public record TransactionDetailsResponse(String transactionHash,
                                             long absoluteSlot,
                                             String blockHash,
                                             long transactionsConfirmations,
                                             FinalityScore finalityScore,
                                             CardanoNetwork network) {}


    public enum FinalityScore {

        LOW(0),
        MEDIUM(1),
        HIGH(2),
        VERY_HIGH(3),
        FINAL(4); // TRANSACTION IS FINAL(!) - NO ROLLBACK POSSIBLE

        private final int score;

        FinalityScore(int score) {
            this.score = score;
        }

        public int getScore() {
            return score;
        }

    }

    public record ChainTipResponse(String hash,
                                   int epochNo,
                                   int absoluteSlot,
                                   boolean synced,
                                   CardanoNetwork network) {

        public boolean isNotSynced() {
            return !synced;
        }

    }

}
