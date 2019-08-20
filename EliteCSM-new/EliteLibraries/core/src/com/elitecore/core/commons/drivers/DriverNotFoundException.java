package com.elitecore.core.commons.drivers;

public class DriverNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	
	public DriverNotFoundException(String message){
		super(message);
	}

	public DriverNotFoundException(String message, Throwable cause){
		super(message, cause);
	}
	
	public DriverNotFoundException(Throwable cause){
		super(cause);
	}
	
}
