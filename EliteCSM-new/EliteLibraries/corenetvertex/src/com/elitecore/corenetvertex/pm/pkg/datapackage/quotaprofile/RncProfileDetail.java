package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import org.apache.commons.lang.SystemUtils;

import javax.annotation.Nonnull;
import java.util.Map;

public class RncProfileDetail implements QuotaProfileDetail{
	private static final String QUOTA_UNLIMITED = "Unlimited";
	private static final String QUOTA_UNDEFINED = "-";
	private static final long serialVersionUID = 1L;
	private String volumePulseUnit;
	private String timePulseUnit;
	private final String quotaProfileId;
	private final String name;
	private final DataServiceType dataServiceType;
	private final int fupLevel;
    private final @Nonnull RatingGroup ratingGroup;
    private final Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage;
	private final long volumePulse;
	private final long timePulse;
	private final long volumePulseInBytes;
	private final long timePulseInSeconds;
	private final double rate;
	private final UsageType rateUnit;
	private final QuotaUsageType quotaUnit;
	private final VolumeUnitType unitType;
	private final boolean isHsqLevel;
	private String pccProfileName;
	private long volumeCarryForwardLimit;
	private long timeCarryForwardLimit;
	private boolean carryForward;
	private String revenueDetail;

	public RncProfileDetail(String name,
							DataServiceType dataServiceType,
							int fupLevel,
							RatingGroup ratingGroup,
							Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage,
							long volumePulse,
							long timePulse,
							long volumePulseInBytes,
							long timePulseInSeconds,
							String volumePulseUnit,
							String timePulseUnit,
							double rate,
							UsageType usageType,
							QuotaUsageType quotaType,
							VolumeUnitType unitType,
							String quotaProfileId,
							boolean isHsqLevel, String pccProfileName,
							long volumeCarryForwardLimit,
							long timeCarryForwardLimit,
							String revenueDetail) {
		this.quotaProfileId = quotaProfileId;
		this.name = name;
		this.fupLevel = fupLevel;
		this.dataServiceType = dataServiceType;
        this.ratingGroup = ratingGroup;
        this.aggregationKeyToAllowedUsage = aggregationKeyToAllowedUsage;
		this.volumePulse = volumePulse;
		this.timePulse = timePulse;
		this.volumePulseInBytes = volumePulseInBytes;
		this.timePulseInSeconds = timePulseInSeconds;
		this.volumePulseUnit = volumePulseUnit;
		this.timePulseUnit = timePulseUnit;
		this.rate = rate;
		this.rateUnit = usageType;
		this.unitType = unitType;
		this.quotaUnit = quotaType;
		this.isHsqLevel = isHsqLevel;
		this.pccProfileName = pccProfileName;
		this.volumeCarryForwardLimit = volumeCarryForwardLimit;
		this.timeCarryForwardLimit = timeCarryForwardLimit;
		this.revenueDetail = revenueDetail;
	}

	public double getRate() {
		return rate;
	}

	public UsageType getRateUnit() {
		return rateUnit;
	}

	public QuotaUsageType getQuotaUnit() {
    	return quotaUnit;
	}

	public VolumeUnitType getUnitType() {
    	return unitType;
	}

	public long getVolumePulseInBytes() {
		return volumePulseInBytes;
	}

	public long getTimePulseInSeconds() {
		return timePulseInSeconds;
	}

	public long getVolumePulse() { return volumePulse; }

	public long getTimePulse() { return timePulse; }

	public String getVolumePulseUnit() { return volumePulseUnit; }

	public String getTimePulseUnit() { return timePulseUnit; }

