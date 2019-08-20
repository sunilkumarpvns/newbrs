package com.elitecore.netvertex.rnc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javax.annotation.Nullable;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.offer.ProductOfferServicePkgRel;
import com.elitecore.corenetvertex.pm.service.Service;
import com.elitecore.corenetvertex.service.notification.NotificationEvent;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriptionRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.netvertex.service.notification.ThresholdNotificationScheme;
import com.elitecore.netvertex.service.pcrf.NotificationQueue;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class ThresholdNotificationProcessor  {
    private static final String MODULE = "THRESHOLD-NTF-PROC";
    private final NetVertexServerContext serverContext;

    public ThresholdNotificationProcessor(NetVertexServerContext serverContext) {
        this.serverContext = serverContext;
    }

    public void process(PCRFRequest request, PCRFResponse response) {

        String subscriberIdentity = request.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
        if (subscriberIdentity == null) {
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().warn(MODULE, "Skipping usage notification. Reason: Sub.SubscriberIdentity not found from PCRF Response");
            }
            return;
        }

        if (PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val.equals(response.getAttribute(PCRFKeyConstants.RESULT_CODE.val)) == false) {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Skipping threshold notification for subscriber " + subscriberIdentity
						+ ". Reason: Request is not processed successfully, Result Code: " + response.getAttribute(PCRFKeyConstants.RESULT_CODE.val));
			}

			return;
		}

        String productOfferName = request.getAttribute(PCRFKeyConstants.SUB_PRODUCT_OFFER.val);
        if (productOfferName == null) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Skipping threshold notification for subscriber " + subscriberIdentity
                        + ". Reason: Sub.ProductOffer is not found in PCRFResponse");
            }
            return;
        }

        ProductOffer productOffer = serverContext.getPolicyRepository().getProductOffer().byName(productOfferName);
        if(Objects.isNull(productOffer)){
            if (getLogger().isErrorLogLevel())
                getLogger().error(MODULE, "Rejecting request for subscriber ID: " + subscriberIdentity + ". Reason: product offer(" + productOfferName + ") not found in policy repository");
            return;
        }

        RnCPackage rncPackage = getRnCPackage(response, subscriberIdentity, productOffer);
		if (rncPackage == null) {
			return;
		}

        if (isEligibleFurtherProcessing(request, response) == false) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping Notification processing. Reason: Notification eligibility criteria not matched");
            }
            return;
        }
        NotificationQueue notificationQueue = new NotificationQueueImpl(response, serverContext);
        processRnCPackage(request, response,rncPackage, notificationQueue);
		processSubscriptions(request, response, subscriberIdentity, notificationQueue);
    }

	private void processSubscriptions(PCRFRequest request, PCRFResponse response, String subscriberIdentity, NotificationQueue notificationQueue) {
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Started subscription notification processing for subscriber ID: " + subscriberIdentity);
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

			if (SubscriptionType.RO_ADDON != subscription.getType()) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Skipping notification for subscriber Id: " + subscriberIdentity + ". Reason: " +
							"RnC notification not supported for package type: " + subscription.getType());
				}
				continue;
			}

			ProductOffer productOffer = serverContext.getPolicyRepository().getProductOffer().addOn().byId(subscription.getProductOfferId());
			if (productOffer == null) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Skipping notification for subscriber " + subscriberIdentity
							+ ". Reason: Product offer not found for Id : " + subscription.getProductOfferId());
				}
				continue;
			}

			RnCPackage addOnRnCPackage = getRnCPackage(response, subscriberIdentity, productOffer);

			if (addOnRnCPackage == null) {
				continue;
			}

			if (ChargingType.EVENT != addOnRnCPackage.getChargingType()) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Skipping notification for subscriber " + subscriberIdentity
							+ ". Reason: AddOn Notification is not supported for Charging Type: " + addOnRnCPackage.getChargingType());
				}
				continue;
			}

			ThresholdNotificationScheme notificationScheme = (ThresholdNotificationScheme) addOnRnCPackage.getThresholdNotificationScheme();

			if (notificationScheme == null) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Skipping notification process for subscriber "
							+ subscriberIdentity + ". Reason: Notification scheme not found for package: " + addOnRnCPackage.getName());
				}
				continue;
			}

			if (multipleSubscriptions.get(subscription.getPackageId()) == null) {
				List<Subscription> subscriptions = new ArrayList<>();
				subscriptions.add(subscription);
				multipleSubscriptions.put(subscription.getPackageId(), subscriptions);
			} else {
				multipleSubscriptions.get(subscription.getPackageId()).add(subscription);
			}
		}

		processNotification(request, response, multipleSubscriptions, notificationQueue);
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Completed subscription notification processing for subscriber ID: " + subscriberIdentity);
		}
	}

	@Nullable
	private RnCPackage getRnCPackage(PCRFResponse response, String subscriberIdentity, ProductOffer productOffer) {



		if (productOffer.getPolicyStatus() == PolicyStatus.FAILURE) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Skipping notification for subscriber " + subscriberIdentity
						+ ". Reason: Product offer (" + productOffer.getName() + ") status is: " + PolicyStatus.FAILURE
						+ ", with fail reason(s): " + productOffer.getFailReason());
			}
			return null;
		}

		List<ProductOfferServicePkgRel> productOfferServicePkgRelList = productOffer.getProductOfferServicePkgRelDataList();
		if(Collectionz.isNullOrEmpty(productOfferServicePkgRelList)){
			if (getLogger().isInfoLogLevel()){
				getLogger().info(MODULE, "Skipping notification for subscriber " + subscriberIdentity
						+ ". Reason: No RnC Package configured in product offer");
			}
			return null;
		}

		RnCPackage rnCPackage = null;

		for (ProductOfferServicePkgRel productOfferServicePkgRel : productOfferServicePkgRelList) {
			Service service = productOfferServicePkgRel.getServiceData();
			if (service.getId().equals(response.getAttribute(PCRFKeyConstants.CS_SERVICE.val))) {
				rnCPackage = (RnCPackage) productOfferServicePkgRel.getRncPackageData();
			}
		}

		if (rnCPackage == null) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Skipping notification for subscriber " + subscriberIdentity
						+ ". Reason: RnC package not found with Service: " + response.getAttribute(PCRFKeyConstants.CS_SERVICE.val));
			}
			return null;
		}

		if (rnCPackage.getPolicyStatus() == PolicyStatus.FAILURE) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Skipping notification for subscriber " + subscriberIdentity
						+ ". Reason: RnC Package(" + rnCPackage.getName() + ") status is: " + PolicyStatus.FAILURE + ", with fail reason(s): "
						+ rnCPackage.getFailReason());
			}
			return null;
		}

		return rnCPackage;
	}

	private void processNotification(PCRFRequest request, PCRFResponse response, Map<String,
			List<Subscription>> subscriptionsByPackageId, NotificationQueue notificationQueue) {

		if (subscriptionsByPackageId.isEmpty()) {
			return;
		}

		String subscriberIdentity = request.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);

		SubscriberRnCNonMonetaryBalance currentBalance = response.getCurrentRnCNonMonetaryBalance();
		SubscriberRnCNonMonetaryBalance previousBalance = response.getPreviousRnCNonMonetaryBalance();

		for (Entry<String, List<Subscription>> multipleSubscriptionEntry : subscriptionsByPackageId.entrySet()) {

			com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage addOn = serverContext.getPolicyRepository().getRnCPackage().byId(multipleSubscriptionEntry.getKey());

			if (isSingleSubscription(multipleSubscriptionEntry)) {
				processSingleSubscription(notificationQueue, subscriberIdentity, currentBalance, previousBalance, addOn, multipleSubscriptionEntry.getValue().get(0));
			} else {
				processMultipleSubscription(notificationQueue, subscriberIdentity, currentBalance, previousBalance, multipleSubscriptionEntry, addOn);
			}
		}
	}

	private void processMultipleSubscription(NotificationQueue notificationQueue, String subscriberIdentity, SubscriberRnCNonMonetaryBalance currentBalance,
											 SubscriberRnCNonMonetaryBalance previousBalance, Entry<String, List<Subscription>> multipleSubscriptionEntry,
											 com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage addOn) {
		List<SubscriptionRnCNonMonetaryBalance> currentSubscriptionsBalances = new ArrayList<>();
		List<SubscriptionRnCNonMonetaryBalance> previousSubscriptionsBalances = new ArrayList<>();

		for (Subscription subscription : multipleSubscriptionEntry.getValue()) {
			currentSubscriptionsBalances.add(currentBalance.getPackageBalance(subscription.getId()));
			previousSubscriptionsBalances.add(previousBalance.getPackageBalance(subscription.getId()));
		}

		ThresholdNotificationScheme notificationScheme = (ThresholdNotificationScheme) addOn.getThresholdNotificationScheme();
		if (previousSubscriptionsBalances.isEmpty() == false && currentSubscriptionsBalances.isEmpty() == false) {
			notificationScheme.queueEligibleEvents(previousSubscriptionsBalances, currentSubscriptionsBalances, notificationQueue);
		} else {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping rnc notification process for subscriber "
						+ subscriberIdentity + ". Reason: Current or previous quota not found for subscription: " + multipleSubscriptionEntry.getValue()
						+ " for package: " + addOn.getName());
			}
		}
	}

	private void processSingleSubscription(NotificationQueue notificationQueue, String subscriberIdentity, SubscriberRnCNonMonetaryBalance currentBalance,
										   SubscriberRnCNonMonetaryBalance previousBalance, com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage addOn, Subscription subscription) {
		SubscriptionRnCNonMonetaryBalance subscriptionCurrentBalance = currentBalance.getPackageBalance(subscription.getId());
		SubscriptionRnCNonMonetaryBalance subscriptionPreviousBalance = previousBalance.getPackageBalance(subscription.getId());

		ThresholdNotificationScheme notificationScheme = (ThresholdNotificationScheme) addOn.getThresholdNotificationScheme();
		if (subscriptionCurrentBalance != null && subscriptionPreviousBalance != null) {
			notificationScheme.queueEligibleEvents(subscriptionPreviousBalance, subscriptionCurrentBalance, notificationQueue);
		} else {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping rnc notification process for subscriber "
						+ subscriberIdentity + ". Reason: Current or previous balance not found for subscription: " + subscription.getId()
						+ " for package: " + addOn.getName());
			}
		}
	}

	private boolean isSingleSubscription(Entry<String, List<Subscription>> multipleSubscriptionEntry) {
		return multipleSubscriptionEntry.getValue().size() == 1;
	}

	public boolean isEligibleFurtherProcessing(PCRFRequest request, PCRFResponse response) {
        String subscriberIdentity = request.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);

		if (PCRFKeyValueConstants.REQUEST_TYPE_EVENT_REQUEST.val.equals(request.getAttribute(PCRFKeyConstants.REQUEST_TYPE.val))
			&& PCRFKeyValueConstants.REQUESTED_ACTION_DIRECT_DEBITING.val.equals(request.getAttribute(PCRFKeyConstants.REQUESTED_ACTION.val)) == false) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping notification for subscriber " + subscriberIdentity
						+ ". Reason: Notification processing is not required when requested action is " + request.getAttribute(PCRFKeyConstants.REQUESTED_ACTION.val));
			}
			return false;
		}

        SubscriberRnCNonMonetaryBalance currentUsages = response.getCurrentRnCNonMonetaryBalance();
        if (currentUsages == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Skipping threshold notification process for subscriber " + subscriberIdentity
                        + ". Reason: Current balance not found");
            }
            return false;
        }
        return true;
    }

    public void processRnCPackage(PCRFRequest request, PCRFResponse response, RnCPackage rncPackage, NotificationQueue notificationQueue) {
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Started rnc notification processing for subscriber ID: " + response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val));
		}

        ThresholdNotificationScheme thresholdNotificationScheme = (ThresholdNotificationScheme) rncPackage.getThresholdNotificationScheme();
        if (thresholdNotificationScheme == null) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping rnc package threshold notification processing. Reason: Threshold Notification Scheme not found");
            }
            return;
        }

        String subscriberIdentity = request.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
        SubscriberRnCNonMonetaryBalance currentUsages = response.getCurrentRnCNonMonetaryBalance();
        SubscriberRnCNonMonetaryBalance previousUsages = response.getPreviousRnCNonMonetaryBalance();
        queueEligibleNotifications(subscriberIdentity, rncPackage, currentUsages, previousUsages, notificationQueue);
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Completed rnc notification processing for subscriber ID: " + response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val));
		}
    }

    private void queueEligibleNotifications(String subscriberIdentity, RnCPackage rncPackage, SubscriberRnCNonMonetaryBalance currentBalance,
                                            SubscriberRnCNonMonetaryBalance previousBalance, NotificationQueue notificationQueue) {

        ThresholdNotificationScheme notificationScheme = (ThresholdNotificationScheme) rncPackage.getThresholdNotificationScheme();
        SubscriptionRnCNonMonetaryBalance previousRnCPackageBalance = null;

        if (previousBalance != null) {
            previousRnCPackageBalance = previousBalance.getPackageBalance(rncPackage.getId());
        }

        SubscriptionRnCNonMonetaryBalance currentRnCPackageBalance = currentBalance.getPackageBalance(rncPackage.getId());

        if (currentRnCPackageBalance != null) {
            notificationScheme.queueEligibleEvents(previousRnCPackageBalance, currentRnCPackageBalance, notificationQueue);
        } else {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping package(" + rncPackage.getName() + ") usage notification process for subscriber "
                        + subscriberIdentity + ". Reason: current usage not found for package: " + rncPackage.getName());
            }
        }
    }

    protected static class NotificationQueueImpl implements NotificationQueue {

        private final PCRFResponse response;
        private final NetVertexServerContext context;

        public NotificationQueueImpl(PCRFResponse pcrfResponse, NetVertexServerContext context) {
            super();
            this.response = pcrfResponse;
            this.context = context;
        }

        @Override
        public void add(NotificationEvent event) {
            context.sendNotification(event.getEmailTemplate(), event.getSMSTemplate(), response, null, event.getNotificationRecipient());
        }
    }
}
