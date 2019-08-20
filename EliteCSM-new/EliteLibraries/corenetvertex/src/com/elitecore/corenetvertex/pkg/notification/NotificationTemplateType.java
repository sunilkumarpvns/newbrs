package com.elitecore.corenetvertex.pkg.notification;

/**
 * 
 * @author Jay Trivedi
 *
 */
public enum NotificationTemplateType {

	EMAIL("Email"),
	SMS("SMS");

	private String displayVal;

	NotificationTemplateType(String displayVal){
		this.displayVal = displayVal;
	}

	public String getDisplayVal(){
		return this.displayVal;
	}
	
}
