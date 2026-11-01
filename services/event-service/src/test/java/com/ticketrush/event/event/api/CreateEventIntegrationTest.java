package com.ticketrush.event.event.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import com.ticketrush.event.TestcontainersConfiguration;
import com.ticketrush.event.event.domain.EventRepository;
import com.ticketrush.event.event.domain.EventStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class CreateEventIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Test
    void createsDraftAndPersistsItInPostgres() throws Exception {
        UUID organizerId = UUID.randomUUID();

        String location = mockMvc.perform(post("/api/v1/events")
                        .header("X-Organizer-Id", organizerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title":"Integration concert",
                                  "description":"Created by the integration test",
                                  "category":"CONCERT",
                                  "capacity":250,
                                  "venue":{"name":"Main stage","city":"Paris","country":"FR","timezone":"Europe/Paris"},
                                  "startAt":"2030-06-01T12:00:00Z",
                                  "endAt":"2030-06-01T14:00:00Z",
                                  "salesStartAt":"2030-05-01T10:00:00Z",
                                  "salesEndAt":"2030-05-31T10:00:00Z"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        String idText = location.substring(location.lastIndexOf('/') + 1);
        var persistedEvent = eventRepository.findById(UUID.fromString(idText)).orElseThrow();
        assertEquals(EventStatus.DRAFT, persistedEvent.getStatus());
        assertEquals(organizerId, persistedEvent.getOrganizerId());
    }
}
