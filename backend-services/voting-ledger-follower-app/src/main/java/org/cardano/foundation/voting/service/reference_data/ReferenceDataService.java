package org.cardano.foundation.voting.service.reference_data;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.entity.Category;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.Proposal;
import org.cardano.foundation.voting.repository.CategoryRepository;
import org.cardano.foundation.voting.repository.EventRepository;
import org.cardano.foundation.voting.repository.ProposalRepository;
import org.cardano.foundation.voting.service.expire.ExpirationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReferenceDataService {

    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;

    private final ProposalRepository proposalRepository;

    private final ExpirationService expirationService;

    @Timed(value = "service.reference.findValidEventByName", percentiles = {0.3, 0.5, 0.95})
    @Transactional(readOnly = true)
    public Optional<Event> findValidEventByName(String name) {
        return eventRepository.findById(name).filter(Event::isValid);
    }

    @Timed(value = "service.reference.findEventByName", percentiles = {0.3, 0.5, 0.95})
    @Transactional(readOnly = true)
    public Optional<Event> findEventByName(String name) {
        return eventRepository.findById(name);
    }

    @Timed(value = "service.reference.findCategoryByName", percentiles = {0.3, 0.5, 0.95})
    @Transactional(readOnly = true)
    public Optional<Category> findCategoryByName(String name) {
        return categoryRepository.findById(name);
    }

    @Timed(value = "service.reference.findProposalById", percentiles = {0.3, 0.5, 0.95})
    @Transactional(readOnly = true)
    public Optional<Proposal> findProposalById(String id) {
        return proposalRepository.findById(id);
    }

    @Timed(value = "service.reference.findProposalByName", percentiles = {0.3, 0.5, 0.95})
    @Transactional(readOnly = true)
    public Optional<Proposal> findProposalByName(Category category, String name) {
        return proposalRepository.findProposalByName(category.getId(), name);
    }

    @Timed(value = "service.reference.storeEvent", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public Event storeEvent(Event event) {
        return eventRepository.saveAndFlush(event);
    }

    @Timed(value = "service.reference.findAllValidEvents", percentiles = {0.3, 0.5, 0.95})
    @Transactional(readOnly = true)
    public List<Event> findAllValidEvents() {
        return eventRepository.findAll()
                .stream()
                .filter(Event::isValid)
                .toList();
    }

    @Timed(value = "service.reference.findAllActiveEvents", percentiles = {0.3, 0.5, 0.95})
    @Transactional(readOnly = true)
    public List<Event> findAllActiveEvents() {
        return findAllValidEvents().stream()
                .filter(expirationService::isEventActive).toList();
    }

    @Timed(value = "service.reference.storeCategory", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public Category storeCategory(Category category) {
        return categoryRepository.saveAndFlush(category);
    }

    @Timed(value = "service.reference.rollback", percentiles = {0.3, 0.5, 0.95})
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
