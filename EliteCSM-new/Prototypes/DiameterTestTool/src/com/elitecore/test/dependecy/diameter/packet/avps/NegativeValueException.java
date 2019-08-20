package com.elitecore.test.dependecy.diameter.packet.avps;

public class NegativeValueException extends RuntimeException{
	static final long serialVersionUID = 1L;
	public NegativeValueException(){
		super("Value is Negative");
	}
	public NegativeValueException(String strMessage){
		super(strMessage);
	}
}
