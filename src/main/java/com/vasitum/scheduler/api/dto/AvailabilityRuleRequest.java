package com.vasitum.scheduler.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AvailabilityRuleRequest(
    @NotBlank String dayOfWeek,
    @NotBlank String startTime,
    @NotBlank String endTime,
    @Min(15) @Max(240) int slotDurationMinutes) {}

