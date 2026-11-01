package com.ticketrush.event.event.domain;

import java.time.Instant;
import java.time.Clock;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * An event and its lifecycle. Mapped to the event table created by V1__create_event_table.sql.
 *
 * <p>Fields have no setters on purpose: state changes go through business methods
 * (createDraft, publish, cancel...) that enforce the business rules.
 */
@Entity
@Table(name = "event")
@EntityListeners(AuditingEntityListener.class)
public class Event {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 32)
    private EventCategory category;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Embedded
    private Venue venue;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    @Column(name = "sales_start_at", nullable = false)
    private Instant salesStartAt;

    @Column(name = "sales_end_at", nullable = false)
    private Instant salesEndAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private EventStatus status;

    @Column(name = "organizer_id", nullable = false, updatable = false)
    private UUID organizerId;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Required by JPA only. */
    protected Event() {
    }

    /** Creates a new draft after enforcing the rules that depend on the current time. */
    public static Event createDraft(
            String title,
            String description,
            EventCategory category,
            int capacity,
            Venue venue,
            Instant startAt,
            Instant endAt,
            Instant salesStartAt,
            Instant salesEndAt,
            UUID organizerId,
            Clock clock) {
        require(title != null && title.equals(title.trim()) && title.length() >= 3 && title.length() <= 150,
                "title must contain between 3 and 150 characters without surrounding whitespace");
        require(description == null || description.length() <= 5000,
                "description must not exceed 5000 characters");
        require(category != null, "category is required");
        require(capacity >= 1 && capacity <= 100000, "capacity must be between 1 and 100000");
        require(venue != null, "venue is required");
        require(startAt != null, "startAt is required");
        require(endAt != null, "endAt is required");
        require(salesStartAt != null, "salesStartAt is required");
        require(salesEndAt != null, "salesEndAt is required");
        require(organizerId != null, "organizerId is required");
        require(clock != null, "clock is required");

        require(salesStartAt.isBefore(salesEndAt)
                        && !salesEndAt.isAfter(startAt)
                        && startAt.isBefore(endAt),
                "dates must satisfy salesStartAt < salesEndAt <= startAt < endAt");
        require(startAt.isAfter(clock.instant()), "startAt must be in the future");

        Event event = new Event();
        event.title = title;
        event.description = description;
        event.category = category;
        event.capacity = capacity;
        event.venue = venue;
        event.startAt = startAt;
        event.endAt = endAt;
        event.salesStartAt = salesStartAt;
        event.salesEndAt = salesEndAt;
        event.status = EventStatus.DRAFT;
        event.organizerId = organizerId;
        return event;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public EventCategory getCategory() {
        return category;
    }

    public int getCapacity() {
        return capacity;
    }

    public Venue getVenue() {
        return venue;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public Instant getSalesStartAt() {
        return salesStartAt;
    }

    public Instant getSalesEndAt() {
        return salesEndAt;
    }

    public EventStatus getStatus() {
        return status;
    }

    public UUID getOrganizerId() {
        return organizerId;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Identity-based equality. The id is null until the entity is persisted, so two new
     * instances are never equal, and the hash code is constant to stay stable across persist.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Event event)) {
            return false;
        }
        return id != null && id.equals(event.getId());
    }

    @Override
    public int hashCode() {
        return Event.class.hashCode();
    }

    @Override
    public String toString() {
        return "Event{id=" + id + ", status=" + status + "}";
    }
}
