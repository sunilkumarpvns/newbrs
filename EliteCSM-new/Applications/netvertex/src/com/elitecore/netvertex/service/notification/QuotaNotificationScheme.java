package com.elitecore.netvertex.service.notification;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaLimitEvent;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaThresholdEvent;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriptionNonMonitoryBalance;
import com.elitecore.netvertex.service.pcrf.NotificationQueue;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class QuotaNotificationScheme extends com.elitecore.corenetvertex.pm.pkg.notification.QuotaNotificationScheme {

	private static final String MODULE = "QUOTA-NTF-SCHEME";

	public QuotaNotificationScheme(List<List<QuotaThresholdEvent>> quotaThresholdEvents, List<List<QuotaLimitEvent>> quotaLimitEvents) {
		super(quotaThresholdEvents, quotaLimitEvents);
	}

	public void queueEligibleEvents(SubscriptionNonMonitoryBalance previousBalance,
                                    SubscriptionNonMonitoryBalance currentBalance, NotificationQueue queue) {

		processThresholdEvents(previousBalance, currentBalance, queue);
		processLimitEvents(previousBalance, currentBalance, queue);
	}

	private void processLimitEvents(SubscriptionNonMonitoryBalance previousBalance, SubscriptionNonMonitoryBalance currentBalance, NotificationQueue queue) {
		for (int index = 0; index < getQuotaLimitEvents().size(); index++) {
			List<QuotaLimitEvent> events = getQuotaLimitEvents().get(index);

			for (int childIndex = 0; childIndex < events.size(); childIndex++) {

				QuotaLimitEvent event = events.get(childIndex);
				NonMonetoryBalance oldBalance = previousBalance.getBalance(event.getQuotaProfileId(), event.getServiceIdentifier(), event.getRgIdentifier(), event.getLevel());

				NonMonetoryBalance newBalance = currentBalance.getBalance(event.getQuotaProfileId(), event.getServiceIdentifier(), event.getRgIdentifier(), event.getLevel());

				if (event.isEligible(oldBalance, newBalance)) {
					if (getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, "Selected eligible event on limit: " + event.getLimit());
					}
					queue.add(event);
					break;
				} else {
					if (getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, "Event not eligible on limit: " + event.getLimit());
					}
				}
			}
		}
	}

	private void processThresholdEvents(SubscriptionNonMonitoryBalance previousBalance, SubscriptionNonMonitoryBalance currentBalance, NotificationQueue queue) {
		for (int index = 0; index < getQuotaThresgoldEvents().size(); index++) {
			List<QuotaThresholdEvent> events = getQuotaThresgoldEvents().get(index);

			for (int childIndex = 0; childIndex < events.size(); childIndex++) {

				QuotaThresholdEvent event = events.get(childIndex);
				NonMonetoryBalance oldBalance = previousBalance.getBalance(event.getQuotaProfileId(), event.getServiceIdentifier(), event.getRgIdentifier(), event.getLevel());

                NonMonetoryBalance newBalance = currentBalance.getBalance(event.getQuotaProfileId(), event.getServiceIdentifier(), event.getRgIdentifier(), event.getLevel());

				if (event.isEligible(oldBalance, newBalance)) {
					if (getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, "Selected eligible event on threshold: " + event.getThreshold());
					}
					queue.add(event);
					break;
				} else {
					if (getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, "Event not eligible on threshold: " + event.getThreshold());
					}
				}
			}
		}
	}

	/**
     * This method is used to queue events in cases of multiple subscriptions of a package
     *
     * @param previousUsages
     * @param currentUsages
     * @param queue
     */
	public void queueEligibleEvents(List<SubscriptionNonMonitoryBalance> previousUsages,
			List<SubscriptionNonMonitoryBalance> currentUsages, NotificationQueue queue) {

		processThresholdEvents(previousUsages, currentUsages, queue);
		processLimitEvents(previousUsages, currentUsages, queue);
	}

	private void processLimitEvents(List<SubscriptionNonMonitoryBalance> previousUsages, List<SubscriptionNonMonitoryBalance> currentUsages, NotificationQueue queue) {
		for (int index = 0; index < getQuotaLimitEvents().size(); index++) {

			List<QuotaLimitEvent> events = getQuotaLimitEvents().get(index);

			for (int childIndex = 0; childIndex < events.size(); childIndex++) {

				QuotaLimitEvent event = events.get(childIndex);
				Map<NonMonetoryBalance, NonMonetoryBalance> subscriberBalances = createCurrentToPreviousBalance(previousUsages, currentUsages, event.getQuotaProfileId(), event.getServiceIdentifier(), event.getRgIdentifier(), event.getLevel());

				if (event.isEligible(subscriberBalances)) {
					getLogger().debug(MODULE, "Selected eligible event on limit: " +  event.getLimit());
					queue.add(event);
					break;
				}
			}
		}
	}

	private void processThresholdEvents(List<SubscriptionNonMonitoryBalance> previousUsages, List<SubscriptionNonMonitoryBalance> currentUsages, NotificationQueue queue) {
		for (int index = 0; index < getQuotaThresgoldEvents().size(); index++) {

			List<QuotaThresholdEvent> events = getQuotaThresgoldEvents().get(index);

			for (int childIndex = 0; childIndex < events.size(); childIndex++) {

				QuotaThresholdEvent event = events.get(childIndex);
				Map<NonMonetoryBalance, NonMonetoryBalance> subscriberBalances = createCurrentToPreviousBalance(previousUsages, currentUsages, event.getQuotaProfileId(), event.getServiceIdentifier(), event.getRgIdentifier(), event.getLevel());

				if (event.isEligible(subscriberBalances)) {
					if(getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, "Selected eligible event on threshold: " +  event.getThreshold());
					}
					queue.add(event);
					break;
				}
			}
		}
	}


	private Map<NonMonetoryBalance, NonMonetoryBalance> createCurrentToPreviousBalance(List<SubscriptionNonMonitoryBalance> previousBalances,
                                                                                       List<SubscriptionNonMonitoryBalance> currentBalances,
                                                                                       String quotaProfileId,
                                                                                       long serviceIdentifier,
                                                                                       long rgIdentifier,
                                                                                       int level) {
		Map<NonMonetoryBalance, NonMonetoryBalance> subscriberBalance = new IdentityHashMap<>();
		
		for (int index = 0; index < currentBalances.size(); index++){

            NonMonetoryBalance previousBalance = previousBalances.get(index).getBalance(quotaProfileId, serviceIdentifier, rgIdentifier, level);
            NonMonetoryBalance currentBalance = currentBalances.get(index).getBalance(quotaProfileId, serviceIdentifier, rgIdentifier, level);
			
			subscriberBalance.put(currentBalance, previousBalance);
		}
		return subscriberBalance;
	}

}
