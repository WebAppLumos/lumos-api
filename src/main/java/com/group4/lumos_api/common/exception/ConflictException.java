package com.group4.lumos_api.common.exception;

/**
 * 현재 리소스 상태와 충돌하는 요청일 때 발생시킨다. (HTTP 409)
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
