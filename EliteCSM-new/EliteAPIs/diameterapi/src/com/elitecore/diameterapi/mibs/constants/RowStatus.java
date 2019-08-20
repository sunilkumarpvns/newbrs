package com.elitecore.diameterapi.mibs.constants;

import java.util.HashMap;
import java.util.Map;

public enum RowStatus {
	
	ACTIVE(1,"Active"),
	NO_IN_SERVICE(2,"NotInService"),
	NOT_READY(3,"NotReady"),
	CREATE_AND_GO(4,"CreateAndGo"),
	CREATE_AND_WAIT(5,"CreateAndWait"),
	DESTROY(6,"Destroy"),;
	
	public final int code;
	public final String statusStr;
	private static final Map<Integer,RowStatus> map;
	
	public static final RowStatus[] VALUES = values();
	
	static {
		map = new HashMap<Integer,RowStatus>();
		for (RowStatus type : VALUES) {
			map.put(type.code, type);
		}
		
		
	}
	RowStatus(int code,String statusStr) {
		this.code = code;
		this.statusStr = statusStr;
	}

	public static RowStatus fromRowStatusTypeCode(int statusTypeCode) {
		return map.get(statusTypeCode);
	}
	
	public static boolean isValid(int value) {
		return map.containsKey(value);
	}
	
	public static String getRowStatusTypeString(int statusTypeCode) {
		RowStatus rowStatus = map.get(statusTypeCode);  
		if(rowStatus != null){
			return rowStatus.statusStr;
		}
		
		return "INVALID ROW-STATUS TYPE";
	}

}
