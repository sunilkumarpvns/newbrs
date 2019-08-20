package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.data.CarryForwardStatus;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.DailyAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.DBHelper;
import com.elitecore.corenetvertex.util.DerbyUtil;
import com.elitecore.corenetvertex.util.QueryBuilder;
import junitparams.JUnitParamsRunner;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class ABMFOperationTestGetBalance {

    private static final String TABLE_NAME = "TBLM_DATA_BALANCE";
    private static final String SELECT_BALANCE_QUERY = "SELECT * FROM "+TABLE_NAME+" WHERE SUBSCRIBER_ID = ? ";
    private static final String DS_NAME = "test-DB";
    public static final long RATING_GROUP_1 = 1;
    private DummyTransactionFactory transactionFactory;
    private static ABMFOperationTestHelper helper;
    private String SUBSCRIBER_ID="123123";


    private String basePackageId = "BASE_1";
    private String addOnId = "AddOn_1";
    private String productOfferId = "PRODUCT_OFFER_1";
    private String promotionalPackageId = "Promotion_1";
    private String invalidPackageId = "Invalid_1";
    private long DATA_SERVICE_TYPE_1 = 1;
    private long DATA_SERVICE_TYPE_4 = 4;
    private String SUBSCRIPTION_ID="123456";
    private String INVALID_SUBSCRIPTION_ID="1234567";
    private Map<String,NonMonetoryBalance> dbBalance=null;

    private ProductOffer baseProductOffer;
    private ProductOffer addonProductOffer;
    private ProductOffer promotionalProductOffer;

    private ABMFResetOperations abmfResetOperations;

    UserPackage base;
    private AddOn addon;
    private PromotionalPackage promotionalPackage;

    private String testDB = UUID.randomUUID().toString();
    private String pccProfileName = "pccProfileName";

    Long currentTime = System.currentTimeMillis();
    long monthInterval=30*24*3600*1000L;
    long weekInterval=7*24*3600*1000L;
    long dayInterval=24*3600*1000L;
    long ourInterval=3600*1000L;

    private static final String currency = "INR";

    @Mock
    private PolicyRepository policyRepository;
    @Mock
    private AlertListener alertListener;
    @Mock
    private BalanceResetOperation balanceResetOperation;

    ABMFOperation abmfOperation;

    @BeforeClass
    public static void setUpClass() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);


        String url = "jdbc:derby:memory:"+ testDB +";create=true";
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, url, "", "", 1, 5000, 3000);
        transactionFactory = (DummyTransactionFactory)new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 100).build();

        helper = new ABMFOperationTestHelper(transactionFactory);
        helper.createPackages();

        base = helper.getUserPackage("base");
        addon = (AddOn) helper.getUserPackage("");
        promotionalPackage = (PromotionalPackage)helper.getUserPackage("promo");

        baseProductOffer = new ProductOffer("test", "test", null, PkgType.BASE, PkgMode.LIVE, 0, ValidityPeriodUnit.DAY,
                        0.0,0.0, PkgStatus.ACTIVE, null,null, base.getId(), null, null,
                        null, PolicyStatus.SUCCESS, null, null, false, null, null, policyRepository,null,null,new HashMap<>(),currency);

        addonProductOffer = new ProductOffer("test", "test", null, PkgType.ADDON, PkgMode.LIVE, 0, ValidityPeriodUnit.DAY,
                0.0,0.0,  PkgStatus.ACTIVE, null,null, addon.getId(), null, null,
                null, PolicyStatus.SUCCESS, null, null, false, null, null, policyRepository,null,null,new HashMap<>(),currency);

        promotionalProductOffer = new ProductOffer("test", "test", null, PkgType.PROMOTIONAL, PkgMode.LIVE, 0, ValidityPeriodUnit.DAY,
                0.0,0.0, PkgStatus.ACTIVE, null,null, promotionalPackage.getId(), null, null,
                null, PolicyStatus.SUCCESS, null, null, false, null, null, policyRepository,null,null,new HashMap<>(),currency);


        ArrayList<PromotionalPackage> promotionalPackages = new ArrayList<>();
        promotionalPackages.add(promotionalPackage);

        addInvalidPromoPackages(promotionalPackages);

        try{
            createTablesAndInsertUsageRecords();
        } catch (Exception e) {

        }

        base = Mockito.spy(base);
        baseProductOffer = Mockito.spy(baseProductOffer);
        addon = Mockito.spy(addon);
        promotionalPackage = Mockito.spy(promotionalPackage);

        Mockito.when(policyRepository.getPkgDataByName(basePackageId)).thenReturn(base);

        ProductOfferStore productOfferStore = spy(ProductOfferStore.class);
        Mockito.when(policyRepository.getProductOffer()).thenReturn(productOfferStore);
        Mockito.when(productOfferStore.byId(basePackageId)).thenReturn(baseProductOffer);
        Mockito.when(productOfferStore.byId(addOnId)).thenReturn(addonProductOffer);
        Mockito.when(productOfferStore.byName(baseProductOffer.getName())).thenReturn(baseProductOffer);
        Mockito.when(policyRepository.getBasePackageDataByName(basePackageId)).thenReturn((BasePackage) base);
        Mockito.when(policyRepository.getPromotionalPackages()).thenReturn(promotionalPackages);
        Mockito.when(policyRepository.getAddOnById(addOnId)).thenReturn(addon);
        Mockito.when(policyRepository.getPkgDataById(basePackageId)).thenReturn(base);
        Mockito.when(policyRepository.getPkgDataById(addOnId)).thenReturn(addon);

        doReturn(helper.getQuotaProfiles(basePackageId)).when(base).getQuotaProfiles();
        doReturn(helper.getQuotaProfiles(addOnId)).when(addon).getQuotaProfiles();
        doReturn(helper.getQuotaProfiles(promotionalPackageId)).when(promotionalPackage).getQuotaProfiles();

        abmfOperation = new ABMFOperation(alertListener,policyRepository,0,0);
    }

    private void addInvalidPromoPackages(ArrayList<PromotionalPackage> promotionalPackages){
        promotionalPackages.add(new PromotionalPackage("promo2",promotionalPackageId, QuotaProfileType.RnC_BASED,
                PkgStatus.INACTIVE,new ArrayList<>(),null, null,"",
                0.0,null,null,null ,false,
                null, null,null,null, null,
                null));
        promotionalPackages.add(new PromotionalPackage("promo3",promotionalPackageId, QuotaProfileType.RnC_BASED,
                PkgStatus.ACTIVE,new ArrayList<>(),null, null,"",
                0.0,null,new Timestamp(System.currentTimeMillis()-(1000*3600*24*30L)), null ,false,
                null, null,null,null, null,
                null));
        promotionalPackages.add(new PromotionalPackage(promotionalPackageId,promotionalPackageId, QuotaProfileType.SY_COUNTER_BASED,
                PkgStatus.ACTIVE,new ArrayList<>(),null, null,"",
                0.0,null,null, null ,false,
                null, null,null,null, null,
                null));
        promotionalPackages.add(new PromotionalPackage("invalid","invalid", QuotaProfileType.RnC_BASED,
                PkgStatus.ACTIVE,new ArrayList<>(),null, null,"",
                0.0,null,null,null ,false,
                null, null,null,null, null,
                null));
    }

    @Test
    public void when_subscriber_id_is_passed_it_will_return_all_details_available() throws Exception {
        SubscriberNonMonitoryBalance balance = abmfOperation.getNonMonitoryBalance(createSprInfo(SUBSCRIBER_ID), transactionFactory);
        Assert.assertNotNull(balance.getPackageBalance(basePackageId));
        Assert.assertNotNull(balance.getPackageBalance(SUBSCRIPTION_ID));
        Assert.assertNotNull(balance.getPackageBalance(promotionalPackageId));
        Assert.assertNotNull(balance.getPackageBalance(INVALID_SUBSCRIPTION_ID));
    }

    @Test
    public void subscriber_with_no_balance_record_it_will_return_zero_sized_list() throws Exception {
        SubscriberNonMonitoryBalance balance = abmfOperation.getNonMonitoryBalance(createSprInfo("InvalidSubscriberId"), transactionFactory);
        Assert.assertEquals(balance.getPackageBalances().values().size(),0);
    }

    @Test
    public void when_package_and_subscription_both_null_then_it_must_return_all_the_available_balances() throws Exception{

        SPRInfoImpl sprInfo = getSprInfo();

        SubscriberNonMonitoryBalance balance = abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                null,null, helper.getSubscriptions(), transactionFactory);

        Assert.assertNotNull(balance.getPackageBalance(basePackageId));
        Assert.assertNotNull(balance.getPackageBalance(SUBSCRIPTION_ID));
        Assert.assertNotNull(balance.getPackageBalance(promotionalPackageId));
    }

    @Test
    public void when_base_package_passed_then_it_must_return_base_package_balance() throws Exception{

        Mockito.when(policyRepository.getPkgDataById(basePackageId+1)).thenReturn(addon);

        SPRInfoImpl sprInfo = getSprInfo();

        SubscriberNonMonitoryBalance balance = abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                basePackageId,null, helper.getSubscriptions(), transactionFactory);

        Assert.assertNotNull(balance.getPackageBalance(basePackageId));
    }

    @Test
    public void fetching_base_balance_and_checking_it_for_reset_values() throws Exception{

        Mockito.when(policyRepository.getPkgDataById(basePackageId+1)).thenReturn(addon);

        SPRInfoImpl sprInfo = getSprInfo();

        SubscriberNonMonitoryBalance balance = abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                basePackageId,null, helper.getSubscriptions(), transactionFactory);

        Assert.assertNotNull(balance.getPackageBalance(basePackageId));

        NonMonetoryBalance resetedBalance = balance.getPackageBalance(basePackageId)
                .getBalance("Quota_"+basePackageId)
                .getBalance(DATA_SERVICE_TYPE_4, RATING_GROUP_1,0);

        //Verifying reset balance

        Assert.assertEquals(resetedBalance.getWeeklyVolume(),0);
        Assert.assertEquals(resetedBalance.getWeeklyTime(),0);
        Assert.assertEquals(resetedBalance.getDailyVolume(),0);
        Assert.assertEquals(resetedBalance.getDailyTime(),0);

    }

    @Test
    public void fetching_base_balance_and_checking_it_for_values_not_reset() throws Exception{

        SPRInfoImpl sprInfo = getSprInfo();

        SubscriberNonMonitoryBalance balance = abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                basePackageId,null, helper.getSubscriptions(), transactionFactory);

        Assert.assertNotNull(balance.getPackageBalance(basePackageId));

        NonMonetoryBalance nonResetBalance = balance.getPackageBalance(basePackageId)
                .getBalance("Quota_"+basePackageId)
                .getBalance(DATA_SERVICE_TYPE_1, RATING_GROUP_1,0);

        //Verifying reset balance

        TblmDataBalanceEntity dbBalance = helper.dbBalance.get(nonResetBalance.getId());

        Assert.assertEquals(new Long(nonResetBalance.getWeeklyVolume()),dbBalance.getWeeklyVolume());
        Assert.assertEquals(new Long(nonResetBalance.getWeeklyTime()),dbBalance.getWeeklyTime());
        Assert.assertEquals(new Long(nonResetBalance.getDailyVolume()),dbBalance.getDailyVolume());
        Assert.assertEquals(new Long(nonResetBalance.getDailyTime()),dbBalance.getDailyTime());
        Assert.assertEquals(new Long(nonResetBalance.getBillingCycleAvailableVolume()),dbBalance.getBillingCycleAvailableVolume());
        Assert.assertEquals(new Long(nonResetBalance.getBillingCycleAvailableTime()),dbBalance.getBillingCycleAvailableTime());
    }

    @Test
    public void when_valid_addon_id_passed_then_it_must_return_addon_balance() throws Exception{

        SPRInfoImpl sprInfo = getSprInfo();

        SubscriberNonMonitoryBalance balance = abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                addOnId,null, helper.getSubscriptions(), transactionFactory);

        Assert.assertNotNull(balance.getPackageBalance("123456"));
    }

    @Test
    public void when_valid_subscription_id_passed_balance_for_that_subsciption_only_should_be_returned() throws Exception{

        SPRInfoImpl sprInfo = getSprInfo();

        SubscriberNonMonitoryBalance balance = abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                null,SUBSCRIPTION_ID, helper.getSubscriptions(), transactionFactory);

        Assert.assertNotNull(balance.getPackageBalance(SUBSCRIPTION_ID));
    }

    @Test
    public void when_addonid_and_subscription_id_both_are_valid_then_balance_for_that_particular_combination_must_be_returned() throws Exception{

        SPRInfoImpl sprInfo = getSprInfo();

        SubscriberNonMonitoryBalance balance = abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                addOnId,SUBSCRIPTION_ID, helper.getSubscriptions(), transactionFactory);

        Assert.assertNotNull(balance.getPackageBalance(SUBSCRIPTION_ID));
    }

    @Test
    public void when_promotional_package_id_is_passed_then_it_must_pass_succcessfully() throws Exception{

        SPRInfoImpl sprInfo = getSprInfo();
        when(policyRepository.getPkgDataById(promotionalPackageId)).thenReturn(promotionalPackage);

        SubscriberNonMonitoryBalance balance = abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                promotionalPackageId,null, helper.getSubscriptions(), transactionFactory);

        Assert.assertNotNull(balance.getPackageBalance(promotionalPackageId));
    }

    @Test(expected = OperationFailedException.class)
    public void when_no_data_package_is_associated_with_spr_profile_then_it_must_throw_an_operationfailedexception() throws Exception{

        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(SUBSCRIBER_ID);

        abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                null,null, helper.getSubscriptions(), transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void when_invalid_package_name_is_passed_it_must_throw_operationfailedexception() throws Exception{

        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(SUBSCRIBER_ID);
        sprInfo.setProductOffer(basePackageId);

        abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                "invalid_package_name",null, helper.getSubscriptions(), transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void getNonMonitoryBalanceWithResetExpiredBalanceFail_when_valid_package_valid_subscription_but_does_not_belong_to_each_other() throws Exception{

        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(SUBSCRIBER_ID);
        sprInfo.setProductOffer(basePackageId);

        abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                addOnId,"123456789", helper.getSubscriptions(), transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void getNonMonitoryBalanceWithResetExpiredBalanceFail_when_valid_package_but_package_id_in_subscription_doesnt_exist() throws Exception{

        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(SUBSCRIBER_ID);
        sprInfo.setProductOffer(basePackageId);

        abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                addOnId,"1234567890", helper.getSubscriptions(), transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void getNonMonitoryBalanceWithResetExpiredBalanceFail_when_valid_package_id_but_invalid_subscription_id_is_passed() throws Exception{

        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(SUBSCRIBER_ID);
        sprInfo.setProductOffer(basePackageId);

        abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                addOnId,"invalid_subscription_id", helper.getSubscriptions(), transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void getNonMonitoryBalanceWithResetExpiredBalanceFail_when_invalid_subscription_id_is_passed() throws Exception{

        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(SUBSCRIBER_ID);
        sprInfo.setProductOffer(basePackageId);

        abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                null,"invalid_subscription_id", helper.getSubscriptions(), transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void getNonMonitoryBalanceWithResetExpiredBalanceFail_when_base_package_and_subscription_id_passed() throws Exception{

        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(SUBSCRIBER_ID);
        sprInfo.setProductOffer(basePackageId);

        abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                basePackageId,"1234567890", helper.getSubscriptions(), transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void getNonMonitoryBalanceWithResetExpiredBalanceFail_when_base_package_not_associated_with_subscriber() throws Exception{

        Mockito.when(policyRepository.getProductOffer().byId(addOnId)).thenReturn(baseProductOffer);  // for the sake of validating fail case

        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(SUBSCRIBER_ID);
        sprInfo.setProductOffer(addOnId);

        abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                addOnId,null, helper.getSubscriptions(), transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void getNonMonitoryBalanceWithResetExpiredBalanceFail_when_subscriptions_not_exist_for_the_addon_id_passed() throws Exception{

        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(SUBSCRIBER_ID);
        sprInfo.setProductOffer(basePackageId);

        abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                addOnId,null,new HashMap<>(), transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void when_package_exist_but_subscription_for_that_package_does_not_then_expception_should_be_thrown_telling_such_message() throws Exception{

        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(SUBSCRIBER_ID);
        sprInfo.setProductOffer(basePackageId);


        HashMap<String, Subscription> subscriptions= new HashMap<>();

        Subscription subscription_1 = new Subscription(SUBSCRIPTION_ID, SUBSCRIBER_ID, "fakeAddon","fakeProductOffer", null, null, SubscriptionState.STARTED,CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON,
                null, null);

        subscriptions.put("something",subscription_1);

        abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                addOnId,null,subscriptions, transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void when_package_ans_subscription_exist_but_they_do_not_have_any_relation_then_expception_must_be_thrown_telling_such_message() throws Exception{

        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(SUBSCRIBER_ID);

        Mockito.when(policyRepository.getAddOnById("fakeAddon")).thenReturn(addon);

        HashMap<String, Subscription> subscriptions= new HashMap<>();

        Subscription subscription_1 = new Subscription(SUBSCRIPTION_ID, SUBSCRIBER_ID, "fakeAddon", "fakeProductOffer", null, null, SubscriptionState.STARTED, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON,
                null, null);

        subscriptions.put(SUBSCRIPTION_ID,subscription_1);

        abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                addOnId,SUBSCRIPTION_ID,subscriptions, transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void when_package_id_as_an_addon_passed_but_no_subscription_for_that_package_exist_then_it_must_throw_an_exception_telling_such_message() throws Exception{

        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(SUBSCRIBER_ID);

        Mockito.when(policyRepository.getAddOnById(addOnId)).thenReturn(null);

        HashMap<String, Subscription> subscriptions= new HashMap<>();

        Subscription subscription_1 = new Subscription(SUBSCRIPTION_ID, SUBSCRIBER_ID, addOnId,productOfferId,  null, null, SubscriptionState.STARTED,CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON,
                null, null);

        subscriptions.put(SUBSCRIPTION_ID,subscription_1);

        abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                addOnId,null,subscriptions, transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void when_only_subscription_id_is_passed_but_package_in_subscription_details_does_not_exist_then_it_must_throw_and_exception_telling_such_message() throws Exception{

        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(SUBSCRIBER_ID);

        HashMap<String, Subscription> subscriptions= new HashMap<>();

        Subscription subscription_1 = new Subscription(SUBSCRIPTION_ID, SUBSCRIBER_ID, "fakePackageId", "fakeProductOfferId", null, null, SubscriptionState.STARTED,CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON,
                null, null);

        subscriptions.put(SUBSCRIPTION_ID,subscription_1);

        abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                null,SUBSCRIPTION_ID,subscriptions, transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void wrong_package_name_as_a_base_package_then_exception_must_be_thrown() throws Exception{

        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(SUBSCRIBER_ID);
        sprInfo.setProductOffer("invalidPackageName");

        abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                null,null,helper.getSubscriptions(), transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void when_transaction_creation_fails_it_must_throw_OperationFailedException_with_message_telling_such_behaviour() throws Exception{

        transactionFactory = spy(transactionFactory);
        doReturn(null).when(transactionFactory).createTransaction();

        abmfOperation.getNonMonitoryBalance(createSprInfo(SUBSCRIBER_ID), transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void when_transaction_factory_is_not_alive_it_must_throw_OperationFailedException_with_message_telling_such_behaviour() throws Exception{

        transactionFactory = spy(transactionFactory);
        doReturn(false).when(transactionFactory).isAlive();

        Mockito.when(transactionFactory.isAlive()).thenReturn(false);
        abmfOperation.getNonMonitoryBalance(createSprInfo(SUBSCRIBER_ID), transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void when_transaction_begin_fails_it_must_handle_Exception() throws Exception{

        transactionFactory = spy(transactionFactory);
        Transaction transaction = transactionFactory.createTransaction();
        transaction = spy(transaction);
        Mockito.when(transactionFactory.createTransaction()).thenReturn(transaction);

        Mockito.doThrow(new TransactionException("exception", TransactionErrorCode.CONNECTION_NOT_FOUND))
                .when(transaction).begin();

        abmfOperation.getNonMonitoryBalance(createSprInfo(SUBSCRIBER_ID), transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void when_for_any_reason_Exception_occurs_while_db_call() throws Exception{

        transactionFactory = spy(transactionFactory);
        Transaction transaction = transactionFactory.createTransaction();
        transaction = spy(transaction);
        Mockito.when(transactionFactory.createTransaction()).thenReturn(transaction);

        PreparedStatement pstm = transaction.prepareStatement(SELECT_BALANCE_QUERY);
        pstm = spy(pstm);
        Mockito.when(transaction.prepareStatement(SELECT_BALANCE_QUERY)).thenReturn(pstm);
        Mockito.doThrow(new ArrayIndexOutOfBoundsException()).when(pstm).executeQuery();

        abmfOperation.getNonMonitoryBalance(createSprInfo(SUBSCRIBER_ID), transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void when_prepared_statement_compilation_fails_it_must_handle_Exception() throws Exception{

        transactionFactory = spy(transactionFactory);
        Transaction transaction = transactionFactory.createTransaction();
        transaction = spy(transaction);
        Mockito.when(transactionFactory.createTransaction()).thenReturn(transaction);

        PreparedStatement pstm = transaction.prepareStatement(SELECT_BALANCE_QUERY);
        pstm = spy(pstm);
        Mockito.when(transaction.prepareStatement(SELECT_BALANCE_QUERY)).thenReturn(pstm);
        Mockito.doThrow(new SQLException("","",ABMFOperation.QUERY_TIMEOUT_ERROR_CODE,null )).when(pstm).executeQuery();

        abmfOperation.getNonMonitoryBalance(createSprInfo(SUBSCRIBER_ID), transactionFactory);

    }

    @Test
    public void when_transaction_end_throws_exception_it_must_handle_and_return_successful_result_no_assertion_needed_resultset_is_delayed_purposefully_to_test_system_alert() throws Exception{

        transactionFactory = spy(transactionFactory);
        Transaction transaction = transactionFactory.createTransaction();
        transaction = spy(transaction);
        doReturn(transaction).when(transactionFactory).createTransaction();
        doThrow(new TransactionException("exception", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).end();


        abmfOperation.getNonMonitoryBalance(createSprInfo(SUBSCRIBER_ID), transactionFactory);

    }

    @Test
    public void futureBalancesShouldBeFilteredOutWhenFetchingBalancesFromDb() throws OperationFailedException {

        SPRInfoImpl sprInfo = getSprInfo();

        SubscriberNonMonitoryBalance balance = abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo,
                basePackageId,null, helper.getSubscriptions(), transactionFactory);

        Assert.assertNull(balance.getBalanceById("future_package_id"));
    }

    @Test
    public void futureBalancesShouldBeFilteredWhenFetchingBalanceFromDatabaseOnReceivingTheEngineRequest() throws OperationFailedException {

        SPRInfoImpl sprInfo = getSprInfo();

        SubscriberNonMonitoryBalance balance = abmfOperation.getNonMonitoryBalance(sprInfo, transactionFactory);
        Assert.assertNull(balance.getBalanceById("future_package_id"));
    }

    @Test
    public void newlyAddedBalanceForNextBillingCycleShouldBeFilteredWhenProvisioningQuotaInBetweenTwoDaysBeforeBalanceExpiry() throws Exception {

        clearExistingDataBalances();
        prepareDataBalanceForRenewal(new Timestamp(currentTime+dayInterval), new Timestamp(currentTime-monthInterval));
        SPRInfoImpl sprInfo = getSprInfo();
        DataBalanceResetDBOperation dataBalanceResetDBOperation = mock(DataBalanceResetDBOperation.class);

        NonMonetoryBalance updatedBalanceList = prepareNonMonetaryBalances(currentTime+dayInterval+1, currentTime+monthInterval, "1:MONTH", ResetBalanceStatus.RESET);

        abmfOperation = new ABMFOperation(alertListener,policyRepository,0,0, new FixedTimeSource(currentTime), new ABMFResetOperations(balanceResetOperation, null, dataBalanceResetDBOperation,new FixedTimeSource(currentTime), new DataBalanceOperationFactory()));
        doReturn(updatedBalanceList).when(balanceResetOperation).performDataBalanceOperations(any(NonMonetoryBalance.class),eq(sprInfo));
        doNothing().when(dataBalanceResetDBOperation).executeUpdateOperations(anyListOf(NonMonetoryBalance.class), any(Transaction.class), anyInt());
        doNothing().when(dataBalanceResetDBOperation).executeInsertOperations(anyListOf(NonMonetoryBalance.class), any(Transaction.class), anyInt());

        SubscriberNonMonitoryBalance balance = abmfOperation.getNonMonitoryBalance(sprInfo, transactionFactory);

        Assert.assertNull(balance.getBalanceById("20"));
    }

    @Test
    public void newlyAddedBalanceForNextBillingCycleShouldBeUsedWhenProvisioningQuotaAfterBalanceExpiry() throws Exception {

        clearExistingDataBalances();
        prepareDataBalanceForRenewal(new Timestamp(currentTime-dayInterval), new Timestamp(currentTime-monthInterval));
        SPRInfoImpl sprInfo = getSprInfo();

        NonMonetoryBalance updatedBalanceList = prepareNonMonetaryBalances(currentTime-dayInterval+1, currentTime+monthInterval, "1:MONTH", ResetBalanceStatus.RESET);

        CarryForwardOperation carryForwardOperation = mock(CarryForwardOperation.class);

        doNothing().when(carryForwardOperation).performCarryForwardOperation(any(NonMonetoryBalance.class), any(DataBalanceOperation.class), anyListOf(NonMonetoryBalance.class));
        abmfResetOperations = new ABMFResetOperations(balanceResetOperation, carryForwardOperation, new DataBalanceResetDBOperation(10, alertListener), new FixedTimeSource(currentTime), new DataBalanceOperationFactory());
        abmfOperation = new ABMFOperation(alertListener,policyRepository,0,0, new FixedTimeSource(currentTime), abmfResetOperations);
        doReturn(updatedBalanceList).when(balanceResetOperation).performDataBalanceOperations(any(NonMonetoryBalance.class),eq(sprInfo));

        SubscriberNonMonitoryBalance balance = abmfOperation.getNonMonitoryBalance(sprInfo, transactionFactory);

        Assert.assertNotNull(balance.getBalanceById("20"));
    }

    private NonMonetoryBalance prepareNonMonetaryBalances(long billingCycleStartTime, long billingCycleResetTime, String renewalInterval, ResetBalanceStatus status) {
        return createServiceRGNonMonitoryBalances("20", "renewable_package_id" , billingCycleStartTime, billingCycleResetTime, status, renewalInterval, SUBSCRIPTION_ID);
    }

    private void clearExistingDataBalances() throws Exception {
        helper.executeQuery("DELETE FROM TBLM_DATA_BALANCE");
    }

    private SPRInfoImpl getSprInfo() {
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(SUBSCRIBER_ID);
        sprInfo.setProductOffer("test");
        return sprInfo;
    }

    private void prepareDataBalanceForRenewal(Timestamp startTime, Timestamp expiryTime) throws Exception {
        TblmDataBalanceEntity balanceToBeRenewed = new TblmDataBalanceEntity( "10", SUBSCRIBER_ID, "renewable_package_id", null,"Quota_"+promotionalPackageId, DATA_SERVICE_TYPE_1,
                0, RATING_GROUP_1, 32212254720L, 32212254720L,
                36000l, 36000l, 1073741824l, 0l,
                7516192768L, 0l, new Timestamp(currentTime), new Timestamp(currentTime), startTime,
                expiryTime, 0l,100l,  new Timestamp(currentTime), ResetBalanceStatus.RESET.name(), "1:MONTH", null, CarryForwardStatus.NOT_CARRY_FORWARD.name());
        helper.executeQuery(QueryBuilder.buildInsertQuery(balanceToBeRenewed));
    }

    private NonMonetoryBalance createServiceRGNonMonitoryBalances(String id, String packageId, long startTime, long billingCycleResetTime, ResetBalanceStatus status, String renewalInterval, String subscriptionId) {
        Random random = new Random();
        NonMonetoryBalance serviceRgNonMonitoryBalance = new NonMonetoryBalance.NonMonetaryBalanceBuilder(id,
                RandomUtils.nextInt(0, Integer.MAX_VALUE),
                packageId,
                1l,
                SUBSCRIBER_ID,
                subscriptionId,
                random.nextInt(1),
                "Quota_"+promotionalPackageId,
                status,
                renewalInterval,
                null)
                .withBillingCycleVolumeBalance(1000, 1000)
                .withBillingCycleTimeBalance(1000,1000)
                .withStartTime(startTime)
                .withBillingCycleResetTime(billingCycleResetTime)
                .withCarryForwardStatus(CarryForwardStatus.NOT_CARRY_FORWARD)
                .build();
        return serviceRgNonMonitoryBalance;
    }
    @After
    public void dropTables() throws Exception {
        Connection connection = transactionFactory.getConnection();
        if(Objects.nonNull(connection)) {
            DBUtility.closeQuietly(connection);
        }
        helper.dropTables();
        DerbyUtil.closeDerby(testDB);
    }

    private boolean compareBalanceWithDBEntry(NonMonetoryBalance nonMonetoryBalance){

        TblmDataBalanceEntity dbBalance = helper.getDbBalance().get(nonMonetoryBalance.getId());

        if (dbBalance == null) {
            return false;
        }

        if (getInRoundedMiliSeconds(dbBalance.getDailyResetTime().getTime()) == nonMonetoryBalance.getDailyResetTime() &&
                dbBalance.getDailyTime() == nonMonetoryBalance.getDailyTime() &&
                dbBalance.getDailyVolume() == nonMonetoryBalance.getDailyVolume()) {
            // Do nothing
        } else if (0 == nonMonetoryBalance.getDailyVolume() &&
                0 == nonMonetoryBalance.getDailyTime()) {
            // Do nothing
        } else {
            return false;
        }

        if (getInRoundedMiliSeconds(dbBalance.getWeeklyResetTime().getTime()) == nonMonetoryBalance.getWeeklyResetTime() &&
                dbBalance.getWeeklyTime() == nonMonetoryBalance.getWeeklyTime() &&
                dbBalance.getWeeklyVolume() == nonMonetoryBalance.getWeeklyVolume()) {
            // Do nothing
        } else if (0 == nonMonetoryBalance.getWeeklyVolume() &&
                0 == nonMonetoryBalance.getWeeklyTime()) {
            // Do nothing
        } else {
            return false;
        }

        return true;
    }

    private long getInRoundedMiliSeconds(long milliSeconds){
        return (milliSeconds/1000)*1000;
    }

    private static void createTablesAndInsertUsageRecords() throws Exception {
        LogManager.getLogger().debug("test", "creating DB");

        helper.createTables();
        helper.addBalanceDetailsInTable();

        LogManager.getLogger().debug("test", "DB created");
    }

    private class ABMFOperationTestHelper {

        public TransactionFactory transactionFactory;
        public String basePackageId = "BASE_1";
        public String addOnId = "AddOn_1";
        public String promotionalPackageId = "Promotion_1";
        public String invalidPackageId = "Invalid_1";
        public BasePackage basePackage=null;
        public AddOn userPackage=null;
        public PromotionalPackage promotionalPackage=null;
        private long DATA_SERVICE_TYPE_1 = 1;
        private long DATA_SERVICE_TYPE_4 = 4;
        private String SUBSCRIPTION_ID="123456";
        private String INVALID_SUBSCRIPTION_ID="1234567";
        private String SUBSCRIBER_ID="123123";
        public String currency = "INR";

        public Map<String,TblmDataBalanceEntity> dbBalance = null;

        public  ABMFOperationTestHelper(TransactionFactory transactionFactory){
            this.transactionFactory = transactionFactory;
        }

        public void createTables() throws Exception {
            executeQuery(QueryBuilder.buildCreateQuery(TblmDataBalanceEntity.class));
        }

        public void dropTables() throws Exception {
            executeQuery(QueryBuilder.buildDropQuery(TblmDataBalanceEntity.class));
            getLogger().debug(this.getClass().getSimpleName(), "Tables Dropped");
        }

        public List<TblmDataBalanceEntity> getBalanceList(){

            List<TblmDataBalanceEntity> list = new ArrayList<>();




            TblmDataBalanceEntity base1 = new TblmDataBalanceEntity( "1",  SUBSCRIBER_ID, basePackageId, null, "Quota_"+basePackageId, DATA_SERVICE_TYPE_1,
                    0,  RATING_GROUP_1, 32212254720L, 32212254720L,
                    36000l, 36000l, 1073741824l,  0l,
                    7516192768L, 0l,  new Timestamp(currentTime+dayInterval), new Timestamp(currentTime+weekInterval),new Timestamp(currentTime+monthInterval),
                    new Timestamp(currentTime), 0l, 100l,new Timestamp(currentTime), null, null, null, CarryForwardStatus.NOT_CARRY_FORWARD.name());

            TblmDataBalanceEntity base2 = new TblmDataBalanceEntity( "2", SUBSCRIBER_ID, basePackageId, null,"Quota_"+basePackageId, DATA_SERVICE_TYPE_4,
                    0, RATING_GROUP_1,  32212254720L, 32212254720L,
                    36000l, 36000l, 1073741824l,  0l,
                    7516192768L, 0l, new Timestamp(currentTime-dayInterval), new Timestamp(currentTime-weekInterval),new Timestamp(currentTime+ourInterval),
                    new Timestamp(currentTime), 0l,100l, new Timestamp(currentTime), null, null, null, CarryForwardStatus.NOT_CARRY_FORWARD.name());

            TblmDataBalanceEntity base3 = new TblmDataBalanceEntity( "3", SUBSCRIBER_ID, basePackageId, null,"Quota_"+basePackageId, DATA_SERVICE_TYPE_1,
                    1, RATING_GROUP_1, 32212254720L, 32212254720L,
                    36000l, 36000l, 1073741824l,  0l,
                    7516192768L, 0l, new Timestamp(currentTime-dayInterval), new Timestamp(currentTime+weekInterval),new Timestamp(currentTime+monthInterval),
                    new Timestamp(currentTime), 0l,100l,  new Timestamp(currentTime), null, null, null, CarryForwardStatus.NOT_CARRY_FORWARD.name());

            TblmDataBalanceEntity base4 = new TblmDataBalanceEntity( "4", SUBSCRIBER_ID, basePackageId, null,"Quota_"+basePackageId, DATA_SERVICE_TYPE_1,
                    2, RATING_GROUP_1, 32212254720L, 32212254720L,
                    36000l, 36000l, 1073741824l, 0l,
                    7516192768L, 0l, new Timestamp(currentTime-dayInterval), new Timestamp(currentTime+weekInterval),new Timestamp(currentTime+monthInterval),
                    new Timestamp(currentTime), 0l,100l,  new Timestamp(currentTime), null, null, null, CarryForwardStatus.NOT_CARRY_FORWARD.name());


            TblmDataBalanceEntity addOn1 = new TblmDataBalanceEntity( "5", SUBSCRIBER_ID, addOnId, SUBSCRIPTION_ID,"Quota_"+addOnId, DATA_SERVICE_TYPE_1,
                    0, RATING_GROUP_1, 32212254720L, 32212254720L,
                    36000l, 36000l, 1073741824l, 0l,
                    7516192768L, 0l, new Timestamp(currentTime-dayInterval), new Timestamp(currentTime+weekInterval),new Timestamp(currentTime+monthInterval),
                    new Timestamp(currentTime), 0l,100l,  new Timestamp(currentTime), null, null, null, CarryForwardStatus.NOT_CARRY_FORWARD.name());

            TblmDataBalanceEntity addOn2 = new TblmDataBalanceEntity( "6", SUBSCRIBER_ID, addOnId, SUBSCRIPTION_ID,"Quota_"+addOnId, DATA_SERVICE_TYPE_4,
                    0, RATING_GROUP_1, 32212254720L, 32212254720L,
                    36000l, 36000l, 1073741824l,  0l,
                    7516192768L, 0l, new Timestamp(currentTime-dayInterval), new Timestamp(currentTime+weekInterval),new Timestamp(currentTime+monthInterval),
                    new Timestamp(currentTime), 0l,100l,  new Timestamp(currentTime), null, null, null, CarryForwardStatus.NOT_CARRY_FORWARD.name());

            TblmDataBalanceEntity addOn3 = new TblmDataBalanceEntity( "7", SUBSCRIBER_ID, addOnId, SUBSCRIPTION_ID,"Quota_"+addOnId, DATA_SERVICE_TYPE_1,
                    1, RATING_GROUP_1, 32212254720L, 32212254720L,
                    36000l, 36000l, 1073741824l, 0l,
                    7516192768L, 0l, new Timestamp(currentTime-dayInterval), new Timestamp(currentTime+weekInterval),new Timestamp(currentTime+monthInterval),
                    new Timestamp(currentTime), 0l,100l,  new Timestamp(currentTime), null, null, null, CarryForwardStatus.NOT_CARRY_FORWARD.name());


            TblmDataBalanceEntity addOn4 = new TblmDataBalanceEntity( "8", SUBSCRIBER_ID, addOnId, SUBSCRIPTION_ID,"Quota_"+addOnId, DATA_SERVICE_TYPE_1,
                    2, RATING_GROUP_1, 32212254720L, 32212254720L,
                    36000l, 36000l, 1073741824l,  0l,
                    7516192768L, 0l, new Timestamp(currentTime-dayInterval), new Timestamp(currentTime+weekInterval),new Timestamp(currentTime+monthInterval),
                    new Timestamp(currentTime), 0l,100l,  new Timestamp(currentTime), null, null, null, CarryForwardStatus.NOT_CARRY_FORWARD.name());


            TblmDataBalanceEntity promo1 = new TblmDataBalanceEntity( "9", SUBSCRIBER_ID, promotionalPackageId, null,"Quota_"+promotionalPackageId, DATA_SERVICE_TYPE_1,
                    2, RATING_GROUP_1, 32212254720L, 32212254720L,
                    36000l, 36000l, 1073741824l, 0l,
                    7516192768L, 0l, new Timestamp(currentTime-dayInterval), new Timestamp(currentTime+weekInterval),new Timestamp(currentTime+monthInterval),
                    new Timestamp(currentTime), 0l,100l,  new Timestamp(currentTime), null, null, null, CarryForwardStatus.NOT_CARRY_FORWARD.name());


            TblmDataBalanceEntity promo2 = new TblmDataBalanceEntity( "10", SUBSCRIBER_ID, promotionalPackageId, null,"Quota_"+promotionalPackageId, DATA_SERVICE_TYPE_4,
                    0, RATING_GROUP_1, 32212254720L, 32212254720L,
                    36000l, 36000l, 1073741824l,  0l,
                    7516192768L, 0l, new Timestamp(currentTime-dayInterval), new Timestamp(currentTime+weekInterval),new Timestamp(currentTime+monthInterval),
                    new Timestamp(currentTime), 0l,100l,  new Timestamp(currentTime), null, null, null, CarryForwardStatus.NOT_CARRY_FORWARD.name());


            TblmDataBalanceEntity promo3 = new TblmDataBalanceEntity( "11", SUBSCRIBER_ID, promotionalPackageId, null,"Quota_"+promotionalPackageId, DATA_SERVICE_TYPE_4,
                    1, RATING_GROUP_1, 32212254720L, 32212254720L,
                    36000l, 36000l, 1073741824l,  0l,
                    7516192768L, 0l, new Timestamp(currentTime-dayInterval), new Timestamp(currentTime+weekInterval),new Timestamp(currentTime+monthInterval),
                    new Timestamp(currentTime), 0l,100l,  new Timestamp(currentTime), null, null, null, CarryForwardStatus.NOT_CARRY_FORWARD.name());

            TblmDataBalanceEntity promo4 = new TblmDataBalanceEntity( "12", SUBSCRIBER_ID, promotionalPackageId, null,"Quota_"+promotionalPackageId, DATA_SERVICE_TYPE_4,
                    2, RATING_GROUP_1, 32212254720L, 32212254720L,
                    36000l, 36000l, 1073741824l, 0l,
                    7516192768L, 0l, new Timestamp(currentTime-dayInterval), new Timestamp(currentTime+weekInterval),new Timestamp(currentTime+monthInterval),
                    new Timestamp(currentTime), 0l,100l,  new Timestamp(currentTime), null, null, null, CarryForwardStatus.NOT_CARRY_FORWARD.name());

            TblmDataBalanceEntity invalid1 = new TblmDataBalanceEntity( "13", "invalid_subscriber_id", invalidPackageId, INVALID_SUBSCRIPTION_ID,"Quota_"+invalidPackageId, DATA_SERVICE_TYPE_1,
                    0, RATING_GROUP_1, 32212254720L, 32212254720L,
                    36000l, 36000l, 1073741824l, 0l,
                    7516192768L, 0l, new Timestamp(currentTime-dayInterval), new Timestamp(currentTime+weekInterval),new Timestamp(currentTime+monthInterval),
                    new Timestamp(currentTime), 0l,100l,  new Timestamp(currentTime), null, null, null, CarryForwardStatus.NOT_CARRY_FORWARD.name());


            TblmDataBalanceEntity invalid2 = new TblmDataBalanceEntity( "14", SUBSCRIBER_ID, invalidPackageId, INVALID_SUBSCRIPTION_ID,"Quota_"+promotionalPackageId, DATA_SERVICE_TYPE_1,
                    1, RATING_GROUP_1, 32212254720L, 32212254720L,
                    36000l, 36000l, 1073741824l, 0l,
                    7516192768L, 0l, new Timestamp(currentTime-dayInterval), new Timestamp(currentTime+weekInterval),new Timestamp(currentTime+monthInterval),
                    new Timestamp(currentTime), 0l,100l,  new Timestamp(currentTime), null, null, null, CarryForwardStatus.NOT_CARRY_FORWARD.name());

            TblmDataBalanceEntity invalid3 = new TblmDataBalanceEntity( "15", SUBSCRIBER_ID, invalidPackageId, INVALID_SUBSCRIPTION_ID,"Quota_"+promotionalPackageId, DATA_SERVICE_TYPE_1,
                    2, RATING_GROUP_1, 32212254720L, 32212254720L,
                    36000l, 36000l, 1073741824l, 0l,
                    7516192768L, 0l, new Timestamp(currentTime-dayInterval), new Timestamp(currentTime+weekInterval),new Timestamp(currentTime+monthInterval),
                    new Timestamp(currentTime), 0l,100l,  new Timestamp(currentTime), null, null, null, CarryForwardStatus.NOT_CARRY_FORWARD.name());

            TblmDataBalanceEntity futureBase = new TblmDataBalanceEntity( "16", SUBSCRIBER_ID, "future_package_id", "012345","Quota_"+promotionalPackageId, DATA_SERVICE_TYPE_1,
                    0, RATING_GROUP_1, 32212254720L, 32212254720L,
                    36000l, 36000l, 1073741824l, 0l,
                    7516192768L, 0l, new Timestamp(currentTime+2*dayInterval), new Timestamp(currentTime+2*weekInterval),new Timestamp(currentTime+monthInterval),
                    new Timestamp(currentTime+dayInterval), 0l,100l,  new Timestamp(currentTime), ResetBalanceStatus.RESET.name(), null, null, CarryForwardStatus.NOT_CARRY_FORWARD.name());

            list.add(base1);
            list.add(base2);
            list.add(base3);
            list.add(base4);
            list.add(addOn1);
            list.add(addOn2);
            list.add(addOn3);
            list.add(addOn4);
            list.add(promo1);
            list.add(promo2);
            list.add(promo3);
            list.add(promo4);
            list.add(invalid1);
            list.add(invalid2);
            list.add(invalid3);
            list.add(futureBase);

            // So that it can be retrieved during case verification
            dbBalance = list.stream().collect(Collectors.toMap(TblmDataBalanceEntity::getId, Function.identity()));

            return list;
        }

        public Map<String, TblmDataBalanceEntity> getDbBalance() {
            return dbBalance;
        }

        public void addBalanceDetailsInTable() throws Exception{
            List<TblmDataBalanceEntity> list = getBalanceList();

            for (TblmDataBalanceEntity subscriberBalance : list) {
                executeQuery(QueryBuilder.buildInsertQuery(subscriberBalance));
            }

        }

        public Map<String, Subscription> getSubscriptions(){
            Map<String, Subscription> addOnSubscriptions = new HashMap<>();

            Subscription subscription_1 = new Subscription(SUBSCRIPTION_ID, SUBSCRIBER_ID, addOnId,productOfferId, null, new Timestamp(System.currentTimeMillis()+3600000*6), SubscriptionState.STARTED,CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON,
                    null, null);

            Subscription subscription_2= new Subscription("123456789", SUBSCRIBER_ID, basePackageId,productOfferId, null, null, SubscriptionState.STARTED,CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON,
                    null, null);

            Subscription subscription_3= new Subscription("1234567890", SUBSCRIBER_ID, "invalidPackageId", null, null, null, SubscriptionState.STARTED,CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON,
                    null, null);

            addOnSubscriptions.put(SUBSCRIPTION_ID,subscription_1);
            addOnSubscriptions.put("123456789",subscription_2);
            addOnSubscriptions.put("1234567890",subscription_3);

            return addOnSubscriptions;
        }

        public void createPackages(){

            basePackage = new BasePackage(basePackageId,basePackageId, QuotaProfileType.RnC_BASED,
                    PkgStatus.ACTIVE,new ArrayList<>(),null, null,"",
                    0.0,null,null, null,null,null,
                    null,"","", null,currency);
            userPackage = new AddOn(addOnId,addOnId, QuotaProfileType.RnC_BASED,
                    PkgStatus.ACTIVE,new ArrayList<>(),true, false,
                    0,null,null,null,null,
                    null, null,null, null, null,
                    null,null,"","", null,currency);
            promotionalPackage = new PromotionalPackage(promotionalPackageId,promotionalPackageId, QuotaProfileType.RnC_BASED,
                    PkgStatus.ACTIVE,new ArrayList<>(),null, null,"",
                    0.0,null,null,null ,false,
                    null, null,null,null, null,
                    null);

        }

        public List<NonMonetoryBalance> getSubscriberUsageFromDB(String subscriberId) throws Exception {

            ResultSet resultSet = executeSelect("SELECT * FROM TBLM_DATA_BALANCE WHERE SUBSCRIBER_ID = '" + subscriberId + "'");
            List<NonMonetoryBalance> subsriberBalanceData = DBHelper.create(resultSet, NonMonetoryBalance.class);
            return subsriberBalanceData;
        }

        private ResultSet executeSelect(String query) throws Exception {

            Transaction transaction = transactionFactory.createTransaction();
            try {

                transaction.begin();
                return transaction.prepareStatement(query).executeQuery();

            } finally {
                transaction.end();
            }
        }

        public List<QuotaProfile> getQuotaProfiles(String dummyName){
            List<QuotaProfile> quotaProfiles = new ArrayList<QuotaProfile>();
            List<Map<String, QuotaProfileDetail >> fupLevelserviceWiseQuotaProfileDetais= new ArrayList<>();

            RatingGroup rg = new RatingGroup("RATING_GROUP_1", "RATING_GROUP_1", "RATING_GROUP_1", 0);

            Map<String, QuotaProfileDetail > hsq = new HashMap<>();
            Map<String, QuotaProfileDetail > fup1 = new HashMap<>();
            Map<String, QuotaProfileDetail > fup2 = new HashMap<>();

            fupLevelserviceWiseQuotaProfileDetais.add(hsq);
            fupLevelserviceWiseQuotaProfileDetais.add(fup1);
            fupLevelserviceWiseQuotaProfileDetais.add(fup2);



            DataServiceType dataServiceType1 = new DataServiceType("DATA_SERVICE_TYPE_1", "Rg1", 1, Collections.emptyList(), Arrays.asList(rg));
            DataServiceType dataServiceType4 = new DataServiceType("DATA_SERVICE_TYPE_4", "Rg1", 4, Collections.emptyList(), Arrays.asList(rg));

            Map<AggregationKey, AllowedUsage> allowedUsageMap = getAllowedUsage();

            hsq.put("DATA_SERVICE_TYPE_1", new RncProfileDetail("hsq_Quota_"+dummyName, dataServiceType1, 0, rg, allowedUsageMap, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null, null,
                    null, "hsq_Quota_"+dummyName, true, pccProfileName,0,0, "test"));
            hsq.put("DATA_SERVICE_TYPE_4", new RncProfileDetail("hsq_Quota_"+dummyName, dataServiceType4, 0, rg, allowedUsageMap, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null, null,
                    null, "hsq_Quota_"+dummyName, true, pccProfileName,0,0, "test"));

            fup1.put("DATA_SERVICE_TYPE_1", new RncProfileDetail("hsq_Quota_"+dummyName, dataServiceType1, 1, rg, allowedUsageMap, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null, null,
                    null, "fup1_Quota_"+dummyName, true, pccProfileName,0,0, "test"));

            fup2.put("DATA_SERVICE_TYPE_1", new RncProfileDetail("hsq_Quota_"+dummyName, dataServiceType1, 2, rg, allowedUsageMap, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null, null,
                    null, "fup2_Quota_"+dummyName, true, pccProfileName,0,0, "test"));

            QuotaProfile quotaProfile = new QuotaProfile("Quota_"+dummyName, dummyName,
                    "Quota_"+dummyName, BalanceLevel.HSQ,2, RenewalIntervalUnit.MONTH, QuotaProfileType.RnC_BASED,
                    fupLevelserviceWiseQuotaProfileDetais, CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue());

            quotaProfiles.add(quotaProfile);

            return quotaProfiles;
        }

        private Map<AggregationKey, AllowedUsage> getAllowedUsage(){
            Map<AggregationKey, AllowedUsage> allowedUsageMap = new HashMap<>();

            AllowedUsage daily = new DailyAllowedUsage(1024*30,512*30,512*30,7200*30, DataUnit.MB, DataUnit.MB, DataUnit.MB, TimeUnit.SECOND);
            AllowedUsage weekly = new DailyAllowedUsage(1024*7,512*7,512*7,7200*7, DataUnit.MB, DataUnit.MB, DataUnit.MB, TimeUnit.SECOND);
            AllowedUsage billingCycle = new DailyAllowedUsage(1024,512,512,7200, DataUnit.MB, DataUnit.MB, DataUnit.MB, TimeUnit.SECOND);

            allowedUsageMap.put(AggregationKey.DAILY, daily);
            allowedUsageMap.put(AggregationKey.WEEKLY, weekly);
            allowedUsageMap.put(AggregationKey.BILLING_CYCLE, billingCycle);

            return  allowedUsageMap;
        }

        public UserPackage getUserPackage(String key){
            switch (key){
                case "base":
                    return basePackage;
                case "promo":
                    return promotionalPackage;
                default:
                    return userPackage;
            }
        }

        private void executeQuery(String query) throws Exception {
            Transaction transaction = transactionFactory.createTransaction();
            try {

                transaction.begin();
                transaction.prepareStatement(query).execute();

            } finally {
                transaction.end();
            }
        }
    }

    private SPRInfo createSprInfo(String subscriberIdentity) {
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(subscriberIdentity);
        return sprInfo;
    }
}
