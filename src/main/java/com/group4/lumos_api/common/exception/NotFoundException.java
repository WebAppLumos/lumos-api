package com.group4.lumos_api.common.exception;

/**
 * 요청한 리소스를 찾을 수 없을 때 발생시킨다. (HTTP 404)
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
