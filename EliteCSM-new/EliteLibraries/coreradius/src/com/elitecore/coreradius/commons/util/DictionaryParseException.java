/*
 *  Core Radius Protocol
 *
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 11th August 2007
 *  Author : Ezhava Baiju D
 */

package com.elitecore.coreradius.commons.util;

/**
 * Thrown when there is any problem parsing radius dictionary in Elitecore 
 * standard format.
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public class DictionaryParseException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DictionaryParseException() {
		super();
	}
	
	public DictionaryParseException(String message) {
		super(message);
	}
	
	public DictionaryParseException(Throwable cause) {
		super(cause);
	}
	
	public DictionaryParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
