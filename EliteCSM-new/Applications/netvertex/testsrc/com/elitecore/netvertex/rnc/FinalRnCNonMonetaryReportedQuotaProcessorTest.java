package com.elitecore.netvertex.rnc;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCardVersion;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.FlatRating;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.FlatSlab;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateCardVersion;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateSlab;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.VersionDetail;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.diameter.gy.ReportingReason;
import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.UUID;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class FinalRnCNonMonetaryReportedQuotaProcessorTest {
    private FinalRnCNonMonetaryReportedQuotaProcessor finalRnCNonMonetaryReportedQuotaProcessor;
    private DummyPolicyRepository policyRepository;
    private PCRFRequest request;
    private PCRFResponse response;
    private MSCC quotaReservationEntry;
    private GyServiceUnits usedServiceUnits=new GyServiceUnits();
    private GyServiceUnits grantedServiceUnits= new GyServiceUnits();
    private RnCNonMonetaryBalance nonMonetaryBalance ;
    private ReportedUsageSummary reportedUsageSummary;
    private MockBasePackage mockBasePackage;
    private ExecutionContext executionContext;

    @Before
    public void setUp() {
        policyRepository = spy(new DummyPolicyRepository());
        request = new PCRFRequestImpl(new FixedTimeSource(100));
        request.setSessionStartTime(new Date());
        request.setPCRFEvents(EnumSet.of(PCRFEvent.SESSION_STOP));
        executionContext = new ExecutionContext(request, response, mock(CacheAwareDDFTable.class), "INR");
        SPRInfo sprInfo = new SPRInfoImpl.SPRInfoBuilder()
                .withSubscriberIdentity(UUID.randomUUID().toString())
                .build();
        nonMonetaryBalance=createNonMonetaryBalance(sprInfo);
        quotaReservationEntry = new MSCC();
        grantedServiceUnits=createGSU(nonMonetaryBalance);
        grantedServiceUnits.setRevenueCode("test");
        quotaReservationEntry.setGrantedServiceUnits(grantedServiceUnits);
        MSCC reportedUsage = new MSCC();
        reportedUsage.setReportingReason(ReportingReason.FINAL);
        usedServiceUnits.setTime(1551083535);
        reportedUsage.setUsedServiceUnits(usedServiceUnits);
        finalRnCNonMonetaryReportedQuotaProcessor = new FinalRnCNonMonetaryReportedQuotaProcessor(reportedUsage,
                quotaReservationEntry,
                null,
                nonMonetaryBalance,
                executionContext,
                policyRepository,
                nonMonetaryBalance);
        mockBasePackage = createPackage(BigDecimal.valueOf(2.0), Uom.SECOND);
        reportedUsageSummary = finalRnCNonMonetaryReportedQuotaProcessor.getReportedUsageSummary();
    }

    @Test
    public void testRevenueCodeIsSetForReportedSummaryUsage() {
        PolicyManager policyManager = mock(PolicyManager.class);
        PolicyManager.setInstance(policyManager);
        when(policyManager.getPkgDataById(mockBasePackage.getId())).thenReturn(mockBasePackage);
        ProductOfferStore productOfferStore = mock(ProductOfferStore.class);
        when(policyManager.getProductOffer()).thenReturn(productOfferStore);
        when(productOfferStore.byId(Mockito.anyString())).thenReturn(null);
        finalRnCNonMonetaryReportedQuotaProcessor.handle();
        Assert.assertEquals(reportedUsageSummary.getRevenueCode(),"test");
    }

    private GyServiceUnits createGSU(RnCNonMonetaryBalance nonMonetoryBalance) {
        return new GyServiceUnits("pkg_base",
                nextInt(5, 10),
                "1",
                nextInt(5, 10),
                null,
                0,
                null,
                false,
                nonMonetoryBalance.getId(),
                0d,
                100.0,
                nextInt(2, 5),
                100,
                nextInt(2, 5), 0,-1, 0);
    }
    private MockBasePackage createPackage(BigDecimal rate, Uom rateOn) {
        MockBasePackage mockBasePackage = policyRepository.mockBasePackage();
        mockBasePackage.quotaProfileTypeIsRnC();
        mockBasePackage.mockRateCard(createRateCardDetails(rate, rateOn));
        return mockBasePackage;
    }

    private DataRateCard createRateCardDetails(BigDecimal rate, Uom on){
        RateSlab slab = new FlatSlab(20, 10, rate, on, on);
        VersionDetail versionDetail = new FlatRating("Calling-Station-ID", "Calling-Station-ID", Arrays.asList(slab), null);
        RateCardVersion rateCardVersion = new DataRateCardVersion("1", "RC1", "version1", Arrays.asList(versionDetail));
        return new DataRateCard("1", "test", "Calling-Station-ID", "Calling-Station-ID", Arrays.asList(rateCardVersion), on, on);

    }

    private RnCNonMonetaryBalance createNonMonetaryBalance(SPRInfo sprInfo) {

        long billingCycleTotalTime = nextLong(61, 1000);
        return new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder(
                UUID.randomUUID().toString(),
                "test",
                "",
                sprInfo.getSubscriberIdentity(),
                null,
                "0",
                ResetBalanceStatus.NOT_RESET, null, ChargingType.SESSION).
                withBillingCycleTimeBalance(billingCycleTotalTime, nextLong(61, billingCycleTotalTime))
                .withDailyUsage(nextLong(61, billingCycleTotalTime))
                .withWeeklyUsage(nextLong(61, billingCycleTotalTime))
                .withDailyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .withWeeklyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(6))
                .withBillingCycleResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .build();
    }
}
