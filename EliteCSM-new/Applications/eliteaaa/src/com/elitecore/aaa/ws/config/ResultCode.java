package com.elitecore.aaa.ws.config;

import java.util.HashMap;
import java.util.Map;

public enum ResultCode {

	SUCCESS("200", "SUCCESS"),
	INVALID_INPUT_PARAMETER("400", "INVALID INPUT PARAMETER"),
 	UNAUTHORIZED_USER("401", "UNAUTHORIZED USER"),
 	NOT_FOUND("404", "NOT FOUND"),
	INTERNAL_ERROR("500", "INTERNAL ERROR"),
	SERVICE_UNAVAILABLE("503", "SERVICE UNAVAILABLE"),
	OPERATION_NOT_SUPPORTED("599", "OPERATION NOT SUPPORTED");
	
	public String responseCode;
	public String name;
	public static Map<String,ResultCode> resultCodeMap = new HashMap<String, ResultCode>();
	static{
		for(ResultCode resultCode : ResultCode.values()){
			resultCodeMap.put(resultCode.responseCode, resultCode);
		}
	}
	
	private ResultCode(String code, String name) {
		this.responseCode = code;
		this.name = name;
	}
	public static ResultCode fromVal(int code){
		return resultCodeMap.get(code);
	}

}
