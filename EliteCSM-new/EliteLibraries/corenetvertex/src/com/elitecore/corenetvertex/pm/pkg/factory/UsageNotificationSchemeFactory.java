package com.elitecore.corenetvertex.pm.pkg.factory;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.MeteringType;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.pkg.notification.UsageNotificationData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileDetailData;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageThresholdEvent;
import com.elitecore.corenetvertex.service.notification.Template;

public class UsageNotificationSchemeFactory {

	private static final String MODULE = "USAGE-NOTIF-SCHEME-FCTRY";
	private PackageFactory packageFactory;

	public UsageNotificationSchemeFactory(PackageFactory packageFactory) {
		
		this.packageFactory = packageFactory;
	}
	
	public UsageNotificationScheme createUsageNotificationScheme(PkgData pkgData, List<String> packagePartialFailReasons) {

		if (Collectionz.isNullOrEmpty(pkgData.getUsageNotificationDatas())) {
			return null;
		}

		ArrayList<UsageThresholdEvent> usageThresholdEvents = new ArrayList<UsageThresholdEvent>();

		for (UsageNotificationData usageNotificationData : pkgData.getUsageNotificationDatas()) {
			List<String> notificationPartialFailReasons = new ArrayList<String>();
			UsageThresholdEvent usageThresholdEvent = createUsageNotificationEvents(usageNotificationData
					, notificationPartialFailReasons);

			if (notificationPartialFailReasons.isEmpty() == false) {
				packagePartialFailReasons.add("Notification event(" + usageNotificationData.getId() + ") parsing partially fail. Cause by:"
						+ format(notificationPartialFailReasons));
			} else if (usageThresholdEvent != null) {
				usageThresholdEvents.add(usageThresholdEvent);
			}

		}

		if (Collectionz.isNullOrEmpty(usageThresholdEvents)) {
			return null;
		}
		Map<String, TreeSet<UsageThresholdEvent>> usageThresholdEventsMap = new HashMap<String, TreeSet<UsageThresholdEvent>>();
		for (UsageThresholdEvent usageThresholdEvent : usageThresholdEvents) {

			String key = usageThresholdEvent.getThresholdKey();

			if (usageThresholdEventsMap.get(key) == null) {
				TreeSet<UsageThresholdEvent> events = new TreeSet<UsageThresholdEvent>();
				events.add(usageThresholdEvent);
				usageThresholdEventsMap.put(key, events);
			} else {
				usageThresholdEventsMap.get(key).add(usageThresholdEvent);
			}

		}

		List<List<UsageThresholdEvent>> usageThresholdEventsList = new ArrayList<List<UsageThresholdEvent>>(usageThresholdEventsMap.size());

		for (TreeSet<UsageThresholdEvent> events : usageThresholdEventsMap.values()) {
			usageThresholdEventsList.add(new ArrayList<UsageThresholdEvent>(events));
		}

		return packageFactory.createUsageNotificationScheme(usageThresholdEventsList);
	}

