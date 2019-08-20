package com.elitecore.corenetvertex.pm.pkg.datapackage;

import com.elitecore.acesstime.TimePeriod;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaNotificationScheme;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public interface UserPackage extends Serializable{

	public abstract String getType();

	public abstract PkgType getPackageType();

	public abstract String getName();

	public abstract String getServiceId(String monitoringKey);

	public abstract QuotaProfile getQuotaProfileByMonitoringKey(String monitoringKey);

	QuotaProfile getQuotaProfile(String id);

	public abstract List<TimePeriod> getTimeSlots();

	public abstract QuotaProfileType getQuotaProfileType();

	public abstract boolean isQuotaProfileExist();

	boolean isQoSProfileDetailsExist();

	public abstract long getNextSessionTimeOut(TimePeriod currTimePeriod);

	public abstract String getId();

	public abstract List<QoSProfile> getQoSProfiles();
	public abstract QoSProfile getQoSProfile(String id);

	public abstract PolicyStatus getStatus();

	public abstract String getFailReason();

	public abstract String getPartialFailReason();

	public abstract PkgStatus getAvailabilityStatus();

	public abstract UsageNotificationScheme getUsageNotificationScheme();

    QuotaNotificationScheme getQuotaNotificationScheme();

    public abstract boolean isMonitoringKeyExist(String monitoringKey);

	public abstract boolean isQuotaProfileExists(String quotaProfileId);

	boolean isDataRateCardExists(String dataRateCardId);

	public abstract boolean isHsqLevelMonitoringKey(String monitoringKey);

	public abstract PkgMode	getMode();

	public abstract void setPolicyStatus(PolicyStatus status);

	public abstract void setPartialFailReason(String partialFailReason);

	public abstract void setFailReason(String failReason);

	public abstract String getDescription();

	public abstract Double getPrice();

	public abstract Timestamp getAvailabilityStartDate();

	public abstract Timestamp getAvailabilityEndDate();

	public abstract List<QuotaProfile> getQuotaProfiles();

	public abstract List<String> getGroupIds();

	public abstract String getPackageMode();

	public abstract String getParam1();

	public abstract String getParam2();

	public abstract DataRateCard getDataRateCard(String id);

	public abstract String getCurrency();
	
}