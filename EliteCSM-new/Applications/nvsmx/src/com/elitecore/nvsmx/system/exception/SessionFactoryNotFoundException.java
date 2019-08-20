package com.elitecore.nvsmx.system.exception;


public class SessionFactoryNotFoundException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SessionFactoryNotFoundException() {
		super();
	}

	public SessionFactoryNotFoundException(String message, Throwable t) {
		super(message, t);
	}

	public SessionFactoryNotFoundException(String message) {
		super(message);
	}

	public SessionFactoryNotFoundException(Throwable t) {
		super(t);
	}

}
