package com.elitecore.corenetvertex.constants;

public enum PackageNotifications {

	USAGE_NOTIFICATION("Usage Notification");
	private String type;
	PackageNotifications(String type){
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
