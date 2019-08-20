package com.elitecore.aaa.core.authprotocol.exception;

import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;

public class AuthenticationFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public AuthenticationFailedException() { 
		super(AuthReplyMessageConstant.AUTHENTICATION_FAILED);
    }
   
    public AuthenticationFailedException(String message) {
        super(message);    
    }
   
    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);    
    }
   
    public AuthenticationFailedException(Throwable cause) {
        super(cause);    
    }
}
