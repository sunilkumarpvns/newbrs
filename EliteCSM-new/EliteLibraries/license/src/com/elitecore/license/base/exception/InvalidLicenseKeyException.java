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
public class InvalidLicenseKeyException extends InvalidLicenseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidLicenseKeyException() {
		super("License key is not valid.");
	}

	public InvalidLicenseKeyException(String message) {
		super(message);
	}

	public InvalidLicenseKeyException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidLicenseKeyException(Throwable cause) {
		super(cause);
	}

}
