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

import com.elitecore.license.base.BaseLicenseException;

/**
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public class InvalidLicenseFileException extends BaseLicenseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidLicenseFileException() {
		super("Not a valid license key.");
	}

	public InvalidLicenseFileException(String message) {
		super(message);
	}

	public InvalidLicenseFileException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidLicenseFileException(Throwable cause) {
		super(cause);
	}
	
}
