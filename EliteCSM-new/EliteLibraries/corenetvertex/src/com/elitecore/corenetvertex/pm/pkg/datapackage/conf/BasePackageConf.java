package com.elitecore.corenetvertex.pm.pkg.datapackage.conf;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaNotificationScheme;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;

public class BasePackageConf {

	private final String name;
	private final String id;
	private QuotaProfileType quotaProfileType;
	private List<QoSProfile> qosProfiles;
	private PolicyStatus status = PolicyStatus.SUCCESS;
	private String failReason;
	private String partialFailReason;
	private PkgStatus availabilityStatus;
	private UsageNotificationScheme usageNotificationScheme;
	private PkgData pkgData;
	private String description;
	private Double price;
	private Timestamp availabilityStartDate;
	private Timestamp availabilityEndDate;
	private PkgMode packageMode;
	private List<String> groupIds;
	private String param1;
	private String param2;
	private QuotaNotificationScheme quotaNotificationScheme;
	private String currency;

	public BasePackageConf(PkgData pkgData, String id, String name,
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
		this.currency=currency;
	}

	public BasePackageConf addQoSProfile(QoSProfile qosProfile) {
		if (qosProfiles == null) {
			qosProfiles = new ArrayList<QoSProfile>();
		}

		qosProfiles.add(qosProfile);

		return this;
	}

	public BasePackageConf addQuotaProfileType(QuotaProfileType type) {
		quotaProfileType = type;
		return this;
	}

	public BasePackageConf addQoSProfiles(List<QoSProfile> qosProfiles) {
		if (this.qosProfiles == null) {
			this.qosProfiles = new ArrayList<QoSProfile>();
		}
		this.qosProfiles.addAll(qosProfiles);

		return this;
	}

	public BasePackageConf addAvailabilityStatus(PkgStatus availabilityStatus) {
		this.availabilityStatus = availabilityStatus;
		return this;
	}

	public BasePackageConf addUsageNotificationScheme(UsageNotificationScheme usageNotificationScheme) {
		this.usageNotificationScheme = usageNotificationScheme;
		return this;
	}

	public BasePackageConf addQuotaNotificationScheme(QuotaNotificationScheme quotaNotificationScheme) {
		this.quotaNotificationScheme = quotaNotificationScheme;
		return this;
	}

	public BasePackageConf addFailReason(String failReason) {
		if (failReason != null) {
			status = PolicyStatus.FAILURE;
			this.failReason = failReason;
		}
		return this;
	}

	public BasePackageConf addPartialFailReason(String partialFailReason) {
		if (partialFailReason != null) {
			if (status != PolicyStatus.FAILURE) {
				status = PolicyStatus.PARTIAL_SUCCESS;
			}
			this.partialFailReason = partialFailReason;
		}
		return this;
	}

	public BasePackageConf withQoSProfiles(List<QoSProfile> qosProfiles) {
		this.qosProfiles = qosProfiles;
		return this;
	}

	public BasePackageConf addPolicyStatus(PolicyStatus policyStatus) {
		this.status = policyStatus;
		return this;
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

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public QuotaProfileType getQuotaProfileType() {
		return quotaProfileType;
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

	public PkgStatus getAvailabilityStatus() {
		return availabilityStatus;
	}

	public UsageNotificationScheme getUsageNotificationScheme() {
		return usageNotificationScheme;
	}

	public QuotaNotificationScheme getQuotaNotificationScheme() {
		return quotaNotificationScheme;
	}

	public PkgData getPkgData() {
		return pkgData;
	}
	
	public List<String> getGroupIds() {
		return groupIds;
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

	public String getCurrency() {
		return currency;
	}
}