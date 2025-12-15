package com.vasitum.scheduler.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Booking {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Candidate candidate;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Slot slot;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookingStatus status;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Version private long version;

  protected Booking() {}

  public Booking(Candidate candidate, Slot slot, BookingStatus status) {
    this.candidate = candidate;
    this.slot = slot;
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public Candidate getCandidate() {
    return candidate;
  }

  public Slot getSlot() {
    return slot;
  }

  public BookingStatus getStatus() {
    return status;
  }

  public void updateSlot(Slot slot) {
    this.slot = slot;
  }

  public void markConfirmed() {
    this.status = BookingStatus.CONFIRMED;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Booking booking)) return false;
    return Objects.equals(id, booking.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}

