package com.elitecore.corenetvertex.pkg.qos;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.QCI;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.core.validator.ChargingRuleBaseNameValidator;
import com.elitecore.corenetvertex.core.validator.GlobalPCCRuleValidator;
import com.elitecore.corenetvertex.core.validator.PCCRuleValidator;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleData;
import com.elitecore.corenetvertex.pkg.pccrule.GlobalPCCRuleData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.LongToStringAdapter;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.StringToIntegerAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.SystemUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "TBLM_QOS_PROFILE_DETAIL")
@XmlAccessorType(XmlAccessType.NONE)
/**
 * provide respect annotation for adding / discrding property for XML where XMLAccessType is set to NONE
 */
public class QosProfileDetailData implements Serializable, Cloneable {

	private static final long serialVersionUID = 4678703459755826842L;
	private static final ToStringStyle QOS_PROFILE_DETAIL_DATA_TO_STRING_STYLE = new QoSProfileDetailDataToString();
	transient private String id;
	private int qci = QCI.QCI_NON_GBR_6.getQci();
	private Integer fupLevel;
	private Long aambrdl;
	private String aambrdlUnit;
	private Long aambrul;
	private String aambrulUnit;
	private Long mbrdl;
	private String mbrdlUnit;
	private Long mbrul;
	private String mbrulUnit;
	private Boolean preCapability;
	private Boolean preVulnerability;
	private byte priorityLevel;
	private Integer action;
	private String rejectCause;
	@SerializedName(FieldValueConstants.USAGE_MONITORING)private Boolean usageMonitoring;
	@SerializedName(FieldValueConstants.SLICE_TOTAL)private Long sliceTotal;
	@SerializedName(FieldValueConstants.SLICE_TOTAL_UNIT)private String sliceTotalUnit;
	@SerializedName(FieldValueConstants.SLICE_UPLOAD)private Long sliceUpload;
	@SerializedName(FieldValueConstants.SLICE_UPLOAD_UNIT)private String sliceUploadUnit;
	@SerializedName(FieldValueConstants.SLICE_DOWNLOAD)private Long sliceDownload;
	@SerializedName(FieldValueConstants.SLICE_DOWNLOAD_UNIT)private String sliceDownloadUnit;
	@SerializedName(FieldValueConstants.SLICE_TIME)private Long sliceTime;
	@SerializedName(FieldValueConstants.SLICE_TIME_UNIT)private String sliceTimeUnit;
	@SerializedName(FieldValueConstants.REDIRECT_URL)private String redirectUrl;

	transient private QosProfileData qosProfile;
	private List<PCCRuleData> pccRules;
	private List<GlobalPCCRuleData> globalPCCRules; //NOSONAR
	private List<ChargingRuleBaseNameData> chargingRuleBaseNames; //NOSONAR
	private List<ChargingRuleData> chargingRuleDatas; //NOSONAR

	public QosProfileDetailData(){
		this.pccRules = Collectionz.newArrayList();
		this.globalPCCRules = Collectionz.newArrayList();
		this.chargingRuleBaseNames = Collectionz.newArrayList();
		this.chargingRuleDatas = Collectionz.newArrayList();
	}

