package com.elitecore.corenetvertex.pkg.qos;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.core.validator.QosProfileDetailValidator;
import com.elitecore.corenetvertex.core.validator.TimePeriodDataValidator;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileData;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.LongToStringAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.SystemUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "TBLM_QOS_PROFILE")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class QosProfileData extends ResourceData implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final ToStringStyle QOS_PROFILE_DATA_TO_STRING_STYLE = new QoSProfileDataToString();
	@SerializedName(FieldValueConstants.NAME)private String name;
	private String description;
	private transient PkgData pkgData;

	private String accessNetwork;
	private String advancedCondition;
	private Long duration;
	private Integer orderNo;
	private transient List<DeviceProfileData> deviceProfiles;
	private transient QuotaProfileData quotaProfile;
	private transient String quotaProfileId;
	private transient String quotaProfileName;

	private List<TimePeriodData> timePeriodDataList;
	private List<QosProfileDetailData> qosProfileDetailDataList;
	private transient SyQuotaProfileData syQuotaProfileData;
    private transient RncProfileData rncProfileData;
    private transient DataRateCardData rateCardData;
	private transient String rateCardId;
	private transient String rateCardName;


	public QosProfileData(){
		timePeriodDataList = Collectionz.newArrayList();
		qosProfileDetailDataList = Collectionz.newArrayList();
	}

	@Column(name = "NAME")
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
	public void setStatus(String status) { //NOSONAR-- REQUIRED FOR IMPORT/EXPORT
		super.setStatus(status);
	}

	@Column(name = "ACCESS_NETWORK")
	@XmlElement(name = "access-network")
	public String getAccessNetwork() {
		return accessNetwork;
	}

	public void setAccessNetwork(String accessNetwork) {
		this.accessNetwork = accessNetwork;
	}

	@Column(name = "ADVANCED_CONDITION")
	public String getAdvancedCondition() {
		return advancedCondition;
	}

	public void setAdvancedCondition(String advancedCondition) {
		this.advancedCondition = advancedCondition;
	}

	@Column(name = "DURATION")
	@XmlJavaTypeAdapter(LongToStringAdapter.class)
	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	@Column(name = "ORDER_NO")
	public Integer getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "qosProfile")
	@Transient
	public List<DeviceProfileData> getDeviceProfiles() {
		return deviceProfiles;
	}

	public void setDeviceProfiles(List<DeviceProfileData> deviceProfiles) {
		this.deviceProfiles = deviceProfiles;
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


	@Column(name="DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;

	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="QUOTA_PROFILE_ID")
	@XmlTransient
	public QuotaProfileData getQuotaProfile() {
		return quotaProfile;
	}

	public void setQuotaProfile(QuotaProfileData quotaProfile) {
		this.quotaProfile = quotaProfile;
	}

	@Transient
	@XmlElement(name="quota-profile-id")
	public String getQuotaProfileId() {
		return quotaProfileId;
	}

	public void setQuotaProfileId(String quotaProfileId) {
		this.quotaProfileId = quotaProfileId;
	}

	@Transient
	@XmlElement(name="quota-profile-name")
	public String getQuotaProfileName() {
		return quotaProfileName;
	}

	public void setQuotaProfileName(String quotaProfileName) {
		this.quotaProfileName = quotaProfileName;
	}

	@OneToMany(cascade={CascadeType.ALL},fetch = FetchType.LAZY, mappedBy = "qosProfile",orphanRemoval = true)
	@Fetch(FetchMode.SUBSELECT)
	@XmlElementWrapper(name="time-periods")
	@XmlElement(name="time-period")
	@Import(required = true, validatorClass = TimePeriodDataValidator.class)
	public List<TimePeriodData> getTimePeriodDataList() {
		return timePeriodDataList;
	}

	public void setTimePeriodDataList(List<TimePeriodData> timePeriodDataList) {
		this.timePeriodDataList = timePeriodDataList;
	}
	
	@OneToMany(cascade={CascadeType.ALL},fetch = FetchType.LAZY, mappedBy = "qosProfile",orphanRemoval = false)
	@Fetch(FetchMode.SUBSELECT)
	@OrderBy("FUP_LEVEL")
	@XmlElementWrapper(name="qos-profile-details")
	@XmlElement(name="qos-profile-detail")
	@Import(required = true, validatorClass = QosProfileDetailValidator.class)
	public List<QosProfileDetailData> getQosProfileDetailDataList() {
		return qosProfileDetailDataList;
	}
	public void setQosProfileDetailDataList(
			List<QosProfileDetailData> qosProfileDetailDataList) {
		this.qosProfileDetailDataList = qosProfileDetailDataList;
	}

	public void removeRelation(){
		pkgData.getQosProfiles().remove(this);
		if(this.getQuotaProfile() != null){ 
			quotaProfile.getQosProfiles().remove(this);
		}
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="SY_QUOTA_PROFILE_ID")
	@XmlTransient
	public SyQuotaProfileData getSyQuotaProfileData() {
		return syQuotaProfileData;
	}

	public void setSyQuotaProfileData(SyQuotaProfileData syQuotaProfileData) {
		this.syQuotaProfileData = syQuotaProfileData;
	}


	@Override
	public String toString() {

		return toString(QOS_PROFILE_DATA_TO_STRING_STYLE);
	}

	public String toString(ToStringStyle toStringStyle) {

		ToStringBuilder toStringBuilder = new ToStringBuilder(this, toStringStyle)
		.append("Name", name)
		.append("Access Network", accessNetwork)
		.append("Advanced Condition", advancedCondition)
		.append("Duration", duration)
		.append("Order No", orderNo)
		.append("Quota Profile Attached", quotaProfile != null ? quotaProfile.getName() : "N/A")
		.append("Data Rate Card Attached", rateCardData != null ? rateCardData.getName() : "N/A")
		.append("Rate Card Attached", rateCardData != null ? rateCardData.getName() : "N/A");

		if (Collectionz.isNullOrEmpty(timePeriodDataList) == false) {
			
			toStringBuilder.append("Time Periods" + SystemUtils.LINE_SEPARATOR);
			for (TimePeriodData timePeriodData : timePeriodDataList) {
				toStringBuilder.append(timePeriodData);
			}
		} else {
			toStringBuilder.append("Time period(s) not configured");
		}
		toStringBuilder.append("\t");
		if (Collectionz.isNullOrEmpty(qosProfileDetailDataList) == false) {
			for (QosProfileDetailData qosProfileData : qosProfileDetailDataList) {

				toStringBuilder.append("QoS Profile Detail", qosProfileData);
			}
		} else {

			toStringBuilder.append("No QoS profile details found");
		}

		return toStringBuilder.toString();

	}

	private static final class QoSProfileDataToString extends ToStringStyle.CustomToStringStyle {

		private static final long serialVersionUID = 1L;

		QoSProfileDataToString() {
			super();
			this.setContentStart(SystemUtils.LINE_SEPARATOR);
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(4) + getTabs(1));
		}
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
		QosProfileData other = (QosProfileData) obj;
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
	@Override
	public JsonObject toJson(){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("Name", name);
		jsonObject.addProperty("Description", description);
		jsonObject.addProperty("Advance Condition", advancedCondition);
		jsonObject.addProperty("Access Network", accessNetwork);
		jsonObject.addProperty("Duration",duration);
		jsonObject.addProperty("Order Number", orderNo);
		if(timePeriodDataList != null){
			JsonArray jsonArray = new JsonArray();
			for(TimePeriodData timePeriodData : timePeriodDataList){ 
				jsonArray.add(timePeriodData.toJson());
			}
			jsonObject.add("Time Based Conditions", jsonArray);
		}
		if(qosProfileDetailDataList != null){
			JsonArray jsonArray = new JsonArray();
			for(QosProfileDetailData qosProfileDetailData : qosProfileDetailDataList){
				jsonArray.add(qosProfileDetailData.toJson());
			}
			jsonObject.add("QosProfile Details", jsonArray);
		}
		return jsonObject;
	}

	@Transient
	public void setDeletedStatus(String statusDeleted) {
		setStatus(CommonConstants.STATUS_DELETED);
		for(QosProfileDetailData  qosProfileDetailData:qosProfileDetailDataList){
			qosProfileDetailData.setDeletedStatus();
		}
	}
	
	public QosProfileData deepClone() throws CloneNotSupportedException {
		QosProfileData newData = (QosProfileData) this.clone();
		
		newData.deviceProfiles = Collectionz.newArrayList();
		if (deviceProfiles.isEmpty() == false) {
			for (DeviceProfileData deviceProfileData : deviceProfiles) {
				DeviceProfileData clonedDeviceProfile = deviceProfileData.deepClone();
				clonedDeviceProfile.setQosProfile(newData);
				newData.deviceProfiles.add(clonedDeviceProfile);
			}
		}
		
		newData.qosProfileDetailDataList = Collectionz.newArrayList();
		if (qosProfileDetailDataList.isEmpty() == false) {
			for (QosProfileDetailData qosProfileDetail : qosProfileDetailDataList) {
				QosProfileDetailData clonedQosProfileDetail = qosProfileDetail.deepClone();
				clonedQosProfileDetail.setQosProfile(newData);
				newData.qosProfileDetailDataList.add(clonedQosProfileDetail);
			}
		}
		
		newData.timePeriodDataList = Collectionz.newArrayList();
		if (timePeriodDataList.isEmpty() == false) {
			for (TimePeriodData timePeriodData : timePeriodDataList) {
				TimePeriodData clonedTimePeriodData = timePeriodData.deepClone();
				clonedTimePeriodData.setQosProfile(newData);
				newData.timePeriodDataList.add(clonedTimePeriodData);
			}
		}
		
		Set<QosProfileData> qosProfileSet = Collectionz.newHashSet();
		qosProfileSet.add(newData);
		if (quotaProfile != null) {
			QuotaProfileData clonedQuotaProfil = quotaProfile.deepClone();
			clonedQuotaProfil.setQosProfiles(qosProfileSet);
			newData.quotaProfile = clonedQuotaProfil;
		}
		
		if (syQuotaProfileData != null) {
			SyQuotaProfileData clonedSyQuotaProfileData = syQuotaProfileData.deepClone();
			clonedSyQuotaProfileData.setQosProfileDatas(new ArrayList<QosProfileData>(qosProfileSet));
			newData.syQuotaProfileData = clonedSyQuotaProfileData;
		}
		
		newData.quotaProfileId = quotaProfileId;
		newData.pkgData = pkgData;
		return newData;
	}

	@Override
	@Transient
	public String getGroups() {
		return pkgData.getGroups();
	}



	@Transient
	@Override
	public ResourceData getAuditableResource() {
		return pkgData;
	}


	@Transient
	@Override
	public String getAuditableId() {
		return pkgData.getId();
	}

	@Transient
	@Override
	public String getHierarchy() {
		return pkgData.getHierarchy() + "<br>"+ getId() +"<br>"+ name;
	}



	@Override
	@Transient
	public String getResourceName() {
		return getName();
	}

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="RNC_PROFILE_ID")
    @XmlTransient
    public RncProfileData getRncProfileData() {
        return rncProfileData;
    }

    public void setRncProfileData(RncProfileData rncProfileData) {
        this.rncProfileData = rncProfileData;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="DATA_RATE_CARD_ID")
    @XmlTransient
    public DataRateCardData getRateCardData() {
        return rateCardData;
    }

    public void setRateCardData(DataRateCardData rateCardData) {
        this.rateCardData = rateCardData;
    }

    @XmlElement(name = "rate-card-id")
	@Transient
    public String getRateCardId() {
        return rateCardId;
    }

    public void setRateCardId(String rateCardId) {
        this.rateCardId = rateCardId;
    }

    @XmlElement(name = "rate-card-name")
	@Transient
    public String getRateCardName() {
        return rateCardName;
    }

    public void setRateCardName(String rateCardName) {
        this.rateCardName = rateCardName;
    }

	public QosProfileData copyModel(PkgData pkgData) {
		QosProfileData newData = new QosProfileData();

		newData.accessNetwork = this.accessNetwork;
		newData.advancedCondition = this.advancedCondition;
		newData.description = this.description;
		newData.duration = this.duration;
		newData.name = this.name;
		newData.orderNo = this.orderNo;
		newData.pkgData = pkgData;

		if(this.rncProfileData != null || this.quotaProfile != null || this.syQuotaProfileData != null) {
			if(pkgData.getQuotaProfileType() == QuotaProfileType.RnC_BASED){
				copyRncBasedQuotaProfile(pkgData, newData);
			} else if(pkgData.getQuotaProfileType() == QuotaProfileType.USAGE_METERING_BASED){
				copyUsageMeteringBasedQuotaProfile(pkgData, newData);
			} else if(pkgData.getQuotaProfileType() == QuotaProfileType.SY_COUNTER_BASED){
				copySyBasedQuotaProfile(pkgData, newData);
			}
		}

		copyRateCardData(pkgData, newData);

		copyDeviceProfiles(pkgData, newData);

		if(this.qosProfileDetailDataList != null) {
			newData.setQosProfileDetailDataList(getQosProfileDetailData(newData));
		}


		newData.setTimePeriodDataList(getTimePeriodDatas(newData));
		newData.setCreatedDateAndStaff(pkgData.getCreatedByStaff());

		return newData;
	}

	private void copyDeviceProfiles(PkgData pkgData, QosProfileData newData) {
		if(this.deviceProfiles != null) {
			List<DeviceProfileData> deviceProfileList = Collectionz.newArrayList();
			for (DeviceProfileData deviceProfileData : this.deviceProfiles) {
				DeviceProfileData deviceProfileDataCopy = deviceProfileData.copyModel();
				deviceProfileDataCopy.setId(null);
				deviceProfileDataCopy.setCreatedDateAndStaff(pkgData.getCreatedByStaff());
				deviceProfileDataCopy.setQosProfile(newData);
				deviceProfileList.add(deviceProfileDataCopy);
			}
			newData.setDeviceProfiles(deviceProfileList);
		}
	}

	private void copyRateCardData(PkgData pkgData, QosProfileData newData) {
		if(this.rateCardData != null){
			for(DataRateCardData rateCard : pkgData.getRateCards()){
				if(rateCard.getName().equalsIgnoreCase(this.rateCardData.getName())){
					newData.rateCardData = rateCard;
					newData.rateCardId = rateCard.getId();
					newData.rateCardName = this.rateCardName;
					if(newData.rateCardData.getQoSProfiles() == null){
						newData.rateCardData.setQoSProfiles(new ArrayList<>());
					}
					newData.rateCardData.getQoSProfiles().add(newData);
				}
			}
		}
	}

	private List<TimePeriodData> getTimePeriodDatas(QosProfileData newData) {
		List<TimePeriodData> timePeriods = Collectionz.newArrayList();
		for(TimePeriodData timePeriodData : this.timePeriodDataList){
			TimePeriodData timePeriodDataCopy = timePeriodData.copyModel();
			timePeriodDataCopy.setId(null);
			timePeriodDataCopy.setQosProfile(newData);
			timePeriods.add(timePeriodDataCopy);
		}
		return timePeriods;
	}

	private List<QosProfileDetailData> getQosProfileDetailData(QosProfileData newData) {
		List<QosProfileDetailData> qosProfileDetails = Collectionz.newArrayList();
		for (QosProfileDetailData qosProfileDetailData : this.qosProfileDetailDataList) {
            QosProfileDetailData qosProfileDetailDataCopy = qosProfileDetailData.copyModel(newData);
            qosProfileDetailDataCopy.setId(null);
            qosProfileDetailDataCopy.setQosProfile(newData);
            qosProfileDetails.add(qosProfileDetailDataCopy);
        }
		return qosProfileDetails;
	}

	private void copySyBasedQuotaProfile(PkgData pkgData, QosProfileData newData) {
		for(SyQuotaProfileData syQuotaProfile : pkgData.getSyQuotaProfileDatas()){
            if(syQuotaProfile.getName().equalsIgnoreCase(this.syQuotaProfileData.getName())){
                newData.syQuotaProfileData = syQuotaProfile;
                if(newData.syQuotaProfileData.getQosProfileDatas() == null){
                    newData.syQuotaProfileData.setQosProfileDatas(new ArrayList<>());
                }
                newData.syQuotaProfileData.getQosProfileDatas().add(newData);
            }
        }
	}

	private void copyUsageMeteringBasedQuotaProfile(PkgData pkgData, QosProfileData newData) {
		for(QuotaProfileData quotaProfileData : pkgData.getQuotaProfiles()){
            if(quotaProfileData.getName().equalsIgnoreCase(this.quotaProfile.getName())){
                newData.quotaProfileId = quotaProfileData.getId();
                newData.quotaProfileName = quotaProfileData.getName();
                newData.quotaProfile = quotaProfileData;
                if(newData.quotaProfile.getQosProfiles() == null){
                    newData.quotaProfile.setQosProfiles(new HashSet<>());
                }
                newData.quotaProfile.getQosProfiles().add(newData);
            }
        }
	}

	private void copyRncBasedQuotaProfile(PkgData pkgData, QosProfileData newData) {
		for (RncProfileData rncProfile : pkgData.getRncProfileDatas()) {
            if (rncProfile.getName().equalsIgnoreCase(this.rncProfileData.getName())) {
                newData.rncProfileData = rncProfile;
                if(newData.rncProfileData.getQosProfiles() == null){
                    newData.rncProfileData.setQosProfiles(new ArrayList<>());
                }
                newData.rncProfileData.getQosProfiles().add(newData);
            }
        }
	}
}
