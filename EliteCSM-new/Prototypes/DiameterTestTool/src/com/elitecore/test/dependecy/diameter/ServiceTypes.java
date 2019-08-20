package com.elitecore.test.dependecy.diameter;

import java.util.HashMap;
import java.util.Map;


public enum ServiceTypes {

	ACCT(1,"Acct"),
	AUTH(2,"Auth"),
	BOTH(3,"Both");
	
	public final int code;
	public final String serviceTypeStr;
	private static final Map<Integer,ServiceTypes> map;
	
	public static final ServiceTypes[] VALUES = values();
	
	static {
		map = new HashMap<Integer,ServiceTypes>();
		for (ServiceTypes type : VALUES) {
			map.put(type.code, type);
		}
		
		
	}
	ServiceTypes(int code,String serviceTypeStr) {
		this.code = code;
		this.serviceTypeStr = serviceTypeStr;
	}

	public static ServiceTypes fromServiceTypeCode(int serviceTypeCode) {
		return map.get(serviceTypeCode);
	}
	
	public static boolean isValid(int value) {
		return map.containsKey(value);
	}
	
	public static String getServiceTypeString(int serviceTypeCode) {
		ServiceTypes serviceType = map.get(serviceTypeCode);  
		if(serviceType != null){
			return serviceType.serviceTypeStr;
		}
		
		return "INVALID SERVICE TYPE";
	}

}
