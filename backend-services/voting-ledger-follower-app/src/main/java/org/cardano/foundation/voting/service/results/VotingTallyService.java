package org.cardano.foundation.voting.service.results;

import com.bloxbean.cardano.client.common.model.Network;
import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.CategoryResultsDatumConverter;
import org.cardano.foundation.voting.domain.TallyResults;
import org.cardano.foundation.voting.service.plutus.PlutusScriptLoader;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardano.foundation.voting.service.utxo.EventResultsUtxoDataService;
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

    private final EventResultsUtxoDataService eventResultsUtxoDataService;

    private final PlutusScriptLoader plutusScriptLoader;

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
        var plutusScriptM = plutusScriptLoader.compileScriptBy(eventDetails, categoryId, tallyName);

        if (plutusScriptM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_TALLY")
                    .withDetail("Unrecognised tally, tally:" + tallyName)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var plutusScript = plutusScriptM.orElseThrow();

        var contractAddress = plutusScriptLoader.getContractAddress(plutusScript, network);

        var eventResultsUtxoDataServiceAllResults = eventResultsUtxoDataService.findAllResults(contractAddress);

        if (eventResultsUtxoDataServiceAllResults.isEmpty()) {
            return Either.right(Optional.empty());
        }

        var foundValidEventResultsUtxoM = eventResultsUtxoDataServiceAllResults.stream()
                .filter(resultsUtxo -> {
                    return resultsUtxo.getWitnessHashesesAsList().stream().anyMatch(witness -> {
                        var verificationKeyHashes = tally.getHydraTallyConfig().getVerificationKeysAsList();

                        return verificationKeyHashes.stream().anyMatch(witness::contains);
                    });
                })
                .findFirst();

        if (foundValidEventResultsUtxoM.isEmpty()) {
            return Either.right(Optional.empty());
        }

        var resultsUtxo = foundValidEventResultsUtxoM.orElseThrow();

        var categoryResultsDatum = categoryResultsDatumConverter.deserialize(resultsUtxo.getInlineDatum());

        var tallyResults = TallyResults.builder()
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

        return Either.right(Optional.of(tallyResults));
    }

}
