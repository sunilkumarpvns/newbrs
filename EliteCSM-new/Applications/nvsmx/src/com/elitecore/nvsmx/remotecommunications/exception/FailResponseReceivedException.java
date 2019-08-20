package com.elitecore.nvsmx.remotecommunications.exception;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by harsh on 6/15/16.
 */
public class FailResponseReceivedException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private LinkedHashMap<String,Object> response;

    /**
     *
     */
    public FailResponseReceivedException(LinkedHashMap<String,Object> response) {
        super();
        this.response = response;
    }

    /**
     * @param message
     */
    public FailResponseReceivedException( String message, LinkedHashMap<String,Object> response) {
        super(message);
        this.response = response;
    }

    /**
     * @param message
     * @param cause
     */
    public FailResponseReceivedException( String message ,
                                   Throwable cause, LinkedHashMap<String,Object> response) {
        super(message, cause);
        this.response = response;
    }

    /**
     * @param cause
     */
    public FailResponseReceivedException( Throwable cause, LinkedHashMap<String,Object> response) {
        super(cause);
        this.response = response;
    }

    public LinkedHashMap<String,Object>  getResponse() {
        return response;
    }
}
