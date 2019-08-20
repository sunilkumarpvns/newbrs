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

/**
 * This event is created for Daily and Weekly quota limit processing
 */
public class QuotaLimitEvent extends NotificationEvent implements Comparable<QuotaLimitEvent> {

    private static final long serialVersionUID = 1L;
    private static final String MODULE = "QUOTA-NTF-EVENT";
    private final UsageType usageType;
    private final AggregationKey aggregationKey;
    private final long serviceIdentifier;
    private final long rgIdentifier;
    private final String quotaProfileId;
    private final long limit;
    private final int level;
    private BalanceKey balanceKey;
    private String key;

    public QuotaLimitEvent(UsageType usageType,
                           AggregationKey aggregationKey,
                           long serviceIdentifier,
                           long rgIdentifier,
                           String quotaProfileId,
                           long limit, int level, Template emailTemplate, Template smsTemplate) {

        super(EventType.QUOTA_THRESHOLD_EVENT, emailTemplate, smsTemplate);
        this.usageType = usageType;
        this.aggregationKey = aggregationKey;
        this.serviceIdentifier = serviceIdentifier;
        this.rgIdentifier = rgIdentifier;
        this.quotaProfileId = quotaProfileId;
        this.limit = limit;
        this.level = level;

        this.balanceKey = BalanceKey.fromUsageType(usageType, aggregationKey);
        this.key = quotaProfileId + serviceIdentifier + aggregationKey.getVal() + level  + usageType;
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
                    +", Limit: " + limit);
        }

        if (balanceKey.getBalance(previousBalance) >= limit) {
            //This Event already satisfied in previous request so returning false
            return false;
        }
        return balanceKey.getBalance(currentBalance) >= limit;

    }

    public boolean isEligible(Map<NonMonetoryBalance, NonMonetoryBalance> currentToPreviousBalances) {

        NonMonetoryBalance maxCurrentBalance = null;
        for (Entry<NonMonetoryBalance, NonMonetoryBalance> subscriberUsageEntry : currentToPreviousBalances.entrySet()) {

            NonMonetoryBalance currentBalance = subscriberUsageEntry.getKey();

            if (maxCurrentBalance == null) {
                maxCurrentBalance = currentBalance;
            } else if (balanceKey.getBalance(currentBalance) > balanceKey.getBalance(maxCurrentBalance)) {
                maxCurrentBalance = currentBalance;
            } else if (balanceKey.getBalance(currentBalance) == balanceKey.getBalance(maxCurrentBalance)) {
                if (balanceKey.getBalance(currentToPreviousBalances.get(currentBalance)) > balanceKey.getBalance(currentToPreviousBalances.get(maxCurrentBalance))) {
                    maxCurrentBalance = currentBalance;
                }
            }
        }

        return isEligible(currentToPreviousBalances.get(maxCurrentBalance), maxCurrentBalance);
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

    public long getLimit() {
        return limit;
    }

    @Override
    public int compareTo(QuotaLimitEvent o) {
        long result = o.limit - this.limit;

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
        QuotaLimitEvent that = (QuotaLimitEvent) o;
        return limit == that.limit;
    }

    @Override
    public int hashCode() {
        return (int) (limit ^ (limit >>> 32));
    }

    public long getServiceIdentifier() {
        return serviceIdentifier;
    }

    private enum BalanceKey {

        DAILY_VOLUME("Daily-Volume") {
            @Override
            long getBalance(NonMonetoryBalance balance) {
                return balance.getDailyVolume();
            }
        },
        DAILY_TIME("Daily-Time") {
            @Override
            long getBalance(NonMonetoryBalance balance) {
                return balance.getDailyTime();
            }
        },
        WEEKLY_VOLUME("Weekly-Volume") {
            @Override
            long getBalance(NonMonetoryBalance balance) {
                return balance.getWeeklyVolume();
            }
        },
        WEEKLY_TIME("Weekly-Time") {
            @Override
            long getBalance(NonMonetoryBalance balance) {
                return balance.getWeeklyTime();
            }
        };

        public final String displayName;

        BalanceKey(String displayName) {
            this.displayName = displayName;
        }

        abstract long getBalance(NonMonetoryBalance balance);

        public static BalanceKey fromUsageType(UsageType usageType, AggregationKey aggregationKey) {

            if (AggregationKey.DAILY == aggregationKey) {
                if (usageType == UsageType.VOLUME) {
                    return BalanceKey.DAILY_VOLUME;
                } else if (usageType == UsageType.TIME) {
                    return BalanceKey.DAILY_TIME;
                }
            } else if (AggregationKey.WEEKLY == aggregationKey) {
                if (usageType == UsageType.VOLUME) {
                    return BalanceKey.WEEKLY_VOLUME;
                } else if (usageType == UsageType.TIME) {
                    return BalanceKey.WEEKLY_TIME;
                }
            }

            return null;
        }
    }

    public String getKey() {
        return key;
    }
}
