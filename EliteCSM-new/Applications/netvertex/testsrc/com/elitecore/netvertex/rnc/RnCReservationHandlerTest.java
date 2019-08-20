package com.elitecore.netvertex.rnc;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.offer.ProductOfferAutoSubscription;
import com.elitecore.corenetvertex.pm.offer.ProductOfferServicePkgRel;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroup;
import com.elitecore.corenetvertex.pm.service.Service;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.pm.store.RnCPackageStore;
import com.elitecore.corenetvertex.spr.BalanceProvider;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.util.SubscriptionUtil;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.QoSProfile;
import com.elitecore.netvertex.pm.QuotaProfile;
import com.elitecore.netvertex.pm.RnCQuotaProfileFactory;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;
import com.elitecore.netvertex.pm.quota.RoPolicyContextImpl;
import com.elitecore.netvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.DummyPCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.RatingGroupSelectionState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RnCReservationHandlerTest {

    public static final String CURRENCY = "INR";
    private RnCReservationHandler reservationHandler;
    private DummyPCRFServiceContext dummyPCRFServiceContext;
    private PCRFRequest request;
    private PCRFResponse response;
    private ExecutionContext executionContext;
    private MockBasePackage basePackage;
    private NonMonetoryBalance nonMonetoryBalance;
    private SubscriberNonMonitoryBalance subscriberNonMonitoryBalance;
    private QuotaProfile quotaProfile;
    private QoSProfile qoSProfile;
    private Subscription subscription;
    private PolicyRepository policyRepository;
    private String currency="INR";

    PCRFServiceContext pcrfServiceContext;
    RnCPackageStore rncPackageStore;
    ProductOfferStore productOfferStore;
    NetVertexServerContext serverContext;

    @Before
    public void setUp(){

        dummyPCRFServiceContext = DummyPCRFServiceContext.spy();
  
        policyRepository = mock(PolicyRepository.class);
        
        reservationHandler = new RnCReservationHandler(dummyPCRFServiceContext);
        request = new PCRFRequestImpl();
        response = new PCRFResponseImpl();

        basePackage = MockBasePackage.create(UUID.randomUUID().toString(), UUID.randomUUID().toString()+"name");

        SPRInfoImpl sprInfo = (SPRInfoImpl) new SPRInfoImpl.SPRInfoBuilder()
                .withSubscriberIdentity("test")
                .withProductOffer(basePackage.getName())
                .build();

        sprInfo.setBalanceProvider(mock(BalanceProvider.class));
        request.setSPRInfo(sprInfo);
        executionContext = new ExecutionContext(request, response, mock(CacheAwareDDFTable.class), CURRENCY);

        RnCQuotaProfileDetail rnCQuotaProfileDetail = new RnCQuotaProfileFactory(UUID.randomUUID().toString(),
                UUID.randomUUID().toString()).randomBalanceWithRate().create();
        Map<String, QuotaProfileDetail> hashMap = new HashMap<>();
        hashMap.put("test", rnCQuotaProfileDetail);

        quotaProfile = new QuotaProfile("test", basePackage.getName(), "test", BalanceLevel.HSQ,2, RenewalIntervalUnit.MONTH, QuotaProfileType.RnC_BASED, Arrays.asList(hashMap), CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue());
        qoSProfile = new QoSProfile("test","test", basePackage.getName(), basePackage.getId(), quotaProfile, null,null,
                0, null, null, null, null, null);

        basePackage.mockQuotaProfie(quotaProfile);

        RatingGroupSelectionState ratingGroupSelectionState = new RatingGroupSelectionState();
        ratingGroupSelectionState.add("test", basePackage, qoSProfile, 0, 0, 0);
        request.setPCCProfileSelectionState(ratingGroupSelectionState);

        nonMonetoryBalance = createNonMonetaryBalance(request.getSPRInfo(), rnCQuotaProfileDetail);
        subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(Arrays.asList(nonMonetoryBalance));
        response.setCurrentNonMonetoryBalance(subscriberNonMonitoryBalance);

        SubscriberMonetaryBalance subscriberMonetaryBalance = new SubscriberMonetaryBalance(new FixedTimeSource(System.currentTimeMillis()));
        subscriberMonetaryBalance.addMonitoryBalances(createMonetaryBalance("test"));
        response.setCurrentMonetaryBalance(subscriberMonetaryBalance);

        
        subscription = new Subscription("test", "test", basePackage.getId(),"productOfferId", null, null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);
        LinkedHashMap<String, Subscription> subscriptionMap = new LinkedHashMap<>();
        subscriptionMap.put(basePackage.getId(), subscription);
        response.setSubscriptions(subscriptionMap);
        request.setAttribute(PCRFKeyConstants.CS_SERVICE.name(), PCRFKeyValueConstants.VOICE_SERVICE_ID.name());

        pcrfServiceContext = mock(PCRFServiceContext.class);
        rncPackageStore = mock(RnCPackageStore.class);
        productOfferStore = mock(ProductOfferStore.class);
        serverContext = mock(NetVertexServerContext.class);
    }


    @Test
    public void test_handle_for_gx_gy_flow_withMSCC() throws OperationFailedException {

        MSCC mscc = new MSCC();
        mscc.setRatingGroup(0);
        request.setReportedMSCCs(Arrays.asList(mscc));

        RoPolicyContextImpl gyPolicyContextImpl = mock(RoPolicyContextImpl.class);
        RnCPackage rncPackage = mock(RnCPackage.class);
        SubscriberMonetaryBalance monetaryBalance = mock(SubscriberMonetaryBalance.class);
        when(gyPolicyContextImpl.getRnCPackage()).thenReturn(rncPackage);
        when(gyPolicyContextImpl.getCurrentMonetaryBalance()).thenReturn(monetaryBalance);
        QuotaReservation quotaReservation = new QuotaReservation();
        quotaReservation.put(new MSCC());
        when(gyPolicyContextImpl.getReservations()).thenReturn(quotaReservation);
        when(monetaryBalance.isServiceBalanceExist(any(String.class))).thenReturn(false);
        when(rncPackage.apply(any(), any(), any())).thenReturn(true);

        reservationHandler.handle(request, response, gyPolicyContextImpl, executionContext);
    }

    @Test
    public void test_handle_nonmonetary_addon() throws OperationFailedException {

        subscription = new Subscription("test", "test", basePackage.getId(),"productOfferId", null, null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);
        LinkedHashMap<String, Subscription> subscriptionMap = new LinkedHashMap<>();
        subscriptionMap.put(basePackage.getId(), subscription);

        RnCPackage rnCPackage = createRnCPackage("test_nonmonetary_adddon", "Non Monetary Addon", RnCPkgType.NON_MONETARY_ADDON);
        rnCPackage = Mockito.spy(rnCPackage);
        subscription = new Subscription("test_nonmonetary_adddon", "test", rnCPackage.getId(),"productOfferId", new Timestamp(System.currentTimeMillis()-java.util.concurrent.TimeUnit.DAYS.toMillis(1)), new Timestamp(System.currentTimeMillis()+java.util.concurrent.TimeUnit.DAYS.toMillis(1)), null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);
        subscriptionMap.put(rnCPackage.getId(), subscription);

        RnCPackage rncMonetaryPackage = createRnCPackage("test_monetary_adddon", "Monetary Addon", RnCPkgType.MONETARY_ADDON);
        rncMonetaryPackage = Mockito.spy(rncMonetaryPackage);
        subscription = new Subscription("test_monetary_adddon", "test", rncMonetaryPackage.getId(),"productOfferId", new Timestamp(System.currentTimeMillis()-java.util.concurrent.TimeUnit.DAYS.toMillis(1)), new Timestamp(System.currentTimeMillis()+java.util.concurrent.TimeUnit.DAYS.toMillis(1)), null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);
        subscriptionMap.put(rncMonetaryPackage.getId(), subscription);

        response.setSubscriptions(subscriptionMap);

        PCRFServiceContext pcrfServiceContext = mock(PCRFServiceContext.class);
        RnCPackageStore rncPackageStore = mock(RnCPackageStore.class);
        ProductOfferStore productOfferStore = mock(ProductOfferStore.class);
        NetVertexServerContext serverContext = mock(NetVertexServerContext.class);

        reservationHandler = new RnCReservationHandler(pcrfServiceContext);

        RoPolicyContextImpl roPolicyContextImpl = mock(RoPolicyContextImpl.class);
        Mockito.doReturn(subscriptionMap).when(roPolicyContextImpl).getSubscriptions();

        Mockito.doReturn(serverContext).when(pcrfServiceContext).getServerContext();
        Mockito.doReturn(policyRepository).when(serverContext).getPolicyRepository();
        Mockito.doReturn(rncPackageStore).when(policyRepository).getRnCPackage();
        Mockito.doReturn(productOfferStore).when(policyRepository).getProductOffer();
        Mockito.doReturn(rnCPackage).when(rncPackageStore).byId("test_nonmonetary_adddon");
        Mockito.doReturn(rncMonetaryPackage).when(rncPackageStore).byId("test_monetary_adddon");

        Mockito.doReturn(true).when(rnCPackage).apply(any(), any(), any());
        ProductOffer productOffer = createProductOffer("productOfferId","productOfferId",PkgType.BASE,rnCPackage, rncMonetaryPackage);
        Mockito.doReturn(productOffer).when(productOfferStore).byId("productOfferId");

        reservationHandler.handle(request, response, roPolicyContextImpl, executionContext);

    }

    @Test
    public void handleDoesNotApplyAddonWhenFnFGroupDoesNotContainCalledNumber() throws OperationFailedException {
        RnCPackage rncMonetaryPackage = createRnCPackage("test_non_monetary_adddon", "Non Monetary Addon",
                RnCPkgType.NON_MONETARY_ADDON);
        RnCPackage rncBasePackage = createRnCPackage("base", "rnc",
                RnCPkgType.BASE);
        rncMonetaryPackage = Mockito.spy(rncMonetaryPackage);
        rncBasePackage = Mockito.spy(rncBasePackage);
        String metadata="{\"fnFGroup\":{\"name\":\"fnf\",\"members\":[\"4568595235\",\"7896523145\"]}}";
        subscription = new Subscription("test_monetary_adddon", "test",
                rncMonetaryPackage.getId(),"productOfferId",
                new Timestamp(System.currentTimeMillis()-java.util.concurrent.TimeUnit.DAYS.toMillis(1)),
                new Timestamp(System.currentTimeMillis()+java.util.concurrent.TimeUnit.DAYS.toMillis(1)),
                null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.RO_ADDON,
                SubscriptionUtil.createMetaData(metadata,null,null),
                null, null);
        LinkedHashMap<String, Subscription> subscriptionMap = new LinkedHashMap<>();
        subscriptionMap.put(rncMonetaryPackage.getId(), subscription);

        reservationHandler = new RnCReservationHandler(pcrfServiceContext);

        RoPolicyContextImpl roPolicyContextImpl = mockRoPolicyContext(rncMonetaryPackage, rncBasePackage, subscriptionMap);

        request.setAttribute(PCRFKeyConstants.CS_CALLED_STATION_ID.getVal(),"8525852585");

        Mockito.doReturn(false).when(rncBasePackage).apply(any(), any(), any());

        reservationHandler.handle(request, response, roPolicyContextImpl, executionContext);

    }

    private RoPolicyContextImpl mockRoPolicyContext(RnCPackage rncMonetaryPackage, RnCPackage rncBasePackage, LinkedHashMap<String, Subscription> subscriptionMap) throws OperationFailedException {
        RoPolicyContextImpl roPolicyContextImpl = mock(RoPolicyContextImpl.class);
        Mockito.doReturn(subscriptionMap).when(roPolicyContextImpl).getSubscriptions();
        Mockito.doReturn(rncBasePackage).when(roPolicyContextImpl).getRnCPackage();
        Mockito.doReturn(subscriptionMap).when(roPolicyContextImpl).getSubscriptions();
        Mockito.doReturn(serverContext).when(pcrfServiceContext).getServerContext();
        Mockito.doReturn(policyRepository).when(serverContext).getPolicyRepository();
        Mockito.doReturn(rncPackageStore).when(policyRepository).getRnCPackage();
        Mockito.doReturn(productOfferStore).when(policyRepository).getProductOffer();
        Mockito.doReturn(rncMonetaryPackage).when(rncPackageStore).byId("test_non_monetary_adddon");

        ProductOffer productOffer = createProductOffer("productOfferId","productOfferId", PkgType.BASE, rncMonetaryPackage);
        Mockito.doReturn(productOffer).when(productOfferStore).byId("productOfferId");
        Mockito.doReturn(request).when(roPolicyContextImpl).getPCRFRequest();
        request.setAttribute(PCRFKeyConstants.CS_SERVICE.val, PCRFKeyValueConstants.DATA_SERVICE_ID.val);
        return roPolicyContextImpl;
    }

    @Test
    public void handleDoesApplyAddonWhenFnFGroupDoesContainsCalledNumber() throws OperationFailedException {
        RnCPackage rncMonetaryPackage = createRnCPackage("test_non_monetary_adddon", "Non Monetary Addon",
                RnCPkgType.NON_MONETARY_ADDON);
        RnCPackage rncBasePackage = createRnCPackage("base", "rnc",
                RnCPkgType.BASE);
        rncMonetaryPackage = Mockito.spy(rncMonetaryPackage);
        rncBasePackage = Mockito.spy(rncBasePackage);
        String metadata="{\"fnFGroup\":{\"name\":\"fnf\",\"members\":[\"4568595235\",\"7896523145\"]}}";
        subscription = new Subscription("test_monetary_adddon", "test",
                rncMonetaryPackage.getId(),"productOfferId",
                new Timestamp(System.currentTimeMillis()-java.util.concurrent.TimeUnit.DAYS.toMillis(1)),
                new Timestamp(System.currentTimeMillis()+java.util.concurrent.TimeUnit.DAYS.toMillis(1)),
                null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.RO_ADDON,
                SubscriptionUtil.createMetaData(metadata,null,null),
                null, null);
        LinkedHashMap<String, Subscription> subscriptionMap = new LinkedHashMap<>();
        subscriptionMap.put(rncMonetaryPackage.getId(), subscription);

        reservationHandler = new RnCReservationHandler(pcrfServiceContext);

        RoPolicyContextImpl roPolicyContextImpl = mockRoPolicyContext(rncMonetaryPackage, rncBasePackage, subscriptionMap);

        request.setAttribute(PCRFKeyConstants.CS_CALLED_STATION_ID.getVal(),"7896523145");

        Mockito.doReturn(false).when(rncBasePackage).apply(any(), any(), any());
        doAnswer((context)-> {
            Subscription sub =context.getArgumentAt(2,Subscription.class);
            sub.setProductOfferId("new_ID");
            return false;
        }).when(rncMonetaryPackage).apply(any(), any(), any());

        reservationHandler.handle(request, response, roPolicyContextImpl, executionContext);
        Assert.assertEquals("new_ID",subscription.getProductOfferId());
    }
    
    @Test
    public void test_handle_nonmonetary_addon_expired() throws OperationFailedException {
    	
        subscription = new Subscription("test", "test", basePackage.getId(),"productOfferId", null, null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);
        LinkedHashMap<String, Subscription> subscriptionMap = new LinkedHashMap<>();
        subscriptionMap.put(basePackage.getId(), subscription);
        
        RnCPackage rnCPackage = createRnCPackage("test_nonmonetary_adddon", "Non Monetary Addon", RnCPkgType.NON_MONETARY_ADDON);
        rnCPackage = Mockito.spy(rnCPackage);        
        subscription = new Subscription("test_nonmonetary_adddon", "test", rnCPackage.getId(),"productOfferId", new Timestamp(System.currentTimeMillis()+java.util.concurrent.TimeUnit.DAYS.toMillis(1)), new Timestamp(System.currentTimeMillis()+java.util.concurrent.TimeUnit.DAYS.toMillis(2)), null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);
        subscriptionMap.put(rnCPackage.getId(), subscription); 
        
        RnCPackage rncMonetaryPackage = createRnCPackage("test_monetary_adddon", "Monetary Addon", RnCPkgType.MONETARY_ADDON);
        rncMonetaryPackage = Mockito.spy(rncMonetaryPackage);        
        subscription = new Subscription("test_monetary_adddon", "test", rncMonetaryPackage.getId(),"productOfferId", new Timestamp(System.currentTimeMillis()-java.util.concurrent.TimeUnit.DAYS.toMillis(1)), new Timestamp(System.currentTimeMillis()+java.util.concurrent.TimeUnit.DAYS.toMillis(1)), null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);
        subscriptionMap.put(rncMonetaryPackage.getId(), subscription); 
        
        response.setSubscriptions(subscriptionMap);

        reservationHandler = new RnCReservationHandler(pcrfServiceContext);

        Mockito.doReturn(serverContext).when(pcrfServiceContext).getServerContext();
        Mockito.doReturn(policyRepository).when(serverContext).getPolicyRepository();
        Mockito.doReturn(rncPackageStore).when(policyRepository).getRnCPackage();
        Mockito.doReturn(productOfferStore).when(policyRepository).getProductOffer();
        Mockito.doReturn(rnCPackage).when(rncPackageStore).byId("test_nonmonetary_adddon");

        Mockito.doReturn(rncMonetaryPackage).when(rncPackageStore).byId("test_monetary_adddon");

        RoPolicyContextImpl roPolicyContextImpl = mock(RoPolicyContextImpl.class);
        
        Mockito.doReturn(subscriptionMap).when(roPolicyContextImpl).getSubscriptions();
        Mockito.doReturn(true).when(rncMonetaryPackage).apply(any(), any(), any());

        ProductOffer productOffer = createProductOffer("base_offer","base_offer",PkgType.BASE,rncMonetaryPackage, rncMonetaryPackage);
        Mockito.doReturn(productOffer).when(productOfferStore).byId("base_offer");

        SubscriberMonetaryBalance monetaryBalance = mock(SubscriberMonetaryBalance.class);
        when(policyRepository.getRnCPackage()).thenReturn(rncPackageStore);
        when(roPolicyContextImpl.getCurrentMonetaryBalance()).thenReturn(monetaryBalance);
        when(monetaryBalance.isServiceBalanceExist(any(String.class))).thenReturn(true);
        
        reservationHandler.handle(request, response, roPolicyContextImpl, executionContext);

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
                reservation,0, 0,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                CURRENCY,null,
                System.currentTimeMillis(), 0,"","");
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

    private ProductOffer createProductOffer(String id, String name, PkgType type, RnCPackage... rnCPackages){
        List< RateCardGroup > rateCardGroups = new ArrayList<>();
        rateCardGroups.add(null);

        ArrayList offerServicePkgRel = new ArrayList();
        Map<String, Set<String>> packageIdToServiceIds = new HashMap<>();
        for(RnCPackage rnCPackage : rnCPackages){
            offerServicePkgRel.add(new ProductOfferServicePkgRel("id",new Service(PCRFKeyValueConstants.DATA_SERVICE_ID.val,"DATA",PkgStatus.ACTIVE), rnCPackage.getId(), policyRepository));

            Set<String> set = new HashSet<>();
            set.add(PCRFKeyValueConstants.DATA_SERVICE_ID.val);
            packageIdToServiceIds.putIfAbsent(rnCPackage.getId(),set);
        }

        return new ProductOffer(id, name, null, type, PkgMode.LIVE,
                30, ValidityPeriodUnit.DAY, 0, 0.0,
         PkgStatus.ACTIVE, offerServicePkgRel, new ArrayList<ProductOfferAutoSubscription>(),
                null, new ArrayList<>(), null, null, PolicyStatus.SUCCESS,
                null, null, true, null, null, policyRepository,
                null, null,packageIdToServiceIds,CURRENCY);
    }

    @Test
    public void applySetsNextTimeOutInTheContext(){

    }
}
