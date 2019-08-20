/**
 * 
 */
package com.elitecore.exprlib.parser.exception;

/**
 * @author milan
 *
 */
public class InvalidSymbolException extends Exception {
	
	private static final long serialVersionUID = -2138012637601256364L;

	public InvalidSymbolException(String message){
		super(message);
	}
	
	public InvalidSymbolException(Exception e){
		super(e);
	}
	
	public InvalidSymbolException(String message, Exception e){
		super(message,e);
	}

}
