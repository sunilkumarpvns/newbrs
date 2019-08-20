package com.elitecore.aaa.license;

import com.elitecore.license.base.exception.licensetype.InvalidLicenseException;

/**
 * A License manager for EliteAAA Server. An AAALicenseManager loads the license
 * starts tasks related to license, removes a license, validates the license and 
 * can be used to get value of a particular field of license.
 * 
 * 
 * @author vicky.singh
 * @author malav.desai
 *
 */
public interface AAALicenseManager {
	
	void init() throws InvalidLicenseException;
	void startLicenseValidationTask();
	void removeLicenseFile();
	boolean validateLicense(String key, String value);
	String getLicenseValue(String key);
	String getLicenseKey();
}