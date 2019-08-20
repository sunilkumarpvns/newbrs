package com.elitecore.corenetvertex.spr.util;

import com.elitecore.corenetvertex.spr.data.FnFGroup;
import com.elitecore.corenetvertex.spr.data.SubscriptionMetadata;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;

public class SubscriptionUtilTest {

    @Test
    public void createMetadataReturnsFnFObjectFromString(){

        SubscriptionMetadata metadata =
                SubscriptionUtil.createMetaData("{\"fnFGroup\":{\"name\":\"fnf\",\"members\":[\"1235468975\"]}}",
                null,null);
        Assert.assertEquals("fnf",metadata.getFnFGroup().getName());
        Assert.assertEquals("1235468975",metadata.getFnFGroup().getMembers().get(0));
    }

    @Test
    public void createMetadataReturnsNullIfMemberInStringIsNotAvailableInPOJO(){
        SubscriptionMetadata metadata =
                SubscriptionUtil.createMetaData("{\"fnFGroup\":{\"nemesis\":\"fnf\",\"members\":[\"1235468975\"]}}",
                        null,null);
        Assert.assertNull(metadata);
    }

    @Test
    public void createMetadataReturnsNullIfStringIsNull(){
        SubscriptionMetadata metadata =
                SubscriptionUtil.createMetaData(null,null,null);
        Assert.assertNull(metadata);
    }

    @Test
    public void createMetadataReturnsNullIfStringIsBlank(){
        SubscriptionMetadata metadata =
                SubscriptionUtil.createMetaData("",
                        null,null);
        Assert.assertNull(metadata);
    }

    @Test
    public void createMetaStringReturnsStringifiedFnFObject(){
        String[] fruitArray = {"5426852135", "7895362154"};
        FnFGroup fnFGroup = new FnFGroup("new", Arrays.asList(fruitArray));
        SubscriptionMetadata metadata = new SubscriptionMetadata();
        metadata.setFnFGroup(fnFGroup);
        String value = SubscriptionUtil.createMetaString(metadata,null,null);
        Assert.assertEquals("{\"fnFGroup\":{\"name\":\"new\",\"members\":[\"5426852135\",\"7895362154\"]}}",value);
    }

    @Test
    public void createMetaStringReturnsNullIfExceptionOccurs(){
        SubscriptionMetadata metadata = new SubscriptionMetadata();
        metadata = Mockito.spy(metadata);
        Mockito.doThrow(new NullPointerException()).when(metadata).getClass();
        String value = SubscriptionUtil.createMetaString(metadata,null,null);
        Assert.assertNull(value);
    }

    @Test
    public void createMetaStringReturnsNullIfMetadataIsNull(){
        String value = SubscriptionUtil.createMetaString(null,null,null);
        Assert.assertNull(value);
    }
}
