package com.ticketrush.event.event.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;

import com.ticketrush.event.event.domain.Event;
import com.ticketrush.event.event.domain.EventCategory;
import com.ticketrush.event.event.domain.EventRepository;
import com.ticketrush.event.event.domain.EventStatus;
import com.ticketrush.event.event.domain.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventApplicationServiceTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

    @Mock
    private EventRepository eventRepository;

    private EventApplicationService service;

    @BeforeEach
    void setUp() {
        service = new EventApplicationService(
                eventRepository,
                Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void createsAndSavesDraftForOrganizer() {
        UUID organizerId = UUID.randomUUID();
        CreateEventCommand command = new CreateEventCommand(
                "Summer concert",
                null,
                EventCategory.CONCERT,
                500,
                new Venue("Main stage", "Paris", "FR", ZoneId.of("Europe/Paris")),
                NOW.plusSeconds(7200),
                NOW.plusSeconds(10800),
                NOW.plusSeconds(1800),
                NOW.plusSeconds(3600));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event saved = service.createDraft(command, organizerId);

        assertEquals(EventStatus.DRAFT, saved.getStatus());
        assertEquals(organizerId, saved.getOrganizerId());
        verify(eventRepository).save(saved);
    }
}
