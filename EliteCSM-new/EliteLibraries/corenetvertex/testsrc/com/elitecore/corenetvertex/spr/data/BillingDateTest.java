package com.elitecore.corenetvertex.spr.data;

import com.elitecore.corenetvertex.constants.PasswordEncryptionType;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

@Ignore
public class BillingDateTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    SPRInfoImpl sprInfo = (SPRInfoImpl) getSubscriber("9999999999", SubscriberStatus.ACTIVE);

    @Test
    public void throws_operation_failed_exception_when_value_is_less_than_1() throws OperationFailedException {
        exception.expect(OperationFailedException.class);
        exception.expectMessage("Invalid Billing Date configured: 0"+". BillingDay must be between 1 to 28");
        SPRFields.BILLING_DATE.setStringValue(sprInfo,"0",true);
    }

    @Test
    public void throws_operation_failed_exception_when_value_is_greater_than_28() throws OperationFailedException{
        exception.expect(OperationFailedException.class);
        exception.expectMessage("Invalid Billing Date configured: 29"+". BillingDay must be between 1 to 28");
        SPRFields.BILLING_DATE.setNumericValue(sprInfo,29L,true);
    }

    @Test
    public void set_bill_date_for_subscriber_when_value_is_between_1_to_28() throws OperationFailedException{
        SPRFields.BILLING_DATE.setStringValue(sprInfo,"1",true);
        Assert.assertEquals(sprInfo.getBillingDate().longValue(),1L);
        SPRFields.BILLING_DATE.setNumericValue(sprInfo,28L,true);
        Assert.assertEquals(sprInfo.getBillingDate().longValue(),28L);

    }

    private SPRInfo getSubscriber(String msisdn, SubscriberStatus status) {
        return new SPRInfoImpl.SPRInfoBuilder().withStatus(status.name()).withEncryptionType(PasswordEncryptionType.NONE.strVal).withMsisdn(msisdn).withSubscriberIdentity(msisdn).build();
    }
}

