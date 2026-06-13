package com.group4.lumos_api.sync.parser;

import com.group4.lumos_api.sync.client.SsvCodec;
import com.group4.lumos_api.sync.model.ParsedTimetableSlot;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseRegistrationParserTest {

    private final CourseRegistrationParser parser = new CourseRegistrationParser();

    @Test
    void parsesCreditFromCourseRegistrationSsv() {
        String body = """
                SSV:utf-8\u001e
                Dataset:DS_COUR530M01\u001e
                _RowType_\u001fscNm:STRING(256)\u001fpnt:STRING(256)\u001frespProfEmpNm:STRING(256)\u001flsnTmtablFormaSmryCtnt:STRING(256)\u001e
                N\u001f운영체제\u001f3\u001f박세진\u001f화13:30~15:20 목10:30~12:20(공1217)\u001e
                """;

        List<ParsedTimetableSlot> slots = parser.parseRows(
                SsvCodec.parseDatasetAllRows(body, "DS_COUR530M01"));

        assertEquals(2, slots.size());
        assertEquals((short) 3, slots.get(0).credit());
        assertEquals("운영체제", slots.get(0).title());
    }
}
