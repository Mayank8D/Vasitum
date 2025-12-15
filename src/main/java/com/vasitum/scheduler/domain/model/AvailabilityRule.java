package com.vasitum.scheduler.domain.model;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

@Entity
public class AvailabilityRule {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Interviewer interviewer;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DayOfWeek dayOfWeek;

  @Column(nullable = false)
  private LocalTime startTime;

  @Column(nullable = false)
  private LocalTime endTime;

  @Column(nullable = false)
  private int slotDurationMinutes;

  protected AvailabilityRule() {}

  public AvailabilityRule(
      Interviewer interviewer,
      DayOfWeek dayOfWeek,
      LocalTime startTime,
      LocalTime endTime,
      int slotDurationMinutes) {
    this.interviewer = interviewer;
    this.dayOfWeek = dayOfWeek;
    this.startTime = startTime;
    this.endTime = endTime;
    this.slotDurationMinutes = slotDurationMinutes;
  }

  public Long getId() {
    return id;
  }

  public Interviewer getInterviewer() {
    return interviewer;
  }

  public DayOfWeek getDayOfWeek() {
    return dayOfWeek;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public int getSlotDurationMinutes() {
    return slotDurationMinutes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AvailabilityRule that)) return false;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}

