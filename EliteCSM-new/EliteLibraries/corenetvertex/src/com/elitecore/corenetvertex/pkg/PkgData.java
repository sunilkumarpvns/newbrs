package com.elitecore.corenetvertex.pkg;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.validator.DataRateCardValidator;
import com.elitecore.corenetvertex.core.validator.PkgValidator;
import com.elitecore.corenetvertex.core.validator.QosProfileValidator;
import com.elitecore.corenetvertex.core.validator.QuotaNotificationValidator;
import com.elitecore.corenetvertex.core.validator.QuotaProfileValidator;
import com.elitecore.corenetvertex.core.validator.RnCProfileValidator;
import com.elitecore.corenetvertex.core.validator.SyQuotaProfileValidator;
import com.elitecore.corenetvertex.core.validator.UsageNotificationValidator;
import com.elitecore.corenetvertex.pkg.importpkg.PkgImportOperation;
import com.elitecore.corenetvertex.pkg.notification.QuotaNotificationData;
import com.elitecore.corenetvertex.pkg.notification.UsageNotificationData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileData;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData;
import com.elitecore.corenetvertex.sm.Replicable;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.DoubleToStringAdapter;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.TimestampToStringAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * This class represents the Package entity
 *
 * @author aditya
 *
 */

@Entity
@Table(name = "TBLM_PACKAGE")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name","description","type","currency","status","packageMode","validityPeriod","validityPeriodUnit","exclusiveAddOn","multipleSubscription","price","quotaProfileType","quotaProfiles","qosProfiles","syQuotaProfileDatas","orderNumber","availabilityStartDate","availabilityEndDate","alwaysPreferPromotionalQoS","usageNotificationDatas","quotaNotificationDatas","param1","param2","tariffSwitchSupport","rncProfileDatas","rateCards"})
@Import(required = true, validatorClass = PkgValidator.class, importClass = PkgImportOperation.class)
@XmlRootElement(name="pkgData")
public class PkgData extends ResourceData implements Serializable,Replicable {

	private static final long serialVersionUID = 1L;
	@SerializedName(FieldValueConstants.NAME)private String name;
	@SerializedName(FieldValueConstants.DESCRIPTION)private String description;
	@SerializedName(FieldValueConstants.TYPE)private String type;
	@SerializedName(FieldValueConstants.MODE)private String packageMode;
	@SerializedName(FieldValueConstants.VALIDITIY_PERIOD)private Integer validityPeriod;
	@SerializedName(FieldValueConstants.VALIDITY_PERIOD_UNIT)private ValidityPeriodUnit validityPeriodUnit = ValidityPeriodUnit.DAY;
	@SerializedName(FieldValueConstants.EXCLUSIVE_ADDON)private Boolean exclusiveAddOn = CommonStatusValues.DISABLE.isBooleanValue();
	@SerializedName(FieldValueConstants.MULTIPLE_SUBSCRIPTION)private Boolean multipleSubscription = CommonStatusValues.ENABLE.isBooleanValue();
	@XmlTransient private Boolean replaceableByAddOn = CommonStatusValues.DISABLE.isBooleanValue();
	@SerializedName(FieldValueConstants.PRICE) @XmlJavaTypeAdapter(DoubleToStringAdapter.class) private Double price;
	@SerializedName(FieldValueConstants.ORDER_NUMBER) private Integer orderNumber;
	@SerializedName(FieldValueConstants.ALWAYS_PREFER_PROMOTIONAL_QOS) private Boolean alwaysPreferPromotionalQoS = false;
	@SerializedName(FieldValueConstants.CURRENCY)private String currency;
	@XmlTransient private Long gracePeriod;
	@XmlTransient private String gracePeriodUnit;
	@XmlJavaTypeAdapter(TimestampToStringAdapter.class) private Timestamp availabilityStartDate;
	@XmlJavaTypeAdapter(TimestampToStringAdapter.class) private Timestamp availabilityEndDate;

