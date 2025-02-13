package org.cardano.foundation.voting.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.ChainNetwork;
import org.cardano.foundation.voting.domain.TallyType;
import org.cardano.foundation.voting.domain.VotingEventType;
import org.cardano.foundation.voting.domain.VotingPowerAsset;
import org.cardano.foundation.voting.domain.web3.WalletType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.common.HttpStatusAdapter;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@Component
@Slf4j
public class ChainFollowerClient {

    private final RestTemplate restTemplate;

    @Value("${ledger.follower.app.base.url}")
    private String ledgerFollowerBaseUrl;

    public Either<Problem, L1CategoryResults> getVotingResultsPerCategory(String eventId,
                                                                          String categoryId,
                                                                          String tallyName
                                                               ) {
        var url = String.format("%s/api/tally/voting-results/{eventId}/{categoryId}/{tallyName}", ledgerFollowerBaseUrl);

        try {
            val l1CategoryResults = restTemplate.getForObject(url,
                    L1CategoryResults.class,
                    eventId,
                    categoryId,
                    tallyName
            );

            return Either.right(l1CategoryResults);
        } catch (HttpClientErrorException e) {
            return Either.left(Problem.builder()
                    .withTitle("CATEGORY_RESULTS_ERROR")
                    .withDetail("Unable to get category results from chain-tip follower service, reason:" + e.getMessage())
                    .withStatus(new HttpStatusAdapter(e.getStatusCode()))
                    .build());
        }
    }

    public Either<Problem, List<L1CategoryResults>> getVotingResultsForAllCategories(String eventId,
                                                                                     String tallyName
    ) {
        var url = String.format("%s/api/tally/voting-results/{eventId}/{tallyName}", ledgerFollowerBaseUrl);

        try {
            val l1CategoryResults = restTemplate.getForObject(url, L1CategoryResults[].class, eventId, tallyName);
            if (l1CategoryResults == null) {
                return Either.right(List.of());
            }

            return Either.right(Arrays.asList(l1CategoryResults));
        } catch (HttpClientErrorException e) {
            return Either.left(Problem.builder()
                    .withTitle("CATEGORY_RESULTS_ERROR")
                    .withDetail("Unable to get category results from chain-tip follower service, reason:" + e.getMessage())
                    .withStatus(new HttpStatusAdapter(e.getStatusCode()))
                    .build());
        }
    }

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

    public Either<Problem, Optional<AccountResponse>> findAccount(String eventName,
                                                                  WalletType walletType,
                                                                  String walletId) {
        var url = String.format("%s/api/account/{event}/{walletType}/{walletId}", ledgerFollowerBaseUrl);

        try {
            @Nullable
            val accountResponse = restTemplate.getForObject(url, AccountResponse.class,
                    eventName,
                    walletType,
                    walletId
            );

            return Either.right(Optional.ofNullable(accountResponse));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == NOT_FOUND) {
                return Either.right(Optional.empty());
            }

            return Either.left(Problem.builder()
                    .withTitle("ACCOUNT_ERROR")
                    .withDetail("Unable to get account details from chain-tip follower service, reason:" + e.getMessage())
                    .withStatus(new HttpStatusAdapter(e.getStatusCode()))
                    .build());
        }
    }

    public Either<Problem, Optional<TransactionDetailsResponse>> getTransactionDetails(String txHash) {
        var url = String.format("%s/api/blockchain/tx-details/{txHash}", ledgerFollowerBaseUrl);

        try {
            var txResponse = restTemplate.getForObject(url, TransactionDetailsResponse.class, txHash);

            return Either.right(Optional.ofNullable(txResponse));
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

    public Either<Problem, Optional<EventDetailsResponse>> getEventDetails(String eventId) {
        var url = String.format("%s/api/reference/event/{id}", ledgerFollowerBaseUrl);

        try {
            @Nullable
            val eventDetailsResponse = restTemplate.getForObject(url, EventDetailsResponse.class, eventId);

            return Either.right(Optional.ofNullable(eventDetailsResponse));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == NOT_FOUND) {
                return Either.right(Optional.empty());
            }

            return Either.left(Problem.builder()
                    .withTitle("REFERENCE_ERROR")
                    .withDetail("Unable to get event details from chain-tip follower service, reason:" + e.getMessage())
                    .withStatus(new HttpStatusAdapter(e.getStatusCode()))
                    .build());
        }
    }

    public record ProposalDetailsResponse(String id, String name) {}

    public record CategoryDetailsResponse(String id,
                                          boolean gdprProtection,
                                          List<ProposalDetailsResponse> proposals) {

        public Optional<ProposalDetailsResponse> findProposalById(String proposalId) {
            return proposals.stream().filter(proposal -> proposal.id().equals(proposalId)).findFirst();
        }

        public Optional<ProposalDetailsResponse> findProposalByName(String name) {
            return proposals.stream().filter(proposal -> proposal.name().equals(name)).findFirst();
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EventDetailsResponse(String id,
                                       String organisers,
                                       boolean finished,
                                       boolean notStarted,
                                       boolean isStarted,
                                       boolean active,
                                       boolean proposalsReveal,
                                       boolean commitmentsWindowOpen,
                                       boolean allowVoteChanging,
                                       boolean highLevelEventResultsWhileVoting,
                                       boolean highLevelCategoryResultsWhileVoting,
                                       boolean categoryResultsWhileVoting,
                                       VotingEventType votingEventType,
                                       List<CategoryDetailsResponse> categories,
                                       List<Tally> tallies) {

        public boolean isEventInactive() {
            return !active;
        }

        public Optional<CategoryDetailsResponse> categoryDetailsById(String categoryId) {
            return categories.stream().filter(category -> category.id().equals(categoryId)).findFirst();
        }

    }

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

    public record TransactionDetailsResponse(String transactionHash,
                                      long absoluteSlot,
                                      String blockHash,
                                      long transactionsConfirmations,
                                      FinalityScore finalityScore,
                                      ChainNetwork network) {}

    public record ChainTipResponse(String hash,
                                   int epochNo,
                                   long absoluteSlot,
                                   boolean synced,
                                   ChainNetwork network) {

        public boolean isNotSynced() {
            return !synced;
        }

    }

    public record AccountResponse(
                           WalletType walletType,
                           String walletId,
                           int epochNo,
                           String votingPower,
                           VotingPowerAsset votingPowerAsset) { }

    public record L1CategoryResults(String tallyName,
                                    String tallyDescription,
                                    TallyType tallyType,
                                    String eventId,
                                    String categoryId,
                                    Map<String, Long> results,
                                    Map<String, Object> metadata) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Tally(String name, TallyType type) {
    }

}
