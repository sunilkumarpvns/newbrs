package com.elitecore.diameterapi.mibs.constants;

import java.util.HashMap;
import java.util.Map;

public enum StorageTypes {

	OTHER(1,"other"),
	VOLATILE(2,"volatile"),
	NON_VOLATILE(3,"NonVolatile"),
	PARMANENT(4,"permanent"),
	READ_ONLY(5,"readOnly"),;
	
	public final int code;
	public final String storageTypeStr;
	private static final Map<Integer,StorageTypes> map;
	
	public static final StorageTypes[] VALUES = values();
	
	static {
		map = new HashMap<Integer,StorageTypes>();
		for (StorageTypes type : VALUES) {
			map.put(type.code, type);
		}
		
		
	}
	StorageTypes(int code,String storageTypeStr) {
		this.code = code;
		this.storageTypeStr = storageTypeStr;
	}

	public int getRoutingAction() {
		return code;
	}

	public static StorageTypes fromStorageTypeCode(int storageTypeCode) {
		return map.get(storageTypeCode);
	}
	
	public static boolean isValid(int value) {
		return map.containsKey(value);
	}
	
	public static String getStorageTypeString(int storageTypeCode) {
		StorageTypes storageType = map.get(storageTypeCode);  
		if(storageType != null){
			return storageType.storageTypeStr;
		}
		
		return "INVALID STORAGE TYPE";
	}



}
