//package org.cardano.foundation.voting.service.reference_data;
//
//import lombok.extern.slf4j.Slf4j;
//import org.cardano.foundation.voting.domain.CardanoNetwork;
//import org.cardano.foundation.voting.domain.entity.Category;
//import org.cardano.foundation.voting.domain.entity.Event;
//import org.cardano.foundation.voting.domain.entity.Proposal;
//import org.cardano.foundation.voting.service.transaction_submit.L1SubmissionService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.UUID;
//
//import static org.cardano.foundation.voting.domain.SchemaVersion.V1;
//import static org.cardano.foundation.voting.domain.VotingEventType.STAKE_BASED;
//
//@Service
//@Slf4j
//public class ReferenceDataCreator {
//
//    public static final String EVENT_NAME = "Voltaire_Pre_Ratification";
//
//    @Autowired
//    private ReferenceDataService referenceDataService;
//
//    @Autowired
//    private L1SubmissionService l1SubmissionService;
//
//    @Autowired
//    private CardanoNetwork cardanoNetwork;
//
//    public void createReferenceData() {
//        // TODO we have to expect on chain that there can be many events with this name
//        var maybeEvent = referenceDataService.findEventByName(EVENT_NAME);
//
//        if (maybeEvent.isPresent()) {
//            log.info("There is already event: {}", maybeEvent.orElseThrow());
//            return;
//        }
//
//        switch (cardanoNetwork) {
//            case PREPROD -> createPreprodReferenceData();
//            default -> throw new RuntimeException("Unsupported network: " + cardanoNetwork);
//        }
//    }
//
//    private void createPreprodReferenceData() {
//        log.info("Creating event along with proposals...");
//
//        var yesId = UUID.randomUUID().toString();
//        var noId = UUID.randomUUID().toString();
//        var abstainId = UUID.randomUUID().toString();
//
//        Event event = new Event();
//        event.setId(EVENT_NAME);
//        event.setVersion(V1);
//        event.setTeam("CF & IOG");
//        event.setStartEpoch(70);
//        event.setVotingEventType(STAKE_BASED);
//        event.setEndEpoch(90);
//        event.setSnapshotEpoch(75);
//
//        Category preRatificationCategory = new Category();
//        preRatificationCategory.setGdprProtection(false);
//        preRatificationCategory.setId("Pre-Ratification");
//        preRatificationCategory.setVersion(V1);
//
//        Proposal yesProposal = new Proposal();
//        yesProposal.setId(yesId);
//        yesProposal.setName("YES");
//        yesProposal.setCategory(preRatificationCategory);
//
//        Proposal noProposal = new Proposal();
//        noProposal.setId(noId);
//        noProposal.setName("NO");
//        noProposal.setCategory(preRatificationCategory);
//
//        Proposal abstainProposal = new Proposal();
//        abstainProposal.setId(abstainId);
//        abstainProposal.setName("ABSTAIN");
//        abstainProposal.setCategory(preRatificationCategory);
//
//        preRatificationCategory.setEvent(event);
//        preRatificationCategory.setProposals(List.of(yesProposal, noProposal, abstainProposal));
//        event.setCategories(List.of(preRatificationCategory));
//
//        referenceDataService.storeEvent(event);
//    }
//
//}
