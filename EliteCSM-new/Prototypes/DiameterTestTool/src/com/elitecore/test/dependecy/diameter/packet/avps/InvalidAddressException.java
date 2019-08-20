package com.elitecore.test.dependecy.diameter.packet.avps;

public class InvalidAddressException extends RuntimeException{
	static final long serialVersionUID = 1L;
	public InvalidAddressException(){
		super("Invalid Diameter Address");
	}
	public InvalidAddressException(String strMessage){
		super(strMessage);
	}
}
