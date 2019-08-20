package com.elitecore.netvertex.service.pcrf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage;
import com.elitecore.corenetvertex.spr.ServiceUsage;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.service.notification.UsageNotificationScheme;

class UsageNotificationProcessor extends NotificationProcessor {
    private static final String MODULE = "USAGE-NTF-PROC";
    private final NetVertexServerContext serverContext;

    public UsageNotificationProcessor(NetVertexServerContext serverContext) {
        super(serverContext);
        this.serverContext = serverContext;
    }

    @Override
    public boolean isEligibleFurtherProcessing(PCRFRequest request, PCRFResponse response) {
        String subscriberIdentity = request.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
        ServiceUsage currentUsages = response.getCurrentUsage();
        if (currentUsages == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Skipping usage notification process for subscriber " + subscriberIdentity
                        + ". Reason: Current usage not found");
            }
            return false;
        }
        return true;
    }

    @Override
    public void processBasePackage(PCRFRequest request, PCRFResponse response, BasePackage basePackage, NotificationQueue notificationQueue) {

        UsageNotificationScheme usageNotificationScheme = (UsageNotificationScheme) basePackage.getUsageNotificationScheme();
        if (usageNotificationScheme == null) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping base package() usage notification processing. Reason: Notification Scheme not found");
            }
            return;
        }

        String subscriberIdentity = request.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
        ServiceUsage currentUsages = response.getCurrentUsage();
        ServiceUsage previousUsages = response.getPreviousUsage();
        queueEligibleNotifications(subscriberIdentity, basePackage, currentUsages, previousUsages, notificationQueue);
    }

    @Override
    public void processPromotionalPackages(PCRFRequest request, PCRFResponse response, NotificationQueue notificationQueue) {
        String subscriberIdentity = request.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
        ServiceUsage currentUsages = response.getCurrentUsage();
        ServiceUsage previousUsages = response.getPreviousUsage();
        Set<String> processedPackages = new HashSet<String>();

        SPRInfo sprInfo = request.getSPRInfo();
        List<String> sprGroupIds = sprInfo.getSPRGroupIds();

        for (int i = 0; i < sprInfo.getSPRGroupIds().size(); i++) {

            ArrayList<PromotionalPackage> promotionalPackagesOfGroup = serverContext.getPolicyRepository().getPromotionalPackagesOfGroup(sprGroupIds.get(i));

            if (Collectionz.isNullOrEmpty(promotionalPackagesOfGroup)) {
                continue;
            }

            for (PromotionalPackage promotionalPackage : promotionalPackagesOfGroup) {

                if (processedPackages.add(promotionalPackage.getId()) == false) {
                    continue;
                }

                if (promotionalPackage.getStatus() == PolicyStatus.FAILURE) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Skipping usage notification for subcriber " + subscriberIdentity + " on package : " + promotionalPackage.getName()
                                + ". Reason : Promotional package(" + promotionalPackage.getName() + ") status is: " + PolicyStatus.FAILURE + ", with fail reason(s): " + promotionalPackage.getFailReason());
                    }
                    continue;
                }

                UsageNotificationScheme usageNotificationScheme = (UsageNotificationScheme) promotionalPackage.getUsageNotificationScheme();
                if (usageNotificationScheme == null) {
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Skipping promotional package(" + promotionalPackage.getName() + ") usage notification processing." +
                                " Reason: Notification Scheme not found");
                    }
                    continue;
                }

                queueEligibleNotifications(subscriberIdentity, promotionalPackage, currentUsages, previousUsages, notificationQueue);
            }
        }
    }

    private void processSingleSubscription(PCRFRequest request, PCRFResponse response, Subscription addOnSubscription, NotificationQueue notificationQueue) {
        String subscriberIdentity = request.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
        ServiceUsage currentUsages = response.getCurrentUsage();
        if (currentUsages == null) {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "Skipping usage notification process for subcriber " + subscriberIdentity
                        + ". Reason: Current usage information not found");
            }
            return;
        }

        ServiceUsage previousUsages = response.getPreviousUsage();

        SubscriptionPackage addOn = serverContext.getPolicyRepository().getAddOnById(addOnSubscription.getPackageId());
        UsageNotificationScheme usageNotificationScheme = (UsageNotificationScheme)addOn.getUsageNotificationScheme();

        Map<String, SubscriberUsage> currentUsage = currentUsages.getPackageUsage(addOnSubscription.getId());
        Map<String, SubscriberUsage> previousUsage = previousUsages.getPackageUsage(addOnSubscription.getId());
        if (currentUsage != null && previousUsage != null) {
            usageNotificationScheme.queueEligibleEvents(previousUsage, currentUsage, notificationQueue);
        } else {
            if (LogManager.getLogger().isDebugLogLevel())
                LogManager.getLogger().debug(MODULE, "Skipping addOn usage notification process for subcriber "
                        + subscriberIdentity + ". Reason: Current or previous usage not found for subscription: " + addOnSubscription.getId()
                        + " for addOn: " + addOn.getName());
        }
    }

    private void processMultipleSubscriptions(PCRFRequest request,
                                             PCRFResponse response,
                                             Map<String, List<Subscription>> subscriptionsByPackageId,
                                             NotificationQueue notificationQueue) {
        if (subscriptionsByPackageId.isEmpty()) {
            return;
        }

        String subscriberIdentity = request.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);

        ServiceUsage currentUsages = response.getCurrentUsage();
        if (currentUsages == null) {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "Skipping usage notification process for subcriber " + subscriberIdentity
                        + ". Reason: Current usage information not found");
            }
            return;
        }

        ServiceUsage previousUsages = response.getPreviousUsage();

        for (Entry<String, List<Subscription>> multipleSubscriptionEntry : subscriptionsByPackageId.entrySet()) {
            AddOn addOn = serverContext.getPolicyRepository().getAddOnById(multipleSubscriptionEntry.getKey());
            UsageNotificationScheme usageNotificationScheme = (UsageNotificationScheme) addOn.getUsageNotificationScheme();

            List<Map<String, SubscriberUsage>> currentSubscriberUsagesList = new ArrayList<>();
            List<Map<String, SubscriberUsage>> previousSubscriberUsagesList = new ArrayList<>();
            for (Subscription addOnSubscription : multipleSubscriptionEntry.getValue()) {
                currentSubscriberUsagesList.add(currentUsages.getPackageUsage(addOnSubscription.getId()));
                if(previousUsages != null) {
                    previousSubscriberUsagesList.add(previousUsages.getPackageUsage(addOnSubscription.getId()));
                }
            }

            if (previousSubscriberUsagesList.isEmpty() == false && currentSubscriberUsagesList.isEmpty() == false) {
                usageNotificationScheme.queueEligibleEvents(previousSubscriberUsagesList, currentSubscriberUsagesList, notificationQueue);
            } else {
                if (LogManager.getLogger().isDebugLogLevel())
                    LogManager.getLogger().debug(MODULE, "Skipping addOn usage notification process for subcriber "
                            + subscriberIdentity + ". Reason: Current or previous usage not found for subscription: " + multipleSubscriptionEntry.getValue()
                            + " for addOn: " + addOn.getName());
            }
        }
    }

    @Override
    public void processSubscriptions(PCRFRequest request, PCRFResponse response, String subscriberIdentity, NotificationQueue notificationQueue) {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Applying notification for subscriptions");
        }

        if (response.getSubscriptions() == null) {
            if (LogManager.getLogger().isDebugLogLevel())
                LogManager.getLogger().debug(MODULE, "Skipping notification process for Subscriptions." +
                        " Reason: No subscriptions found for subscriber ID: " + subscriberIdentity);
            return;
        }

        Map<String, List<Subscription>> multipleSubscriptionAddOns = new HashMap<>();
        for (Subscription addOnSubscription : response.getSubscriptions().values()) {

            SubscriptionPackage addOn = serverContext.getPolicyRepository().getAddOnById(addOnSubscription.getPackageId());

            if (addOn == null) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Skipping usage notification process for subscriber ID: " + subscriberIdentity
                            + ". Reason: Either package is not AddOn Type or Package is not found for id:  " + addOnSubscription.getPackageId());
                }
                continue;
            }

            if (PolicyStatus.FAILURE == addOn.getStatus()) {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Skipping usage notification process for subscriber ID: " + subscriberIdentity
                            + ". Reason: Subscription package(name:" + addOn.getName() + ") has status FAILURE. Reason: " + addOn.getFailReason());
                }
                continue;
            }

            UsageNotificationScheme usageNotificationScheme = (UsageNotificationScheme) addOn.getUsageNotificationScheme();
            if (usageNotificationScheme == null) {
                if (getLogger().isDebugLogLevel())
                    getLogger().debug(MODULE, "Skipping addOn usage notification process for subscriber "
                            + subscriberIdentity + ". Reason: Notification scheme not found for addOn: " + addOn.getName());
                continue;
            }

            if (addOn.isMultipleSubscription()) {
                if (multipleSubscriptionAddOns.get(addOn.getId()) == null) {
                    List<Subscription> addOnSubscriptions = new ArrayList<Subscription>();
                    addOnSubscriptions.add(addOnSubscription);
                    multipleSubscriptionAddOns.put(addOn.getId(), addOnSubscriptions);
                } else {
                    multipleSubscriptionAddOns.get(addOn.getId()).add(addOnSubscription);
                }

            } else {
                processSingleSubscription(request, response, addOnSubscription, notificationQueue);
            }
        }

        processMultipleSubscriptions(request, response, multipleSubscriptionAddOns, notificationQueue);

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Notification processing for subscriptions completed");
        }
    }

    private void queueEligibleNotifications(String subscriberIdentity, Package pakage, ServiceUsage currentUsages,
                                            ServiceUsage previousUsages, NotificationQueue notificationQueue) {

        UsageNotificationScheme notificationScheme = (UsageNotificationScheme) pakage.getUsageNotificationScheme();
        Map<String, SubscriberUsage> previousBasePackageUsage = null;

        if (previousUsages != null) {
            previousBasePackageUsage = previousUsages.getPackageUsage(pakage.getId());
        }

        Map<String, SubscriberUsage> currentPackageUsage = currentUsages.getPackageUsage(pakage.getId());

        if (currentPackageUsage != null) {
            notificationScheme.queueEligibleEvents(previousBasePackageUsage, currentPackageUsage, notificationQueue);
        } else {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping package(" + pakage.getName() + ") usage notification process for subscriber "
                        + subscriberIdentity + ". Reason: current usage not found for package: " + pakage.getName());
            }
        }
    }
}
