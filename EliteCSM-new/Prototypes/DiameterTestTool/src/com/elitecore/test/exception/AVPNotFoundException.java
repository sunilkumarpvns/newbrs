package com.elitecore.test.exception;

public class AVPNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	public AVPNotFoundException(String id,String message, Throwable cause) {
		super(message, cause);
	}

	public AVPNotFoundException(String id,String message) {
		super(message);
	}

	public AVPNotFoundException(String id,Throwable cause) {
		super(cause);
	}
	
	public String getId(){
		return id;
	}

}
