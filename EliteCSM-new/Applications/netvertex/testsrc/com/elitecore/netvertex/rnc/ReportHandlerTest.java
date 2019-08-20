package com.elitecore.netvertex.rnc;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.DailyAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.diameter.gy.RequestedAction;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.service.pcrf.DummyPCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.RandomUtils.nextDouble;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


@RunWith(HierarchicalContextRunner.class)
public class ReportHandlerTest {

    public static final String INR = "INR";
    private DummyPCRFServiceContext serviceContext;
    private ReportHandler reportHandler;
    private PCRFRequestImpl pcrfRequest;
    private PCRFResponseImpl pcrfResponse;
    private ExecutionContext executionContext;
    private String dummyName = "TEST_QUOTA_PROFILE";
    private String quotaProfileId = "TEST_QUOTA_PROFILE";
    @Mock private PolicyRepository policyRepository;
    private long carryForwardVolumeLimit = CommonConstants.QUOTA_UNLIMITED;
    private long carryForwardTimeLimit= CommonConstants.QUOTA_UNLIMITED;

    @Before
    public void setUp() {
        initMocks(this);
        serviceContext = DummyPCRFServiceContext.spy();
        SystemParameterConfiguration systemParameterConfiguration = serviceContext.getServerContext().getServerConfiguration().spySystemParameterConf();
        doReturn("INR").when(systemParameterConfiguration).getSystemCurrency();
        when(serviceContext.getServerContext().getPolicyRepository()).thenReturn(policyRepository);
        reportHandler = spy(new ReportHandler(serviceContext, new QuotaReportHandlerFactory(policyRepository)));
        pcrfRequest = spy(new PCRFRequestImpl(new FixedTimeSource()));
        pcrfResponse = spy(new PCRFResponseImpl());
        executionContext = spy(new ExecutionContext(pcrfRequest, pcrfResponse, mock(CacheAwareDDFTable.class), INR));
    }


    public class Process {

        @Before
        public void setUp() throws OperationFailedException {
            pcrfResponse.setQuotaReservation(new QuotaReservation());
            doReturn(new SubscriberNonMonitoryBalance(null)).when(executionContext).getCurrentNonMonetoryBalance();
            doReturn(new SubscriberMonetaryBalance(new FixedTimeSource(System.currentTimeMillis()))).when(executionContext).getCurrentMonetaryBalance();
        }

        @Test
        public void revenueDetailIsSetForDataPackageInReportSummarywhenQuotaProfileIsPresent()  {
            ReportSummary reportSummary = new ReportSummary();
            ReportedUsageSummary summary = createReportedUsageSummary();
            reportSummary.add(summary);
            QuotaProfile quotaProfile = getQuotaProfile(QuotaUsageType.VOLUME, BalanceLevel.HSQ, carryForwardVolumeLimit, carryForwardTimeLimit);
            doReturn(reportSummary).when(pcrfResponse).getReportSummary();
            doReturn(quotaProfile).when(policyRepository).getQuotaProfile(anyString(), anyString());

            UserPackage userPackage = mock(UserPackage.class);
            doReturn(userPackage).when(policyRepository).getPkgDataById(anyString());
            doReturn("USD").when(userPackage).getCurrency();

            reportHandler.process(pcrfRequest, pcrfResponse, executionContext);
            Assert.assertEquals(reportSummary.getReportedUsageSummaries().get(0).getRevenueCode(),"test");

        }


        @Test
        public void revenueCodeForDataPackageNotSetWhenReportedUsageSummaryLevelIsNotEqualToRncProfileFupLevel() {
            ReportSummary reportSummary = new ReportSummary();
            ReportedUsageSummary summary = createReportedUsageSummary();
            summary.setLevel(2);
            reportSummary.add(summary);
            QuotaProfile quotaProfile = getQuotaProfile(QuotaUsageType.VOLUME, BalanceLevel.HSQ, carryForwardVolumeLimit, carryForwardTimeLimit);
            doReturn(reportSummary).when(pcrfResponse).getReportSummary();
            doReturn(quotaProfile).when(policyRepository).getQuotaProfile(anyString(), anyString());

            UserPackage userPackage = mock(UserPackage.class);
            doReturn(userPackage).when(policyRepository).getPkgDataById(anyString());
            doReturn("USD").when(userPackage).getCurrency();

            reportHandler.process(pcrfRequest, pcrfResponse, executionContext);
            Assert.assertNull(reportSummary.getReportedUsageSummaries().get(0).getRevenueCode());

        }


