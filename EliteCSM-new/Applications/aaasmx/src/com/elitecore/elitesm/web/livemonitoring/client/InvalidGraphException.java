package com.elitecore.elitesm.web.livemonitoring.client;

public class InvalidGraphException  extends Exception{
	
	
	private static final long serialVersionUID = 1L;
	public InvalidGraphException(){
		super();
	}
	public InvalidGraphException(String arg){
		super(arg);
	}
	public InvalidGraphException(Throwable cause){
		super( cause);
	}
	public InvalidGraphException(String message,Throwable cause){
		super(message,cause);
	}
}