	@Id
	@Column(name = "ID")
	@GeneratedValue(generator = "eliteSequenceGenerator")
	@XmlElement(name="id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "QCI")
	@XmlElement(name="qci")
	public int getQci() {
		return qci;
	}

	public void setQci(int qci) {
		this.qci = qci;
	}

	@Column(name = "FUP_LEVEL")
	@XmlElement(name="fup-level")
	@XmlJavaTypeAdapter(StringToIntegerAdapter.class)
	public Integer getFupLevel() {
		return fupLevel;
	}

	public void setFupLevel(Integer fupLevel) {
		this.fupLevel = fupLevel;
	}

	@Column(name = "AAMBRDL")
	@XmlElement(name="aambrdl")
	@XmlJavaTypeAdapter(LongToStringAdapter.class)
	public Long getAambrdl() {
		return aambrdl;
	}

	public void setAambrdl(Long aambrdl) {
		this.aambrdl = aambrdl;
	}

	@Column(name = "AAMBRUL")
	@XmlElement(name="aambrul")
	@XmlJavaTypeAdapter(LongToStringAdapter.class)
	public Long getAambrul() {
		return aambrul;
	}

	public void setAambrul(Long aambrul) {
		this.aambrul = aambrul;
	}

	@Column(name = "MBRDL")
	@XmlElement(name="mbrdl")
	@XmlJavaTypeAdapter(LongToStringAdapter.class)
	public Long getMbrdl() {
		return mbrdl;
	}

	public void setMbrdl(Long gbrdl) {
		this.mbrdl = gbrdl;
	}

	@Column(name = "MBRUL")
	@XmlElement(name="mbrul")
	@XmlJavaTypeAdapter(LongToStringAdapter.class)
	public Long getMbrul() {
		return mbrul;
	}

	public void setMbrul(Long gbrul) {
		this.mbrul = gbrul;
	}

	@Column(name = "PRE_CAPABILITY")
	@XmlElement(name="preCapability")
	public Boolean getPreCapability() {
		return preCapability;
	}

	public void setPreCapability(Boolean preCapability) {
		this.preCapability = preCapability;
	}

	@Column(name = "PRE_VULNERABILITY")
	@XmlElement(name="preVulnerability")
	public Boolean getPreVulnerability() {
		return preVulnerability;
	}

	public void setPreVulnerability(Boolean preVulnerability) {
		this.preVulnerability = preVulnerability;
	}

	@Column(name = "PRIORITY_LEVEL")
	@XmlElement(name="priority-level")
	public byte getPriorityLevel() {
		return priorityLevel;
	}

	public void setPriorityLevel(byte priorityLevel) {
		this.priorityLevel = priorityLevel;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "QOS_PROFILE_ID")
	@XmlTransient
	public QosProfileData getQosProfile() {
		return qosProfile;
	}

	public void setQosProfile(QosProfileData qosProfile) {
		this.qosProfile = qosProfile;
	}


	@ManyToMany(fetch = FetchType.LAZY)
	@Fetch(FetchMode.SUBSELECT)
	@Where(clause="STATUS != 'DELETED'")
	@JoinTable(name = "TBLM_QOS_PROFILE_PCC_RELATION", joinColumns = { @JoinColumn(name = "QOS_PROFILE_DETAIL_ID", nullable = false) },
			inverseJoinColumns = { @JoinColumn(name = "PCC_RULE_ID", nullable = false) })
	@XmlElementWrapper(name="pcc-rules")
	@XmlElement(name="pcc-rule")
	@Import(required = true, validatorClass = PCCRuleValidator.class)
	public List<PCCRuleData> getPccRules() {
		return pccRules;
	}

	public void setPccRules(List<PCCRuleData> pccRules) {
		this.pccRules = pccRules;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@Fetch(FetchMode.SUBSELECT)
	@Where(clause="STATUS != 'DELETED'")
	@JoinTable(	name = "TBLM_QOS_PROFILE_CRBN_REL",
				joinColumns = { @JoinColumn(name = "QOS_PROFILE_DETAIL_ID", nullable = false) },
				inverseJoinColumns = { @JoinColumn(name = "CHARGING_RULE_BASE_NAME_ID", nullable = false)
				})
	/*@XmlElementWrapper(name="charging-rule-base-names")
	@XmlElement(name="charging-rule-base-name")
	@Import(required = true, validatorClass = ChargingRuleBaseNameValidator.class)*/
	public List<ChargingRuleBaseNameData> getChargingRuleBaseNames(){
		return chargingRuleBaseNames;
	}

	public void setChargingRuleBaseNames( List<ChargingRuleBaseNameData> chargingRuleBaseNames){
		this.chargingRuleBaseNames = chargingRuleBaseNames;
	}

	@Column(name = "ACTION")
	@XmlElement(name="action")
	@XmlJavaTypeAdapter(StringToIntegerAdapter.class)
	public Integer getAction() {
		return action;
	}

	public void setAction(Integer action) {
		this.action = action;
	}

	@Column(name = "REJECT_CAUSE")
	@XmlElement(name="rejectCause")
	public String getRejectCause() {
		return rejectCause;
	}

	public void setRejectCause(String rejectCause) {
		this.rejectCause = rejectCause;
	}

	@Column(name = "AAMBRDL_UNIT")
	@XmlElement(name="aambrdl-unit")
	public String getAambrdlUnit() {
		return aambrdlUnit;
	}

	public void setAambrdlUnit(String aambrdlUnit) {
		this.aambrdlUnit = aambrdlUnit;
	}

	@Column(name = "AAMBRUL_UNIT")
	@XmlElement(name="aambrul-unit")
	public String getAambrulUnit() {
		return aambrulUnit;
	}

	public void setAambrulUnit(String aambrulUnit) {
		this.aambrulUnit = aambrulUnit;
	}

	@Column(name = "MBRDL_UNIT")
	@XmlElement(name="mbrdl-unit")
	public String getMbrdlUnit() {
		return mbrdlUnit;
	}

	public void setMbrdlUnit(String mbrdlUnit) {
		this.mbrdlUnit = mbrdlUnit;
	}

	@Column(name = "MBRUL_UNIT")
	@XmlElement(name="mbrul-unit")
	public String getMbrulUnit() {
		return mbrulUnit;
	}

	public void setMbrulUnit(String mbrulUnit) {
		this.mbrulUnit = mbrulUnit;
	}

	@Transient
	public ResourceData getAuditableResource() {
		return qosProfile.getAuditableResource();
	}
	
	@Column(name = "USAGE_MONITORING")
	@XmlElement(name="usageMonitoring")
	public Boolean getUsageMonitoring() {
		return usageMonitoring;
	}

	public void setUsageMonitoring(Boolean usageMonitoring) {
		this.usageMonitoring = usageMonitoring;
	}

	@Column(name="SLICE_TOTAL")
	@XmlElement(name="slice-total")
	@XmlJavaTypeAdapter(LongToStringAdapter.class)
	public Long getSliceTotal() {
		return sliceTotal;
	}

	public void setSliceTotal(Long sliceTotal) {
		this.sliceTotal = sliceTotal;
	}

	@Column(name="SLICE_TOTAL_UNIT")
	@XmlElement(name="slice-total-unit")
	public String getSliceTotalUnit() {
		return sliceTotalUnit;
	}

	public void setSliceTotalUnit(String sliceTotalUnit) {
		this.sliceTotalUnit = sliceTotalUnit;
	}

	@Column(name="SLICE_UPLOAD")
	@XmlElement(name="slice-upload")
	@XmlJavaTypeAdapter(LongToStringAdapter.class)
	public Long getSliceUpload() {
		return sliceUpload;
	}

	public void setSliceUpload(Long sliceUpload) {
		this.sliceUpload = sliceUpload;
	}

	@Column(name="SLICE_UPLOAD_UNIT")
	@XmlElement(name="slice-upload-unit")
	public String getSliceUploadUnit() {
		return sliceUploadUnit;
	}

	public void setSliceUploadUnit(String sliceUploadUnit) {
		this.sliceUploadUnit = sliceUploadUnit;
	}

	@Column(name="SLICE_DOWNLOAD")
	@XmlElement(name="slice-download")
	@XmlJavaTypeAdapter(LongToStringAdapter.class)
	public Long getSliceDownload() {
		return sliceDownload;
	}

	public void setSliceDownload(Long sliceDownload) {
		this.sliceDownload = sliceDownload;
	}

	@Column(name="SLICE_DOWNLOAD_UNIT")
	@XmlElement(name="slice-download-unit")
	public String getSliceDownloadUnit() {
		return sliceDownloadUnit;
	}

	public void setSliceDownloadUnit(String sliceDownloadUnit) {
		this.sliceDownloadUnit = sliceDownloadUnit;
	}

	@Column(name="SLICE_TIME")
	@XmlElement(name="slice-time")
	@XmlJavaTypeAdapter(LongToStringAdapter.class)
	public Long getSliceTime() {
		return sliceTime;
	}

	public void setSliceTime(Long sliceTime) {
		this.sliceTime = sliceTime;
	}

	@Column(name="SLICE_TIME_UNIT")
	@XmlElement(name="slice-time-unit")
	public String getSliceTimeUnit() {
		return sliceTimeUnit;
	}

	public void setSliceTimeUnit(String sliceTimeUnit) {
		this.sliceTimeUnit = sliceTimeUnit;
	}
	
	@Override
	public String toString() {

		return toString(QOS_PROFILE_DETAIL_DATA_TO_STRING_STYLE);
	}

	public String toString(ToStringStyle toStringStyle) {
		
		ToStringBuilder toStringBuilder = new ToStringBuilder(this,
				toStringStyle).append("Level", (fupLevel ==0 ? "HSQ" : "fup level " + fupLevel))
				.append("QCI", qci)
				.append("AAMBRDL", aambrdl + " " + aambrdlUnit)
				.append("AAMBRUL", aambrul+ " " + aambrulUnit)
				.append("MBRDL", mbrdl+ " " + mbrdlUnit)
				.append("MBRUL", mbrul+ " " + mbrulUnit)
				.append("Priority Level", priorityLevel)
				.append("Pre-emption capability", preCapability)
				.append("Pre-emption vulnerability", preVulnerability)
				.append("Action", action == QoSProfileAction.ACCEPT.getId() ? QoSProfileAction.ACCEPT.getName() : QoSProfileAction.REJECT.getName())
				.append("Reject Cause", rejectCause);
		if(action == QoSProfileAction.ACCEPT.getId()) {
			toStringBuilder.append("");
			if (Collectionz.isNullOrEmpty(pccRules) == false) {
				for (PCCRuleData pccRuleData : pccRules) {
					toStringBuilder.append("PCC Rule", pccRuleData);
				}
			}
		}
		

		return toStringBuilder.toString();
	}

	public QosProfileDetailData copyModel(QosProfileData qosProfileData) {
		QosProfileDetailData newData = new QosProfileDetailData();

		newData.fupLevel = this.fupLevel;
		newData.aambrdl = this.aambrdl;
		newData.aambrdlUnit = this.aambrdlUnit;
		newData.aambrul = this.aambrul;
		newData.aambrulUnit = this.aambrulUnit;
		newData.mbrdl = this.mbrdl;
		newData.mbrdlUnit = this.mbrdlUnit;
		newData.mbrul = this.mbrul;
		newData.mbrulUnit = this.mbrulUnit;
		newData.preCapability = this.preCapability;
		newData.preVulnerability = this.preVulnerability;
		newData.priorityLevel = this.priorityLevel;
		newData.action = this.action;
		newData.rejectCause = this.rejectCause;
		newData.usageMonitoring = this.usageMonitoring;
		newData.sliceTotal = this.sliceTotal;
		newData.sliceTotalUnit = this.sliceTotalUnit;
		newData.sliceUpload = this.sliceUpload;
		newData.sliceUploadUnit = this.sliceUploadUnit;
		newData.sliceDownload = this.sliceDownload;
		newData.sliceDownloadUnit = this.sliceDownloadUnit;
		newData.sliceTime = this.sliceTime;
		newData.sliceTimeUnit = this.sliceTimeUnit;
		newData.redirectUrl = this.redirectUrl;

		for (PCCRuleData pccRule : this.pccRules) {
			if(PCCRuleScope.LOCAL == pccRule.getScope()){
				PCCRuleData pccRuleCopy = pccRule.copyModel();
				pccRuleCopy.setGroups(qosProfileData.getGroups());
				pccRuleCopy.setId(null);
				pccRuleCopy.setCreatedDateAndStaff(qosProfileData.getPkgData().getCreatedByStaff());
				newData.getPccRules().add(pccRuleCopy);
			}else{
				pccRule.getQosProfileDetails().add(newData);
			}
		}

		for (ChargingRuleBaseNameData chargingRuleBaseNameData : this.chargingRuleBaseNames) {
			chargingRuleBaseNameData.getQosProfileDetails().add(newData);
		}

		return  newData;
	}

	private static final class QoSProfileDetailDataToString extends
			ToStringStyle.CustomToStringStyle {

		private static final long serialVersionUID = 1L;

		QoSProfileDetailDataToString() {
			super();
			this.setContentStart(SystemUtils.LINE_SEPARATOR);
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(6)
					+ getTabs(2));
		}
	}
	
	@Transient
	public String getHierarchy() {
		return qosProfile.getHierarchy() + "<br>" + id + "<br>"+(fupLevel > 0 ? ("FUP" + fupLevel) : "HSQ");
	}
	
	public JsonObject toJson(){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("AAMBRDL", aambrdl);
		jsonObject.addProperty("AAMBRDL Unit", aambrdlUnit);
		jsonObject.addProperty("AAMBRUL", aambrul);
		jsonObject.addProperty("AAMBRUL Unit", aambrulUnit);
		jsonObject.addProperty("MBRDL", mbrdl);
		jsonObject.addProperty("MBRDL Unit", mbrdlUnit);
		jsonObject.addProperty("MBRUL", mbrul);
		jsonObject.addProperty("MBRUL Unit", mbrulUnit);
		jsonObject.addProperty("Pre-Emption Capability", CommonStatusValues.fromBooleanValue(preCapability).getStringName());
		jsonObject.addProperty("Pre-Emption Vulnerablity", CommonStatusValues.fromBooleanValue(preVulnerability).getStringName());
		jsonObject.addProperty("Priority Level", priorityLevel);
		jsonObject.addProperty("Action", QoSProfileAction.fromValue(action).getName());
		jsonObject.addProperty("RejectCause", rejectCause);
		jsonObject.addProperty("Redirect URL", redirectUrl);
		jsonObject.addProperty("QCI", qci);
		if(pccRules != null){
			JsonArray jsonArray = new JsonArray();
			for(PCCRuleData pccRuleData : pccRules){
				jsonArray.add(pccRuleData.toJson());
			}
			jsonObject.add("PCC Rules", jsonArray);
		}

		if(chargingRuleBaseNames != null){
			JsonArray jsonArray = new JsonArray();
			for(ChargingRuleBaseNameData chargingRuleBaseNameData : chargingRuleBaseNames){
				jsonArray.add(chargingRuleBaseNameData.toJson());
			}
			jsonObject.add("ChargingRuleBaseNames", jsonArray);
		}

		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonObject);
		jsonObject = new JsonObject();
		jsonObject.add((fupLevel > 0 ? ("FUP" + fupLevel) : "HSQ"), jsonArray);
		return jsonObject;
	}

