package com.elitecore.corenetvertex.constants;

/**
 * This enum will define status for the resource. It can be Active or InActive
 * @author Ishani Dave
 *
 */

public enum ResourceStatus {


	ACTIVE("Active"),
	INACTIVE("Inactive");

	private String val;
	ResourceStatus(String val){
		this.val = val;
	}
	
	public static ResourceStatus fromVal(String value) {
		for (ResourceStatus status : values()) {
			if (status.val.equalsIgnoreCase(value)) {
				return status;
			}
		}
		return null;
	}
 }
