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

package com.elitecore.license.base.exception.licensetype;

import com.elitecore.license.base.BaseLicenseException;

/**
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public class InvalidLicenseException extends BaseLicenseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidLicenseException() {
		super("Not a valid license key.");
	}

	public InvalidLicenseException(String message) {
		super(message);
	}

	public InvalidLicenseException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidLicenseException(Throwable cause) {
		super(cause);
	}
	
}
