package com.elitecore.corenetvertex.pm.pkg.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import javax.annotation.Nullable;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.MeteringType;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.pd.notification.TopUpNotificationData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.pkg.notification.QuotaNotificationData;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaLimitEvent;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaNotificationScheme;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaThresholdEvent;
import com.elitecore.corenetvertex.service.notification.NotificationEvent;
import com.elitecore.corenetvertex.service.notification.Template;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.pkg.factory.FactoryUtils.format;

public class QuotaNotificationSchemeFactory {
    private static final String MODULE = "QUOTA-NTF-FCTRY";
    public static final String EMAIL = "EMAIL";
    public static final String SMS = "SMS";
    private PackageFactory packageFactory;

    public QuotaNotificationSchemeFactory(PackageFactory packageFactory) {
        this.packageFactory = packageFactory;
    }

    public @Nullable QuotaNotificationScheme createTopUpQuotaNotificationScheme(List<TopUpNotificationData> topUpNotificationList,
                                                               QuotaProfile quotaProfile,
                                                               List<String> partialFailReasons) {
        if (Collectionz.isNullOrEmpty(topUpNotificationList) || quotaProfile == null) {
            return null;
        }

        ArrayList<QuotaThresholdEvent> quotaThresholdEvents = new ArrayList<QuotaThresholdEvent>();
        for (TopUpNotificationData notificationData : topUpNotificationList) {
            List<String> notificationPartialFailReasons = new ArrayList<String>();
            QuotaThresholdEvent quotaThresholdEvent = (QuotaThresholdEvent)createNotificationEvents(quotaProfile,
                    0,
                    CommonConstants.ALL_SERVICE_ID,
                    notificationData.getThreshold(),
                    notificationData.getEmailTemplateData(),
                    notificationData.getSmsTemplateData(),
                    AggregationKey.BILLING_CYCLE,
                    notificationPartialFailReasons);

            if (notificationPartialFailReasons.isEmpty() == false) {
                partialFailReasons.add("Notification event(" + notificationData.getId() + ") parsing partially fail. Cause by:"
                        + format(notificationPartialFailReasons));
            } else if (quotaThresholdEvent != null) {
                quotaThresholdEvents.add(quotaThresholdEvent);
            }
        }

        if (Collectionz.isNullOrEmpty(quotaThresholdEvents)) {
            return null;
        }

        Map<String, TreeSet<QuotaThresholdEvent>> thresholdEventsMap = new HashMap<>();
        for (QuotaThresholdEvent quotaThresholdEvent : quotaThresholdEvents) {
            /// TopUp has only quota with single rg,service,quota so used only quota profile id as key
            String key = quotaThresholdEvent.getQuotaProfileId();

            if (thresholdEventsMap.get(key) == null) {
                TreeSet<QuotaThresholdEvent> events = new TreeSet<>();
                thresholdEventsMap.put(key, events);
            }
            thresholdEventsMap.get(key).add(quotaThresholdEvent);
        }

        List<List<QuotaThresholdEvent>> thresholdEventsList = new ArrayList<>(thresholdEventsMap.size());
        for (TreeSet<QuotaThresholdEvent> events : thresholdEventsMap.values()) {
            thresholdEventsList.add(new ArrayList<>(events));
        }

        return packageFactory.createQuotaNotificationScheme(thresholdEventsList, Collectionz.newArrayList());

    }

    private Template createTemplate(NotificationTemplateData templateData, String notificationMode, long usageThreshold, String packageName) {
        if (templateData != null) {
            return new Template(templateData.getId(), templateData.getName(), templateData.getSubject(),
                    templateData.getTemplateData());
        }
        getLogger().debug(MODULE, notificationMode + " Template not configured for threshold(" + usageThreshold + ") in package: " + packageName);
        return null;
    }

    private String createFailMessage(QuotaProfile quotaProfile, Integer usageThreshold, String rncProfileDetailServiceIdOrName, String quotaUsageType, AggregationKey aggregationKey) {
        return "Skipping creation of notification event with threshold: " + usageThreshold + ", package: " + quotaProfile.getPkgName()
                + ", data service Id Or Name: " + rncProfileDetailServiceIdOrName
                + ", aggregation key: " + aggregationKey.getVal()
                + ", Metering Type: " + quotaUsageType;
    }