	private transient PolicyStatus policyStatus = PolicyStatus.SUCCESS;
	@XmlTransient private String failReason;
	@XmlTransient private String partialFailReason;
	private QuotaProfileType quotaProfileType = QuotaProfileType.RnC_BASED;
	private transient List<SyQuotaProfileData> syQuotaProfileDatas;

	private transient List<QuotaProfileData> quotaProfiles;
	private transient List<QosProfileData> qosProfiles;

	private transient List<UsageNotificationData> usageNotificationDatas;
	private transient List<QuotaNotificationData> quotaNotificationDatas;
	private transient Set<String> applicableBasePackages;
	private transient Set<String> applicableTopUps;
	@SerializedName(FieldValueConstants.PARAM1) private String param1;
	private transient List<PkgGroupOrderData> pkgGroupWiseOrders;


	@SerializedName(FieldValueConstants.PARAM2)private String param2;
	@SerializedName(FieldValueConstants.TARIFF_SWITCH_SUPPORT)private Boolean tariffSwitchSupport = CommonStatusValues.ENABLE.isBooleanValue();

	private transient List<RncProfileData> rncProfileDatas;
	private transient List<DataRateCardData> rateCards;


	public PkgData() {
		quotaProfiles = Collectionz.newArrayList();
		qosProfiles = Collectionz.newArrayList();
		usageNotificationDatas = Collectionz.newArrayList();
		quotaNotificationDatas = Collectionz.newArrayList();
		syQuotaProfileDatas = Collectionz.newArrayList();
		applicableTopUps = Collectionz.newHashSet();
		applicableBasePackages = Collectionz.newHashSet();
		pkgGroupWiseOrders = Collectionz.newLinkedList();
		rncProfileDatas = Collectionz.newArrayList();
		rateCards = new ArrayList<>();
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "pkgData", orphanRemoval = false)
	@Fetch(FetchMode.SUBSELECT)
	@Where(clause="STATUS != 'DELETED'")
	@XmlElementWrapper(name="quota-profiles")
	@XmlElement(name="quota-Profile")
	@Import(required = true, validatorClass = QuotaProfileValidator.class)
	public List<QuotaProfileData> getQuotaProfiles() {
		return quotaProfiles;
	}

	public void setQuotaProfiles(List<QuotaProfileData> quotaProfiles) {
		this.quotaProfiles = quotaProfiles;
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
	@XmlElement(name="status")
	public String getStatus() {
		return super.getStatus();
	}

	@Override
	public void setStatus(String status) {//NOSONAR
		//Removing this method will cause problem when importing package.
		super.setStatus(status);
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "TYPE")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "PACKAGE_MODE")
	public String getPackageMode() {
		return packageMode;
	}

	public void setPackageMode(String packageMode) {
		this.packageMode = packageMode;
	}

	@Column(name = "VALIDITY_PERIOD")
	public Integer getValidityPeriod() {
		return validityPeriod;
	}

	public void setValidityPeriod(Integer validityPeriod) {
		this.validityPeriod = validityPeriod;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "VALIDITY_PERIOD_UNIT")
	public ValidityPeriodUnit getValidityPeriodUnit() {
		return validityPeriodUnit;
	}

	public void setValidityPeriodUnit(ValidityPeriodUnit validityPeriodUnit) {
		this.validityPeriodUnit = validityPeriodUnit;
	}

	@Transient
	public String getValidity() {
		if (validityPeriod != null) {
			return validityPeriod + " " + validityPeriodUnit.displayValue;
		}
		return "";
	}

	public void setValidity(String validity) {
		//IGNORED
	}

	@Column(name = "PRICE")
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@Column(name = "GRACE_PERIOD")
	public Long getGracePeriod() {
		return this.gracePeriod;
	}

	public void setGracePeriod(Long gracePeriod) {
		this.gracePeriod = gracePeriod;
	}

	@Column(name = "GRACE_PERIOD_UNIT")
	public String getGracePeriodUnit() {
		return this.gracePeriodUnit;
	}

