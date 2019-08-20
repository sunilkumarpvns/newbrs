package com.elitecore.corenetvertex.pkg.pccrule;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.core.constant.ChargingModes;
import com.elitecore.corenetvertex.core.validator.DataServiceTypeValidator;
import com.elitecore.corenetvertex.core.validator.GlobalPCCRuleImportValidator;
import com.elitecore.corenetvertex.core.validator.ServiceDataFlowValidator;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.dataservicetype.ServiceDataFlowData;
import com.elitecore.corenetvertex.pkg.importpkg.PCCRuleImportOperation;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.LongToStringAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.SystemUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "TBLM_PCC_RULE")
@XmlAccessorType(XmlAccessType.FIELD)
@Import(required = true, validatorClass = GlobalPCCRuleImportValidator.class, importClass = PCCRuleImportOperation.class)
@XmlRootElement
public class PCCRuleData extends ResourceData implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	private static final ToStringStyle PCC_RULE_DATA_TO_STRING_STYLE = new PCCRuleDataToString();
	@XmlElement(name = "dataServiceType") private DataServiceTypeData dataServiceTypeData;
	private transient String serviceTypeId = CommonConstants.ALL_SERVICE_ID;
	private transient List<QosProfileDetailData> qosProfileDetails;
	@SerializedName(FieldValueConstants.NAME)private String name;
	@SerializedName(FieldValueConstants.PRECEDENCE)private Short precedence;
	@SerializedName(FieldValueConstants.CHARGING_MODE)private Byte chargingMode;
	@SerializedName(FieldValueConstants.MONITORING_KEY)private String monitoringKey;
	@SerializedName(FieldValueConstants.SPONSOR_ID)private String sponsorIdentity;
	@SerializedName(FieldValueConstants.APP_SERVICE_PROVIDER_ID)private String appServiceProviderId;
	@SerializedName(FieldValueConstants.FLOW_STATUS)private Byte flowStatus;
	private String serviceIdentifier;
	@SerializedName(FieldValueConstants.USAGE_MONITORING)private Boolean usageMonitoring;
	@SerializedName(FieldValueConstants.QCI)private Byte qci;
	private Byte arp;
	@SerializedName(FieldValueConstants.PRE_EMPTION_CAPABILITY)private Boolean preCapability;
	@SerializedName(FieldValueConstants.PRE_EMPTION_VULNERABILITY)private Boolean preVulnerability;
	@SerializedName(FieldValueConstants.GBRDL) @XmlJavaTypeAdapter(LongToStringAdapter.class) private Long gbrdl;
	@SerializedName(FieldValueConstants.GBRDL_UNIT) private String gbrdlUnit;
	@SerializedName(FieldValueConstants.GBRUL) @XmlJavaTypeAdapter(LongToStringAdapter.class) private Long gbrul;
	@SerializedName(FieldValueConstants.GBRUL_UNIT)private String gbrulUnit;
	@SerializedName(FieldValueConstants.MBRDL) @XmlJavaTypeAdapter(LongToStringAdapter.class) private Long mbrdl;
	@SerializedName(FieldValueConstants.MBRDL_UNIT)private String mbrdlUnit;
	@SerializedName(FieldValueConstants.MBRUL) @XmlJavaTypeAdapter(LongToStringAdapter.class) private Long mbrul;
	@SerializedName(FieldValueConstants.MBRUL_UNIT)private String mbrulUnit;
	@SerializedName(FieldValueConstants.TYPE)private String type;
	@SerializedName(FieldValueConstants.SLICE_TOTAL) @XmlJavaTypeAdapter(LongToStringAdapter.class) private Long sliceTotal;
	@SerializedName(FieldValueConstants.SLICE_TOTAL_UNIT)private String sliceTotalUnit;
	@SerializedName(FieldValueConstants.SLICE_UPLOAD) @XmlJavaTypeAdapter(LongToStringAdapter.class) private Long sliceUpload;
	@SerializedName(FieldValueConstants.SLICE_UPLOAD_UNIT)private String sliceUploadUnit;
	@SerializedName(FieldValueConstants.SLICE_DOWNLOAD) @XmlJavaTypeAdapter(LongToStringAdapter.class)  private Long sliceDownload;
	@SerializedName(FieldValueConstants.SLICE_DOWNLOAD_UNIT)private String sliceDownloadUnit;
	@SerializedName(FieldValueConstants.SLICE_TIME) @XmlJavaTypeAdapter(LongToStringAdapter.class) private Long sliceTime;
	@SerializedName(FieldValueConstants.SLICE_TIME_UNIT)private String sliceTimeUnit;
	@XmlTransient private List<ServiceDataFlowData> serviceDataFlowList;
	@XmlTransient private PCCRuleScope scope;
	@SerializedName(FieldValueConstants.CHARGING_KEY_NAME)private transient String chargingKeyName;
	@SerializedName(FieldValueConstants.CHARGING_KEY)private String chargingKey;

	public PCCRuleData() {
		serviceDataFlowList = Collectionz.newArrayList();
		qosProfileDetails  = Collectionz.newArrayList();
	}

	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}

	@Override
	public void setStatus(String status) {
		super.setStatus(status);
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "DATA_SERVICE_TYPE_ID")
	@Import(required = true, validatorClass = DataServiceTypeValidator.class)
	public DataServiceTypeData getDataServiceTypeData() {
		return dataServiceTypeData;
	}

	public void setDataServiceTypeData(DataServiceTypeData dataServiceTypeData) {
		this.dataServiceTypeData = dataServiceTypeData;
	}

	@Column(name = "USAGE_MONITORING")
	public Boolean getUsageMonitoring() {
		return usageMonitoring;
	}

	public void setUsageMonitoring(Boolean usageMonitoring) {
		this.usageMonitoring = usageMonitoring;
	}

	@ManyToMany
	@JoinTable(name = "TBLM_QOS_PROFILE_PCC_RELATION", joinColumns = { @JoinColumn(name = "PCC_RULE_ID", nullable = false) },
		inverseJoinColumns = { @JoinColumn(name = "QOS_PROFILE_DETAIL_ID", nullable = false) })
	@XmlTransient
	public List<QosProfileDetailData> getQosProfileDetails() {
		return qosProfileDetails;
	}

	public void setQosProfileDetails(List<QosProfileDetailData> qosProfileDetail) {
		this.qosProfileDetails = qosProfileDetail;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "PRECEDENCE")
	public Short getPrecedence() {
		return precedence;
	}

	public void setPrecedence(Short precedence) {
		this.precedence = precedence;
	}

	@Column(name = "CHARGING_MODE")
	public Byte getChargingMode() {
		return chargingMode;
	}

	public void setChargingMode(Byte chargingMode) {
		this.chargingMode = chargingMode;
	}

	@Column(name = "CHARGING_KEY")
	public String getChargingKey() {
		return chargingKey;
	}

	public void setChargingKey(String chargingKey) {
		this.chargingKey = chargingKey;
	}

	@Transient
	@XmlElement (name = "chargingKeyName")
	public String getChargingKeyName() {
		return chargingKeyName;
	}

	public void setChargingKeyName(String chargingKeyName) {
		this.chargingKeyName = chargingKeyName;
	}

	@Column(name = "MONITORING_KEY")
	public String getMonitoringKey() {
		return monitoringKey;
	}

	public void setMonitoringKey(String monitoringKey) {
		this.monitoringKey = monitoringKey.trim();
	}

	@Column(name = "SPONSOR_IDENTITY")
	public String getSponsorIdentity() {
		return sponsorIdentity;
	}

	public void setSponsorIdentity(String sponsorIdenity) {
		this.sponsorIdentity = sponsorIdenity;
	}

	@Column(name = "APP_SERVICE_PROVIDER_ID")
	public String getAppServiceProviderId() {
		return appServiceProviderId;
	}

	public void setAppServiceProviderId(String appServiceProviderId) {
		this.appServiceProviderId = appServiceProviderId;
	}

	@Column(name = "FLOW_STATUS")
	public Byte getFlowStatus() {
		return flowStatus;
	}

	public void setFlowStatus(Byte flowStatus) {
		this.flowStatus = flowStatus;
	}

	@Column(name = "SERVICE_IDENTIFIER")
	public String getServiceIdentifier() {
		return serviceIdentifier;
	}

	public void setServiceIdentifier(String serviceIdentifier) {
		this.serviceIdentifier = serviceIdentifier;
	}

	@Column(name = "ARP")
	public Byte getArp() {
		return arp;
	}

	public void setArp(Byte arp) {
		this.arp = arp;
	}

	@Column(name = "GBRDL")
	public Long getGbrdl() {
		return gbrdl;
	}

	public void setGbrdl(Long gbrdl) {
		this.gbrdl = gbrdl;
	}

	@Column(name = "GBRUL")
	public Long getGbrul() {
		return gbrul;
	}

	public void setGbrul(Long gbrul) {
		this.gbrul = gbrul;
	}

	@Column(name = "MBRDL")
	public Long getMbrdl() {
		return mbrdl;
	}

	public void setMbrdl(Long mbrdl) {
		this.mbrdl = mbrdl;
	}

	@Column(name = "MBRUL")
	public Long getMbrul() {
		return mbrul;
	}

	public void setMbrul(Long mbrul) {
		this.mbrul = mbrul;
	}

	@Column(name = "TYPE")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "GBRDL_UNIT")
	public String getGbrdlUnit() {
		return gbrdlUnit;
	}

	public void setGbrdlUnit(String gbrdlUnit) {
		this.gbrdlUnit = gbrdlUnit;
	}

	@Column(name = "GBRUL_UNIT")
	public String getGbrulUnit() {
		return gbrulUnit;
	}

	public void setGbrulUnit(String gbrulUnit) {
		this.gbrulUnit = gbrulUnit;
	}

	@Column(name = "MBRDL_UNIT")
	public String getMbrdlUnit() {
		return mbrdlUnit;
	}

	public void setMbrdlUnit(String mbrdlUnit) {
		this.mbrdlUnit = mbrdlUnit;
	}

	@Column(name = "MBRUL_Unit")
	public String getMbrulUnit() {
		return mbrulUnit;
	}

	public void setMbrulUnit(String mbrulUnit) {
		this.mbrulUnit = mbrulUnit;
	}

	@Column(name = "SLICE_Total")
	public Long getSliceTotal() {
		return sliceTotal;
	}

	public void setSliceTotal(Long sliceTotal) {
		this.sliceTotal = sliceTotal;
	}

	@Column(name = "SLICE_Total_UNIT")
	public String getSliceTotalUnit() {
		return sliceTotalUnit;
	}

	public void setSliceTotalUnit(String sliceTotalUnit) {
		this.sliceTotalUnit = sliceTotalUnit;
	}

	@Column(name = "SLICE_UPLOAD")
	public Long getSliceUpload() {
		return sliceUpload;
	}

	public void setSliceUpload(Long sliceUpload) {
		this.sliceUpload = sliceUpload;
	}

	@Column(name = "SLICE_UPLOAD_UNIT")
	public String getSliceUploadUnit() {
		return sliceUploadUnit;
	}

	public void setSliceUploadUnit(String sliceUploadUnit) {
		this.sliceUploadUnit = sliceUploadUnit;
	}

	@Column(name = "SLICE_DOWNLOAD")
	public Long getSliceDownload() {
		return sliceDownload;
	}

	public void setSliceDownload(Long sliceDownload) {
		this.sliceDownload = sliceDownload;
	}

	@Column(name = "SLICE_DOWNLOAD_UNIT")
	public String getSliceDownloadUnit() {
		return sliceDownloadUnit;
	}

	public void setSliceDownloadUnit(String sliceDownloadUnit) {
		this.sliceDownloadUnit = sliceDownloadUnit;
	}

	@Column(name = "SLICE_TIME")
	public Long getSliceTime() {
		return sliceTime;
	}

	public void setSliceTime(Long sliceTime) {
		this.sliceTime = sliceTime;
	}

	@Column(name = "SLICE_TIME_UNIT")
	public String getSliceTimeUnit() {
		return sliceTimeUnit;
	}

	public void setSliceTimeUnit(String sliceTimeUnit) {
		this.sliceTimeUnit = sliceTimeUnit;
	}

	@Column(name = "QCI")
	public Byte getQci() {
		return qci;
	}

	public void setQci(Byte qci) {
		this.qci = qci;
	}

	@Column(name = "PRE_CAPABILITY")
	public Boolean getPreCapability() {
		return preCapability;
	}

	public void setPreCapability(Boolean preCapability) {
		this.preCapability = preCapability;
	}

	@Column(name = "PRE_VULNERABILITY")
	public Boolean getPreVulnerability() {
		return preVulnerability;
	}

	public void setPreVulnerability(Boolean preVulnerability) {
		this.preVulnerability = preVulnerability;
	}

	@Transient
	public String getServiceTypeId() {
		return serviceTypeId;
	}

	public void setServiceTypeId(String serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "pccRule", orphanRemoval = true)
	@Fetch(FetchMode.SUBSELECT)
	@XmlElementWrapper(name="service-data-flows")
	@XmlElement(name="service-data-flow")
	@Import(required = true, validatorClass = ServiceDataFlowValidator.class)
	public List<ServiceDataFlowData> getServiceDataFlowList() {
		return serviceDataFlowList;
	}

	public void setServiceDataFlowList(
			List<ServiceDataFlowData> serviceDataFlowList) {
		this.serviceDataFlowList = serviceDataFlowList;
	}

	public void removeRelation() {
		for(QosProfileDetailData qosProfileDetailData:qosProfileDetails){
			qosProfileDetailData.getPccRules().remove(this);
		}
	}

	@Override
	@Column(name="GROUPS")
	public String getGroups() {
		return super.getGroups();
	}

	@Override
	public void setGroups(String groups) {
		super.setGroups(groups);
	}

	@Override
	public String toString() {

		return toString(PCC_RULE_DATA_TO_STRING_STYLE);
	}

	public String toString(ToStringStyle toStringStyle) {

		return new ToStringBuilder(this, toStringStyle).append("Name", name)
				.append("QCI", qci).append("GBRDL", gbrdl + " " + gbrdlUnit)
				.append("GBRUL", gbrul + " " + gbrulUnit)
				.append("MBRDL", mbrdl + " " + mbrdlUnit)
				.append("MBRUL", mbrul + " " + mbrulUnit)
				.append("Type", type)
				.append("Monitoring Key", monitoringKey).append("")
				.append("Data Service Type", dataServiceTypeData).toString();
	}

	@Enumerated(EnumType.STRING)
	@Column(name="SCOPE")
	public PCCRuleScope getScope() {
		return scope;
	}

	public void setScope(PCCRuleScope scope) {
		this.scope = scope;
	}

    public PCCRuleData copyModel() {
        PCCRuleData newData = new PCCRuleData();

        newData.appServiceProviderId = this.appServiceProviderId;
        newData.arp = this.arp;
        newData.chargingKey = this.chargingKey;
        newData.chargingKeyName = this.chargingKeyName;
        newData.chargingMode = this.chargingMode;
        newData.flowStatus = this.flowStatus;
        newData.gbrdl = this.gbrdl;
        newData.gbrdlUnit = this.gbrdlUnit;
        newData.gbrul = this.gbrul;
        newData.gbrulUnit = this.gbrulUnit;
        newData.mbrdl = this.mbrdl;
        newData.mbrdlUnit = this.mbrdlUnit;
        newData.mbrul = this.mbrul;
        newData.mbrulUnit = this.mbrulUnit;
        newData.monitoringKey = this.monitoringKey;
        newData.name = this.name;
        newData.preCapability = this.preCapability;
        newData.precedence = this.precedence;
        newData.preVulnerability = this.preVulnerability;
        newData.qci = this.qci;
        newData.serviceTypeId = this.serviceTypeId;
        newData.sliceDownload = this.sliceDownload;
        newData.sliceDownloadUnit = this.sliceDownloadUnit;
        newData.sliceTime = this.sliceTime;
        newData.sliceTimeUnit = this.sliceTimeUnit;
        newData.sliceTotal = this.sliceTotal;
        newData.sliceTotalUnit = this.sliceTotalUnit;
        newData.sliceUpload = this.sliceUpload;
        newData.sliceUploadUnit = this.sliceUploadUnit;
        newData.sponsorIdentity = this.sponsorIdentity;
        newData.type = this.type;
        newData.usageMonitoring = this.usageMonitoring;
        newData.serviceIdentifier = this.serviceIdentifier;
        newData.dataServiceTypeData = this.dataServiceTypeData;
        newData.scope = this.scope;

        List<ServiceDataFlowData> serviceDataFlowList = Collectionz.newArrayList();
        for (ServiceDataFlowData serviceDataFlowData : this.serviceDataFlowList) {
            ServiceDataFlowData serviceDataFlowDataCopy = serviceDataFlowData.copyModel();
            serviceDataFlowDataCopy.setServiceDataFlowId(null);
            serviceDataFlowDataCopy.setPccRule(newData);
            serviceDataFlowList.add(serviceDataFlowDataCopy);
        }
        newData.setServiceDataFlowList(serviceDataFlowList);

        return newData;
    }

    private static final class PCCRuleDataToString extends
            ToStringStyle.CustomToStringStyle {

		private static final long serialVersionUID = 1L;

		PCCRuleDataToString() {
			super();
			this.setContentStart(SystemUtils.LINE_SEPARATOR);
			this.setContentEnd("");
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(8)
					+ getTabs(3));
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
		PCCRuleData other = (PCCRuleData) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (getId().equals(other.getId()) == false)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (name.equals(other.name) == false)
			return false;
		return true;
	}

	@Transient
	@Override
	public String getAuditableId() {
		if(PCCRuleScope.GLOBAL.equals(scope)==true){
			return	super.getAuditableId();
		}


		return qosProfileDetails.get(0).getQosProfile().getPkgData().getId();
	}

	@Transient
	@Override
	public ResourceData getAuditableResource() {
		if(PCCRuleScope.GLOBAL.equals(scope)==true){
		return super.getAuditableResource();
		}
		return qosProfileDetails.get(0).getAuditableResource();
	}

	@Transient
	@Override
	public String getHierarchy() {
		if(scope.equals(PCCRuleScope.GLOBAL)==true){
			return getId() + "<br>"+ name;
		}
		return qosProfileDetails.get(0).getHierarchy() + "<br>" +getId()+"<br>"+ name;
	}


	@Override
	public JsonObject toJson(){
		Gson gson =  new GsonBuilder().serializeNulls().create();
		JsonObject jsonObject = gson.toJsonTree(this).getAsJsonObject();
		jsonObject.addProperty("Data Service Type", dataServiceTypeData.getName());
		jsonObject.addProperty(FieldValueConstants.CHARGING_MODE, ChargingModes.fromValue(chargingMode).getDisplayName());
		jsonObject.addProperty(FieldValueConstants.PRE_EMPTION_CAPABILITY, CommonStatusValues.fromBooleanValue(preCapability).getStringName());
		jsonObject.addProperty(FieldValueConstants.PRE_EMPTION_VULNERABILITY, CommonStatusValues.fromBooleanValue(preVulnerability).getStringName());
		jsonObject.addProperty(FieldValueConstants.USAGE_MONITORING, CommonStatusValues.fromBooleanValue(usageMonitoring).getStringName());


		return jsonObject;
	}

	public PCCRuleData deepClone() throws CloneNotSupportedException {
		PCCRuleData newData = (PCCRuleData) this.clone();

		newData.serviceTypeId = serviceTypeId;
		newData.qosProfileDetails = qosProfileDetails;
		newData.dataServiceTypeData = dataServiceTypeData == null ? null : dataServiceTypeData.deepClone();
		newData.serviceDataFlowList = Collectionz.newArrayList();

		if(serviceDataFlowList.isEmpty() == false) {
			for (ServiceDataFlowData serviceDataFlow : serviceDataFlowList) {
				ServiceDataFlowData clonedServiceDataFlow = serviceDataFlow.deepClone();
				clonedServiceDataFlow.setPccRule(newData);
				newData.serviceDataFlowList.add(clonedServiceDataFlow);
			}
		}
		return newData;
	}

	@Transient
	public String getServiceName() {
		if(dataServiceTypeData !=null){
		 return dataServiceTypeData.getName();
		}
		return null;
	}

	public void setServiceName(String serviceName) {
        //It is just to comply for struts model a property must be having getter & setters
	}

	@Override
	@Transient
	public String getResourceName() {
		return getName();
	}

}
