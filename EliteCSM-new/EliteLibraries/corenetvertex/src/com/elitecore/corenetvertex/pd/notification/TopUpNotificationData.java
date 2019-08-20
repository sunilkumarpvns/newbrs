package com.elitecore.corenetvertex.pd.notification;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.pd.topup.DataTopUpData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Objects;

@Entity(name = "com.elitecore.corenetvertex.pd.notification.TopUpNotificationData")
@Table(name = "TBLM_TOP_UP_NOTIFICATION")
public class TopUpNotificationData extends ResourceData {

    private static final long serialVersionUID = 1L;
    private static final String TOP_UP_NOTIFICATION = "TOP-UP-NOTIFICATION";
    private NotificationTemplateData smsTemplateData;
    private NotificationTemplateData emailTemplateData;
    private Integer threshold;
    private transient DataTopUpData dataTopUpData;
    private String dataTopUpId;


    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "EMAIL_TEMPLATE_ID", referencedColumnName = "ID")
    public NotificationTemplateData getEmailTemplateData() {
        return emailTemplateData;
    }

    public void setEmailTemplateData(NotificationTemplateData emailTemplateData) {
        this.emailTemplateData = emailTemplateData;
    }

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "SMS_TEMPLATE_ID", referencedColumnName = "ID")
    public NotificationTemplateData getSmsTemplateData() {
        return smsTemplateData;
    }

    public void setSmsTemplateData(NotificationTemplateData smsTemplateData) {
        this.smsTemplateData = smsTemplateData;
    }

    @Column(name = "THRESHOLD")
    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    @Column(name = "STATUS")
    @Override
    public String getStatus() {
        return super.getStatus();
    }


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DATA_TOPUP_ID")
    @XmlTransient
    @JsonIgnore
    public DataTopUpData getDataTopUpData() {
        return dataTopUpData;
    }

    public void setDataTopUpData(DataTopUpData dataTopUpData) {
        this.dataTopUpData = dataTopUpData;
        if (dataTopUpData != null) {
            setDataTopUpId(dataTopUpData.getId());
        }
    }




    @Transient
    public String getEmailTemplateId() {
        if (getEmailTemplateData() != null) {
            return getEmailTemplateData().getId();
        }
        return null;
    }

    public void setEmailTemplateId(String emailTemplateId) {
        if (Strings.isNullOrBlank(emailTemplateId) == false) {
            NotificationTemplateData emailTemplateData = new NotificationTemplateData();
            emailTemplateData.setId(emailTemplateId);
            this.emailTemplateData = emailTemplateData;
        }
    }


    @Override
    @Column(name="GROUPS")
    public String getGroups(){
        if(getDataTopUpData()!=null){
            return getDataTopUpData().getGroups();
        }else{
            return super.getGroups();
        }
    }


   @Transient
    public String getSmsTemplateId() {
        if (getSmsTemplateData() != null) {
            return getSmsTemplateData().getId();
        }
        return null;
    }

    public void setSmsTemplateId(String smsTemplateId) {
        if (Strings.isNullOrBlank(smsTemplateId) == false) {
            NotificationTemplateData smsTemplateData = new NotificationTemplateData();
            smsTemplateData.setId(smsTemplateId);
            this.setSmsTemplateData(smsTemplateData);
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject topUpNotification = new JsonObject();
        topUpNotification.addProperty("Email Template Data ", getEmailTemplateData() != null ? getEmailTemplateData().getName() : getEmailTemplateId());
        topUpNotification.addProperty("SMS Template Data", getSmsTemplateData() != null ? getSmsTemplateData().getName() : getSmsTemplateId());
        topUpNotification.addProperty("Threshold", threshold);
        return topUpNotification;
    }

    @Transient
    public String getDataTopUpId() {
        return dataTopUpId;
    }

    public void setDataTopUpId(String dataTopUpId) {
        this.dataTopUpId = dataTopUpId;
        if (this.dataTopUpData == null && Strings.isNullOrBlank(dataTopUpId) == false) {
            DataTopUpData dataTopUpData = new DataTopUpData();
            dataTopUpData.setId(dataTopUpId);
            this.dataTopUpData = dataTopUpData;
        }
    }



    @Override
    @Transient
    public String getResourceName() {
        if (Objects.isNull(getDataTopUpData())) {
            return TOP_UP_NOTIFICATION;
        }
        return getDataTopUpData().getName();
    }


    @Override
    @Transient
    public String getHierarchy() {
        return dataTopUpData.getHierarchy() + "<br>"+ getId() +"<br>";
    }

    @Override
    @Transient
    public ResourceData getAuditableResource() {
        return getDataTopUpData();
    }

    @Override
    @Transient
    public String getAuditableId() {
        return getDataTopUpData().getId();
    }
}