	public void setGracePeriodUnit(String gracePeriodUnit) {
		this.gracePeriodUnit = gracePeriodUnit;
	}

	@Column(name = "AVAILABILITY_START_DATE")
	public Timestamp getAvailabilityStartDate() {
		return this.availabilityStartDate;
	}

	public void setAvailabilityStartDate(Timestamp availabilityStartDate) {
		this.availabilityStartDate = availabilityStartDate;
	}

	@Column(name = "AVAILABILITY_END_DATE")
	public Timestamp getAvailabilityEndDate() {
		return this.availabilityEndDate;
	}

	public void setAvailabilityEndDate(Timestamp availabilityEndDate) {
		this.availabilityEndDate = availabilityEndDate;
	}


	@Column(name="CURRENCY")
	public String getCurrency() {
		return this.currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}


	@Column(name = "EXCLUSIVE_ADDON")
	public Boolean isExclusiveAddOn() {
		return exclusiveAddOn;
	}

	public void setExclusiveAddOn(Boolean exclusiveAddOn) {
		if(exclusiveAddOn != null) {
			this.exclusiveAddOn = exclusiveAddOn;
		}
	}

	@Column(name = "MULTIPLE_SUBSCRIPTION")
	public Boolean isMultipleSubscription() {
		return multipleSubscription;
	}

	public void setMultipleSubscription(Boolean multipleSubscription) {
		if(multipleSubscription != null) {
			this.multipleSubscription = multipleSubscription;
		}
	}

	@Column(name = "REPLACEABLE_BY_ADDON")
	public Boolean isReplaceableByAddOn() {
		return replaceableByAddOn;
	}

	public void setReplaceableByAddOn(Boolean replacebleAddOn) {
		if(replacebleAddOn != null) {
			this.replaceableByAddOn = replacebleAddOn;
		}
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "pkgData", orphanRemoval = false)
	@Fetch(FetchMode.SUBSELECT)
	@Where(clause="STATUS != 'DELETED'")
	@OrderBy(clause="ORDER_NO asc")
	@XmlElementWrapper(name="qos-profiles")
	@XmlElement(name="qos-profile")
	@Import(required = true, validatorClass = QosProfileValidator.class)
	public List<QosProfileData> getQosProfiles() {
		return qosProfiles;
	}

