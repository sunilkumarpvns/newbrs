package com.elitecore.diameterapi.diameter.common.packet.avps;

public class MaxValueException extends RuntimeException{
	static final long serialVersionUID = 1L;
	public MaxValueException(){
		super("Value exceeds maximum limit");
	}
	public MaxValueException(String strMessage){
		super(strMessage);
	}
}