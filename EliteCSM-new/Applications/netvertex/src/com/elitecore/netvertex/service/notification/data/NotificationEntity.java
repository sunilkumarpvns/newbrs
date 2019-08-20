package com.elitecore.netvertex.service.notification.data;

import com.elitecore.core.servicex.EliteScheduledService;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

public class NotificationEntity extends EliteScheduledService.TargetEntity {
	private String notificationId;
	private String emailSubject;
	private String emailData;
	private String toEmailAddress;
	private String smsData;
	private String toSMSAddress;
	private String sourceId;
	private PCRFResponse pcrfResponse;
	private boolean emailSendStatus;
	private boolean smsSendStatus;
	
	
	
	public NotificationEntity(String notificationId, String emailSubject, String emailData,
							  String toEmailAddress, String smsData, String toSMSAddress, PCRFResponse pcrfResponse,
							  boolean emailStatus, boolean smsStatus) {
		super(notificationId);
		this.notificationId = notificationId;
		this.emailSubject = emailSubject;
		this.emailData = emailData;
		this.toEmailAddress = toEmailAddress;
		this.smsData = smsData;
		this.toSMSAddress = toSMSAddress;
		this.pcrfResponse = pcrfResponse;
		this.emailSendStatus = emailStatus;
		this.smsSendStatus = smsStatus;
	}

	public NotificationEntity(String id) {
		super(id);
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailData() {
		return emailData;
	}

	public void setEmailData(String emailData) {
		this.emailData = emailData;
	}

	public String getToEmailAddress() {
		return toEmailAddress;
	}

	public void setToEmailAddress(String toEmailAddress) {
		this.toEmailAddress = toEmailAddress;
	}
	public String getSMSData() {
		return smsData;
	}

	public void setSmsData(String smsData) {
		this.smsData = smsData;
	}

	public String getToSMSAddress() {
		return toSMSAddress;
	}

	public void setToSMSAddress(String toSMSAddress) {
		this.toSMSAddress = toSMSAddress;
	}

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}


	public PCRFResponse getPcrfResponse() {
		return pcrfResponse;
	}

   

	public boolean isEmailSend(){
		return emailSendStatus;
	}
	
	public boolean isSmsSend(){
		return smsSendStatus;
	}
	

}
