package com.vasitum.scheduler.domain.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Interviewer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Column(nullable = false)
  private int weeklyInterviewLimit;

  protected Interviewer() {}

  public Interviewer(String name, int weeklyInterviewLimit) {
    this.name = name;
    this.weeklyInterviewLimit = weeklyInterviewLimit;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getWeeklyInterviewLimit() {
    return weeklyInterviewLimit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Interviewer interviewer)) return false;
    return Objects.equals(id, interviewer.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}

