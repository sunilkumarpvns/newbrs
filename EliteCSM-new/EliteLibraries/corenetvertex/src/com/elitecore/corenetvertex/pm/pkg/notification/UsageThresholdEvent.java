package com.elitecore.corenetvertex.pm.pkg.notification;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.EventType;
import com.elitecore.corenetvertex.constants.MeteringType;
import com.elitecore.corenetvertex.service.notification.NotificationEvent;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.corenetvertex.spr.SubscriberUsage;

/**
 * 
 * @author Jay Trivedi
 * 
 */
public class UsageThresholdEvent extends NotificationEvent implements Comparable<UsageThresholdEvent> {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = "USG-NTF-EVENT";
	private final MeteringType meteringType;
	private final AggregationKey aggregationKey;
	private final String serviceId;
	private final String quotaProfileId;
	private final String quotaAndServiceUsageKey;
	private final String key;
	private final long threshold;
	private UsageKey usageKey;

	public UsageThresholdEvent(MeteringType meteringType,
			AggregationKey aggregationKey,
			String serviceId,
			String quotaProfileId,
			long threshold, Template emailTemplate, Template smsTemplate) {

		super(EventType.USAGE_THRESHOLD_EVENT, emailTemplate, smsTemplate);
		this.meteringType = meteringType;
		this.aggregationKey = aggregationKey;
		this.serviceId = serviceId;
		this.quotaProfileId = quotaProfileId;
		this.threshold = threshold;

		usageKey = UsageKey.fromAggregationKeyAndMeteringKey(getAggregationKey(), getMeteringType());
		quotaAndServiceUsageKey = quotaProfileId + CommonConstants.USAGE_KEY_SEPARATOR +  serviceId;
		this.key = quotaProfileId + serviceId + aggregationKey + meteringType;
	}

	public boolean isEligible(@Nullable SubscriberUsage previousUsage, SubscriberUsage currentUsage) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Usage Key: " + usageKey.displayName);
			if (previousUsage == null) {
				getLogger().debug(MODULE, "Previous Usage not found");
			} else {
				getLogger().debug(MODULE, "Previous Usage: " + usageKey.getUsage(previousUsage));
			}
			
