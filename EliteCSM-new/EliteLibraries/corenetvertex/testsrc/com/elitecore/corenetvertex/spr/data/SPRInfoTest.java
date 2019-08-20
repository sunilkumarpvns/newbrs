package com.elitecore.corenetvertex.spr.data;

import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.spr.SubscriptionProvider;
import com.elitecore.corenetvertex.spr.UMOperationTest;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl.SPRInfoBuilder;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

public class SPRInfoTest {

    private TestableSubscriptionProvider testableSubscriptionProvider;
    private SPRInfoImpl info;

    @Before
    public  void setUp() {
        testableSubscriptionProvider = new TestableSubscriptionProvider();
        info = new SPRInfoImpl();
        info.setSubscriptionProvider(testableSubscriptionProvider);

    }
    @Test
    public void test_setSubscriberPackage_should_change_package_in_SPRInfo() throws Exception {

        final String currentPackage = "currentPackage";

        SPRInfo info = new SPRInfoBuilder().withProductOffer(currentPackage).build();

        final String nextPackage = "nextPackage";

        info.setProductOffer(nextPackage);

        assertEquals(nextPackage, info.getProductOffer());
    }

    @Test
    public void test_setUsageMeteringEnable_should_change_unknownUser_flag_in_SPRInfo() throws Exception {

        SPRInfo info = new SPRInfoBuilder().build();

        final boolean changedFlag = true;

        info.setUnknownUser(changedFlag);

        assertEquals(changedFlag, info.isUnknownUser());
    }

    @Test
    public void testSubscriptionProviderIsCalledWhenSubscriptionISNotFetchedFromCache() throws OperationFailedException {
        SPRInfoImpl info = new SPRInfoImpl();
        TestableSubscriptionProvider testableSubscriptionProvider = new TestableSubscriptionProvider();
        info.setSubscriptionProvider(testableSubscriptionProvider);
        info.getActiveSubscriptions(System.currentTimeMillis());
        assertEquals(1, testableSubscriptionProvider.getNoOfCalls());
    }

    @Test
    public void testSubscriptionProviderIsNotCalledWhenSubscriptionIsFetchedFromCache() throws OperationFailedException {
        SPRInfoImpl info = new SPRInfoImpl();
        TestableSubscriptionProvider testableSubscriptionProvider = new TestableSubscriptionProvider();
        info.setSubscriptionProvider(testableSubscriptionProvider);
        info.getActiveSubscriptions(System.currentTimeMillis());
        info.getActiveSubscriptions(System.currentTimeMillis());
        assertEquals(1, testableSubscriptionProvider.getNoOfCalls());
    }

    @Test
    public void testStatusOfSubscriberShouldBeSetAsACTIVEWhenStatusOfSubscriberIsNotConfigured() {
        SPRInfoImpl info = new SPRInfoImpl();
        info.setStatus(null);
        String expectedStatus = SubscriberStatus.ACTIVE.name();
        String actualStatus = info.getStatus();
        Assert.assertEquals(expectedStatus, actualStatus);
    }


    public static class TestableSubscriptionProvider implements SubscriptionProvider {

        private int noOfCalls = 0;
        private Subscription subscription;
        LinkedHashMap<String, Subscription> subscriptionLinkedHashMap = new LinkedHashMap<String, Subscription>();

        @Override
        public LinkedHashMap<String, Subscription> getSubscriptions(SPRInfo sprInfo) throws OperationFailedException {
            noOfCalls++;
            Subscription subscription = Mockito.mock(Subscription.class);
            setSubscriptions(subscription);
            subscriptionLinkedHashMap.put("1", subscription);
            return subscriptionLinkedHashMap;
        }

        @Override
        public LinkedHashMap<String, Subscription> getSubscriptions(String subscriberId) throws OperationFailedException {
            return null;
        }

        private void setSubscriptions(Subscription... subscription) {
            this.subscription = subscription[0];
        }

        public int getNoOfCalls() {
            return noOfCalls;
        }

        private Subscription getSubscription() {
            return subscription;
        }
    }

}
