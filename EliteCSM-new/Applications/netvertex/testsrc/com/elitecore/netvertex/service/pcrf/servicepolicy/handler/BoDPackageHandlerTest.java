package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.bod.BoDQosMultiplier;
import com.elitecore.corenetvertex.pm.bod.BoDServiceMultiplier;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.PCCRuleImpl;
import com.elitecore.corenetvertex.pm.store.BoDPackageStore;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.pm.FinalQoSSelectionData;
import com.elitecore.netvertex.pm.PCRFPolicyContextImpl;
import com.elitecore.netvertex.pm.PCRFQoSProcessor;
import com.elitecore.netvertex.pm.PackageType;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.QoSInformation;
import com.elitecore.netvertex.pm.QoSProcessor;
import com.elitecore.netvertex.pm.QoSProfile;
import com.elitecore.netvertex.pm.QoSProfileDetail;
import com.elitecore.netvertex.pm.UMBaseQoSProfileDetail;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.DummyPCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BoDPackageHandlerTest {
    private BoDPackageHandler boDPackageHandler;
    private PCRFServiceContext serviceContext;
    private PolicyContext pcrfPolicyContext;
    private PCRFRequest request;
    private PCRFResponse response;
    private ExecutionContext executionContext;
    private MockBasePackage basePackage;
    private QoSProcessor qoSProcessor;
    private PolicyRepository policyRepository;
    private FinalQoSSelectionData finalQoSSelectionData;
    private PCCRule pccRule;
    private BoDQosMultiplier boDQosMultiplier;
    private QoSProfile qoSProfile;
    private BoDPackageStore boDPackageStore;
    private Subscription subscription;
    private QoSProfileDetail qoSProfileDetail;
    private Map<Integer, BoDQosMultiplier> fupLevelToBoDQosMultipliers;
    private static final String PCC_RULE_ID = UUID.randomUUID().toString();
    private static final String PCC_RULE_NAME = UUID.randomUUID().toString();
    private static final String PACKAGE_ID = UUID.randomUUID().toString();
    public static final String SUBSCRIBER_ID = "TEST";
    public static final String INR = "INR";

    @Before
    public void setUp(){
        serviceContext = DummyPCRFServiceContext.spy();

        request = new PCRFRequestImpl();
        response = new PCRFResponseImpl();
        basePackage = MockBasePackage.create(PACKAGE_ID, PACKAGE_ID + "name");
        request.setSPRInfo(spy((SPRInfoImpl) new SPRInfoImpl.SPRInfoBuilder()
                .withSubscriberIdentity(SUBSCRIBER_ID)
                .withProductOffer(basePackage.getName())
                .build()));


        executionContext = spy(new ExecutionContext(request, response, mock(CacheAwareDDFTable.class), INR));
        QoSInformation qoSInformation = new QoSInformation();
        qoSProcessor = spy(new PCRFQoSProcessor(qoSInformation));
        policyRepository = mock(PolicyRepository.class);
        pcrfPolicyContext = spy(new PCRFPolicyContextImpl(request, response, basePackage, executionContext, qoSProcessor, policyRepository));

        Random random = new Random();
        pccRule = new PCCRuleImpl(PCC_RULE_ID, PCC_RULE_NAME);
        pccRule.setGBRDL(random.nextInt());
        pccRule.setMBRDL(random.nextInt());
        pccRule.setGBRUL(random.nextInt());
        pccRule.setMBRUL(random.nextInt());
        qoSProfile = new QoSProfile(PCC_RULE_ID, PCC_RULE_NAME, PACKAGE_ID, PACKAGE_ID, null, null, null,
                0, null, null, null, null, null);
        qoSProfileDetail = new UMBaseQoSProfileDetail(PCC_RULE_NAME, PACKAGE_ID + "name", QoSProfileAction.ACCEPT, null, null, null, false, 0,
                0, false, null, null);
        finalQoSSelectionData = spy(new FinalQoSSelectionData(PackageType.BASE));
        finalQoSSelectionData.setQosProfileDetail(qoSProfileDetail, PACKAGE_ID, qoSProfile);
        finalQoSSelectionData.add(pccRule, PACKAGE_ID, qoSProfile);

        subscription = new Subscription(PCC_RULE_ID, SUBSCRIBER_ID, PACKAGE_ID, null, new Timestamp(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000l), new Timestamp(System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000l), SubscriptionState.STARTED, 0, SubscriptionType.BOD, null, null);
        LinkedHashMap<String, Subscription> subscriptions = new LinkedHashMap<>(1);
        subscriptions.put(subscription.getId(), subscription);
        response.setSubscriptions(subscriptions);

        boDQosMultiplier = new BoDQosMultiplier(2.0);
        fupLevelToBoDQosMultipliers = new HashMap<>(1);
        fupLevelToBoDQosMultipliers.put(0, boDQosMultiplier);

        BoDPackage boDPackageWithApplicableQoS = spy(new BoDPackage(PCC_RULE_ID, PCC_RULE_NAME, null, PkgMode.LIVE
                , PkgStatus.ACTIVE, 30, ValidityPeriodUnit.DAY, Arrays.asList(qoSProfile.getName())
                , fupLevelToBoDQosMultipliers, null, null, PolicyStatus.SUCCESS, null
                , null, null, null, null));
        boDPackageStore = mock(BoDPackageStore.class);
        when(policyRepository.getBoDPackage()).thenReturn(boDPackageStore);
        when(boDPackageStore.byId(subscription.getPackageId())).thenReturn(boDPackageWithApplicableQoS);
        boDPackageHandler = new BoDPackageHandler();
    }

    @Test
    public void bodPackageAppliedWithSessionMultiplier(){
        boDPackageHandler.applyBoDPackage(pcrfPolicyContext, finalQoSSelectionData);
        Assert.assertEquals((long) (pccRule.getGBRDL() * boDQosMultiplier.getSessionMultiplier()), finalQoSSelectionData.getPccRuleToQoSProfile().entrySet().iterator().next().getKey().getGBRDL());
        Assert.assertEquals((long) (pccRule.getGBRUL() * boDQosMultiplier.getSessionMultiplier()), finalQoSSelectionData.getPccRuleToQoSProfile().entrySet().iterator().next().getKey().getGBRUL());
        Assert.assertEquals((long) (pccRule.getMBRDL() * boDQosMultiplier.getSessionMultiplier()), finalQoSSelectionData.getPccRuleToQoSProfile().entrySet().iterator().next().getKey().getMBRDL());
        Assert.assertEquals((long) (pccRule.getMBRUL() * boDQosMultiplier.getSessionMultiplier()), finalQoSSelectionData.getPccRuleToQoSProfile().entrySet().iterator().next().getKey().getMBRUL());
    }

    @Test
    public void bodPackageAppliedWithServiceMultiplier(){
        boDQosMultiplier.addDataServiceIdToServiceMultiplier(0l,
                new BoDServiceMultiplier("SERVICE_TYPE_1","All Service",
                        3.0,1));
        boDPackageHandler.applyBoDPackage(pcrfPolicyContext, finalQoSSelectionData);
        Assert.assertEquals((long) (pccRule.getGBRDL() * boDQosMultiplier.getDataServiceIdToServiceMultipliers().get(0l).getMultiplier()), finalQoSSelectionData.getPccRuleToQoSProfile().entrySet().iterator().next().getKey().getGBRDL());
        Assert.assertEquals((long) (pccRule.getGBRUL() * boDQosMultiplier.getDataServiceIdToServiceMultipliers().get(0l).getMultiplier()), finalQoSSelectionData.getPccRuleToQoSProfile().entrySet().iterator().next().getKey().getGBRUL());
        Assert.assertEquals((long) (pccRule.getMBRDL() * boDQosMultiplier.getDataServiceIdToServiceMultipliers().get(0l).getMultiplier()), finalQoSSelectionData.getPccRuleToQoSProfile().entrySet().iterator().next().getKey().getMBRDL());
        Assert.assertEquals((long) (pccRule.getMBRUL() * boDQosMultiplier.getDataServiceIdToServiceMultipliers().get(0l).getMultiplier()), finalQoSSelectionData.getPccRuleToQoSProfile().entrySet().iterator().next().getKey().getMBRUL());
    }

    @Test
    public void bodPackageAppliedWithEmptyQoSProfile(){
        BoDPackage boDPackageWithEmptyQoS = spy(new BoDPackage(PCC_RULE_ID, PCC_RULE_NAME, null, PkgMode.LIVE
                , PkgStatus.ACTIVE, 30, ValidityPeriodUnit.DAY, null
                , fupLevelToBoDQosMultipliers, null, null, PolicyStatus.SUCCESS, null
                , null, null, null, null));
        when(boDPackageStore.byId(subscription.getPackageId())).thenReturn(boDPackageWithEmptyQoS);

        boDPackageHandler.applyBoDPackage(pcrfPolicyContext, finalQoSSelectionData);
        Assert.assertEquals((long) (pccRule.getGBRDL() * boDQosMultiplier.getSessionMultiplier()), finalQoSSelectionData.getPccRuleToQoSProfile().entrySet().iterator().next().getKey().getGBRDL());
        Assert.assertEquals((long) (pccRule.getGBRUL() * boDQosMultiplier.getSessionMultiplier()), finalQoSSelectionData.getPccRuleToQoSProfile().entrySet().iterator().next().getKey().getGBRUL());
        Assert.assertEquals((long) (pccRule.getMBRDL() * boDQosMultiplier.getSessionMultiplier()), finalQoSSelectionData.getPccRuleToQoSProfile().entrySet().iterator().next().getKey().getMBRDL());
        Assert.assertEquals((long) (pccRule.getMBRUL() * boDQosMultiplier.getSessionMultiplier()), finalQoSSelectionData.getPccRuleToQoSProfile().entrySet().iterator().next().getKey().getMBRUL());
    }

    @Test
    public void bodPackageNotAppliedAsBasePackageIsNotApplied(){
        qoSProfile = new QoSProfile(PCC_RULE_ID, PCC_RULE_NAME, SUBSCRIBER_ID, SUBSCRIBER_ID, null, null, null,
                0, null, null, null, null, null);
        finalQoSSelectionData.setQosProfileDetail(null, PACKAGE_ID, qoSProfile);
        boDPackageHandler.applyBoDPackage(pcrfPolicyContext, finalQoSSelectionData);
        verify(pcrfPolicyContext, never()).getBoDPkgSubscriptions();
    }

    @Test
    public void bodPackageNotAppliedAsNoPCCRuleApplied(){
        finalQoSSelectionData.getPccRuleToQoSProfile().remove(pccRule);
        boDPackageHandler.applyBoDPackage(pcrfPolicyContext, finalQoSSelectionData);
        verify(pcrfPolicyContext, never()).getBoDPkgSubscriptions();
    }

    @Test
    public void bodPackageNotAppliedAsNoFinalQosIsSelected(){
        boDPackageHandler.applyBoDPackage(pcrfPolicyContext, null);
        verify(pcrfPolicyContext, never()).getBoDPkgSubscriptions();
    }

    @Test
    public void bodPackageNotAppliedAsBoDSubscriptionsNotFound() throws OperationFailedException {
        when(executionContext.getSubscriptions()).thenReturn(null);
        boDPackageHandler.applyBoDPackage(pcrfPolicyContext, finalQoSSelectionData);
        verify(finalQoSSelectionData, atMost(1)).add(pccRule, PACKAGE_ID, qoSProfile);
    }

    @Test
    public void bodPackageNotAppliedAsFutureBoDSubscriptionsFound() {
        Subscription subscription = new Subscription(PCC_RULE_ID, SUBSCRIBER_ID, PACKAGE_ID, null, new Timestamp(System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000l), new Timestamp(System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000l), SubscriptionState.SUBSCRIBED, 0, SubscriptionType.BOD, null, null);
        LinkedHashMap<String, Subscription> subscriptions = new LinkedHashMap<>(1);
        subscriptions.put(subscription.getId(), subscription);
        response.setSubscriptions(subscriptions);

        boDPackageHandler.applyBoDPackage(pcrfPolicyContext, finalQoSSelectionData);
        verify(finalQoSSelectionData, atMost(1)).add(pccRule, PACKAGE_ID, qoSProfile);
        Assert.assertEquals(subscription.getStartTime(), pcrfPolicyContext.getRevalidationTime());
    }

    @Test
    public void bodPackageNotAppliedAsFUPLevelIsNotConfigured() {
        qoSProfileDetail = new UMBaseQoSProfileDetail(PCC_RULE_NAME, PACKAGE_ID + "name", QoSProfileAction.ACCEPT, null, null, null, false, 1,
                0, false, null, null);
        finalQoSSelectionData.setQosProfileDetail(qoSProfileDetail, PACKAGE_ID, qoSProfile);
        boDPackageHandler.applyBoDPackage(pcrfPolicyContext, finalQoSSelectionData);
        verify(finalQoSSelectionData, atMost(1)).add(pccRule, PACKAGE_ID, qoSProfile);
    }
}
