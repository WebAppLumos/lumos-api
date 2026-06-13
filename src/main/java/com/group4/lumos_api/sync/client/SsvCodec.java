package com.group4.lumos_api.sync.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Nexacro SSV(Smart Service Variant) 요청/응답 코덱.
 */
public final class SsvCodec {

    private static final char RS = '\u001e';
    private static final char US = '\u001f';

    private SsvCodec() {
    }

    public static String buildRequest(Map<String, String> fields) {
        StringBuilder sb = new StringBuilder("SSV:utf-8").append(RS);
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            sb.append(entry.getKey()).append('=').append(entry.getValue()).append(RS);
        }
        return sb.toString();
    }

    public static String buildRequestWithDataset(Map<String, String> headerFields,
                                                   String datasetName,
                                                   List<String> columnNames,
                                                   Map<String, String> rowValues) {
        StringBuilder sb = new StringBuilder("SSV:utf-8").append(RS);
        if (headerFields != null) {
            for (Map.Entry<String, String> entry : headerFields.entrySet()) {
                sb.append(entry.getKey()).append('=').append(entry.getValue()).append(RS);
            }
        }
        sb.append("Dataset:").append(datasetName).append(RS);
        sb.append("_RowType_").append(US);
        for (int i = 0; i < columnNames.size(); i++) {
            if (i > 0) {
                sb.append(US);
            }
            sb.append(columnNames.get(i)).append(":STRING(256)");
        }
        sb.append(RS);
        sb.append('N');
        for (String columnName : columnNames) {
            sb.append(US).append(rowValues.getOrDefault(columnName, ""));
        }
        sb.append(RS);
        return sb.toString();
    }

    public static Map<String, String> parseSimpleFields(String body) {
        Map<String, String> result = new LinkedHashMap<>();
        if (body == null || body.isBlank()) {
            return result;
        }
        String[] records = body.split(String.valueOf(RS));
        for (String record : records) {
            if (record.isBlank() || record.startsWith("Dataset:") || record.startsWith("_RowType_")) {
                continue;
            }
            int eq = record.indexOf('=');
            if (eq <= 0) {
                continue;
            }
            String key = record.substring(0, eq);
            String value = record.substring(eq + 1);
            if (key.contains(":")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }

    public static int parseErrorCode(String body) {
        if (body == null || body.isBlank()) {
            return -1;
        }
        for (String record : body.split(String.valueOf(RS))) {
            if (!record.startsWith("ErrorCode")) {
                continue;
            }
            int eq = record.indexOf('=');
            if (eq <= 0) {
                continue;
            }
            try {
                return Integer.parseInt(record.substring(eq + 1).trim());
            } catch (NumberFormatException ignored) {
                return -1;
            }
        }
        return -1;
    }

    public static String parseErrorMessage(String body) {
        if (body == null || body.isBlank()) {
            return "";
        }
        for (String record : body.split(String.valueOf(RS))) {
            if (!record.startsWith("ErrorMsg")) {
                continue;
            }
            int eq = record.indexOf('=');
            if (eq > 0) {
                return record.substring(eq + 1).trim();
            }
        }
        return "";
    }

    public static String parseDatasetRows(String body, String datasetName) {
        Map<String, String> row = parseDatasetFirstRowMap(body, datasetName);
        if (row.isEmpty()) {
            return "";
        }
        return "N" + US + String.join(String.valueOf(US), row.values());
    }

    /**
     * Dataset 첫 데이터 행을 컬럼명-값 맵으로 반환한다.
     */
    public static List<Map<String, String>> parseDatasetAllRows(String body, String datasetName) {
        int datasetStart = body.indexOf("Dataset:" + datasetName);
        if (datasetStart < 0) {
            return List.of();
        }

        String[] records = body.substring(datasetStart).split(String.valueOf(RS));
        String[] columnNames = null;
        List<Map<String, String>> rows = new ArrayList<>();

        for (String record : records) {
            if (record.isBlank() || record.startsWith("Dataset:")) {
                continue;
            }

            if (record.startsWith("_RowType_")) {
                String columnPart = record.substring("_RowType_".length());
                if (!columnPart.isEmpty() && columnPart.charAt(0) == US) {
                    columnPart = columnPart.substring(1);
                }
                if (containsColumnDefinition(columnPart)) {
                    columnNames = parseColumnNames(columnPart);
                }
                continue;
            }

            if (containsColumnDefinition(record)) {
                columnNames = parseColumnNames(record);
                continue;
            }

            if (columnNames == null || record.isEmpty()) {
                continue;
            }

            char rowType = record.charAt(0);
            if (rowType == 'N' || rowType == 'U' || rowType == 'D') {
                String[] values = record.split(String.valueOf(US));
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 1; i < values.length && i - 1 < columnNames.length; i++) {
                    row.put(columnNames[i - 1], values[i]);
                }
                rows.add(row);
            }
        }

        return rows;
    }

    public static Map<String, String> parseDatasetFirstRowMap(String body, String datasetName) {
        int datasetStart = body.indexOf("Dataset:" + datasetName);
        if (datasetStart < 0) {
            return Map.of();
        }

        String[] records = body.substring(datasetStart).split(String.valueOf(RS));
        String[] columnNames = null;

        for (String record : records) {
            if (record.isBlank() || record.startsWith("Dataset:")) {
                continue;
            }

            if (record.startsWith("_RowType_")) {
                String columnPart = record.substring("_RowType_".length());
                if (!columnPart.isEmpty() && columnPart.charAt(0) == US) {
                    columnPart = columnPart.substring(1);
                }
                if (containsColumnDefinition(columnPart)) {
                    columnNames = parseColumnNames(columnPart);
                }
                continue;
            }

            if (containsColumnDefinition(record)) {
                columnNames = parseColumnNames(record);
                continue;
            }

            if (columnNames == null || record.isEmpty()) {
                continue;
            }

            char rowType = record.charAt(0);
            if (rowType == 'N' || rowType == 'U' || rowType == 'D') {
                String[] values = record.split(String.valueOf(US));
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 1; i < values.length && i - 1 < columnNames.length; i++) {
                    row.put(columnNames[i - 1], values[i]);
                }
                return row;
            }
        }

        return Map.of();
    }

    public static char unitSeparator() {
        return US;
    }

    private static boolean containsColumnDefinition(String record) {
        String lower = record.toLowerCase();
        return lower.contains(":string") || lower.contains(":int") || lower.contains(":decimal");
    }

    private static String[] parseColumnNames(String record) {
        return Arrays.stream(record.split(String.valueOf(US)))
                .filter(value -> !value.isBlank())
                .map(SsvCodec::stripColumnType)
                .toArray(String[]::new);
    }

    private static String stripColumnType(String raw) {
        int colon = raw.indexOf(':');
        return colon > 0 ? raw.substring(0, colon) : raw;
    }
}
