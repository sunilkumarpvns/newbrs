package com.elitecore.core.util.constants;

import java.util.HashMap;
import java.util.Map;

public enum Vendor {
	
	CISCO("CISCO",9L),
    Huawei("Huawei",2011L),
    Juniper("Juniper",2636L),
    Ericsson("Ericsson",193L),
    Other("Other",999999999999L);
	
	private final String vendorName;
	private final Long vedorId;
	
	private Vendor(String vendorName,Long vendorId) {
		this.vendorName = vendorName;
		this.vedorId=vendorId;
	}
	
	private static final Vendor[] types = values();
	private static final Map<Long , Vendor> objectMap;
	private static final Map<String , Vendor> objectMapByName;
	
	static {
		objectMap = new HashMap<Long,Vendor>();
		objectMapByName = new HashMap<String, Vendor>();
		
		for ( Vendor type : types){
			objectMap.put(type.vedorId, type);
			objectMapByName.put(type.vendorName, type);
		}
	}

	public static Vendor fromID(Long vendorId){
		return objectMap.get(vendorId);
	}
	
	public static Vendor fromName(String vendorName){
		return objectMapByName.get(vendorName);
	}
	
	public Long getId() {
		return vedorId;
	}
	
	public String getName() {
		return vendorName;
	}
}
