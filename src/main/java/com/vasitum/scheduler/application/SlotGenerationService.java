package com.vasitum.scheduler.application;

import com.vasitum.scheduler.domain.model.AvailabilityRule;
import com.vasitum.scheduler.domain.model.Interviewer;
import com.vasitum.scheduler.domain.model.Slot;
import com.vasitum.scheduler.domain.repository.AvailabilityRuleRepository;
import com.vasitum.scheduler.domain.repository.InterviewerRepository;
import com.vasitum.scheduler.domain.repository.SlotRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SlotGenerationService {
  private static final int HORIZON_DAYS = 14;

  private final InterviewerRepository interviewerRepository;
  private final AvailabilityRuleRepository availabilityRuleRepository;
  private final SlotRepository slotRepository;

  public SlotGenerationService(
      InterviewerRepository interviewerRepository,
      AvailabilityRuleRepository availabilityRuleRepository,
      SlotRepository slotRepository) {
    this.interviewerRepository = interviewerRepository;
    this.availabilityRuleRepository = availabilityRuleRepository;
    this.slotRepository = slotRepository;
  }

  @Transactional
  public int generateForInterviewer(Long interviewerId) {
    Interviewer interviewer =
        interviewerRepository
            .findById(interviewerId)
            .orElseThrow(() -> new ApiException(org.springframework.http.HttpStatus.NOT_FOUND, "Interviewer not found"));

    List<AvailabilityRule> rules = availabilityRuleRepository.findByInterviewer(interviewer);
    if (rules.isEmpty()) {
      throw new ApiException(
          org.springframework.http.HttpStatus.BAD_REQUEST, "No availability rules for interviewer");
    }

    LocalDate today = LocalDate.now();
    LocalDate endDate = today.plusDays(HORIZON_DAYS);
    int created = 0;

    for (LocalDate date = today; !date.isAfter(endDate); date = date.plusDays(1)) {
      DayBucket bucket = weekBucket(date);
      int existingForWeek =
          slotRepository.findByInterviewerAndStartTimeBetween(
                  interviewer, bucket.weekStart, bucket.weekEnd.plusSeconds(1))
              .size();

      int remaining = interviewer.getWeeklyInterviewLimit() - existingForWeek;
      if (remaining <= 0) {
        continue;
      }

      for (AvailabilityRule rule : rules) {
        if (rule.getDayOfWeek() != date.getDayOfWeek()) continue;
        created += generateForDay(interviewer, date, rule, remaining);
        remaining = interviewer.getWeeklyInterviewLimit()
            - slotRepository.findByInterviewerAndStartTimeBetween(
                    interviewer, bucket.weekStart, bucket.weekEnd.plusSeconds(1))
                .size();
        if (remaining <= 0) break;
      }
    }
    return created;
  }

  private int generateForDay(
      Interviewer interviewer, LocalDate date, AvailabilityRule rule, int remainingForWeek) {
    int created = 0;
    LocalTime current = rule.getStartTime();
    while (current.plusMinutes(rule.getSlotDurationMinutes()).isBefore(rule.getEndTime())
        || current.plusMinutes(rule.getSlotDurationMinutes()).equals(rule.getEndTime())) {
      if (remainingForWeek <= 0) break;
      LocalDateTime start = date.atTime(current);
      LocalDateTime end = start.plusMinutes(rule.getSlotDurationMinutes());
      Slot slot = new Slot(interviewer, start, end, 1);
      try {
        slotRepository.save(slot);
        created++;
        remainingForWeek--;
      } catch (DataIntegrityViolationException ignored) {
        // slot already exists; skip
      }
      current = current.plusMinutes(rule.getSlotDurationMinutes());
    }
    return created;
  }

  private DayBucket weekBucket(LocalDate date) {
    LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
    LocalDate weekEnd = weekStart.plusDays(6);
    return new DayBucket(weekStart.atStartOfDay(), weekEnd.atTime(LocalTime.MAX));
  }

  private record DayBucket(LocalDateTime weekStart, LocalDateTime weekEnd) {}
}

