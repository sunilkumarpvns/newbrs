package com.elitecore.elitesm.datamanager.core.util;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.validator.GenericValidator;

import com.elitecore.commons.base.Strings;

public class EliteGenericValidator extends GenericValidator {
    
	public static boolean isIPV4Address(String ipAddress){
		
		//String ipRegExp = "(?<time>(\\d|\\:)+)\\s(?<ip>(\\d|\\.)+)\\s(?<site>\\S+)";
		//String ipRegExp ="\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){2}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";
		//String ipRegExp = =/^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/;
		final Pattern IP_PATTERN =
            Pattern.compile("b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).)"
                                  + "{3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)b");

		return IP_PATTERN.matcher(ipAddress).matches();
	}

	public static boolean maxLength(long longValue, int length){
		
		if(String.valueOf(longValue).trim().length() > length)
			return false;
		else
			return true;
	}

	public static boolean isBlankOrNull(Timestamp timestamp){
		
		if(timestamp == null)
			return true;
		else
			return false;
	}

	public static boolean isBlankOrNull(Date date){
		
		if(date == null)
			return true;
		else
			return false;
	}
	public static boolean isGreaterThanZero(String value){
		return (!Strings.isNullOrBlank(value));
	}
}
