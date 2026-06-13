package com.group4.lumos_api.sync.model;

import java.time.LocalTime;

/**
 * EDWARD 시간표에서 파싱한 수업 1칸(요일·교시 단위).
 */
public record ParsedTimetableSlot(
        String title,
        String professor,
        String classroom,
        short dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {
}
