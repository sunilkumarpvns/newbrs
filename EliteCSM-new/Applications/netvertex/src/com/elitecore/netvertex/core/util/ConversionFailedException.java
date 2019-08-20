package com.elitecore.netvertex.core.util;

public class ConversionFailedException extends Exception {

	private static final long serialVersionUID = 1L;
	public ConversionFailedException(String id,String message, Throwable cause) {
		super(message, cause);
	}

	public ConversionFailedException(String id,String message) {
		super(message);
	}

	public ConversionFailedException(String id,Throwable cause) {
		super(cause);
	}
	
	public ConversionFailedException(String message) {
		super(message);
	}

}
