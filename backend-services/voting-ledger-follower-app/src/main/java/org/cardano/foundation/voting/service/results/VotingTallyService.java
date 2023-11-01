package org.cardano.foundation.voting.service.results;

import com.bloxbean.cardano.client.common.model.Network;
import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.CategoryResultsDatum;
import org.cardano.foundation.voting.domain.CategoryResultsDatumConverter;
import org.cardano.foundation.voting.domain.TallyResults;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataUtxoStateReader;
import org.cardano.foundation.voting.service.plutus.PlutusScriptLoader;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.Map;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.entity.Tally.TallyType.HYDRA;
import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@AllArgsConstructor
@Slf4j
public class VotingTallyService {

    private final ReferenceDataService referenceDataService;

    private final CategoryResultsDatumConverter categoryResultsDatumConverter;

    private final BlockchainDataUtxoStateReader blockchainDataUtxoStateReader;

    private final Network network;

    @Timed(value = "service.vote_results.getVoteResults", histogram = true)
    public Either<Problem, Optional<TallyResults>> getVoteResults(String eventId,
                                                                  String categoryId,
                                                                  String tallyName) {
        val eventAdditionalInfoM = referenceDataService
                .findValidEventByName(eventId);

        if (eventAdditionalInfoM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, event:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var eventDetails = eventAdditionalInfoM.orElseThrow();

        var isValidCategory = eventDetails.getCategories()
                .stream()
                .anyMatch(category -> category.getId().equals(categoryId));

        if (!isValidCategory) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_CATEGORY")
                    .withDetail("Unrecognised category, category:" + categoryId)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var tallyM = eventDetails.getTallies()
                .stream()
                .filter(tally -> tally.getName().equals(tallyName))
                .findFirst();

        if (tallyM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_TALLY")
                    .withDetail("Unrecognised tally, tally:" + tallyName)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var tally = tallyM.orElseThrow();

        if (tally.getType() != HYDRA) {
            return Either.left(Problem.builder()
                    .withTitle("TALLY_TYPE_NOT_SUPPORTED")
                    .withDetail("Tally type not supported, tally:" + tally)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var hydraTally = tally.getHydraTallyConfig();

        var verificationKeys = hydraTally.getVerificationKeysAsList();
        var compiledContractTemplate = hydraTally.getCompiledScript();

        var scriptLoader = PlutusScriptLoader.builder()
                .eventId(eventDetails.getId())
                .categoryId(categoryId)
                .organiser(eventDetails.getOrganisers())
                .verificationKeys(verificationKeys)
                .tallyName(tallyName)
                .parametrisedCompiledTemplate(compiledContractTemplate)
                .build();

        var compiledScript = scriptLoader.getCompiledScript();

        var contractAddress = scriptLoader.getContractAddress(compiledScript, network);

        var uTxOsE = blockchainDataUtxoStateReader.getUTxOs(contractAddress, verificationKeys);

        if (uTxOsE.isEmpty()) {
            return Either.left(uTxOsE.getLeft());
        }

        var uTxOs = uTxOsE.get();

        if (uTxOs.isEmpty()) {
            return Either.right(Optional.empty());
        }

        var categoryResultsDatumM = uTxOs.stream()
                .filter(utxo -> utxo.getInlineDatum().isPresent())
                .map(utxo -> utxo.getInlineDatum().get())
                .map(inlineDatum -> {
                    try {
                        return Optional.of(categoryResultsDatumConverter.deserialize(inlineDatum));
                    } catch (Exception e) {
                        log.warn("Unable to deserialize inline datum to CategoryResultsDatum", e);

                        return Optional.<CategoryResultsDatum>empty();
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        return Either.right(categoryResultsDatumM.map(categoryResultsDatum -> {
            return TallyResults.builder()
                    .tallyName(tallyName)
                    .tallyDescription(tally.getDescription())
                    .tallyType(tally.getType())
                    .eventId(categoryResultsDatum.getEventId())
                    .categoryId(categoryResultsDatum.getCategoryId())
                    .results(categoryResultsDatum.getResults())
                    .metadata(Map.of(
                            "contractAddress", contractAddress,
                            "contractName", hydraTally.getContractName(),
                            "contractVersion", hydraTally.getContractVersion(),
                            "contractHash", hydraTally.getCompiledScriptHash(),
                            "compilerName", hydraTally.getCompilerName(),
                            "compilerVersion", hydraTally.getCompilerVersion(),
                            "plutusVersion", hydraTally.getPlutusVersion())
                    )
                    .build();
        }));
    }

}
