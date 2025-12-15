package com.vasitum.scheduler.domain.repository;

import java.time.LocalDateTime;

public record SlotProjection(
    Long id,
    Long interviewerId,
    LocalDateTime startTime,
    LocalDateTime endTime,
    int capacity,
    int bookedCount) {}

