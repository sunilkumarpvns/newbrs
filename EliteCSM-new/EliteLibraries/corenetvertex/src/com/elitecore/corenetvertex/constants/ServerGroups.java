package com.elitecore.corenetvertex.constants;

public enum ServerGroups {

	OFFLINE_RNC("Offline RnC"), 
	PCC("PCC");

	private String value;

	private ServerGroups(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static ServerGroups fromVal(String value) {
		for (ServerGroups status : values()) {
			if (status.value.equalsIgnoreCase(value)) {
				return status;
			}
		}
		return null;
	}

}
