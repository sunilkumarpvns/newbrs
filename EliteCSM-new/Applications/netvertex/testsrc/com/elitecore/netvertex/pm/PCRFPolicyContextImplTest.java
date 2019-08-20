package com.elitecore.netvertex.pm;

import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.BalanceProvider;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriptionNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.ddf.DDFTable;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PCRFPolicyContextImplTest {
    public static final String INR = "INR";
    private PCRFRequest request;
    private PCRFResponse response;
    private ExecutionContext executionContext;
    private MockBasePackage basePackage;
    private PCRFPolicyContextImpl pcrfPolicyContext;
    private QoSProfile qoSProfile;
    private QoSProcessor qoSProcessor;
    private DDFTable ddfTable;
    private SPRInfoImpl sprInfo;
    private SubscriberNonMonitoryBalance subscriberNonMonitoryBalance;
    private SubscriptionNonMonitoryBalance subscriptionNonMonitoryBalance;
    private PolicyRepository policyRepository;
    private ProductOffer productOffer;
    private ProductOfferStore productOfferStore;

    @Before
    public void setUp() throws OperationFailedException {
        request = new PCRFRequestImpl();
        response = new PCRFResponseImpl();
        basePackage = MockBasePackage.create(UUID.randomUUID().toString(), UUID.randomUUID().toString()+"name");

        sprInfo = spy((SPRInfoImpl) new SPRInfoImpl.SPRInfoBuilder()
                .withSubscriberIdentity("test")
                .withProductOffer(basePackage.getName())
                .build());

        sprInfo.setBalanceProvider(mock(BalanceProvider.class));
        request.setSPRInfo(sprInfo);

        QoSInformation qoSInformation = new QoSInformation();
        qoSProcessor = spy(new PCRFQoSProcessor(qoSInformation));

        ddfTable = mock(CacheAwareDDFTable.class);
        executionContext = spy(new ExecutionContext(request, response, mock(CacheAwareDDFTable.class), INR));
        doReturn(ddfTable).when(executionContext).getDDFTable();
        pcrfPolicyContext = spy(new PCRFPolicyContextImpl(request, response, basePackage, executionContext, qoSProcessor, policyRepository));
        subscriberNonMonitoryBalance = spy(new SubscriberNonMonitoryBalance(new ArrayList<>()));
        subscriptionNonMonitoryBalance = spy(new SubscriptionNonMonitoryBalance(basePackage.getName()));
        policyRepository = mock(PolicyRepository.class);
        productOfferStore = spy(new ProductOfferStore());
        productOffer = mock(ProductOffer.class);
        when(policyRepository.getProductOffer()).thenReturn(productOfferStore);
        doReturn(policyRepository).when(pcrfPolicyContext).getPolicyRepository();
        qoSProfile=mock(QoSProfile.class);
        doReturn(subscriberNonMonitoryBalance).when(pcrfPolicyContext).getCurrentBalance();
    }

    @Test
    public void processDoesNotAddBalanceForKnownSubscriber() throws OperationFailedException{
        doReturn(true).when(qoSProcessor).process(any(QoSProfile.class), any(BasePolicyContext.class),
                any(UserPackage.class), any(Subscription.class));
        pcrfPolicyContext.process(qoSProfile,basePackage,null);
        verify(ddfTable,times(0)).addDataRnCBalance(any(String.class),any(Subscription.class),any(ProductOffer.class));
    }

    @Test
    public void processDoesNotAddBalanceForUnKnownSubscriberAndUMPackage() throws OperationFailedException{
        doReturn(true).when(qoSProcessor).process(any(QoSProfile.class), any(BasePolicyContext.class),
                any(UserPackage.class), any(Subscription.class));
        doReturn(true).when(sprInfo).isUnknownUser();
        doReturn(QuotaProfileType.USAGE_METERING_BASED).when(basePackage).getQuotaProfileType();
        pcrfPolicyContext.process(qoSProfile,basePackage,null);
        verify(ddfTable,times(0)).addDataRnCBalance(any(String.class),any(Subscription.class),any(ProductOffer.class));
    }

    @Test
    public void processDoesNotAddBalanceForRnCAddonSubscription() throws OperationFailedException {
        doReturn(true).when(qoSProcessor).process(any(QoSProfile.class), any(BasePolicyContext.class),
                any(UserPackage.class), any(Subscription.class));
        doReturn(true).when(sprInfo).isUnknownUser();
        pcrfPolicyContext.process(qoSProfile, basePackage, new Subscription(null,
                null, null, null, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),
                SubscriptionState.STARTED, 100, SubscriptionType.ADDON, null,
                null));
        verify(ddfTable,times(0)).addDataRnCBalance(any(String.class),any(Subscription.class),any(ProductOffer.class));
    }

    @Test
    public void processDoesAddBalanceForRnCBasePackage() throws OperationFailedException {
        doReturn(true).when(qoSProcessor).process(any(QoSProfile.class), any(BasePolicyContext.class),
                any(UserPackage.class), any(Subscription.class));
        doReturn(true).when(sprInfo).isUnknownUser();
        doReturn(null).when(executionContext).getCurrentNonMonetoryBalance();
        doReturn(subscriberNonMonitoryBalance).when(pcrfPolicyContext).getCurrentBalance();
        when(productOfferStore.byName(any(String.class))).thenReturn(productOffer);

        SubscriptionNonMonitoryBalance subscriptionNonMonitoryBalance = new SubscriptionNonMonitoryBalance(basePackage.getId());
        when(ddfTable.addDataRnCBalance(any(String.class),any(Subscription.class),any(ProductOffer.class))).thenReturn(subscriptionNonMonitoryBalance);

        pcrfPolicyContext.process(qoSProfile,basePackage,null);
        verify(ddfTable,times(1)).addDataRnCBalance(any(String.class),any(Subscription.class),any(ProductOffer.class));
    }

    @Test
    public void processDoesNotAddBalanceForRnCBasePackageIfPreviousBalanceAlreadyExist() throws OperationFailedException {
        doReturn(true).when(qoSProcessor).process(any(QoSProfile.class), any(BasePolicyContext.class),
                any(UserPackage.class), any(Subscription.class));
        doReturn(true).when(sprInfo).isUnknownUser();
        when(subscriberNonMonitoryBalance.getPackageBalance(basePackage.getId())).thenReturn(subscriptionNonMonitoryBalance);
        when(productOfferStore.byName(any(String.class))).thenReturn(productOffer);
        pcrfPolicyContext.process(qoSProfile,basePackage,null);
        verify(ddfTable,times(0)).addDataRnCBalance(any(String.class),any(Subscription.class),any(ProductOffer.class));
    }

    @Test
    public void processReturnsFalseWhenExceptionIsThrownDuringAddingBalance() throws OperationFailedException {
        doReturn(true).when(sprInfo).isUnknownUser();
        when(productOfferStore.byName(any(String.class))).thenReturn(productOffer);
        when(ddfTable.addDataRnCBalance(any(String.class),any(Subscription.class),any(ProductOffer.class))).thenThrow(new OperationFailedException("failed"));
        Assert.assertFalse(pcrfPolicyContext.process(qoSProfile,basePackage,null));
        verify(ddfTable,times(1)).addDataRnCBalance(any(String.class),any(Subscription.class),any(ProductOffer.class));
    }

}
