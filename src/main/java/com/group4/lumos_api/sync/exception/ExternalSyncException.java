package com.group4.lumos_api.sync.exception;

/**
 * EDWARD 등 외부 학교 시스템 연동 실패 시 사용한다.
 */
public class ExternalSyncException extends RuntimeException {

    public ExternalSyncException(String message) {
        super(message);
    }

    public ExternalSyncException(String message, Throwable cause) {
        super(message, cause);
    }
}