	public String getUsageKey() {
		return quotaProfileId + CommonConstants.USAGE_KEY_SEPARATOR + dataServiceType.getDataServiceTypeID() + ratingGroup;
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

	public AllowedUsage getDailyAllowedUsage() {
		return aggregationKeyToAllowedUsage.get(AggregationKey.DAILY);
	}

	public AllowedUsage getWeeklyAllowedUsage() {
		return aggregationKeyToAllowedUsage.get(AggregationKey.WEEKLY);
	}

    public AllowedUsage getBillingCycleAllowedUsage() {
        return aggregationKeyToAllowedUsage.get(AggregationKey.BILLING_CYCLE);
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
		return isHsqLevel;
	}

	@Override
	public QuotaProfileType getQuotaProfileType() {
		return QuotaProfileType.RnC_BASED;
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
		return dataServiceType.getDataServiceTypeID();
	}

	public DataServiceType getDataServiceType() {
		return dataServiceType;
	}

    public String getRatingGroupId() {
        return ratingGroup.getRatingGroupId();
    }
    
    public RatingGroup getRatingGroup() {
    	return ratingGroup;
    }

	@Override
	public String getServiceName() {
		return dataServiceType.getName();
	}

	public AllowedUsage getAllowedUsage(AggregationKey aggregationKey) {
		switch (aggregationKey) {
            case BILLING_CYCLE:
                return getBillingCycleAllowedUsage();
			case DAILY:
				return getDailyAllowedUsage();
			case WEEKLY:
				return getWeeklyAllowedUsage();
			case CUSTOM:
				return null;
		}
		
		return null;
	}

	public String toString() {

		ToStringBuilder builder = new ToStringBuilder(this, QUOTA_PROFILE_DETAIL_TO_STRING_STYLE)
				.append("FUP Level", fupLevel)
				.append("Service Name", dataServiceType.getName())
				.append("Rate", rate)
				.append("Rating On", rateUnit)
				.append("Volume Pulse", volumePulse)
				.append("Time Pulse", timePulse);

		if (carryForward) {
			builder.append("Volume Carry Forward Limit", getDisplayValue(volumeCarryForwardLimit, DataUnit.MB))
					.append("Time Carry Forward Limit", getTimeUnitDisplayValue(timeCarryForwardLimit, TimeUnit.MINUTE));
		}

		builder.append(SystemUtils.LINE_SEPARATOR);


		if (Maps.isNullOrEmpty(aggregationKeyToAllowedUsage) == false) {

			AllowedUsage billingCycleAllowedUsage = getBillingCycleAllowedUsage();
			if (billingCycleAllowedUsage != null) {
				builder.append("" + AggregationKey.BILLING_CYCLE);

				builder.append("TOTAL : " + getDisplayValue(billingCycleAllowedUsage.getTotal(), billingCycleAllowedUsage.getTotalUnit()));
				builder.append("DOWNLOAD : " + getDisplayValue(billingCycleAllowedUsage.getDownload(), billingCycleAllowedUsage.getDownloadUnit()));
				builder.append("UPLOAD : " + getDisplayValue(billingCycleAllowedUsage.getUpload(), billingCycleAllowedUsage.getUploadUnit()));
			}

			builder.append(SystemUtils.LINE_SEPARATOR);

			AllowedUsage dailyAllowedUsage = getDailyAllowedUsage();
			if (dailyAllowedUsage != null) {
				builder.append("" + AggregationKey.DAILY);
				builder.append("TOTAL : " + getDisplayValue(dailyAllowedUsage.getTotal(), dailyAllowedUsage.getTotalUnit()));
				builder.append("DOWNLOAD : " + getDisplayValue(dailyAllowedUsage.getDownload(), dailyAllowedUsage.getDownloadUnit()));
				builder.append("UPLOAD : " + getDisplayValue(dailyAllowedUsage.getUpload(), dailyAllowedUsage.getUploadUnit()));
			}

			builder.append(SystemUtils.LINE_SEPARATOR);

			AllowedUsage weeklyAllowedUsage = getWeeklyAllowedUsage();
			if (weeklyAllowedUsage != null) {
				builder.append("" + AggregationKey.WEEKLY);
				builder.append("TOTAL : " + getDisplayValue(weeklyAllowedUsage.getTotal(), weeklyAllowedUsage.getTotalUnit()));
				builder.append("DOWNLOAD : " + getDisplayValue(weeklyAllowedUsage.getDownload(), weeklyAllowedUsage.getDownloadUnit()));
				builder.append("UPLOAD : " + getDisplayValue(weeklyAllowedUsage.getUpload(), weeklyAllowedUsage.getUploadUnit()));
			}

			builder.append(SystemUtils.LINE_SEPARATOR);

		} else {

			builder.append("No usage configured");
		}

		return builder.toString();
	}

	private String getTimeUnitDisplayValue(long timeCarryForwardLimit, TimeUnit timeUnit) {
		if (timeCarryForwardLimit == CommonConstants.QUOTA_UNLIMITED) {
			return QUOTA_UNLIMITED;
		} else if (timeCarryForwardLimit == CommonConstants.QUOTA_UNDEFINED) {
			return QUOTA_UNDEFINED;
		}
		return timeCarryForwardLimit + " " + timeUnit;
	}

	private String getDisplayValue(long allowedUsage, DataUnit dataUnit) {

		if (allowedUsage == CommonConstants.QUOTA_UNLIMITED) {
			return QUOTA_UNLIMITED;
		} else if (allowedUsage == CommonConstants.QUOTA_UNDEFINED) {
			return QUOTA_UNDEFINED;
		}
		return allowedUsage + " " + dataUnit;
	}

	public boolean isRateConfigured(){
		return rate>0;
	}

	public String getPccProfileName() {
		return pccProfileName;
	}

	public long getVolumeCarryForwardLimit() {
		return isUnlimitedOrUndefined(volumeCarryForwardLimit)?volumeCarryForwardLimit:DataUnit.MB.toBytes(volumeCarryForwardLimit);
	}

	public long getTimeCarryForwardLimit() {
		return isUnlimitedOrUndefined(timeCarryForwardLimit)?timeCarryForwardLimit:TimeUnit.MINUTE.toSeconds(timeCarryForwardLimit);
	}

	private boolean isUnlimitedOrUndefined(long units) {
		return units == CommonConstants.UNLIMITED_QCF_QUOTA || units == CommonConstants.QUOTA_UNDEFINED;
	}

	public String getRevenueDetail() {
		return revenueDetail;
	}

	public void setCarryForward(Boolean carryForward) {
		this.carryForward = carryForward;
	}
}
