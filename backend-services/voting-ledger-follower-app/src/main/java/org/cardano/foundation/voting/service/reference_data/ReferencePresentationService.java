package org.cardano.foundation.voting.service.reference_data;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.EventAdditionalInfo;
import org.cardano.foundation.voting.domain.HydraTally;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.presentation.*;
import org.cardano.foundation.voting.service.epoch.CustomEpochService;
import org.cardano.foundation.voting.service.expire.EventAdditionalInfoService;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.Optional;

import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReferencePresentationService {

    private final ReferenceDataService referenceDataService;

    private final EventAdditionalInfoService eventAdditionalInfoService;

    private final CustomEpochService customEpochService;

    public Either<Problem, Optional<EventPresentation>> findEventReference(String name) {
        var maybeValidEventByName = referenceDataService.findValidEventByName(name);

        if (maybeValidEventByName.isEmpty()) {
            return Either.right(Optional.empty());
        }

        var event = maybeValidEventByName.get();
        var categories = event.getCategories().stream().map(category -> {
                    var proposals = category.getProposals().stream().map(proposal -> ProposalPresentation.builder()
                                    .id(proposal.getId())
                                    .name(proposal.getName())
                                    .build())
                            .toList();

                    return CategoryPresentation.builder()
                            .id(category.getId())
                            .gdprProtection(category.isGdprProtection())
                            .proposals(proposals)
                            .build();
                }
        ).toList();

        var eventAdditionalInfoE = eventAdditionalInfoService.getEventAdditionalInfo(event);
        if (eventAdditionalInfoE.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("REFERENCE_ERROR")
                    .withDetail("Unable to get expiration data.")
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build());
        }
        var eventAdditionalInfo = eventAdditionalInfoE.get();

        var tallyPresentations = event.getTallies().stream().map(
                tally -> {
                    var hydraTallyPresentationConfigM = switch (tally.getType()) {
                        case HYDRA: {
                            var hydraTally = (HydraTally) tally.getHydraTallyConfig();

                            yield Optional.of(HydraTallyConfigPresentation.builder()
                                    .contractName(hydraTally.getContractName())
                                    .compiledScript(hydraTally.getCompiledScript())
                                    .compiledScriptHash(hydraTally.getCompiledScriptHash())
                                    .contractVersion(hydraTally.getContractVersion())
                                    .verificationKeys(hydraTally.getVerificationKeysHashesAsList())
                                    .compilerName(hydraTally.getCompilerName())
                                    .plutusVersion(hydraTally.getPlutusVersion())
                                    .compilerVersion(hydraTally.getCompilerVersion())
                                    .build());
                        }
                    };

                    var tallyPresentationBuilder = TallyPresentation.builder()
                            .name(tally.getName())
                            .type(tally.getType())
                            .description(tally.getDescription());

                    tallyPresentationBuilder.config(hydraTallyPresentationConfigM);

                    return tallyPresentationBuilder.build();
                }
        ).toList();

        var eventBuilder = EventPresentation.builder()
                .id(event.getId())
                .organisers(event.getOrganisers())
                .votingEventType(event.getVotingEventType())
                .startEpoch(event.getStartEpoch())
                .endEpoch(event.getEndEpoch())
                .startSlot(event.getStartSlot())
                .endSlot(event.getEndSlot())
                .snapshotEpoch(event.getSnapshotEpoch())
                .categories(categories)
                .isNotStarted(eventAdditionalInfo.notStarted())
                .isStarted(eventAdditionalInfo.started())
                .isActive(eventAdditionalInfo.active())
                .isFinished(eventAdditionalInfo.finished())
                .isProposalsReveal(eventAdditionalInfo.proposalsReveal())
                .isCommitmentsWindowOpen(eventAdditionalInfo.commitmentsWindowOpen())
                .isAllowVoteChanging(event.isAllowVoteChanging())
                .isHighLevelEventResultsWhileVoting(event.getHighLevelEventResultsWhileVoting())
                .isHighLevelCategoryResultsWhileVoting(event.getHighLevelEventResultsWhileVoting())
                .isCategoryResultsWhileVoting(event.getCategoryResultsWhileVoting())
                .tallies(tallyPresentations)
                ;

        switch (event.getVotingEventType()) {
            case STAKE_BASED, BALANCE_BASED -> {
                eventBuilder.eventStartDate(customEpochService.getEpochStartTime(event.getStartEpoch().orElseThrow()));
                eventBuilder.eventEndDate(customEpochService.getEpochEndTime(event.getEndEpoch().orElseThrow()));
                eventBuilder.snapshotTime(customEpochService.getEpochEndTime(event.getSnapshotEpoch().orElseThrow()));
                eventBuilder.proposalsRevealDate(customEpochService.getEpochStartTime(event.getProposalsRevealEpoch().orElseThrow()));
                eventBuilder.proposalsRevealEpoch(event.getProposalsRevealEpoch());
            }
            case USER_BASED -> {
                eventBuilder.eventStartDate(customEpochService.getTimeBasedOnAbsoluteSlot(event.getStartSlot().orElseThrow()));
                eventBuilder.eventEndDate(customEpochService.getTimeBasedOnAbsoluteSlot(event.getEndSlot().orElseThrow()));
                eventBuilder.proposalsRevealDate(customEpochService.getTimeBasedOnAbsoluteSlot(event.getProposalsRevealSlot().orElseThrow()));
                eventBuilder.proposalsRevealSlot(event.getProposalsRevealSlot());
            }
        }

        return Either.right(Optional.of(eventBuilder.build()));
    }

    public Either<Problem, List<EventAdditionalInfo>> eventsSummaries() {
        List<Event> allValidEvents = referenceDataService.findAllValidEvents();

        return eventAdditionalInfoService.getEventAdditionalInfo(allValidEvents);
    }

}
