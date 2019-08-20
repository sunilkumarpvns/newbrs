package com.elitecore.corenetvertex.pkg.notification;

import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.core.validator.DataServiceTypeValidator;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.StringToIntegerAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name="TBLM_QUOTA_NOTIFICATION")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class QuotaNotificationData extends ResourceData implements Serializable {

    private static final long serialVersionUID = 1L;
    private transient PkgData pkgData;
    private RncProfileData quotaProfile;
    private NotificationTemplateData smsTemplateData;
    private NotificationTemplateData emailTemplateData;
    private DataServiceTypeData dataServiceTypeData;
    private Integer fupLevel;
    private AggregationKey aggregationKey;
    private Integer threshold;
    private transient String quotaProfileId;
    private transient String quotaProfileName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "QUOTA_PROFILE_ID")
    @XmlTransient
    public RncProfileData getQuotaProfile() {
        return quotaProfile;
    }

    public void setQuotaProfile(RncProfileData quotaProfile) {
        this.quotaProfile = quotaProfile;
    }

    @Column(name = "FUP_LEVEL")
    @XmlElement(name = "fup-level")
    @XmlJavaTypeAdapter(StringToIntegerAdapter.class)
    public Integer getFupLevel() {
        return fupLevel;
    }

    public void setFupLevel(Integer fupLevel) {
        this.fupLevel = fupLevel;
    }

    @Enumerated(EnumType.STRING)
    @Column(name="AGGREGATION_KEY")
    public AggregationKey getAggregationKey() {
        return aggregationKey;
    }
    public void setAggregationKey(AggregationKey aggregationKey) {
        this.aggregationKey = aggregationKey;
    }
    @Column(name="THRESHOLD")
    public Integer getThreshold() {
        return threshold;
    }
    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="PACKAGE_ID")
    @XmlTransient
    public PkgData getPkgData() {
        return pkgData;
    }
    public void setPkgData(PkgData pkgData) {
        this.pkgData = pkgData;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="DATA_SERVICE_TYPE_ID")
    @Import(required = true, validatorClass = DataServiceTypeValidator.class)
    @XmlElement(name = "dataServiceType")
    public DataServiceTypeData getDataServiceTypeData() {
        return dataServiceTypeData;
    }

    public void setDataServiceTypeData(DataServiceTypeData dataServiceTypeData) {
        this.dataServiceTypeData = dataServiceTypeData;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="EMAIL_TEMPLATE_ID")
    public NotificationTemplateData getEmailTemplateData() {
        return emailTemplateData;
    }
    public void setEmailTemplateData(NotificationTemplateData emailTemplateData) {
        this.emailTemplateData = emailTemplateData;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="SMS_TEMPLATE_ID")
    public NotificationTemplateData getSmsTemplateData() {
        return smsTemplateData;
    }
    public void setSmsTemplateData(NotificationTemplateData smsTemplateData) {
        this.smsTemplateData = smsTemplateData;
    }

    @Transient
    public String getQuotaProfileId() {
        if(quotaProfile != null){
            return quotaProfile.getId();
        }
        return quotaProfileId;
    }
    public void setQuotaProfileId(String quotaProfileId) {
        this.quotaProfileId = quotaProfileId;
    }

    @Transient
    public String getQuotaProfileName() {
        if(quotaProfile != null){
            return quotaProfile.getName();
        }
        return quotaProfileName;
    }
    public void setQuotaProfileName(String quotaProfileName) {
        this.quotaProfileName = quotaProfileName;
    }

    public void removeRelation(){
        quotaProfile.getQuotaNotificationDatas().remove(this);

    }

    @Column(name = "STATUS")
    @Override
    public String getStatus() {
        return super.getStatus();
    }

    @Transient
    @Override
    public ResourceData getAuditableResource() {
        return pkgData;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        QuotaNotificationData other = (QuotaNotificationData) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (getId().equals(other.getId()) == false)
            return false;
        return true;
    }

    @Override
    public JsonObject toJson(){
        JsonObject childJsonObject = new JsonObject();
        childJsonObject.addProperty(FieldValueConstants.AGGREGATION_KEY, aggregationKey.getVal());
        childJsonObject.addProperty(FieldValueConstants.FUP_LEVEL, fupLevel != null ? BalanceLevel.fromVal(fupLevel).getDisplayVal():null);
        childJsonObject.addProperty(FieldValueConstants.THRESHOLD, threshold);
        if (Objects.isNull(emailTemplateData) == false) {
            childJsonObject.addProperty(FieldValueConstants.EMAIL_TEMPLATE, emailTemplateData.getName());
        }
        if (Objects.isNull(smsTemplateData) == false) {
            childJsonObject.addProperty(FieldValueConstants.SMS_TEMPLATE, smsTemplateData.getName());
        }
        JsonArray serviceJsonArray = new JsonArray();
        serviceJsonArray.add(childJsonObject);
        JsonObject serviceJsonObject = new JsonObject();
        serviceJsonObject.add(dataServiceTypeData.getName(),serviceJsonArray);
        JsonObject quotaProfileJsonObject = new JsonObject();
        quotaProfileJsonObject.add(quotaProfile.getName(),serviceJsonObject);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("Quota Notification", quotaProfileJsonObject);
        return jsonObject;
    }

    public QuotaNotificationData deepClone() throws CloneNotSupportedException {
        QuotaNotificationData newData = (QuotaNotificationData) this.clone();
        newData.emailTemplateData = emailTemplateData == null ? null : emailTemplateData.deepClone();
        newData.smsTemplateData = smsTemplateData ==null ? null : smsTemplateData.deepClone();
        newData.dataServiceTypeData = dataServiceTypeData == null ? null : dataServiceTypeData.deepClone();
        newData.quotaProfile = quotaProfile == null ? null : quotaProfile.copyModel();
        newData.pkgData = pkgData;
        return newData;
    }

    @Transient
    public String getName() {
        return "";
    }


    @Override
    @Transient
    public String getHierarchy() {
        return getId() + "<br>" + getName();
    }
    @Override
    @Transient
    public String getResourceName() {
        return getName();
    }

    public QuotaNotificationData copyModel(PkgData pkgData) {
        QuotaNotificationData newData = new QuotaNotificationData();
        newData.aggregationKey = this.aggregationKey;
        newData.dataServiceTypeData = this.dataServiceTypeData;
        newData.emailTemplateData = this.emailTemplateData;
        newData.fupLevel = this.fupLevel;
        newData.smsTemplateData = this.smsTemplateData;
        newData.threshold = this.threshold;

        for(RncProfileData rncProfileDetail : pkgData.getRncProfileDatas()){
            if(rncProfileDetail.getName().equalsIgnoreCase(this.quotaProfile.getName())){
                newData.quotaProfileId = rncProfileDetail.getId();
                newData.quotaProfileName = rncProfileDetail.getName();
                newData.quotaProfile = rncProfileDetail;
            }
        }
        return newData;
    }
}
