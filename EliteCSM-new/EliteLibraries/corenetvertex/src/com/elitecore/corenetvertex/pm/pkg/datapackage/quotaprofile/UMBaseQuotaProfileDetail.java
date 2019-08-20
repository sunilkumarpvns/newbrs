package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.util.Map;

public class UMBaseQuotaProfileDetail implements QuotaProfileDetail, ToStringable{
	private static final String QUOTA_UNLIMITED = "Unlimited";
	private static final long serialVersionUID = 1L;
	private final String quotaProfileId;
	private final String name;
	private final String serviceId;
	private final int fupLevel;
	private String serviceName;
	private Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage;

	public UMBaseQuotaProfileDetail(String quotaProfileId, String name, String serviceId, int fupLevel, String serviceName,
			Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage) {
		this.quotaProfileId = quotaProfileId;
		this.name = name;
		this.serviceId = serviceId;
		this.fupLevel = fupLevel;
		this.serviceName = serviceName;
		this.aggregationKeyToAllowedUsage = aggregationKeyToAllowedUsage;
	}

	public String getUsageKey() {
		return quotaProfileId + CommonConstants.USAGE_KEY_SEPARATOR + serviceId;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.newpkg.IUMBaseQuotaProfileDetail#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.elitecore.netvertex.pm.IQuotaProfileDetails#getQuotaProfileIdOrRateCardId()
	 */
	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.newpkg.IUMBaseQuotaProfileDetail#getQuotaProfileIdOrRateCardId()
	 */
	@Override
	public String getQuotaProfileId() {
		return quotaProfileId;
	}
 
	public AllowedUsage getBillingCycleAllowedUsage() {
		return aggregationKeyToAllowedUsage.get(AggregationKey.BILLING_CYCLE);
	}

	public AllowedUsage getDailyAllowedUsage() {
		return aggregationKeyToAllowedUsage.get(AggregationKey.DAILY);
	}

	public AllowedUsage getWeeklyAllowedUsage() {
		return aggregationKeyToAllowedUsage.get(AggregationKey.WEEKLY);
	}

	public AllowedUsage getCustomAllowedUsage() {
		return aggregationKeyToAllowedUsage.get(AggregationKey.CUSTOM);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.elitecore.netvertex.pm.IQuotaProfileDetails#getFupLevel()
	 */
	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.newpkg.IUMBaseQuotaProfileDetail#getFupLevel()
	 */
	@Override
	public int getFupLevel() {
		return fupLevel;
	}

	@Override
	public boolean isHsqLevel() {
		return true;
	}

	@Override
	public QuotaProfileType getQuotaProfileType() {
		return QuotaProfileType.USAGE_METERING_BASED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.elitecore.netvertex.pm.IQuotaProfileDetails#getServiceId()
	 */
	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.newpkg.IUMBaseQuotaProfileDetail#getServiceId()
	 */
	@Override
	public String getServiceId() {
		return serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public AllowedUsage getAllowedUsage(AggregationKey aggregationKey) {
		switch (aggregationKey) {
			case BILLING_CYCLE:
				return getBillingCycleAllowedUsage();
			case CUSTOM:
				return getCustomAllowedUsage();
			case DAILY:
				return getDailyAllowedUsage();
			case WEEKLY:
				return getWeeklyAllowedUsage();
		}
		
		return null;
	}

	private String getDisplayValue(long allowedUsage, DataUnit dataUnit) {

		if (allowedUsage == CommonConstants.QUOTA_UNLIMITED) {
			return QUOTA_UNLIMITED;
		}

		return allowedUsage + " " + dataUnit;
	}

	private String getDisplayValue(long allowedUsage, TimeUnit timeUnit) {

		if (allowedUsage == CommonConstants.QUOTA_UNLIMITED) {
			return QUOTA_UNLIMITED;
		}
		return allowedUsage + " " + timeUnit;

	}


	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();

		toString(builder);

		return builder.toString();

	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.newline()
				.newline()
				.incrementIndentation()
				.incrementIndentation()
				.append("FUP Level", fupLevel)
				.append("Service Name", serviceName);


		if (Maps.isNullOrEmpty(aggregationKeyToAllowedUsage) == false) {

			AllowedUsage billingCycleAllowedUsage = getBillingCycleAllowedUsage();
			if (billingCycleAllowedUsage != null) {
				builder.newline();
				builder.appendHeading(AggregationKey.BILLING_CYCLE.name());
				builder.incrementIndentation();
				builder.append("TOTAL", getDisplayValue(billingCycleAllowedUsage.getTotal(), billingCycleAllowedUsage.getTotalUnit()));
				builder.append("DOWNLOAD",getDisplayValue(billingCycleAllowedUsage.getDownload(), billingCycleAllowedUsage.getDownloadUnit()));
				builder.append("UPLOAD", getDisplayValue(billingCycleAllowedUsage.getUpload(), billingCycleAllowedUsage.getUploadUnit()));
				builder.append("TIME", getDisplayValue(billingCycleAllowedUsage.getTime(), billingCycleAllowedUsage.getTimeUnit()));
				builder.decrementIndentation();
			}

			AllowedUsage dailyAllowedUsage = getDailyAllowedUsage();
			if (dailyAllowedUsage != null) {
				builder.newline();
				builder.appendHeading(AggregationKey.DAILY.name());
				builder.incrementIndentation();
				builder.append("TOTAL", getDisplayValue(dailyAllowedUsage.getTotal(), dailyAllowedUsage.getTotalUnit()));
				builder.append("DOWNLOAD", getDisplayValue(dailyAllowedUsage.getDownload(), dailyAllowedUsage.getDownloadUnit()));
				builder.append("UPLOAD", getDisplayValue(dailyAllowedUsage.getUpload(), dailyAllowedUsage.getUploadUnit()));
				builder.append("TIME", getDisplayValue(dailyAllowedUsage.getTime(), dailyAllowedUsage.getTimeUnit()));
				builder.decrementIndentation();
			}

			AllowedUsage weeklyAllowedUsage = getWeeklyAllowedUsage();
			if (weeklyAllowedUsage != null) {
				builder.newline();
				builder.appendHeading(AggregationKey.WEEKLY.name());
				builder.incrementIndentation();
				builder.append("TOTAL", getDisplayValue(weeklyAllowedUsage.getTotal(), weeklyAllowedUsage.getTotalUnit()));
				builder.append("DOWNLOAD", getDisplayValue(weeklyAllowedUsage.getDownload(),weeklyAllowedUsage.getDownloadUnit()));
				builder.append("UPLOAD", getDisplayValue(weeklyAllowedUsage.getUpload(), weeklyAllowedUsage.getUploadUnit()));
				builder.append("TIME", getDisplayValue(weeklyAllowedUsage.getTime(), weeklyAllowedUsage.getTimeUnit()));
				builder.decrementIndentation();
			}

			AllowedUsage customAllowedUsage = getCustomAllowedUsage();
			if (customAllowedUsage != null) {
				builder.newline();
				builder.appendHeading(AggregationKey.CUSTOM.name());
				builder.incrementIndentation();
				builder.append("TOTAL ", getDisplayValue(customAllowedUsage.getTotal(), customAllowedUsage.getTotalUnit()));
				builder.append("DOWNLOAD", getDisplayValue(customAllowedUsage.getDownload(), customAllowedUsage.getDownloadUnit()));
				builder.append("UPLOAD", getDisplayValue(customAllowedUsage.getUpload(), customAllowedUsage.getUploadUnit()));
				builder.append("TIME", getDisplayValue(customAllowedUsage.getTime(), customAllowedUsage.getTimeUnit()));
				builder.decrementIndentation();
			}

		} else {

			builder.appendValue("No usage configured");
		}
	}
}
