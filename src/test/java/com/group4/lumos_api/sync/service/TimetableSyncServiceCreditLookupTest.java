package com.group4.lumos_api.sync.service;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TimetableSyncServiceCreditLookupTest {

    @Test
    void lookupCreditByTitle_matchesPartialEnglishCourseTitle() {
        Map<String, Short> creditByTitle = new LinkedHashMap<>();
        creditByTitle.put(
                "INTRODUCTION TO OBJECT ORIENTED PROGRAMMING(영어강의)",
                (short) 3);
        creditByTitle.put("INTRODUCTION TO OBJECT ORIENTED", (short) 3);

        assertEquals(
                (short) 3,
                TimetableSyncService.lookupCreditByTitle(
                        creditByTitle,
                        "INTRODUCTION TO OBJECT ORIENTED"));
    }

    @Test
    void lookupCreditByTitle_returnsNullWhenNoMatch() {
        Map<String, Short> creditByTitle = Map.of("운영체제", (short) 3);

        assertNull(TimetableSyncService.lookupCreditByTitle(creditByTitle, "알고리즘"));
    }
}
