package com.happn.agareau.techtest.densitypop.error;

import org.springframework.web.server.ResponseStatusException;

public class ServiceException extends ResponseStatusException {

    private final ErrorCode code;

    private final String context;

    public ServiceException(ErrorCode code, String context, Throwable cause) {
        super(code.getResponseStatus(), code.getMessage(), cause);
        this.code = code;
        this.context = context;
    }

    public ServiceException(ErrorCode code, String context) {
        this(code, context, null);
    }

    public ServiceException(ErrorCode code, Throwable cause) {
        this(code, null, cause);
    }

    public ServiceException(ErrorCode code) {
        this(code, null, null);
    }

    public ErrorCode getCode() {
        return code;
    }

    public String getContext() {
        return context;
    }
}
