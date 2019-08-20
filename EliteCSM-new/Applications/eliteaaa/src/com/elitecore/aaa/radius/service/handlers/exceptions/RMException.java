/**
 * 
 */
package com.elitecore.aaa.radius.service.handlers.exceptions;

/**
 * @author pulin
 *
 */
public class RMException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public RMException() {
		super("RM Processing Faild");
	}

	public RMException(String message, Throwable cause) {
		super(message, cause);
	}

	public RMException(String message) {
		super(message);
	}

	public RMException(Throwable cause) {
		super(cause);
	}
}
