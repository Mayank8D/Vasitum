package com.vasitum.scheduler.api;

import com.vasitum.scheduler.api.dto.SlotResponse;
import com.vasitum.scheduler.application.SlotGenerationService;
import com.vasitum.scheduler.application.SlotQueryService;
import com.vasitum.scheduler.application.SlotQueryService.SlotPage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
public class SlotController {
  private final SlotGenerationService slotGenerationService;
  private final SlotQueryService slotQueryService;

  public SlotController(
      SlotGenerationService slotGenerationService, SlotQueryService slotQueryService) {
    this.slotGenerationService = slotGenerationService;
    this.slotQueryService = slotQueryService;
  }

  @PostMapping("/interviewers/{interviewerId}/slots/generate")
  public Map<String, Integer> generate(@PathVariable("interviewerId") Long interviewerId) {
    int created = slotGenerationService.generateForInterviewer(interviewerId);
    return Map.of("created", created);
  }

  @GetMapping("/slots")
  public Map<String, Object> list(
      @RequestParam(name = "cursor", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime cursor,
      @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit) {
    SlotPage page = slotQueryService.listAvailable(cursor, limit);
    List<SlotResponse> slots = page.slots().stream().map(SlotResponse::fromProjection).toList();
    return Map.of("slots", slots, "nextCursor", page.nextCursor().orElse(null));
  }
}

