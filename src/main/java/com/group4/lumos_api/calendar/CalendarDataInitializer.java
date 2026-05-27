package com.group4.lumos_api.calendar;

import com.group4.lumos_api.calendar.entity.CalendarEvent;
import com.group4.lumos_api.calendar.repository.CalendarEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CalendarDataInitializer implements CommandLineRunner {

    private final CalendarEventRepository repository;

    @Override
    public void run(String... args) throws Exception {
        // DB에 학사 일정(studentId is null)이 하나도 없을 때만 초기 데이터 삽입
        if (repository.findByStudentIdIsNullOrStudentId(null).isEmpty()) {
            List<CalendarEvent> academicEvents = List.of(
                CalendarEvent.builder()
                        .title("[학사] 2026학년도 1학기 개강")
                        .content("즐거운 마음으로 개강을 맞이하세요!")
                        .date(LocalDate.of(2026, 3, 2))
                        .studentId(null)
                        .build(),
                CalendarEvent.builder()
                        .title("[학사] 1학기 중간고사 기간")
                        .content("공부한 만큼 좋은 결과 있기를 응원합니다.")
                        .date(LocalDate.of(2026, 4, 20))
                        .studentId(null)
                        .build(),
                CalendarEvent.builder()
                        .title("[학사] 봄 축제 (Lumos Festival)")
                        .content("캠퍼스 낭만을 즐기는 시간!")
                        .date(LocalDate.of(2026, 5, 15))
                        .studentId(null)
                        .build(),
                CalendarEvent.builder()
                        .title("[학사] 1학기 종강")
                        .content("한 학기 동안 수고 많으셨습니다!")
                        .date(LocalDate.of(2026, 6, 22))
                        .studentId(null)
                        .build()
            );

            repository.saveAll(academicEvents);
            System.out.println(">>> 학사 일정 초기 데이터가 삽입되었습니다.");
        }
    }
}