    /*

    Example: allowed balance is 100 and 80% ThreasoldPercentage configured.
    calulated quota thresold should be 20.

     */
    private long convertPercentageToQuotaThresold(long dataInBytes, int thresholdPercentage) {

        if (dataInBytes == CommonConstants.QUOTA_UNLIMITED || dataInBytes == CommonConstants.QUOTA_UNDEFINED || dataInBytes == 0) {
            return -1;
        }

        return dataInBytes - ((dataInBytes * thresholdPercentage) / 100);
    }

    private long convertPercentageToQuotaLimit(long dataInBytes, int thresholdPercentage) {

        if (dataInBytes == CommonConstants.QUOTA_UNLIMITED || dataInBytes == CommonConstants.QUOTA_UNDEFINED || dataInBytes == 0) {
            return -1;
        }

        return (dataInBytes * thresholdPercentage) / 100;
    }

    private MeteringType convertToMeteringType(UsageType quotaUsageType, VolumeUnitType unitType) {

        if (UsageType.VOLUME == quotaUsageType) {
            if (unitType == null) {
                return null;
            }

            switch (unitType) {
                case TOTAL:
                    return MeteringType.VOLUME_TOTAL;
                case UPLOAD:
                    return MeteringType.VOLUME_UPLOAD;
                case DOWNLOAD:
                    return MeteringType.VOLUME_DOWNLOAD;
            }

        } else if (UsageType.TIME == quotaUsageType) {
            return MeteringType.TIME;
        }
        return null;
    }


