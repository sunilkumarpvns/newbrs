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

package com.elitecore.coreradius.commons.attributes;


 
/**
 * This exception is thrown when any attribute with invalid length
 * is encounterd while reading the packet. 
 *
 */
public class InvalidRadiusAttributeLengthException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public InvalidRadiusAttributeLengthException(){
		super("Invalid attribute length.");
	}

	public InvalidRadiusAttributeLengthException(String message){
		super(message);
	}
	
}
