
package com.elitecore.netvertex.pm.qos.rnc.ratecard;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.FlatSlab;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.pm.PCRFPolicyContextImpl;
import com.elitecore.netvertex.pm.PCRFQoSProcessor;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.QoSInformation;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class DataRateCardVersionTest {

    public static final String INR = "INR";
    private DataRateCardVersion dataRateCardVersion;
    private final String id= "Id";
    private final String rcName = "RC";
    private final String name = "version1";
    private List<com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.VersionDetail> versionDetails;
    private static final String keyOne = "k1";
    private static final String keyTwo = "k2";
    private PolicyContext policyContext;
    private QoSInformation qoSInformation;
    private SubscriberMonetaryBalance subscriberMonetaryBalance;
    private FixedTimeSource fixedTimeSource;

    @Before
    public void setUp() throws OperationFailedException {
        versionDetails = new ArrayList<>();
        dataRateCardVersion = new DataRateCardVersion(id, rcName, name, versionDetails);
        fixedTimeSource = new FixedTimeSource(System.currentTimeMillis());
        PCRFRequest pcrfRequest = new PCRFRequestImpl();
        PCRFResponse pcrfResponse = new PCRFResponseImpl();

        qoSInformation = new QoSInformation();
        policyContext = spy(new PCRFPolicyContextImpl(pcrfRequest, pcrfResponse,
                new MockBasePackage(),
                new ExecutionContext(pcrfRequest, pcrfResponse, CacheAwareDDFTable.getInstance(), INR),
                new PCRFQoSProcessor(qoSInformation), new DummyPolicyRepository()));
        subscriberMonetaryBalance = spy(new SubscriberMonetaryBalance(fixedTimeSource));

    }



    public class isApplicableTrueWhen {

        public class versionDetailIsApplicableAndSlabIsFree {

            @Test
            public void shouldNotCallGetBalance() throws Exception {
                addVersionDetailWithApplicabilityAndSlabIsFree(true, true);

                assertTrue(dataRateCardVersion.isApplicable(policyContext, keyOne, keyTwo));
                verify(policyContext, never()).getCurrentMonetaryBalance();
            }
        }

        public class slabIsNotFreeAnd {

            @Before
            public void setUp() throws OperationFailedException {
                addVersionDetailWithApplicabilityAndSlabNotFree(true);
                MonetaryBalance dataMonetaryBalance = createDataMonetaryBalance(10000);
                subscriberMonetaryBalance.addMonitoryBalances(dataMonetaryBalance);
                doReturn(subscriberMonetaryBalance).when(policyContext).getCurrentMonetaryBalance();
            }

            @Test
            public void monetaryBalanceIsExist() throws Exception {
                assertTrue(dataRateCardVersion.isApplicable(policyContext, keyOne, keyTwo));
                verify(policyContext, times(1)).getCurrentMonetaryBalance();
            }
        }
    }

    private MonetaryBalance createDataMonetaryBalance(long availableBalance) {
        return new MonetaryBalance("id",
                "subscriberId",
                CommonConstants.MONEY_DATA_SERVICE,
                availableBalance,
                10,
                0,0, 0,
                0,
                0,
                "INR",null,
                0,
                0,
                null,
                null);
    }

    private FlatSlab createSlabIsFree(boolean isFree) {
        FlatSlab flatSlab = mock(FlatSlab.class);
        doReturn(isFree).when(flatSlab).isFree();
        doReturn(new BigDecimal(0)).when(flatSlab).getRate();
        return flatSlab;
    }

    private FlatSlab createNonFreeSlab() {
        FlatSlab flatSlab = new FlatSlab(1l, 5l, new BigDecimal(2l), Uom.BYTE, Uom.BYTE);
        return flatSlab;
    }

    public class ApplyReservationFalseWhen {
        private String packageId= "packageId";
        private Subscription subscription;
        private QuotaReservation quotaReservation;

        public class VersionDetailIsNotSatisfied {

            private MockBasePackage basePackage;

            @Before
            public void setUp() throws OperationFailedException {
                quotaReservation = new QuotaReservation();
                basePackage = MockBasePackage.create(UUID.randomUUID().toString(), UUID.randomUUID().toString()+"name");
                createVersionDetailWithApplicability(false);
                subscription = new Subscription(basePackage.getId(), "test", basePackage.getId(),"productOfferId", null,  null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON,
                        null, null);
            }

            @Test
            public void versionDetailsEmpty() {
                versionDetails.clear();
                assertFalse(dataRateCardVersion.applyReservation(policyContext, keyOne, keyTwo, packageId, subscription, quotaReservation));
            }

            @Test
            public void versionDetailIsNotApplicable() {
                assertFalse(dataRateCardVersion.applyReservation(policyContext, keyOne, keyTwo, packageId, subscription, quotaReservation));
            }
        }

        public class VersionDetailIsApplicableAndSlabIsNotFree {

            @Test
            public void monetaryBalanceDoesNotExist() throws OperationFailedException {
                doReturn(null).when(policyContext).getCurrentMonetaryBalance();
                assertFalse(dataRateCardVersion.applyReservation(policyContext, keyOne, keyTwo, packageId, subscription, quotaReservation));
            }
        }
    }

    public class ApplyReservationTrueWhen {

        public class VersionDetailIsApplicableAndSlabIsNotFree {
            private String packageId= "packageId";
            private Subscription subscription;
            private MockBasePackage basePackage;
            private QuotaReservation quotaReservation;
            private MonetaryBalance monetaryBalance = createMonetaryBalance("test");

            @Before
            public void setUp() throws OperationFailedException {
                addVersionDetailWithApplicabilityAndSlabNotFree(true);
                quotaReservation = new QuotaReservation();
                basePackage = MockBasePackage.create(UUID.randomUUID().toString(), UUID.randomUUID().toString()+"name");
                subscription = new Subscription(basePackage.getId(), "test", basePackage.getId(),"productOfferId", null,  null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON,
                        null, null);

                SubscriberMonetaryBalance subscriberMonetaryBalance = new SubscriberMonetaryBalance(fixedTimeSource);
                subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);
            }

            @Test
            public void monetaryBalanceExists() throws OperationFailedException {
                doReturn(subscriberMonetaryBalance).when(policyContext).getCurrentMonetaryBalance();
                when(subscriberMonetaryBalance.getServiceBalance(anyString())).thenReturn(monetaryBalance);
                System.out.println(monetaryBalance);
                assertTrue(dataRateCardVersion.applyReservation(policyContext, keyOne, keyTwo, packageId, subscription, quotaReservation));

                verify(policyContext, times(1)).getCurrentMonetaryBalance();
            }

            @Test
            public void usableBalanceWithCreditLimitExists() throws OperationFailedException {
                monetaryBalance.setCreditLimit(100);
                monetaryBalance.setAvailBalance(0);
                doReturn(subscriberMonetaryBalance).when(policyContext).getCurrentMonetaryBalance();
                when(subscriberMonetaryBalance.getServiceBalance(anyString())).thenReturn(monetaryBalance);
                assertTrue(dataRateCardVersion.applyReservation(policyContext, keyOne, keyTwo, packageId, subscription, quotaReservation));
                verify(policyContext, times(1)).getCurrentMonetaryBalance();
				assertTrue(policyContext.getPCRFResponse().isQuotaReservationChanged());
			}

            //TODO all calculation related test cases
        }
    }

    public class isApplicableFalse {


        public class VersionDetailIsNotApplicable {
            @Before
            public void setUp() throws OperationFailedException {
                addVersionDetailWithApplicabilityAndSlabIsFree(false, true);
            }

            @Test
            public void versionDetailsEmpty() {
                versionDetails.clear();
                assertFalse(dataRateCardVersion.isApplicable(policyContext, keyOne, keyTwo));
            }

            @Test
            public void versionDetailIsNotApplicable() {
                assertFalse(dataRateCardVersion.isApplicable(policyContext, keyOne, keyTwo));
            }
        }

        public class VersionDetailIsApplicable {
            private MonetaryBalance monetaryBalance = createMonetaryBalance("test");
            private String packageId= "packageId";
            private Subscription subscription;
            private QuotaReservation quotaReservation;
            private MockBasePackage basePackage;

            @Before
            public void setUp() throws OperationFailedException {
                addVersionDetailWithApplicabilityAndSlabIsFree(true, false);
                quotaReservation = new QuotaReservation();
                basePackage = MockBasePackage.create(UUID.randomUUID().toString(), UUID.randomUUID().toString()+"name");

                subscription = new Subscription(basePackage.getId(), "test", basePackage.getId(),"productOfferId", null,  null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON,
                        null, null);

                doReturn(subscriberMonetaryBalance).when(policyContext).getCurrentMonetaryBalance();
            }
            @Test
            public void slabIsNotFreeAndMonetaryBalanceNotFound() throws OperationFailedException {
                assertFalse(dataRateCardVersion.isApplicable(policyContext, keyOne, keyTwo));
            }

            @Test
            public void rateIsZeroForDataRateCardTime() throws OperationFailedException {
                doReturn(subscriberMonetaryBalance).when(policyContext).getCurrentMonetaryBalance();
                when(subscriberMonetaryBalance.getServiceBalance(anyString())).thenReturn(monetaryBalance);
                System.out.println(monetaryBalance);
                when(versionDetails.iterator().next().getSlabs().iterator().next().isFree()).thenReturn(true);
                assertTrue(dataRateCardVersion.applyReservation(policyContext, keyOne, keyTwo, packageId, subscription, quotaReservation));

                verify(policyContext, times(1)).getCurrentMonetaryBalance();
            }

            @Test
            public void rateIsZeroForDataRateCardVolume() throws OperationFailedException {
                doReturn(subscriberMonetaryBalance).when(policyContext).getCurrentMonetaryBalance();
                when(subscriberMonetaryBalance.getServiceBalance(anyString())).thenReturn(monetaryBalance);
                System.out.println(monetaryBalance);
                when(versionDetails.iterator().next().getSlabs().iterator().next().isFree()).thenReturn(true);
                when(versionDetails.iterator().next().getSlabs().iterator().next().isVolumeBasedRateDefined()).thenReturn(true);
                assertTrue(dataRateCardVersion.applyReservation(policyContext, keyOne, keyTwo, packageId, subscription, quotaReservation));

                verify(policyContext, times(1)).getCurrentMonetaryBalance();
            }

            @Test
            public void monetaryBalanceIsNotExist() throws OperationFailedException {
                MonetaryBalance dataMonetaryBalance = createDataMonetaryBalance(0);
                subscriberMonetaryBalance.addMonitoryBalances(dataMonetaryBalance);

                assertFalse(dataRateCardVersion.isApplicable(policyContext, keyOne, keyTwo));
            }

            @Test
            public void creditLimitIsExhausted() throws OperationFailedException {
                MonetaryBalance dataMonetaryBalance = createDataMonetaryBalance(-100);
                dataMonetaryBalance.setCreditLimit(100);
                subscriberMonetaryBalance.addMonitoryBalances(dataMonetaryBalance);

                assertFalse(dataRateCardVersion.isApplicable(policyContext, keyOne, keyTwo));
            }
        }
    }

    private void addVersionDetailWithApplicabilityAndSlabIsFree(boolean isApplicable, boolean IsFreeSlab) {
        VersionDetail versionDetail = createVersionDetailWithApplicability(isApplicable);
        FlatSlab flatSlab = createSlabIsFree(IsFreeSlab);
        doReturn(Arrays.asList(flatSlab)).when(versionDetail).getSlabs();
        versionDetails.add(versionDetail);
    }

    private void addVersionDetailWithApplicabilityAndSlabNotFree(boolean isApplicable) {
        VersionDetail versionDetail = createVersionDetailWithApplicability(isApplicable);
        FlatSlab flatSlab = createNonFreeSlab();
        doReturn(Arrays.asList(flatSlab)).when(versionDetail).getSlabs();
        versionDetails.add(versionDetail);
    }

    private VersionDetail createVersionDetailWithApplicability(boolean isApplicable) {
        VersionDetail versionDetail = mock(VersionDetail.class);
        doReturn(isApplicable).when(versionDetail).isApplicable(any(), any(), any());
        return versionDetail;
    }

    private MonetaryBalance createMonetaryBalance(String subscriberId) {
        int totalBalance = nextInt(100, 1000);
        int availableBalance = nextInt(10, totalBalance);
        return new MonetaryBalance(UUID.randomUUID().toString(),
                subscriberId,
                CommonConstants.MONEY_DATA_SERVICE,
                availableBalance,
                totalBalance,
                0,0, 0,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                UUID.randomUUID().toString(),null,
                System.currentTimeMillis(),
                0,
                "","");
    }
}