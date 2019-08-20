package com.elitecore.corenetvertex.constants;

import java.util.HashMap;
import java.util.Map;

public enum PasswordEncryptionType {
	
	NONE(0),
	ELITECRYPT(3),
	ELITE_PASSWORD_CRYPT(6),
	BASE16(16),
	BASE32(32),
	BASE64(64),
	;
	
	public final long val;
	public final String strVal;
	public final String displayVal;
	
	private static Map<Long, PasswordEncryptionType> integerValTopasswordEncryptionType;
	private static Map<String, PasswordEncryptionType> stringValueToPasswordEncryptionType;
	
	static{
		integerValTopasswordEncryptionType = new HashMap<Long, PasswordEncryptionType>();
		stringValueToPasswordEncryptionType = new HashMap<String, PasswordEncryptionType>();
		for(PasswordEncryptionType passwordEncryptionType : values()){
			integerValTopasswordEncryptionType.put(passwordEncryptionType.val, passwordEncryptionType);
			stringValueToPasswordEncryptionType.put(passwordEncryptionType.strVal, passwordEncryptionType);
		}
	}
	private PasswordEncryptionType(int val){
		this.val = val;
		this.strVal = String.valueOf(val);
		this.displayVal = name() + CommonConstants.OPENING_PARENTHESES + val + CommonConstants.CLOSING_PARENTHESES;
	}
	
	public static PasswordEncryptionType fromValue(long val){
		return integerValTopasswordEncryptionType.get(val);
	}
	
	public static PasswordEncryptionType fromValue(String val){
		return stringValueToPasswordEncryptionType.get(val);
	}
	
	public String getDisplayVal(){
		return displayVal;
	}
	
}
