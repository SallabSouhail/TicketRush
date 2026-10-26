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
}
