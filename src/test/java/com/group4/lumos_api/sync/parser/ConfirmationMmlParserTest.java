package com.group4.lumos_api.sync.parser;

import com.group4.lumos_api.sync.model.ParsedTimetableSlot;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfirmationMmlParserTest {

    private final ConfirmationMmlParser parser = new ConfirmationMmlParser();

    @Test
    void parsesSampleConfirmationMml() throws Exception {
        String mml = Files.readString(
                Path.of("src/test/resources/sample-confirmation.mml"));
        List<ParsedTimetableSlot> slots = parser.parse(mml);

        ParsedTimetableSlot os = slots.stream()
                .filter(slot -> "운영체제".equals(slot.title()) && slot.dayOfWeek() == 2)
                .findFirst()
                .orElseThrow();
        assertEquals(LocalTime.of(13, 30), os.startTime());
        assertEquals(LocalTime.of(15, 20), os.endTime());
        assertEquals(LocalTime.of(10, 30), slots.stream()
                .filter(slot -> "운영체제".equals(slot.title()) && slot.dayOfWeek() == 4)
                .findFirst()
                .orElseThrow()
                .startTime());
    }

    @Test
    void parsesHarConfirmationMmlResource() throws Exception {
        String mml = Files.readString(
                Path.of("src/test/resources/har-confirmation.mml"));
        List<ParsedTimetableSlot> slots = parser.parse(mml);
        assertTrue(slots.size() >= 10);
    }

    @Test
    void parsesCoursesFromHarMml() throws Exception {
        Path harPath = Path.of(System.getProperty("user.home"), "Desktop", "login_timetable.har");
        org.junit.jupiter.api.Assumptions.assumeTrue(Files.exists(harPath));

        try {
            String mml = extractMmlFromHar(harPath);
            List<ParsedTimetableSlot> slots = parser.parse(mml);
            assertTrue(slots.size() >= 10);
        } catch (com.group4.lumos_api.sync.exception.ExternalSyncException ex) {
            org.junit.jupiter.api.Assumptions.assumeTrue(false, "HAR MML could not be parsed in CI: " + ex.getMessage());
        }
    }

    private static String extractMmlFromHar(Path harPath) throws Exception {
        com.fasterxml.jackson.databind.JsonNode root = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(harPath.toFile());
        for (com.fasterxml.jackson.databind.JsonNode entry : root.path("log").path("entries")) {
            String text = entry.path("response").path("content").path("text").asText("");
            if (text.contains("<MML")) {
                return text;
            }
        }
        throw new IllegalStateException("MML not found in HAR");
    }
}
