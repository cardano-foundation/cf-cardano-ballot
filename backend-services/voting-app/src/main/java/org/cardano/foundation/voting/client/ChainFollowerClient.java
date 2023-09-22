package org.cardano.foundation.voting.client;

import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.VotingEventType;
import org.cardano.foundation.voting.domain.VotingPowerAsset;
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

    public Either<Problem, Optional<AccountResponse>> findAccount(String eventName, String stakeAddress) {
        var url = String.format("%s/api/account/{event}/{stakeAddress}", ledgerFollowerBaseUrl);

        try {
            return Either.right(Optional.ofNullable(restTemplate.getForObject(url, AccountResponse.class, eventName, stakeAddress)));
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

    public Either<Problem, Optional<EventDetailsResponse>> getEventDetails(String eventId) {
        var url = String.format("%s/api/reference/event/{id}", ledgerFollowerBaseUrl);

        try {
            return Either.right(Optional.ofNullable(restTemplate.getForObject(url, EventDetailsResponse.class, eventId)));
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

    public record EventDetailsResponse(String id,
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
                                       List<CategoryDetailsResponse> categories) {

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
                                      CardanoNetwork network) {}

    public record ChainTipResponse(String hash,
                                   int epochNo,
                                   int absoluteSlot,
                                   boolean synced,
                                   CardanoNetwork network) {

        public boolean isNotSynced() {
            return !synced;
        }

    }

    public record AccountResponse(
                           String stakeAddress,
                           int epochNo,
                           String votingPower,
                           VotingPowerAsset votingPowerAsset) { }

}