        @Test
        public void setCurrencyInReportSummaryOfPackageCurrency() {
            ReportSummary reportSummary = new ReportSummary();
            ReportedUsageSummary summary = createReportedUsageSummary();
            summary.setLevel(2);
            reportSummary.add(summary);
          //  QuotaProfile quotaProfile = getQuotaProfile(QuotaUsageType.VOLUME, BalanceLevel.HSQ, carryForwardVolumeLimit, carryForwardTimeLimit);
            doReturn(reportSummary).when(pcrfResponse).getReportSummary();
            //doReturn(quotaProfile).when(policyRepository).getQuotaProfile(anyString(), anyString());

            UserPackage userPackage = mock(UserPackage.class);
            doReturn(userPackage).when(policyRepository).getPkgDataById(anyString());
            doReturn("USD").when(userPackage).getCurrency();

            reportHandler.process(pcrfRequest, pcrfResponse, executionContext);

            Assert.assertNotNull(reportSummary.getReportedUsageSummaries().get(0).getCurrency());
            Assert.assertEquals("USD", reportSummary.getReportedUsageSummaries().get(0).getCurrency());

        }


        @Test
        public void skipProcessingWhenQuotaReservationNotFound() {
            pcrfResponse.setQuotaReservation(null);
            reportHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verifyNoMoreInteractions(executionContext);
            verify(pcrfRequest, times(0)).getReportedMSCCs();
        }

        @Test
        public void skipProcessingWhenExeceptionIsGeneratedWhileFetchingCurrentNonMonetaryBalance() throws OperationFailedException {
            doThrow(new OperationFailedException("from test")).when(executionContext).getCurrentNonMonetoryBalance();
            reportHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verify(executionContext).getCurrentNonMonetoryBalance();
            verify(pcrfRequest, times(0)).getReportedMSCCs();
        }

        @Test
        public void skipProcessingWhenExeceptionIsGeneratedWhileFetchingCurrentMonetaryBalanceWhenUnaccountedQuotaIsMonetary() throws OperationFailedException {
            doThrow(new OperationFailedException("from test")).when(executionContext).getCurrentMonetaryBalance();

            MonetaryBalance monetaryBalance = createMonetaryBalance("");
            MSCC msccQuota = createGrantedMSCC(monetaryBalance);
            msccQuota.getGrantedServiceUnits().setMonetaryBalanceId(monetaryBalance.getId());
            QuotaReservation unAccountedReservation = new QuotaReservation();
            unAccountedReservation.put(msccQuota);
            pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_STOP);
            pcrfResponse.setUnAccountedQuota(unAccountedReservation);
            reportHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verify(executionContext).getCurrentMonetaryBalance();
            verify(pcrfRequest, times(0)).getReportedMSCCs();
            verify(pcrfResponse, times(2)).getUnAccountedQuota();
        }

        @Test
        public void skipProcessingWhenExeceptionIsGeneratedWhileFetchingCurrentMonetaryBalanceWhenQuotaReportedIsOfTypeMonetary() throws OperationFailedException {
            doThrow(new OperationFailedException("from test")).when(executionContext).getCurrentMonetaryBalance();

            MonetaryBalance monetaryBalance = createMonetaryBalance("");
            MSCC msccQuota = createGrantedMSCC(monetaryBalance);
            msccQuota.getGrantedServiceUnits().setMonetaryBalanceId(monetaryBalance.getId());

            List<MSCC> reportedMSCCs = new ArrayList<>();
            reportedMSCCs.add(msccQuota);
            pcrfRequest.setReportedMSCCs(reportedMSCCs);

            QuotaReservation quotaReservation = new QuotaReservation();
            quotaReservation.put(msccQuota);
            pcrfResponse.setQuotaReservation(quotaReservation);

            reportHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verify(executionContext).getCurrentMonetaryBalance();
            verify(pcrfRequest, times(1)).getReportedMSCCs();
        }

