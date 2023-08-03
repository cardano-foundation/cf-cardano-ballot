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

    public ZonedDateTime getEpochStartTimeBasedOnEpochNo(int epochNo) {
        var epochLength = genesisConfig.getEpochLength();

        var shelleyStartTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(eraService.shelleyEraStartTime()), ZoneId.of("UTC"));

//        log.info("ShelleyStartTime:" + shelleyStartTime.toEpochSecond());
//
//        if (network.getNetworkType().isEmpty()) {
//            return ZonedDateTime.now();
//        }
//
//        var networkStartTime = genesisConfig.getStartTime(network.getNetworkType().orElseThrow().getProtocolMagic());
//
//        long shelleyTime = shelleyStartTime.toEpochSecond() - networkStartTime;
//
//        var slotsCount = shelleyTime / epochLength;
//
//        log.info("slotsCount:" + slotsCount);

        return switch (network) {
            case MAIN, PREPROD, PREVIEW:
                yield ZonedDateTime.ofInstant(Instant.ofEpochSecond(((epochNo * epochLength) + shelleyStartTime.toEpochSecond())), ZoneId.of("UTC"));
            case DEV:
                yield ZonedDateTime.now(clock);
        };
    }

    public ZonedDateTime getEpochEndTime(int epochNo) {
        return getEpochStartTimeBasedOnEpochNo(epochNo + 1).minusSeconds(1); // lol at minus 1 second
    }

    public ZonedDateTime getEpochStartTimeBasedOnAbsoluteSlot(long absoluteSlotNo) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(eraService.blockTime(Era.Shelley, absoluteSlotNo)), ZoneId.of("UTC"));
    }

    public ZonedDateTime getEpochEndTimeBasedOnAbsoluteSlot(long absoluteSlotNo) {
        return getEpochStartTimeBasedOnAbsoluteSlot(absoluteSlotNo + 1).minusSeconds(1); // lol at minus 1 second
    }

}
