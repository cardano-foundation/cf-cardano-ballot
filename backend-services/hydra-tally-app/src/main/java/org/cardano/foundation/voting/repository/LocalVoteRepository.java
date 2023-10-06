package org.cardano.foundation.voting.repository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.cardano.foundation.voting.domain.CompactVote;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class LocalVoteRepository implements VoteRepository {

    private final String votesPath;

    @Override
    @SneakyThrows
    public List<CompactVote> findAllVotes(String eventId) {
        var reader = new FileReader(votesPath);

        var votes = new ArrayList<CompactVote>();

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
                        "updated_at"
                )
                .build();

        var votesParsed = format.parse(reader);

        for (var vote : votesParsed) {
            var voteId = vote.get("id");
            var voteEventId = vote.get("event_id");
            var coseSignature = vote.get("cose_signature");
            var cosePublicKey = vote.get("cose_public_key");

            if (!voteEventId.equals(eventId)) {
                continue;
            }

            var compactVote = new CompactVote(voteId, voteEventId, coseSignature, Optional.ofNullable(cosePublicKey));

            votes.add(compactVote);
        }

        return votes;
    }

}
