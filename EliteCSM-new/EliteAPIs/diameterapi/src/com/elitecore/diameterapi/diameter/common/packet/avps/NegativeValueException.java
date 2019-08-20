package com.elitecore.diameterapi.diameter.common.packet.avps;

public class NegativeValueException extends RuntimeException{
	static final long serialVersionUID = 1L;
	public NegativeValueException(){
		super("Value is Negative");
	}
	public NegativeValueException(String strMessage){
		super(strMessage);
	}
}
