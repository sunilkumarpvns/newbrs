package com.elitecore.netvertex.service.notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaThresholdEvent;
import com.elitecore.corenetvertex.service.notification.NotificationEvent;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriptionNonMonitoryBalance;
import com.elitecore.netvertex.service.pcrf.NotificationQueue;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(HierarchicalContextRunner.class)
public class QuotaNotificationSchemeTest {

    private QuotaNotificationScheme quotaNotificationScheme;
    private List<List<QuotaThresholdEvent>> thresholdEvents;
    private List<List<QuotaThresholdEvent>> limitEvents;
    private NotificationQueue notificationQueue;
    private SubscriptionNonMonitoryBalance nonMonitoryBalance;
    private String quotaProfileId;
    private int fupLevel = 0;
    private QuotaThresholdEvent quotaThresholdEvent;

    @Before
    public void setUp() {

        this.quotaProfileId = UUID.randomUUID().toString();
        thresholdEvents = new ArrayList<>();
        limitEvents = new ArrayList<>();

        List<QuotaThresholdEvent> eventList = createEvents(true);
        thresholdEvents.add(eventList);

        this.quotaNotificationScheme = new QuotaNotificationScheme(thresholdEvents, Collectionz.newArrayList());
        this.notificationQueue = spy(new NotificationQueueExt());
        this.nonMonitoryBalance = new SubscriptionNonMonitoryBalance("packageId");

        NonMonetoryBalance nonMonetoryBalance = new NonMonetoryBalance.NonMonetaryBalanceBuilder("id",
                CommonConstants.ALL_SERVICE_IDENTIFIER,
                "packageId",
                CommonConstants.DEFAULT_RATING_GROUP_IDENTIFIER,
                "subscriberIdentity",
                "subscriptionId",
                0,
                quotaProfileId,
                ResetBalanceStatus.NOT_RESET, null, null)
                .build();
        this.nonMonitoryBalance.addBalance(nonMonetoryBalance);
    }

    @Nonnull
    private List<QuotaThresholdEvent> createEvents(boolean isEligible) {
        QuotaThresholdEvent quotaThresholdEvent = createEvent(isEligible);
        List<QuotaThresholdEvent> eventList = new ArrayList<>();
        eventList.add(quotaThresholdEvent);
        return eventList;
    }

    @Nonnull
    private QuotaThresholdEventExt createEvent(boolean isEligible) {
        return new QuotaThresholdEventExt(UsageType.VOLUME,
                AggregationKey.BILLING_CYCLE,
                CommonConstants.ALL_SERVICE_IDENTIFIER,
                CommonConstants.DEFAULT_RATING_GROUP_IDENTIFIER,
                quotaProfileId, 0, fupLevel, null, null, isEligible);
    }

    public class queueEligibleEventsThrowsNPEWhen {

        @Test(expected = NullPointerException.class)
        public void previousBalanceIsNull() throws Exception {
            SubscriptionNonMonitoryBalance previousBalance = null;
            SubscriptionNonMonitoryBalance currentBalance = new SubscriptionNonMonitoryBalance("packageId");
            ;
            quotaNotificationScheme.queueEligibleEvents(previousBalance, currentBalance, notificationQueue);
        }

        @Test(expected = NullPointerException.class)
        public void CurrentBalanceIsNull() throws Exception {
            SubscriptionNonMonitoryBalance previousBalance = new SubscriptionNonMonitoryBalance("packageId");
            SubscriptionNonMonitoryBalance currentBalance = null;
            quotaNotificationScheme.queueEligibleEvents(previousBalance, currentBalance, notificationQueue);
        }
    }

    public class OneEvent {

        @Test
        public void queueEligibleEventsAddsEventWhenEventIsEligibleTrue() throws Exception {
            List<QuotaThresholdEvent> events = createEvents(true);
            quotaNotificationScheme = new QuotaNotificationScheme(Arrays.asList(events), Collectionz.newArrayList());
            SubscriptionNonMonitoryBalance previousBalance = nonMonitoryBalance;
            SubscriptionNonMonitoryBalance currentBalance = nonMonitoryBalance;

            quotaNotificationScheme.queueEligibleEvents(previousBalance, currentBalance, notificationQueue);

            verify(notificationQueue, times(1)).add(events.get(0));
        }

        @Test
        public void queueEligibleEventsNotAddsEventWhenEventIsEligibleFalse() throws Exception {
            List<QuotaThresholdEvent> events = createEvents(false);
            quotaNotificationScheme = new QuotaNotificationScheme(Arrays.asList(events), Collectionz.newArrayList());
            SubscriptionNonMonitoryBalance previousBalance = nonMonitoryBalance;
            SubscriptionNonMonitoryBalance currentBalance = nonMonitoryBalance;

            quotaNotificationScheme.queueEligibleEvents(previousBalance, currentBalance, notificationQueue);

            verify(notificationQueue, never()).add(events.get(0));
        }
    }

    public class MultipleEvents {

        @Test
        public void OnlyFirstEligibleEventShouldBeQueuedIfMultipleEventIsEligibleOnSingleKey() throws Exception {
            List<QuotaThresholdEvent> events = new ArrayList<>();
            events.add(createEvent(true));
            events.add(createEvent(true));
            quotaNotificationScheme = new QuotaNotificationScheme(Arrays.asList(events), Collectionz.newArrayList());
            SubscriptionNonMonitoryBalance previousBalance = nonMonitoryBalance;
            SubscriptionNonMonitoryBalance currentBalance = nonMonitoryBalance;

            quotaNotificationScheme.queueEligibleEvents(previousBalance, currentBalance, notificationQueue);

            verify(notificationQueue, times(1)).add(events.get(0));
        }
    }

    public class queueEligibleEventsWithMultipleUsages {

        @Test
        public void OnlyFirstEligibleEventShouldBeQueuedIfMultipleEventIsEligibleOnSingleKey() throws Exception {
            List<QuotaThresholdEvent> events = createEvents(true);
            quotaNotificationScheme = new QuotaNotificationScheme(Arrays.asList(events), Collectionz.newArrayList());

            quotaNotificationScheme.queueEligibleEvents(Arrays.asList(nonMonitoryBalance, nonMonitoryBalance), Arrays.asList(nonMonitoryBalance, nonMonitoryBalance), notificationQueue);

            verify(notificationQueue, times(1)).add(events.get(0));
        }
    }

    private class NotificationQueueExt implements NotificationQueue {

        @Override
        public void add(NotificationEvent event) {

        }
    }

    private class QuotaThresholdEventExt extends QuotaThresholdEvent {

        private boolean isEligible = true;

        public QuotaThresholdEventExt(UsageType usageType, AggregationKey aggregationKey,
                                      long serviceIdentifier, long rgIdentifier, String quotaProfileId,
                                      long threshold, int level, Template emailTemplate, Template smsTemplate,
                                      boolean isEligible) {
            super(usageType, aggregationKey, serviceIdentifier, rgIdentifier, quotaProfileId, threshold, level, emailTemplate, smsTemplate);
            this.isEligible = isEligible;
        }

        @Override
        public boolean isEligible(@Nullable NonMonetoryBalance previousBalance, NonMonetoryBalance currentBalance) {
            return isEligible;
        }
    }
}