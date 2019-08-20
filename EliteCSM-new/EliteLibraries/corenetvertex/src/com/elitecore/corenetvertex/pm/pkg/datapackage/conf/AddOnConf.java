package com.elitecore.corenetvertex.pm.pkg.datapackage.conf;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaNotificationScheme;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;

public class AddOnConf {

	private String id;
	private String name;
	private QuotaProfileType quotaProfileType;
	private boolean isExclusive;
	private ValidityPeriodUnit validityPeriodUnit;
	private int validityPeriod;
	private List<QoSProfile> qosProfiles;
	private PolicyStatus status = PolicyStatus.SUCCESS;
	private String failReason;
	private String partialFailReason;
	private Boolean multipleSubscription;
	private PkgStatus availabilityStatus;
	private UsageNotificationScheme usageNotificationScheme;
	private PkgData pkgData;
	private String description;
	private Double price;
	private Timestamp availabilityStartDate;
	private Timestamp availabilityEndDate;
	private PkgMode packageMode;
	private List<String> groupIds;
	private boolean preferPromotionalQoS;
	private String param1;
	private String param2;
	private PkgType pkgType;
	private List<GroupManageOrder> pkgGroupOrderConfs;
	private QuotaNotificationScheme quotaNotificationScheme;
	private String currency;

	public AddOnConf(PkgData pkgData, String id, String name,
			PkgMode packageMode,
			String description,
			Double price,
			Timestamp availabilityStartDate,
			Timestamp availabilityEndDate, List<String> groupIds,String param1 ,String param2) {
		this.pkgData = pkgData;
		this.id = id;
		this.name = name;
		this.packageMode = packageMode;
		this.description = description;
		this.price = price;
		this.availabilityStartDate = availabilityStartDate;
		this.availabilityEndDate = availabilityEndDate;
		this.groupIds = groupIds;
		this.param1 = param1;
		this.param2 = param2;
	}

	public AddOnConf(PkgData pkgData, String id, String name,
					 PkgMode packageMode,
					 String description,
					 Double price,
					 Timestamp availabilityStartDate,
					 Timestamp availabilityEndDate, List<String> groupIds,String param1 ,String param2,String currency) {
		this.pkgData = pkgData;
		this.id = id;
		this.name = name;
		this.packageMode = packageMode;
		this.description = description;
		this.price = price;
		this.availabilityStartDate = availabilityStartDate;
		this.availabilityEndDate = availabilityEndDate;
		this.groupIds = groupIds;
		this.param1 = param1;
		this.param2 = param2;
		this.currency = currency;
	}

	public AddOnConf addQoSProfile(QoSProfile qosProfile) {
		if (qosProfiles == null) {
			qosProfiles = new ArrayList<QoSProfile>();
		}

		qosProfiles.add(qosProfile);

		return this;
	}

	public AddOnConf addQoSProfiles(List<QoSProfile> qosProfiles) {
		if (this.qosProfiles == null) {
			this.qosProfiles = new ArrayList<QoSProfile>();
		}
		this.qosProfiles.addAll(qosProfiles);

		return this;
	}

	public AddOnConf exclusiveAddOn() {
		this.isExclusive = true;
		return this;
	}
	
	public AddOnConf preferPromotionalQoS() {
		this.preferPromotionalQoS = true;
		return this;
	}

	public AddOnConf addQuotaProfileType(QuotaProfileType quotaProfileType) {
		this.quotaProfileType = quotaProfileType;
		return this;

	}

	public AddOnConf setValidityPeriod(int validityPeriod) {
		this.validityPeriod = validityPeriod;
		return this;
	}

	public AddOnConf withUsageNotificationScheme(UsageNotificationScheme usageNotificationScheme) {

		this.usageNotificationScheme = usageNotificationScheme;
		return this;
	}
	
	public AddOnConf withPkgGroupOrderConfs(List<GroupManageOrder> pkgGroupOrderConfs) {
		this.pkgGroupOrderConfs = pkgGroupOrderConfs;
		return this;
	}

	public AddOnConf withValidityPeriodUnit(ValidityPeriodUnit validityPeriodUnit) {
		this.validityPeriodUnit = validityPeriodUnit;
		return this;
	}

	public AddOnConf withAvailabilityStatus(PkgStatus availabilityStatus) {
		this.availabilityStatus = availabilityStatus;
		return this;
	}

	public AddOnConf withFailReason(String failReason) {
		if (failReason != null) {
			status = PolicyStatus.FAILURE;
			this.failReason = failReason;
		}
		return this;
	}

	public AddOnConf withPartialFailReason(String partialFailReason) {
		if (partialFailReason != null) {
			if (status != PolicyStatus.FAILURE) {
				status = PolicyStatus.PARTIAL_SUCCESS;
			}
			this.partialFailReason = partialFailReason;
		}
		return this;
	}

	public AddOnConf withMultipleSubscription(Boolean multipleSubscription) {
		this.multipleSubscription = multipleSubscription;
		return this;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public QuotaProfileType getQuotaProfileType() {
		return quotaProfileType;
	}

	public boolean isExclusive() {
		return isExclusive;
	}

	public ValidityPeriodUnit getValidityPeriodUnit() {
		return validityPeriodUnit;
	}

	public int getValidityPeriod() {
		return validityPeriod;
	}

	public List<QoSProfile> getQosProfiles() {
		return qosProfiles;
	}

	public PolicyStatus getStatus() {
		return status;
	}

	public String getFailReason() {
		return failReason;
	}

	public String getPartialFailReason() {
		return partialFailReason;
	}

	public Boolean getMultipleSubscription() {
		return multipleSubscription;
	}

	public PkgStatus getAvailabilityStatus() {
		return availabilityStatus;
	}

	public UsageNotificationScheme getUsageNotificationScheme() {
		return usageNotificationScheme;
	}

	public PkgData getPkgData() {
		return pkgData;
	}
	
	public String getDescription() {
		return description;
	}

	public Double getPrice() {
		return price;
	}

	public Timestamp getAvailabilityStartDate() {
		return availabilityStartDate;
	}

	public Timestamp getAvailabilityEndDate() {
		return availabilityEndDate;
	}

	public PkgMode getPackageMode() {
		return packageMode;
	}

	public List<String> getGroupIds() {
		return groupIds;
	}

	public boolean getPreferPromotionalQoS() {
		return preferPromotionalQoS;
	}
	
	public String getParam1() {
		if(Strings.isNullOrBlank(param1) == false) {
			return param1.trim();
		}
		return null;
	}

	public String getParam2() {
		if(Strings.isNullOrBlank(param2) == false) {
			return param2.trim();
		}
		return null;
	}

	public void addPackageType(PkgType pkgType) {
		this.pkgType = pkgType;
	}

	public PkgType getPkgType() {
		return pkgType;
	}

	public List<GroupManageOrder> getPkgGroupOrderConfs() {
		return pkgGroupOrderConfs;
	}

    public AddOnConf addQuotaNotificationScheme(QuotaNotificationScheme quotaNotificationScheme) {
		this.quotaNotificationScheme = quotaNotificationScheme;
		return this;
    }

	public QuotaNotificationScheme getQuotaNotificationScheme() {
		return quotaNotificationScheme;
	}

	public String getCurrency() {
		return currency;
	}
}