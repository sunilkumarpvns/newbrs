package com.elitecore.commons.kpi.exception;

public class RegistrationFailedException extends Exception {

	private static final long serialVersionUID = -7622811393017535618L;

	public RegistrationFailedException(String message) {
		super(message);
	}

	public RegistrationFailedException(Throwable cause) {
		super(cause);
	}

	public RegistrationFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}
