package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.pm.util.MockBasePackage;
import com.elitecore.corenetvertex.spr.SubscriberRepositoryTestSuite.SPROperationTestHelper;
import com.elitecore.corenetvertex.spr.SubscriberRepositoryTestSuite.UMConfigurationImpl;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfo.SubscriberMode;
import com.elitecore.corenetvertex.spr.data.SubscriberDetails;
import com.elitecore.corenetvertex.spr.data.SubscriberProfileData;
import com.elitecore.corenetvertex.spr.data.TestSubscriberData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.DerbyUtil;
import junitparams.JUnitParamsRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class TestSubscriberEnabledSPInterfaceTest {

	private static final String DS_NAME = "test-DB";
	private DummyTransactionFactoryBuilder builder;
	private DummyTransactionFactory transactionFactory;
	private SPROperationTestHelper helper;
	private UMConfigurationImpl umConfiguration;
	private ABMFconfigurationImpl abmFconfiguration;
	private TestSubscriberCache testSubscriberCache;
	private UMOperation umOperation;
	private static final String currency = "INR";

	@Mock
	PolicyRepository policyRepository;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

	}

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);
		DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, "jdbc:derby:memory:TestingDB;create=true", "", "", 1, 5000, 3000);
		umConfiguration = new UMConfigurationImpl();
		abmFconfiguration = new ABMFconfigurationImpl(1, 1, 1);
		builder = spy(new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1));

		transactionFactory = (DummyTransactionFactory) builder.build();

		when(builder.build()).thenReturn(transactionFactory);

		testSubscriberCache = new TestSubscriberCache(transactionFactory, mock(AlertListener.class));
		
		umOperation = new UMOperationBuilder(mock(AlertListener.class), policyRepository, null).withUMConf(umConfiguration).withTransactionFactory(transactionFactory).build();

		helper = new SPROperationTestHelper(transactionFactory);
		helper.createProfileTable();
	}
	
	
	@Test
	public void test_getProfile_should_give_profile_with_testSubscriber_flag_if_subscriberId_is_test_subscriber() throws Exception {
		String subscriberId = "101";
		setUp_test_getProfile_should_give_testSubscriberProfile_if_subscriberId_is_test_subscriber(subscriberId);
		
		SubscriberRepository operation = new SubscriberRepositoryImpl(null, null, builder.build(), mock(AlertListener.class), null, umOperation, abmFconfiguration, testSubscriberCache,
				Collectionz.<String>newArrayList(), null, null, null, null, null, null, null, "INR");

		operation.addTestSubscriber(subscriberId);
		
		SPRInfo profile = operation.getProfile(subscriberId);
		
		assertSame(SubscriberMode.TEST, profile.getSubscriberMode());
	}

	private void setUp_test_getProfile_should_give_testSubscriberProfile_if_subscriberId_is_test_subscriber(String subscriberId) throws Exception,
			IllegalAccessException, InvocationTargetException {
		helper.insertProfile(getSubscriberProfileData(subscriberId));
		TestSubscriberData testSubscriberData = new TestSubscriberData(subscriberId);
		helper.executeQuery(testSubscriberData.insertQuery());
	}
	
	@Test
	public void test_addProfile_if_profile_is_TEST_subscriber_then_it_should_add_record_in_TestSubscriberCache() throws Exception {
		SPRInfo sprInfo = getSubscriberProfile(SubscriberMode.TEST, "pkgName", "111");
		PolicyRepository policyRepository = setUpFor_test_addprofile_if_profile_is_LIVE_subscriber_then_it_should_not_add_if_subscribed_package_is_in_TEST_mode("pkgName");
		SubscriberRepository repository = new SubscriberRepositoryImpl(null, null, builder.build(), mock(AlertListener.class), policyRepository, umOperation, abmFconfiguration, testSubscriberCache,
				Collectionz.<String>newArrayList(), null,  null, null, null, null, null, null, "INR");
		
		repository.addProfile(new SubscriberDetails(sprInfo,null), null);
		
		assertTrue(repository.isTestSubscriber(sprInfo.getSubscriberIdentity()));
	}
	
	
	/**
	 * LIVE subscriber with LIVE package only
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_addProfile_if_profile_is_LIVE_subscriber_then_it_should_not_add_record_in_TestSubscriberCache() throws Exception {
		String subscribedPkgName = "pkgName";
		SPRInfo sprInfo = getSubscriberProfile(SubscriberMode.LIVE, subscribedPkgName, "111");
		PolicyRepository policyRepository = setUpFor_test_addProfile_if_profile_is_LIVE_subscriber_then_it_should_not_add_record_in_TestSubscriberCache(subscribedPkgName);
		SubscriberRepository repository = new SubscriberRepositoryImpl(null, null, builder.build(), mock(AlertListener.class), policyRepository , umOperation, abmFconfiguration, testSubscriberCache,
				Collectionz.<String>newArrayList(), null, null, null, null, null, null, null, "INR");

		
		repository.addProfile(new SubscriberDetails(sprInfo,null), null);
		
		assertFalse(repository.isTestSubscriber(sprInfo.getMsisdn()));
		
	}

	private PolicyRepository setUpFor_test_addProfile_if_profile_is_LIVE_subscriber_then_it_should_not_add_record_in_TestSubscriberCache(
			String subscribedPkgName) {
		PolicyRepository policyRepository = mock(PolicyRepository.class);
        MockBasePackage mockBasePackage = MockBasePackage.create(UUID.randomUUID().toString(), subscribedPkgName);
        mockBasePackage.modeIsLive();
        mockBasePackage.statusActive();
        doReturn(mockBasePackage).when(policyRepository).getBasePackageDataByName(subscribedPkgName);
		ProductOfferStore productOfferStore = spy(ProductOfferStore.class);
		doReturn(productOfferStore).when(policyRepository).getProductOffer();

		ProductOffer productOffer = new ProductOffer("test", "test", null, PkgType.BASE, PkgMode.LIVE, 0, ValidityPeriodUnit.DAY,
				0.0, 0.0, PkgStatus.ACTIVE, null, null, mockBasePackage.getId(), null, null,
				null, PolicyStatus.SUCCESS, null, null,false, null, null, policyRepository,null,null,new HashMap<>(),currency);

		doReturn(productOffer).when(productOfferStore).byName(subscribedPkgName);

		return policyRepository;
	}
	
	private PolicyRepository setUpFor_test_addprofile_if_profile_is_LIVE_subscriber_then_it_should_not_add_if_subscribed_package_is_in_TEST_mode(
			String subscribedPkgName) {
		PolicyRepository policyRepository = mock(PolicyRepository.class);
        MockBasePackage mockBasePackage = MockBasePackage.create(UUID.randomUUID().toString(), subscribedPkgName);
        mockBasePackage.modeIsTest();
        mockBasePackage.statusActive();
        doReturn(mockBasePackage).when(policyRepository).getBasePackageDataByName(subscribedPkgName);

		ProductOfferStore productOfferStore = spy(ProductOfferStore.class);
		doReturn(productOfferStore).when(policyRepository).getProductOffer();

		ProductOffer productOffer = new ProductOffer("test", "test", null, PkgType.BASE, PkgMode.TEST, 0, ValidityPeriodUnit.DAY,
				0.0,0.0, PkgStatus.ACTIVE, null, null, mockBasePackage.getId(), null, null,
				null, PolicyStatus.SUCCESS, null, null,false, null, null, policyRepository,null,null,new HashMap<>(),currency);

		doReturn(productOffer).when(productOfferStore).byName(subscribedPkgName);


		return policyRepository;
	}
	
	@Test
	public void test_addprofile_if_profile_is_LIVE_subscriber_then_it_should_not_add_if_subscribed_package_is_in_TEST_mode() throws Exception {
		String subscribedPkgName = "pkgName";
		SPRInfo sprInfo = getSubscriberProfile(SubscriberMode.LIVE, subscribedPkgName, null);
		PolicyRepository policyRepository = setUpFor_test_addprofile_if_profile_is_LIVE_subscriber_then_it_should_not_add_if_subscribed_package_is_in_TEST_mode(subscribedPkgName);
		SubscriberRepository operation = new SubscriberRepositoryImpl(null, null, builder.build(), mock(AlertListener.class), policyRepository , umOperation, abmFconfiguration, testSubscriberCache,
				Collectionz.<String>newArrayList(), null,  null, null, null, null, null, null, "INR");
		
		expectedException.expect(OperationFailedException.class);
		expectedException.expectMessage("Subscriber add operation failed."
						+ " Reason: Live subscriber must not have Test package("
						+ subscribedPkgName + ")");
		
		operation.addProfile(new SubscriberDetails(sprInfo,null), null);
	}
	
	private SubscriberProfileData getSubscriberProfileData(String subscriberIdentity) {
		
		SubscriberProfileData data1 = new SubscriberProfileData.SubscriberProfileDataBuilder()
		.withSubscriberIdentity(subscriberIdentity)
		.withImsi("1234")
		.withMsisdn("9797979110")
		.withUserName("user110")
		.withPassword("user110")
		.withPhone("123110")
		.withBirthdate(new Timestamp(new Date().getTime()))
		.withExpiryDate(new Timestamp(System.currentTimeMillis()))
		.build();

		return data1;
	}
	
	private SPRInfo getSubscriberProfile(SubscriberMode subscriberMode, String packageName, String subscriberIdentity) throws OperationFailedException {
		SubscriberProfileData data = getSubscriberProfileData(subscriberIdentity);
		SPRInfo info = data.getSPRInfo();
		info.setProductOffer(packageName);
		info.setSubscriberMode(subscriberMode);

        MockBasePackage mockBasePackage = MockBasePackage.create(UUID.randomUUID().toString(), packageName);
        mockBasePackage.statusActive();
        doReturn(mockBasePackage).when(policyRepository).getBasePackageDataByName(packageName);
		return info;
	}
	
	@After
	public void afterDropTables() throws Exception {
		helper.dropProfileTables();
		DBUtility.closeQuietly(transactionFactory.getConnection());
		DerbyUtil.closeDerby("TestingDB");

	}

}
