package org.cardano.foundation.voting.service.leader_board;

import io.vavr.control.Either;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.Leaderboard;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.Optional;

public interface LeaderboardWinnersService {

    Either<Problem, Boolean> isCategoryLeaderboardAvailable(String event,
                                                            String category,
                                                            boolean forceLeaderboard);

    Either<Problem, Boolean> isCategoryLeaderboardAvailable(String event,
                                                            boolean forceLeaderboard);

    Either<Problem, Optional<Leaderboard.ByProposalsInCategoryStats>> getCategoryLeaderboard(String event,
                                                                                             String category,
                                                                                             boolean forceLeaderboard);

    Either<Problem, List<Leaderboard.ByProposalsInCategoryStats>> getAllCategoriesLeaderboard(String event,
                                                                                              boolean forceLeaderboard);

    Either<Problem, Optional<Leaderboard.ByProposalsInCategoryStats>> getCategoryLeaderboard(ChainFollowerClient.EventDetailsResponse eventDetails,
                                                                                             ChainFollowerClient.CategoryDetailsResponse category,
                                                                                             boolean forceLeaderboard);

    Either<Problem, List<Leaderboard.ByProposalsInCategoryStats>> getCategoryLeaderboardForAllCategories(ChainFollowerClient.EventDetailsResponse eventDetails,
                                                                                                         boolean forceLeaderboard);

    Either<Problem, Optional<Leaderboard.ByCandidatesInCategoryStats>> getCategoryLeaderboardCandidate(String event,
                                                                                                       String category,
                                                                                                       boolean forceLeaderboard);

    Either<Problem, Optional<Leaderboard.ByCandidatesInCategoryStats>> getCategoryLeaderboardCandidate(ChainFollowerClient.EventDetailsResponse eventDetails,
                                                                                                       ChainFollowerClient.CategoryDetailsResponse category,
                                                                                                       boolean forceLeaderboard);
}
