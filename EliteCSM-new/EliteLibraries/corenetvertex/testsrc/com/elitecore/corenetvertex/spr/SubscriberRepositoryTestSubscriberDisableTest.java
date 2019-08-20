package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.factory.ProductOfferDataFactory;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.pm.util.MockAddOnPackage;
import com.elitecore.corenetvertex.spr.SubscriberRepositoryTestSuite.SPROperationTestHelper;
import com.elitecore.corenetvertex.spr.SubscriberRepositoryTestSuite.UMConfigurationImpl;
import com.elitecore.corenetvertex.spr.SubscriptionOperationTestSuite.SubscriptionDBHelper;
import com.elitecore.corenetvertex.spr.data.SubscriberProfileData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import junitparams.JUnitParamsRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

import static com.elitecore.corenetvertex.spr.SubscriberRepositoryTestSuite.ABMFConfigurationImpl;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;


@RunWith(JUnitParamsRunner.class)
public class SubscriberRepositoryTestSubscriberDisableTest {

	
	private static final String DS_NAME = "test-DB";
	private DummyTransactionFactoryBuilder builder;
	private SPROperationTestHelper helper;
	private SubscriptionDBHelper subscriptionDBHelper;
	private ABMFConfigurationImpl abmFconfiguration;
	private UMOperation umOperation;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private DummyTransactionFactory transactionFactory;
	
