package org.cardano.foundation.voting.service;

import io.micrometer.core.annotation.Timed;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReferenceDataService {

    @Autowired
    private EventRepository eventRepository;

    @Timed(value = "service.reference.findEventById", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public Optional<Event> findEvent(String eventId) {
        return eventRepository.findById(eventId);
    }

    @Timed(value = "service.reference.findEventByName", percentiles = {0.3, 0.5, 0.95})
    @Transactional
    public Optional<Event> findEventByName(String name) {
        return eventRepository.findAll().stream().filter(event -> event.getName().equals(name)).findFirst();
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
