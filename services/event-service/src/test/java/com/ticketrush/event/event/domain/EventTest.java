package com.ticketrush.event.event.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class EventTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Test
    void createsDraftWhenDatesAreValidAndStartIsInFuture() {
        Event event = Event.createDraft(
                "Summer concert",
                "Outdoor music event",
                EventCategory.CONCERT,
                500,
                new Venue("Main stage", "Paris", "FR", ZoneId.of("Europe/Paris")),
                NOW.plusSeconds(7200),
                NOW.plusSeconds(10800),
                NOW.plusSeconds(1800),
                NOW.plusSeconds(3600),
                UUID.randomUUID(),
                CLOCK);

        assertEquals(EventStatus.DRAFT, event.getStatus());
        assertEquals("Summer concert", event.getTitle());
        assertEquals(NOW.plusSeconds(7200), event.getStartAt());
    }

    @Test
    void rejectsInvalidSalesDateOrder() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Event.createDraft(
                        "Summer concert",
                        null,
                        EventCategory.CONCERT,
                        500,
                        new Venue("Main stage", "Paris", "FR", ZoneId.of("Europe/Paris")),
                        NOW.plusSeconds(7200),
                        NOW.plusSeconds(10800),
                        NOW.plusSeconds(3600),
                        NOW.plusSeconds(1800),
                        UUID.randomUUID(),
                        CLOCK));
    }

    @Test
    void rejectsEventStartingInThePast() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Event.createDraft(
                        "Summer concert",
                        null,
                        EventCategory.CONCERT,
                        500,
                        new Venue("Main stage", "Paris", "FR", ZoneId.of("Europe/Paris")),
                        NOW.minusSeconds(1),
                        NOW.plusSeconds(3600),
                        NOW.minusSeconds(7200),
                        NOW.minusSeconds(3600),
                        UUID.randomUUID(),
                        CLOCK));
    }
}
