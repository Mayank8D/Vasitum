package com.vasitum.scheduler.application;

import com.vasitum.scheduler.api.dto.BookingResponse;
import com.vasitum.scheduler.domain.repository.BookingRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BookingQueryService {
  private final BookingRepository bookingRepository;

  public BookingQueryService(BookingRepository bookingRepository) {
    this.bookingRepository = bookingRepository;
  }

  public List<BookingResponse> findByEmail(String email) {
    return bookingRepository.findByCandidateEmailWithRelations(email).stream()
        .map(BookingResponse::from)
        .toList();
  }
}

