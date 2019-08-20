package com.elitecore.nvsmx.system.keys;

/**
 * @author kirpalsinh.raj
 *
 */
public enum ValidationKeys {
	
	EMPTY_NAME("empty.name"),
	NAME_VALIDATION_REGEX("name.validation.regex"),
	NAME_ALREADY_EXIST("name.already.exist"),
	INVALID_NAME("invalid.name"),
	INVALID_VALUE("invalid.value"),
	VALID_VALUE("valid.value"),
	VALID_NAME("valid.name"),
	NAME_VALIDATION_FAILED("name.validation.failed");
	
	public String key;
	ValidationKeys(String key){
		this.key =  key;
	}
}
