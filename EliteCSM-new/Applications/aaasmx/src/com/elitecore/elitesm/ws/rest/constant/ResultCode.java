package com.elitecore.elitesm.ws.rest.constant;

import java.util.HashMap;
import java.util.Map;

public enum ResultCode {

	SUCCESS("200", "SUCCESS"),
	PARTIAL_SUCCESS("201","PARTIAL SUCCESS"),
	INVALID_INPUT_PARAMETER("400", "INVALID INPUT PARAMETER"),
 	INPUT_PARAMETER_MISSING("401", "INPUT PARAMETER MISSING"),
 	NOT_FOUND("404", "NOT FOUND"),
 	METHOD_NOT_ALLOWED("405","METHOD NOT ALLOWED"),
	ALREADY_EXIST("450", "ALREADY EXIST"),
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
