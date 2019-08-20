package com.elitecore.aaa.core.commons.util.exception;

@SuppressWarnings("serial")
public class InvalidPacketException extends Exception {

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
