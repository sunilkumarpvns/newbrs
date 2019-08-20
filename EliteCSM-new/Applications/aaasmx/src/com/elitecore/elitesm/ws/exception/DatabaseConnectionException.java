package com.elitecore.elitesm.ws.exception;

public class DatabaseConnectionException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public DatabaseConnectionException(String message){
		super(message);
	}

	public DatabaseConnectionException(String message,Throwable e){
		super(message,e);
	}

}
