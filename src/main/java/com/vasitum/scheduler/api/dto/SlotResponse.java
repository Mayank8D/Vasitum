package com.vasitum.scheduler.api.dto;

import java.time.LocalDateTime;
import com.vasitum.scheduler.domain.repository.SlotProjection;

public record SlotResponse(
    Long id, Long interviewerId, LocalDateTime startTime, LocalDateTime endTime, int remaining) {
  public static SlotResponse fromProjection(SlotProjection p) {
    return new SlotResponse(
        p.id(),
        p.interviewerId(),
        p.startTime(),
        p.endTime(),
        p.capacity() - p.bookedCount());
  }
}