	private static UsageThresholdEvent createUsageNotificationEvents(UsageNotificationData usageNotificationData, List<String> notificationPartialFailReasons) {

		String quotaProfileId = null;
		Long threshold = null;
		QuotaProfileData thresholdQuotaProfile = usageNotificationData.getQuotaProfile();
		Integer usageThreshold = usageNotificationData.getThreshold();
		BalanceLevel configuredBalanceLevel = thresholdQuotaProfile.getBalanceLevel();
		for (QuotaProfileDetailData quotaProfileDetailData : thresholdQuotaProfile.getQuotaProfileDetailDatas()) {

			/*
			 * we should threshold only configured balance level
			 */
			if ((configuredBalanceLevel.fupLevel == quotaProfileDetailData.getFupLevel()) == false) {
				continue;
			}

			if (quotaProfileDetailData.getDataServiceTypeData().getId().equals(usageNotificationData.getDataServiceTypeData().getId()) &&
					quotaProfileDetailData.getAggregationKey().equals(usageNotificationData.getAggregationKey().toString())) {

				MeteringType meteringType = usageNotificationData.getMeteringType();
				switch (meteringType) {
					case VOLUME_TOTAL:
						threshold = convertPercentageToUsage(getDataInBytes(quotaProfileDetailData.getTotal(), quotaProfileDetailData.getTotalUnit()), usageThreshold);
						break;
					case VOLUME_DOWNLOAD:
						threshold = convertPercentageToUsage(getDataInBytes(quotaProfileDetailData.getDownload(), quotaProfileDetailData.getDownloadUnit()), usageThreshold);
						break;
					case VOLUME_UPLOAD:
						threshold = convertPercentageToUsage(getDataInBytes(quotaProfileDetailData.getUpload(), quotaProfileDetailData.getUploadUnit()), usageThreshold);
						break;
					case TIME:
						threshold = convertPercentageToUsage(getTimeInSeconds(quotaProfileDetailData.getTime(), quotaProfileDetailData.getTimeUnit()), usageThreshold);
						break;
				}
				quotaProfileId = quotaProfileDetailData.getQuotaProfile().getId();

				break;
			}
		}
		String pkgName = usageNotificationData.getPkgData().getName();
		if (threshold == null) {
			String message = "Skipping creation of notification event with threshold: " + usageThreshold + ", package: " + pkgName
					+ ", quota profile: " + thresholdQuotaProfile.getName()
					+ ", data service type: " + usageNotificationData.getDataServiceTypeData().getName()
					+ ", aggregation key: " + usageNotificationData.getAggregationKey()
					+ ", Metering Type: " + usageNotificationData.getMeteringType()
					+ ". Reason: Usage is not defined";
			getLogger().warn(MODULE, message);
			notificationPartialFailReasons.add(message);
			return null;
		}
		if (threshold == 0) {
			String message = "Skipping creation of notification event with threshold: " + usageThreshold + ", package: " + pkgName
					+ ", quota profile: " + thresholdQuotaProfile.getName()
					+ ", data service type: " + usageNotificationData.getDataServiceTypeData().getName()
					+ ", aggregation key: " + usageNotificationData.getAggregationKey()
					+ ", Metering Type: " + usageNotificationData.getMeteringType()
					+ ". Reason: Usage is unlimited";
			getLogger().warn(MODULE, message);
			notificationPartialFailReasons.add(message);
			return null;
		}

		Template emailTemplate = null;
		Template smsTemplate = null;

		NotificationTemplateData emailTemplateData = usageNotificationData.getEmailTemplateData();
		if (emailTemplateData != null) {
			emailTemplate = new Template(emailTemplateData.getId(), emailTemplateData.getName(),
					emailTemplateData.getSubject(), emailTemplateData.getTemplateData());

		} else {
			getLogger().debug(MODULE, "Email Template not configured for threshold(" + usageThreshold + ") in package: " + pkgName);
		}

		NotificationTemplateData smsTemplateData = usageNotificationData.getSmsTemplateData();
		if (smsTemplateData != null) {
			smsTemplate = new Template(smsTemplateData.getId(), smsTemplateData.getName(), smsTemplateData.getSubject(),
					smsTemplateData.getTemplateData());

		} else {
			getLogger().debug(MODULE, "SMS Template not configured for threshold(" + usageThreshold + ") in package: " + pkgName);
		}

		if (emailTemplateData == null && smsTemplateData == null) {
			String message = "No notification will be generated for threshold(" + usageThreshold + ") in package: " + pkgName
					+ ". Reason: Email or SMS template is not configured";
			getLogger().warn(MODULE, message);
			notificationPartialFailReasons.add(message);
			return null;
		}
		return new UsageThresholdEvent(
				usageNotificationData.getMeteringType(),
				usageNotificationData.getAggregationKey(),
				usageNotificationData.getDataServiceTypeData().getId(), quotaProfileId,
				threshold, emailTemplate, smsTemplate);

	}
	
	private static String format(List<String> failReasons) {

		StringBuilder builder = new StringBuilder();

		for (int index = failReasons.size() - 1; index > 0; index--) {
			builder.append(failReasons.get(index));
			builder.append(CommonConstants.COMMA);
		}

		builder.append(failReasons.get(0));

		return builder.toString();
	}

	private static long getDataInBytes(Long dataQuantity, String dataUnit) {

		if (dataQuantity == null) {
			return 0;
		}

		DataUnit usageUnit = DataUnit.valueOf(dataUnit);

		if (usageUnit != null) {
			return usageUnit.toBytes(dataQuantity);
		}

		return dataQuantity;
	}


	private static Long convertPercentageToUsage(long dataInBytes, Integer thresholdPercentage) {
		return (dataInBytes * thresholdPercentage) / 100;
	}

	private static long getTimeInSeconds(Long time, String timeUnitStr) {

		if (time == null) {
			return 0;
		}

		TimeUnit timeUnit = TimeUnit.valueOf(timeUnitStr);

		if (timeUnit != null) {
			return timeUnit.toSeconds(time);
		}

		return time;
	}
}
