package com.vasitum.scheduler.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
    uniqueConstraints = {
      @UniqueConstraint(
          name = "slot_unique_time_interviewer",
          columnNames = {"interviewer_id", "startTime", "endTime"})
    })
public class Slot {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Interviewer interviewer;

  @Column(nullable = false)
  private LocalDateTime startTime;

  @Column(nullable = false)
  private LocalDateTime endTime;

  @Column(nullable = false)
  private int capacity = 1;

  @Column(nullable = false)
  private int bookedCount = 0;

  @Version private long version;

  protected Slot() {}

  public Slot(Interviewer interviewer, LocalDateTime startTime, LocalDateTime endTime, int capacity) {
    this.interviewer = interviewer;
    this.startTime = startTime;
    this.endTime = endTime;
    this.capacity = capacity;
  }

  public Long getId() {
    return id;
  }

  public Interviewer getInterviewer() {
    return interviewer;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public int getCapacity() {
    return capacity;
  }

  public int getBookedCount() {
    return bookedCount;
  }

  public boolean hasCapacity() {
    return bookedCount < capacity;
  }

  public void incrementBooking() {
    if (!hasCapacity()) {
      throw new IllegalStateException("Slot is full");
    }
    bookedCount += 1;
  }

  public void decrementBooking() {
    if (bookedCount > 0) {
      bookedCount -= 1;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Slot slot)) return false;
    return Objects.equals(id, slot.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}

