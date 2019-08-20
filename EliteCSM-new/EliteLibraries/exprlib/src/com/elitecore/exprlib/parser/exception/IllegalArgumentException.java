package com.elitecore.exprlib.parser.exception;

public class IllegalArgumentException extends Exception {
	private static final long serialVersionUID = 3641986468612017706L;

	public IllegalArgumentException (String message){
		super(message);
	}
	
	public IllegalArgumentException (Exception e){
		super(e);
	}
	
	public IllegalArgumentException (String message, Exception e){
		super(message,e);
	}

}
