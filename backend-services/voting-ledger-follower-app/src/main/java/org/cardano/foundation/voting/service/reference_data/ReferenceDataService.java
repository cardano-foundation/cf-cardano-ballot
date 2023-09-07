package org.cardano.foundation.voting.service.reference_data;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.entity.MerkleRootHash;
import org.cardano.foundation.voting.domain.entity.Category;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.Proposal;
import org.cardano.foundation.voting.repository.CategoryRepository;
import org.cardano.foundation.voting.repository.EventRepository;
import org.cardano.foundation.voting.repository.MerkleRootHashRepository;
import org.cardano.foundation.voting.repository.ProposalRepository;
import org.cardano.foundation.voting.service.expire.ExpirationService;
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
    private ExpirationService expirationService;

    @Autowired
    private MerkleRootHashRepository merkleRootHashRepository;

    @Timed(value = "service.reference.findValidEventByName", histogram = true)
    @Transactional(readOnly = true)
    public Optional<Event> findValidEventByName(String name) {
        return eventRepository.findById(name).filter(Event::isValid);
    }

    @Timed(value = "service.reference.findEventByName", histogram = true)
    @Transactional(readOnly = true)
    public Optional<Event> findEventByName(String name) {
        return eventRepository.findById(name);
    }

    @Timed(value = "service.reference.findCategoryByName", histogram = true)
    @Transactional(readOnly = true)
    public Optional<Category> findCategoryByName(String name) {
        return categoryRepository.findById(name);
    }

    @Timed(value = "service.reference.findProposalById", histogram = true)
    @Transactional(readOnly = true)
    public Optional<Proposal> findProposalById(String id) {
        return proposalRepository.findById(id);
    }

    @Timed(value = "service.reference.findProposalByName", histogram = true)
    @Transactional(readOnly = true)
    public Optional<Proposal> findProposalByName(Category category, String name) {
        return proposalRepository.findProposalByName(category.getId(), name);
    }

    @Timed(value = "service.reference.storeEvent", histogram = true)
    @Transactional
    public Event storeEvent(Event event) {
        return eventRepository.saveAndFlush(event);
    }

    @Timed(value = "service.reference.findAllValidEvents", histogram = true)
    @Transactional(readOnly = true)
    public List<Event> findAllValidEvents() {
        return eventRepository.findAll()
                .stream()
                .filter(Event::isValid)
                .toList();
    }

    @Timed(value = "service.reference.findAllActiveEvents", histogram = true)
    @Transactional(readOnly = true)
    public List<Event> findAllActiveEvents() {
        return findAllValidEvents().stream()
                .filter(event -> expirationService.isEventActive(event)).toList();
    }

    @Timed(value = "service.reference.storeCategory", histogram = true)
    @Transactional
    public Category storeCategory(Category category) {
        return categoryRepository.saveAndFlush(category);
    }

    @Timed(value = "service.reference.storeCommitments", histogram = true)
    @Transactional
    public List<MerkleRootHash> storeCommitments(List<MerkleRootHash> merkleRootHashes) {
        log.info("Storing commitments:{}", merkleRootHashes);
        return merkleRootHashRepository.saveAllAndFlush(merkleRootHashes);
    }

    @Timed(value = "service.reference.rollback", histogram = true)
    @Transactional
    public void rollbackReferenceDataAfterSlot(long slot) {
        proposalRepository.deleteAllAfterSlot(slot);
        proposalRepository.flush();

        categoryRepository.deleteAllAfterSlot(slot);
        categoryRepository.flush();

        eventRepository.deleteAllAfterSlot(slot);
        eventRepository.flush();
    }

}
