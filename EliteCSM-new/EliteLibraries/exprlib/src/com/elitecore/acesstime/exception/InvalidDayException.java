package com.elitecore.acesstime.exception;

public class InvalidDayException extends Exception {
	private static final long serialVersionUID = 3641986468612017706L;

	public InvalidDayException (String message){
		super(message);
	}
	
	public InvalidDayException (Exception e){
		super(e);
	}
	
	public InvalidDayException (String message, Exception e){
		super(message,e);
	}

}
