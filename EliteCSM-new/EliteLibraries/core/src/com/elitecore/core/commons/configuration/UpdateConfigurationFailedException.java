package com.elitecore.core.commons.configuration;

public class UpdateConfigurationFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public UpdateConfigurationFailedException(String message) {
		super(message);
	}

	public UpdateConfigurationFailedException(String message,Throwable cause) {
		super(message,cause);
	}
	public UpdateConfigurationFailedException(){
		super("Error while updating configuration.");
	}
}
