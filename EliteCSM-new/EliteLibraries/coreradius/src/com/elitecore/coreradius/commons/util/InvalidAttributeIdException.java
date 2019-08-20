package com.elitecore.coreradius.commons.util;

public class InvalidAttributeIdException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public InvalidAttributeIdException(){
		super("Invalid Usersfile.");
	}
	
	public InvalidAttributeIdException(String message){
		super(message);
	}
	
	public InvalidAttributeIdException(Throwable cause){
		super(cause);
	}
	
	public InvalidAttributeIdException(String message, Throwable cause){
		super(message, cause);
	}
}
