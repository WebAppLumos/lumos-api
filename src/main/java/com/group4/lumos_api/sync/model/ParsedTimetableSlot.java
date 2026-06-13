package com.group4.lumos_api.sync.model;

import java.time.LocalTime;

/**
 * EDWARD 시간표에서 파싱한 수업 1칸(요일·교시 단위) 또는 온라인 수업.
 */
public record ParsedTimetableSlot(
        String title,
        String professor,
        String classroom,
        Short credit,
        boolean isOnline,
        short dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {
}