	@Mock
	AlertListener alertListener;
	@Mock
	PolicyRepository policyRepository;
	@Mock
	ProductOfferStore productOfferStore;
	@Mock
	EDRListener subscriberEDListener;
	private HibernateSessionFactory hibernateSessionFactory;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		String ssid = UUID.randomUUID().toString();
		String url = "jdbc:h2:mem:" + ssid;
		DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, url, "", "", 1, 5000, 3000);
		UMConfigurationImpl umConfiguration = new UMConfigurationImpl();
		abmFconfiguration = new ABMFConfigurationImpl();
		builder = spy(new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1));

		transactionFactory = (DummyTransactionFactory) builder.build();
		umOperation = new UMOperationBuilder(mock(AlertListener.class), policyRepository, null).withUMConf(umConfiguration).build();

		when(builder.build()).thenReturn(transactionFactory);
		transactionFactory.createTransaction();

		helper = new SPROperationTestHelper(transactionFactory);
		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.connection.url", url);
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		hibernateSessionFactory = HibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml", hibernateProperties);
		subscriptionDBHelper = new SubscriptionDBHelper(transactionFactory);
	}

	/**
	 * START - NETVERTEX-3068
	 */

	@Test
	public void test_purgeProfile_shouldThrowOperationFailedExceptionWhenSubscriberNotExist() throws OperationFailedException {
		SPInterface spInterface = new NVDBSPInterface(alertListener, transactionFactory);
		SubscriberRepository repository = new SubscriberRepositoryImpl(null,
				null,
				transactionFactory,
				alertListener,
				spInterface,
				policyRepository,
				umOperation,
				abmFconfiguration, null,
				Collectionz.<String>newArrayList(), null, null, null,
				null, null, subscriberEDListener, null,"INR");
		String subscriberId = "101";
		expectedException.expect(OperationFailedException.class);
		expectedException.expectMessage("Skipping purge for subscriber");
		repository.purgeProfile(subscriberId, null);
	}


	@Test
	public void test_markForDeleteProfile_shouldReturnCountOneWhenSubscriberIsMarkedDeleted() throws Exception {
		SPInterface spInterface = new NVDBSPInterface(alertListener, transactionFactory);
		SubscriberRepository repository = new SubscriberRepositoryImpl(null,
				null,
				transactionFactory,
				alertListener,
				spInterface,
				policyRepository,
				umOperation,
				abmFconfiguration, null,
				Collectionz.<String>newArrayList(), null, null, null,
				null, null, subscriberEDListener, null,"INR");

		String subscriberId = "101";
		helper.insertProfile(new SubscriberProfileData.SubscriberProfileDataBuilder().
				withSubscriberIdentity(subscriberId).
				withStatus(SubscriberStatus.ACTIVE.name()).build());
		int actualDeletedCount = repository.markForDeleteProfile(subscriberId, null);
		assertEquals(1, actualDeletedCount);
	}

	@Test
	public void test_markForDeleteProfile_shouldThrowOperationFailedExceptionWhenSubscriberNotExist() throws Exception {
		SPInterface spInterface = new NVDBSPInterface(alertListener, transactionFactory);
		SubscriberRepository repository = new SubscriberRepositoryImpl(null,
				null,
				transactionFactory,
				alertListener,
				spInterface,
				policyRepository,
				umOperation,
				abmFconfiguration, null,
				Collectionz.<String>newArrayList(), null, null, null,
				null, null, subscriberEDListener, null,"INR");
		String subscriberId = "101";
		expectedException.expect(OperationFailedException.class);
		expectedException.expectMessage("not found for delete");
		repository.markForDeleteProfile(subscriberId, null);
	}

	@Test
	public void test_restoreProfile_shouldReturnCountOneWhenSubscriberIsRestored() throws Exception {
		SPInterface spInterface = new NVDBSPInterface(alertListener, transactionFactory);
		SubscriberRepository repository = new SubscriberRepositoryImpl(null,
				null,
				transactionFactory,
				alertListener,
				spInterface,
				policyRepository,
				umOperation,
				abmFconfiguration, null,
				Collectionz.<String>newArrayList(), null, null, null,
				null, null, subscriberEDListener, null,"INR");
		String subscriberId = "101";
		helper.insertProfile(new SubscriberProfileData.SubscriberProfileDataBuilder().
				withSubscriberIdentity(subscriberId).
				withStatus(SubscriberStatus.DELETED.name()).build());
		int actualRestoredCount = repository.restoreProfile(subscriberId, null);
		assertEquals(1, actualRestoredCount);
	}

	@Test
	public void test_restoreProfile_shouldThrowOperationFailedExceptionWhenSubscriberNotExist() throws Exception {
		SPInterface spInterface = new NVDBSPInterface(alertListener, transactionFactory);
		SubscriberRepository repository = new SubscriberRepositoryImpl(null,
				null,
				transactionFactory,
				alertListener,
				spInterface,
				policyRepository,
				umOperation,
				abmFconfiguration, null,
				Collectionz.<String>newArrayList(), null, null, null,
				null, null, subscriberEDListener, null,"INR");
		String subscriberId = "101";
		expectedException.expect(OperationFailedException.class);
		expectedException.expectMessage("not found for restore");
		repository.restoreProfile(subscriberId, null);
	}

	/**
	 * END - NETVERTEX-3068
	 */
	
	@Test
	public void test_purge_should_return_count_one_when_provided_subscriber_purged() throws Exception {
		
		SubscriberRepository repository = new SubscriberRepositoryImpl(null, null,transactionFactory, alertListener,  policyRepository, umOperation, abmFconfiguration, null,
				Collectionz.<String>newArrayList(), null, null, null, null, null, subscriberEDListener, null,"INR");
		
		String subscriberId = "101";
		helper.insertProfile(new SubscriberProfileData.SubscriberProfileDataBuilder().
				withSubscriberIdentity(subscriberId).
				withStatus(SubscriberStatus.DELETED.name()).build());
				
		int actualPurgeCount = repository.purgeProfile(subscriberId, null);
		
		assertEquals(1, actualPurgeCount);


	}

	@Test
	public void test_purgeMultiple_should_purge_all_subscriber() throws Exception {
		
		SubscriberRepository repository = new SubscriberRepositoryImpl(null, null,transactionFactory, alertListener,  policyRepository, umOperation, abmFconfiguration, null,
				Collectionz.<String>newArrayList(), null, null, null, null, null, subscriberEDListener, null,"INR");
		
		List<String> subscriberIds = Arrays.asList("501","502","503","504","505");
		Map<String, Integer> expectedResult = new HashMap<String, Integer>(subscriberIds.size());

		for (String id : subscriberIds) {
			expectedResult.put(id, 1);
		}
		
		insertMarkDeletedRecords(subscriberIds);
		
		Map<String, Integer> actualResult = repository.purgeProfile(subscriberIds, null);
		assertReflectionEquals(expectedResult, actualResult, ReflectionComparatorMode.LENIENT_ORDER);
		
	}
	
	@Test
	public void test_purgeMultiple_should_not_throw_SQLException_in_case_of_timeout() throws Exception {

		SPInterface spInterface = spy(new NVDBSPInterface(alertListener, transactionFactory));
		SubscriberRepository repository = new SubscriberRepositoryImpl(null,
				null,
				transactionFactory,
				alertListener,
				spInterface,
				policyRepository,
				umOperation,
				abmFconfiguration, null,
				Collectionz.<String>newArrayList(), null, null, null, null, null, subscriberEDListener, null,"INR");
		
		List<String> subscriberIds = Arrays.asList("501","502","503","504","505");
		Map<String, Integer> expectedResult = new HashMap<String, Integer>(subscriberIds.size());
		
		for (String id : subscriberIds) {
			expectedResult.put(id, 1);
		}

		//when(spInterface.purgeProfile(anyString(), Mockito.any(Transaction.class))).thenReturn(1);
		//when(spInterface.purgeProfile(Mockito.matches("503"), Mockito.any(Transaction.class))).thenReturn(-1);
		doReturn(-1).when(spInterface).purgeProfile(Mockito.matches("503")
				, Mockito.any(Transaction.class));


		transactionFactory.createTransaction();

		expectedResult.put("503", -1);
		
		insertMarkDeletedRecords(subscriberIds);

		Map<String, Integer> actualResult = repository.purgeProfile(subscriberIds, null);
		
		assertReflectionEquals(expectedResult, actualResult, ReflectionComparatorMode.LENIENT_ORDER);

	}
	
	@Test
	public void test_purgeProfile_multiple_should_return_accumulated_result_even_if_DB_fail_in_middle() throws Exception {

		//SPInterface spInterface = mock(SPInterface.class);
		SPInterface spInterface = spy(new NVDBSPInterface(alertListener, transactionFactory));
		SubscriberRepository repository = new SubscriberRepositoryImpl(null,
				null,
				transactionFactory,
				alertListener,
				spInterface,
				policyRepository,
				umOperation,
				abmFconfiguration, null,
				Collectionz.<String>newArrayList(), null, null, null, null, null, subscriberEDListener, null,"INR");

		List<String> subscriberIds = Arrays.asList("501", "502", "503", "504", "505");
		Map<String, Integer> expectedResult = new HashMap<String, Integer>(subscriberIds.size());

		expectedResult.put(subscriberIds.get(0), 1);
		expectedResult.put(subscriberIds.get(1), 1);
		expectedResult.put(subscriberIds.get(2), -1);
		expectedResult.put(subscriberIds.get(3), 1);
		expectedResult.put(subscriberIds.get(4), 1);

//		when(spInterface.purgeProfile(anyString(), Mockito.any(Transaction.class))).thenReturn(1);
//		when(spInterface.purgeProfile(Mockito.matches("503"), Mockito.any(Transaction.class))).thenThrow(new OperationFailedException("From test"));
		doThrow(new OperationFailedException("From test")).when(spInterface).purgeProfile(Mockito.matches("503"), Mockito.any(Transaction.class));

		insertMarkDeletedRecords(subscriberIds);

		Map<String, Integer> actualResult = repository.purgeProfile(subscriberIds, null);

		assertReflectionEquals(expectedResult, actualResult, ReflectionComparatorMode.LENIENT_ORDER);
	}
	
	
	private void insertMarkDeletedRecords(List<String> subscriberIds) throws Exception {
		for (String id : subscriberIds) {
			helper.insertProfile(new SubscriberProfileData.SubscriberProfileDataBuilder().
					withSubscriberIdentity(id).
					withStatus(SubscriberStatus.DELETED.name()).build());
		}
	}

	@After
	public void afterDropTables() throws Exception {
		Connection connection = transactionFactory.getConnection();
		if(Objects.nonNull(connection)) {
			DBUtility.closeQuietly(connection);
		}
		hibernateSessionFactory.shutdown();
	}

	private ProductOffer getProductOffer(MockAddOnPackage addOn) {
		ProductOfferData productOfferData =  new ProductOfferDataFactory()
				.withId("1").withName("Test").withStatus(PkgStatus.ACTIVE.name())
				.withMode("LIVE").withType("ADDON")
				.withDataServicePkgId(addOn.getId()).build();
		return new ProductOffer(
				productOfferData.getId(), productOfferData.getName(), productOfferData.getDescription(),
				PkgType.ADDON, PkgMode.LIVE, 30,
				ValidityPeriodUnit.DAY, productOfferData.getSubscriptionPrice() != null ? productOfferData.getSubscriptionPrice() : 0d, productOfferData.getCreditBalance() != null ? productOfferData.getCreditBalance() : 0d,
				PkgStatus.ACTIVE, null, null,
				productOfferData.getDataServicePkgId(), productOfferData.getGroupList(), productOfferData.getAvailabilityStartDate(),
				productOfferData.getAvailabilityEndDate(), PolicyStatus.SUCCESS,  null, null, (Objects.nonNull(productOfferData.isFnFOffer()) && productOfferData.isFnFOffer().booleanValue()),
                productOfferData.getParam1(), productOfferData.getParam2(), policyRepository,null,null,new HashMap<>(),productOfferData.getCurrency()
		);
	}
}
