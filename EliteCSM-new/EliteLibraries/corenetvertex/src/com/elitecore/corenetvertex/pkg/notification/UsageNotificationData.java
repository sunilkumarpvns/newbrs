package com.elitecore.corenetvertex.pkg.notification;

import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.MeteringType;
import com.elitecore.corenetvertex.core.validator.DataServiceTypeValidator;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.sm.ResourceData;
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
import java.io.Serializable;


@Entity
@Table(name="TBLM_USAGE_NOTIFICATION")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class UsageNotificationData extends ResourceData implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	private transient PkgData pkgData;
	private QuotaProfileData quotaProfile;
	private NotificationTemplateData smsTemplateData;
	private NotificationTemplateData emailTemplateData;
	private DataServiceTypeData dataServiceTypeData;
	private MeteringType meteringType;
	private AggregationKey aggregationKey;
	private Integer threshold;
	private String displayMeteringValue;
	private String displayAggregationKeyValue;
	private transient String quotaProfileId;
	private transient String quotaProfileName;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "QUOTA_PROFILE_ID")
	@XmlTransient
	public QuotaProfileData getQuotaProfile() {
		return quotaProfile;
	}

	public void setQuotaProfile(QuotaProfileData quotaProfile) {
		this.quotaProfile = quotaProfile;
	}

	@Enumerated(EnumType.STRING)
	@Column(name="METERING_TYPE")
	public MeteringType getMeteringType() {
		return meteringType;
	}
	public void setMeteringType(MeteringType meteringType) {
		this.meteringType = meteringType;
		setDisplayMeteringValue(meteringType.getVal());
	}

	@Enumerated(EnumType.STRING)
	@Column(name="AGGREGATION_KEY")
	public AggregationKey getAggregationKey() {
		return aggregationKey;
	}
	public void setAggregationKey(AggregationKey aggregationKey) {
		this.aggregationKey = aggregationKey;
		setDisplayAggregationKeyValue(aggregationKey.getVal());
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
	@XmlTransient
	public String getDisplayMeteringValue() {
		return displayMeteringValue;
	}
	public void setDisplayMeteringValue(String displayMeteringValue) {
		this.displayMeteringValue = displayMeteringValue;
	}

	@Transient
	@XmlTransient
	public String getDisplayAggregationKeyValue() {
		return displayAggregationKeyValue;
	}
	public void setDisplayAggregationKeyValue(String displayAggregationKeyValue) {
		this.displayAggregationKeyValue = displayAggregationKeyValue;
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
		quotaProfile.getUsageNotificationDatas().remove(this);

	}
	
	@Column(name = "STATUS")
	@Override
	public String getStatus() {
		return super.getStatus();
	}

	@Override
	public void setStatus(String status) {
		super.setStatus(status);
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
		UsageNotificationData other = (UsageNotificationData) obj;
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
		childJsonObject.addProperty(FieldValueConstants.METERING_TYPE, meteringType.val);
		childJsonObject.addProperty(FieldValueConstants.THRESHOLD, threshold);
		JsonArray serviceJsonArray = new JsonArray();
		serviceJsonArray.add(childJsonObject);
		JsonObject serviceJsonObject = new JsonObject();
		serviceJsonObject.add(dataServiceTypeData.getName(),serviceJsonArray);
		JsonObject quotaProfileJsonObject = new JsonObject();
		if(quotaProfile != null && quotaProfile.getName() != null) {
			quotaProfileJsonObject.add(quotaProfile.getName(), serviceJsonObject);
		} else if(quotaProfileName != null){
			quotaProfileJsonObject.add(quotaProfileName, serviceJsonObject);
		}
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("Usage Notification", quotaProfileJsonObject);
		return jsonObject;
	}
	
	public UsageNotificationData deepClone() throws CloneNotSupportedException {
		UsageNotificationData newData = (UsageNotificationData) this.clone();
		newData.emailTemplateData = emailTemplateData == null ? null : emailTemplateData.deepClone();
		newData.smsTemplateData = smsTemplateData ==null ? null : smsTemplateData.deepClone();		
		newData.dataServiceTypeData = dataServiceTypeData == null ? null : dataServiceTypeData.deepClone();
		newData.quotaProfile = quotaProfile == null ? null : quotaProfile.deepClone();
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

	public UsageNotificationData copyModel(PkgData pkgData) {
		UsageNotificationData newData = new UsageNotificationData();
		newData.aggregationKey = this.aggregationKey;
		newData.dataServiceTypeData = this.dataServiceTypeData;
		newData.displayAggregationKeyValue = this.displayAggregationKeyValue;
		newData.displayMeteringValue = this.displayMeteringValue;
		newData.emailTemplateData = this.emailTemplateData;
		newData.meteringType = this.meteringType;
		newData.smsTemplateData = this.smsTemplateData;
		newData.threshold = this.threshold;

		for(QuotaProfileData quotaProfileData : pkgData.getQuotaProfiles()){
			if(quotaProfileData.getName().equalsIgnoreCase(this.quotaProfile.getName())){
				newData.quotaProfileId = quotaProfileData.getId();
				newData.quotaProfileName = quotaProfileData.getName();
				newData.quotaProfile = quotaProfileData;
			}
		}

		return newData;
	}
}
