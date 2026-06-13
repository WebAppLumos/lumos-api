package com.group4.lumos_api.sync.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SSO login_process HTML 응답에서 EDWARD 핸드오프 URL을 추출한다.
 */
final class SsoHandoffParser {

    private static final Pattern LOCATION_PATTERN = Pattern.compile(
            "location\\.(?:href|replace)\\s*=\\s*['\"]([^'\"]+)['\"]",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern META_REFRESH_PATTERN = Pattern.compile(
            "url=([^;\"'>\\s]+)",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern FORM_ACTION_PATTERN = Pattern.compile(
            "<form[^>]+action=\"([^\"]+)\"",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private SsoHandoffParser() {
    }

    static String extractRedirectUrl(String html) {
        if (html == null || html.isBlank()) {
            return null;
        }

        Matcher locationMatcher = LOCATION_PATTERN.matcher(html);
        if (locationMatcher.find()) {
            return locationMatcher.group(1).trim();
        }

        Matcher metaMatcher = META_REFRESH_PATTERN.matcher(html);
        if (metaMatcher.find()) {
            return metaMatcher.group(1).trim();
        }

        Matcher formMatcher = FORM_ACTION_PATTERN.matcher(html);
        if (formMatcher.find()) {
            return formMatcher.group(1).trim();
        }

        return null;
    }
}
