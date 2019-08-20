/*
 *  Radius Client API
 *  
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 *  Created on 11th September 2006
 *  Created By Dhirendra Kumar Singh
 */

package com.elitecore.coreradius.commons;

/**
 * This exception is thrown if any rule is violated from the RADIUS server
 * implementation point of view.
 * @author Elitecore Technologies Ltd.
 * @version 1.0
 */
public class RadiusGeneralException extends Exception{

	private static final long serialVersionUID = 4151201157176954485L;

	public RadiusGeneralException() {
		super();
	}
	
	public RadiusGeneralException(String message) {
		super(message);
	}
	
	public RadiusGeneralException(Throwable cause) {
		super(cause);
	}
	
	public RadiusGeneralException(String message, Throwable cause) {
		super(message, cause);
	}
}
