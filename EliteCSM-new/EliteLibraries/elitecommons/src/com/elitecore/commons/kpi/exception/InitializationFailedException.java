package com.elitecore.commons.kpi.exception;

public class InitializationFailedException extends Exception {
	private static final long serialVersionUID = -7622811393017535618L;

	public InitializationFailedException(String message) {
		super(message);
	}

	public InitializationFailedException(Throwable cause) {
		super(cause);
	}

	public InitializationFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}
