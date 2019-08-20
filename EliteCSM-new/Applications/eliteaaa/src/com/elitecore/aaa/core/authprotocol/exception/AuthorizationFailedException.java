package com.elitecore.aaa.core.authprotocol.exception;

import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;

public class AuthorizationFailedException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public AuthorizationFailedException() { 
		super(AuthReplyMessageConstant.AUTHORIZATION_FAILED);
    }
   
    public AuthorizationFailedException(String message) {
        super(message);    
    }
   
    public AuthorizationFailedException(String message, Throwable cause) {
        super(message, cause);    
    }
   
    public AuthorizationFailedException(Throwable cause) {
        super(cause);    
    }
}
