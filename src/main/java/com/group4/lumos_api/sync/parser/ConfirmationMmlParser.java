package com.group4.lumos_api.sync.parser;

import com.group4.lumos_api.sync.exception.ExternalSyncException;
import com.group4.lumos_api.sync.model.ParsedTimetableSlot;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * EDWARD 수강신청확인서 Crownix MML 파서.
 */
@Component
public class ConfirmationMmlParser {

    private static final Pattern COURSE_CODE = Pattern.compile("^\\d{5}-\\d{1,2}$");
    private static final Pattern SCHEDULE_PART = Pattern.compile(
            ".*(\\d{1,2}:\\d{2}~\\d{1,2}:\\d{2}).*");
    private static final int COLUMN_TOLERANCE = 160;
    private static final int TIME_ROW_OFFSET = 400;
    private static final int TIME_ROW_BEFORE = 220;

    private static final Set<String> IGNORED_VALUES = Set.of(
            "강의", "실습", "강의/실습", "원격수업(중복선택가능)", "재수강", "여부"
    );

    public List<ParsedTimetableSlot> parse(String mmlXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document document = factory.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(mmlXml.getBytes(StandardCharsets.UTF_8)));

            NodeList tlNodes = document.getElementsByTagName("TL");
            Map<Integer, Map<Integer, String>> rows = new HashMap<>();

            for (int i = 0; i < tlNodes.getLength(); i++) {
                Element tl = (Element) tlNodes.item(i);
                String text = tl.getTextContent().trim();
                if (text.isBlank() || isIgnoredHeader(text)) {
                    continue;
                }

                int le = parseIntAttr(tl, "le");
                int to = parseIntAttr(tl, "to");
                if (to <= 0) {
                    continue;
                }

                rows.computeIfAbsent(to, key -> new HashMap<>()).put(le, text);
            }

            ColumnLayout columns = detectColumns(rows);

            List<ParsedTimetableSlot> slots = new ArrayList<>();
            List<Integer> rowTops = rows.keySet().stream().sorted().toList();
            for (int rowTop : rowTops) {
                Map<Integer, String> cells = rows.get(rowTop);
                String courseCode = findClosestMatching(cells, columns.codeCol(), COURSE_CODE);
                if (courseCode == null) {
                    continue;
                }

                String title = findClosestValidText(cells, columns.nameCol(), ConfirmationMmlParser::isCourseTitle);
                if (title == null) {
                    continue;
                }

                String professor = findClosestValidText(cells, columns.profCol(), ConfirmationMmlParser::isProfessorName);
                if (professor == null) {
                    professor = "";
                }

                Short credit = parseCredit(cells, columns.creditCol());

                String schedule = collectScheduleText(rows, rowTops, rowTop, columns.timeCol());
                if (schedule.isBlank()) {
                    schedule = collectScheduleTextLoose(rows, rowTops, rowTop);
                }
                slots.addAll(EdwardScheduleParser.parse(title, professor, schedule, credit));
            }

            slots.sort(Comparator
                    .comparing(ParsedTimetableSlot::dayOfWeek)
                    .thenComparing(ParsedTimetableSlot::startTime)
                    .thenComparing(ParsedTimetableSlot::title));

