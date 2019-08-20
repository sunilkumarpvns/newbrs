package com.brs.userservicemanagement.util;

import org.springframework.util.Base64Utils;

public class TokenGeneratorUtil {
public static String generateToken(){
	final String ALPHA_NUMERIC_STRING 
	= "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	StringBuilder builder = new StringBuilder();
	for(int i=0;i<5;i++) {
	int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
	builder.append(ALPHA_NUMERIC_STRING.charAt(character));
	}
	return builder.toString();
	}

}
