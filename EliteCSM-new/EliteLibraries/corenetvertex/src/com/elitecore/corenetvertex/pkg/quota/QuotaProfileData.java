package com.elitecore.corenetvertex.pkg.quota;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CounterPresence;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.core.validator.QuotaProfileDetailValidator;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.notification.UsageNotificationData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Entity(name="com.elitecore.corenetvertex.pkg.quota.QuotaProfileData")
@Table(name="TBLM_QUOTA_PROFILE")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class QuotaProfileData extends ResourceData implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final ToStringStyle QUOTA_PROFILE_DATA_TO_STRING_STYLE = new QuotaProfileDataToString();
	@SerializedName(FieldValueConstants.NAME)private String name;
	private String description;
	private transient PkgData pkgData;
	private List<QuotaProfileDetailData> quotaProfileDetailDatas;
	private Map<Integer, QuotaProfileDetailData> fupLevelMap;
	private Integer usagePresence = CounterPresence.MANDATORY.getVal();
	private BalanceLevel balanceLevel = BalanceLevel.HSQ;
	private Integer  renewalInterval;
	private String renewalIntervalUnit;

	private transient Set<QosProfileData> qosProfiles;
	private transient List<UsageNotificationData> usageNotificationDatas;

	public QuotaProfileData() {
		quotaProfileDetailDatas = Collectionz.newArrayList();
		usageNotificationDatas = Collectionz.newArrayList();
		fupLevelMap = Maps.newHashMap();
	}

	@Column(name="NAME")
	@XmlElement(name="quota-profile-name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}

	@Override
	public void setStatus(String status) {//NOSONAR
		//Removing this method will cause problem when importing package.
		super.setStatus(status);
	}


	@Column(name="DESCRIPTION")
	@XmlElement(name="description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;

	}
	
	@Column(name = "USAGE_AVAILABILITY")
	@XmlElement(name = "usage-presence")
	public Integer getUsagePresence() {
		return usagePresence;
	}

	public void setUsagePresence(Integer usagePresence) {
		this.usagePresence = usagePresence;
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


	@OneToMany(cascade={CascadeType.ALL},fetch=FetchType.LAZY,mappedBy="quotaProfile",orphanRemoval=true)
	@XmlElementWrapper(name="quota-profile-details")
	@XmlElement(name="quota-profile-detail")
	@Fetch(FetchMode.SUBSELECT)
	@Import(required = true, validatorClass = QuotaProfileDetailValidator.class)
	public List<QuotaProfileDetailData> getQuotaProfileDetailDatas() {
		return quotaProfileDetailDatas;
	}

	public void setQuotaProfileDetailDatas(
			List<QuotaProfileDetailData> quotaProfileDetailDatas) {
		this.quotaProfileDetailDatas = quotaProfileDetailDatas;
	}

	@Transient
	@XmlTransient
	public Map<Integer, QuotaProfileDetailData> getFupLevelMap() {
		return fupLevelMap;
	}
	public void setFupLevelMap(Map<Integer, QuotaProfileDetailData> fupLevelMap) {
		this.fupLevelMap = fupLevelMap;
	}


	@OneToMany(cascade={CascadeType.ALL},fetch=FetchType.LAZY,mappedBy="quotaProfile")
	@Fetch(FetchMode.SUBSELECT)
	@Where(clause="STATUS != 'DELETED'")
	@XmlTransient
	public Set<QosProfileData> getQosProfiles() {
		return qosProfiles;
	}
	public void setQosProfiles(Set<QosProfileData> qosProfiles) {
		this.qosProfiles = qosProfiles;
	}
	
	@Transient
	@Override
	public ResourceData getAuditableResource() {
		return pkgData;
	}

	@Override
	public String toString() {

		ToStringBuilder toStringBuilder = new ToStringBuilder(this, QUOTA_PROFILE_DATA_TO_STRING_STYLE).append("Name", name);

		toStringBuilder.append("");
		if (Collectionz.isNullOrEmpty(quotaProfileDetailDatas) == false) {

			for (QuotaProfileDetailData quotaProfileDetailData : quotaProfileDetailDatas) {
				toStringBuilder.append("Quota Profile Detail Data", quotaProfileDetailData);
			}
		} else {

			toStringBuilder.append("No quota profile detail(s) found");
		}

		return toStringBuilder.toString();
	}

	@Enumerated(EnumType.STRING)
	@Column(name="BALANCE_LEVEL")
	@XmlElement(name="balance-level")
	public BalanceLevel getBalanceLevel() {
		return balanceLevel;
	}

	public void setBalanceLevel(BalanceLevel balanceLevel) {
		this.balanceLevel = balanceLevel;
	}

    public QuotaProfileData copyModel() {
		QuotaProfileData newData = new QuotaProfileData();

		newData.description = this.description;
		newData.name = this.name;
		newData.balanceLevel = this.balanceLevel;
		newData.renewalInterval = this.renewalInterval;
		newData.renewalIntervalUnit = this.renewalIntervalUnit;
		newData.usagePresence = this.usagePresence;
		newData.setQosProfiles(null);

		List<QuotaProfileDetailData> quotaProfileDetails = Collectionz.newArrayList();
		for (QuotaProfileDetailData quotaProfileDetailData:this.quotaProfileDetailDatas) {
			QuotaProfileDetailData quotaProfileDetailDataCopy = quotaProfileDetailData.copyModel();
			quotaProfileDetailDataCopy.setId(null);
			quotaProfileDetailDataCopy.setQuotaProfile(newData);
			quotaProfileDetails.add(quotaProfileDetailDataCopy);
		}
		newData.setQuotaProfileDetailDatas(quotaProfileDetails);
		return newData;
    }

    private static final class QuotaProfileDataToString extends ToStringStyle.CustomToStringStyle {

		private static final long serialVersionUID = 1L;

		QuotaProfileDataToString() {
			super();
			this.setContentStart(SystemUtils.LINE_SEPARATOR);
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(4) + getTabs(1));
		}
	}

	@OneToMany(cascade={CascadeType.ALL},fetch=FetchType.LAZY,mappedBy="quotaProfile",orphanRemoval = true)
	@Fetch(FetchMode.SUBSELECT)
	@XmlTransient
	public List<UsageNotificationData> getUsageNotificationDatas() {
		return usageNotificationDatas;
	}
	public void setUsageNotificationDatas(
			List<UsageNotificationData> usageNotificationDatas) {
		this.usageNotificationDatas = usageNotificationDatas;
	}

	@Column(name = "RENEWAL_INTERVAL")
	@XmlElement(name = "renewal-interval")
	public Integer getRenewalInterval() {
		return renewalInterval;
	}

	public void setRenewalInterval(Integer renewalInterval) {
		this.renewalInterval = renewalInterval;
	}

	@Column(name="RENEWAL_INTERVAL_UNIT")
	@XmlElement(name="renewal-interval-unit")
	public String getRenewalIntervalUnit() {
		return renewalIntervalUnit;
	}

	public void setRenewalIntervalUnit(String renewalIntervalUnit) {
		this.renewalIntervalUnit = renewalIntervalUnit;
	}

	public void removeRelation(){
		pkgData.getUsageNotificationDatas().removeAll(this.getUsageNotificationDatas());
		pkgData.getQuotaProfiles().remove(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuotaProfileData other = (QuotaProfileData) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Transient
	@Override
	public String getAuditableId() {
		return pkgData.getId();
	}

	@Override
	@Transient
	public String getHierarchy() {
		return pkgData.getHierarchy() +"<br>"+ getId() + "<br>"+ name;
	}

	@Override
	public JsonObject toJson(){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("Name", name);
		jsonObject.addProperty("Description", description);
		if(quotaProfileDetailDatas != null){
			Collections.sort(quotaProfileDetailDatas, (QuotaProfileDetailData q1, QuotaProfileDetailData q2) ->
					new CompareToBuilder().append(q1.getFupLevel(), q2.getFupLevel()).append(q1.getDataServiceTypeData().getName(), q2.getDataServiceTypeData().getName()) .toComparison()
			);
			JsonObject fupLevelJsonObject = null;
			JsonArray fupLevelJsonArray  = new JsonArray();
			Integer fupLevel = null;
			JsonArray serviceJsonArray = new JsonArray();
			for(QuotaProfileDetailData quotaProfileDetailData : quotaProfileDetailDatas){
				if(fupLevel == null || fupLevel.intValue() != quotaProfileDetailData.getFupLevel().intValue()){
					if(fupLevel != null){
						fupLevelJsonObject.add((fupLevel > 0 ? ("FUP" + fupLevel) : "HSQ"), serviceJsonArray);
						fupLevelJsonArray.add(fupLevelJsonObject);
						serviceJsonArray = new JsonArray();
					}
					fupLevel = quotaProfileDetailData.getFupLevel();
					fupLevelJsonObject = new JsonObject();
				}
				serviceJsonArray.add(quotaProfileDetailData.toJson());
			}
			if(fupLevelJsonObject!=null){
				fupLevelJsonObject.add((fupLevel > 0 ? ("FUP" + fupLevel) : "HSQ"), serviceJsonArray);
			}
			fupLevelJsonArray.add(fupLevelJsonObject);
			jsonObject.add("QuotaProfile Details", fupLevelJsonArray);
		}
		return jsonObject;
	}
	
	public QuotaProfileData deepClone() throws CloneNotSupportedException {
		QuotaProfileData newData = (QuotaProfileData) this.clone();
		
		
		newData.quotaProfileDetailDatas = Collectionz.newArrayList();
		if (quotaProfileDetailDatas.isEmpty() == false) {
			for (QuotaProfileDetailData quotaDetail : quotaProfileDetailDatas) {
				QuotaProfileDetailData clonedQuotaDetail = quotaDetail.deepClone();
				clonedQuotaDetail.setQuotaProfile(newData);
				newData.quotaProfileDetailDatas.add(clonedQuotaDetail);
			}
		}
		
		newData.fupLevelMap = Maps.newHashMap();
		if (fupLevelMap.isEmpty() == false) {
			for (Entry<Integer, QuotaProfileDetailData> entry : fupLevelMap.entrySet()) {
				QuotaProfileDetailData clonedQuotaDetail = entry.getValue().deepClone();
				clonedQuotaDetail.setQuotaProfile(newData);
				newData.fupLevelMap.put(entry.getKey(), clonedQuotaDetail);
			}
		}
		
		newData.usageNotificationDatas = Collectionz.newArrayList();
		if (usageNotificationDatas.isEmpty() == false) {
			for (UsageNotificationData notificationData : usageNotificationDatas) {
				UsageNotificationData clonedUsageNotification = notificationData.deepClone();
				clonedUsageNotification.setQuotaProfile(newData);
				newData.usageNotificationDatas.add(clonedUsageNotification);
			}
		}

		newData.qosProfiles = qosProfiles;
		newData.pkgData = pkgData;
		return newData;
	}

	@Override
	@Transient
	public String getGroups() {
		return pkgData.getGroups();
	}

	@Override
	@Transient
	public String getResourceName() {
		return getName();
	}
}
