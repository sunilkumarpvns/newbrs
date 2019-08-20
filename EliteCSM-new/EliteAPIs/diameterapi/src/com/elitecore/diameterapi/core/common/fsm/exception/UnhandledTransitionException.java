/**
 * 
 */
package com.elitecore.diameterapi.core.common.fsm.exception;

/**
 * @author pulin
 *
 */
public class UnhandledTransitionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public UnhandledTransitionException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public UnhandledTransitionException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public UnhandledTransitionException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnhandledTransitionException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
