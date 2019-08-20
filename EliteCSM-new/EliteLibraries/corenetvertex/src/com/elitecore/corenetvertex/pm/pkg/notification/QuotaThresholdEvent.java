package com.elitecore.corenetvertex.pm.pkg.notification;

import java.util.Map;
import java.util.Map.Entry;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.EventType;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.service.notification.NotificationEvent;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class QuotaThresholdEvent extends NotificationEvent implements Comparable<QuotaThresholdEvent> {

    private static final long serialVersionUID = 1L;
    private static final String MODULE = "QUOTA-NTF-EVENT";
    private final UsageType usageType;
    private final AggregationKey aggregationKey;
    private final long serviceIdentifier;
    private final long rgIdentifier;
    private final String quotaProfileId;
    private final long threshold;
    private final int level;
    private BalanceKey balanceKey;
    private String key;

    public QuotaThresholdEvent(UsageType usageType,
                               AggregationKey aggregationKey,
                               long serviceIdentifier,
                               long rgIdentifier,
                               String quotaProfileId,
                               long threshold, int level, Template emailTemplate, Template smsTemplate) {

        super(EventType.QUOTA_THRESHOLD_EVENT, emailTemplate, smsTemplate);
        this.usageType = usageType;
        this.aggregationKey = aggregationKey;
        this.serviceIdentifier = serviceIdentifier;
        this.rgIdentifier = rgIdentifier;
        this.quotaProfileId = quotaProfileId;
        this.threshold = threshold;
        this.level = level;

        balanceKey = BalanceKey.fromUsageType(usageType);
        this.key = quotaProfileId + serviceIdentifier + aggregationKey.getVal() + level + usageType;
    }

    public int getLevel() {
        return level;
    }

    public long getRgIdentifier() {
        return rgIdentifier;
    }

    public boolean isEligible(NonMonetoryBalance previousBalance, NonMonetoryBalance currentBalance) {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Balance Key: " + balanceKey.displayName
                    + ", Previous Balance: " + balanceKey.getBalance(previousBalance)
                    + ", Current Balance: " + balanceKey.getBalance(currentBalance)
                    +", Threshold: " + threshold);
        }

        if (balanceKey.getBalance(previousBalance) <= threshold) {
            //This Event already satisfied in previous request so returning false
            return false;
        }
        return balanceKey.getBalance(currentBalance) <= threshold;

    }

    public boolean isEligible(Map<NonMonetoryBalance, NonMonetoryBalance> currentToPreviousBalances) {

        NonMonetoryBalance leastCurrentBalance = null;
        for (Entry<NonMonetoryBalance, NonMonetoryBalance> subscriberUsageEntry : currentToPreviousBalances.entrySet()) {

            NonMonetoryBalance currentBalance = subscriberUsageEntry.getKey();

            if (leastCurrentBalance == null) {
                leastCurrentBalance = currentBalance;
            } else if (balanceKey.getBalance(currentBalance) < balanceKey.getBalance(leastCurrentBalance)) {
                leastCurrentBalance = currentBalance;
            } else {
                if (balanceKey.getBalance(currentBalance) == balanceKey.getBalance(leastCurrentBalance) && balanceKey.getBalance(currentToPreviousBalances.get(currentBalance)) < balanceKey.getBalance(currentToPreviousBalances.get(leastCurrentBalance))) {
                    leastCurrentBalance = currentBalance;
                }
            }
        }

        return isEligible(currentToPreviousBalances.get(leastCurrentBalance), leastCurrentBalance);
    }

    public String getQuotaProfileId() {
        return quotaProfileId;
    }

    public UsageType getUsageType() {
        return usageType;
    }

    public AggregationKey getAggregationKey() {
        return aggregationKey;
    }

    public long getThreshold() {
        return threshold;
    }

    @Override
    public int compareTo(QuotaThresholdEvent o) {
        long result = this.threshold - o.threshold;

        if (result > 0) {
            return 1;
        } else if (result < 0) {
            return -1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        QuotaThresholdEvent that = (QuotaThresholdEvent) o;
        return threshold == that.threshold;
    }

    @Override
    public int hashCode() {
        return (int) (threshold ^ (threshold >>> 32));
    }

    public long getServiceIdentifier() {
        return serviceIdentifier;
    }

    private enum BalanceKey {

        BILLING_CYCLE_VOLUME("Billing-Cycle-Volume") {
            @Override
            long getBalance(NonMonetoryBalance balance) {
                return balance.getBillingCycleAvailableVolume();
            }
        },
        BILLING_CYCLE_TIME("Billing-Cycle-Time") {
            @Override
            long getBalance(NonMonetoryBalance balance) {
                return balance.getBillingCycleAvailableTime();
            }
        };

        public final String displayName;

        BalanceKey(String displayName) {
            this.displayName = displayName;
        }

        abstract long getBalance(NonMonetoryBalance balance);

        public static BalanceKey fromUsageType(UsageType usageType) {
            if (usageType == UsageType.VOLUME) {
                return BalanceKey.BILLING_CYCLE_VOLUME;
            } else if (usageType == UsageType.TIME) {
                return BalanceKey.BILLING_CYCLE_TIME;
            }
            return null;
        }
    }

    public String getKey() {
        return key;
    }
}
