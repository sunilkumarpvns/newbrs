package com.elitecore.corenetvertex.constants;

/**
 * 
 * @author Jay Trivedi
 *
 */

public enum PkgStatus {
	
	
	ACTIVE("Active"),
	INACTIVE("Inactive"),
	RETIRED("Retired");

    public final String val;
	PkgStatus(String val){
		this.val = val;
	}
	
	public static PkgStatus fromVal(String value) {

		for (PkgStatus status : values()) {
			if (status.val.equalsIgnoreCase(value)) {
				
				return status;
			}
		}
		return null;
	}
 }
