package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.util.SubscriptionUtil;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Timestamp;

public class AddOnSubscriptionComparatorTest {

    private AddOnSubscriptionComparator comparator = new AddOnSubscriptionComparator();

    @Test
    public void forSameStartTimeAndSameEndTimeComparatorGives1ResultOne(){
        Subscription subscription1 =  new Subscription("abc1","xyz", "foo", "productOfferId", new Timestamp(100), new Timestamp(1000000L),
                null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);
        Subscription subscription2 =  new Subscription("abc2","xyz", "foo", "productOfferId", new Timestamp(100), new Timestamp(1000000L),
                null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);

        Assert.assertEquals(1,comparator.compare(subscription1,subscription2));
    }

    @Test
    public void forDifferentSubscriptionEndTimeWhenLeftOneHasGreaterEndTimeCompatorReturnsNegative1(){
        Subscription subscription1 =  new Subscription("abc1","xyz", "foo", "productOfferId", new Timestamp(100), new Timestamp(1000002L),
                null , CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);
        Subscription subscription2 =  new Subscription("abc2","xyz", "foo", "productOfferId", new Timestamp(100), new Timestamp(1000000L),
                null , CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);

        Assert.assertEquals(-1,comparator.compare(subscription1,subscription2));
    }

    @Test
    public void forDifferentSubscriptionEndTimeWhenRightOneHasGreaterEndTimeCompatorReturns1(){
        Subscription subscription1 =  new Subscription("abc1","xyz", "foo", "productOfferId", new Timestamp(100), new Timestamp(1000000L),
                null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);
        Subscription subscription2 =  new Subscription("abc2","xyz", "foo", "productOfferId", new Timestamp(100), new Timestamp(1000002L),
                null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);

        Assert.assertEquals(1,comparator.compare(subscription1,subscription2));
    }

    @Test
    public void forDifferentSubscriptionStartTimeWhenLeftOneHasGreaterEndTimeCompatorReturnsNegative1(){
        Subscription subscription1 =  new Subscription("abc1","xyz", "foo", "productOfferId", new Timestamp(102), new Timestamp(1000000L),
                null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);
        Subscription subscription2 =  new Subscription("abc2","xyz", "foo", "productOfferId", new Timestamp(100), new Timestamp(1000000L),
                null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);

        Assert.assertEquals(-1,comparator.compare(subscription1,subscription2));
    }

    @Test
    public void forDifferentSubscriptionStartTimeWhenRightOneHasGreaterEndTimeCompatorReturns1(){
        Subscription subscription1 =  new Subscription("abc1","xyz", "foo", "productOfferId", new Timestamp(100), new Timestamp(1000000L),
                null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);
        Subscription subscription2 =  new Subscription("abc2","xyz", "foo", "productOfferId", new Timestamp(102), new Timestamp(1000002L),
                null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);

        Assert.assertEquals(1,comparator.compare(subscription1,subscription2));
    }

    @Test
    public void forSameSubscriptionPriorityWhenLeftOneHasFnFGroupComparatorReturns1(){
        Subscription subscription1 =  new Subscription("abc1","xyz", "foo", "productOfferId", new Timestamp(100), new Timestamp(1000000L),
                null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, SubscriptionUtil.createMetaData("{\"fnFGroup\":{\"name\":\"fnf\",\"members\":[]}}","abc1","test"), null, null);
        Subscription subscription2 =  new Subscription("abc2","xyz", "foo", "productOfferId", new Timestamp(100), new Timestamp(1000000L),
                null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null, null);

        Assert.assertEquals(1,comparator.compare(subscription1,subscription2));
    }

    @Test
    public void forDifferentSubscriptionPriorityWhenLeftOneHasFnFGroupButLowPriorityComparatorReturnsNegative1(){
        Subscription subscription1 =  new Subscription("abc1","xyz", "foo", "productOfferId", new Timestamp(100), new Timestamp(1000000L),
                null, CommonConstants.MAX_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON,SubscriptionUtil.createMetaData("{\"fnFGroup\":{\"name\":\"fnf\",\"members\":[]}}","abc1","test1"), null, null);
        Subscription subscription2 =  new Subscription("abc2","xyz", "foo", "productOfferId", new Timestamp(100), new Timestamp(1000000L),
                null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null, null);

        Assert.assertEquals(-1,comparator.compare(subscription1,subscription2));
    }

    @Test
    public void forBothFnFSubscriptionsWithSamePriorityWhenLeftOneHasEarlyEndTimeComparatorReturns1(){
        Subscription subscription1 =  new Subscription("abc1","xyz", "foo", "productOfferId", new Timestamp(100), new Timestamp(1000000L),
                null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON,SubscriptionUtil.createMetaData("{\"fnFGroup\":{\"name\":\"fnfGroup1\",\"members\":[]}}","abc1","test"), null, null);
        Subscription subscription2 =  new Subscription("abc2","xyz", "foo", "productOfferId", new Timestamp(100), new Timestamp(10000001L),
                null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON,SubscriptionUtil.createMetaData("{\"fnFGroup\":{\"name\":\"fnfGroup2\",\"members\":[]}}","abc1","test"), null, null);

        Assert.assertEquals(1,comparator.compare(subscription1,subscription2));
    }

    @Test
    public void forBothFnFSubscriptionsWithSamePriorityAndSameEndTimeWhenLeftOneHasEarlyStartTimeComparatorReturns1(){
        Subscription subscription1 =  new Subscription("abc1","xyz", "foo", "productOfferId", new Timestamp(99), new Timestamp(1000000L),
                null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, SubscriptionUtil.createMetaData("{\"fnFGroup\":{\"name\":\"fnfGroup1\",\"members\":[]}}","abc1","test"), null, null);
        Subscription subscription2 =  new Subscription("abc2","xyz", "foo", "productOfferId", new Timestamp(100), new Timestamp(1000000L),
                null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON,SubscriptionUtil.createMetaData("{\"fnFGroup\":{\"name\":\"fnfGroup1\",\"members\":[]}}","abc1","test"),  null, null);

        Assert.assertEquals(1,comparator.compare(subscription1,subscription2));
    }

}
