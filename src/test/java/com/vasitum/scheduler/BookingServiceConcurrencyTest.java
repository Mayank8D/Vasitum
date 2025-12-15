package com.vasitum.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.vasitum.scheduler.application.BookingService;
import com.vasitum.scheduler.application.SlotGenerationService;
import com.vasitum.scheduler.domain.model.AvailabilityRule;
import com.vasitum.scheduler.domain.model.Interviewer;
import com.vasitum.scheduler.domain.repository.AvailabilityRuleRepository;
import com.vasitum.scheduler.domain.repository.InterviewerRepository;
import com.vasitum.scheduler.domain.repository.SlotRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BookingServiceConcurrencyTest {

  @Autowired private BookingService bookingService;
  @Autowired private SlotGenerationService slotGenerationService;
  @Autowired private InterviewerRepository interviewerRepository;
  @Autowired private AvailabilityRuleRepository availabilityRuleRepository;
  @Autowired private SlotRepository slotRepository;

  private Long slotId;

  @BeforeEach
  void setup() {
    availabilityRuleRepository.deleteAll();
    slotRepository.deleteAll();
    interviewerRepository.deleteAll();

    Interviewer interviewer = interviewerRepository.save(new Interviewer("Alex", 5));
    AvailabilityRule rule =
        availabilityRuleRepository.save(
            new AvailabilityRule(
                interviewer,
                LocalDate.now().getDayOfWeek(),
                LocalTime.now().plusMinutes(5),
                LocalTime.now().plusMinutes(65),
                30));
    slotGenerationService.generateForInterviewer(interviewer.getId());
    slotId = slotRepository.findAll().get(0).getId();
  }

  @Test
  void onlyOneBookingShouldWin() throws Exception {
    var executor = Executors.newFixedThreadPool(2);
    var latch = new CountDownLatch(2);

    var results =
        List.of(
            executor.submit(
                () -> {
                  latch.countDown();
                  latch.await();
                  return bookingService.createBooking("a@x.com", slotId);
                }),
            executor.submit(
                () -> {
                  latch.countDown();
                  latch.await();
                  return bookingService.createBooking("b@x.com", slotId);
                }));

    int success = 0;
    int failures = 0;
    for (var f : results) {
      try {
        f.get();
        success++;
      } catch (Exception e) {
        failures++;
      }
    }

    assertThat(success).isEqualTo(1);
    assertThat(failures).isEqualTo(1);
    assertThatThrownBy(() -> bookingService.createBooking("c@x.com", slotId))
        .hasMessageContaining("Slot already full");
  }
}

