package com.elitecore.corenetvertex.pd.rncpackage.notification;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pd.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@Table(name="TBLM_RNC_NOTIFICATION")
public class RncNotificationData extends ResourceData implements Serializable {


    private transient RncPackageData rncPackageData;
    private RateCardData rateCardData;
    private NotificationTemplateData emailTemplateData;
    private NotificationTemplateData smsTemplateData;
    private Integer threshold;
    private String rateCardId;
    private String rncPackageId;
    private String emailTemplateId;
    private String smsTemplateId;


    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="RNC_PACKAGE_ID")
    @JsonIgnore
    public RncPackageData getRncPackageData() {
        return rncPackageData;
    }

    public void setRncPackageData(RncPackageData rncPackageData) {
        if (rncPackageData != null) {
            setRncPackageId(rncPackageData.getId());
        }
        this.rncPackageData = rncPackageData;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RATE_CARD_ID")
    @JsonIgnore
    public RateCardData getRateCardData() {
        return rateCardData;
    }

    public void setRateCardData(RateCardData rateCardData) {
        if (rateCardData != null) {
            setRateCardId(rateCardData.getId());
        }
        this.rateCardData = rateCardData;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="EMAIL_TEMPLATE_ID")
    @JsonIgnore
    public NotificationTemplateData getEmailTemplateData() {
        return emailTemplateData;
    }

    public void setEmailTemplateData(NotificationTemplateData emailTemplateData) {
        if (emailTemplateData != null) {
            setEmailTemplateId(emailTemplateData.getId());
        }
        this.emailTemplateData = emailTemplateData;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="SMS_TEMPLATE_ID")
    @JsonIgnore
    public NotificationTemplateData getSmsTemplateData() {
        return smsTemplateData;
    }

    public void setSmsTemplateData(NotificationTemplateData smsTemplateData) {
        if (smsTemplateData != null) {
            setSmsTemplateId(smsTemplateData.getId());
        }
        this.smsTemplateData = smsTemplateData;
    }

    @Column(name="THRESHOLD")
    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    @Transient
    public String getRateCardId() {
        return rateCardId;
    }

    public void setRateCardId(String rateCardId) {
        this.rateCardId = rateCardId;
    }

    @Transient
    public String getRncPackageId() {
        return rncPackageId;
    }

    public void setRncPackageId(String rncPackageId) {
        this.rncPackageId = rncPackageId;
    }

    @Transient
    public String getEmailTemplateId() {
        return emailTemplateId;
    }

    public void setEmailTemplateId(String emailTemplateId) {
        this.emailTemplateId = emailTemplateId;
    }

    @Transient
    public String getSmsTemplateId() {
        return smsTemplateId;
    }

    public void setSmsTemplateId(String smsTemplateId) {
        this.smsTemplateId = smsTemplateId;
    }

    @Override
    @Transient
    public String getGroups() {
        return rncPackageData.getGroups();
    }


    @Transient
    @Override
    @JsonIgnore
    public String getResourceName() {
        return rateCardData.getName();
    }

    @Override
    public JsonObject toJson() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.THRESHOLD,threshold);
        JsonObject nonMonetaryJson = new JsonObject();
        nonMonetaryJson.addProperty(FieldValueConstants.NAME,rateCardData.getName());
        jsonObject.add(FieldValueConstants.NON_MONETARY_RATE_CARD,nonMonetaryJson);
        if(emailTemplateData != null) {
            JsonObject emailTemplateJson = new JsonObject();
            emailTemplateJson.addProperty(FieldValueConstants.NAME,emailTemplateData.getName());
            jsonObject.add(FieldValueConstants.EMAIL_TEMPLATE, emailTemplateJson);
        }
        if(smsTemplateData != null) {
            JsonObject smsTemplateJson = new JsonObject();
            smsTemplateJson.addProperty(FieldValueConstants.NAME,smsTemplateData.getName());
            jsonObject.add(FieldValueConstants.SMS_TEMPLATE, smsTemplateJson);
        }
        return jsonObject;
    }

    @Transient
    @Override
    public String getAuditableId() {
        return rncPackageData.getId();
    }

    @Transient
    @Override
    public ResourceData getAuditableResource() {
        return rncPackageData;
    }

    @Override
    @Transient
    public String getHierarchy() {
        return rncPackageData.getHierarchy() + "<br>" + getId() + "<br>" + rateCardData.getName();
    }


    public RncNotificationData copyModel(RncPackageData rncPackage){
        RncNotificationData newRncNotificationData = new RncNotificationData();
        newRncNotificationData.setEmailTemplateData(this.getEmailTemplateData());
        newRncNotificationData.setSmsTemplateData(this.getSmsTemplateData());
        newRncNotificationData.setThreshold(this.getThreshold());
        newRncNotificationData.setGroups(this.getGroups());
        for(RateCardData rateCardData : rncPackage.getRateCardData()){
            if(rateCardData.getName().equalsIgnoreCase(this.rateCardData.getName())){
                newRncNotificationData.setRateCardData(rateCardData);
                newRncNotificationData.setCreatedDateAndStaff(rncPackage.getCreatedByStaff());
            }
        }
        newRncNotificationData.setCreatedDateAndStaff(rncPackage.getCreatedByStaff());
        return newRncNotificationData;
    }
}
