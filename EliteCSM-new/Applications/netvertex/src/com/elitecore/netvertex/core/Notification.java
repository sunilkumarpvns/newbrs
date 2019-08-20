package com.elitecore.netvertex.core;

import com.elitecore.corenetvertex.constants.NotificationRecipient;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.sql.Timestamp;

public class Notification {

    private Template emailTemplate;
    private Template smsTemplate;
    private Timestamp validityDate;
    private NotificationRecipient recipient;
    private PCRFResponse pcrfResponse;
    private String emailSendStatus;
    private String smsSendStatus;

    public Notification(Template emailTemplate, Template smsTemplate, PCRFResponse pcrfResponse, Timestamp validityDate,
                        NotificationRecipient recipient, String emailSendStatus, String smsSendStatus) {
        this.emailTemplate = emailTemplate;
        this.smsTemplate = smsTemplate;
        this.pcrfResponse = pcrfResponse;
        this.validityDate = validityDate;
        this.recipient = recipient;
        this.emailSendStatus = emailSendStatus;
        this.smsSendStatus = smsSendStatus;
    }

    public String getEmailSubject() {
        return emailTemplate == null ? null : emailTemplate.getSubject();
    }

    public String getEmailTemplateData() {
        return emailTemplate == null ? null : emailTemplate.getTemplateData();
    }

    public String getSMSTemplateData() {
        return smsTemplate == null ? null : smsTemplate.getTemplateData();
    }

    public PCRFResponse getPCRFResponse() {
        return pcrfResponse;
    }

    public Timestamp getValidityDate(){
        return validityDate;
    }

    public int getRecipient() {
        return recipient.recipient;
    }

    public void setRecipient(NotificationRecipient recipient) {
        this.recipient = recipient;
    }

    public void setPcrfResponse(PCRFResponse pcrfResponse) {
        this.pcrfResponse = pcrfResponse;
    }

    public String getEmailSendStatus() {
        return emailSendStatus;
    }

    public void setEmailSendStatus(String emailSendStatus) {
        this.emailSendStatus = emailSendStatus;
    }

    public String getSmsSendStatus() {
        return smsSendStatus;
    }

    public void setSmsSendStatus(String smsSendStatus) {
        this.smsSendStatus = smsSendStatus;
    }
}
