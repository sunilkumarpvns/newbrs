package com.elitecore.nvsmx.system.util.migrate;

import com.elitecore.corenetvertex.spr.data.SPRFields;

public enum NV648SprColumnEnum {
	
	SUBSCRIBERPACKAGE("SUBSCRIBERPACKAGE", SPRFields.PRODUCT_OFFER.columnName),
	SUBSCRIBERSTATUS("SUBSCRIBERSTATUS", SPRFields.STATUS.columnName);
	
	
	public String name;
	private String columnNameIn680;
	
	private NV648SprColumnEnum(String name, String columnNameIn680) {
		this.name = name;
		this.columnNameIn680 = columnNameIn680;
	}
	
	public static String getUpdatedColumnName(String name) {
		if (SUBSCRIBERPACKAGE.name.equals(name)) {
			return SUBSCRIBERPACKAGE.columnNameIn680;
		} else if (SUBSCRIBERSTATUS.name.equals(name)) {
			return SUBSCRIBERSTATUS.columnNameIn680;
		}
		
		return null;
	}

}
