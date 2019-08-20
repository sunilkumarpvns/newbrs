package com.elitecore.corenetvertex.service.notification;

import java.io.Serializable;

import com.elitecore.corenetvertex.constants.EventType;
import com.elitecore.corenetvertex.constants.NotificationRecipient;
import com.elitecore.corenetvertex.service.notification.Template;

/**
 * 
 * @author Jay Trivedi
 *
 */
public class NotificationEvent implements Serializable{

	private static final long serialVersionUID = 1L;
	private EventType eventType;
	private Template emailTemplate;
	private Template smsTemplate;
	private NotificationRecipient notificationRecipient;
	
	public NotificationEvent(EventType eventType, Template emailTemplate, Template smsTemplate) {
		this.eventType = eventType;
		this.emailTemplate = emailTemplate;
		this.smsTemplate = smsTemplate;
		this.notificationRecipient = NotificationRecipient.SELF;
	}

	public EventType getEventType() {
		return eventType;
	}

	public Template getEmailTemplate() {
		return emailTemplate;
	}

	public Template getSMSTemplate() {
		return smsTemplate;
	}

	public NotificationRecipient getNotificationRecipient() {
		return notificationRecipient;
	}
	
}
