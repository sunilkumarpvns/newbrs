package com.elitecore.test.exception;

public class InvalidPacketException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidPacketException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPacketException(String message) {
		super(message);
	}

	public InvalidPacketException(Throwable cause) {
		super(cause);
	}
}
