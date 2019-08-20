package com.elitecore.core.commons.configuration;

public class LoadConfigurationException extends ConfigurationException{
	
	private static final long serialVersionUID = 1L;

	public LoadConfigurationException(){
		super("Error while loading the configuration.");
	}
	
	public LoadConfigurationException(Throwable exp) {
		super(exp);
	}
	
	public LoadConfigurationException(String message) {
		super(message);
	}
	public LoadConfigurationException(String message,Throwable cause) {
		super(message,cause);
	}
}
