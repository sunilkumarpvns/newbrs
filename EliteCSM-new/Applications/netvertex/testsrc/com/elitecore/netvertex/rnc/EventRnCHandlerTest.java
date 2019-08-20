package com.elitecore.netvertex.rnc;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.core.systemx.esix.FakeTaskScheduler;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroup;
import com.elitecore.corenetvertex.pm.store.RnCPackageStore;
import com.elitecore.corenetvertex.spr.BalanceProvider;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionMetadata;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.util.SubscriptionUtil;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.pm.BasePackage;
import com.elitecore.netvertex.pm.quota.RoPolicyContextImpl;
import com.elitecore.netvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.DummyPCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class EventRnCHandlerTest {

    private static final String INR = "INR";
    private static final String SUBSCRIBER_ID = UUID.randomUUID().toString();

    private DummyPCRFServiceContext serviceContext;
    private EventRnCHandler eventRnCHandler;
    private PCRFRequest request;
    private PCRFResponse response;
    private ExecutionContext executionContext;
    private FakeTaskScheduler fakeTaskScheduler;
    private Subscription subscription;
    private List<Subscription> nonMonetarySubscriptions;
    private List<Subscription> monetarySubscriptions;
    @Mock private RoPolicyContextImpl roPolicyContext;
    @Mock private IndentingPrintWriter indentingPrintWriter;
    @Mock private SubscriberMonetaryBalance subscriberMonetaryBalance;
    @Mock private PolicyRepository policyRepository;
    RnCPackageStore rncPackageStore = mock(RnCPackageStore.class);

    private String currency="INR";

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        serviceContext = DummyPCRFServiceContext.spy();
        response = new PCRFResponseImpl();

        request = new PCRFRequestImpl();
        SPRInfoImpl sprInfo = (SPRInfoImpl) new SPRInfoImpl.SPRInfoBuilder()
                .withSubscriberIdentity(SUBSCRIBER_ID)
                .build();
        sprInfo.setBalanceProvider(mock(BalanceProvider.class));
        sprInfo.setProductOffer(UUID.randomUUID().toString());
        request.setSPRInfo(sprInfo);
        request.setAttribute(PCRFKeyConstants.CS_SERVICE.val, PCRFKeyValueConstants.VOICE_SERVICE_ID.name());

        request.setAttribute(PCRFKeyConstants.REQUESTED_ACTION.val, PCRFKeyValueConstants.REQUESTED_ACTION_DIRECT_DEBITING.val);
        executionContext = spy(new ExecutionContext(request, response, mock(CacheAwareDDFTable.class), INR));

        request.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, SUBSCRIBER_ID);

        executionContext = spy(new ExecutionContext(request, response, mock(CacheAwareDDFTable.class), INR));
        fakeTaskScheduler = Mockito.spy(new FakeTaskScheduler());
        when(serviceContext.getServerContext().getTaskScheduler()).thenReturn(fakeTaskScheduler);
        eventRnCHandler = new EventRnCHandler(serviceContext);

        nonMonetarySubscriptions = new ArrayList<>();
        monetarySubscriptions = new ArrayList<>();
    }

    @Test
    public void skipEventProessingWhenRequestedServiceIsInActive() {
        eventRnCHandler.process(request, response, executionContext);

        Assert.assertEquals(response.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()), PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
    }

    @Test
    public void rejectRequestIfSubscriberPackageNotFound(){
        eventRnCHandler.process(request, response, executionContext);
        Assert.assertEquals(response.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()), PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
    }

    @Test
    public void basePackageShouldNotBeAppliedIfNonMonetaryAddOnIsApplied() throws OperationFailedException {

        RnCPackage rnCPackage = createNonMonetarySubscription();
        setUpForSubscription();

        eventRnCHandler.applyRoPackages(roPolicyContext, nonMonetarySubscriptions, null, rnCPackage, executionContext);

        verify(rnCPackage, never()).apply(roPolicyContext, roPolicyContext.getReservations(), subscription);
    }

    @Test
    public void addonIsAppliedIfCalledNumberIsPartOfFnFGroup() throws OperationFailedException {

        RnCPackage rnCPackage = createNonMonetarySubscription();
        when(rncPackageStore.byId(rnCPackage.getId())).thenReturn(rnCPackage);
        SubscriptionMetadata metadata =
                SubscriptionUtil.createMetaData("{\"fnFGroup\":{\"name\":\"fnfGroup1\",\"members\":[\"456852154\",\"5684216845\"]}}",null,null);
        doReturn(metadata.getFnFGroup().getName()).when(subscription).getFnFGroupName();
        doReturn(metadata.getFnFGroup().getMembers()).when(subscription).getFnFGroupMembers();
        setUpForSubscription();

        request.setAttribute(PCRFKeyConstants.CS_CALLED_STATION_ID.getVal(),"456852154");

        when(policyRepository.getRnCPackage()).thenReturn(rncPackageStore);
        when(serviceContext.getServerContext().getPolicyRepository()).thenReturn(policyRepository);
        doAnswer(param->{
            param.getArgumentAt(0,RoPolicyContextImpl.class).getPCRFRequest().setAttribute("Result", "SET");
            return true;
        }).when(rnCPackage).apply(any(),any(),any());
        eventRnCHandler.applyRoPackages(roPolicyContext, null, nonMonetarySubscriptions, rnCPackage, executionContext);

        Assert.assertEquals("SET",roPolicyContext.getPCRFRequest().getAttribute("Result"));
    }

    @Test
    public void addonIsNeverAppliedIfCalledNumberIsNotPartOfFnFGroup() throws OperationFailedException {

        RnCPackage rnCPackage = createNonMonetarySubscription();
        when(rncPackageStore.byId(rnCPackage.getId())).thenReturn(rnCPackage);
        SubscriptionMetadata metadata =
                SubscriptionUtil.createMetaData("{\"fnFGroup\":{\"name\":\"fnfGroup1\",\"members\":[]}}",null,null);
        doReturn(metadata.getFnFGroup().getName()).when(subscription).getFnFGroupName();
        doReturn(metadata.getFnFGroup().getMembers()).when(subscription).getFnFGroupMembers();
        setUpForSubscription();

        request.setAttribute(PCRFKeyConstants.CS_CALLED_STATION_ID.getVal(),"4568521545");

        when(policyRepository.getRnCPackage()).thenReturn(rncPackageStore);
        when(serviceContext.getServerContext().getPolicyRepository()).thenReturn(policyRepository);
        eventRnCHandler.applyRoPackages(roPolicyContext, null, nonMonetarySubscriptions, rnCPackage, executionContext);

        verify(rnCPackage, never()).apply(roPolicyContext, roPolicyContext.getReservations(), subscription);
    }

    @Test
    public void monetaryAddonShouldBeApplied() throws OperationFailedException {

        RnCPackage rnCPackage = createNonMonetarySubscription();

        RnCPackage rncMonetaryPackage = createMonetaryAddOnSubscription();

        when(serviceContext.getServerContext().getPolicyRepository()).thenReturn(policyRepository);
        eventRnCHandler = new EventRnCHandler(serviceContext);
        when(policyRepository.getRnCPackage()).thenReturn(rncPackageStore);
        when(rncPackageStore.byId(rncMonetaryPackage.getId())).thenReturn(rncMonetaryPackage);

        eventRnCHandler.applyRoPackages(roPolicyContext, nonMonetarySubscriptions, monetarySubscriptions, rnCPackage, executionContext);
        eventRnCHandler.applyAddOnSubscriptions(monetarySubscriptions, roPolicyContext, executionContext);

        verify(rncMonetaryPackage, atLeastOnce()).apply(roPolicyContext, roPolicyContext.getReservations(), subscription);
    }

    private RnCPackage createMonetaryAddOnSubscription() throws OperationFailedException {
        RnCPackage rncMonetaryPackage = createRnCPackage("test_monetary_adddon", "Monetary Addon", RnCPkgType.MONETARY_ADDON);
        rncMonetaryPackage = Mockito.spy(rncMonetaryPackage);
        setUpForSubscription();
        subscription = spy(new Subscription("test_monetary_adddon", "test", rncMonetaryPackage.getId(),"productOfferId", new Timestamp(System.currentTimeMillis()-java.util.concurrent.TimeUnit.DAYS.toMillis(1)), new Timestamp(System.currentTimeMillis()+java.util.concurrent.TimeUnit.DAYS.toMillis(1)), null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null));
        monetarySubscriptions.add(subscription);
        return rncMonetaryPackage;
    }

    private RnCPackage createNonMonetarySubscription() {
        RnCPackage rnCPackage = createRnCPackage("test_nonmonetary_adddon", "Non Monetary Addon", RnCPkgType.NON_MONETARY_ADDON);
        rnCPackage = Mockito.spy(rnCPackage);
        subscription = spy(new Subscription("test_nonmonetary_adddon", "test", rnCPackage.getId(),
                "productOfferId",
                new Timestamp(System.currentTimeMillis()-java.util.concurrent.TimeUnit.DAYS.toMillis(1)),
                new Timestamp(System.currentTimeMillis()+java.util.concurrent.TimeUnit.DAYS.toMillis(2)),
                null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON,
                null, null));
        nonMonetarySubscriptions.add(subscription);
        return rnCPackage;
    }

    private void setUpForSubscription() throws OperationFailedException {
        BasePackage basePackage = MockBasePackage.create(UUID.randomUUID().toString(), UUID.randomUUID().toString()+"name");
        LinkedHashMap<String, Subscription> subscriptionMap = new LinkedHashMap<>();
        subscription = spy(new Subscription("test", "test", basePackage.getId(),"productOfferId", null, null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null));
        Mockito.doReturn(subscriptionMap).when(roPolicyContext).getSubscriptions();
        Mockito.doReturn(indentingPrintWriter).when(roPolicyContext).getTraceWriter();
        Mockito.doReturn(subscriberMonetaryBalance).when(roPolicyContext).getCurrentMonetaryBalance();
        request.setAttribute(PCRFKeyConstants.CS_SERVICE.val, PCRFKeyValueConstants.VOICE_SERVICE_ID.name());
        Mockito.doReturn(request).when(roPolicyContext).getPCRFRequest();
    }

    private RnCPackage createRnCPackage(String id, String name, RnCPkgType type){
        List< RateCardGroup > rateCardGroups = new ArrayList<>();
        rateCardGroups.add(null);

        return new RnCPackage(id, name, null,
                null, rateCardGroups, null,
                null, type,
                null, null, null,
                null, null, ChargingType.SESSION,currency);
    }
}
