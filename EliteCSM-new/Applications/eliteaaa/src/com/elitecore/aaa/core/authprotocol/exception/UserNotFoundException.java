package com.elitecore.aaa.core.authprotocol.exception;

import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;


public class UserNotFoundException extends AuthenticationFailedException {

	private static final long serialVersionUID = 1L;

	public UserNotFoundException() { 
		super(AuthReplyMessageConstant.USER_NOT_FOUND);
	}

	public UserNotFoundException(String message) {
		super(message);    
	}

	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);    
	}

	public UserNotFoundException(Throwable cause) {
		super(cause);    
	}
}

