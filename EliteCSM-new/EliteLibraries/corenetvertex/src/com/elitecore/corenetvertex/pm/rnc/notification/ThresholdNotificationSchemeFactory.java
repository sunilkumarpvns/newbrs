package com.elitecore.corenetvertex.pm.rnc.notification;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.RateCardType;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.pd.ratecard.NonMonetaryRateCardData;
import com.elitecore.corenetvertex.pd.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.rncpackage.notification.RncNotificationData;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.pm.rnc.RnCFactory;
import com.elitecore.corenetvertex.service.notification.Template;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class ThresholdNotificationSchemeFactory {

	private static final String MODULE = "THRESHOLD-NOTIF-SCHEME-FCTRY";
	private RnCFactory rncFactory;
	public ThresholdNotificationSchemeFactory(RnCFactory rncFactory){
		this.rncFactory = rncFactory;
	}

	public ThresholdNotificationScheme createThresholdNotificationScheme(List<RncNotificationData> rncNotifications,
																		 ChargingType chargingType,
																		 List<String> rncPackagePartialFailReasons) {

		if (Collectionz.isNullOrEmpty(rncNotifications)) {
			return null;
		}

		ArrayList<ThresholdEvent> thresholdEvents = new ArrayList<>();

		for (RncNotificationData rncNotificationData : rncNotifications) {
			List<String> notificationPartialFailReasons = new ArrayList<>();
			ThresholdEvent thresholdEvent = createThresholdNotificationEvents(rncNotificationData, chargingType
					, notificationPartialFailReasons);

			if (notificationPartialFailReasons.isEmpty() == false) {
				rncPackagePartialFailReasons.add("Threshold Notification event(" + rncNotificationData.getId() + ") parsing partially fail. Cause by:"
						+ format(notificationPartialFailReasons));
			} else if (thresholdEvent != null) {
				thresholdEvents.add(thresholdEvent);
			}

		}

		if (Collectionz.isNullOrEmpty(thresholdEvents)) {
			return null;
		}


		LinkedHashMap<String, TreeSet<ThresholdEvent>> rateCardIdToThresholdEvent = new LinkedHashMap<>();
		for (ThresholdEvent thresholdEvent : thresholdEvents) {
			String key = thresholdEvent.getRateCardId();
			TreeSet<ThresholdEvent> events = rateCardIdToThresholdEvent.computeIfAbsent(key, event -> new TreeSet<>());
			events.add(thresholdEvent);
		}

		List<ThresholdEvent> thresholdEventList = new ArrayList<>(rateCardIdToThresholdEvent.size());
		for (TreeSet<ThresholdEvent> events : rateCardIdToThresholdEvent.values()) {
			thresholdEventList.addAll(events);
		}

		return rncFactory.createThresholdNotificationScheme(thresholdEventList);
	}

	private static ThresholdEvent createThresholdNotificationEvents(RncNotificationData rncNotificationData, ChargingType chargingType,
																	List<String> notificationPartialFailReasons) {

		String rncPackageName = rncNotificationData.getRncPackageData().getName();

		long threshold = getThreshold(rncNotificationData, chargingType, notificationPartialFailReasons);

		Template emailTemplate = null;
		Template smsTemplate = null;

		NotificationTemplateData emailTemplateData = rncNotificationData.getEmailTemplateData();
		if (emailTemplateData != null) {
			emailTemplate = new Template(emailTemplateData.getId(), emailTemplateData.getName(),
					emailTemplateData.getSubject(), emailTemplateData.getTemplateData());

		} else {
			getLogger().debug(MODULE, "Email Template not configured for threshold(" + rncNotificationData.getThreshold() + ") in rnc package: " + rncPackageName);
		}

		NotificationTemplateData smsTemplateData = rncNotificationData.getSmsTemplateData();
		if (smsTemplateData != null) {
			smsTemplate = new Template(smsTemplateData.getId(), smsTemplateData.getName(), smsTemplateData.getSubject(),
					smsTemplateData.getTemplateData());

		} else {
			getLogger().debug(MODULE, "SMS Template not configured for threshold(" + rncNotificationData.getThreshold() + ") in rnc package: " + rncPackageName);
		}

		if (emailTemplateData == null && smsTemplateData == null) {
			String message = "No notification will be generated for threshold(" + rncNotificationData.getThreshold() + ") in rnc package: " + rncPackageName
					+ ". Reason: Email or SMS template is not configured";
			getLogger().warn(MODULE, message);
			notificationPartialFailReasons.add(message);
			return null;
		}

		if(notificationPartialFailReasons.isEmpty() == false) {
			getLogger().warn(MODULE, "Skipping creation of threshold notification event for rnc package: " + rncPackageName
					+ ". Reason: " + notificationPartialFailReasons.toString());

			return null;
		}
		return new ThresholdEvent(rncNotificationData.getRateCardData().getId(), threshold, emailTemplate, smsTemplate);

	}

	private static long getThreshold(RncNotificationData rncNotificationData, ChargingType chargingType, List<String> notificationPartialFailReasons){
		RateCardData rateCardData = rncNotificationData.getRateCardData();
		if(rateCardData.getType().equalsIgnoreCase(RateCardType.NON_MONETARY.name()) == false) {
			String message = "No notification will be generated for monetary rate card(" + rateCardData.getName() + ")"
					+ ". Reason: Notification can not be generated for monetary rate card";
			getLogger().warn(MODULE, message);
			notificationPartialFailReasons.add(message);
			return 0;
		}
		NonMonetaryRateCardData nonMonetaryRateCardData = rateCardData.getNonMonetaryRateCardData();
		Integer balanceThreshold = rncNotificationData.getThreshold();
		if (balanceThreshold == null || balanceThreshold <= 0) {
			String message = "For Non monetary rate card: " + rateCardData.getName()  +" Threshold is not defined";
			getLogger().warn(MODULE, message);
			notificationPartialFailReasons.add(message);
			return 0;
		}

		long configuredBalance = 0;
		if (ChargingType.EVENT == chargingType) {
			if (nonMonetaryRateCardData.getEvent() == null) {
				String message = "Free units are not configured in Non monetary rate card: " + rateCardData.getName();
				notificationPartialFailReasons.add(message);
				return 0;
			}
			configuredBalance = nonMonetaryRateCardData.getEvent();
		} else if (ChargingType.SESSION == chargingType) {
			if (nonMonetaryRateCardData.getTime() == null) {
				String message = "For Non monetary rate card: " + rateCardData.getName() + " balance is unlimited";
				notificationPartialFailReasons.add(message);
				return 0;
			}
			configuredBalance = getTimeInSeconds(nonMonetaryRateCardData.getTime(), nonMonetaryRateCardData.getTimeUom());
		}

		return convertThresholdPercentage(configuredBalance, balanceThreshold);
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

	private static Long convertThresholdPercentage(long time, Integer thresholdPercentage) {
		return time - ((time * thresholdPercentage) / 100);
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
