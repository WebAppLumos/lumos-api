package com.group4.lumos_api.sync.model;

import java.time.LocalDate;

/**
 * 학년도·학기 정보.
 */
public record AcademicTerm(int year, String termCode, String termLabel) {

    public static AcademicTerm of(int year, String termCode) {
        return new AcademicTerm(year, termCode, termLabel(termCode));
    }

    public static String termLabel(String termCode) {
        return switch (termCode) {
            case "1" -> "1학기";
            case "2" -> "2학기";
            case "3" -> "하계학기";
            case "4" -> "동계학기";
            default -> termCode + "학기";
        };
    }

    public static String guessRegularTermCode() {
        int month = LocalDate.now().getMonthValue();
        return (month >= 3 && month <= 8) ? "1" : "2";
    }
}
