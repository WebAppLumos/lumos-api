package com.group4.lumos_api.calendar;

import com.group4.lumos_api.calendar.entity.CalendarEvent;
import com.group4.lumos_api.calendar.entity.EventCategory;
import com.group4.lumos_api.calendar.entity.EventPriority;
import com.group4.lumos_api.calendar.repository.CalendarEventRepository;
import com.group4.lumos_api.user.entity.Users;
import com.group4.lumos_api.user.repository.UsersRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CalendarDataInitializer implements CommandLineRunner {

    private final CalendarEventRepository repository;
    private final UsersRepository usersRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println(">>> CalendarDataInitializer: 관리자(admin) 계정 상태를 점검합니다...");
        
        // 💡 꼬인 데이터를 방지하기 위해, 만약 admin 아이디가 없더라도 
        // 학번 '0000000'이 이미 DB에 존재한다면 그걸 admin으로 재활용하거나 새로 만듭니다.
        Users adminUser = usersRepository.findById("admin").orElse(null);
        
        if (adminUser == null) {
            // 혹시 ID는 다른데 학번만 '0000000'인 찌꺼기 데이터가 있는지 확인해서 지워버립니다.
            entityManager.createNativeQuery("DELETE FROM users WHERE student_number = '0000000' OR email = 'admin@kmu.ac.kr'").executeUpdate();
            
            System.out.println(">>> CalendarDataInitializer: 깨끗한 상태로 admin 계정을 새로 생성합니다.");
            Users newAdmin = Users.builder()
                    .userId("admin")
                    .email("admin@kmu.ac.kr")
                    .name("학사관리자")
                    .phoneNumber("053-123-4567")
                    .major("교무처")
                    .grade(1)
                    .studentNumber("0000000") // 이제 중복 에러 안 남
                    .profileImageUrl(null)
                    .build();
            adminUser = usersRepository.save(newAdmin);
        }

        System.out.println(">>> CalendarDataInitializer: 계명대학교 2026-2027 전체 상세 일정을 초기화합니다...");

        // 학사 일정(admin)만 삭제하고 사용자 일정은 보존 (TRUNCATE 대신 DELETE 사용)
        entityManager.createNativeQuery("DELETE FROM calendar WHERE user_id = 'admin'").executeUpdate();

        List<CalendarEvent> events = new ArrayList<>();

        // --- 2026년 3월 ---
        events.add(create("2026-03-01", "1학기 개시일 (삼일절)", null, EventPriority.LOW, EventCategory.HOLIDAY, adminUser));
        events.add(create("2026-03-02", "삼일절 대체공휴일", null, EventPriority.LOW, EventCategory.HOLIDAY, adminUser));
        events.add(create("2026-03-03", "1학기 개강 (수업 시작)", "2026학년도 제1학기 수업 시작", EventPriority.HIGH, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-03-03", "1학기 수강신청 확인 및 변경", "3/3 ~ 3/5", EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-03-30", "1학기 수업일수 1/4선", null, EventPriority.LOW, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-03-30", "고난 주간 시작", "3/30 ~ 4/4", EventPriority.LOW, EventCategory.ACADEMIC, adminUser));

        // --- 2026년 4월 ---
        events.add(create("2026-04-05", "부활절", null, EventPriority.LOW, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-04-06", "1학기 수업일수 1/3선", null, EventPriority.LOW, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-04-09", "부활절 예배", null, EventPriority.LOW, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-04-24", "1학기 수업일수 1/2선", null, EventPriority.LOW, EventCategory.ACADEMIC, adminUser));

        // --- 2026년 5월 ---
        events.add(create("2026-05-01", "노동절 (공휴일)", null, EventPriority.LOW, EventCategory.HOLIDAY, adminUser));
        events.add(create("2026-05-04", "교육 실습 시작", "5/4 ~ 5/29", EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-05-05", "어린이날 (공휴일)", null, EventPriority.LOW, EventCategory.HOLIDAY, adminUser));
        events.add(create("2026-05-11", "1학기 수업일수 2/3선", null, EventPriority.LOW, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-05-20", "창립기념일 (휴업일)", "계명대학교 창립기념일", EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-05-25", "부처님오신날 대체공휴일", null, EventPriority.LOW, EventCategory.HOLIDAY, adminUser));

        // --- 2026년 6월 ---
        events.add(create("2026-06-03", "2026 지방선거 (휴강)", null, EventPriority.LOW, EventCategory.HOLIDAY, adminUser));
        events.add(create("2026-06-09", "보강일 (5/1 노동절 휴강분)", null, EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-06-10", "보강일 (5/5 어린이날 휴강분)", null, EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-06-11", "보강일 (5/20 창립기념일 휴강분)", null, EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-06-12", "보강일 (5/25 부처님오신날 대체휴일 휴강분)", null, EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-06-15", "보강일 (6/3 지방선거 휴강분)", null, EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-06-16", "1학기 정기시험 (기말고사)", "6/16 ~ 6/22", EventPriority.HIGH, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-06-23", "하계방학 및 계절학기 시작", null, EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));

        // --- 2026년 7월 ---
        events.add(create("2026-07-01", "2학기 재입학 신청 (1차)", "7/1 ~ 7/7", EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-07-01", "2학기 복학 신청 (1차)", "7/1 ~ 7/15", EventPriority.HIGH, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-07-17", "제헌절 (공휴일)", null, EventPriority.LOW, EventCategory.HOLIDAY, adminUser));

        // --- 2026년 8월 ---
        events.add(create("2026-08-17", "광복절 대체공휴일", null, EventPriority.LOW, EventCategory.HOLIDAY, adminUser));
        events.add(create("2026-08-20", "2025학년도 후기 학부 학위수여일 (졸업)", null, EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-08-20", "2025학년도 후기 대학원 학위수여일 (졸업)", null, EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-08-24", "2학기 등록금 수납 기간", "8/24 ~ 8/27", EventPriority.HIGH, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-08-26", "2학기 개강 예배", null, EventPriority.LOW, EventCategory.ACADEMIC, adminUser));

        // --- 2026년 9월 ---
        events.add(create("2026-09-01", "2학기 개시일 (개강)", "2026학년도 제2학기 수업 시작", EventPriority.HIGH, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-09-24", "추석 연휴 시작", "9/24 ~ 9/26", EventPriority.LOW, EventCategory.HOLIDAY, adminUser));
        events.add(create("2026-09-28", "2학기 수업일수 1/4선", null, EventPriority.LOW, EventCategory.ACADEMIC, adminUser));

        // --- 2026년 10월 ---
        events.add(create("2026-10-05", "개천절 대체공휴일", null, EventPriority.LOW, EventCategory.HOLIDAY, adminUser));
        events.add(create("2026-10-06", "2학기 수업일수 1/3선", null, EventPriority.LOW, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-10-09", "한글날 (공휴일)", null, EventPriority.LOW, EventCategory.HOLIDAY, adminUser));
        events.add(create("2026-10-23", "2학기 수업일수 1/2선", null, EventPriority.LOW, EventCategory.ACADEMIC, adminUser));

        // --- 2026년 11월 ---
        events.add(create("2026-11-09", "2학기 수업일수 2/3선", null, EventPriority.LOW, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-11-19", "추수감사 예배", null, EventPriority.LOW, EventCategory.ACADEMIC, adminUser));

        // --- 2026년 12월 ---
        events.add(create("2026-12-08", "보강일 (9/24 추석 휴일 휴강분)", null, EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-12-09", "보강일 (9/25 추석 휴일 휴강분)", null, EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-12-10", "보강일 (10/5 개천절 대체휴일 휴강분)", null, EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-12-11", "보강일 (10/9 한글날 휴강분)", null, EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-12-11", "성탄 축하 예배", null, EventPriority.LOW, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-12-14", "2학기 정기시험 (기말고사)", "12/14 ~ 12/18", EventPriority.HIGH, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-12-21", "동계방학 및 계절학기 시작", null, EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));
        events.add(create("2026-12-25", "성탄절 (공휴일)", null, EventPriority.LOW, EventCategory.HOLIDAY, adminUser));

        // --- 2027년 1월 ---
        events.add(create("2027-01-01", "신정", null, EventPriority.LOW, EventCategory.HOLIDAY, adminUser));
        events.add(create("2027-01-04", "2027학년도 1학기 재입학 신청 (1차)", "1/4 ~ 1/8", EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));
        events.add(create("2027-01-04", "2027학년도 1학기 복학 신청 (1차)", "1/4 ~ 1/15", EventPriority.HIGH, EventCategory.ACADEMIC, adminUser));

        // --- 2027년 2월 ---
        events.add(create("2027-02-18", "2026학년도 전기 학부 학위수여식 (졸업)", null, EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));
        events.add(create("2027-02-19", "2026학년도 전기 대학원 학위수여식 (졸업)", null, EventPriority.MEDIUM, EventCategory.ACADEMIC, adminUser));
        events.add(create("2027-02-22", "2027학년도 1학기 등록금 수납", "2/22 ~ 2/25", EventPriority.HIGH, EventCategory.ACADEMIC, adminUser));
        events.add(create("2027-02-23", "전체 교수회", null, EventPriority.LOW, EventCategory.ACADEMIC, adminUser));
        events.add(create("2027-02-24", "2027학년도 1학기 개강 예배", null, EventPriority.LOW, EventCategory.ACADEMIC, adminUser));
        events.add(create("2027-02-26", "2027학년도 입학식", null, EventPriority.HIGH, EventCategory.ACADEMIC, adminUser));

        repository.saveAll(events);
        System.out.println(">>> CalendarDataInitializer: 총 " + events.size() + "개의 상세 일정이 고유 계정 [admin] 소유로 ID 1번부터 안정적으로 등록되었습니다.");
    }

    private CalendarEvent create(String date, String title, String content, EventPriority priority, EventCategory category, Users adminUser) {
        return CalendarEvent.builder()
                .date(LocalDate.parse(date))
                .title("[학사] " + title)
                .content(content)
                .category(category)
                .priority(priority)
                .user(adminUser)
                .isCompleted(false)
                .build();
    }
}