	@Transient
	public void setDeletedStatus() {
		for(PCCRuleData pccRuleData:pccRules){
			if(PCCRuleScope.LOCAL == pccRuleData.getScope()) {
				pccRuleData.setStatus(CommonConstants.STATUS_DELETED);
			}
		}
	}
	
	public QosProfileDetailData deepClone() throws CloneNotSupportedException {
		QosProfileDetailData newData =  (QosProfileDetailData) this.clone();
		newData.pccRules = Collectionz.newArrayList();
		if (pccRules.isEmpty() == false)  {
			for (PCCRuleData pccRuleData : pccRules) {
				PCCRuleData clonedPccRuleData = pccRuleData.deepClone();
				clonedPccRuleData.getQosProfileDetails().add(newData);
				newData.pccRules.add(clonedPccRuleData);
			}
		}

		newData.chargingRuleBaseNames = Collectionz.newArrayList();
		if (chargingRuleBaseNames.isEmpty() == false)  {
			for (ChargingRuleBaseNameData chargingRuleBaseNameData : chargingRuleBaseNames) {
				ChargingRuleBaseNameData clonedChargingRuleData = chargingRuleBaseNameData.deepClone();
				clonedChargingRuleData.getQosProfileDetails().add(newData);
				newData.chargingRuleBaseNames.add(clonedChargingRuleData);
			}
		}

		newData.qosProfile = qosProfile;
		return newData;
	}

	public void setGlobalPCCRules(List<GlobalPCCRuleData> globalPCCRules) {
		this.globalPCCRules = globalPCCRules;
	}

	@XmlElementWrapper(name="global-pcc-rules")
	@XmlElement(name="global-pcc-rule")
	@Import(required = true, validatorClass = GlobalPCCRuleValidator.class)
	@Transient
	public List<GlobalPCCRuleData> getGlobalPCCRules() {
		return globalPCCRules;
	}

	@XmlElementWrapper(name="charging-rule-base-names")
	@XmlElement(name="charging-rule-base-name")
	@Import(required = true, validatorClass = ChargingRuleBaseNameValidator.class)
	@Transient
	public List<ChargingRuleData> getChargingRuleDatas() {
		return chargingRuleDatas;
	}

	public void setChargingRuleDatas(List<ChargingRuleData> chargingRuleDatas) {
		this.chargingRuleDatas = chargingRuleDatas;
	}

	@Column(name = "REDIRECT_URL")
	@XmlElement(name="redirectUrl")
	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
}