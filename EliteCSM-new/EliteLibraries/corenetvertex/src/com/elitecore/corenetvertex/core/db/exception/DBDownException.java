package com.elitecore.corenetvertex.core.db.exception;


public class DBDownException extends Exception{

	private static final long serialVersionUID = 1L;

	
	public DBDownException(String message) {
		super(message);
	}

	public DBDownException(Throwable throwable) {
		super(throwable);
	}
	
	public DBDownException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
}
