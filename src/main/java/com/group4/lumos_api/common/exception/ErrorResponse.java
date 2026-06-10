package com.group4.lumos_api.common.exception;

import java.time.LocalDateTime;

/**
 * API 오류 응답 본문 표준 형식.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message
) {
}
