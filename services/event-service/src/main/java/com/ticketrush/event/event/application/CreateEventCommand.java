package com.ticketrush.event.event.application;

import java.time.Instant;

import com.ticketrush.event.event.domain.EventCategory;
import com.ticketrush.event.event.domain.Venue;

/** Input to the application use case for creating an event draft. */
public record CreateEventCommand(
        String title,
        String description,
        EventCategory category,
        int capacity,
        Venue venue,
        Instant startAt,
        Instant endAt,
        Instant salesStartAt,
        Instant salesEndAt) {
}
