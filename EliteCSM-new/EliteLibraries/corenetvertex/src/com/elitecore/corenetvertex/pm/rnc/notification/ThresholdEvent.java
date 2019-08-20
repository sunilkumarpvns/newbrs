package com.elitecore.corenetvertex.pm.rnc.notification;

import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import com.elitecore.corenetvertex.constants.EventType;
import com.elitecore.corenetvertex.service.notification.NotificationEvent;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class  ThresholdEvent  extends NotificationEvent implements Comparable<ThresholdEvent> {

    private static final long serialVersionUID = 1L;

    private static final String MODULE = "THRESHOLD-NTF-EVENT";
    private final String rateCardId;
    private final long threshold;

    public ThresholdEvent(String rateCardId,
                               long threshold, Template emailTemplate, Template smsTemplate) {
        super(EventType.THRESHOLD_EVENT, emailTemplate, smsTemplate);
        this.rateCardId = rateCardId;
        this.threshold = threshold;
    }

    public boolean isEligible(@Nullable RnCNonMonetaryBalance previousRncNonMonetaryBalance, RnCNonMonetaryBalance currentRncNonMonetaryBalance) {
        if (getLogger().isDebugLogLevel()) {
            if (previousRncNonMonetaryBalance == null) {
                getLogger().debug(MODULE, "Previous Balance not found for non monetary rate card id: " + rateCardId);
            } else {
                getLogger().debug(MODULE, "Previous Balance: " + previousRncNonMonetaryBalance.getBillingCycleAvailable());
            }

            if (currentRncNonMonetaryBalance == null) {
                getLogger().debug(MODULE, "Current Balance not found for non monetary rate card id: " + rateCardId);
            } else {
                getLogger().debug(MODULE, "Current Balance: " + currentRncNonMonetaryBalance.getBillingCycleAvailable());
            }
            getLogger().debug(MODULE, "Threshold: " + threshold);
        }

        if (previousRncNonMonetaryBalance != null && previousRncNonMonetaryBalance.getBillingCycleAvailable() <= threshold) {
            return false;
        }
        if(currentRncNonMonetaryBalance != null) {
            return currentRncNonMonetaryBalance.getBillingCycleAvailable() <= threshold;
        }
        return false;

    }

	public boolean isEligible(Map<RnCNonMonetaryBalance, RnCNonMonetaryBalance> previousToCurrentBalances) {

		RnCNonMonetaryBalance lastUsedBalance = null;
		for (Entry<RnCNonMonetaryBalance, RnCNonMonetaryBalance> subscriberUsageEntry : previousToCurrentBalances.entrySet()) {
			if (subscriberUsageEntry.getKey().getBillingCycleAvailable() != subscriberUsageEntry.getValue().getBillingCycleAvailable()) {
				lastUsedBalance = subscriberUsageEntry.getKey();
			}
		}

		if (lastUsedBalance == null) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Balance not consumed from subscriptions");
			}
			return false;
		}

		return isEligible(lastUsedBalance, previousToCurrentBalances.get(lastUsedBalance));
	}

    public String getRateCardId() {
        return rateCardId;
    }

    public long getThreshold() {
        return threshold;
    }



    @Override
    public int compareTo(ThresholdEvent o) {
        return  Long.compare(this.threshold,o.threshold );
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

