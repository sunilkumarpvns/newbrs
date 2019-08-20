package com.elitecore.corenetvertex.constants;


public enum NotificationRecipient {

	SELF(1),
	PARENT(2);
	
	public final int recipient;
	
	private NotificationRecipient(int val) {
		this.recipient = val;
	}
	
	public static NotificationRecipient fromValue(int recipient) {
		if (SELF.recipient == recipient) {
			return SELF;
		} else if (PARENT.recipient == recipient) {
			return PARENT;
		}
	    return null;
	}
	
}
