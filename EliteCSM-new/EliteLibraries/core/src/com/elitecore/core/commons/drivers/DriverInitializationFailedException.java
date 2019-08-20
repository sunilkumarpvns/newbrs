package com.elitecore.core.commons.drivers;

import com.elitecore.core.commons.InitializationFailedException;

public class DriverInitializationFailedException extends InitializationFailedException {
	
	private static final long serialVersionUID = 1L;

	public DriverInitializationFailedException(){
		super("Error during driver initialization.");
	}
	
	public DriverInitializationFailedException(String message){
		super(message);
	}
	
	public DriverInitializationFailedException(String message, Throwable cause){
		super(message, cause);
	}

	public DriverInitializationFailedException(Throwable cause){
		super(cause);
	}
	
}
