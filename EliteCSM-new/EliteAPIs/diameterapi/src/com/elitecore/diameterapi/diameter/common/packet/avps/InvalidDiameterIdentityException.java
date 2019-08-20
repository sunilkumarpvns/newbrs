package com.elitecore.diameterapi.diameter.common.packet.avps;

public class InvalidDiameterIdentityException extends RuntimeException{
	static final long serialVersionUID = 1L;
	public InvalidDiameterIdentityException(){
		super("Invalid Diameter Identity");
	}
	public InvalidDiameterIdentityException(String strMessage){
		super(strMessage);
	}
}
