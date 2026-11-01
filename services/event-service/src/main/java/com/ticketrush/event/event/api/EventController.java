package com.ticketrush.event.event.api;

import java.net.URI;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ticketrush.event.event.application.EventApplicationService;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventApplicationService eventApplicationService;

    public EventController(EventApplicationService eventApplicationService) {
        this.eventApplicationService = eventApplicationService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> createDraft(
            @RequestHeader("X-Organizer-Id") UUID organizerId,
            @Valid @RequestBody CreateEventRequest request) {
        EventResponse response = EventResponse.from(
                eventApplicationService.createDraft(request.toCommand(), organizerId));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }
}
