package com.elitecore.corenetvertex.spr.exceptions;

import java.util.HashMap;
import java.util.Map;

public enum ResultCode {

	SUCCESS(200, "SUCCESS"),
	INVALID_INPUT_PARAMETER(400, "INVALID INPUT PARAMETER"),
 	INPUT_PARAMETER_MISSING(401, "INPUT PARAMETER MISSING"),
 	NOT_FOUND(404, "NOT FOUND"),
	ALREADY_EXIST(450, "ALREADY EXIST"),
	INTERNAL_ERROR(500, "INTERNAL ERROR"),
	SERVICE_UNAVAILABLE(503, "SERVICE UNAVAILABLE"),
	OPERATION_NOT_SUPPORTED(599, "OPERATION NOT SUPPORTED"),
	PRECONDITION_FAILED(412, "PRECONDITION FAILED");
	
	public final int code;
	public final String name;
	public static Map<Integer,ResultCode> resultCodeMap = new HashMap<Integer, ResultCode>();
	static{
		for(ResultCode resultCode : ResultCode.values()){
			resultCodeMap.put(resultCode.code, resultCode);
		}
	}
	
	
	private ResultCode(int code, String name) {
		this.code = code;
		this.name = name;
	}
	public static ResultCode fromVal(int code){
		return resultCodeMap.get(code);
	}

}
