package com.elitecore.netvertex.pm;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.QCI;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.pm.IPCanQoSFactory;
import com.elitecore.corenetvertex.pm.constants.SelectionResult;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.PCCRuleImpl;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.util.Maps;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import com.elitecore.netvertex.usagemetering.ServiceUnit;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class UMBaseQoSProfileDetailTest {

    private final String INR = "INR";
    private PolicyContext policyContext;
    private QoSInformation qoSInformation;
    private UMBaseQoSProfileDetail detail;
    private List<PCCRule> pccRules;
    private List<ChargingRuleBaseName> chargingRuleBaseNames;
    private Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail;
    private UMBaseQuotaProfileDetail allServiceQuotaProfileDetail;
    private UMBaseQuotaProfileDetail quotaProfileDetail1;
    private UMBaseQuotaProfileDetail quotaProfileDetail2;
    private DummyUsageProvider dummyUsageProvider;

    @Before
    public void setUp() {
        dummyUsageProvider = spy(new DummyUsageProvider());
        qoSInformation = new QoSInformation();
        PCRFRequest pcrfRequest = new PCRFRequestImpl();
        PCRFResponse pcrfResponse = new PCRFResponseImpl();
        policyContext = spy(new PCRFPolicyContextImpl(pcrfRequest, pcrfResponse,
                new MockBasePackage(),
                new ExecutionContext(pcrfRequest, pcrfResponse, CacheAwareDDFTable.getInstance(), INR),
                new PCRFQoSProcessor(qoSInformation), new DummyPolicyRepository()));


        PCCRuleImpl pccRule1 = new PCCRuleImpl.PCCRuleBuilder("pcc1", "name1").withServiceIdentifier(1).withServiceTypeId("1").withQci(QCI.QCI_GBR_1).build();
        PCCRuleImpl pccRule2 = new PCCRuleImpl.PCCRuleBuilder("pcc2", "name2").withServiceIdentifier(2).withServiceTypeId("2").withQci(QCI.QCI_GBR_1).build();
        pccRules = new ArrayList<>();
        pccRules.add(pccRule1);
        pccRules.add(pccRule2);

        allServiceQuotaProfileDetail = QuotaProfileDetailDataFactory.createQuotaProfileDetailWithRandomUsageFor(
                "testQuotaProfileId",
                "testServiceId"
        );

        quotaProfileDetail1 = QuotaProfileDetailDataFactory.createQuotaProfileDetailWithRandomUsageFor("q1","s1");
        quotaProfileDetail2 = QuotaProfileDetailDataFactory.createQuotaProfileDetailWithRandomUsageFor("q2","s2");
        serviceToQuotaProfileDetail = new HashMap<>();
        serviceToQuotaProfileDetail.put("1",quotaProfileDetail1);
        serviceToQuotaProfileDetail.put("2",quotaProfileDetail2);

        chargingRuleBaseNames = new ArrayList<>();
        chargingRuleBaseNames.add(createCRBN());
        chargingRuleBaseNames.add(createCRBN());

        qoSInformation.startPackageQoSSelection(new MockBasePackage());
    }

    public class applyReturnsFullyAppliedWhen {

        @Test
        public void qosProfileActionIsSetToReject() {

            detail = getUMBaseQoSProfileDetail(QoSProfileAction.REJECT, 0, null, null,
                    IPCanQoSFactory.randomQoS(), null, null, dummyUsageProvider, false);

            SelectionResult selectionResult = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

            assertSame(SelectionResult.FULLY_APPLIED, selectionResult);
        }

        @Test
        public void quotaProfileDetailIsNotAvailable() {

            detail = getUMBaseQoSProfileDetail(QoSProfileAction.ACCEPT, 0, null, null,
                    IPCanQoSFactory.randomQoS(), null, null, dummyUsageProvider, false);

            SelectionResult selectionResult = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

            assertSame(SelectionResult.FULLY_APPLIED, selectionResult);
        }

        @Test
        public void currentUsageIsNull() throws OperationFailedException {

            Map<String, SubscriberUsage> subscriberUsageMap = new HashMap<>();
            doReturn(subscriberUsageMap).when(dummyUsageProvider).getCurrentUsage(policyContext,qoSInformation);

            detail = getUMBaseQoSProfileDetail(QoSProfileAction.ACCEPT, 0, allServiceQuotaProfileDetail, serviceToQuotaProfileDetail,
                    IPCanQoSFactory.randomQoS(), pccRules, null, dummyUsageProvider, false);

            SelectionResult selectionResult = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

            assertSame(SelectionResult.FULLY_APPLIED, selectionResult);
        }

        @Test
        public void allServiceQuotaProfileDetailisAvailableWithNoPCCRuleAndNoCRBN() {

            detail = getUMBaseQoSProfileDetail(QoSProfileAction.ACCEPT, 0, allServiceQuotaProfileDetail, null,
                    IPCanQoSFactory.randomQoS(), null, null, dummyUsageProvider, true);

            SelectionResult selectionResult = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

            assertSame(SelectionResult.FULLY_APPLIED, selectionResult);
        }

        @Test
        public void allThePCCRulesAreSatisfied() {

            detail = getUMBaseQoSProfileDetail(QoSProfileAction.ACCEPT, 0, allServiceQuotaProfileDetail, serviceToQuotaProfileDetail,
                    IPCanQoSFactory.randomQoS(), pccRules, null, dummyUsageProvider, true);

            SelectionResult selectionResult = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

            assertSame(SelectionResult.FULLY_APPLIED, selectionResult);
        }

    }

    public class applyReturnsPartiallyAppliedWhen {

        @Test
        public void someOfThePCCRulesAreSatisfied() throws OperationFailedException {

            Map<String, SubscriberUsage> subscriberUsageMap = new HashMap<>();
            subscriberUsageMap.put("q2:s2",getExpectedSubscriberUsage("q2","s2", quotaProfileDetail1));

            doReturn(subscriberUsageMap).when(dummyUsageProvider).getCurrentUsage(policyContext,qoSInformation);

            detail = getUMBaseQoSProfileDetail(QoSProfileAction.ACCEPT, 0, allServiceQuotaProfileDetail, serviceToQuotaProfileDetail,
                    IPCanQoSFactory.randomQoS(), pccRules, null, dummyUsageProvider, true);

            SelectionResult selectionResult = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

            assertSame(SelectionResult.PARTIALLY_APPLIED, selectionResult);
        }

    }

    public class applyReturnsNotAppliedWhen {

        @Test
        public void noneOfThePCCRulesAreSatisfied() throws OperationFailedException {

            Map<String, SubscriberUsage> subscriberUsageMap = new HashMap<>();
            subscriberUsageMap.put("q1:s1",getExpectedSubscriberUsage("q1","s1", quotaProfileDetail1));
            subscriberUsageMap.put("q2:s2",getExpectedSubscriberUsage("q2","s2", quotaProfileDetail1));

            doReturn(subscriberUsageMap).when(dummyUsageProvider).getCurrentUsage(policyContext,qoSInformation);

            detail = getUMBaseQoSProfileDetail(QoSProfileAction.ACCEPT, 0, allServiceQuotaProfileDetail, serviceToQuotaProfileDetail,
                    IPCanQoSFactory.randomQoS(), pccRules, null, dummyUsageProvider, true);

            SelectionResult selectionResult = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

            assertSame(SelectionResult.NOT_APPLIED, selectionResult);
        }

        @Test
        public void operationFailsToPerform() throws OperationFailedException {

            SPRInfo sprInfo = spy(new SPRInfoImpl());
            OperationFailedException usageException = spy(new OperationFailedException("Test Usage Exception"));

            doThrow(OperationFailedException.class).when(dummyUsageProvider).getCurrentUsage(policyContext,qoSInformation);
            when(policyContext.getSPInfo()).thenReturn(sprInfo);
            when(sprInfo.getSubscriberIdentity()).thenReturn("testSubscriber");
            qoSInformation.setUsageException(usageException);

            detail = getUMBaseQoSProfileDetail(QoSProfileAction.ACCEPT, 0, allServiceQuotaProfileDetail, serviceToQuotaProfileDetail,
                    IPCanQoSFactory.randomQoS(), pccRules, null, dummyUsageProvider, true);

            SelectionResult selectionResult = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

            assertSame(SelectionResult.NOT_APPLIED, selectionResult);
        }

        @Test
        public void usageNotFound() throws OperationFailedException {

            SPRInfo sprInfo = spy(new SPRInfoImpl());

            doThrow(OperationFailedException.class).when(dummyUsageProvider).getCurrentUsage(policyContext,qoSInformation);
            when(policyContext.getSPInfo()).thenReturn(sprInfo);
            when(sprInfo.getSubscriberIdentity()).thenReturn("testSubscriber");

            Map<String, SubscriberUsage> subscriberUsageMap = new HashMap<>();

            doReturn(subscriberUsageMap).when(dummyUsageProvider).getCurrentUsage(policyContext,qoSInformation);

            detail = getUMBaseQoSProfileDetail(QoSProfileAction.ACCEPT, 0, allServiceQuotaProfileDetail, serviceToQuotaProfileDetail,
                    IPCanQoSFactory.randomQoS(), pccRules, null, dummyUsageProvider, true);

            SelectionResult selectionResult = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

            assertSame(SelectionResult.NOT_APPLIED, selectionResult);
        }

    }

    public class crbnIsProcessedWhen{

        @Test
        public void previousQoSProfileDetailIsNotAvailable() {

            qoSInformation.setQoSProfileDetail(null);

            detail = getUMBaseQoSProfileDetail(QoSProfileAction.ACCEPT, 0, allServiceQuotaProfileDetail, serviceToQuotaProfileDetail,
                    IPCanQoSFactory.randomQoS(), pccRules, chargingRuleBaseNames, dummyUsageProvider, true);

            SelectionResult selectionResult = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

            assertFalse(qoSInformation.getPccBalanceMap().isEmpty());
            assertSame(SelectionResult.FULLY_APPLIED, selectionResult);
        }

    }

    private SubscriberUsage getExpectedSubscriberUsage(String quotaProfileId, String serviceId, UMBaseQuotaProfileDetail quotaProfileDetail){
        SubscriberUsage subscriberUsage = new SubscriberUsage(SubscriberUsage.NEW_ID,
                quotaProfileId,
                "testSubscriberId",
                serviceId,
                "testSubscriptionId",
                "testPackageId","testProductOfferId");

        int VOLUME_TO_EXHAUST_BALANCE = 1000;
        subscriberUsage.setBillingCycleUpload(quotaProfileDetail.getBillingCycleAllowedUsage().getUpload()+VOLUME_TO_EXHAUST_BALANCE);
        subscriberUsage.setBillingCycleDownload(quotaProfileDetail.getBillingCycleAllowedUsage().getDownload()+VOLUME_TO_EXHAUST_BALANCE);
        subscriberUsage.setBillingCycleTotal(quotaProfileDetail.getBillingCycleAllowedUsage().getTotal()+VOLUME_TO_EXHAUST_BALANCE);
        subscriberUsage.setBillingCycleTime(quotaProfileDetail.getBillingCycleAllowedUsage().getTime()+VOLUME_TO_EXHAUST_BALANCE);
        return subscriberUsage;
    }

    private UMBaseQoSProfileDetail getUMBaseQoSProfileDetail(QoSProfileAction qoSProfileAction,
                                                             int fupLevel,
                                                             UMBaseQuotaProfileDetail allServiceQuotaProfileDetail,
                                                             Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail,
                                                             IPCANQoS sessionQos, List<PCCRule> pccRules, List<ChargingRuleBaseName> crbns,
                                                             DummyUsageProvider dummyUsageProvider, boolean isUsageRequired) {
        String name = "umBaseQoSProfileName";
        String pkgName = "umBaseDataPackage";
        String rejectReason = "Reject Reason";
        return new UMBaseQoSProfileDetail(name,
                pkgName,
                qoSProfileAction,
                rejectReason,
                fupLevel,
                allServiceQuotaProfileDetail,
                serviceToQuotaProfileDetail,
                isUsageRequired,
                sessionQos,
                pccRules,
                true,
                new SliceInformation(100,50,50,60),
                0,
                false,
                dummyUsageProvider,
                null,
                crbns);
    }

    private class DummyUsageProvider implements QoSProfileDetail.UsageProvider{

        @Nullable
        @Override
        public Map<String, SubscriberUsage> getCurrentUsage(PolicyContext policyContext, QoSInformation qosInformation) throws OperationFailedException {
            ServiceUnit existingUnit = new ServiceUnit(0, 5, 15, 20);
            ServiceUnit reportedUnit = new ServiceUnit(0, 5, 15, 20);
            SubscriberUsage subscriberUsage = new SubscriberUsage.SubscriberUsageBuilder("subUsageId",
                    "test",
                    "0",
                    "testQuotaProfileId",
                    "testPackageId",
                    "testProductOfferId").withSubscriptionId("testSubscriptionId")
                    .withAllTypeUsage(existingUnit.getTotalOctets() + reportedUnit.getTotalOctets(), existingUnit.getOutputOctets()
                            + reportedUnit.getOutputOctets(), existingUnit.getInputOctets() + reportedUnit.getInputOctets(), existingUnit.getTime()
                            + reportedUnit.getTime())
                    .build();

            Map subscriberUsageMap = new HashMap();
            subscriberUsageMap.put("q1:s1",subscriberUsage);
            subscriberUsageMap.put("q2:s2",subscriberUsage);
            return subscriberUsageMap;
        }
    }

    public ChargingRuleBaseName createCRBN() {
        DataServiceType dataServiceType = new DataServiceType(CommonConstants.ALL_SERVICE_ID,
                CommonConstants.ALL_SERVICE_NAME,
                1,
                null,
                null);

        return new ChargingRuleBaseName(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                Maps.newLinkedHashMap(Maps.Entry.newEntry("mk1", dataServiceType)),
                0,
                new HashMap<String, SliceInformation>());
    }
}
