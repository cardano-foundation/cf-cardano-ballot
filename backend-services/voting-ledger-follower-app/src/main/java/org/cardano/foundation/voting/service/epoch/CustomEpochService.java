package org.cardano.foundation.voting.service.epoch;

import com.bloxbean.cardano.yaci.core.model.Era;
import com.bloxbean.cardano.yaci.store.core.StoreProperties;
import com.bloxbean.cardano.yaci.store.core.configuration.GenesisConfig;
import com.bloxbean.cardano.yaci.store.core.domain.CardanoEra;
import com.bloxbean.cardano.yaci.store.core.service.EraService;
import com.bloxbean.cardano.yaci.store.core.storage.api.EraStorage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static com.bloxbean.cardano.yaci.core.model.Era.Byron;
import static com.bloxbean.cardano.yaci.core.model.Era.Shelley;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomEpochService {

    private final CardanoNetwork network;

    private final GenesisConfig genesisConfig;

    private final EraService eraService;

    private final EraStorage eraStorage;

    private final CustomEraService customEraService;

    private final StoreProperties storeProperties;

    private long firstShelleySlot;

    private final static ZoneId UTC = ZoneId.of("UTC");

    @PostConstruct
    public void init() {
        firstShelleySlot = eraStorage.findFirstNonByronEra()
                .map(CardanoEra::getStartSlot)
                .orElse(0L);
    }

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
        var shelleySlotDuration = (long) this.genesisConfig.slotDuration(Shelley);
        var shelleySlotsPerEpoch = this.genesisConfig.slotsPerEpoch(Shelley);

        var diffEpochCount = epochNo - byronEraData.endEpochNo() - 1;

        var postShelleyDuration = diffEpochCount * shelleySlotsPerEpoch * shelleySlotDuration;

        var totalTimeInstant = Instant.ofEpochSecond(shelleyStartTime).plusSeconds(postShelleyDuration);

        return Optional.of(ZonedDateTime.ofInstant(totalTimeInstant, UTC));
    }

    public long blockTime(Era era, long slot) {
        if (era == Byron) {
            return byronBlockTime(slot);
        }

        long slotsFromShelleyStart = slot - firstShelleySlot;

        return (eraService.shelleyEraStartTime() + slotsFromShelleyStart * (long) genesisConfig.slotDuration(Shelley));
    }

    private long byronBlockTime(long slot) {
        long startTime = genesisConfig.getStartTime(storeProperties.getProtocolMagic());

        return startTime + slot * (long) genesisConfig.slotDuration(Byron);
    }

    public Optional<ZonedDateTime> getEpochEndTime(int epochNo) {
        return getEpochStartTimeBasedOnEpochNo(epochNo + 1).map(time -> time.minusSeconds(1));
    }

    public Optional<ZonedDateTime> getTimeBasedOnAbsoluteSlot(long absoluteSlotNo) {
        var millis = blockTime(Shelley, absoluteSlotNo) * 1000;

        var t = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), UTC);

        return Optional.of(t.atZone(UTC));
    }

}
