package com.vasitum.scheduler.api.dto;

import com.vasitum.scheduler.domain.model.Booking;
import com.vasitum.scheduler.domain.model.BookingStatus;

public record BookingResponse(Long id, Long slotId, String candidateEmail, BookingStatus status) {
  public static BookingResponse from(Booking booking) {
    return new BookingResponse(
        booking.getId(),
        booking.getSlot().getId(),
        booking.getCandidate().getEmail(),
        booking.getStatus());
  }
}

