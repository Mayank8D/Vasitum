package com.vasitum.scheduler.application;

import com.vasitum.scheduler.domain.model.Booking;
import com.vasitum.scheduler.domain.model.BookingStatus;
import com.vasitum.scheduler.domain.model.Candidate;
import com.vasitum.scheduler.domain.model.Slot;
import com.vasitum.scheduler.domain.repository.BookingRepository;
import com.vasitum.scheduler.domain.repository.CandidateRepository;
import com.vasitum.scheduler.domain.repository.SlotRepository;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {
  private final BookingRepository bookingRepository;
  private final CandidateRepository candidateRepository;
  private final SlotRepository slotRepository;

  public BookingService(
      BookingRepository bookingRepository,
      CandidateRepository candidateRepository,
      SlotRepository slotRepository) {
    this.bookingRepository = bookingRepository;
    this.candidateRepository = candidateRepository;
    this.slotRepository = slotRepository;
  }

  @Transactional
  public Booking createBooking(String candidateEmail, Long slotId) {
    Candidate candidate =
        candidateRepository
            .findByEmail(candidateEmail)
            .orElseGet(() -> candidateRepository.save(new Candidate(candidateEmail)));

    bookingRepository
        .findByCandidate(candidate)
        .ifPresent(
            b -> {
              throw new ApiException(HttpStatus.CONFLICT, "Candidate already has a booking");
            });

    Slot slot =
        slotRepository
            .findWithLockingById(slotId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Slot not found"));

    if (slot.getStartTime().isBefore(LocalDateTime.now())) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Slot is in the past");
    }
    if (!slot.hasCapacity()) {
      throw new ApiException(HttpStatus.CONFLICT, "Slot already full");
    }

    slot.incrementBooking();
    Booking booking = new Booking(candidate, slot, BookingStatus.CONFIRMED);
    slotRepository.save(slot);
    return bookingRepository.save(booking);
  }

  @Transactional
  public Booking updateBooking(Long bookingId, Long newSlotId) {
    Booking booking =
        bookingRepository
            .findWithLockingById(bookingId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Booking not found"));

    Slot currentSlot = booking.getSlot();
    Slot newSlot =
        slotRepository
            .findWithLockingById(newSlotId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Slot not found"));

    if (currentSlot.getId().equals(newSlot.getId())) {
      return booking;
    }
    if (newSlot.getStartTime().isBefore(LocalDateTime.now())) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Slot is in the past");
    }
    if (!newSlot.hasCapacity()) {
      throw new ApiException(HttpStatus.CONFLICT, "Slot already full");
    }

    newSlot.incrementBooking();
    currentSlot.decrementBooking();
    booking.updateSlot(newSlot);
    booking.markConfirmed();

    slotRepository.save(currentSlot);
    slotRepository.save(newSlot);
    return bookingRepository.save(booking);
  }
}

