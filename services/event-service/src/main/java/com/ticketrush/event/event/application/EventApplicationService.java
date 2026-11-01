package com.ticketrush.event.event.application;

import java.time.Clock;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ticketrush.event.event.domain.Event;
import com.ticketrush.event.event.domain.EventRepository;

@Service
public class EventApplicationService {

    private final EventRepository eventRepository;
    private final Clock clock;

    public EventApplicationService(EventRepository eventRepository, Clock clock) {
        this.eventRepository = eventRepository;
        this.clock = clock;
    }

    @Transactional
    public Event createDraft(CreateEventCommand command, UUID organizerId) {
        Event event = Event.createDraft(
                command.title(),
                command.description(),
                command.category(),
                command.capacity(),
                command.venue(),
                command.startAt(),
                command.endAt(),
                command.salesStartAt(),
                command.salesEndAt(),
                organizerId,
                clock);
        return eventRepository.save(event);
    }
}
