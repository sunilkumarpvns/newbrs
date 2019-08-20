package com.elitecore.netvertex.pm.quota;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.spr.BalanceProvider;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.QuotaProfile;
import com.elitecore.netvertex.pm.RnCQuotaProfileFactory;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.mockito.Mockito.mock;

public class RoPolicyContextImplTest {

    public static final String INR = "INR";
    private PCRFRequest request;
    private PCRFResponse response;
    private ExecutionContext executionContext;
    private MockBasePackage basePackage;
    private RoPolicyContextImpl gyPolicyContextImpl;
    private RnCQuotaProfileDetail rnCQuotaProfileDetail;

    @Before
    public void setUp() throws OperationFailedException {
        request = new PCRFRequestImpl();
        response = new PCRFResponseImpl();

        basePackage = MockBasePackage.create(UUID.randomUUID().toString(), UUID.randomUUID().toString()+"name");

        SPRInfoImpl sprInfo = (SPRInfoImpl) new SPRInfoImpl.SPRInfoBuilder()
                .withSubscriberIdentity("test")
                .withProductOffer(basePackage.getName())
                .build();

        sprInfo.setBalanceProvider(mock(BalanceProvider.class));
        request.setSPRInfo(sprInfo);
        executionContext = new ExecutionContext(request, response, mock(CacheAwareDDFTable.class), INR);

        String quotaProfileId = UUID.randomUUID().toString();

        rnCQuotaProfileDetail = new RnCQuotaProfileFactory(quotaProfileId, UUID.randomUUID().toString()).randomBalanceWithRate().create();
        Map<String, QuotaProfileDetail> hashMap = new HashMap<>();
        hashMap.put("test", rnCQuotaProfileDetail);

        QuotaProfile quotaProfile = new QuotaProfile("test", basePackage.getName(), quotaProfileId, BalanceLevel.HSQ,2, RenewalIntervalUnit.MONTH, QuotaProfileType.RnC_BASED, Arrays.asList(hashMap), CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue());
        basePackage.mockQuotaProfie(quotaProfile);

        NonMonetoryBalance nonMonetoryBalance = createNonMonetaryBalance(request.getSPRInfo(), rnCQuotaProfileDetail);
        SubscriberNonMonitoryBalance subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(Arrays.asList(nonMonetoryBalance));
        response.setCurrentNonMonetoryBalance(subscriberNonMonitoryBalance);

        SubscriberMonetaryBalance subscriberMonetaryBalance = new SubscriberMonetaryBalance(new FixedTimeSource(System.currentTimeMillis()));
        subscriberMonetaryBalance.addMonitoryBalances(createMonetaryBalance("test"));
        response.setCurrentMonetaryBalance(subscriberMonetaryBalance);


        gyPolicyContextImpl = new RoPolicyContextImpl(request, response, null, executionContext, null, null);
    }

    @Test
    public void test_process_distributedValidityTime_revalidationTimeIsProvided(){

        int validityTimeInSeconds = 10;
        QuotaReservation quotaReservation = gyPolicyContextImpl.getGrantedAllMSCC();

        MSCC mscc = new MSCC();
        mscc.setRatingGroup(rnCQuotaProfileDetail.getRatingGroup().getIdentifier());
        mscc.setValidityTime(validityTimeInSeconds);
        quotaReservation.put(mscc);

        long validityTime = gyPolicyContextImpl.getGrantedAllMSCC().get(mscc.getRatingGroup()).getValidityTime();

        Assert.assertTrue(validityTime >= validityTimeInSeconds);
    }

    @Test
    public void test_process_distributedValidityTime_revalidationTimeIsZero(){

        int validityTimeInSeconds = 10;

        RoPolicyContextImpl gyPolicyContextImpl = new RoPolicyContextImpl(request, response, null, executionContext, null, null);
        QuotaReservation quotaReservation = gyPolicyContextImpl.getGrantedAllMSCC();

        MSCC mscc = new MSCC();
        mscc.setRatingGroup(rnCQuotaProfileDetail.getRatingGroup().getIdentifier());
        mscc.setValidityTime(validityTimeInSeconds);
        quotaReservation.put(mscc);

        long validityTime = gyPolicyContextImpl.getGrantedAllMSCC().get(mscc.getRatingGroup()).getValidityTime();

        Assert.assertTrue(validityTime == validityTimeInSeconds);
    }

    private NonMonetoryBalance createNonMonetaryBalance(SPRInfo sprInfo, RnCQuotaProfileDetail rnCQuotaProfileDetail) {

        long billingCycleTotalVolume = nextLong(2, 1000);
        long billingCycleTotalTime = nextLong(2, 1000);
        return new NonMonetoryBalance.NonMonetaryBalanceBuilder(
                UUID.randomUUID().toString(), rnCQuotaProfileDetail.getDataServiceType().getServiceIdentifier(),
                basePackage.getId(),
                rnCQuotaProfileDetail.getRatingGroup().getIdentifier(),
                sprInfo.getSubscriberIdentity(),
                null,
                0,
                basePackage.getQuotaProfiles().get(0).getId(), ResetBalanceStatus.NOT_RESET, null, null).
                withBillingCycleVolumeBalance(billingCycleTotalVolume, nextLong(2, billingCycleTotalVolume)).
                withBillingCycleTimeBalance(billingCycleTotalTime, nextLong(2, billingCycleTotalTime))
                .withDailyUsage(nextLong(2, billingCycleTotalVolume), nextLong(2, billingCycleTotalTime))
                .withWeeklyUsage(nextLong(2, billingCycleTotalVolume), nextLong(2, billingCycleTotalTime))
                .withDailyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .withWeeklyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(6))
                .withBillingCycleResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .build();
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
                reservation,
                0,
                0,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                UUID.randomUUID().toString(),
                MonetaryBalanceType.DEFAULT.name(),
                System.currentTimeMillis(),
                0,
                "","");
    }
}