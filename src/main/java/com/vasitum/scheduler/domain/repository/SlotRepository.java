package com.vasitum.scheduler.domain.repository;

import com.vasitum.scheduler.domain.model.Interviewer;
import com.vasitum.scheduler.domain.model.Slot;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SlotRepository extends JpaRepository<Slot, Long> {
  @Query(
      """
      select new com.vasitum.scheduler.domain.repository.SlotProjection(
        s.id, i.id, s.startTime, s.endTime, s.capacity, s.bookedCount
      )
      from Slot s
      join s.interviewer i
      where s.startTime >= :from
        and (s.bookedCount < s.capacity)
      order by s.startTime asc
      """)
  List<SlotProjection> findAvailableFrom(@Param("from") LocalDateTime from, Pageable pageable);

  List<Slot> findByInterviewerAndStartTimeBetween(
      Interviewer interviewer, LocalDateTime start, LocalDateTime end);

  @Lock(value = jakarta.persistence.LockModeType.OPTIMISTIC)
  Optional<Slot> findWithLockingById(Long id);
}

