package com.vasitum.scheduler.domain.repository;

import com.vasitum.scheduler.domain.model.Candidate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
  Optional<Candidate> findByEmail(String email);
}

