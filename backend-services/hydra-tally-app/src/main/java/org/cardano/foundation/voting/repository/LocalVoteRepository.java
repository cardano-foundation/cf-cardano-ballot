package org.cardano.foundation.voting.repository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.cardano.foundation.voting.domain.Vote;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class LocalVoteRepository implements VoteRepository {

    private final ResourceLoader resourceLoader;

    private final String votesPath;

    @Override
    @SneakyThrows
    public List<Vote> findAllVotes(String eventId) {
        var r = resourceLoader.getResource(votesPath);

        var votes = new ArrayList<Vote>();

        var format = CSVFormat.POSTGRESQL_CSV.builder()
                .setSkipHeaderRecord(true)
                .setHeader(
                        "id",
                        "id_numeric_hash",
                        "event_id",
                        "category_id",
                        "proposal_id",
                        "voter_stake_address",
                        "cose_signature",
                        "cose_public_key",
                        "voting_power",
                        "voted_at_slot",
                        "created_at",
                        "updated_at",
                        "organiser"
                        )
                .build();

        var votesParsed = format.parse(new InputStreamReader(r.getInputStream()));

        for (var vote : votesParsed) {
            var voteId = vote.get("id");
            var voteEventId = vote.get("event_id");
            var coseSignature = vote.get("cose_signature");
            var cosePublicKey = vote.get("cose_public_key");
            var categoryId = vote.get("category_id");
            var proposalId = vote.get("proposal_id");
            var voterStakeAddress = vote.get("voter_stake_address");
            var votingPower = Optional.ofNullable(vote.get("voting_power"));
            var organiser = vote.get("organiser");

            if (!voteEventId.equals(eventId)) {
                continue;
            }

            var voteE = Vote.create(
                    voteId,
                    voteEventId,
                    organiser,
                    categoryId,
                    proposalId,
                    voterStakeAddress,
                    votingPower,
                    coseSignature,
                    Optional.ofNullable(cosePublicKey)
            );

            if (voteE.isEmpty()) {
                log.error("Vote creation failed, reason:{}", voteE.getLeft());
            }

            votes.add(voteE.get());
        }

        return votes;
    }

    @Override
    public List<Vote> findAllVotes(String eventId, String categoryId) {
        return findAllVotes(eventId)
                .parallelStream()
                .filter(v -> {
                    return v.categoryId().equals(categoryId);
                })
                .toList();
    }

    @Override
    public Set<String> getAllUniqueCategories(String eventId) {
        return findAllVotes(eventId)
                .parallelStream()
                .map(Vote::categoryId)
                .collect(Collectors.toSet());
    }

}
