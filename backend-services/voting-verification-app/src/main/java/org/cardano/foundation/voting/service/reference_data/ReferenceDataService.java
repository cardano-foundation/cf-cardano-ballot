package org.cardano.foundation.voting.service.reference_data;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.entity.Category;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.MerkleRootHash;
import org.cardano.foundation.voting.domain.entity.Proposal;
import org.cardano.foundation.voting.repository.CategoryRepository;
import org.cardano.foundation.voting.repository.EventRepository;
import org.cardano.foundation.voting.repository.MerkleRootHashRepository;
import org.cardano.foundation.voting.repository.ProposalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ReferenceDataService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private MerkleRootHashRepository merkleRootHashRepository;

    @Timed(value = "service.reference.findValidEventByName", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public Optional<Event> findValidEventByName(String name) {
        return eventRepository.findById(name).filter(Event::isValid);
    }

    @Timed(value = "service.reference.findEventByName", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public Optional<Event> findEventByName(String name) {
        return eventRepository.findById(name);
    }

    @Timed(value = "service.reference.findCategoryByName", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public Optional<Category> findCategoryByName(String name) {
        return categoryRepository.findById(name);
    }

    @Timed(value = "service.reference.findProposalById", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public Optional<Proposal> findProposalById(String id) {
        return proposalRepository.findById(id);
    }

    @Timed(value = "service.reference.findProposalByName", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public Optional<Proposal> findProposalByName(Category category, String name) {
        return proposalRepository.findProposalByName(category.getId(), name);
    }

    @Timed(value = "service.reference.storeEvent", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public Event storeEvent(Event event) {
        return eventRepository.saveAndFlush(event);
    }

    @Timed(value = "service.reference.findAllValidEvents", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public List<Event> findAllValidEvents() {
        return eventRepository.findAll()
                .stream()
                .filter(Event::isValid)
                .toList();
    }

    @Timed(value = "service.reference.storeCategory", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public Category storeCategory(Category category) {
        return categoryRepository.saveAndFlush(category);
    }

    @Timed(value = "service.reference.storeCommitments", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public List<MerkleRootHash> storeCommitments(List<MerkleRootHash> merkleRootHashes) {
        return merkleRootHashRepository.saveAllAndFlush(merkleRootHashes);
    }

    @Timed(value = "service.reference.rollback", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public void rollbackReferenceDataAfterSlot(long slot) {
        log.info("Rollback reference data after slot:{}", slot);

        eventRepository.deleteAllAfterSlot(slot);
        categoryRepository.deleteAllAfterSlot(slot);
        proposalRepository.deleteAllAfterSlot(slot);

        eventRepository.flush();
        categoryRepository.flush();
        proposalRepository.flush();
    }

}
