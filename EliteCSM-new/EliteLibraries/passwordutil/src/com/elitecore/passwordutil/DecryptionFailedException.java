package com.elitecore.passwordutil;

public class DecryptionFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public DecryptionFailedException() {
	}

	public DecryptionFailedException(String message) {
		super(message);
	}

	public DecryptionFailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
