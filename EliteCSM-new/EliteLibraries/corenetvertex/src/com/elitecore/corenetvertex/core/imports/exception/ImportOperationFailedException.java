package com.elitecore.corenetvertex.core.imports.exception;


public class ImportOperationFailedException extends Exception{

	private static final long serialVersionUID = 1L;


	public ImportOperationFailedException(String message) {
		super(message);
	}

	public ImportOperationFailedException(Throwable throwable) {
		super(throwable);
	}

	public ImportOperationFailedException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
}
