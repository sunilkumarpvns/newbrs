/*
 *  License Module
 *
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 10th September 2007
 *  Created By Ezhava Baiju D
 */

package com.elitecore.license.base.exception;

import com.elitecore.license.base.exception.licensetype.InvalidLicenseException;
public class InvalidLicenseDataException extends InvalidLicenseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidLicenseDataException() {
		super("Not a valid license key.");
	}

	public InvalidLicenseDataException(String message) {
		super(message);
	}

	public InvalidLicenseDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidLicenseDataException(Throwable cause) {
		super(cause);
	}
	
}
