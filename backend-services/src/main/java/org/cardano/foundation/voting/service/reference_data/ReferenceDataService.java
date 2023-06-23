package org.cardano.foundation.voting.service.reference_data;

import io.micrometer.core.annotation.Timed;
import org.cardano.foundation.voting.domain.entity.Category;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.Proposal;
import org.cardano.foundation.voting.repository.CategoryRepository;
import org.cardano.foundation.voting.repository.EventRepository;
import org.cardano.foundation.voting.repository.ProposalRepository;
import org.cardano.foundation.voting.service.ExpirationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReferenceDataService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private ExpirationService expirationService;

    @Timed(value = "service.reference.findEventById", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public Optional<Event> findEventById(String id) {
        return eventRepository.findById(id);
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

    @Timed(value = "service.reference.storeEvent", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public Event storeEvent(Event event) {
        return eventRepository.saveAndFlush(event);
    }

    public List<Event> findAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> findAllActiveEvents() {
        return eventRepository.findAll().stream().filter(event -> expirationService.isEventActive(event)).toList();
    }

}
