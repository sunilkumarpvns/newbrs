package com.elitecore.nvsmx.system.exception;

public class InvalidParameterException extends RuntimeException {


    private static final long serialVersionUID = 1L;

    public InvalidParameterException() {
        super();
    }

    public InvalidParameterException(String message, Throwable t) {
        super(message, t);
    }

    public InvalidParameterException(String message) {
        super(message);
    }

    public InvalidParameterException(Throwable t) {
        super(t);
    }

}
