package com.vasitum.scheduler.domain.repository;

import com.vasitum.scheduler.domain.model.AvailabilityRule;
import com.vasitum.scheduler.domain.model.Interviewer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailabilityRuleRepository extends JpaRepository<AvailabilityRule, Long> {
  List<AvailabilityRule> findByInterviewer(Interviewer interviewer);
}

