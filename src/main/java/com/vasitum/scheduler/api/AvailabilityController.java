package com.vasitum.scheduler.api;

import com.vasitum.scheduler.api.dto.AvailabilityRuleRequest;
import com.vasitum.scheduler.api.dto.InterviewerRequest;
import com.vasitum.scheduler.api.dto.InterviewerResponse;
import com.vasitum.scheduler.application.ApiException;
import com.vasitum.scheduler.domain.model.AvailabilityRule;
import com.vasitum.scheduler.domain.model.Interviewer;
import com.vasitum.scheduler.domain.repository.AvailabilityRuleRepository;
import com.vasitum.scheduler.domain.repository.InterviewerRepository;
import jakarta.validation.Valid;
import java.time.DayOfWeek;
import java.time.LocalTime;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interviewers")
public class AvailabilityController {
  private final InterviewerRepository interviewerRepository;
  private final AvailabilityRuleRepository availabilityRuleRepository;

  public AvailabilityController(
      InterviewerRepository interviewerRepository,
      AvailabilityRuleRepository availabilityRuleRepository) {
    this.interviewerRepository = interviewerRepository;
    this.availabilityRuleRepository = availabilityRuleRepository;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public InterviewerResponse createInterviewer(@Valid @RequestBody InterviewerRequest request) {
    Interviewer interviewer =
        interviewerRepository.save(
            new Interviewer(request.name(), request.weeklyInterviewLimit()));
    return InterviewerResponse.from(interviewer);
  }

  @PostMapping("/{interviewerId}/availability")
  @ResponseStatus(HttpStatus.CREATED)
  public void addAvailability(
      @PathVariable("interviewerId") Long interviewerId,
      @Valid @RequestBody AvailabilityRuleRequest request) {
    Interviewer interviewer =
        interviewerRepository
            .findById(interviewerId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Interviewer not found"));

    AvailabilityRule rule =
        new AvailabilityRule(
            interviewer,
            DayOfWeek.valueOf(request.dayOfWeek().toUpperCase()),
            LocalTime.parse(request.startTime()),
            LocalTime.parse(request.endTime()),
            request.slotDurationMinutes());
    availabilityRuleRepository.save(rule);
  }
}