			if (currentUsage == null) {
				getLogger().debug(MODULE, "Current Usage not found");
			} else {
				getLogger().debug(MODULE, "Current Usage: " + usageKey.getUsage(currentUsage));
			}
			getLogger().debug(MODULE, "Threshold: " + threshold);
		} 
		
		if (previousUsage != null && usageKey.getUsage(previousUsage) >= threshold) {
			return false;
		}
		return usageKey.getUsage(currentUsage) >= threshold;

	}

	public boolean isEligible(Map<SubscriberUsage, SubscriberUsage> previousToCurrentUsages) {

		SubscriberUsage leastUsage = null;
		for (Entry<SubscriberUsage, SubscriberUsage> subscriberUsageEntry : previousToCurrentUsages.entrySet()) {

			SubscriberUsage previousUsage = subscriberUsageEntry.getKey();
			if (leastUsage == null) {
				leastUsage = previousUsage;
			} else if (usageKey.getUsage(previousUsage) < usageKey.getUsage(leastUsage)) {
				leastUsage = previousUsage;
			} else if (usageKey.getUsage(previousUsage) == usageKey.getUsage(leastUsage)) {
				if (usageKey.getUsage(previousToCurrentUsages.get(previousUsage)) < usageKey.getUsage(previousToCurrentUsages.get(leastUsage))) {
					leastUsage = previousUsage;
				}
			}
		}
		
		return isEligible(leastUsage, previousToCurrentUsages.get(leastUsage));
	}
	
	public String getQuotaProfileId() {
		return quotaProfileId;
	}

	public MeteringType getMeteringType() {
		return meteringType;
	}

	public AggregationKey getAggregationKey() {
		return aggregationKey;
	}

	public long getThreshold() {
		return threshold;
	}

	@Override
	public int compareTo(UsageThresholdEvent o) {
		// in descending order
		long result = o.threshold - this.threshold;
		
		if (result > 0) {
			return 1;
		} else if (result < 0) {
			return -1;
		} 
		
		return 0;
	}

	public String getThresholdKey() {
		return key;
	}
	
	public String getUsageKey() {
		return quotaAndServiceUsageKey;
	}

	public String getServiceId() {
		return serviceId;
	}

	private enum UsageKey {

		BILLING_CYCLE_TOTAL("Billing-Cycle-Total") {
			@Override
			long getUsage(SubscriberUsage subscriberUsage) {
				return subscriberUsage.getBillingCycleTotal();
			}
		},
		BILLING_CYCLE_DOWNLOAD("Billing-Cycle-Download") {
			@Override
			long getUsage(SubscriberUsage subscriberUsage) {
				return subscriberUsage.getBillingCycleDownload();
			}
		},
		BILLING_CYCLE_UPLOAD("Billing-Cycle-Upload") {
			@Override
			long getUsage(SubscriberUsage subscriberUsage) {
				return subscriberUsage.getBillingCycleUpload();
			}
		},
		BILLING_CYCLE_TIME("Billing-Cycle-Time") {
			@Override
			long getUsage(SubscriberUsage subscriberUsage) {
				return subscriberUsage.getBillingCycleTime();
			}
		},
		DAILY_TOTAL("Daily-Total") {
			@Override
			long getUsage(SubscriberUsage subscriberUsage) {
				return subscriberUsage.getDailyTotal();
			}
		},
		DAILY_DOWNLOAD("Daily-Download") {
			@Override
			long getUsage(SubscriberUsage subscriberUsage) {
				return subscriberUsage.getDailyDownload();
			}
		},
		DAILY_UPLOAD("Daily-Upload") {
			@Override
			long getUsage(SubscriberUsage subscriberUsage) {
				return subscriberUsage.getDailyUpload();
			}
		},
		DAILY_TIME("Daily-Time") {
			@Override
			long getUsage(SubscriberUsage subscriberUsage) {
				return subscriberUsage.getDailyTime();
			}
		},
		WEEKLY_TOTAL("Weekly-Total") {
			@Override
			long getUsage(SubscriberUsage subscriberUsage) {
				return subscriberUsage.getWeeklyTotal();
			}
		},
		WEEKLY_DOWNLOAD("Weekly-Download") {
			@Override
			long getUsage(SubscriberUsage subscriberUsage) {
				return subscriberUsage.getWeeklyDownload();
			}
		},
		WEEKLY_UPLOAD("Weekly-Upload") {
			@Override
			long getUsage(SubscriberUsage subscriberUsage) {
				return subscriberUsage.getWeeklyUpload();
			}
		},
		WEEKLY_TIME("Weekly-Time") {
			@Override
			long getUsage(SubscriberUsage subscriberUsage) {
				return subscriberUsage.getWeeklyTime();
			}
		},
		CUSTOM_TOTAL("Custom-Total") {
			@Override
			long getUsage(SubscriberUsage subscriberUsage) {
				return subscriberUsage.getCustomTotal();
			}
		},
		CUSTOM_DOWNLOAD("Custom-Download") {
			@Override
			long getUsage(SubscriberUsage subscriberUsage) {
				return subscriberUsage.getCustomDownload();
			}
		},
		CUSTOM_UPLOAD("Custom-Upload") {
			@Override
			long getUsage(SubscriberUsage subscriberUsage) {
				return subscriberUsage.getCustomUpload();
			}
		},
		CUSTOM_TIME("Custom-Time") {
			@Override
			long getUsage(SubscriberUsage subscriberUsage) {
				return subscriberUsage.getCustomTime();
			}
		}
		;

		public final String displayName ;

		UsageKey (String displayName) {
			this.displayName = displayName;
		}
		
		static UsageKey fromAggregationKeyAndMeteringKey(AggregationKey aggregationKey, MeteringType meteringType) {

			switch (aggregationKey) {
				case BILLING_CYCLE:
					return getUsageKeyForBillingCycle(meteringType);
				case DAILY:
					return getUsageKeyForDaily(meteringType);
				case WEEKLY:
					return getUsageKeyForWeekly(meteringType);
				case CUSTOM:
					return getUsageKeyForCustom(meteringType);
			}
			return null;
			
		}

		private static UsageKey getUsageKeyForCustom(MeteringType meteringType) {
			switch (meteringType) {
                case VOLUME_TOTAL:
                    return CUSTOM_TOTAL;
                case VOLUME_DOWNLOAD:
                    return CUSTOM_DOWNLOAD;
                case VOLUME_UPLOAD:
                    return CUSTOM_UPLOAD;
                case TIME:
                    return CUSTOM_TIME;
            }
			return null;
		}

		private static UsageKey getUsageKeyForWeekly(MeteringType meteringType) {
			switch (meteringType) {
                case VOLUME_TOTAL:
                    return WEEKLY_TOTAL;
                case VOLUME_DOWNLOAD:
                    return WEEKLY_DOWNLOAD;
                case VOLUME_UPLOAD:
                    return WEEKLY_UPLOAD;
                case TIME:
                    return WEEKLY_TIME;
            }
			return null;
		}

		private static UsageKey getUsageKeyForDaily(MeteringType meteringType) {
			switch (meteringType) {
                case VOLUME_TOTAL:
                    return DAILY_TOTAL;
                case VOLUME_DOWNLOAD:
                    return DAILY_DOWNLOAD;
                case VOLUME_UPLOAD:
                    return DAILY_UPLOAD;
                case TIME:
                    return DAILY_TIME;
            }
			return null;
		}

		private static UsageKey getUsageKeyForBillingCycle(MeteringType meteringType) {
			switch (meteringType) {
                case VOLUME_TOTAL:
                    return BILLING_CYCLE_TOTAL;
                case VOLUME_DOWNLOAD:
                    return BILLING_CYCLE_DOWNLOAD;
                case VOLUME_UPLOAD:
                    return BILLING_CYCLE_UPLOAD;
                case TIME:
                    return BILLING_CYCLE_TIME;
            }
			return null;
		}


		abstract long getUsage(SubscriberUsage subscriberUsage) ;
	}

}
