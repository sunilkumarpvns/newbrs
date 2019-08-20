package com.elitecore.netvertex.service.notification;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import com.elitecore.corenetvertex.pm.rnc.notification.ThresholdEvent;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriptionRnCNonMonetaryBalance;
import com.elitecore.netvertex.service.pcrf.NotificationQueue;


import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * @author Ishani Dave
 */

public class ThresholdNotificationScheme extends com.elitecore.corenetvertex.pm.rnc.notification.ThresholdNotificationScheme {

    private static final String MODULE = "THRESHOLD-NTF-SCHEME";

    public ThresholdNotificationScheme(List<ThresholdEvent> thresholdEvents) {
        super(thresholdEvents);
    }

    public void queueEligibleEvents(@Nullable SubscriptionRnCNonMonetaryBalance previousBalance,
                                    SubscriptionRnCNonMonetaryBalance currentBalance, NotificationQueue queue) {

        List<ThresholdEvent> thresholdEvents = getThresholdEvents();
        for (int index = 0; index < thresholdEvents.size(); index++) {

            ThresholdEvent thresholdEvent = thresholdEvents.get(index);
            RnCNonMonetaryBalance oldRncNonMonetaryBalance = null;
            if (previousBalance != null) {
                oldRncNonMonetaryBalance = previousBalance.getBalance(thresholdEvent.getRateCardId());
            }

            RnCNonMonetaryBalance newRncNonMonetaryBalance = currentBalance.getBalance(thresholdEvent.getRateCardId());

            if (thresholdEvent.isEligible(oldRncNonMonetaryBalance, newRncNonMonetaryBalance)) {
                if(getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Selected eligible event on threshold: " + thresholdEvent.getThreshold());
                }

                queue.add(thresholdEvent);
                break;
            }
        }
    }

	public void queueEligibleEvents(List<SubscriptionRnCNonMonetaryBalance> previousBalances,
									List<SubscriptionRnCNonMonetaryBalance> currentBalances,
									NotificationQueue notificationQueue) {

		for (int index = 0; index < getThresholdEvents().size(); index++) {

			ThresholdEvent event = getThresholdEvents().get(index);

			Map<RnCNonMonetaryBalance, RnCNonMonetaryBalance> subscriberBalances = createPreviousToCurrentBalance(previousBalances, currentBalances,
					event);

			if (event.isEligible(subscriberBalances)) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Selected eligible event on threshold: " + event.getThreshold());
				}
				notificationQueue.add(event);
				break;
			}
		}
	}

	private Map<RnCNonMonetaryBalance, RnCNonMonetaryBalance> createPreviousToCurrentBalance(List<SubscriptionRnCNonMonetaryBalance> previousBalances,
																							 List<SubscriptionRnCNonMonetaryBalance> currentBalances,
																							 ThresholdEvent event) {
		Map<RnCNonMonetaryBalance, RnCNonMonetaryBalance> subscriberBalance = new IdentityHashMap<>();

		for (int index = 0; index < currentBalances.size(); index++) {

			RnCNonMonetaryBalance previousBalance = previousBalances.get(index).getBalance(event.getRateCardId());
			RnCNonMonetaryBalance currentBalance = currentBalances.get(index).getBalance(event.getRateCardId());

			subscriberBalance.put(previousBalance, currentBalance);
		}
		return subscriberBalance;
	}
}
