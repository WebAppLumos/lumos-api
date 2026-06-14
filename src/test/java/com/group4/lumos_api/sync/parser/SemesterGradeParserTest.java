package com.group4.lumos_api.sync.parser;

import com.group4.lumos_api.sync.client.SsvCodec;
import com.group4.lumos_api.sync.model.ParsedSemesterGrade;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SemesterGradeParserTest {

    @Test
    void parsesSemesterGradeRowsFromEdwardSsv() {
        String body = "SSV:utf-8\u001eErrorCode:int=0\u001eErrorMsg:string=SUCCESS\u001eDataset:DS_SCOR210M01\u001e"
                + "_RowType_\u001fyy:string(32)\u001ftmGbn:string(32)\u001ftmNm:string(32)\u001f"
                + "gvupInclAcqHp:bigdecimal(16)\u001faplyHp:bigdecimal(16)\u001f"
                + "avgScr:bigdecimal(16)\u001favgMrks:bigdecimal(16)\u001f"
                + "schaffWarnGbn:string(32)\u001fschaffWarnNm:string(32)\u001e"
                + "N\u001f2025\u001f2\u001f2학기\u001f18\u001f18\u001f0\u001f4\u001f0\u001f해당없음\u001e"
                + "N\u001f2025\u001f1\u001f1학기\u001f20\u001f20\u001f0\u001f4\u001f0\u001f해당없음\u001e"
                + "N\u001f2024\u001f2\u001f2학기\u001f17\u001f17\u001f0\u001f3.9643\u001f0\u001f해당없음\u001e";

        List<ParsedSemesterGrade> grades = SemesterGradeParser.parseRows(
                SsvCodec.parseDatasetAllRows(body, "DS_SCOR210M01"));

        assertEquals(3, grades.size());
        assertEquals(2024, grades.get(0).academicYear());
        assertEquals("2", grades.get(0).termCode());
        assertEquals(17, grades.get(0).completedCredits());
        assertEquals(3.9643, grades.get(0).gpa(), 0.0001);
        assertFalse(grades.get(0).academicWarning());

        assertEquals(2025, grades.get(2).academicYear());
        assertEquals("2", grades.get(2).termCode());
        assertEquals(18, grades.get(2).completedCredits());
        assertEquals(4.0, grades.get(2).gpa(), 0.0001);
        assertTrue(grades.stream().noneMatch(ParsedSemesterGrade::academicWarning));
    }
}
