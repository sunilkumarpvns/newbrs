package com.elitecore.classicrating.blmanager.base;

public class MandatoryParameterMissingException extends Exception {

	private static final long serialVersionUID = 1L;

	public MandatoryParameterMissingException() {
	}

	public MandatoryParameterMissingException(String message) {
		super(message);
	}

	public MandatoryParameterMissingException(Throwable throwable) {
		super(throwable);
	}

	public MandatoryParameterMissingException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
