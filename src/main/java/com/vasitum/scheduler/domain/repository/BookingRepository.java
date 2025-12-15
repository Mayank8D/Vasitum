package com.vasitum.scheduler.domain.repository;

import com.vasitum.scheduler.domain.model.Booking;
import com.vasitum.scheduler.domain.model.Candidate;
import com.vasitum.scheduler.domain.model.Slot;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {
  @Lock(value = jakarta.persistence.LockModeType.OPTIMISTIC)
  @Query(
      """
      select b from Booking b
      join fetch b.candidate c
      join fetch b.slot s
      where b.id = :id
      """)
  Optional<Booking> findWithLockingById(@Param("id") Long id);

  Optional<Booking> findByCandidate(Candidate candidate);

  boolean existsByCandidateAndSlot(Candidate candidate, Slot slot);

  @Query(
      """
      select b from Booking b
      join fetch b.candidate c
      join fetch b.slot s
      where c.email = :email
      """)
  List<Booking> findByCandidateEmailWithRelations(@Param("email") String email);
}