        @Test
        public void processDoesNotRemoveQuotaReservationFromResponseWhenUsedServiceUnitsNotReceivedInRequest() throws OperationFailedException {

            MonetaryBalance monetaryBalance = createMonetaryBalance("");
            MSCC msccQuota = createGrantedMSCC(monetaryBalance);
            msccQuota.getGrantedServiceUnits().setMonetaryBalanceId(monetaryBalance.getId());

            List<MSCC> reportedMSCCs = new ArrayList<>();
            msccQuota = msccQuota.copy();
            msccQuota.setUsedServiceUnits(null);
            reportedMSCCs.add(msccQuota);
            pcrfRequest.setReportedMSCCs(reportedMSCCs);

            QuotaReservation quotaReservation = new QuotaReservation();
            quotaReservation.put(msccQuota);
            pcrfResponse.setQuotaReservation(quotaReservation);

            reportHandler.process(pcrfRequest, pcrfResponse, executionContext);
            Assert.assertNotNull(pcrfResponse.getQuotaReservation().get(msccQuota.getRatingGroup()));
        }

    }

    public ReportedUsageSummary createReportedUsageSummary() {
        ReportedUsageSummary summary = new ReportedUsageSummary(0, Arrays.asList(nextLong(1, 10)));
        summary.setCoreSessionId(uuid());
        summary.setQuotaProfile(uuid(), uuid());
        summary.setRateCard(uuid(), uuid());
        summary.setLevel(0);
        summary.setPackageId(uuid(), uuid());
        summary.setProductOfferName(uuid());
        summary.setRequestedAction(RequestedAction.DIRECT_DEBITING.toString());
        summary.setSubscriptionId(uuid());
        summary.setCurrency(uuid());
        summary.setExponent(nextInt(0, 10));
        summary.setReportedTime(nextLong(0, 10));
        summary.setReportedVolume(nextLong(0, 10));
        summary.setReportedEvent(nextLong(0, 10));
        summary.setCurrentUnAccountedVolume(nextLong(0, 10));
        summary.setCurrentUnAccountedTime(nextLong(0, 10));
        summary.setPreviousUnAccountedVolume(nextLong(0,10));
        summary.setPreviousUnAccountedTime(nextLong(0,10));
        summary.setDeductedMonetaryBalance(nextDouble(0,10));
        summary.setDeductedTimeBalance(nextLong(0,10));
        summary.setDeductedVolumeBalance(nextLong(0,10));
        summary.setCalculatedTimePulse(nextLong(0,10));
        summary.setCalculatedVolumePulse(nextLong(0,10));
        summary.setVolumePulse(nextLong(0,10));
        summary.setTimePulse(nextLong(0,10));
        summary.setReportOperation(ReportOperation.FINAL);
        summary.setRateCardName(uuid());
        summary.setDiscount(0);
        summary.setDiscountAmount(0);
        summary.setRateCardGroupName(uuid());
        summary.setSessionStopTime(new Date(System.currentTimeMillis()));
        summary.setTariffType(PCRFKeyValueConstants.TARIFF_TYPE_NORMAL.val);
        return summary;
    }

    private String uuid() {
        return UUID.randomUUID().toString();
    }

    private QuotaProfile getQuotaProfile(QuotaUsageType quotaUsageType, BalanceLevel balanceLevel, long carryForwardVolumeLimit, long carryForwardTimeLimit) {
        String pccProfileName = "pccProfileName";

        List<Map<String, QuotaProfileDetail>> fupLevelserviceWiseQuotaProfileDetais= new ArrayList<>();

        RatingGroup rg = new RatingGroup("RATING_GROUP_1", "RATING_GROUP_1", "RATING_GROUP_1", 0);
        Map<String, QuotaProfileDetail > hsq = new HashMap<>();
        DataServiceType dataServiceType1 = new DataServiceType("DATA_SERVICE_TYPE_1", "Rg1", 1, Collections.emptyList(), Arrays.asList(rg));
        Map<AggregationKey, AllowedUsage> allowedUsageMap = getAllowedUsage();

        hsq.put("DATA_SERVICE_TYPE_1", new RncProfileDetail("hsq_Quota_"+dummyName, dataServiceType1, 0, rg, allowedUsageMap, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null,
                quotaUsageType,null, quotaProfileId, true, pccProfileName, carryForwardVolumeLimit, carryForwardTimeLimit, "test"));
        fupLevelserviceWiseQuotaProfileDetais.add(hsq);
        QuotaProfile quotaProfile = new QuotaProfile("Quota_"+dummyName, dummyName,
                quotaProfileId, balanceLevel,2, RenewalIntervalUnit.MONTH, QuotaProfileType.RnC_BASED,
                fupLevelserviceWiseQuotaProfileDetais, CommonStatusValues.DISABLE.isBooleanValue(), CommonStatusValues.ENABLE.isBooleanValue());

        return quotaProfile;
    }

    private Map<AggregationKey, AllowedUsage> getAllowedUsage(){
        Map<AggregationKey, AllowedUsage> allowedUsageMap = new HashMap<>();

        AllowedUsage daily = new DailyAllowedUsage(1024*30,512*30,512*30,7200*30, DataUnit.MB, DataUnit.MB, DataUnit.MB, TimeUnit.SECOND);
        allowedUsageMap.put(AggregationKey.DAILY, daily);
        return  allowedUsageMap;
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

    private MSCC createGrantedMSCC(MonetaryBalance monetaryBalance) {
        GyServiceUnits grantedServiceUnit = createGSU(monetaryBalance);
        grantedServiceUnit.setMonetaryBalanceId(monetaryBalance.getId());
        grantedServiceUnit.setReservedMonetaryBalance(nextLong(1, Double.valueOf(monetaryBalance.getTotalReservation()).intValue()));
        grantedServiceUnit.setTimePulse(nextInt(2, 5));
        grantedServiceUnit.setVolumePulse(nextInt(2, 5));
        grantedServiceUnit.setRateMinorUnit(100);
        MSCC grantedMSCC = new MSCC();
        grantedMSCC.setGrantedServiceUnits(grantedServiceUnit);
        grantedMSCC.setUsedServiceUnits(grantedServiceUnit);

        return grantedMSCC;
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

    public class  IsApplicable {

        @Test
        public void returnTrueWhenRequestContainsReportedUsgae() {
            PCRFRequestImpl serviceRequest = new PCRFRequestImpl(new FixedTimeSource());
            serviceRequest.setReportedMSCCs(Arrays.asList(new MSCC()));
            serviceRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GY.val);
            serviceRequest.setAttribute(PCRFKeyConstants.CS_SERVICE.val, PCRFKeyValueConstants.DATA_SERVICE_ID.val);
            assertTrue(reportHandler.isApplicable(serviceRequest, new PCRFResponseImpl()));
        }

        @Test
        public void returnTrueWhenSessionTypeIsRadius() {
            PCRFRequestImpl serviceRequest = new PCRFRequestImpl(new FixedTimeSource());
            serviceRequest.setReportedMSCCs(Arrays.asList(new MSCC()));
            serviceRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.RADIUS.val);
            serviceRequest.setAttribute(PCRFKeyConstants.CS_SERVICE.val, PCRFKeyValueConstants.DATA_SERVICE_ID.val);
            assertTrue(reportHandler.isApplicable(serviceRequest, new PCRFResponseImpl()));
        }

        @Test
        public void returnTrueWhenRequestDoesNotContainsReportedUsgae() {
            PCRFRequestImpl serviceRequest = new PCRFRequestImpl(new FixedTimeSource());
            assertFalse(reportHandler.isApplicable(serviceRequest, new PCRFResponseImpl()));
        }

        @Test
        public void returnTrueWhenPCRFEventInRequestContainsSESSION_STOP() {
            HashSet<PCRFEvent> events = new HashSet<PCRFEvent>(1,1);
            events.add(PCRFEvent.SESSION_STOP);
            PCRFRequestImpl serviceRequest = new PCRFRequestImpl(new FixedTimeSource());
            serviceRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GY.val);
            serviceRequest.setAttribute(PCRFKeyConstants.CS_SERVICE.val, PCRFKeyValueConstants.DATA_SERVICE_ID.val);
            serviceRequest.setPCRFEvents(events);
            assertTrue("Request with Session-Stop event should be applicable in report handler",reportHandler.isApplicable(serviceRequest, new PCRFResponseImpl()));
        }

        @Test
        public void returnFalseWhenPCRFEventInRequestContainsSESSION_START() {
            HashSet<PCRFEvent> events = new HashSet<PCRFEvent>(1,1);
            events.add(PCRFEvent.SESSION_START);
            PCRFRequestImpl serviceRequest = new PCRFRequestImpl(new FixedTimeSource());
            serviceRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GY.val);
            serviceRequest.setAttribute(PCRFKeyConstants.CS_SERVICE.val, PCRFKeyValueConstants.DATA_SERVICE_ID.val);
            serviceRequest.setPCRFEvents(events);
            assertFalse("Request with Session-Start event should not be applicable in report handler",reportHandler.isApplicable(serviceRequest, new PCRFResponseImpl()));
        }

    }
}