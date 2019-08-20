package com.elitecore.diameterapi.diameter.common.packet.avps;

public class URISyntaxException extends RuntimeException{
	static final long serialVersionUID = 1L;
	public URISyntaxException(){
		super("Invalid Diameter URI");
	}
	public URISyntaxException(String strMessage){
		super(strMessage);
	}
}
