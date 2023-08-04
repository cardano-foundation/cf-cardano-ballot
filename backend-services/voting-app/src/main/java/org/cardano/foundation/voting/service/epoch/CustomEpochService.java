package org.cardano.foundation.voting.service.epoch;

import com.bloxbean.cardano.yaci.core.model.Era;
import com.bloxbean.cardano.yaci.store.core.configuration.GenesisConfig;
import com.bloxbean.cardano.yaci.store.core.service.EraService;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static com.bloxbean.cardano.yaci.core.model.Era.Byron;

@Service
@Slf4j
public class CustomEpochService {

    @Autowired
    private CardanoNetwork network;

    @Autowired
    private Clock clock;

    @Autowired
    private GenesisConfig genesisConfig;

    @Autowired
    private EraService eraService;

    @Autowired
    private CustomEraService customEraService;

    public Optional<ZonedDateTime> getEpochStartTimeBasedOnEpochNo(int epochNo) {
        var maybeByronEraData = customEraService.getEraData(Byron, network);

        if (maybeByronEraData.isEmpty()) {
            return Optional.empty();
        }
        var byronEraData = maybeByronEraData.orElseThrow();

        if (epochNo <= byronEraData.endEpochNo()) {
            return Optional.empty();
        }

        var shelleyStartTime = this.eraService.shelleyEraStartTime();
        var shelleySlotDuration = (long) this.genesisConfig.slotDuration(Era.Shelley);
        var shelleySlotsPerEpoch = this.genesisConfig.slotsPerEpoch(Era.Shelley);

        var diffEpochCount = epochNo - byronEraData.endEpochNo() - 1;

        var postShelleyDuration = diffEpochCount * shelleySlotsPerEpoch * shelleySlotDuration;

        var totalTimeInstant = Instant.ofEpochSecond(shelleyStartTime).plusSeconds(postShelleyDuration);

        return Optional.of(ZonedDateTime.ofInstant(totalTimeInstant, ZoneId.of("UTC")));
    }

    public Optional<ZonedDateTime> getEpochEndTime(int epochNo) {
        return getEpochStartTimeBasedOnEpochNo(epochNo + 1).map(time -> time.minusSeconds(1));
    }

    // TODO CF Summit 2023
    public Optional<ZonedDateTime> getEpochStartTimeBasedOnAbsoluteSlot(long absoluteSlotNo) {
        return Optional.empty();
    }

    // TODO CF Summit 2023
    public Optional<ZonedDateTime> getEpochEndTimeBasedOnAbsoluteSlot(long absoluteSlotNo) {
        return Optional.empty();
    }

}
