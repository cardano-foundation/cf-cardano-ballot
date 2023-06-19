package org.cardano.foundation.voting.service.reference_data;

import io.micrometer.core.annotation.Timed;
import org.cardano.foundation.voting.domain.entity.Category;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.Proposal;
import org.cardano.foundation.voting.repository.CategoryRepository;
import org.cardano.foundation.voting.repository.EventRepository;
import org.cardano.foundation.voting.repository.ProposalRepository;
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

    @Timed(value = "service.reference.findEventByName", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public Optional<Event> findEventByName(String eventName) {
        return eventRepository.findByName(eventName);
    }

    @Timed(value = "service.reference.findCategoryByName", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public Optional<Category> findCategoryByName(String eventName) {
        return categoryRepository.findByName(eventName);
    }

    @Timed(value = "service.reference.findProposalByName", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public Optional<Proposal> findProposalByName(String eventName) {
        return proposalRepository.findByName(eventName);
    }

    @Timed(value = "service.reference.storeEvent", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public Event storeEvent(Event event) {
        return eventRepository.saveAndFlush(event);
    }

    public List<Event> findAllEvents() {
        return eventRepository.findAll();
    }

}
