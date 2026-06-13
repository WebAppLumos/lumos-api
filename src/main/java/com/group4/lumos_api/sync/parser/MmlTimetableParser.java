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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Crownix MML XML(개인시간표 리포트) 파서.
 */
@Component
public class MmlTimetableParser {

    private static final Pattern PROFESSOR_LINE = Pattern.compile("^(.+?)\\s+([A-Za-z가-힣0-9]+\\d+)$");
    private static final int GRID_LEFT = 1088;
    private static final int GRID_TOP = 1416;
    private static final int COL_WIDTH = 1068;
    private static final int ROW_HEIGHT = 301;

    private static final Map<Integer, LocalTime[]> SLOT_TIMES = buildSlotTimes();

    public List<ParsedTimetableSlot> parse(String mmlXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document document = factory.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(mmlXml.getBytes(StandardCharsets.UTF_8)));

            NodeList tlNodes = document.getElementsByTagName("TL");
            Map<String, CellContent> cells = new HashMap<>();

            for (int i = 0; i < tlNodes.getLength(); i++) {
                Element tl = (Element) tlNodes.item(i);
                String text = tl.getTextContent().trim();
                if (text.isBlank() || isIgnoredText(text)) {
                    continue;
                }

                int le = parseIntAttr(tl, "le");
                int to = parseIntAttr(tl, "to");
                if (le < GRID_LEFT || to < GRID_TOP) {
                    continue;
                }

                String cellKey = cellKey(le, to);
                CellContent content = cells.computeIfAbsent(cellKey, k -> new CellContent(dayOfWeek(le), slotIndex(to)));
                if (PROFESSOR_LINE.matcher(text).matches()) {
                    content.professorLine = text;
                } else {
                    content.title = text;
                }
            }

            List<ParsedTimetableSlot> slots = new ArrayList<>();
            for (CellContent cell : cells.values()) {
                if (cell.title == null || cell.professorLine == null) {
                    continue;
                }
                Matcher matcher = PROFESSOR_LINE.matcher(cell.professorLine);
                if (!matcher.matches()) {
                    continue;
                }
                LocalTime[] times = SLOT_TIMES.get(cell.slotIndex);
                if (times == null) {
                    continue;
                }
                slots.add(new ParsedTimetableSlot(
                        cell.title,
                        matcher.group(1).trim(),
                        matcher.group(2).trim(),
                        cell.dayOfWeek,
                        times[0],
                        times[1]
                ));
            }

            slots.sort(Comparator
                    .comparing(ParsedTimetableSlot::dayOfWeek)
                    .thenComparing(ParsedTimetableSlot::startTime)
                    .thenComparing(ParsedTimetableSlot::title));

            if (slots.isEmpty()) {
                throw new ExternalSyncException("시간표에서 수업 정보를 찾지 못했습니다.");
            }
            return slots;
        } catch (ExternalSyncException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalSyncException("시간표 리포트 파싱에 실패했습니다.", e);
        }
    }

    private static boolean isIgnoredText(String text) {
        return text.startsWith("*")
                || text.contains("학년도")
                || text.contains("강의시간")
                || text.contains("수업시간표")
                || text.contains("확인 바람");
    }

    private static String cellKey(int le, int to) {
        return dayOfWeek(le) + ":" + slotIndex(to);
    }

    private static short dayOfWeek(int le) {
        int col = Math.min(5, Math.max(0, (le - GRID_LEFT) / COL_WIDTH));
        return (short) (col + 1);
    }

    private static int slotIndex(int to) {
        return Math.max(0, (to - GRID_TOP) / ROW_HEIGHT);
    }

    private static int parseIntAttr(Element element, String name) {
        String value = element.getAttribute(name);
        if (value == null || value.isBlank()) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    private static Map<Integer, LocalTime[]> buildSlotTimes() {
        Map<Integer, LocalTime[]> map = new HashMap<>();
        String[][] rows = {
                {"08:00", "08:30"}, {"08:30", "09:00"},
                {"09:00", "09:30"}, {"09:30", "10:00"},
                {"10:00", "10:30"}, {"10:30", "11:00"},
                {"11:00", "11:30"}, {"11:30", "12:00"},
                {"12:00", "12:30"}, {"12:30", "13:00"},
                {"13:00", "13:30"}, {"13:30", "14:00"},
                {"14:00", "14:30"}, {"14:30", "15:00"},
                {"15:00", "15:30"}, {"15:30", "16:00"},
                {"16:00", "16:30"}, {"16:30", "17:00"},
                {"17:00", "17:30"}, {"17:30", "18:00"},
                {"18:00", "18:30"}, {"18:30", "19:00"},
                {"19:00", "19:30"}, {"19:30", "20:00"},
                {"20:00", "20:30"}, {"20:30", "21:00"},
                {"21:00", "21:30"}, {"21:30", "22:00"},
                {"22:00", "22:30"}, {"22:30", "23:00"},
                {"23:00", "23:30"}, {"23:30", "00:00"},
        };
        for (int i = 0; i < rows.length; i++) {
            map.put(i, new LocalTime[]{
                    LocalTime.parse(rows[i][0]),
                    LocalTime.parse(rows[i][1])
            });
        }
        return map;
    }

    private static class CellContent {
        private final short dayOfWeek;
        private final int slotIndex;
        private String title;
        private String professorLine;

        private CellContent(short dayOfWeek, int slotIndex) {
            this.dayOfWeek = dayOfWeek;
            this.slotIndex = slotIndex;
        }
    }
}
