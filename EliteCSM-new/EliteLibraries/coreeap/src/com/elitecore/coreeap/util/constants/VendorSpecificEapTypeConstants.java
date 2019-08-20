package com.elitecore.coreeap.util.constants;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum VendorSpecificEapTypeConstants implements IEnum{	
	IETF_VENDOR_ID(0,"IETF");
	public final int typeId;
	public final String name;
	private static final Map<Integer,VendorSpecificEapTypeConstants> map;
	public static final VendorSpecificEapTypeConstants[] VALUES = values();
	static {
		map = new HashMap<Integer,VendorSpecificEapTypeConstants>(2);
		for (VendorSpecificEapTypeConstants type : VALUES) {
			map.put(type.typeId, type);
		}
	}	

	VendorSpecificEapTypeConstants(int id,String name){
		this.typeId= id;
		this.name = name;
	}
	public int getTypeId(){
		return this.typeId;
	}
	public static boolean isValid(int value){
		return map.containsKey(value);	
	}
	public static String getName(int value){
		return map.get(value).name;
	}
}
