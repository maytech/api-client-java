package com.quatrix.api;

import io.swagger.client.ApiException;

public class QuatrixApiException extends Exception {

    private final int code;
    private final String responseBody;

    public QuatrixApiException(String message, Throwable cause, int code, String body) {
        super(message, cause);
        this.code = code;
        this.responseBody = body;
    }

    public QuatrixApiException(String message, Throwable cause) {
        this(message, cause, 0, null);
    }

    public QuatrixApiException(String message, int code) {
        this(message, null, code, null);
    }

    public QuatrixApiException(ApiException ex) {
        this(ex.getMessage(), ex, ex.getCode(), ex.getResponseBody());
    }

    public int getCode() {
        return code;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
