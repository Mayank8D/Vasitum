package com.vasitum.scheduler.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record BookingRequest(@Email String candidateEmail, @NotNull Long slotId) {}

