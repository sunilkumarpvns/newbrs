package com.elitecore.core.commons.configuration;

public class ConfigurationException extends Exception{

	private static final long serialVersionUID = 1L;

	public ConfigurationException(Throwable exp) {
		super(exp);
	}
	
	public ConfigurationException(String message) {
		super(message);
	}
	
	public ConfigurationException(String message,Throwable cause) {
		super(message,cause);
	}
}
