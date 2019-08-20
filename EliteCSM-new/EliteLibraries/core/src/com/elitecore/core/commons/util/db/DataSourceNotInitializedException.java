package com.elitecore.core.commons.util.db;

public class DataSourceNotInitializedException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public DataSourceNotInitializedException(String message) {
		super(message);
	}
	
	public DataSourceNotInitializedException(Throwable cause) {
		super(cause);
	}
	
	public DataSourceNotInitializedException(String message, Throwable cause) {
		super(message, cause);
	}

}
