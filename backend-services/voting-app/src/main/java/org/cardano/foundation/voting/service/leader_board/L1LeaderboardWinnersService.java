package org.cardano.foundation.voting.service.leader_board;

import io.vavr.control.Either;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.Leaderboard;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;
import static org.cardano.foundation.voting.domain.TallyType.HYDRA;
import static org.zalando.problem.Status.*;

@Service
@Qualifier("l1_leaderboard_winners_service")
public class L1LeaderboardWinnersService extends AbstractWinnersService implements LeaderboardWinnersService {

    @Override
    public Either<Problem, List<Leaderboard.ByProposalsInCategoryStats>> getAllCategoriesLeaderboard(String event,
                                                                                                     boolean forceLeaderboard) {
        var eventDetailsE = chainFollowerClient.getEventDetails(event);
        if (eventDetailsE.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("ERROR_GETTING_EVENT_DETAILS")
                    .withDetail("Unable to get event details from chain-tip follower service, event:" + event)
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build()
            );
        }
        var eventDetailsResponseM = eventDetailsE.get();
        if (eventDetailsResponseM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, event:" + event)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }
        var eventDetails = eventDetailsResponseM.orElseThrow();

        return getCategoryLeaderboardForAllCategories(eventDetails, forceLeaderboard);
    }

    @Override
    public Either<Problem, Optional<Leaderboard.ByProposalsInCategoryStats>> getCategoryLeaderboard(String event,
                                                                                                    String category,
                                                                                                    boolean forceLeaderboard) {
        var eventDetailsE = chainFollowerClient.getEventDetails(event);
        if (eventDetailsE.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("ERROR_GETTING_EVENT_DETAILS")
                    .withDetail("Unable to get event details from chain-tip follower service, event:" + event)
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build()
            );
        }
        var eventDetailsResponseM = eventDetailsE.get();
        if (eventDetailsResponseM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, event:" + event)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }
        var eventDetails = eventDetailsResponseM.orElseThrow();

        var categoryM = eventDetails.categoryDetailsById(category);
        if (categoryM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_CATEGORY")
                    .withDetail("Unrecognised category, category:" + category)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }
        var categoryDetails = categoryM.orElseThrow();

        var categoryLeaderboardAvailableE = isCategoryLeaderboardAvailable(eventDetails, forceLeaderboard);
        if (categoryLeaderboardAvailableE.isEmpty()) {
            return Either.left(categoryLeaderboardAvailableE.getLeft());
        }

        var isCategoryLeaderBoardAvailable = categoryLeaderboardAvailableE.get();
        if (!isCategoryLeaderBoardAvailable) {
            return Either.left(Problem.builder()
                    .withTitle("VOTING_RESULTS_NOT_AVAILABLE")
                    .withDetail("Category level voting results not available until results can be revealed!")
                    .withStatus(FORBIDDEN)
                    .build()
            );
        }

        var hydraTallyNameM = findFirstHydraTallyName(eventDetails);

        if (hydraTallyNameM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_TALLY")
                    .withDetail("Unrecognised tally, tally:" + "Hydra Tally Experiment")
                    .withStatus(NO_CONTENT)
                    .build()
            );
        }

        var tally = hydraTallyNameM.orElseThrow();
        var votingResultsE = chainFollowerClient.getVotingResultsPerCategory(
                eventDetails.id(),
                categoryDetails.id(),
                tally.name()
        );

        if (votingResultsE.isEmpty()) {
            var issue = votingResultsE.swap().get();

            if (issue.getStatus().getStatusCode() == 404) {
                return Either.right(Optional.empty());
            }

            return Either.left(Problem.builder()
                    .withTitle("ERROR_GETTING_VOTING_RESULTS")
                    .withDetail("Unable to get voting results from chain-tip follower service, event:" + event + ", category:" + category)
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build()
            );
        }

        var votingResults = votingResultsE.get();

