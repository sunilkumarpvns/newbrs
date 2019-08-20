package com.brs.userservicemanagement.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {
public static boolean isValidMobileNumber(String mobileNumber){
	Pattern p = Pattern.compile("[0-9]{10}");
    Matcher m = p.matcher(mobileNumber);
    return (m.matches());
}
}
