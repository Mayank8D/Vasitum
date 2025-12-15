package com.vasitum.scheduler.api.dto;

import jakarta.validation.constraints.NotNull;

public record BookingUpdateRequest(@NotNull Long slotId) {}

