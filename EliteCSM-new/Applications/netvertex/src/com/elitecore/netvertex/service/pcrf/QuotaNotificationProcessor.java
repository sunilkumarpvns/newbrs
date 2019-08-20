package com.elitecore.netvertex.service.pcrf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriptionNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.pm.AddOn;
import com.elitecore.netvertex.service.notification.QuotaNotificationScheme;

public class QuotaNotificationProcessor extends NotificationProcessor {
    private static final String MODULE = "QUOTA-NTF-PROC";
    private final NetVertexServerContext serverContext;

    public QuotaNotificationProcessor(NetVertexServerContext serverContext) {
        super(serverContext);
        this.serverContext = serverContext;
    }

    @Override
    protected boolean isEligibleFurtherProcessing(PCRFRequest request, PCRFResponse response) {
        String subscriberIdentity = request.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
        SubscriberNonMonitoryBalance currentBalance = response.getCurrentNonMonetoryBalance();
        if (currentBalance == null) {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "Skipping quota notification process for subscriber " + subscriberIdentity
                        + ". Reason: Current balance is not found");
            }
            return false;
        }

        SubscriberNonMonitoryBalance previousBalance = response.getPreviousNonMonetoryBalance();
        if (previousBalance == null) {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "Skipping quota notification process for subscriber " + subscriberIdentity
                        + ". Reason: Previous balance is not found");
            }
            return false;
        }
        return true;
    }

    @Override
    protected void processBasePackage(PCRFRequest request, PCRFResponse response, BasePackage basePackage, NotificationQueue notificationQueue) {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Processing notification for Base package: " + basePackage.getName());
        }
        String subscriberIdentity = request.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
        QuotaNotificationScheme notificationScheme = (QuotaNotificationScheme) basePackage.getQuotaNotificationScheme();
        if (notificationScheme == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().debug(MODULE, "Skipping quota notification process for subscriber "
                        + subscriberIdentity + ". Reason: Notification scheme not found for base package: " + basePackage.getName());
            }

            return;
        }

        SubscriberNonMonitoryBalance currentBalance = response.getCurrentNonMonetoryBalance();
        SubscriberNonMonitoryBalance previousBalance = response.getPreviousNonMonetoryBalance();

        SubscriptionNonMonitoryBalance baseCurrentBalance = currentBalance.getPackageBalance(basePackage.getId());
        SubscriptionNonMonitoryBalance basePreviousBalance = previousBalance.getPackageBalance(basePackage.getId());

        if (baseCurrentBalance != null && basePreviousBalance != null) {
            notificationScheme.queueEligibleEvents(basePreviousBalance, baseCurrentBalance, notificationQueue);
        } else {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping quota notification process for subscriber "
                        + subscriberIdentity + ". Reason: Current or previous balance not found for base package: " + basePackage.getName());
            }
        }


        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "notification processing for base package: " + basePackage.getName() + " completed");
        }
    }

    @Override
    protected void processPromotionalPackages(PCRFRequest request, PCRFResponse response, NotificationQueue notificationQueue) {
        // PROMOTIONAL PACKAGE NOTIFICATION FOR RNC PACKAGE NOT IMPLEMENTED
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Skipping promotional package notification processing. Reason: Quota Notification is not supported in Promotional Package");
        }
    }

    /*
        Checks only for QuotaTopUp Subscriptions
     */
    private void processSingleSubscription(PCRFRequest request, PCRFResponse response, String packageName, QuotaNotificationScheme notificationScheme, Subscription subscription, NotificationQueue notificationQueue) {
        String subscriberIdentity = request.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
        SubscriberNonMonitoryBalance currentBalance = response.getCurrentNonMonetoryBalance();
        SubscriberNonMonitoryBalance previousBalance = response.getPreviousNonMonetoryBalance();

        SubscriptionNonMonitoryBalance subscriptionCurrentBalance = currentBalance.getPackageBalance(subscription.getId());
        SubscriptionNonMonitoryBalance subscriptionPreviousBalance = previousBalance.getPackageBalance(subscription.getId());

        if (subscriptionCurrentBalance != null && subscriptionPreviousBalance != null) {
            notificationScheme.queueEligibleEvents(subscriptionPreviousBalance, subscriptionCurrentBalance, notificationQueue);
        } else {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "Skipping quota notification process for subscriber "
                        + subscriberIdentity + ". Reason: Current or previous balance not found for subscription: " + subscription.getId()
                        + " for package: " + packageName);
            }
        }
    }

    private void processMultipleSubscriptions(PCRFRequest request, PCRFResponse response,
                                              Map<String, List<Subscription>> subscriptionsByPackageId, NotificationQueue notificationQueue) {
        if (subscriptionsByPackageId.isEmpty()) {
            return;
        }

        String subscriberIdentity = request.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);

        SubscriberNonMonitoryBalance currentBalance = response.getCurrentNonMonetoryBalance();
        SubscriberNonMonitoryBalance previousBalance = response.getPreviousNonMonetoryBalance();

        for (Entry<String, List<Subscription>> multipleSubscriptionEntry : subscriptionsByPackageId.entrySet()) {

            Subscription tmpSubscription = multipleSubscriptionEntry.getValue().get(0);
            QuotaNotificationScheme notificationScheme = null;
            String packageName = null;
            if (SubscriptionType.ADDON == tmpSubscription.getType()) {
                UserPackage addOn = serverContext.getPolicyRepository().getAddOnById(multipleSubscriptionEntry.getKey());
                packageName = addOn.getName();
                notificationScheme = (QuotaNotificationScheme) addOn.getQuotaNotificationScheme();
            } else if (SubscriptionType.TOP_UP == tmpSubscription.getType()) {
                QuotaTopUp quotaTopUp = serverContext.getPolicyRepository().getQuotaTopUpById(multipleSubscriptionEntry.getKey());
                notificationScheme = (QuotaNotificationScheme) quotaTopUp.getQuotaNotificationScheme();
            } else {
                continue;
            }

            List<SubscriptionNonMonitoryBalance> currentSubscriptionsBalances = new ArrayList<>();
            List<SubscriptionNonMonitoryBalance> previousSubscriptionsBalances = new ArrayList<>();
            for (Subscription subscription : multipleSubscriptionEntry.getValue()) {
                currentSubscriptionsBalances.add(currentBalance.getPackageBalance(subscription.getId()));
                previousSubscriptionsBalances.add(previousBalance.getPackageBalance(subscription.getId()));
            }

            if (previousSubscriptionsBalances.isEmpty() == false && currentSubscriptionsBalances.isEmpty() == false) {
                notificationScheme.queueEligibleEvents(previousSubscriptionsBalances, currentSubscriptionsBalances, notificationQueue);
            } else {
                if (LogManager.getLogger().isDebugLogLevel())
                    LogManager.getLogger().debug(MODULE, "Skipping quota notification process for subscriber "
                            + subscriberIdentity + ". Reason: Current or previous quota not found for subscription: " + multipleSubscriptionEntry.getValue()
                            + " for package: " + packageName);
            }
        }
    }

    @Override
    protected void processSubscriptions(PCRFRequest request, PCRFResponse response, String subscriberIdentity, NotificationQueue notificationQueue) {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Applying notification for subscriptions");
        }

        SPRInfo sprInfo = request.getSPRInfo();

        if (sprInfo == null) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping notification process for Subscriptions." +
                        " Reason: Subscriber profile not found from PCRF Request for subscriber ID: " + subscriberIdentity);
            }
            return;
        }

        LinkedHashMap<String, Subscription> activeSubscriptions = null;
        try {
            activeSubscriptions = sprInfo.getActiveSubscriptions(System.currentTimeMillis());
        } catch (OperationFailedException e) {
            getLogger().error(MODULE, "Skipping notification process for Subscriptions for subscriber ID:" + subscriberIdentity +
                    ". Reason: " + e.getMessage());
            if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
                getLogger().trace(MODULE, e);
            }
            return;
        }

        if (Maps.isNullOrEmpty(activeSubscriptions)) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping notification process for Subscriptions." +
                        " Reason: No subscriptions found for subscriber ID: " + subscriberIdentity);
            }
            return;
        }

        Map<String, List<Subscription>> multipleSubscriptions = new HashMap<>();
        for (Subscription subscription : activeSubscriptions.values()) {

            QuotaNotificationScheme notificationScheme = null;
            boolean isMultipleSubscription = true;
            String packageName = null;
            if (SubscriptionType.ADDON == subscription.getType()) {
                ProductOffer productOffer = serverContext.getPolicyRepository().getProductOffer().addOn().byId(subscription.getProductOfferId());
                if (productOffer == null) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Skipping quota notification for subscriber " + subscriberIdentity
                                + ". Reason: Product offer not found for Id : " + subscription.getProductOfferId());
                    }
                    continue;
                }

                if (productOffer.getPolicyStatus() == PolicyStatus.FAILURE) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Skipping quota notification for subcriber " + subscriberIdentity
                                + ". Reason: Product offer (" + productOffer.getName() + ") status is: " + PolicyStatus.FAILURE
                                + ", with fail reason(s): " + productOffer.getFailReason());
                    }
                    continue;
                }

                AddOn addOn = (AddOn)productOffer.getDataServicePkgData();

                if (addOn == null) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Skipping usage notification for subcriber " + subscriberIdentity
                                + ". Reason: AddOn package not found for packageName : " + productOffer.getName());
                    }
                    continue;
                }

                if (addOn.getStatus() == PolicyStatus.FAILURE) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Skipping usage notification for subcriber " + subscriberIdentity
                                + ". Reason: AddOn package(" + addOn.getName() + ") status is: " + PolicyStatus.FAILURE + ", with fail reason(s): " + addOn.getFailReason());
                    }
                    continue;
                }

                notificationScheme = (QuotaNotificationScheme) addOn.getQuotaNotificationScheme();
                packageName = addOn.getName();
            } else if (SubscriptionType.TOP_UP == subscription.getType()) {
                QuotaTopUp quotaTopUp = serverContext.getPolicyRepository().getQuotaTopUpById(subscription.getPackageId());

                if (quotaTopUp == null) {
                    getLogger().warn(MODULE, "Skipping quota notification process for subscriber ID: " + subscriberIdentity
                            + ". Reason: Package found with ID: " + subscription.getPackageId() + " is not QuotaTopUp");
                    continue;
                }

                if (PolicyStatus.FAILURE == quotaTopUp.getStatus()) {
                    getLogger().warn(MODULE, "Skipping usage notification process for subscriber ID: " + subscriberIdentity
                            + ". Reason: Subscription package(name:" + quotaTopUp.getName() + ") has status FAILURE. Reason: " + quotaTopUp.getFailReason());
                    continue;
                }
                notificationScheme = (QuotaNotificationScheme)quotaTopUp.getQuotaNotificationScheme();
                packageName = quotaTopUp.getName();
            } else {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Skipping quota notification for subscriber Id: " + subscriberIdentity + ". Reason: " +
                            "Quota notification not supported for package type: " + subscription.getType());
                }
                continue;
            }

            if (notificationScheme == null) {
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                    LogManager.getLogger().debug(MODULE, "Skipping quota notification process for subscriber "
                            + subscriberIdentity + ". Reason: Notification scheme not found for package: " + packageName);
                continue;
            }

            if (isMultipleSubscription) {
                if (multipleSubscriptions.get(subscription.getPackageId()) == null) {
                    List<Subscription> subscriptions = new ArrayList<>();
                    subscriptions.add(subscription);
                    multipleSubscriptions.put(subscription.getPackageId(), subscriptions);
                } else {
                    multipleSubscriptions.get(subscription.getPackageId()).add(subscription);
                }

            } else {
                processSingleSubscription(request, response, packageName, notificationScheme, subscription, notificationQueue);
            }
        }

        processMultipleSubscriptions(request, response, multipleSubscriptions, notificationQueue);
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Notification process completed for subscriptions");
        }
    }
}
