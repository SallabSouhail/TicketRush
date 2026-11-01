package com.ticketrush.event.event.api;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.ticketrush.event.event.application.CreateEventCommand;
import com.ticketrush.event.event.domain.EventCategory;
import com.ticketrush.event.event.domain.Venue;

public record CreateEventRequest(
        @NotBlank @Size(min = 3, max = 150) String title,
        @Size(max = 5000) String description,
        @NotNull EventCategory category,
        @Min(1) @Max(100000) int capacity,
        @NotNull @Valid VenueRequest venue,
        @NotNull Instant startAt,
        @NotNull Instant endAt,
        @NotNull Instant salesStartAt,
        @NotNull Instant salesEndAt) {

    public CreateEventCommand toCommand() {
        return new CreateEventCommand(
                title,
                description,
                category,
                capacity,
                venue.toDomain(),
                startAt,
                endAt,
                salesStartAt,
                salesEndAt);
    }

    public record VenueRequest(
            @NotBlank @Size(max = 150) String name,
            @NotBlank @Size(max = 100) String city,
            @NotBlank @Pattern(regexp = "[A-Z]{2}") String country,
            @NotBlank @Size(max = 64) String timezone) {

        public Venue toDomain() {
            try {
                return new Venue(name, city, country, ZoneId.of(timezone));
            } catch (DateTimeException exception) {
                throw new IllegalArgumentException("timezone must be a valid IANA timezone", exception);
            }
        }
    }
}
