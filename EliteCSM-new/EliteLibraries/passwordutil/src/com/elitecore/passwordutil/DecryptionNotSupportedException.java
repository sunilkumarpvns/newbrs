/*
 *  Server Framework
 *  
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 */

package com.elitecore.passwordutil;

public class DecryptionNotSupportedException extends Exception {

	private static final long serialVersionUID = 1L;

	public DecryptionNotSupportedException(){
    	super("Decryption not supported.");
    }

    public DecryptionNotSupportedException(String message){
    	super(message);
    }
    
}