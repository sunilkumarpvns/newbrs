package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCardVersion;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.FlatRating;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.FlatSlab;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateCardVersion;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateSlab;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.VersionDetail;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.diameter.gy.ReportingReason;
import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class NonFinalRnCReportedQuotaProcessorTest {
    private NonFinalRnCReportedQuotaProcessor nonFinalRnCReportedQuotaProcessor;
    private DummyPolicyRepository policyRepository;
    private MSCC quotaReservationEntry;
    private GyServiceUnits usedServiceUnits=new GyServiceUnits();
    private GyServiceUnits grantedServiceUnits= new GyServiceUnits();
    private MonetaryBalance monetaryBalance ;
    private ReportedUsageSummary reportedUsageSummary;
    private MockBasePackage mockBasePackage;

    @Before
    public void setUp() {
        policyRepository = spy(new DummyPolicyRepository());
        monetaryBalance=createMonetaryBalance("subscriberId");
        quotaReservationEntry = new MSCC();
        grantedServiceUnits=createGSU(monetaryBalance);
        grantedServiceUnits.setRevenueCode("test");
        quotaReservationEntry.setGrantedServiceUnits(grantedServiceUnits);
        MSCC reportedUsage = new MSCC();
        reportedUsage.setReportingReason(ReportingReason.FINAL);
        usedServiceUnits.setTime(1551083535);
        reportedUsage.setUsedServiceUnits(usedServiceUnits);
        nonFinalRnCReportedQuotaProcessor = new NonFinalRnCReportedQuotaProcessor(reportedUsage,
                quotaReservationEntry,
                null,
                createMonetaryBalance("subscriberId"),
                policyRepository,
                createMonetaryBalance("subscriberId"));
        mockBasePackage = createPackage(BigDecimal.valueOf(2.0), Uom.SECOND);
        reportedUsageSummary = nonFinalRnCReportedQuotaProcessor.getReportedUsageSummary();
    }

    @Test
    public void testRevenueCodeIsSetForReportedSummaryUsage() {
        PolicyManager policyManager = mock(PolicyManager.class);
        PolicyManager.setInstance(policyManager);
        when(policyManager.getPkgDataById(mockBasePackage.getId())).thenReturn(mockBasePackage);
        ProductOfferStore productOfferStore = mock(ProductOfferStore.class);
        when(policyManager.getProductOffer()).thenReturn(productOfferStore);
        when(productOfferStore.byId(Mockito.anyString())).thenReturn(null);
        nonFinalRnCReportedQuotaProcessor.handle();
        Assert.assertEquals(reportedUsageSummary.getRevenueCode(),"test");
    }

    private GyServiceUnits createGSU(MonetaryBalance monetaryBalance) {
        return new GyServiceUnits("pkg_base",
                nextInt(5, 10),
                "1",
                nextInt(5, 10),
                null,
                0,
                null,
                false,
                monetaryBalance.getId(),
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

    private MonetaryBalance createMonetaryBalance(String subscriberId) {
        int totalBalance = nextInt(2, 1000);
        int availableBalance = nextInt(1, totalBalance);
        int reservation = nextInt(1, availableBalance);
        return new MonetaryBalance(UUID.randomUUID().toString(),
                subscriberId,
                CommonConstants.MONEY_DATA_SERVICE,
                availableBalance,
                totalBalance,
                reservation,0, 0,
                System.currentTimeMillis(),
                CommonConstants.FUTURE_DATE,
                UUID.randomUUID().toString(),null,
                System.currentTimeMillis(),0
                ,"","");
    }
}
