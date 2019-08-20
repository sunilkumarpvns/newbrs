package com.elitecore.netvertex.service.pcrf;

public class UsageNotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UsageNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public UsageNotFoundException(String message) {
		super(message);
	}

	public UsageNotFoundException(Throwable cause) {
		super(cause);
	}

}
