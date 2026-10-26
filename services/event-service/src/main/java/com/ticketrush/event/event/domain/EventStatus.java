package com.ticketrush.event.event.domain;

/**
 * Lifecycle of an event, changed only by an organizer action.
 * Stored as text: keep in sync with the ck_event_status constraint.
 */
public enum EventStatus {
    DRAFT,
    PUBLISHED,
    CANCELLED
}
