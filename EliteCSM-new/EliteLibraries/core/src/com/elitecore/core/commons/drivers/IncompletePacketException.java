package com.elitecore.core.commons.drivers;

public class IncompletePacketException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public IncompletePacketException(){
		super("Required attribute missing in the request packet.");
	}
	
	public IncompletePacketException(String message){
		super(message);
	}
}
