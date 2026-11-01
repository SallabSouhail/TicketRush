package com.ticketrush.event.event.api;

import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import com.ticketrush.event.event.domain.Event;
import com.ticketrush.event.event.domain.EventCategory;
import com.ticketrush.event.event.domain.EventStatus;

public record EventResponse(
        UUID id,
        String title,
        String description,
        EventCategory category,
        int capacity,
        VenueResponse venue,
        Instant startAt,
        Instant endAt,
        Instant salesStartAt,
        Instant salesEndAt,
        EventStatus status,
        UUID organizerId,
        Instant createdAt,
        Instant updatedAt) {

    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getCategory(),
                event.getCapacity(),
                new VenueResponse(
                        event.getVenue().name(),
                        event.getVenue().city(),
                        event.getVenue().country(),
                        event.getVenue().timezone()),
                event.getStartAt(),
                event.getEndAt(),
                event.getSalesStartAt(),
                event.getSalesEndAt(),
                event.getStatus(),
                event.getOrganizerId(),
                event.getCreatedAt(),
                event.getUpdatedAt());
    }

    public record VenueResponse(String name, String city, String country, ZoneId timezone) {
    }
}
