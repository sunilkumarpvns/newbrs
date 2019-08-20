package com.elitecore.netvertex.service.pcrf.servicepolicy.authprotocol.exception;	

import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;

public class InvalidPasswordException extends AuthenticationFailedException{

	private static final long serialVersionUID = 1L;

	public InvalidPasswordException() { 
		super(PCRFKeyValueConstants.RESULT_CODE_INVALID_PASSWORD.val);		
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