    public QuotaNotificationScheme createQuotaNotificationScheme(List<QuotaNotificationData> quotaNotificationDatas,
                                                                 List<QoSProfile> qosProfiles,
                                                                 List<String> partialFailReasons) {

        if (Collectionz.isNullOrEmpty(quotaNotificationDatas)) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping Quota notification creation. Reason: Notifications not configured");
            }
            return null;
        }

        ArrayList<QuotaThresholdEvent> quotaThresholdEvents = new ArrayList<QuotaThresholdEvent>();
        ArrayList<QuotaLimitEvent> quotaLimitEvents = new ArrayList<>();
        for (QuotaNotificationData notificationData : quotaNotificationDatas) {
            List<String> notificationPartialFailReasons = new ArrayList<String>();
            QuotaProfile quotaProfile = getQuotaProfile(qosProfiles, notificationData.getQuotaProfileName());

            if (quotaProfile == null) {
                continue;
            }

            NotificationEvent notificationEvent = createNotificationEvents(quotaProfile, notificationData.getFupLevel(),
                    notificationData.getDataServiceTypeData().getId(),
                    notificationData.getThreshold(),
                    notificationData.getEmailTemplateData(),
                    notificationData.getSmsTemplateData(),
                    notificationData.getAggregationKey(),
                    notificationPartialFailReasons);

            if (notificationPartialFailReasons.isEmpty() == false) {
                partialFailReasons.add("Notification event(" + notificationData.getId() + ") parsing partially fail. Cause by:"
                        + format(notificationPartialFailReasons));
                continue;
            }

            if (notificationEvent != null) {
                if (AggregationKey.BILLING_CYCLE == notificationData.getAggregationKey()) {
                    quotaThresholdEvents.add((QuotaThresholdEvent) notificationEvent);
                } else {
                    quotaLimitEvents.add((QuotaLimitEvent) notificationEvent);
                }
            }
        }

        if (Collectionz.isNullOrEmpty(quotaThresholdEvents) && Collectionz.isNullOrEmpty(quotaLimitEvents)) {
            return null;
        }

        List<List<QuotaThresholdEvent>> thresholdEventsList = getQuotaThresholds(quotaThresholdEvents);
        List<List<QuotaLimitEvent>> limitEventsList = getQuotaLimitEvents(quotaLimitEvents);
        return packageFactory.createQuotaNotificationScheme(thresholdEventsList, limitEventsList);
    }

    private List<List<QuotaLimitEvent>> getQuotaLimitEvents(ArrayList<QuotaLimitEvent> quotaLimitEvents) {
        Map<String, TreeSet<QuotaLimitEvent>> limitEventsMap = new HashMap<>();
        for (QuotaLimitEvent quotaLimitEvent : quotaLimitEvents) {
            String key = quotaLimitEvent.getKey();

            if (limitEventsMap.get(key) == null) {
                TreeSet<QuotaLimitEvent> events = new TreeSet<>();
                limitEventsMap.put(key, events);
            }
            limitEventsMap.get(key).add(quotaLimitEvent);
        }

        List<List<QuotaLimitEvent>> limitEventsList = new ArrayList<>(limitEventsMap.size());
        for (TreeSet<QuotaLimitEvent> events : limitEventsMap.values()) {
            limitEventsList.add(new ArrayList<>(events));
        }
        return limitEventsList;
    }

    private List<List<QuotaThresholdEvent>> getQuotaThresholds(ArrayList<QuotaThresholdEvent> quotaThresholdEvents) {
        Map<String, TreeSet<QuotaThresholdEvent>> thresholdEventsMap = new HashMap<>();
        for (QuotaThresholdEvent quotaThresholdEvent : quotaThresholdEvents) {
            /// TopUp has only quota with single rg,service,quota so used only quota profile id as key
            String key = quotaThresholdEvent.getKey();

            if (thresholdEventsMap.get(key) == null) {
                TreeSet<QuotaThresholdEvent> events = new TreeSet<>();
                thresholdEventsMap.put(key, events);
            }
            thresholdEventsMap.get(key).add(quotaThresholdEvent);
        }

        List<List<QuotaThresholdEvent>> thresholdEventsList = new ArrayList<>(thresholdEventsMap.size());
        for (TreeSet<QuotaThresholdEvent> events : thresholdEventsMap.values()) {
            thresholdEventsList.add(new ArrayList<>(events));
        }
        return thresholdEventsList;
    }

    private NotificationEvent createNotificationEvents(QuotaProfile quotaProfile, int fupLevel, String serviceId, Integer thresholdPercentage,
                                                       NotificationTemplateData emailTemplateData,
                                                       NotificationTemplateData smsTemplateData,
                                                       AggregationKey aggregationKey,
                                                       List<String> partialFailReasons) {


        if (quotaProfile.getAllLevelServiceWiseQuotaProfileDetails().size()-1 < fupLevel) {
            String message = createFailMessage(quotaProfile, thresholdPercentage, serviceId, "No quota usage found", aggregationKey)
                    + ". Reason: Fup level: " + fupLevel + " is not configured";
            getLogger().warn(MODULE, message);
            partialFailReasons.add(message);
            return null;
        }


        Map<String, QuotaProfileDetail> serviceToQuotaProfileDetailMap = quotaProfile.getAllLevelServiceWiseQuotaProfileDetails().get(fupLevel);
        RncProfileDetail rncProfileDetail = (RncProfileDetail) serviceToQuotaProfileDetailMap.get(serviceId);
        if(rncProfileDetail == null){
            String message = "Skipping creation of notification event with threshold: " + thresholdPercentage + ", package: " + quotaProfile.getPkgName()
                    + ", data service Id Or Name: " + serviceId
                    + ", Reason: RnC Profile Detail not found for service id: "+serviceId;
            getLogger().warn(MODULE,message);
            partialFailReasons.add(message);
            return null;
        }

        UsageType quotaUsageType = getUsageType(rncProfileDetail);
        if (quotaUsageType == null) {
            return null;
        }
        if(aggregationKey.equals(AggregationKey.CUSTOM)) {
            String message = createFailMessage(quotaProfile, thresholdPercentage, rncProfileDetail.getServiceName(), quotaUsageType.getVal(), aggregationKey)
                    + ". Reason: Aggregation key is custom";
            getLogger().warn(MODULE, message);
            partialFailReasons.add(message);
            return null;
        }

        if (thresholdPercentage == null || thresholdPercentage <= 0) {
            String message = createFailMessage(quotaProfile, thresholdPercentage, rncProfileDetail.getServiceName(), quotaUsageType.getVal(), aggregationKey)
                    + ". Reason: Threshold percentage should be greator than zero, Invalid threshold percentage: " + thresholdPercentage;

            getLogger().warn(MODULE, message);
            partialFailReasons.add(message);
            return null;
        }

        if (emailTemplateData == null && smsTemplateData == null) {
            String message = createFailMessage(quotaProfile, thresholdPercentage, rncProfileDetail.getServiceName(), quotaUsageType.getVal(), aggregationKey)
                    + ". Reason: Email or SMS template is not configured";
            getLogger().warn(MODULE, message);
            partialFailReasons.add(message);
            return null;
        }

        VolumeUnitType unitType = rncProfileDetail.getUnitType();
        MeteringType meteringType = convertToMeteringType(quotaUsageType, unitType);
        if (meteringType == null) {
            String message = createFailMessage(quotaProfile, thresholdPercentage, rncProfileDetail.getServiceName(), quotaUsageType.getVal(), aggregationKey)
                    + ". Reason: Invalid Volume Unit Type: " + unitType + " with Quota Usage Type: " + quotaUsageType;

            getLogger().warn(MODULE, message);
            partialFailReasons.add(message);
            return null;
        }
        Template emailTemplate = createTemplate(emailTemplateData, EMAIL, thresholdPercentage, quotaProfile.getPkgName());
        Template smsTemplate = createTemplate(smsTemplateData, SMS, thresholdPercentage, quotaProfile.getPkgName());

        UsageType usageType = null;
        long threshold = 0;
        long quotaBytes = 0;

        switch (meteringType) {
            case VOLUME_TOTAL:
                quotaBytes = rncProfileDetail.getAllowedUsage(aggregationKey).getTotalInBytes();
                usageType = UsageType.VOLUME;
                break;
            case VOLUME_DOWNLOAD:
                quotaBytes = rncProfileDetail.getAllowedUsage(aggregationKey).getDownloadInBytes();
                usageType = UsageType.VOLUME;
                break;
            case VOLUME_UPLOAD:
                quotaBytes = rncProfileDetail.getAllowedUsage(aggregationKey).getUploadInBytes();
                usageType = UsageType.VOLUME;
                break;
            case TIME:
                quotaBytes = rncProfileDetail.getAllowedUsage(aggregationKey).getTimeInSeconds();
                usageType = UsageType.TIME;
                break;
        }

        if (AggregationKey.BILLING_CYCLE == aggregationKey) {
            threshold = convertPercentageToQuotaThresold(quotaBytes, thresholdPercentage);
        } else {
            threshold = convertPercentageToQuotaLimit(quotaBytes, thresholdPercentage);
        }

        if (threshold < 0) {
            String message = createFailMessage(quotaProfile, thresholdPercentage, rncProfileDetail.getServiceName(), quotaUsageType.getVal(), aggregationKey)
                    + ". Reason: Allowed Usage is zero/undefined/unlimited";
            getLogger().warn(MODULE, message);
            partialFailReasons.add(message);
            return null;
        }

        if(AggregationKey.BILLING_CYCLE == aggregationKey) {
            return new QuotaThresholdEvent(
                    usageType,
                    aggregationKey,
                    rncProfileDetail.getDataServiceType().getServiceIdentifier(),
                    rncProfileDetail.getRatingGroup().getIdentifier(),
                    quotaProfile.getId(),
                    threshold,
                    rncProfileDetail.getFupLevel(),
                    emailTemplate, smsTemplate);
        } else {
            return new QuotaLimitEvent(
                    usageType,
                    aggregationKey,
                    rncProfileDetail.getDataServiceType().getServiceIdentifier(),
                    rncProfileDetail.getRatingGroup().getIdentifier(),
                    quotaProfile.getId(),
                    threshold,
                    rncProfileDetail.getFupLevel(),
                    emailTemplate, smsTemplate);
        }

    }

    private UsageType getUsageType(RncProfileDetail rncProfileDetail) {
        if (QuotaUsageType.HYBRID == rncProfileDetail.getQuotaUnit()) {
            return rncProfileDetail.getRateUnit();
        } else if (QuotaUsageType.VOLUME == rncProfileDetail.getQuotaUnit()) {
            return UsageType.VOLUME;
        } else if (QuotaUsageType.TIME== rncProfileDetail.getQuotaUnit()) {
            return UsageType.TIME;
        }

        return null;
    }

    private QuotaProfile getQuotaProfile(List<QoSProfile> qosProfiles, String quotaProfileName) {

        for (QoSProfile qoSProfile : qosProfiles) {
			if (qoSProfile.getQuotaProfile() == null) {
				continue;
			}

            if (qoSProfile.getQuotaProfile().getName().equalsIgnoreCase(quotaProfileName)) {
                return qoSProfile.getQuotaProfile();
            }
        }
        return null;
    }
}
