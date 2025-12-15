package com.vasitum.scheduler.domain.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Candidate {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  protected Candidate() {}

  public Candidate(String email) {
    this.email = email;
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Candidate candidate)) return false;
    return Objects.equals(id, candidate.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}

