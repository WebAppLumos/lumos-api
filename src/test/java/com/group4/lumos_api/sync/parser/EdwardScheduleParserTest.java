package com.group4.lumos_api.sync.parser;

import com.group4.lumos_api.sync.model.ParsedTimetableSlot;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EdwardScheduleParserTest {

    @Test
    void parsesTwoDayScheduleWithSharedRoom() {
        List<ParsedTimetableSlot> slots = EdwardScheduleParser.parse(
                "소프트웨어공학",
                "사공상욱",
                "월12:00~13:15 수16:30~17:45(공1302)"
        );

        assertEquals(2, slots.size());
        assertEquals((short) 1, slots.get(0).dayOfWeek());
        assertEquals(LocalTime.of(12, 0), slots.get(0).startTime());
        assertEquals(LocalTime.of(13, 15), slots.get(0).endTime());
        assertEquals("공1302", slots.get(0).classroom());

        assertEquals((short) 3, slots.get(1).dayOfWeek());
        assertEquals(LocalTime.of(16, 30), slots.get(1).startTime());
        assertEquals(LocalTime.of(17, 45), slots.get(1).endTime());
    }

    @Test
    void parsesOperatingSystemsScheduleFromConfirmationSheet() {
        List<ParsedTimetableSlot> slots = EdwardScheduleParser.parse(
                "운영체제",
                "박세진",
                "화13:30~15:20 목10:30~12:20(공1217)"
        );

        assertEquals(2, slots.size());
        assertEquals((short) 2, slots.get(0).dayOfWeek());
        assertEquals(LocalTime.of(13, 30), slots.get(0).startTime());
        assertEquals(LocalTime.of(15, 20), slots.get(0).endTime());

        assertEquals((short) 4, slots.get(1).dayOfWeek());
        assertEquals(LocalTime.of(10, 30), slots.get(1).startTime());
        assertEquals(LocalTime.of(12, 20), slots.get(1).endTime());
        assertEquals("공1217", slots.get(1).classroom());
    }

    @Test
    void parsesSingleSlotSchedule() {
        ParsedTimetableSlot slot = EdwardScheduleParser.parse(
                "예배와찬양(3)",
                "곽은성",
                "수13:30~14:20(영354)"
        ).get(0);

        assertEquals((short) 3, slot.dayOfWeek());
        assertEquals(LocalTime.of(13, 30), slot.startTime());
        assertEquals(LocalTime.of(14, 20), slot.endTime());
        assertEquals("영354", slot.classroom());
    }

    @Test
    void parsesOnlineCourseWithoutScheduleTimes() {
        List<ParsedTimetableSlot> slots = EdwardScheduleParser.parse(
                "진로선택과자기계발",
                "",
                "원격수업(중복선택가능)",
                (short) 3
        );

        assertEquals(1, slots.size());
        assertTrue(slots.get(0).isOnline());
        assertEquals((short) 3, slots.get(0).credit());
        assertEquals("진로선택과자기계발", slots.get(0).title());
    }
}
