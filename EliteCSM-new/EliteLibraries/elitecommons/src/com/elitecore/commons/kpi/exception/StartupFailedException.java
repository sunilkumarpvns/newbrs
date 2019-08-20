package com.elitecore.commons.kpi.exception;

public class StartupFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public StartupFailedException(String message) {
		super(message);
	}

	public StartupFailedException(Throwable cause) {
		super(cause);
	}

	public StartupFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}
