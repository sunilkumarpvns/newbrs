package com.elitecore.nvsmx.system.exception;

public class HibernateDataException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HibernateDataException() {
		super();
		
	}

	public HibernateDataException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public HibernateDataException(String message) {
		super(message);
		
	}

	public HibernateDataException(Throwable cause) {
		super(cause);
		
	}

}
