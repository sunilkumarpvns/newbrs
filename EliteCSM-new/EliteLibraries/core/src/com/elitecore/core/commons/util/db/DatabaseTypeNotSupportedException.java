/*
 *  Server Framework
 *  
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 *  Created on 10th April 2006
 *  Author : Ezhava Baiju D  
 */

package com.elitecore.core.commons.util.db;

/**
 * Thrown to indicate a technical problem.  
 *
 */
public class DatabaseTypeNotSupportedException extends Exception {

	private static final long serialVersionUID = 1L;

	public DatabaseTypeNotSupportedException(String message) {
		super(message);
	}

	public DatabaseTypeNotSupportedException(Throwable cause) {
		super(cause);
	}

	public DatabaseTypeNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}
}