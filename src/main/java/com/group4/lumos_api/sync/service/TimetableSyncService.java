package com.group4.lumos_api.sync.service;

import com.group4.lumos_api.common.exception.BadRequestException;
import com.group4.lumos_api.common.exception.NotFoundException;
import com.group4.lumos_api.course.entity.Course;
import com.group4.lumos_api.course.repository.CourseRepository;
import com.group4.lumos_api.entry.entity.TimetableEntry;
import com.group4.lumos_api.entry.repository.TimetableEntryRepository;
import com.group4.lumos_api.semester.entity.Semester;
import com.group4.lumos_api.semester.repository.SemesterRepository;
import com.group4.lumos_api.sync.client.EdwardSessionClient;
import com.group4.lumos_api.sync.dto.TimetableImportRequest;
import com.group4.lumos_api.sync.dto.TimetableSyncRequest;
import com.group4.lumos_api.sync.dto.TimetableSyncResponse;
import com.group4.lumos_api.sync.exception.ExternalSyncException;
import com.group4.lumos_api.sync.model.AcademicTerm;
import com.group4.lumos_api.sync.model.EdwardSession;
import com.group4.lumos_api.sync.model.ParsedTimetableSlot;
import com.group4.lumos_api.sync.parser.ConfirmationMmlParser;
import com.group4.lumos_api.sync.parser.CourseRegistrationParser;
import com.group4.lumos_api.sync.parser.EdwardScheduleParser;
import com.group4.lumos_api.sync.parser.MmlTimetableParser;
import com.group4.lumos_api.sync.source.ExternalSyncSource;
import com.group4.lumos_api.timetable.entity.Timetable;
import com.group4.lumos_api.timetable.repository.TimetableRepository;
import com.group4.lumos_api.user.entity.Users;
import com.group4.lumos_api.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TimetableSyncService implements ExternalSyncSource<TimetableSyncResponse> {

    private static final String SOURCE_ID = "edward-timetable";
    private static final String TIMETABLE_TITLE = "EDWARD 동기화";

    private final EdwardSessionClient edwardSessionClient;
    private final CourseRegistrationParser courseRegistrationParser;
    private final ConfirmationMmlParser confirmationMmlParser;
    private final MmlTimetableParser mmlTimetableParser;
    private final UsersRepository usersRepository;
    private final SemesterRepository semesterRepository;
    private final TimetableRepository timetableRepository;
    private final CourseRepository courseRepository;
    private final TimetableEntryRepository entryRepository;
    private final EdwardStudentNumberGuard edwardStudentNumberGuard;

    @Override
    public String sourceId() {
        return SOURCE_ID;
    }

    public TimetableSyncResponse syncFromEdward(String userId, TimetableSyncRequest request) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        edwardStudentNumberGuard.ensureMatchesOrAssign(user, request.getEdwardLoginName());

        char[] password = request.getEdwardPassword().toCharArray();
        try (EdwardSession session = edwardSessionClient.login(request.getEdwardLoginName(), password)) {
            EdwardSessionClient.YearTerm yearTerm = edwardSessionClient.resolveYearTerm(
                    session, request.getYear(), request.getTermCode());

            edwardSessionClient.warmUpGlioSession(session);
            edwardSessionClient.recordPersonalDataAccess(
                    session,
                    yearTerm.year(),
                    yearTerm.termCode(),
                    request.getEdwardLoginName(),
                    yearTerm.termLabel()
            );

            var rows = edwardSessionClient.fetchCourseRegistrationList(
                    session,
                    yearTerm.year(),
                    yearTerm.termCode(),
                    request.getEdwardLoginName()
            );

            List<ParsedTimetableSlot> slots = courseRegistrationParser.parseRows(rows);
            return upsertTimetable(user, toAcademicTerm(yearTerm), slots);
        } finally {
            java.util.Arrays.fill(password, '\0');
        }
    }

    public TimetableSyncResponse importFromMml(String userId, TimetableImportRequest request) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        edwardStudentNumberGuard.ensureMatchesOrAssign(user, request.getStudentNumber());

        if ((request.getMml() == null || request.getMml().isBlank())
                && (request.getSsv() == null || request.getSsv().isBlank())
                && (request.getConfirmationMml() == null || request.getConfirmationMml().isBlank())) {
            throw new BadRequestException("시간표 데이터가 비어 있습니다.");
        }

        ConfirmationMetadata confirmationMetadata = mergeExtensionCredits(
                buildConfirmationMetadata(request.getConfirmationMml()),
                request.getCreditByTitle());

        List<ParsedTimetableSlot> slots = resolveImportSlots(request, confirmationMetadata);

        int year = request.getYear() != null
                ? request.getYear()
                : java.time.Year.now().getValue();
        String termCode = request.getTermCode() != null && !request.getTermCode().isBlank()
                ? request.getTermCode()
                : AcademicTerm.guessRegularTermCode();
        AcademicTerm term = AcademicTerm.of(year, termCode);

        return upsertTimetable(user, term, slots);
    }

    private List<ParsedTimetableSlot> resolveImportSlots(TimetableImportRequest request,
                                                       ConfirmationMetadata confirmationMetadata) {
        if (request.getSsv() != null && !request.getSsv().isBlank()) {
            List<Map<String, String>> rows = com.group4.lumos_api.sync.client.SsvCodec.parseDatasetAllRows(
                    request.getSsv(), "DS_COUR530M01");
            if (!rows.isEmpty()) {
                try {
                    List<ParsedTimetableSlot> fromSsv = applyConfirmationMetadata(
                            courseRegistrationParser.parseRows(rows),
                            confirmationMetadata);
                    if (!fromSsv.isEmpty()) {
                        return fromSsv;
                    }
                } catch (ExternalSyncException ignored) {
                    // SSV에 dataset은 있지만 시간표 슬롯이 없을 때 confirmation MML로 fallback
                }
            }
        }

        String mml = firstNonBlankMml(request.getConfirmationMml(), request.getMml());
        if (mml == null || mml.isBlank() || (!mml.contains("<MML") && !mml.contains("<DOCUMENT"))) {
            if (request.getSsv() != null && !request.getSsv().isBlank()) {
                throw new ExternalSyncException("수강신청 확인 데이터에서 시간표 정보를 찾지 못했습니다.");
            }
            throw new BadRequestException("유효한 시간표 데이터가 아닙니다.");
        }

        return applyConfirmationMetadata(parseImportedMml(mml), confirmationMetadata);
    }

    @Override
    public TimetableSyncResponse sync(String userId, SyncCredentials credentials) {
        TimetableSyncRequest request = new TimetableSyncRequest();
        request.setEdwardLoginName(credentials.loginName());
        request.setEdwardPassword(new String(credentials.password()));
        return syncFromEdward(userId, request);
    }

    private TimetableSyncResponse upsertTimetable(Users user, AcademicTerm term,
                                                  List<ParsedTimetableSlot> slots) {
        String semesterTitle = term.year() + "학년도 " + term.termLabel();
        Semester semester = findOrCreateSemester(user, semesterTitle, term.year(), term.termCode());
        Timetable timetable = findOrCreateTimetable(semester);

        entryRepository.deleteAllByTimetable_Id(timetable.getId());

        List<ParsedTimetableSlot> mergedSlots = mergeSlotsByCourseAndDay(slots);

        Map<String, Course> courseCache = new HashMap<>();
        int entryCount = 0;

        for (ParsedTimetableSlot slot : mergedSlots) {
            Course course = courseCache.computeIfAbsent(
                    courseKey(slot.title(), slot.professor()),
                    key -> findOrCreateCourse(semester, slot)
            );

            TimetableEntry entry = new TimetableEntry();
            entry.setTimetable(timetable);
            entry.setCourse(course);
            if (slot.isOnline()) {
                entry.setDayOfWeek(null);
                entry.setStartTime(null);
                entry.setEndTime(null);
            } else {
                entry.setDayOfWeek(slot.dayOfWeek());
                entry.setStartTime(slot.startTime());
                entry.setEndTime(slot.endTime());
            }
            entryRepository.save(entry);
            entryCount++;
        }

        return new TimetableSyncResponse(
                semester.getId(),
                timetable.getId(),
                semesterTitle,
                courseCache.size(),
                entryCount
        );
    }

    private Semester findOrCreateSemester(Users user, String title, int year, String termCode) {
        Optional<Semester> existing = semesterRepository.findAllByUser_UserIdOrderBySortOrderAscIdAsc(user.getUserId()).stream()
                .filter(semester -> title.equals(semester.getTitle()))
                .findFirst();
        if (existing.isPresent()) {
            return existing.get();
        }

        LocalDate startDate = termCode.equals("2")
                ? LocalDate.of(year, 9, 1)
                : LocalDate.of(year, 3, 1);
        LocalDate endDate = termCode.equals("2")
                ? LocalDate.of(year, 12, 31)
                : LocalDate.of(year, 8, 31);

        Semester semester = new Semester();
        semester.setUser(user);
        semester.setTitle(title);
        semester.setStartDate(startDate);
        semester.setEndDate(endDate);
        semester.setIsActive(true);
        int nextOrder = semesterRepository.findAllByUser_UserIdOrderBySortOrderAscIdAsc(user.getUserId())
                .stream()
                .mapToInt(Semester::getSortOrder)
                .max()
                .orElse(-1) + 1;
        semester.setSortOrder(nextOrder);
        return semesterRepository.save(semester);
    }

    private Timetable findOrCreateTimetable(Semester semester) {
        return timetableRepository.findAllBySemester_IdOrderBySortOrderAscIdAsc(semester.getId()).stream()
                .filter(t -> TIMETABLE_TITLE.equals(t.getTitle()))
                .findFirst()
                .orElseGet(() -> {
                    Timetable timetable = new Timetable();
                    timetable.setSemester(semester);
                    timetable.setTitle(TIMETABLE_TITLE);
                    int nextOrder = timetableRepository.findAllBySemester_IdOrderBySortOrderAscIdAsc(semester.getId())
                            .stream()
                            .mapToInt(Timetable::getSortOrder)
                            .max()
                            .orElse(-1) + 1;
                    timetable.setSortOrder(nextOrder);
                    return timetableRepository.save(timetable);
                });
    }

    private Course findOrCreateCourse(Semester semester, ParsedTimetableSlot slot) {
        Optional<Course> existing = courseRepository.findAllBySemester_IdOrderByIdAsc(semester.getId()).stream()
                .filter(course -> slot.title().equals(course.getTitle()))
                .filter(course -> slot.professor().equals(course.getProfessor()))
                .findFirst();
        if (existing.isPresent()) {
            Course course = existing.get();
            course.setClassroom(slot.isOnline() ? "온라인" : slot.classroom());
            course.setIsOnline(slot.isOnline());
            if (slot.credit() != null) {
                course.setCredit(slot.credit());
            }
            return courseRepository.save(course);
        }

        Course course = new Course();
        course.setSemester(semester);
        course.setTitle(slot.title());
        course.setProfessor(slot.professor());
        course.setClassroom(slot.isOnline() ? "온라인" : slot.classroom());
        course.setCredit(slot.credit());
        course.setIsOnline(slot.isOnline());
        return courseRepository.save(course);
    }

    private static AcademicTerm toAcademicTerm(EdwardSessionClient.YearTerm yearTerm) {
        return AcademicTerm.of(yearTerm.year(), yearTerm.termCode());
    }

    private static String courseKey(String title, String professor) {
        return title + "||" + professor;
    }

    private static String firstNonBlankMml(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }

    private record ConfirmationMetadata(
            Map<String, Short> creditByCourseKey,
            Map<String, Short> creditByTitle,
            Map<String, String> professorByTitle
    ) {
        private static ConfirmationMetadata empty() {
            return new ConfirmationMetadata(Map.of(), Map.of(), Map.of());
        }
    }

    private ConfirmationMetadata mergeExtensionCredits(ConfirmationMetadata metadata,
                                                      Map<String, Short> extensionCredits) {
        if (extensionCredits == null || extensionCredits.isEmpty()) {
            return metadata;
        }

        Map<String, Short> creditByTitle = new LinkedHashMap<>(metadata.creditByTitle());
        for (Map.Entry<String, Short> entry : extensionCredits.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank() || entry.getValue() == null) {
                continue;
            }
            registerCreditByTitle(creditByTitle, entry.getKey(), entry.getValue());
        }

        return new ConfirmationMetadata(metadata.creditByCourseKey(), creditByTitle, metadata.professorByTitle());
    }

    private static void registerCreditByTitle(Map<String, Short> creditByTitle, String title, Short credit) {
        String normalized = normalizeTitle(title);
        if (normalized.isBlank()) {
            return;
        }

        creditByTitle.putIfAbsent(normalized, credit);

        int programmingIndex = normalized.indexOf(" PROGRAMMING");
        if (programmingIndex > 0) {
            creditByTitle.putIfAbsent(normalized.substring(0, programmingIndex).trim(), credit);
        }
    }

    static Short lookupCreditByTitle(Map<String, Short> creditByTitle, String slotTitle) {
        if (creditByTitle.isEmpty() || slotTitle == null || slotTitle.isBlank()) {
            return null;
        }

        String normalized = normalizeTitle(slotTitle);
        Short direct = creditByTitle.get(normalized);
        if (direct != null) {
            return direct;
        }

        Short bestMatch = null;
        int bestOverlap = -1;
        for (Map.Entry<String, Short> entry : creditByTitle.entrySet()) {
            String key = entry.getKey();
            if (!key.startsWith(normalized) && !normalized.startsWith(key)) {
                continue;
            }

            int overlap = Math.min(key.length(), normalized.length());
            if (overlap > bestOverlap) {
                bestOverlap = overlap;
                bestMatch = entry.getValue();
            }
        }

        return bestMatch;
    }

    private ConfirmationMetadata buildConfirmationMetadata(String confirmationMml) {
        if (confirmationMml == null || confirmationMml.isBlank()) {
            return ConfirmationMetadata.empty();
        }

        try {
            Map<String, Short> creditByCourseKey = new LinkedHashMap<>();
            Map<String, Short> creditByTitle = new LinkedHashMap<>();
            Map<String, String> professorByTitle = new LinkedHashMap<>();

            for (ParsedTimetableSlot slot : confirmationMmlParser.parse(confirmationMml)) {
                String normalizedTitle = normalizeTitle(slot.title());

                if (slot.credit() != null) {
                    creditByCourseKey.putIfAbsent(courseKey(slot.title(), slot.professor()), slot.credit());
                    registerCreditByTitle(creditByTitle, normalizedTitle, slot.credit());
                }
                if (slot.professor() != null && !slot.professor().isBlank()) {
                    professorByTitle.putIfAbsent(normalizedTitle, slot.professor());
                }
            }

            return new ConfirmationMetadata(creditByCourseKey, creditByTitle, professorByTitle);
        } catch (ExternalSyncException ignored) {
            return ConfirmationMetadata.empty();
        }
    }

    private List<ParsedTimetableSlot> applyConfirmationMetadata(List<ParsedTimetableSlot> slots,
                                                                ConfirmationMetadata metadata) {
        if (metadata.creditByCourseKey().isEmpty()
                && metadata.creditByTitle().isEmpty()
                && metadata.professorByTitle().isEmpty()) {
            return slots;
        }

        return slots.stream()
                .map(slot -> enrichSlotFromConfirmation(slot, metadata))
                .toList();
    }

    private ParsedTimetableSlot enrichSlotFromConfirmation(ParsedTimetableSlot slot,
                                                           ConfirmationMetadata metadata) {
        Short credit = slot.credit();
        if (credit == null) {
            credit = metadata.creditByCourseKey().get(courseKey(slot.title(), slot.professor()));
            if (credit == null) {
                credit = lookupCreditByTitle(metadata.creditByTitle(), slot.title());
            }
        }

        String professor = slot.professor();
        if (professor == null || professor.isBlank()) {
            professor = metadata.professorByTitle().getOrDefault(normalizeTitle(slot.title()), "");
        }

        if (java.util.Objects.equals(credit, slot.credit())
                && java.util.Objects.equals(professor, slot.professor())) {
            return slot;
        }

        return new ParsedTimetableSlot(
                slot.title(),
                professor,
                slot.classroom(),
                credit,
                slot.isOnline(),
                slot.dayOfWeek(),
                slot.startTime(),
                slot.endTime()
        );
    }

    private static String normalizeTitle(String title) {
        return title == null ? "" : title.trim();
    }

    private List<ParsedTimetableSlot> parseImportedMml(String mml) {
        if (isConfirmationMml(mml)) {
            try {
                return confirmationMmlParser.parse(mml);
            } catch (ExternalSyncException confirmationError) {
                try {
                    return mmlTimetableParser.parse(mml);
                } catch (ExternalSyncException ignored) {
                    throw confirmationError;
                }
            }
        }
        return mmlTimetableParser.parse(mml);
    }

    private static boolean isConfirmationMml(String mml) {
        String compact = mml.replace(" ", "");
        return compact.contains("수강신청확인")
                || compact.contains("과목코드")
                || mml.contains("unst0040");
    }

    private static List<ParsedTimetableSlot> mergeSlotsByCourseAndDay(List<ParsedTimetableSlot> slots) {
        record CourseDayKey(String courseKey, short dayOfWeek) {
        }

        Map<CourseDayKey, ParsedTimetableSlot> merged = new LinkedHashMap<>();
        for (ParsedTimetableSlot slot : slots) {
            CourseDayKey key = new CourseDayKey(courseKey(slot.title(), slot.professor()), slot.dayOfWeek());
            merged.merge(key, slot, TimetableSyncService::mergeTwoSlots);
        }

        return merged.values().stream()
                .sorted(Comparator
                        .comparing(ParsedTimetableSlot::dayOfWeek)
                        .thenComparing(ParsedTimetableSlot::startTime)
                        .thenComparing(ParsedTimetableSlot::title))
                .toList();
    }

    private static ParsedTimetableSlot mergeTwoSlots(ParsedTimetableSlot left, ParsedTimetableSlot right) {
        if (left.isOnline() || right.isOnline()) {
            Short credit = left.credit() != null ? left.credit() : right.credit();
            return EdwardScheduleParser.onlineCourse(left.title(), left.professor(), credit);
        }

        LocalTime start = left.startTime().isBefore(right.startTime()) ? left.startTime() : right.startTime();
        LocalTime end = left.endTime().isAfter(right.endTime()) ? left.endTime() : right.endTime();
        String classroom = !left.classroom().isBlank() ? left.classroom() : right.classroom();
        Short credit = left.credit() != null ? left.credit() : right.credit();
        return new ParsedTimetableSlot(
                left.title(),
                left.professor(),
                classroom,
                credit,
                false,
                left.dayOfWeek(),
                start,
                end
        );
    }
}
