package com.ticketrush.event.event.domain;

import java.time.ZoneId;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Where an event takes place. A value object: no identity, immutable, stored in the event table.
 * The timezone belongs to the place, and is used to display the event's local time.
 */
@Embeddable
public record Venue(
        @Column(name = "venue_name", nullable = false, length = 150) String name,
        @Column(name = "city", nullable = false, length = 100) String city,
        @JdbcTypeCode(SqlTypes.CHAR) @Column(name = "country", nullable = false, length = 2) String country,
        @Column(name = "timezone", nullable = false, length = 64) ZoneId timezone) {

    public Venue {
        require(name != null && !name.isBlank() && name.length() <= 150,
                "venue name must contain between 1 and 150 characters");
        require(city != null && !city.isBlank() && city.length() <= 100,
                "city must contain between 1 and 100 characters");
        require(country != null && country.matches("[A-Z]{2}"),
                "country must be a two-letter uppercase code");
        require(timezone != null && ZoneId.getAvailableZoneIds().contains(timezone.getId()),
                "timezone must be a valid IANA timezone");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
