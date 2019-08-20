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

package com.elitecore.core.commons.attributes;



/**
 * This exception is thrown when any attribute with invalid length
 * is encounterd while reading the packet. 
 *
 */
public class InvalidAttributeLengthException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public InvalidAttributeLengthException(){
		super("Invalid attribute length.");
	}

	public InvalidAttributeLengthException(String message){
		super(message);
	}
	
}
