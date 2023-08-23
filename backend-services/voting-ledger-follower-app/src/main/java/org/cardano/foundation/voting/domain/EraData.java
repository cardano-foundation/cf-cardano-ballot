package org.cardano.foundation.voting.domain;

import com.bloxbean.cardano.yaci.core.model.Era;

import java.util.Optional;

public record EraData(
        long startAbsoluteSlot,
        long endAbsoluteSlot,
        int epochStartBlock,
        int epochEndBlock,
        long epochStartTime,
        long epochEndTime,
        int startEpochNo,
        int endEpochNo,
        Optional<Era> previousEra,
        Optional<Era> nextEra
)
{
}
