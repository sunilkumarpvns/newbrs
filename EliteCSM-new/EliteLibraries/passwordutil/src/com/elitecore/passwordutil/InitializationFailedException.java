package com.elitecore.passwordutil;

public class InitializationFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public InitializationFailedException() {
	}

	public InitializationFailedException(String message) {
		super(message);
	}

	public InitializationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
