package com.elitecore.core.commons.drivers;

public class TypeNotSupportedException extends Exception {
	/**
	 * @author ishani.bhatt
	 */
	private static final long serialVersionUID = 1L;

	
	public TypeNotSupportedException(String message){
		super(message);
	}

	public TypeNotSupportedException(String message, Throwable cause){
		super(message, cause);
	}
	
	public TypeNotSupportedException(Throwable cause){
		super(cause);
	}
	public TypeNotSupportedException(String message, int type){
		super(message + " Type: "+ type + " is not supported.");
	}


}
