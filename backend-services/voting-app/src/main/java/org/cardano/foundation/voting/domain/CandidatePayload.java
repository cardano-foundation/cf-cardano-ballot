package org.cardano.foundation.voting.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CandidatePayload {
    private CandidatePayloadData data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CandidatePayloadData {
        private List<Long> votes;
        private Long votingPower;
    }
}
