package com.elitecore.passwordutil;

public class EncryptionFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public EncryptionFailedException() {
	}

	public EncryptionFailedException(String message) {
		super(message);
	}

	public EncryptionFailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
