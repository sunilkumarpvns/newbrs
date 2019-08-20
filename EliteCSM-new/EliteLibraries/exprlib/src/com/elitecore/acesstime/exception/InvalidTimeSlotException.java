package com.elitecore.acesstime.exception;

public class InvalidTimeSlotException extends Exception {
	private static final long serialVersionUID = 3641986468612017706L;

	public InvalidTimeSlotException (String message){
		super(message);
	}
	
	public InvalidTimeSlotException (Exception e){
		super(e);
	}
	
	public InvalidTimeSlotException (String message, Exception e){
		super(message,e);
	}

}
