package com.vasitum.scheduler.application;

import com.vasitum.scheduler.domain.repository.SlotRepository;
import com.vasitum.scheduler.domain.repository.SlotProjection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class SlotQueryService {
  private static final int DEFAULT_LIMIT = 20;
  private final SlotRepository slotRepository;

  public SlotQueryService(SlotRepository slotRepository) {
    this.slotRepository = slotRepository;
  }

  public SlotPage listAvailable(LocalDateTime cursor, Integer limit) {
    int size = limit == null || limit <= 0 ? DEFAULT_LIMIT : Math.min(limit, 100);
    LocalDateTime startAfter = cursor == null ? LocalDateTime.now() : cursor;
    List<SlotProjection> slots =
        slotRepository.findAvailableFrom(startAfter, PageRequest.of(0, size + 1));
    boolean hasMore = slots.size() > size;
    List<SlotProjection> page = hasMore ? slots.subList(0, size) : slots;
    Optional<LocalDateTime> nextCursor =
        hasMore ? Optional.of(page.get(page.size() - 1).startTime()) : Optional.empty();
    return new SlotPage(page, nextCursor);
  }

  public record SlotPage(List<SlotProjection> slots, Optional<LocalDateTime> nextCursor) {}
}

