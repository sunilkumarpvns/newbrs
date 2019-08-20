package com.elitecore.aaa.core.authprotocol;

import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;

public class InvalidPasswordException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public InvalidPasswordException() { 
		super(AuthReplyMessageConstant.INVALID_PASSWORD);
    }
   
    public InvalidPasswordException(String message) {
        super(message);    
    }
   
    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);    
    }
   
    public InvalidPasswordException(Throwable cause) {
        super(cause);    
    }
}
