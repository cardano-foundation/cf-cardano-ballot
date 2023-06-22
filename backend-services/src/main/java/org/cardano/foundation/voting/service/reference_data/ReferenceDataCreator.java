package org.cardano.foundation.voting.service.reference_data;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.entity.Category;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.Proposal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.CardanoNetwork.PREPROD;
import static org.cardano.foundation.voting.domain.EventType.STAKE_BASED;

@Service
@Slf4j
public class ReferenceDataCreator {

    public static final String EVENT_NAME = "Voltaire_Pre_Ratification";

    @Autowired
    private ReferenceDataService referenceDataService;

    @Autowired
    private CardanoNetwork cardanoNetwork;

    public void createReferenceData() {
        Optional<Event> maybeVoltaireEvent = referenceDataService.findEventByName(EVENT_NAME);

        if (maybeVoltaireEvent.isPresent()) {
            log.info("There is already event: {}", maybeVoltaireEvent.orElseThrow());
            return;
        }

        if (cardanoNetwork == PREPROD) {
            createPreprodReferenceData();
            return;
        }

        throw new RuntimeException("Unsupported network: " + cardanoNetwork);
    }

    private void createPreprodReferenceData() {
        log.info("Creating event along with proposals...");

        Event event = new Event();
        event.setId("5abcb6a2-f9a9-4617-b9ce-10b9dd290354");
        event.setName(EVENT_NAME);
        event.setPresentationName("CIP-1694 Voltaire Pre-Ratification");
        event.setTeam("CF Team");
        event.setStartEpoch(70);
        event.setEventType(STAKE_BASED);
        event.setEndEpoch(100);
        event.setSnapshotEpoch(75);

        event.setDescription("Pre-Ratification of the Voltaire era");

        Category preRatificationCategory = new Category();
        preRatificationCategory.setId("e969729d-ab08-4ca3-a17d-13f3a8b8c0ab");
        preRatificationCategory.setName("Pre-Ratification");
        preRatificationCategory.setDescription("Pre-Ratification for CIP-1694");
        preRatificationCategory.setPresentationName("Pre-Ratification");

        Proposal yesProposal = new Proposal();
        yesProposal.setId("ffb9fd11-b82b-4766-bcd5-b8e7b760624a");
        yesProposal.setName("YES");
        yesProposal.setPresentationName("Yes");
        yesProposal.setCategory(preRatificationCategory);

        Proposal noProposal = new Proposal();
        noProposal.setId("ffb9fd11-b82b-4766-bcd5-b8e7b760624b");
        noProposal.setName("NO");
        noProposal.setPresentationName("No");
        noProposal.setCategory(preRatificationCategory);

        Proposal abstainProposal = new Proposal();
        abstainProposal.setId("ffb9fd11-b82b-4766-bcd5-b8e7b760624c");
        abstainProposal.setName("ABSTAIN");
        abstainProposal.setPresentationName("Abstain");
        abstainProposal.setCategory(preRatificationCategory);

        preRatificationCategory.setEvent(event);
        preRatificationCategory.setProposals(List.of(yesProposal, noProposal, abstainProposal));
        event.setCategories(List.of(preRatificationCategory));

        referenceDataService.storeEvent(event);
    }

}
