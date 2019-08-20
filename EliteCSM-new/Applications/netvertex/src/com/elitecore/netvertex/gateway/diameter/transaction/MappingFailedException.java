package com.elitecore.netvertex.gateway.diameter.transaction;

import com.elitecore.corenetvertex.data.ResultCode;

public class MappingFailedException extends  Exception {

    private static final long serialVersionUID = 1L;
    private final ResultCode errorCode;

    public MappingFailedException(String message, ResultCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public MappingFailedException(String message) {
        this(message, ResultCode.DIAMETER_END_USER_SERVICE_DENIED);
    }

    public MappingFailedException(Throwable throwable) {
        this(throwable, ResultCode.DIAMETER_END_USER_SERVICE_DENIED);
    }

    public MappingFailedException(Throwable throwable, ResultCode errorCode) {
        super(throwable);
        this.errorCode = errorCode;
    }

    public MappingFailedException(String message, Throwable throwable) {
        this(message, ResultCode.DIAMETER_END_USER_SERVICE_DENIED, throwable);
    }

    public MappingFailedException(String message, ResultCode errorCode, Throwable throwable) {
        super(message,throwable);
        this.errorCode = errorCode;
    }

    public ResultCode getErrorCode() {
        return errorCode;
    }
}
