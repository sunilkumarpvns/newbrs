package com.elitecore.core.commons.drivers;

public class UpdateAccountDataFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public UpdateAccountDataFailedException(){
		super("Driver level operation failed.");
	}

	public UpdateAccountDataFailedException(String message){
		super(message);
	}
	
	public UpdateAccountDataFailedException(String message, Throwable cause){
		super(message, cause);
	}
	
	public UpdateAccountDataFailedException(Throwable cause){
		super(cause);
	}

	
}
