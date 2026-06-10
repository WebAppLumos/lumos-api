package com.group4.lumos_api.common.exception;

/**
 * 요청 값이 비즈니스 규칙에 어긋날 때 발생시킨다. (HTTP 400)
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
