package org.cardano.foundation.voting.service;

import io.micrometer.core.annotation.Timed;
import org.cardano.foundation.voting.domain.entity.Event;
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
    private CategoryRepository categoryRepository;

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private EventRepository eventRepository;

    @Timed(value = "service.reference.findEvent", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public Optional<Event> findEvent(String eventId) {
        return eventRepository.findById(eventId);
    }

    @Timed(value = "service.reference.storeEvent", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public Event storeEvent(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> findEvents() {
        return eventRepository.findAll();
    }

}
