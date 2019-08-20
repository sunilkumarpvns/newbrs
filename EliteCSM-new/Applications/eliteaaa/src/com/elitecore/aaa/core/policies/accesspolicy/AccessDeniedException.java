package com.elitecore.aaa.core.policies.accesspolicy;
 
public class AccessDeniedException extends Exception {

	private static final long serialVersionUID = 1L;

	public AccessDeniedException() {
		super("Access Denied");
	}

	public AccessDeniedException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccessDeniedException(String message) {
		super(message);
	}

	public AccessDeniedException(Throwable cause) {
		super(cause);
	}

}
