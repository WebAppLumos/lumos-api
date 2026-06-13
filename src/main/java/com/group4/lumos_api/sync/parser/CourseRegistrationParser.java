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
            String professor = firstNonBlank(row, "respProfEmpNm", "respProfNm", "profNm", "profEmpNm");
            Short credit = parseCredit(row);
            String schedule = firstNonBlank(
                    row,
                    "lsnTmtablFormaSmryCtnt",
                    "openHpStr",
                    "openHp",
                    "scSmryCtnt"
            );
            if (schedule.isBlank()) {
                schedule = firstOnlineHint(row);
            }
            if (title.isBlank()) {
                continue;
            }
            List<ParsedTimetableSlot> rowSlots = EdwardScheduleParser.parse(title, professor, schedule, credit);
            if (rowSlots.isEmpty()) {
                continue;
            }
            slots.addAll(rowSlots);
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

    private static String firstOnlineHint(Map<String, String> row) {
        for (String value : row.values()) {
            if (EdwardScheduleParser.isOnlineSchedule(value)) {
                return value.trim();
            }
        }
        return "";
    }

    private static Short parseCredit(Map<String, String> row) {
        String raw = firstNonBlank(
                row,
                "pnt",
                "scPnt",
                "cmpsPnt",
                "cptnPnt",
                "lsnPnt",
                "cmpsScCnt",
                "cptnScCnt",
                "credit"
        );
        if (raw.isBlank()) {
            raw = row.entrySet().stream()
                    .filter(entry -> looksLikeCreditColumn(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .findFirst()
                    .orElse("");
        }
        if (raw.isBlank()) {
            return null;
        }

        try {
            return Short.parseShort(raw.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static boolean looksLikeCreditColumn(String key) {
        if (key == null || key.isBlank()) {
            return false;
        }

        String normalized = key.toLowerCase();
        if (normalized.contains("prof")
                || normalized.contains("tmtabl")
                || normalized.contains("openhp")
                || normalized.contains("smry")
                || normalized.contains("scnm")
                || normalized.contains("stuno")
                || normalized.contains("yy")
                || normalized.contains("tmgbn")) {
            return false;
        }

        return normalized.contains("pnt") || normalized.contains("credit");
    }
}