            if (slots.isEmpty()) {
                throw new ExternalSyncException("수강신청 확인서에서 시간표 정보를 찾지 못했습니다.");
            }
            return slots;
        } catch (ExternalSyncException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalSyncException("수강신청 확인서 파싱에 실패했습니다.", e);
        }
    }

    private static ColumnLayout detectColumns(Map<Integer, Map<Integer, String>> rows) {
        ColumnLayout layout = ColumnLayout.defaults();
        for (Map<Integer, String> cells : rows.values()) {
            for (Map.Entry<Integer, String> cell : cells.entrySet()) {
                String text = normalizeHeader(cell.getValue().trim());
                switch (text) {
                    case "과목코드" -> layout = layout.withCodeCol(cell.getKey());
                    case "과목명" -> layout = layout.withNameCol(cell.getKey());
                    case "학점" -> layout = layout.withCreditCol(cell.getKey());
                    case "담당교수" -> layout = layout.withProfCol(cell.getKey());
                    case "강의시간" -> layout = layout.withTimeCol(cell.getKey());
                    default -> {
                    }
                }
            }
        }
        return layout;
    }

    private static String collectScheduleText(Map<Integer, Map<Integer, String>> rows, List<Integer> rowTops,
                                              int rowTop, int timeColumn) {
        StringBuilder schedule = new StringBuilder();
        for (int extraTop : rowTops) {
            if (extraTop < rowTop - TIME_ROW_BEFORE || extraTop > rowTop + TIME_ROW_OFFSET) {
                continue;
            }
            Map<Integer, String> cells = rows.get(extraTop);
            if (cells == null) {
                continue;
            }

            for (Map.Entry<Integer, String> cell : cells.entrySet()) {
                String text = cell.getValue().trim();
                if (!looksLikeSchedulePart(text)) {
                    continue;
                }
                if (Math.abs(cell.getKey() - timeColumn) <= COLUMN_TOLERANCE || extraTop != rowTop) {
                    schedule.append(text);
                }
            }
        }
        return schedule.toString().trim();
    }

    private static String collectScheduleTextLoose(Map<Integer, Map<Integer, String>> rows, List<Integer> rowTops,
                                                   int rowTop) {
        StringBuilder schedule = new StringBuilder();
        for (int extraTop : rowTops) {
            if (extraTop < rowTop - TIME_ROW_BEFORE || extraTop > rowTop + TIME_ROW_OFFSET) {
                continue;
            }
            Map<Integer, String> cells = rows.get(extraTop);
            if (cells == null) {
                continue;
            }
            for (String text : cells.values()) {
                String trimmed = text.trim();
                if (looksLikeSchedulePart(trimmed)) {
                    schedule.append(trimmed);
                }
            }
        }
        return schedule.toString().trim();
    }

    private static String normalizeHeader(String text) {
        return text.replace(" ", "");
    }

    private static boolean looksLikeSchedulePart(String text) {
        if (!SCHEDULE_PART.matcher(text).matches()) {
            return false;
        }
        return text.contains("~");
    }

    private static Short parseCredit(Map<Integer, String> cells, int creditColumn) {
        String creditText = findClosestMatching(cells, creditColumn, Pattern.compile("^\\d+$"));
        if (creditText == null) {
            return null;
        }

        try {
            return Short.parseShort(creditText);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static String findClosestMatching(Map<Integer, String> cells, int targetColumn, Pattern pattern) {
        String best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (Map.Entry<Integer, String> cell : cells.entrySet()) {
            int distance = Math.abs(cell.getKey() - targetColumn);
            if (distance > COLUMN_TOLERANCE) {
                continue;
            }
            String text = cell.getValue().trim();
            if (!pattern.matcher(text).matches()) {
                continue;
            }
            if (distance < bestDistance) {
                bestDistance = distance;
                best = text;
            }
        }
        return best;
    }

    private static String findClosestValidText(Map<Integer, String> cells, int targetColumn,
                                               java.util.function.Predicate<String> validator) {
        String best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (Map.Entry<Integer, String> cell : cells.entrySet()) {
            int distance = Math.abs(cell.getKey() - targetColumn);
            if (distance > COLUMN_TOLERANCE) {
                continue;
            }
            String text = cell.getValue().trim();
            if (!validator.test(text)) {
                continue;
            }
            if (distance < bestDistance) {
                bestDistance = distance;
                best = text;
            }
        }
        return best;
    }

    private static boolean isProfessorName(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        if (IGNORED_VALUES.contains(text) || COURSE_CODE.matcher(text).matches()) {
            return false;
        }
        return !text.matches("^\\d+$");
    }

    private static boolean isCourseTitle(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        if (IGNORED_VALUES.contains(text) || COURSE_CODE.matcher(text).matches()) {
            return false;
        }
        if (text.matches("^\\d+$")) {
            return false;
        }
        return !Set.of("전공선택", "균형교양", "교양선택", "타전공").contains(text);
    }

    private static boolean isIgnoredHeader(String text) {
        return text.startsWith("*")
                || text.contains("학년도")
                || text.contains("확인 바람")
                || text.equals("$/#");
    }

    private static int parseIntAttr(Element element, String name) {
        String value = element.getAttribute(name);
        if (value == null || value.isBlank()) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    private record ColumnLayout(int codeCol, int nameCol, int creditCol, int profCol, int timeCol) {
        private static ColumnLayout defaults() {
            return new ColumnLayout(1311, 1958, 4027, 4356, 5813);
        }

        private ColumnLayout withCodeCol(int codeCol) {
            return new ColumnLayout(codeCol, nameCol, creditCol, profCol, timeCol);
        }

        private ColumnLayout withNameCol(int nameCol) {
            return new ColumnLayout(codeCol, nameCol, creditCol, profCol, timeCol);
        }

        private ColumnLayout withCreditCol(int creditCol) {
            return new ColumnLayout(codeCol, nameCol, creditCol, profCol, timeCol);
        }

        private ColumnLayout withProfCol(int profCol) {
            return new ColumnLayout(codeCol, nameCol, creditCol, profCol, timeCol);
        }

        private ColumnLayout withTimeCol(int timeCol) {
            return new ColumnLayout(codeCol, nameCol, creditCol, profCol, timeCol);
        }
    }
}
