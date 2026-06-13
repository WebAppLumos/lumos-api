package com.group4.lumos_api.sync.parser;

import com.group4.lumos_api.sync.exception.ExternalSyncException;
import com.group4.lumos_api.sync.model.ParsedTimetableSlot;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * EDWARD 수강신청확인(DS_COUR530M01) 응답 파서.
 */
@Component
public class CourseRegistrationParser {

    public List<ParsedTimetableSlot> parseRows(List<Map<String, String>> rows) {
        List<ParsedTimetableSlot> slots = new ArrayList<>();

        for (Map<String, String> row : rows) {
            String title = firstNonBlank(row, "scNm", "sc_nm");
            String professor = firstNonBlank(row, "respProfEmpNm");
            String schedule = firstNonBlank(
                    row,
                    "lsnTmtablFormaSmryCtnt",
                    "openHpStr",
                    "openHp",
                    "scSmryCtnt"
            );
            if (title.isBlank()) {
                continue;
            }
            slots.addAll(EdwardScheduleParser.parse(title, professor, schedule));
        }

        slots.sort(Comparator
                .comparing(ParsedTimetableSlot::dayOfWeek)
                .thenComparing(ParsedTimetableSlot::startTime)
                .thenComparing(ParsedTimetableSlot::title));

        if (slots.isEmpty()) {
            throw new ExternalSyncException("수강신청 확인 데이터에서 시간표 정보를 찾지 못했습니다.");
        }
        return slots;
    }

    private static String firstNonBlank(Map<String, String> row, String... keys) {
        for (String key : keys) {
            String value = row.getOrDefault(key, "").trim();
            if (!value.isBlank()) {
                return value;
            }
        }
        return "";
    }
}
