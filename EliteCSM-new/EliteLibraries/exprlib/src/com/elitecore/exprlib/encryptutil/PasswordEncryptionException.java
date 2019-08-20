package com.elitecore.exprlib.encryptutil;

public class PasswordEncryptionException extends Exception{

	private static final long serialVersionUID = 8453232089612481658L;

	public PasswordEncryptionException(){
		super();
	}
	
	public PasswordEncryptionException(String message){
		super(message);
	}
	
	public PasswordEncryptionException(Throwable cause){
		super(cause);
	}
	
	public PasswordEncryptionException(String message, Throwable cause){
		super(message, cause);
	}
}
