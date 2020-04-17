package com.happn.agareau.techtest.densitypop.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    NB_ZONES_NEGATIVE(1, "number of zones cannot be negative ! ", HttpStatus.BAD_REQUEST),
    UPLOAD_ERROR(2, "error on uploading file", HttpStatus.INTERNAL_SERVER_ERROR),
    READING_FILE_ERROR(3, "error when reading file", HttpStatus.INTERNAL_SERVER_ERROR);


    private final int code;
    private final String message;
    private final HttpStatus responseStatus;

    ErrorCode(int code, String message, HttpStatus responseStatus) {
        this.code = code;
        this.message = message;
        this.responseStatus = responseStatus;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getResponseStatus() {
        return responseStatus;
    }
}
