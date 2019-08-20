package com.elitecore.netvertex.pm.quota;

import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.data.ResultCode;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class QuotaReservationTest {
    private QuotaReservation quotaReservation = new QuotaReservation();

    @Test
    public void test_quotaReservation_getAsJson(){

        GyServiceUnits grantedServiceUnits = new GyServiceUnits();
        grantedServiceUnits.setPackageId("Package Id");
        grantedServiceUnits.setSubscriptionId("Subscription Id");
        grantedServiceUnits.setQuotaProfileIdOrRateCardId("Quota Profile Id");
        grantedServiceUnits.setBalanceId("Balance Id");
        grantedServiceUnits.setVolume(1);
        grantedServiceUnits.setReservationRequired(true);

        MSCC mscc1 = new MSCC();
        mscc1.setRatingGroup(0l);
        mscc1.setServiceIdentifiers(Arrays.asList(2l));
        mscc1.setResultCode(ResultCode.SUCCESS);
        mscc1.setGrantedServiceUnits(grantedServiceUnits);

        MSCC mscc2 = new MSCC();
        mscc2.setRatingGroup(1l);
        mscc2.setServiceIdentifiers(Arrays.asList(2l));
        mscc2.setResultCode(ResultCode.SUCCESS);
        mscc2.setGrantedServiceUnits(grantedServiceUnits);

        quotaReservation.put(mscc1);
        quotaReservation.put(mscc2);

        String json = quotaReservation.getAsJson();

        Assert.assertSame(quotaReservation.get(0l), mscc1);
        Assert.assertSame(quotaReservation.get(1l), mscc2);
    }

    @Test
    public void test_quotaReservation_isReservationExist_when_flag_is_true(){

        GyServiceUnits grantedServiceUnits = new GyServiceUnits();
        grantedServiceUnits.setPackageId("Package Id");
        grantedServiceUnits.setSubscriptionId("Subscription Id");
        grantedServiceUnits.setQuotaProfileIdOrRateCardId("Quota Profile Id");
        grantedServiceUnits.setBalanceId("Balance Id");
        grantedServiceUnits.setVolume(1);
        grantedServiceUnits.setReservationRequired(true);

        MSCC mscc = new MSCC();
        mscc.setRatingGroup(0l);
        mscc.setServiceIdentifiers(Arrays.asList(2l));
        mscc.setResultCode(ResultCode.SUCCESS);
        mscc.setGrantedServiceUnits(grantedServiceUnits);

        quotaReservation.put(mscc);

        Assert.assertSame(true, quotaReservation.isReservationExist());
    }

    @Test
    public void test_quotaReservation_isReservationExist_when_monetary_reservation_exist(){

        GyServiceUnits grantedServiceUnits = new GyServiceUnits();
        grantedServiceUnits.setPackageId("Package Id");
        grantedServiceUnits.setSubscriptionId("Subscription Id");
        grantedServiceUnits.setQuotaProfileIdOrRateCardId("Quota Profile Id");
        grantedServiceUnits.setBalanceId("Balance Id");
        grantedServiceUnits.setVolume(1);
        grantedServiceUnits.setReservedMonetaryBalance(1);

        MSCC mscc = new MSCC();
        mscc.setRatingGroup(0l);
        mscc.setServiceIdentifiers(Arrays.asList(2l));
        mscc.setResultCode(ResultCode.SUCCESS);
        mscc.setGrantedServiceUnits(grantedServiceUnits);

        quotaReservation.put(mscc);

        Assert.assertSame(true, quotaReservation.isReservationExist());
    }
}