	public void setQosProfiles(List<QosProfileData> qosProfiles) {
		this.qosProfiles = qosProfiles;
	}


	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "pkgData", orphanRemoval = false)
	@Fetch(FetchMode.SUBSELECT)
	@XmlElementWrapper(name="rate-cards")
	@XmlElement(name="rate-card")
	@Import(required = true, validatorClass = DataRateCardValidator.class)
	public List<DataRateCardData> getRateCards() {
		return rateCards;
	}

	public void setRateCards(List<DataRateCardData> rateCards) {
		this.rateCards = rateCards;
	}

	public void setPolicyStatus(PolicyStatus policyStatus) {
		this.policyStatus = policyStatus;

	}

	public void setFailReasons(String failReasons) {
		this.failReason = failReasons;

	}

	public void setPartialFailReasons(String partialFailReasons) {
		this.partialFailReason = partialFailReasons;
	}

	@Transient
	@XmlTransient
	public String getFailReason() {
		return failReason;
	}

	@Transient
	@XmlTransient
	public String getPartialFailReason() {
		return partialFailReason;
	}

	@Transient
	@XmlTransient
	public PolicyStatus getPolicyStatus() {
		return policyStatus;
	}

	@Transient
	public Integer getPriority() {
		return null;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "QUOTA_PROFILE_TYPE")
	public QuotaProfileType getQuotaProfileType() {
		return quotaProfileType;
	}

	public void setQuotaProfileType(QuotaProfileType quotaProfileType) {
		this.quotaProfileType = quotaProfileType;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "pkgData", orphanRemoval = false)
	@Fetch(FetchMode.SUBSELECT)
	@Where(clause="STATUS != 'DELETED'")
	@XmlElementWrapper(name="sy-quota-profiles")
	@XmlElement(name="sy-quota-Profile")
	@Import(required = true, validatorClass = SyQuotaProfileValidator.class)
	public List<SyQuotaProfileData> getSyQuotaProfileDatas() {
		return syQuotaProfileDatas;
	}

	public void setSyQuotaProfileDatas(List<SyQuotaProfileData> syQuotaProfileDatas) {
		this.syQuotaProfileDatas = syQuotaProfileDatas;
	}


	@Override
	public String  toString() {

		ToStringBuilder toStringBuilder = new ToStringBuilder(this,
				ToStringStyle.CUSTOM_TO_STRING_STYLE).append("Name", name)
				.append("Type", type)
				.append("Package Mode", packageMode)
				.append("Quota Profile Type", quotaProfileType != null ?  quotaProfileType.getVal() : "N/A")
				.append("Availability Start Date", availabilityStartDate)
				.append("Availability End Date", availabilityEndDate)
				.append("Order Number", orderNumber);

		if (PkgType.BASE.val.equalsIgnoreCase(type)) {
			toStringBuilder.append("Applicable TopUps", applicableTopUps);

		} else if (PkgType.ADDON.val.equalsIgnoreCase(type)) {
			toStringBuilder.append("Exclusive AddOn", exclusiveAddOn);

		}

		toStringBuilder.append("\t");
		if (Collectionz.isNullOrEmpty(qosProfiles) == false) {

			for (QosProfileData qosProfileData : qosProfiles) {
				if(qosProfileData != null) {
					toStringBuilder.append("QoS Profile", qosProfileData);
				}
			}
		}

		if (Collectionz.isNullOrEmpty(quotaProfiles) == false) {

			for (QuotaProfileData quotaProfileData : quotaProfiles) {
				if(quotaProfileData != null) {
					toStringBuilder.append("UM Quota Profile", quotaProfileData);
				}
			}
		}

		if (Collectionz.isNullOrEmpty(syQuotaProfileDatas) == false) {

			for (SyQuotaProfileData quotaProfileData : syQuotaProfileDatas) {
				if(quotaProfileData != null) {
					toStringBuilder.append("Sy Quota Profile", quotaProfileData);
				}
			}
		}

		if(Collectionz.isNullOrEmpty(rncProfileDatas) == false){
			for(RncProfileData rncProfileData : rncProfileDatas){
				if(rncProfileData != null){
					toStringBuilder.append("RnC Quota Profiles",rncProfileData);
				}
			}
		}

		if(Collectionz.isNullOrEmpty(rateCards) == false){
			for(DataRateCardData rateCardData : rateCards) {
				if(rateCardData != null) {
					toStringBuilder.append("Data Rate Cards", rateCardData);
				}
			}
		}

		toStringBuilder.append("Policy Status", policyStatus);
		if (failReason != null) {
			toStringBuilder.append("Fail Reasons", failReason);
		}
		if (partialFailReason != null) {
			toStringBuilder.append("Partial Fail Reasons", partialFailReason);
		}

		return toStringBuilder.toString();

	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "pkgData", orphanRemoval = false)
	@Fetch(FetchMode.SUBSELECT)
	@Where(clause="STATUS != 'DELETED'")
	@XmlElementWrapper(name="usgae-notifications")
	@XmlElement(name="usge-notification")
	@Import(validatorClass = UsageNotificationValidator.class, required = false)
	public List<UsageNotificationData> getUsageNotificationDatas() {
		return usageNotificationDatas;
	}

	public void setUsageNotificationDatas(
			List<UsageNotificationData> usageNotificationDatas) {
		this.usageNotificationDatas = usageNotificationDatas;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "pkgData", orphanRemoval = false)
	@Fetch(FetchMode.SUBSELECT)
	@Where(clause="STATUS != 'DELETED'")
	@XmlElementWrapper(name="quota-notifications")
	@XmlElement(name="quota-notification")
	@Import(validatorClass = QuotaNotificationValidator.class, required = false)
	public List<QuotaNotificationData> getQuotaNotificationDatas() {
		return quotaNotificationDatas;
	}

	public void setQuotaNotificationDatas(List<QuotaNotificationData> quotaNotificationDatas) {
		this.quotaNotificationDatas = quotaNotificationDatas;
	}

	@Transient
	@Override
	public String getHierarchy() {
		return getId() + "<br>" + name;
	}

	@Override
	public JsonObject toJson(){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.NAME , name);
		jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
		jsonObject.addProperty(FieldValueConstants.GROUPS, getGroupNames());
		jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
		jsonObject.addProperty(FieldValueConstants.TYPE, type);
		jsonObject.addProperty("Quota Profile Type", quotaProfileType.getVal());
		jsonObject.addProperty(FieldValueConstants.CURRENCY, getCurrency());

		//enable on type add-on
		if(PkgType.ADDON.val.equalsIgnoreCase(type)) {
			jsonObject.addProperty("Exclusive Add On", exclusiveAddOn);

		}

		jsonObject.addProperty(FieldValueConstants.PARAM1, param1);
		jsonObject.addProperty(FieldValueConstants.PARAM2, param2);
		jsonObject.addProperty("Package Mode", packageMode);

		//for prommotional pacakge type = promotional
		if(PkgType.PROMOTIONAL.val.equalsIgnoreCase(type)) {
			jsonObject.addProperty("Always Prefer Promotional QOS", alwaysPreferPromotionalQoS);

		}
		if(type.equalsIgnoreCase(PkgType.PROMOTIONAL.name()) || type.equalsIgnoreCase(PkgType.EMERGENCY.name())) {
			//for promotional & emergency
			jsonObject.addProperty(FieldValueConstants.AVAILABILITY_START_DATE, availabilityStartDate.toString());
			jsonObject.addProperty(FieldValueConstants.AVAILABILITY_END_DATE, availabilityEndDate.toString());
		}

		if(Collectionz.isNullOrEmpty(quotaProfiles) == false || Collectionz.isNullOrEmpty(syQuotaProfileDatas) == false || Collectionz.isNullOrEmpty(rncProfileDatas) == false){
			if(quotaProfileType == QuotaProfileType.SY_COUNTER_BASED) {
				JsonArray jsonArray = new JsonArray();
				for (SyQuotaProfileData syQuotaProfileData : syQuotaProfileDatas) {
					jsonArray.add(syQuotaProfileData.toJson());
				}
				jsonObject.add("Quota Profiles", jsonArray);

			} else if(quotaProfileType == QuotaProfileType.USAGE_METERING_BASED) {
				JsonArray jsonArray = new JsonArray();
				for (QuotaProfileData quotaProfile : quotaProfiles) {
					jsonArray.add(quotaProfile.toJson());
				}
				jsonObject.add("Quota Profiles", jsonArray);

			} else if(quotaProfileType == QuotaProfileType.RnC_BASED) {
				JsonArray jsonArray = new JsonArray();
				for (RncProfileData rncProfileData : rncProfileDatas) {
					jsonArray.add(rncProfileData.toJson());
				}
				jsonObject.add("Quota Profiles", jsonArray);
			}
		}

		if(Collectionz.isNullOrEmpty(rateCards) == false){
			JsonArray rateCardJsonArray = new JsonArray();
			for (DataRateCardData rateCard : rateCards) {
				rateCardJsonArray.add(rateCard.toJson());
			}
			jsonObject.add("Rate Cards", rateCardJsonArray);
		}

		if (Collectionz.isNullOrEmpty(qosProfiles) == false) {
			JsonArray qosProfileJsonArray = new JsonArray();
			for (QosProfileData qosProfile : qosProfiles) {
				qosProfileJsonArray.add(qosProfile.toJson());
			}
			jsonObject.add("Qos Profiles", qosProfileJsonArray);
		}

		if(Collectionz.isNullOrEmpty(usageNotificationDatas) == false) {
			JsonArray usageNotificationJsonArray = new JsonArray();
			for (UsageNotificationData usageNotificationData : usageNotificationDatas) {
				usageNotificationJsonArray.add(usageNotificationData.toJson());
			}
			jsonObject.add("Usage Notifications", usageNotificationJsonArray);
		}

		if(Collectionz.isNullOrEmpty(quotaNotificationDatas) == false) {
			JsonArray quotaNotificationJsonArray = new JsonArray();
			for (QuotaNotificationData quotaNotificationData: quotaNotificationDatas) {
				quotaNotificationJsonArray.add(quotaNotificationData.toJson());
			}
			jsonObject.add("Quota Notifications", quotaNotificationJsonArray);
		}

		return jsonObject;
	}

	@Override
	@Transient
	public String getResourceName() {
		return getName();
	}

	@Override
	@Column(name="GROUPS")
	public String getGroups() {
		return super.getGroups();
	}



	@Transient
	public Set<String> getApplicableBasePackages() {
		return applicableBasePackages;
	}

	@Transient
	public void addApplicableBasePackage(String applicableBasePackage) {
		this.applicableBasePackages.add(applicableBasePackage);
	}

	@Transient
	@XmlTransient
	public Set<String> getApplicableTopUps() {
		return applicableTopUps;
	}

	@Transient
	public void setApplicableTopUps(String applicableTopUp) {
		this.applicableTopUps.add(applicableTopUp);
	}

	public void setDeletedStatus(){
		setStatus(CommonConstants.STATUS_DELETED);
		for(QuotaProfileData quotaProfile:quotaProfiles){
			quotaProfile.setStatus(CommonConstants.STATUS_DELETED);
		}
		for(QosProfileData qosProfile:qosProfiles){
			qosProfile.setDeletedStatus(CommonConstants.STATUS_DELETED);
		}
		for(SyQuotaProfileData syQuotaProfile:syQuotaProfileDatas){
			syQuotaProfile.setStatus(CommonConstants.STATUS_DELETED);
		}
		for(UsageNotificationData usageNotificationData:usageNotificationDatas){
			usageNotificationData.setStatus(CommonConstants.STATUS_DELETED);
		}
		for(QuotaNotificationData quotaNotificationData:quotaNotificationDatas){
			quotaNotificationData.setStatus(CommonConstants.STATUS_DELETED);
		}
		for(RncProfileData rncProfileData:rncProfileDatas){
			rncProfileData.setStatus(CommonConstants.STATUS_DELETED);
		}
		if(Collectionz.isNullOrEmpty(rateCards) == false) {
			for (DataRateCardData rateCardData : rateCards) {
				rateCardData.setStatus(CommonConstants.STATUS_DELETED);
			}
		}

	}



	@Column(name = "ORDER_NO")
	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Column(name = "PARAM_1")
	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	@Column(name = "PARAM_2")
	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	@Column(name="PREFER_PROMOTIONAL_QOS")
	public Boolean getAlwaysPreferPromotionalQoS() {
		return alwaysPreferPromotionalQoS;
	}

	public void setAlwaysPreferPromotionalQoS(Boolean alwaysPreferPromotionalQoS) {
		this.alwaysPreferPromotionalQoS = alwaysPreferPromotionalQoS;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "pkgData", orphanRemoval = false)
	@Fetch(FetchMode.SUBSELECT)
	@OrderBy(clause = "ORDER_NO ASC")
	public List<PkgGroupOrderData> getPkgGroupWiseOrders() {
		return pkgGroupWiseOrders;
	}

	public void setPkgGroupWiseOrders(List<PkgGroupOrderData> pkgGroupWiseOrders) {
		this.pkgGroupWiseOrders = pkgGroupWiseOrders;
	}

	@Column(name="TARIFF_SWITCH_SUPPORT")
	public Boolean getTariffSwitchSupport() {
		return tariffSwitchSupport;
	}

	public void setTariffSwitchSupport(Boolean tariffSwitchSupport) {
		this.tariffSwitchSupport = tariffSwitchSupport;
	}


	@Transient
	public String getPkgTypeDisplayValue() {
		if(Strings.isNullOrBlank(type)){
			return "";
		}
		return PkgType.fromName(type).val;
	}

	@OneToMany(fetch = FetchType.LAZY , mappedBy = "pkgData" , cascade = {CascadeType.ALL} , orphanRemoval = false)
	@Fetch(FetchMode.SUBSELECT)
	@Where(clause="STATUS != 'DELETED'")
	@XmlElement(name="rncProfileDatas")
	@Import(required = true, validatorClass = RnCProfileValidator.class)
	public List<RncProfileData> getRncProfileDatas() {
		return rncProfileDatas;
	}

	public void setRncProfileDatas(List<RncProfileData> rncProfileDatas) {
		this.rncProfileDatas = rncProfileDatas;
	}

	@Override
	public PkgData copyModel() {
		PkgData newData = new PkgData();
		newData.setId(null);

		newData.description = this.description;
		newData.type = this.type;
		newData.packageMode = PkgMode.DESIGN.name();
		newData.validityPeriod = this.validityPeriod;
		newData.validityPeriodUnit = this.validityPeriodUnit;
		newData.currency = this.currency;
		newData.exclusiveAddOn = this.exclusiveAddOn;
		newData.multipleSubscription = this.multipleSubscription;
		newData.replaceableByAddOn = this.replaceableByAddOn;
		newData.price = this.price;
		newData.orderNumber = this.orderNumber;
		newData.alwaysPreferPromotionalQoS = this.alwaysPreferPromotionalQoS;

		newData.gracePeriod = this.gracePeriod;
		newData.gracePeriodUnit = this.gracePeriodUnit;
		newData.availabilityStartDate = this.availabilityStartDate;
		newData.availabilityEndDate = this.availabilityEndDate;

		newData.quotaProfileType = this.quotaProfileType;
		newData.param1 = this.param1;
		newData.param2 = this.param2;
		newData.tariffSwitchSupport = this.tariffSwitchSupport;
		newData.setGroupNames(this.getGroupNames());
		newData.setGroups(this.getGroups());
		newData.setCreatedDate(this.getCreatedDate());
		newData.setCreatedByStaff(this.getCreatedByStaff());

		if(newData.quotaProfileType == QuotaProfileType.SY_COUNTER_BASED) {
			List<SyQuotaProfileData> syQuotaProfiles = new ArrayList<>();
			for (SyQuotaProfileData syQuotaProfileData : this.syQuotaProfileDatas) {
				SyQuotaProfileData syQuotaProfileDataCopy = syQuotaProfileData.copyModel(newData);
				syQuotaProfileDataCopy.setId(null);
				syQuotaProfileDataCopy.setPkgData(newData);
				syQuotaProfiles.add(syQuotaProfileDataCopy);
			}
			newData.setSyQuotaProfileDatas(syQuotaProfiles);

		} else if(newData.quotaProfileType == QuotaProfileType.USAGE_METERING_BASED) {
			List<QuotaProfileData> quotaProfileDataList = Collectionz.newArrayList();
			for (QuotaProfileData quotaProfileData : this.quotaProfiles) {
				QuotaProfileData quotaProfileDataCopy = quotaProfileData.copyModel();
				quotaProfileDataCopy.setId(null);
				quotaProfileDataCopy.setCreatedDateAndStaff(newData.getCreatedByStaff());
				quotaProfileDataCopy.setPkgData(newData);
				quotaProfileDataList.add(quotaProfileDataCopy);
			}
			newData.setQuotaProfiles(quotaProfileDataList);

		} else if(newData.quotaProfileType == QuotaProfileType.RnC_BASED){
			List<RncProfileData> rncProfiles = Collectionz.newArrayList();
			for (RncProfileData rncProfileData : this.rncProfileDatas){
				RncProfileData rncProfileDataCopy = rncProfileData.copyModel();
				rncProfileDataCopy.setId(null);
				rncProfileDataCopy.setCreatedDateAndStaff(newData.getCreatedByStaff());
				rncProfileDataCopy.setPkgData(newData);
				rncProfiles.add(rncProfileDataCopy);
			}
			newData.setRncProfileDatas(rncProfiles);
		}
		List<DataRateCardData> rateCardList = Collectionz.newArrayList();
		for(DataRateCardData dataRateCardData : this.rateCards){
			DataRateCardData dataRateCardDataCopy = dataRateCardData.copyModel();
			dataRateCardDataCopy.setId(null);
			dataRateCardDataCopy.setCreatedDateAndStaff(newData.getCreatedByStaff());
			dataRateCardDataCopy.setPkgData(newData);
			rateCardList.add(dataRateCardDataCopy);
		}
		newData.setRateCards(rateCardList);

		List<QosProfileData> qosProfileList = Collectionz.newArrayList();
		for(QosProfileData qosProfileData : this.qosProfiles){
			QosProfileData qosProfileDataCopy = qosProfileData.copyModel(newData);
			qosProfileDataCopy.setId(null);
			qosProfileDataCopy.setPkgData(newData);
			qosProfileList.add(qosProfileDataCopy);
		}
		newData.setQosProfiles(qosProfileList);

		if(this.usageNotificationDatas != null) {
			List<UsageNotificationData> usageNotificationDatasList = Collectionz.newArrayList();
			for (UsageNotificationData usageNotificationData : this.usageNotificationDatas) {
				UsageNotificationData usageNotificationDataCopy = usageNotificationData.copyModel(newData);
				usageNotificationDataCopy.setId(null);
				usageNotificationDataCopy.setCreatedDateAndStaff(newData.getCreatedByStaff());
				usageNotificationDataCopy.setPkgData(newData);
				usageNotificationDatasList.add(usageNotificationDataCopy);
			}
			newData.setUsageNotificationDatas(usageNotificationDatasList);
		}

		if(this.quotaNotificationDatas != null) {
			List<QuotaNotificationData> quotaNotificationDatasList = Collectionz.newArrayList();
			for (QuotaNotificationData quotaNotificationData : this.quotaNotificationDatas) {
				QuotaNotificationData usageNotificationDataCopy = quotaNotificationData.copyModel(newData);
				usageNotificationDataCopy.setId(null);
				usageNotificationDataCopy.setCreatedDateAndStaff(newData.getCreatedByStaff());
				usageNotificationDataCopy.setPkgData(newData);
				quotaNotificationDatasList.add(usageNotificationDataCopy);
			}
			newData.setQuotaNotificationDatas(quotaNotificationDatasList);
		}

		Set<String> applicableBasePackageSet = Collectionz.newHashSet();
		Iterator<String> applicableBasePackagesIterator = this.applicableBasePackages.iterator();
		while (applicableBasePackagesIterator.hasNext()){
			applicableBasePackageSet.add(applicableBasePackagesIterator.next());
		}
		newData.applicableBasePackages = applicableBasePackageSet;

		Set<String> applicableTopUpSet = Collectionz.newHashSet();
		Iterator<String> applicableTopUpsIterator = this.applicableTopUps.iterator();
		while (applicableTopUpsIterator.hasNext()){
			applicableTopUpSet.add(applicableTopUpsIterator.next());
		}
		newData.applicableTopUps = applicableTopUpSet;

		List<PkgGroupOrderData> pkgGroupWiseOrderList = Collectionz.newArrayList();
		for(PkgGroupOrderData pkgGroupOrderData : this.pkgGroupWiseOrders){
			PkgGroupOrderData pkgGroupOrderDataCopy = pkgGroupOrderData.copyModel();
			pkgGroupOrderDataCopy.setId(null);
			pkgGroupOrderDataCopy.setPkgData(newData);
			pkgGroupWiseOrderList.add(pkgGroupOrderDataCopy);
		}
		newData.setPkgGroupWiseOrders(pkgGroupWiseOrderList);
		return newData;
	}
}
