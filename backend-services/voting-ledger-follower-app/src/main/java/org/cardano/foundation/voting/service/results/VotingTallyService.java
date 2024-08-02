package org.cardano.foundation.voting.service.results;

import com.bloxbean.cardano.client.common.model.Network;
import io.micrometer.core.annotation.Timed;
import io.vavr.Value;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.CategoryResultsDatum;
import org.cardano.foundation.voting.domain.CategoryResultsDatumConverter;
import org.cardano.foundation.voting.domain.TallyResults;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.service.plutus.PlutusScriptLoader;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardano.foundation.voting.service.utxo.EventResultsUtxoDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.entity.Tally.TallyType.HYDRA;
import static org.cardano.foundation.voting.utils.MoreEither.findFirstError;
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

    @Timed(value = "service.vote_results.getVoteResultsForAllCategories", histogram = true)
    @Transactional(readOnly = true)
    public Either<Problem, List<TallyResults>> getVoteResultsForAllCategories(String eventId,
                                                                              String tallyName) {
        val eventDetailsM = referenceDataService
                .findValidEventByName(eventId);

        if (eventDetailsM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, event:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var eventDetails = eventDetailsM.orElseThrow();

        var allResultsList = eventDetails.getCategories()
                .stream()
                .map(cat -> getVoteResultsPerCategory(eventDetails, cat.getId(), tallyName))
                .toList();

        var errorM = findFirstError(allResultsList);

        if (errorM.isPresent()) {
            return Either.left(errorM.get().getLeft());
        }

        var allResults = allResultsList.stream()
                .filter(Either::isRight)
                .map(Value::getOrNull)
                .filter(Objects::nonNull)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        return Either.right(allResults);

    }
    @Timed(value = "service.vote_results.getVoteResultsForACategory", histogram = true)
    @Transactional(readOnly = true)
    public Either<Problem, Optional<TallyResults>> getVoteResultsPerCategory(String eventId,
                                                                                  String categoryId,
                                                                                  String tallyName) {
        val eventDetailsM = referenceDataService
                .findValidEventByName(eventId);

        if (eventDetailsM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, event:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var event = eventDetailsM.orElseThrow();

        return getVoteResultsPerCategory(event, categoryId, tallyName);
    }

    @Transactional(readOnly = true)
    public Either<Problem, Optional<TallyResults>> getVoteResultsPerCategory(Event eventDetails,
                                                                             String categoryId,
                                                                             String tallyName) {
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

        var eventValidVerificationKeyHashes = tally.getHydraTallyConfig().getVerificationKeysHashesAsList();

        var foundValidEventResultsUtxoM = eventResultsUtxoDataServiceAllResults.stream()
                .filter(resultsUtxo -> resultsUtxo.getWitnessesHashes().stream().anyMatch(eventValidVerificationKeyHashes::contains))
                .findFirst();

        if (foundValidEventResultsUtxoM.isEmpty()) {
            return Either.right(Optional.empty());
        }

        var resultsUtxo = foundValidEventResultsUtxoM.orElseThrow();

        var categoryResultsDatumM = parseCategoryResultsDatum(resultsUtxo.getInlineDatum());

        if (categoryResultsDatumM.isEmpty()) {
            return Either.right(Optional.empty());
        }

        var categoryResultsDatum = categoryResultsDatumM.orElseThrow();

        var tallyResults = TallyResults.builder()
                .tallyName(tallyName)
                .tallyDescription(tally.getDescription())
                .tallyType(tally.getType())
                .eventId(categoryResultsDatum.getEventId())
                .categoryId(categoryResultsDatum.getCategoryId())
                .results(categoryResultsDatum.getResults())
                .metadata(Map.of(
                        "contractAddress", contractAddress,
                        "categoryResultsDatum", resultsUtxo.getInlineDatum(),
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

    private Optional<CategoryResultsDatum> parseCategoryResultsDatum(String inlineDatum) {
        try {
            return Optional.of(categoryResultsDatumConverter.deserialize(inlineDatum));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
