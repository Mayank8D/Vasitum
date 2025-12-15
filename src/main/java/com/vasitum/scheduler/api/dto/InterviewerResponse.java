package com.vasitum.scheduler.api.dto;

import com.vasitum.scheduler.domain.model.Interviewer;

public record InterviewerResponse(Long id, String name, int weeklyInterviewLimit) {
  public static InterviewerResponse from(Interviewer interviewer) {
    return new InterviewerResponse(
        interviewer.getId(), interviewer.getName(), interviewer.getWeeklyInterviewLimit());
  }
}