        var byProposalsInCategoryStatsM = switch (eventDetails.votingEventType()) {
            case STAKE_BASED, BALANCE_BASED -> {
                var proposalResults = votingResults.results().entrySet().stream().collect(toMap(Map.Entry::getKey, e -> {
                    var score = e.getValue();

                    var b = Leaderboard.Votes.builder();
                    b.votingPower(String.valueOf(score));
                    b.votes(0); // TODO support for vote count from L1 data

                    return b.build();
                }));

                yield Optional.of(Leaderboard.ByProposalsInCategoryStats.builder()
                        .category(category)
                        .proposals(reInitialiseResultsToEmptyIfMissing(categoryDetails, proposalResults, eventDetails))
                        .build()
                );
            }
            case USER_BASED -> {
                var proposalResults = votingResults.results().entrySet().stream().collect(toMap(Map.Entry::getKey, e -> {
                    var score = e.getValue();

                    var b = Leaderboard.Votes.builder();
                    b.votes(score);

                    return b.build();
                }));

                yield Optional.of(Leaderboard.ByProposalsInCategoryStats.builder()
                        .category(category)
                        .proposals(reInitialiseResultsToEmptyIfMissing(categoryDetails, proposalResults, eventDetails))
                        .build()
                );
            }
        };

        return Either.right(byProposalsInCategoryStatsM);
    }

    @Override
    public Either<Problem, Optional<Leaderboard.ByProposalsInCategoryStats>> getCategoryLeaderboard(ChainFollowerClient.EventDetailsResponse eventDetails,
                                                                                                    ChainFollowerClient.CategoryDetailsResponse categoryDetails,
                                                                                                    boolean forceLeaderboard) {
        var categoryLeaderboardAvailableE = isCategoryLeaderboardAvailable(eventDetails, forceLeaderboard);
        if (categoryLeaderboardAvailableE.isEmpty()) {
            return Either.left(categoryLeaderboardAvailableE.getLeft());
        }

        var isCategoryLeaderBoardAvailable = categoryLeaderboardAvailableE.get();
        if (!isCategoryLeaderBoardAvailable) {
            return Either.left(Problem.builder()
                    .withTitle("VOTING_RESULTS_NOT_AVAILABLE")
                    .withDetail("Category level voting results not available until results can be revealed!")
                    .withStatus(FORBIDDEN)
                    .build()
            );
        }

        var hydraTallyNameM = findFirstHydraTallyName(eventDetails);

        if (hydraTallyNameM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_TALLY")
                    .withDetail("Unrecognised tally, tally:" + "Hydra Tally Experiment")
                    .withStatus(NO_CONTENT)
                    .build()
            );
        }

        var tally = hydraTallyNameM.orElseThrow();

        var votingResultsE = chainFollowerClient.getVotingResultsPerCategory(
                eventDetails.id(),
                categoryDetails.id(),
                tally.name()
        );

        if (votingResultsE.isEmpty()) {
            var issue = votingResultsE.swap().get();

            if (issue.getStatus().getStatusCode() == 404) {
                return Either.right(Optional.empty());
            }

            return Either.left(Problem.builder()
                    .withTitle("ERROR_GETTING_VOTING_RESULTS")
                    .withDetail("Unable to get voting results from chain-tip follower service, event:" + categoryDetails.id() + ", category:" + categoryDetails.id())
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build()
            );
        }

        var votingResults = votingResultsE.get();

        var byProposalsInCategoryStatsM = switch (eventDetails.votingEventType()) {
            case STAKE_BASED, BALANCE_BASED -> {
                var proposalResults = votingResults.results().entrySet().stream().collect(toMap(Map.Entry::getKey, e -> {
                    var score = e.getValue();

                    var b = Leaderboard.Votes.builder();
                    b.votingPower(String.valueOf(score));
                    b.votes(0); // TODO support for vote count from L1 data

                    return b.build();
                }));

                yield Optional.of(Leaderboard.ByProposalsInCategoryStats.builder()
                        .category(categoryDetails.id())
                        .proposals(reInitialiseResultsToEmptyIfMissing(categoryDetails, proposalResults, eventDetails))
                        .build()
                );
            }
            case USER_BASED -> {
                var proposalResults = votingResults.results().entrySet().stream().collect(toMap(Map.Entry::getKey, e -> {
                    var score = e.getValue();

                    var b = Leaderboard.Votes.builder();
                    b.votes(score);

                    return b.build();
                }));

                yield Optional.of(Leaderboard.ByProposalsInCategoryStats.builder()
                        .category(categoryDetails.id())
                        .proposals(reInitialiseResultsToEmptyIfMissing(categoryDetails, proposalResults, eventDetails))
                        .build()
                );
            }
        };

        return Either.right(byProposalsInCategoryStatsM);
    }

    @Override
    public Either<Problem, List<Leaderboard.ByProposalsInCategoryStats>> getCategoryLeaderboardForAllCategories(ChainFollowerClient.EventDetailsResponse eventDetails,
                                                                                                                boolean forceLeaderboard) {
        var categoryLeaderboardAvailableE = isCategoryLeaderboardAvailable(eventDetails, forceLeaderboard);
        if (categoryLeaderboardAvailableE.isEmpty()) {
            return Either.left(categoryLeaderboardAvailableE.getLeft());
        }

        var isCategoryLeaderBoardAvailable = categoryLeaderboardAvailableE.get();
        if (!isCategoryLeaderBoardAvailable) {
            return Either.left(Problem.builder()
                    .withTitle("VOTING_RESULTS_NOT_AVAILABLE")
                    .withDetail("Category level voting results not available until results can be revealed!")
                    .withStatus(FORBIDDEN)
                    .build()
            );
        }

        var hydraTallyNameM = findFirstHydraTallyName(eventDetails);

        if (hydraTallyNameM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_TALLY")
                    .withDetail("Unrecognised tally, tally:" + "Hydra Tally Experiment")
                    .withStatus(NO_CONTENT)
                    .build()
            );
        }

        var tally = hydraTallyNameM.orElseThrow();

        var votingResultsE = chainFollowerClient.getVotingResultsForAllCategories(
                eventDetails.id(),
                tally.name()
        );

        if (votingResultsE.isEmpty()) {
            var issue = votingResultsE.swap().get();

            if (issue.getStatus().getStatusCode() == 404) {
                return Either.right(List.of());
            }

            return Either.left(Problem.builder()
                    .withTitle("ERROR_GETTING_VOTING_RESULTS")
                    .withDetail("Unable to get voting results from chain-tip follower service, event:" + eventDetails.id())
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build()
            );
        }

        var votingResults = votingResultsE.get();
        var allResults = votingResults
                .stream()
                .map(l1CategoryResults -> {
                    return switch (eventDetails.votingEventType()) {
                        case STAKE_BASED, BALANCE_BASED -> {
                            var proposalResults = l1CategoryResults.results().entrySet().stream().collect(toMap(Map.Entry::getKey, e -> {
                                var score = e.getValue();

                                var b = Leaderboard.Votes.builder();
                                b.votingPower(String.valueOf(score));
                                b.votes(0); // TODO support for vote count from L1 data

                                return b.build();
                            }));

                            var category = eventDetails.categoryDetailsById(l1CategoryResults.categoryId()).orElseThrow();

                            yield Leaderboard.ByProposalsInCategoryStats.builder()
                                    .category(l1CategoryResults.categoryId())
                                    .proposals(reInitialiseResultsToEmptyIfMissing(category, proposalResults, eventDetails))
                                    .build();
                        }
                        case USER_BASED -> {
                            var proposalResults = l1CategoryResults.results().entrySet().stream().collect(toMap(Map.Entry::getKey, e -> {
                                var score = e.getValue();

                                var b = Leaderboard.Votes.builder();
                                b.votes(score);
                                b.votingPower("0");

                                return b.build();
                            }));

                            var category = eventDetails.categoryDetailsById(l1CategoryResults.categoryId()).orElseThrow();

                            yield Leaderboard.ByProposalsInCategoryStats.builder()
                                    .category(category.id())
                                    .proposals(reInitialiseResultsToEmptyIfMissing(category, proposalResults, eventDetails))
                                    .build();
                        }
                    };
                })
                .toList();

        return Either.right(allResults);
    }

    private Optional<ChainFollowerClient.Tally> findFirstHydraTallyName(ChainFollowerClient.EventDetailsResponse eventDetailsResponse) {
        return eventDetailsResponse.tallies().stream()
                .filter(tally -> tally.type() == HYDRA)
                .findFirst();
    }

    @Override
    public Either<Problem, Optional<Leaderboard.ByCandidatesInCategoryStats>> getCategoryLeaderboardCandidate(String event,
                                                                                                              String category,
                                                                                                              boolean forceLeaderboard) {
        var eventDetailsE = chainFollowerClient.getEventDetails(event);
        if (eventDetailsE.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("ERROR_GETTING_EVENT_DETAILS")
                    .withDetail("Unable to get event details from chain-tip follower service, event:" + event)
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build()
            );
        }
        var eventDetailsResponseM = eventDetailsE.get();
        if (eventDetailsResponseM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, event:" + event)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }
        var eventDetails = eventDetailsResponseM.orElseThrow();

        var categoryM = eventDetails.categoryDetailsById(category);
        if (categoryM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_CATEGORY")
                    .withDetail("Unrecognised category, category:" + category)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }
        var categoryDetails = categoryM.orElseThrow();

        var categoryLeaderboardAvailableE = isCategoryLeaderboardAvailable(eventDetails, forceLeaderboard);
        if (categoryLeaderboardAvailableE.isEmpty()) {
            return Either.left(categoryLeaderboardAvailableE.getLeft());
        }

        var isCategoryLeaderBoardAvailable = categoryLeaderboardAvailableE.get();
        if (!isCategoryLeaderBoardAvailable) {
            return Either.left(Problem.builder()
                    .withTitle("VOTING_RESULTS_NOT_AVAILABLE")
                    .withDetail("Category level voting results not available until results can be revealed!")
                    .withStatus(FORBIDDEN)
                    .build()
            );
        }

        var hydraTallyNameM = findFirstHydraTallyName(eventDetails);

        if (hydraTallyNameM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_TALLY")
                    .withDetail("Unrecognised tally, tally:" + "Hydra Tally Experiment")
                    .withStatus(NO_CONTENT)
                    .build()
            );
        }

        var tally = hydraTallyNameM.orElseThrow();
        var votingResultsE = chainFollowerClient.getVotingResultsPerCategory(
                eventDetails.id(),
                categoryDetails.id(),
                tally.name()
        );

        if (votingResultsE.isEmpty()) {
            var issue = votingResultsE.swap().get();

            if (issue.getStatus().getStatusCode() == 404) {
                return Either.right(Optional.empty());
            }

            return Either.left(Problem.builder()
                    .withTitle("ERROR_GETTING_VOTING_RESULTS")
                    .withDetail("Unable to get voting results from chain-tip follower service, event:" + event + ", category:" + category)
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build()
            );
        }

        var votingResults = votingResultsE.get();

        var byProposalsInCategoryStatsM = switch (eventDetails.votingEventType()) {
            case STAKE_BASED, BALANCE_BASED -> {
                var proposalResults = votingResults.results().entrySet().stream().collect(toMap(Map.Entry::getKey, e -> {
                    var score = e.getValue();

                    var b = Leaderboard.Votes.builder();
                    b.votingPower(String.valueOf(score));
                    b.votes(0); // TODO support for vote count from L1 data

                    return b.build();
                }));

                yield Optional.of(Leaderboard.ByCandidatesInCategoryStats.builder()
                        .category(category)
                        .candidates(reInitialiseResultsToEmptyIfMissing(categoryDetails, proposalResults, eventDetails))
                        .build()
                );
            }
            case USER_BASED -> {
                var proposalResults = votingResults.results().entrySet().stream().collect(toMap(Map.Entry::getKey, e -> {
                    var score = e.getValue();

                    var b = Leaderboard.Votes.builder();
                    b.votes(score);

                    return b.build();
                }));

                yield Optional.of(Leaderboard.ByCandidatesInCategoryStats.builder()
                        .category(category)
                        .candidates(reInitialiseResultsToEmptyIfMissing(categoryDetails, proposalResults, eventDetails))
                        .build()
                );
            }
        };

        return Either.right(byProposalsInCategoryStatsM);
    }

    @Override
    public Either<Problem, Optional<Leaderboard.ByCandidatesInCategoryStats>> getCategoryLeaderboardCandidate(ChainFollowerClient.EventDetailsResponse eventDetails,
                                                                                                              ChainFollowerClient.CategoryDetailsResponse categoryDetails,
                                                                                                              boolean forceLeaderboard) {
        var categoryLeaderboardAvailableE = isCategoryLeaderboardAvailable(eventDetails, forceLeaderboard);
        if (categoryLeaderboardAvailableE.isEmpty()) {
            return Either.left(categoryLeaderboardAvailableE.getLeft());
        }

        var isCategoryLeaderBoardAvailable = categoryLeaderboardAvailableE.get();
        if (!isCategoryLeaderBoardAvailable) {
            return Either.left(Problem.builder()
                    .withTitle("VOTING_RESULTS_NOT_AVAILABLE")
                    .withDetail("Category level voting results not available until results can be revealed!")
                    .withStatus(FORBIDDEN)
                    .build()
            );
        }

        var hydraTallyNameM = findFirstHydraTallyName(eventDetails);

        if (hydraTallyNameM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_TALLY")
                    .withDetail("Unrecognised tally, tally:" + "Hydra Tally Experiment")
                    .withStatus(NO_CONTENT)
                    .build()
            );
        }

        var tally = hydraTallyNameM.orElseThrow();

        var votingResultsE = chainFollowerClient.getVotingResultsPerCategory(
                eventDetails.id(),
                categoryDetails.id(),
                tally.name()
        );

        if (votingResultsE.isEmpty()) {
            var issue = votingResultsE.swap().get();

            if (issue.getStatus().getStatusCode() == 404) {
                return Either.right(Optional.empty());
            }

            return Either.left(Problem.builder()
                    .withTitle("ERROR_GETTING_VOTING_RESULTS")
                    .withDetail("Unable to get voting results from chain-tip follower service, event:" + categoryDetails.id() + ", category:" + categoryDetails.id())
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build()
            );
        }

        var votingResults = votingResultsE.get();

        var byProposalsInCategoryStatsM = switch (eventDetails.votingEventType()) {
            case STAKE_BASED, BALANCE_BASED -> {
                var proposalResults = votingResults.results().entrySet().stream().collect(toMap(Map.Entry::getKey, e -> {
                    var score = e.getValue();

                    var b = Leaderboard.Votes.builder();
                    b.votingPower(String.valueOf(score));
                    b.votes(0); // TODO support for vote count from L1 data

                    return b.build();
                }));

                yield Optional.of(Leaderboard.ByCandidatesInCategoryStats.builder()
                        .category(categoryDetails.id())
                        .candidates(reInitialiseResultsToEmptyIfMissing(categoryDetails, proposalResults, eventDetails))
                        .build()
                );
            }
            case USER_BASED -> {
                var proposalResults = votingResults.results().entrySet().stream().collect(toMap(Map.Entry::getKey, e -> {
                    var score = e.getValue();

                    var b = Leaderboard.Votes.builder();
                    b.votes(score);

                    return b.build();
                }));

                yield Optional.of(Leaderboard.ByCandidatesInCategoryStats.builder()
                        .category(categoryDetails.id())
                        .candidates(reInitialiseResultsToEmptyIfMissing(categoryDetails, proposalResults, eventDetails))
                        .build()
                );
            }
        };

        return Either.right(byProposalsInCategoryStatsM);
    }
}