package com.elitecore.netvertex.pm.rnc.pkg;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroup;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.pm.quota.RoPolicyContextImpl;
import com.elitecore.netvertex.pm.rnc.ratecard.MonetaryRateCard;
import com.elitecore.netvertex.pm.rnc.ratecard.NonMonetaryRateCard;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class RnCPackageTest {
    @Mock
    private QuotaReservation quotaReservation;
    @Mock
    private NonMonetaryRateCard nonMonetaryRateCard;
    @Mock
    private MonetaryRateCard monetaryRateCard;
    @Mock
    private AccessTimePolicy accessTimePolicy;

    private RoPolicyContextImpl policyContext;

    private static final String currency="INR";

    @Before
    public void setup(){
        ExecutionContext context = Mockito.mock(ExecutionContext.class);

        Mockito.when(context.getCurrentTime()).thenReturn(Calendar.getInstance());

        policyContext = new RoPolicyContextImpl(null,
                null, null, context,
                null,null);

        policyContext =  Mockito.spy(policyContext);

    }

    private RnCPackage createRnCPackage(){
        List< RateCardGroup > rateCardGroups = new ArrayList<>();
        rateCardGroups.add(null);

        return new RnCPackage("id", "name", null,
                null, rateCardGroups, null,
                null, null,
                null, null, null,
                null, null, ChargingType.SESSION,currency);
    }

    @Test
    public void applySetsNextTimeOutInTheContext(){
        RnCPackage rnCPackage = createRnCPackage();
        rnCPackage =Mockito.spy(rnCPackage);
        Mockito.doReturn(100L).when(rnCPackage).getNextSessionTimeOut(Mockito.any());
        rnCPackage.apply(policyContext, quotaReservation, null);
        Assert.assertEquals(100L, policyContext.getTimeout());
    }
}
