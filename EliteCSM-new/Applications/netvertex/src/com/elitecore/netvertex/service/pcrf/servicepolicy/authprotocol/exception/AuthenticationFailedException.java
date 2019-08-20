package com.elitecore.netvertex.service.pcrf.servicepolicy.authprotocol.exception;

import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;

public class AuthenticationFailedException extends Exception {

	private static final long serialVersionUID = 1L;
    private String failureReason;

	public AuthenticationFailedException() { 
		super(PCRFKeyValueConstants.RESULT_CODE_AUTHENTICATION_FAILED.val);
    }
   
    public AuthenticationFailedException(String message) {
        super(message);    
    }

    public AuthenticationFailedException(String message, String failureReason) {
        super(message);
        this.failureReason = failureReason;
    }
   
    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationFailedException(String message, Throwable cause, String failureReason) {
        super(message, cause);
        this.failureReason =  failureReason;
    }
   
    public AuthenticationFailedException(Throwable cause) {
        super(cause);    
    }

    public String getFailureReason(){
        return failureReason;
    }
}
