package com.vasitum.scheduler.api;

import java.time.Instant;

public record ErrorResponse(Instant timestamp, String message) {
  public static ErrorResponse of(String message) {
    return new ErrorResponse(Instant.now(), message);
  }
}

