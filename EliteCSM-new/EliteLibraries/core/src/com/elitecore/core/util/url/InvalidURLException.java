
package com.elitecore.core.util.url;
/**
 * @author Milan Paliwal
 *
 */
public class InvalidURLException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidURLException(String message){
		super(message);
	}
	
	public InvalidURLException(Exception e){
		super(e);
	}
	
	public InvalidURLException(String message, Exception e){
		super(message,e);
	}
}
