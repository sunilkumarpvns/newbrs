package com.elitecore.corenetvertex.pm.pkg.notification;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriptionNonMonitoryBalance;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(HierarchicalContextRunner.class)
public class QuotaThresholdEventTest {

    private QuotaThresholdEvent event;
    private String quotaProfileId;
    private SubscriptionNonMonitoryBalance nonMonitoryBalance;
    private Random random = new Random();

    @Before
    public void setUp() {
        this.quotaProfileId = UUID.randomUUID().toString();
        this.event = createEvent(UsageType.VOLUME, 10);
        this.nonMonitoryBalance = new SubscriptionNonMonitoryBalance("packageId");
        NonMonetoryBalance nonMonetoryBalance = createNonMonetaryBalance();
        this.nonMonitoryBalance.addBalance(nonMonetoryBalance);
    }

    private NonMonetoryBalance createNonMonetaryBalance() {
        return new NonMonetoryBalance.NonMonetaryBalanceBuilder("id",
                CommonConstants.ALL_SERVICE_IDENTIFIER,
                "packageId",
                CommonConstants.DEFAULT_RATING_GROUP_IDENTIFIER,
                "subscriberIdentity",
                "subscriptionId",
                0,
                quotaProfileId,
                ResetBalanceStatus.NOT_RESET, null, null)
                .build();
    }

    private NonMonetoryBalance.NonMonetaryBalanceBuilder createNonMonetaryBalanceBuilder() {
        return new NonMonetoryBalance.NonMonetaryBalanceBuilder("id",
                CommonConstants.ALL_SERVICE_IDENTIFIER,
                "packageId",
                CommonConstants.DEFAULT_RATING_GROUP_IDENTIFIER,
                "subscriberIdentity",
                "subscriptionId",
                0,
                quotaProfileId,
                ResetBalanceStatus.NOT_RESET, null, null);
    }

    private QuotaThresholdEvent createEvent(UsageType usageType, int threshold) {
        return new QuotaThresholdEvent(usageType,
                AggregationKey.BILLING_CYCLE,
                CommonConstants.ALL_SERVICE_IDENTIFIER,
                CommonConstants.DEFAULT_RATING_GROUP_IDENTIFIER,
                quotaProfileId, threshold, 0, null, null);
    }


    public class isEligibleWithSingleBalanceShouldReturnFalseWhen {
        @Test
        public void previousBalanceIsLessThanThreshold() throws Exception {
            int threshold = nextInt(2, 100);
            QuotaThresholdEvent event = createEvent(UsageType.VOLUME, 10);
            NonMonetoryBalance previousBalance = createNonMonetaryBalanceBuilder()
                    .withBillingCycleVolumeBalance(nextLong(threshold, Integer.MAX_VALUE), nextLong(1, threshold - 1)).build();
            NonMonetoryBalance currentBalance = createNonMonetaryBalanceBuilder()
                    .withBillingCycleVolumeBalance(nextLong(threshold, Integer.MAX_VALUE), threshold).build();
            assertFalse(event.isEligible(previousBalance, currentBalance));
        }

        @Test
        public void previousBalanceIsEqualToThreshold() throws Exception {
            int threshold = random.nextInt(100);
            QuotaThresholdEvent event = createEvent(UsageType.VOLUME, 10);
            NonMonetoryBalance previousBalance = createNonMonetaryBalanceBuilder()
                    .withBillingCycleVolumeBalance(nextLong(threshold, Integer.MAX_VALUE), 0).build();
            NonMonetoryBalance currentBalance = createNonMonetaryBalanceBuilder()
                    .withBillingCycleVolumeBalance(nextLong(threshold, Integer.MAX_VALUE), threshold).build();
            assertFalse(event.isEligible(previousBalance, currentBalance));
        }

