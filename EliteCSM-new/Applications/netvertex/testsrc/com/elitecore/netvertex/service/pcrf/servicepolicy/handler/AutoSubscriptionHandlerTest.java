package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.offer.ProductOfferAutoSubscription;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionParameter;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.UnauthorizedActionException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.pm.util.MockProductOffer;
import com.elitecore.netvertex.service.pcrf.DummyPCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.UUID;

import static com.elitecore.netvertex.core.util.Maps.Entry.newEntry;
import static com.elitecore.netvertex.core.util.Maps.newLinkedHashMap;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(HierarchicalContextRunner.class)
public class AutoSubscriptionHandlerTest {

    private AutoSubscriptionHandler autoSubscriptionHandler;
    private DummyPCRFServiceContext pcrfServiceContext;
    private PCRFRequest pcrfRequest;
    private PCRFResponse pcrfResponse;
    private ExecutionContext executionContext;
    @Mock
    private CacheAwareDDFTable cacheAwareDDFTable;
    private FixedTimeSource fixedTimeSource;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        pcrfServiceContext = DummyPCRFServiceContext.spy();
        autoSubscriptionHandler = new AutoSubscriptionHandler(pcrfServiceContext);
        pcrfRequest = new PCRFRequestImpl();
        pcrfResponse = new PCRFResponseImpl();
        executionContext = spy(new ExecutionContext(pcrfRequest, pcrfResponse, cacheAwareDDFTable, "USD"));
        fixedTimeSource = new FixedTimeSource(System.currentTimeMillis());
    }




    public class skipAutoSubscriptionWhen {


        private DummyPolicyRepository policyRepository;
        private SPRInfoImpl sprInfo;
        private String productOfferName = "BaseProductOffer";
        private String addOnProductOfferName = "AddOnProductOffer";
        private MockProductOffer productOffer;
        private MockProductOffer addOnProductOffer;

        @Before
        public void setUp() throws OperationFailedException {
            policyRepository = spy((DummyPolicyRepository)pcrfServiceContext.getServerContext().getPolicyRepository());
            pcrfServiceContext.getServerContext().setPolicyRepository(policyRepository);
            sprInfo = spy(new SPRInfoImpl());
            pcrfRequest.setSPRInfo(sprInfo);
            sprInfo.setProductOffer(productOfferName);
            productOffer = MockProductOffer.create(policyRepository, "Base-" + UUID.randomUUID().toString(), productOfferName);
            addOnProductOffer = MockProductOffer.create(policyRepository, "AddOn-" + UUID.randomUUID().toString(), addOnProductOfferName);
            addOnProductOffer.setPolicyStatus(PolicyStatus.SUCCESS);
            productOffer.addAutoSubscriptions(addOnProductOffer.getId(), addOnProductOffer.getName());
            productOffer.setPolicyStatus(PolicyStatus.SUCCESS);
            policyRepository.addProductOffer(productOffer);
            policyRepository.addProductOffer(addOnProductOffer);
            doReturn(new LinkedHashMap<>()).when(sprInfo).getActiveSubscriptions(anyLong());
        }

        @Test
        public void baseProductOfferNotFoundFromCache() throws OperationFailedException, UnauthorizedActionException {
            ProductOfferStore<ProductOffer> productOfferStore = mock(ProductOfferStore.class);
            doReturn(productOfferStore).when(policyRepository).getProductOffer();
            autoSubscriptionHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verify(productOfferStore).byName(productOfferName);
            checkAutoSubscriptionNotCalled();
        }

        @Test
        public void baseProductOfferNotFoundFromSPR() throws OperationFailedException, UnauthorizedActionException {
            ProductOfferStore<ProductOffer> productOfferStore = mock(ProductOfferStore.class);
            doReturn(productOfferStore).when(policyRepository).getProductOffer();
            sprInfo.setProductOffer(null);
            autoSubscriptionHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verify(productOfferStore, Mockito.times(0)).byName(Mockito.anyString());
            verify(sprInfo, Mockito.times(1)).getProductOffer();
            checkAutoSubscriptionNotCalled();
        }

        @Test
        public void baseProductOfferIsInFailueState() throws OperationFailedException, UnauthorizedActionException {
            productOffer.setPolicyStatus(PolicyStatus.FAILURE);
            Mockito.reset(productOffer);
            autoSubscriptionHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verify(productOffer, Mockito.times(1)).getPolicyStatus();
            checkAutoSubscriptionNotCalled();
        }

        @Test
        public void autoSubscriptionNotAttachedToBaseProductOffer() throws OperationFailedException, UnauthorizedActionException {
            productOffer.removeAutoSubscriptions();
            autoSubscriptionHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verify(productOffer, Mockito.times(1)).getProductOfferAutoSubscriptions();
            checkAutoSubscriptionNotCalled();
        }

        @Test
        public void errorWhileFetchingSubscriptions() throws OperationFailedException, UnauthorizedActionException {
            doThrow(new OperationFailedException("Fail from test")).when(executionContext).getSubscriptions();
            autoSubscriptionHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verify(productOffer, Mockito.times(1)).getProductOfferAutoSubscriptions();
            verify(executionContext, times(1)).getSubscriptions();
            checkAutoSubscriptionNotCalled();
        }

        @Test
        public void subscriberHasActiveSubscriptionOfSameProductOffer() throws OperationFailedException, UnauthorizedActionException {
            Subscription subscription = new Subscription.SubscriptionBuilder().withId(UUID.randomUUID().toString()).withProductOfferId(addOnProductOffer.getId()).build();
            doReturn(newLinkedHashMap(newEntry(subscription.getId(), subscription))).when(sprInfo).getActiveSubscriptions(anyLong());
            autoSubscriptionHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verify(productOffer, Mockito.times(1)).getProductOfferAutoSubscriptions();
            verify(executionContext, times(1)).getSubscriptions();
            checkAutoSubscriptionNotCalled();
        }

        @Test
        public void autoSubscriptionProductOfferIsNotFreeAndSubscriberDoesNotHaveEnoughMonetaryBalance() throws OperationFailedException, UnauthorizedActionException {
            SubscriberMonetaryBalance currentMonetaryBalance = createCurrentNonMonetaryBalance();
            doReturn(currentMonetaryBalance).when(executionContext).getCurrentMonetaryBalance();
            addOnProductOffer.setPrice(1d);
            autoSubscriptionHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verify(productOffer, Mockito.times(1)).getProductOfferAutoSubscriptions();
            verify(executionContext, times(1)).getSubscriptions();
            checkAutoSubscriptionNotCalled();
        }

        @Test
        public void autoSubscriptionProductOfferIsNotFreeAndSubscriberDoesNotHaveEnoughCredit() throws OperationFailedException, UnauthorizedActionException {
            SubscriberMonetaryBalance currentMonetaryBalance = createCreditLimitBalance();
            doReturn(currentMonetaryBalance).when(executionContext).getCurrentMonetaryBalance();
            addOnProductOffer.setPrice(1d);
            autoSubscriptionHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verify(productOffer, Mockito.times(1)).getProductOfferAutoSubscriptions();
            verify(executionContext, times(1)).getSubscriptions();
            checkAutoSubscriptionNotCalled();
        }

        private SubscriberMonetaryBalance createCurrentNonMonetaryBalance() {
            MonetaryBalance monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    null,
                    1.0,
                    100.0,
                    1.0,0,
                    0,
                    System.currentTimeMillis() - 100,
                    System.currentTimeMillis() + 100,
                    "USD",null,
                    System.currentTimeMillis(),
                    0,
                    null,
                    null);
            SubscriberMonetaryBalance currentMonetaryBalance = new SubscriberMonetaryBalance(fixedTimeSource);
            currentMonetaryBalance.addMonitoryBalances(monetaryBalance);
            return currentMonetaryBalance;
        }

        private SubscriberMonetaryBalance createCreditLimitBalance() {
            MonetaryBalance monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    null,
                    -9.111,
                    100,
                    1.0,11, 0,
                    System.currentTimeMillis() - 100,
                    System.currentTimeMillis() + 100,
                    "USD",null,
                    System.currentTimeMillis(), 0,
                    null,
                    null);
            SubscriberMonetaryBalance currentMonetaryBalance = new SubscriberMonetaryBalance(fixedTimeSource);
            currentMonetaryBalance.addMonitoryBalances(monetaryBalance);
            return currentMonetaryBalance;
        }

        @Test
        public void autoSubscriptionProductOfferIsNotFreeAndMonetaryBalanceNotFound() throws OperationFailedException, UnauthorizedActionException {
            SubscriberMonetaryBalance currentMonetaryBalance = new SubscriberMonetaryBalance(fixedTimeSource);
            doReturn(currentMonetaryBalance).when(executionContext).getCurrentMonetaryBalance();
            addOnProductOffer.setPrice(1d);
            autoSubscriptionHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verify(productOffer, Mockito.times(1)).getProductOfferAutoSubscriptions();
            verify(executionContext, times(1)).getSubscriptions();
            checkAutoSubscriptionNotCalled();
        }

        @Test
        public void autoSubscriptionProductOfferIsNotFreeAndErrorOccurredWhileFetchingMonetaryBalance() throws OperationFailedException, UnauthorizedActionException {
            doThrow(new OperationFailedException("Error throw from Test")).when(executionContext).getCurrentMonetaryBalance();
            addOnProductOffer.setPrice(1d);
            autoSubscriptionHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verify(productOffer, Mockito.times(1)).getProductOfferAutoSubscriptions();
            verify(executionContext, times(1)).getSubscriptions();
            checkAutoSubscriptionNotCalled();
        }

        @Test
        public void advanceConditionNotSatisfied() throws OperationFailedException, InvalidExpressionException, UnauthorizedActionException {
            productOffer.addAutoSubscriptions("true=false", addOnProductOffer.getId(), addOnProductOffer.getName());
            addOnProductOffer.setPrice(1d);
            autoSubscriptionHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verify(productOffer, Mockito.times(1)).getProductOfferAutoSubscriptions();
            verify(executionContext, times(1)).getSubscriptions();
            checkAutoSubscriptionNotCalled();
        }

        @Test
        public void errorOccurredWhileSubscribingAutoSubscription() throws OperationFailedException, UnauthorizedActionException {
            doThrow(new OperationFailedException("Error throw from test")).when(cacheAwareDDFTable).autoSubscribeAddOnProductOfferById(any(SubscriptionParameter.class));
            productOffer.addAutoSubscriptions(addOnProductOffer.getId(), addOnProductOffer.getName());
            autoSubscriptionHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verify(productOffer, Mockito.times(1)).getProductOfferAutoSubscriptions();
            verify(executionContext, times(1)).getSubscriptions();
        }

        @Test
        public void autoSubscriptionProductOfferNotFoundFromRepository() throws OperationFailedException {
            ProductOfferAutoSubscription productOfferAutoSubscription = new ProductOfferAutoSubscription(UUID.randomUUID().toString(),
                    null,
                    null,
                    "UnknownId",
                    policyRepository,
                    "UnknownAddOnProductOffer");
            productOffer.addAutoSubscriptions(productOfferAutoSubscription);
            autoSubscriptionHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verify(productOffer, Mockito.times(1)).getProductOfferAutoSubscriptions();
            verify(executionContext, times(1)).getSubscriptions();
        }


        public void checkAutoSubscriptionNotCalled() throws OperationFailedException, UnauthorizedActionException {
            verify(cacheAwareDDFTable, times(0)).autoSubscribeAddOnProductOfferById(any(SubscriptionParameter.class));
        }

        public void checkAutoSubscriptionCalled() throws OperationFailedException, UnauthorizedActionException {
            verify(cacheAwareDDFTable, times(1)).autoSubscribeAddOnProductOfferById(any(SubscriptionParameter.class));
        }
    }


    public class isApplicable {
        private GyServiceUnits usedServiceUnits;

        @Before
        public void setUp() {
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.getVal(), SessionTypeConstant.RO.getVal());
            pcrfRequest.setPCRFEvents(EnumSet.of(PCRFEvent.SESSION_UPDATE));
            MSCC mscc = new MSCC();
            usedServiceUnits = new GyServiceUnits();
            usedServiceUnits.setVolume(RandomUtils.nextLong(1, 1000));
            usedServiceUnits.setTime(RandomUtils.nextLong(1, 1000));
            mscc.setUsedServiceUnits(usedServiceUnits);
            pcrfRequest.setReportedMSCCs(Arrays.asList(mscc));
        }

        public class trueWhen {

            @Test
            public void gyUpdateRequestReceivedWithNonZeroVolumeReported() {
                usedServiceUnits.setTime(0);
                pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.getVal(), SessionTypeConstant.GY.getVal());
                assertTrue(autoSubscriptionHandler.isApplicable(pcrfRequest, pcrfResponse));

            }

            @Test
            public void gyUpdateRequestReceivedWithNonZeroTimeReported() {
                usedServiceUnits.setVolume(0);
                pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.getVal(), SessionTypeConstant.GY.getVal());
                assertTrue(autoSubscriptionHandler.isApplicable(pcrfRequest, pcrfResponse));

            }

            @Test
            public void roUpdateRequestReceivedWithNonZeroVolumeReported() {
                usedServiceUnits.setTime(0);
                assertTrue(autoSubscriptionHandler.isApplicable(pcrfRequest, pcrfResponse));
            }

            @Test
            public void roUpdateRequestReceivedWithNonZeroTimeReported() {
                usedServiceUnits.setVolume(0);
                assertTrue(autoSubscriptionHandler.isApplicable(pcrfRequest, pcrfResponse));
            }
        }

        public class falseWhen {

            private RandomUtils random = new RandomUtils();



            @Test
            public void requestTypeIsNotUpdate() {

                EnumSet<PCRFEvent> pcrfEvents = EnumSet.allOf(PCRFEvent.class);
                pcrfEvents.remove(PCRFEvent.SESSION_UPDATE);
                pcrfRequest.setPCRFEvents(pcrfEvents);
                assertFalse(autoSubscriptionHandler.isApplicable(pcrfRequest, pcrfResponse));

            }

            @Test
            public void sessionTypeIsNotGyAndRo() {

                Arrays.stream(SessionTypeConstant.values()).filter(sessionTypeConstant -> sessionTypeConstant != SessionTypeConstant.RO
                        && sessionTypeConstant != SessionTypeConstant.GY).forEach(sessionTypeConstant -> {
                            pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.getVal(), sessionTypeConstant.getVal());
                            assertFalse(autoSubscriptionHandler.isApplicable(pcrfRequest, pcrfResponse));
                });

            }

            @Test
            public void noReportedMsccFound() {
                pcrfRequest.setReportedMSCCs(null);
                assertFalse(autoSubscriptionHandler.isApplicable(pcrfRequest, pcrfResponse));
            }

            @Test
            public void noReportedMsccFoundWithNonZeroUsageReported() {
                MSCC mscc = new MSCC();
                mscc.setUsedServiceUnits(new GyServiceUnits());
                MSCC mscc2 = new MSCC();
                pcrfRequest.setReportedMSCCs(Arrays.asList(mscc, mscc2));
                assertFalse(autoSubscriptionHandler.isApplicable(pcrfRequest, pcrfResponse));
            }
        }

    }
}