package org.cardano.foundation.voting.service.reference_data;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.EventAdditionalInfo;
import org.cardano.foundation.voting.domain.entity.*;
import org.cardano.foundation.voting.repository.CategoryRepository;
import org.cardano.foundation.voting.repository.EventRepository;
import org.cardano.foundation.voting.repository.ProposalRepository;
import org.cardano.foundation.voting.service.expire.EventAdditionalInfoService;
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

    private final EventAdditionalInfoService eventAdditionalInfoService;

    @Timed(value = "service.reference.findValidEventById", histogram = true)
    @Transactional(readOnly = true)
    public Optional<Event> findValidEventById(String id) {
        return eventRepository.findById(id).filter(Event::isValid);
    }

    @Timed(value = "service.reference.findEventById", histogram = true)
    @Transactional(readOnly = true)
    public Optional<Event> findEventById(String id) {
        return eventRepository.findById(id);
    }

    @Timed(value = "service.reference.findCategoryById", histogram = true)
    @Transactional(readOnly = true)
    public Optional<Category> findCategoryById(CategoryId categoryId) {
        return categoryRepository.findById(categoryId);
    }

    @Timed(value = "service.reference.findProposalById", histogram = true)
    @Transactional(readOnly = true)
    public Optional<Proposal> findProposalById(ProposalId proposalId) {
        return proposalRepository.findById(proposalId);
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
                .filter(event -> eventAdditionalInfoService.getEventAdditionalInfo(event).fold(problem -> false, EventAdditionalInfo::active))
                .toList();
    }

    @Timed(value = "service.reference.storeCategory", histogram = true)
    @Transactional
    public Category storeCategory(Category category) {
        return categoryRepository.saveAndFlush(category);
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
