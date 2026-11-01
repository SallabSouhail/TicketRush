package com.ticketrush.event.event.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;

import com.ticketrush.event.event.application.EventApplicationService;
import com.ticketrush.event.event.domain.Event;
import com.ticketrush.event.event.domain.EventCategory;
import com.ticketrush.event.event.domain.Venue;
import com.ticketrush.event.shared.web.ApiExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(EventController.class)
@org.springframework.context.annotation.Import(ApiExceptionHandler.class)
class EventControllerTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventApplicationService eventApplicationService;

    @Test
    void returnsCreatedWithLocationForValidDraft() throws Exception {
        UUID organizerId = UUID.randomUUID();
        Event event = Event.createDraft(
                "Summer concert", null, EventCategory.CONCERT, 500,
                new Venue("Main stage", "Paris", "FR", ZoneId.of("Europe/Paris")),
                NOW.plusSeconds(7200), NOW.plusSeconds(10800), NOW.plusSeconds(1800), NOW.plusSeconds(3600),
                organizerId, Clock.fixed(NOW, ZoneOffset.UTC));
        when(eventApplicationService.createDraft(any(), eq(organizerId))).thenReturn(event);

        mockMvc.perform(post("/api/v1/events")
                        .header("X-Organizer-Id", organizerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title":"Summer concert",
                                  "description":null,
                                  "category":"CONCERT",
                                  "capacity":500,
                                  "venue":{"name":"Main stage","city":"Paris","country":"FR","timezone":"Europe/Paris"},
                                  "startAt":"2026-01-01T12:00:00Z",
                                  "endAt":"2026-01-01T13:00:00Z",
                                  "salesStartAt":"2026-01-01T10:30:00Z",
                                  "salesEndAt":"2026-01-01T11:00:00Z"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.organizerId").value(organizerId.toString()));
    }

    @Test
    void returnsProblemDetailForInvalidCountry() throws Exception {
        mockMvc.perform(post("/api/v1/events")
                        .header("X-Organizer-Id", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title":"Summer concert",
                                  "category":"CONCERT",
                                  "capacity":500,
                                  "venue":{"name":"Main stage","city":"Paris","country":"fr","timezone":"Europe/Paris"},
                                  "startAt":"2026-01-01T12:00:00Z",
                                  "endAt":"2026-01-01T13:00:00Z",
                                  "salesStartAt":"2026-01-01T10:30:00Z",
                                  "salesEndAt":"2026-01-01T11:00:00Z"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }
}
