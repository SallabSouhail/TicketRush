package com.ticketrush.event.event.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;

import org.junit.jupiter.api.Test;

class VenueTest {

    @Test
    void rejectsCountryThatIsNotTwoUppercaseLetters() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Venue("Stadium", "Paris", "fr", ZoneId.of("Europe/Paris")));
    }

    @Test
    void acceptsIanaTimezone() {
        Venue venue = new Venue("Stadium", "Paris", "FR", ZoneId.of("Europe/Paris"));

        assertEquals("Europe/Paris", venue.timezone().getId());
    }
}
