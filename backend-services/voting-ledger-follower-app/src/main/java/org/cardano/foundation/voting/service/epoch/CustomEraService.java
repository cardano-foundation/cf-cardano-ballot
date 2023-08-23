package org.cardano.foundation.voting.service.epoch;

import com.bloxbean.cardano.yaci.core.model.Era;
import com.bloxbean.cardano.yaci.store.core.configuration.GenesisConfig;
import com.bloxbean.cardano.yaci.store.core.service.EraService;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.EraData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class CustomEraService {

    @Autowired
    private GenesisConfig genesisConfig;

    @Autowired
    private EraService eraService;

    public Optional<EraData> getEraData(Era era, CardanoNetwork cardanoNetwork) {
        return getStartAbsoluteSlot(era, cardanoNetwork)
                .flatMap(startAbsoluteSlot -> getEndAbsoluteSlot(era, cardanoNetwork)
                    .flatMap(endAbsoluteSlot -> getStartBlock(era, cardanoNetwork)
                    .flatMap(startBlock -> getEndBlock(era, cardanoNetwork)
                        .flatMap(endBlock -> getStartEpochTime(era, cardanoNetwork)
                                .flatMap(startEpochTime -> getEndEpochTime(era, cardanoNetwork)
                                        .flatMap(endEpochTime -> getStartEpochNumber(era, cardanoNetwork)
                                                .flatMap(startEpochNumber -> getEndEpochNumber(era, cardanoNetwork)
                                                        .map(endEpochNumber -> new EraData(
                                                                startAbsoluteSlot,
                                                                endAbsoluteSlot,
                                                                startBlock,
                                                                endBlock,
                                                                startEpochTime,
                                                                endEpochTime,
                                                                startEpochNumber,
                                                                endEpochNumber,
                                                                previousEra(era),
                                                                nextEra(era)
                                                        ))
                                                )
                                        ))))));
    }

    public Optional<Long> getEndEpochTime(Era era, CardanoNetwork cardanoNetwork) {
        return getEndAbsoluteSlot(era, cardanoNetwork).map(absoluteSlot -> eraService.blockTime(era, absoluteSlot));
    }

    public Optional<Long> getStartEpochTime(Era era, CardanoNetwork cardanoNetwork) {
        return getStartAbsoluteSlot(era, cardanoNetwork).map(absoluteSlot -> eraService.blockTime(era, absoluteSlot));
    }

    public Optional<Integer> getEndEpochNumber(Era era, CardanoNetwork network) {
        return switch (network) {
            case MAIN, PREPROD -> switch (era)  {
                case Byron, Shelley, Allegra, Mary, Alonzo -> getEndAbsoluteSlot(era, network)
                        .map(slot -> (int) (slot / genesisConfig.slotsPerEpoch(era)));
                case Babbage -> Optional.empty();
            };
            case PREVIEW, DEV -> Optional.empty();
        };
    }

    // TODO
    public Optional<Integer> getStartEpochNumber(Era era, CardanoNetwork network) {
        return Optional.of(-1);
    }

    public Optional<Integer> getEndBlock(Era era, CardanoNetwork network) {
        return switch (network) {
            case MAIN -> switch (era)  {
                case Byron -> Optional.of(4490510);
                case Shelley -> Optional.of(5086523);
                case Allegra -> Optional.of(5406746);
                case Mary -> Optional.of(6236059);
                case Alonzo -> Optional.of(7791698);
                case Babbage -> Optional.empty();
            };
            case PREPROD -> switch (era)  {
                case Byron -> Optional.of(45);
                case Shelley -> Optional.of(21644);
                case Allegra -> Optional.of(43242);
                case Mary -> Optional.of(64902);
                case Alonzo -> Optional.of(172497);
                case Babbage -> Optional.empty();
            };
            case PREVIEW, DEV -> Optional.empty();
        };
    }

    public Optional<Long> getEndAbsoluteSlot(Era era, CardanoNetwork network) {
        return switch (network) {
            case MAIN -> switch (era)  {
                case Byron -> Optional.of(4492799L);
                case Shelley -> Optional.of(16588737L);
                case Allegra -> Optional.of(23068793L);
                case Mary -> Optional.of(39916796L);
                case Alonzo -> Optional.of(72316796L);
                case Babbage -> Optional.empty();
            };
            case PREPROD -> switch (era)  {
                case Byron -> Optional.of(84242L);
                case Shelley -> Optional.of(518360L);
                case Allegra -> Optional.of(950340L);
                case Mary -> Optional.of(1382348L);
                case Alonzo -> Optional.of(3542390L);
                case Babbage -> Optional.empty();
            };
            case PREVIEW, DEV -> Optional.empty();
        };
    }

    public Optional<Integer> getStartBlock(Era era, CardanoNetwork network) {
        return switch (network) {
            case MAIN, PREPROD -> switch (era)  {
                case Byron -> Optional.of(0);
                case Shelley, Allegra, Mary, Alonzo, Babbage -> {
                    Era previousEra = previousEra(era).orElseThrow();

                    yield getEndBlock(previousEra, network)
                            .map(lastBlock -> lastBlock + 1);
                }
            };
            case PREVIEW, DEV -> Optional.empty();
        };
    }

    public Optional<Long> getStartAbsoluteSlot(Era era, CardanoNetwork network) {
        return switch (network) {
            case MAIN, PREPROD -> switch (era)  {
                case Byron -> Optional.of(0L);
                case Shelley, Allegra, Mary, Alonzo, Babbage -> {
                    Era previousEra = previousEra(era).orElseThrow();

                    yield getEndAbsoluteSlot(previousEra, network)
                            .map(lastBlock -> lastBlock + 1);
                }
            };
            case PREVIEW, DEV -> Optional.empty();
        };
    }

    public Optional<Era> previousEra(Era era) {
        return switch (era) {
            case Byron -> Optional.empty();
            case Shelley, Allegra, Mary, Alonzo, Babbage -> Arrays.stream(Era.values())
                    .filter(e -> e.value == era.value - 1)
                    .findFirst();
        };
    }

    public Optional<Era> nextEra(Era era) {
        return switch (era) {
            case Byron -> Optional.empty();
            case Shelley, Allegra, Mary, Alonzo, Babbage -> Arrays.stream(Era.values())
                    .filter(e -> e.value == era.value + 1)
                    .findFirst();
        };
    }
}
