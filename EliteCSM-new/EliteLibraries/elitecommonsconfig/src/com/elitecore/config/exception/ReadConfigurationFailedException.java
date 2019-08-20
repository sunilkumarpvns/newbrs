package com.elitecore.config.exception;

public class ReadConfigurationFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1305517881541669618L;

	public ReadConfigurationFailedException(String message) {
		super(message);
	}

	public ReadConfigurationFailedException(Throwable cause) {
		super(cause);
	}

	public ReadConfigurationFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}
