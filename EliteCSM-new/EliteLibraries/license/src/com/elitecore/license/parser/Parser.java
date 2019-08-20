package com.elitecore.license.parser;

import com.elitecore.license.base.License;
import com.elitecore.license.base.exception.InvalidLicenseKeyException;

/**
 * Given a license key and a de-cryptor it decrypts the key 
 * and creates a  license.  
 * 
 * @author malav.desai
 * @author vicky.singh
 *
 */
public interface Parser {
	
	License parse() throws InvalidLicenseKeyException ;
}
