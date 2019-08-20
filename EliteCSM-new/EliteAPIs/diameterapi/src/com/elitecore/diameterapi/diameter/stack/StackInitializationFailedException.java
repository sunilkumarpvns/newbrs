/**
 * 
 */
package com.elitecore.diameterapi.diameter.stack;


/**
 * @author pulindani
 *
 */
public class StackInitializationFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public StackInitializationFailedException() {
	}

	/**
	 * @param message
	 */
	public StackInitializationFailedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public StackInitializationFailedException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public StackInitializationFailedException(String message,
			Throwable cause) {
		super(message, cause);
	}
	
}
