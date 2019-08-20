package com.elitecore.nvsmx.ws.subscription.exception;

public class ValidationFailedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private int resultCode; 
	public ValidationFailedException(int code, String string) {
		super(string);
		resultCode = code;
	}
	
	public int getResultCode() {
		return resultCode;
	}

}
