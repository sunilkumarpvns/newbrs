/*
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 */

package com.elitecore.passwordutil;

public class NoSuchEncryptionException extends java.lang.Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchEncryptionException(){
	}

	public NoSuchEncryptionException(String message){
		super(message);
	}
	
}