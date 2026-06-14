package com.group4.lumos_api.sync.parser;

import com.group4.lumos_api.sync.model.ParsedSemesterGrade;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class SemesterGradeParser {

    private static final Map<String, Integer> TERM_SORT_ORDER = Map.of(
            "1", 1,
            "3", 2,
            "2", 3,
            "4", 4
    );

    private SemesterGradeParser() {
    }

    public static List<ParsedSemesterGrade> parseRows(List<Map<String, String>> rows) {
        return rows.stream()
                .map(SemesterGradeParser::parseRow)
                .sorted(Comparator
                        .comparingInt(ParsedSemesterGrade::academicYear)
                        .thenComparingInt(grade -> TERM_SORT_ORDER.getOrDefault(grade.termCode(), 99)))
                .toList();
    }

    private static ParsedSemesterGrade parseRow(Map<String, String> row) {
        int academicYear = parseInt(row.get("yy"), 0);
        String termCode = firstNonBlank(row.get("tmGbn"));
        String termName = firstNonBlank(row.get("tmNm"), termLabel(termCode));
        int completedCredits = parseInt(firstNonBlank(
                row.get("gvupInclAcqHp"),
                row.get("cptnAllwHp"),
                row.get("gvupExcpAcqHp")
        ), 0);
        int registeredCredits = parseInt(row.get("aplyHp"), 0);
        double gpa = parseGpa(row);
        boolean academicWarning = isAcademicWarning(
                row.get("schaffWarnGbn"),
                row.get("schaffWarnNm")
        );

        return new ParsedSemesterGrade(
                academicYear,
                termCode,
                termName,
                completedCredits,
                registeredCredits,
                gpa,
                academicWarning
        );
    }

    private static double parseGpa(Map<String, String> row) {
        // Edward 학기성적 화면은 avgMrks(평균평점)를 표시하고 avgScr은 0인 경우가 많다.
        for (String key : List.of("avgMrks", "avgMrksF", "avgScr", "avgScrF")) {
            double value = parseDouble(row.get(key), -1.0);
            if (value >= 0.0) {
                return value;
            }
        }
        return 0.0;
    }

    private static boolean isAcademicWarning(String warnCode, String warnName) {
        String code = firstNonBlank(warnCode);
        if (!code.isEmpty() && !"0".equals(code)) {
            return true;
        }
        String name = firstNonBlank(warnName);
        return !name.isEmpty()
                && !"해당없음".equals(name)
                && !"없음".equals(name);
    }

    private static String termLabel(String termCode) {
        return switch (termCode) {
            case "1" -> "1학기";
            case "2" -> "2학기";
            case "3" -> "하계학기";
            case "4" -> "동계학기";
            default -> termCode + "학기";
        };
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }

    private static int parseInt(String value, int fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return (int) Math.round(Double.parseDouble(value.trim()));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static double parseDouble(String value, double fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