        @Test
        public void previousBalanceAndCurrentBalanceBalanceIsGreatorThanThreshold() throws Exception {
            int threshold = random.nextInt(100);
            QuotaThresholdEvent event = createEvent(UsageType.VOLUME, 10);
            NonMonetoryBalance previousBalance = createNonMonetaryBalanceBuilder()
                    .withBillingCycleVolumeBalance(nextLong(threshold, Integer.MAX_VALUE), nextLong(threshold+1, Integer.MAX_VALUE)).build();
            NonMonetoryBalance currentBalance = createNonMonetaryBalanceBuilder()
                    .withBillingCycleVolumeBalance(nextLong(threshold, Integer.MAX_VALUE), nextLong(threshold+1, Integer.MAX_VALUE)).build();
            assertFalse(event.isEligible(previousBalance, currentBalance));
        }
    }


    public class isEligibleWithSingleBalanceShouldReturnTrueWhen {
        @Test
        public void previousBalanceIsGreatorThanThresholdAndCurrentBalanceIsLessThanThreshold() throws Exception {
            int threshold =nextInt(2, 100);
            QuotaThresholdEvent event = createEvent(UsageType.VOLUME, threshold);
            NonMonetoryBalance previousBalance = createNonMonetaryBalanceBuilder()
                    .withBillingCycleVolumeBalance(nextLong(threshold, Integer.MAX_VALUE), nextLong(threshold+1, Integer.MAX_VALUE)).build();
            NonMonetoryBalance currentBalance = createNonMonetaryBalanceBuilder()
                    .withBillingCycleVolumeBalance(nextLong(threshold, Integer.MAX_VALUE), nextLong(1, threshold-1)).build();

            assertTrue(event.isEligible(previousBalance, currentBalance));
        }

        @Test
        public void previousBalanceIsGreatorThanThresholdAndCurrentBalanceIsEqualToThreshold() throws Exception {
            int threshold = random.nextInt(100);
            QuotaThresholdEvent event = createEvent(UsageType.VOLUME, threshold);
            NonMonetoryBalance previousBalance = createNonMonetaryBalanceBuilder()
                    .withBillingCycleVolumeBalance(nextLong(threshold, Integer.MAX_VALUE), nextLong(threshold+1, Integer.MAX_VALUE)).build();
            NonMonetoryBalance currentBalance = createNonMonetaryBalanceBuilder()
                    .withBillingCycleVolumeBalance(nextLong(threshold, Integer.MAX_VALUE), threshold).build();

            assertTrue(event.isEligible(previousBalance, currentBalance));
        }
    }

    public class isEligibleWithMultipleBalance {

        @Test
        public void callsIsEligibleWithSingleBalanceWithLeastCurrentBalance() throws Exception {
            /* Intentionally provided range from 3 to 100, because thresold value used as base value for generating min and max balance.
            */
            int threshold = nextInt(3,100);
            QuotaThresholdEvent event = spy(createEvent(UsageType.VOLUME, threshold));
            NonMonetoryBalance previousBalance1 = createNonMonetaryBalanceBuilder()
                    .withBillingCycleVolumeBalance(nextLong(threshold, Integer.MAX_VALUE), nextLong(threshold+1, Integer.MAX_VALUE)).build();
            NonMonetoryBalance previousBalance2 = createNonMonetaryBalanceBuilder()
                    .withBillingCycleVolumeBalance(nextLong(threshold, Integer.MAX_VALUE), nextLong(threshold+1, Integer.MAX_VALUE)).build();
            NonMonetoryBalance currentBalance1 = createNonMonetaryBalanceBuilder()
                    .withBillingCycleVolumeBalance(nextLong(threshold, Integer.MAX_VALUE), nextLong(2, threshold-1)).build();
            NonMonetoryBalance currentBalance2 = createNonMonetaryBalanceBuilder()
                    .withBillingCycleVolumeBalance(nextLong(threshold, Integer.MAX_VALUE), nextLong(1, currentBalance1.getBillingCycleAvailableVolume()-1)).build();

            Map<NonMonetoryBalance, NonMonetoryBalance> currentToPreviousBalances = new IdentityHashMap<>(2);
            currentToPreviousBalances.put(currentBalance1, previousBalance1);
            currentToPreviousBalances.put(currentBalance2, previousBalance2);

            event.isEligible(currentToPreviousBalances);
            verify(event, times(1)).isEligible(previousBalance2, currentBalance2);
        }
    }
}