package com.group4.lumos_api.sync.parser;

import com.group4.lumos_api.sync.model.ParsedTimetableSlot;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * EDWARD 강의시간 요약 문자열 파서.
 * 예: {@code 월12:00~13:15 수16:30~17:45(공1302)}, {@code 화13:30~15:20 목10:30~12:20(공1217)}
 */
public final class EdwardScheduleParser {

    private static final Pattern DAY_TIME = Pattern.compile(
            "(월|화|수|목|금)(\\d{1,2}:\\d{2})~(\\d{1,2}:\\d{2})");
    private static final Pattern TRAILING_ROOM = Pattern.compile("\\(([A-Za-z가-힣0-9]+)\\)\\s*$");

    private static final Map<String, Short> DAY_OF_WEEK = Map.of(
            "월", (short) 1,
            "화", (short) 2,
            "수", (short) 3,
            "목", (short) 4,
            "금", (short) 5
    );

    private EdwardScheduleParser() {
    }

    public static List<ParsedTimetableSlot> parse(String title, String professor, String scheduleText) {
        if (scheduleText == null || scheduleText.isBlank()) {
            return List.of();
        }

        String room = extractTrailingRoom(scheduleText);
        String timeText = scheduleText;
        if (room != null) {
            timeText = TRAILING_ROOM.matcher(scheduleText).replaceFirst("").trim();
        }

        List<ParsedTimetableSlot> slots = new ArrayList<>();
        Matcher matcher = DAY_TIME.matcher(timeText);
        while (matcher.find()) {
            Short dayOfWeek = DAY_OF_WEEK.get(matcher.group(1));
            if (dayOfWeek == null) {
                continue;
            }
            slots.add(new ParsedTimetableSlot(
                    title,
                    professor,
                    room != null ? room : "",
                    dayOfWeek,
                    parseTime(matcher.group(2)),
                    parseTime(matcher.group(3))
            ));
        }
        return slots;
    }

    public static String extractTrailingRoom(String scheduleText) {
        Matcher matcher = TRAILING_ROOM.matcher(scheduleText.trim());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static LocalTime parseTime(String raw) {
        String[] parts = raw.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return LocalTime.of(hour, minute);
    }
}
