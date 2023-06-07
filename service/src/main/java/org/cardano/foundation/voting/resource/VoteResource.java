package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.*;
import org.cardano.foundation.voting.service.ReferenceDataService;
import org.cardano.foundation.voting.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/vote")
@Slf4j
public class VoteResource {

//    @Autowired
//    private VoteService voteService;
//
//    @Autowired
//    private ReferenceDataService referenceDataService;
//
//    @RequestMapping(value = "/result/{eventId}/{categoryId}", method = GET, produces = "application/json")
//    @Timed(value = "resource.vote.resultByEventIdAndCategoryId", percentiles = { 0.3, 0.5, 0.95 })
//    public ResponseEntity<?> getVotingResultByEventIdAndCategoryId(@RequestAttribute User user,
//                                                                   @PathVariable String eventId,
//                                                                   @PathVariable String categoryId) {
//        Optional<CategoryInfo> maybeCategory = referenceDataService.findCategoryByEventAndCategoryId(eventId, categoryId);
//
//        if (maybeCategory.isEmpty()) {
//            log.info("Invalid eventId:{} or categoryId:{}, userId:{}", eventId, categoryId, user.getUsername());
//
//            return ResponseEntity.notFound().build();
//        }
//
//        CategoryInfo category = maybeCategory.get();
//
//        List<UserVote> userVotes = voteService.findUserVotes(eventId, user);
//
//        if (userVotes.isEmpty()) {
//            return ResponseEntity.ok(VotingResult.builder().build());
//        }
//
//        Optional<VotingResult> maybeVotingResult = userVotes.stream()
//                .filter(entry -> entry.getCategoryId().equals(categoryId) && referenceDataService.findProposalByEventIdCategoryIdProposalId(eventId, categoryId, entry.getProposalId()).isPresent())
//                .map(vote -> {
//                    var proposalId = vote.getProposalId();
//
//                    ProposalInfo nominee = referenceDataService.findProposalByEventIdCategoryIdProposalId(eventId, categoryId, proposalId).orElseThrow();
//
//                    return VotingResult.builder()
//                            .category(new IdLabel(categoryId, category.getPresentationName()))
//                            .proposal(new IdLabel(proposalId, nominee.getPresentationName()))
//                            .build();
//                }).toList()
//                .stream()
//                .findFirst();
//
//        return maybeVotingResult.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.ok(VotingResult.builder().build()));
//    }

//    @RequestMapping(value = "/results/all/{eventId}/{categoryGroupName}", method = GET, produces = "application/json")
//    @Timed(value = "resource.voting.allResultsByEventIdAndCategoryGroupName", percentiles = { 0.3, 0.5, 0.95 })
//    public ResponseEntity<?> getVotingResultsByEventId(@RequestAttribute User user,
//                                                       @PathVariable String eventId,
//                                                       @Valid @PathVariable String categoryGroupName) {
//        List<Vote> userVotes = voteService.findUserVotes(eventId, user);
//
//        List<VotingResult> allVotingResults = userVotes.stream().filter(vote -> {
//            var categoryId = vote.getCategoryId();
//            var proposalId = vote.getProposalId();
//
//            var foundIds = referenceDataService.findProposalByEventIdCategoryIdProposalId(eventId, categoryId, proposalId).isPresent();
//            if (!foundIds) {
//                return false;
//            }
//
//            CategoryInfo category = referenceDataService.findCategoryByEventAndCategoryId(eventId, categoryId).orElseThrow();
//
//            return category.getCategoryGroup().getName().equals(categoryGroupName);
//        }).map(vote -> {
//            CategoryInfo category = referenceDataService.findCategoryByEventAndCategoryId(eventId, vote.getCategoryId()).orElseThrow();
//            ProposalInfo proposal = referenceDataService.findProposalByEventIdCategoryIdProposalId(eventId, vote.getCategoryId(), vote.getProposalId()).orElseThrow();
//
//            return VotingResult.builder()
//                    .category(new IdLabel(vote.getCategoryId(), category.getPresentationName()))
//                    .proposal(new IdLabel(vote.getProposalId(), proposal.getPresentationName()))
//                    .build();
//        }).toList();
//
//        return ResponseEntity.ok(allVotingResults);
//    }

}
