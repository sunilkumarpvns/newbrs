package com.elitecore.license.formatter;

/**
 * A formatter is used to format the license file in desired formats
 * and with desired encryption. 
 * 
 * @author malav.desai
 * @author vicky.singh
 */
public interface Formatter {
	
	Formatter startLicense();
	Formatter startModule();
	Formatter append(String value);
	Formatter endModule();
	Formatter endLicense();
	String format();
	Formatter appendPublicKey(String value);
}
