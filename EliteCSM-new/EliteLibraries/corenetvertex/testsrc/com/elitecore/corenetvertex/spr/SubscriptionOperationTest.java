package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.factory.ProductOfferDataFactory;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.SubscriptionOperationTestSuite.SubscriptionDBHelper;
import com.elitecore.corenetvertex.spr.data.SubscriptionData;
import com.elitecore.corenetvertex.util.DerbyUtil;
import junitparams.JUnitParamsRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class SubscriptionOperationTest { // NOSONAR

	private static final String PREPARESTATEMENT = "preparedStatement";
	private static final String BEGIN = "begin";
	private static final String EXECUTE = "execute";
	private static final String GET = "get";
	private String testDB = UUID.randomUUID().toString();

	@Mock
	private PolicyRepository policyRepository;
	@Mock
	ProductOfferStore productOfferStore;
	@Mock
	private AddOn addOn;
	private ProductOffer productOffer;

	private DummyTransactionFactory transactionFactory;
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private SubscriptionDBHelper helper;

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	}

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);
        String url = "jdbc:derby:memory:"+ testDB +";create=true";
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", SubscriptionOperationTestSuite.DS_NAME, url, "", "", 1, 5000, 3000);
		transactionFactory = (DummyTransactionFactory) new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1).build();

		helper = new SubscriptionDBHelper(transactionFactory);

		createTablesAndInsertSubscriptionRecords();
		productOffer = getProductOffer(addOn);
		when(policyRepository.getAddOnById(Mockito.anyString())).thenReturn(addOn);
		when(policyRepository.getProductOffer()).thenReturn(productOfferStore);
		when(productOfferStore.byId(productOffer.getId())).thenReturn(productOffer);

	}

	public void setupMockToGenerateException(String whenToThrow, Class<? extends Throwable> exceptionToBeThrown) throws Exception {
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		ResultSet resultSet = mock(ResultSet.class);

		transactionFactory = spy(transactionFactory);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
		doReturn(resultSet).when(preparedStatement).executeQuery();

		if (PREPARESTATEMENT.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(transaction).prepareStatement(Mockito.anyString());
		} else if (BEGIN.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(transaction).begin();
		} else if (EXECUTE.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(preparedStatement).executeQuery();
		} else if (GET.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(resultSet).next();
		}

	}

	@SuppressWarnings("unused")
	private Object[][] dataProviderFor_test_getAddonSubscriptions_should_throw_OperationFailedException_when_any_exception_thrown() {
		return new Object[][] {
				{
						PREPARESTATEMENT, TransactionException.class
		},
				{
						BEGIN, TransactionException.class
		},
				{
						EXECUTE, SQLException.class
		},
				{
						GET, SQLException.class
		}
		};
	}

	@SuppressWarnings("unused")
	private Object[][] dataProviderFor_test_getBodSubscription_should_throw_OperationFailedException_when_any_exception_thrown() {
		return new Object[][] {
				{
						PREPARESTATEMENT, TransactionException.class
		},
				{
						BEGIN, TransactionException.class
		},
				{
						EXECUTE, SQLException.class
		},
				{
						GET, SQLException.class
		}
		};
	}

	/**
	 * <PRE>
	 * 3 subscription created
	 * SubscriberIdentity > Addonsubscription
	 * 				 101  ---- > 1,3 
	 * 	 			 102  ---- > 2
	 * 	 			 103  ---- > no subscription
	 * </PRE>
	 */
	private void createTablesAndInsertSubscriptionRecords() throws Exception {
		LogManager.getLogger().debug(this.getClass().getSimpleName(), "creating DB");

		helper.createTables();

		helper.insertAddonRecords(getAddonSubscritionData());

		LogManager.getLogger().debug(this.getClass().getSimpleName(), "DB created");
	}

	/**
	 * Add Adddon record here.
	 * 
	 * Used for inserting table entry and generating expected entity
	 */
	private List<SubscriptionData> getAddonSubscritionData() {

		SubscriptionData record1 = new SubscriptionData("101", addOn.getId(), "2014-10-29 09:26:50.12", "2020-12-31 09:26:50.12",
				"2", "0", "1", "2020-12-31 09:26:50.12",productOffer.getId());

		SubscriptionData record2 = new SubscriptionData("102",  addOn.getId(), "2014-10-29 09:26:50.12", "2020-12-31 09:26:50.12",
				"2", "0", "2", "2020-12-31 09:26:50.12",productOffer.getId());

		SubscriptionData record3 = new SubscriptionData("101", addOn.getId(), "2014-10-29 09:26:50.12", "2020-12-31 09:26:50.12",
				"2", "0", "3", "2020-12-31 09:26:50.12",productOffer.getId());
		
		record1.setAddOn(addOn);
		record2.setAddOn(addOn);
		record3.setAddOn(addOn);
		
		return Arrays.asList(record1, record2, record3);
	}

	@After
	public void afterDropTables() throws Exception {
		helper.dropTables();
		DBUtility.closeQuietly(transactionFactory.getConnection());
        DerbyUtil.closeDerby(testDB);
	}

	private ProductOffer getProductOffer(AddOn addOn) {
		ProductOfferData productOfferData =  new ProductOfferDataFactory()
				.withId("1").withName("Test").withStatus(PkgStatus.ACTIVE.name())
				.withMode("LIVE").withType("ADDON")
				.withDataServicePkgId(addOn.getId()).build();
		return new ProductOffer(
				productOfferData.getId(), productOfferData.getName(), productOfferData.getDescription(),
				PkgType.ADDON, PkgMode.LIVE, 30,
				ValidityPeriodUnit.DAY, productOfferData.getSubscriptionPrice() != null ? productOfferData.getSubscriptionPrice() : 0d,
                productOfferData.getCreditBalance() != null ? productOfferData.getCreditBalance() : 0d,
				PkgStatus.ACTIVE, null,null,
				productOfferData.getDataServicePkgId(), productOfferData.getGroupList(), productOfferData.getAvailabilityStartDate(),
				productOfferData.getAvailabilityEndDate(), PolicyStatus.SUCCESS,  null, null,
				(Objects.nonNull(productOfferData.isFnFOffer()) && productOfferData.isFnFOffer().booleanValue()),
				productOfferData.getParam1(), productOfferData.getParam2(), policyRepository,
				null,null,new HashMap<>(),productOfferData.getCurrency()
		);
	}

}
