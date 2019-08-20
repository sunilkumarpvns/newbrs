package com.elitecore.core.commons.configuration;

/**
 * 
 * @author narendra.pathai
 *
 */
public class StoreConfigurationException extends ConfigurationException{

	private static final long serialVersionUID = 1L;

	public StoreConfigurationException() {
		super("Error in storing configuration");
	}
	
	public StoreConfigurationException(String message){
		super(message);
	}
	
	public StoreConfigurationException(Throwable exp) {
		super(exp);
	}
	
	public StoreConfigurationException(String message,Throwable cause) {
		super(message,cause);
	}
}
