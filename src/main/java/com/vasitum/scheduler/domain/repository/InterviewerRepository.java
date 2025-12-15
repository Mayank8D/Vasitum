package com.vasitum.scheduler.domain.repository;

import com.vasitum.scheduler.domain.model.Interviewer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewerRepository extends JpaRepository<Interviewer, Long> {}

