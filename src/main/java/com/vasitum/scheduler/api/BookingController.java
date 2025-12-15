package com.vasitum.scheduler.api;

import com.vasitum.scheduler.api.dto.BookingRequest;
import com.vasitum.scheduler.api.dto.BookingResponse;
import com.vasitum.scheduler.api.dto.BookingUpdateRequest;
import com.vasitum.scheduler.application.BookingQueryService;
import com.vasitum.scheduler.application.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
public class BookingController {
  private final BookingService bookingService;
  private final BookingQueryService bookingQueryService;

  public BookingController(BookingService bookingService, BookingQueryService bookingQueryService) {
    this.bookingService = bookingService;
    this.bookingQueryService = bookingQueryService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BookingResponse create(@Valid @RequestBody BookingRequest request) {
    return BookingResponse.from(
        bookingService.createBooking(request.candidateEmail(), request.slotId()));
  }

  @PutMapping("/{bookingId}")
  public BookingResponse update(
      @PathVariable("bookingId") Long bookingId,
      @Valid @RequestBody BookingUpdateRequest request) {
    return BookingResponse.from(bookingService.updateBooking(bookingId, request.slotId()));
  }

  @GetMapping
  public java.util.List<BookingResponse> listByEmail(@RequestParam("email") @Email String email) {
    return bookingQueryService.findByEmail(email);
  }
}

