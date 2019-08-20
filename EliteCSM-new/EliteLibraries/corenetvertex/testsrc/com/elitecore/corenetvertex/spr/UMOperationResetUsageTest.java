package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.factory.BasePackageFactory;
import com.elitecore.corenetvertex.pm.factory.ProductOfferDataFactory;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.util.ResetTimeUtility;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import junitparams.JUnitParamsRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class UMOperationResetUsageTest {

    // Fail cases are covered in UMOperationThrowsOperationFailedExceptionWithServiceUnavailableResultCodeTest.java

	private static final String DS_NAME = "test-DB";
    public static final String PRODUCT_OFFER_ID = "1";
    private BasePackage basepackage;

	@Mock
	private AlertListener alertListener;
	@Mock private PolicyRepository policyRepository;
	private DummyTransactionFactory transactionFactory;
	private UMOperation umOperation;
	@Mock
	ProductOfferStore productOfferStore;
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private HibernateSessionFactory hibernateSessionFactory;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		String sid = UUID.randomUUID().toString();
		String connectionURL = "jdbc:h2:mem:"+ sid;
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, connectionURL, "", "", 1, 5000, 3000);
		transactionFactory = new DummyTransactionFactory(dbDataSource);
		transactionFactory.createTransaction();
		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.connection.url", connectionURL);
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		hibernateSessionFactory = HibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml", hibernateProperties);

		setupPackage();

		umOperation = new UMOperation(alertListener, policyRepository);
	}

	@After
	public void tearDownConnection() throws Exception {
		Connection connection = transactionFactory.getConnection();
		if(Objects.nonNull(connection)) {
			DBUtility.closeQuietly(connection);
		}
		hibernateSessionFactory.shutdown();

	}

	public void setupPackage(){

		BasePackageFactory.BasePackageBuilder basePackageBuilder = new BasePackageFactory.BasePackageBuilder(null, "base_id", "base_package");
		basePackageBuilder.withQuotaProfileType(QuotaProfileType.USAGE_METERING_BASED).withQoSProfiles(new ArrayList<>());
		basepackage = basePackageBuilder.build();

		basepackage = Mockito.spy(basepackage);
		ProductOffer productOffer = getProductOffer(basepackage);

		when(policyRepository.getProductOffer()).thenReturn(productOfferStore);
		when(policyRepository.getPkgDataById("base_id")).thenReturn(basepackage);
		when(policyRepository.getProductOffer().byId(productOffer.getId())).thenReturn(productOffer);
	}

	public void spyQuotaProfileForPackage(Integer renewalInterval, RenewalIntervalUnit renewalIntervalUnit){
		List<QuotaProfile> quotaProfileList =  new ArrayList<>();

		List<Map<String, QuotaProfileDetail>> fupLevelserviceWiseQuotaProfileDetais = new ArrayList<>();
		fupLevelserviceWiseQuotaProfileDetais.add(new HashMap<>());
		fupLevelserviceWiseQuotaProfileDetais.add(new HashMap<>());
		fupLevelserviceWiseQuotaProfileDetais.add(new HashMap<>());

		QuotaProfile quotaProfile1 = new QuotaProfile("profile1",
				"pkg1",
				"id1",
				BalanceLevel.HSQ,
				renewalInterval,
				renewalIntervalUnit,
				QuotaProfileType.USAGE_METERING_BASED,
				fupLevelserviceWiseQuotaProfileDetais, CommonStatusValues.DISABLE.isBooleanValue(), CommonStatusValues.DISABLE.isBooleanValue());

		quotaProfileList.add(quotaProfile1);
		Mockito.doReturn(quotaProfileList).when(basepackage).getQuotaProfiles();
	}

	public void createUsageAndSaveInDatabase(List<QuotaProfile> quotaProfileList){
		for(QuotaProfile quotaProfile : quotaProfileList){
			SubscriberUsageData subscriberUsageData = new SubscriberUsageData.SubscriberUsageDataBuilder()
					.withId("balance1")
					.withSubscriberIdentity("sub1").withPackageId("base_id")
					.withQuotaProfileId(quotaProfile.getId())
					.withServiceId("service1")
					.withDefaultUsage((long)Math.random()).withProductOfferId("1").build();
			hibernateSessionFactory.save(subscriberUsageData);
		}
	}

	public List<SubscriberUsageData> createExpectedUsage(List<QuotaProfile> quotaProfileList){
		List<SubscriberUsageData> list = new ArrayList<>();
		for(QuotaProfile quotaProfile : quotaProfileList){
			SubscriberUsageData subscriberUsageData = new SubscriberUsageData.SubscriberUsageDataBuilder()
					.withSubscriberIdentity("sub1").withPackageId("base_id")
					.withId("balance1")
					.withServiceId("service1")
					.withQuotaProfileId(quotaProfile.getId())
					.withDefaultUsage(0).withProductOfferId("1").build();

			if(quotaProfile.getRenewalInterval()!=0){
				subscriberUsageData.setBillingCycleResetTime(quotaProfile.getRenewalIntervalUnit().addTime(System.currentTimeMillis(),quotaProfile.getRenewalInterval()));
			} else {
				subscriberUsageData.setBillingCycleResetTime(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(19*365l));
			}
			subscriberUsageData.setDailyResetTime(ResetTimeUtility.calculateDailyResetTime(System.currentTimeMillis()));
			subscriberUsageData.setWeeklyResetTime(ResetTimeUtility.calculateWeeklyResetTime(System.currentTimeMillis()));
			list.add(subscriberUsageData);
		}
		return list;
	}

	@Test
	public void testsItResetBalanceAndUpdateBillingCycleResetTimeForQuotaProfileForWhichRenewalIntervalIsSet() throws OperationFailedException{
		spyQuotaProfileForPackage(2, RenewalIntervalUnit.MONTH_END);
		createUsageAndSaveInDatabase(basepackage.getQuotaProfiles());

		umOperation.resetUsage("sub1", PRODUCT_OFFER_ID, transactionFactory);

		assertValues(createExpectedUsage(basepackage.getQuotaProfiles()),hibernateSessionFactory.get(SubscriberUsageData.class));
	}

	@Test
	public void testsItResetBalanceAndUpdateBillingCycleResetTimeForQuotaProfileForWhichRenewalIntervalNotSet() throws Exception{
		spyQuotaProfileForPackage(0, RenewalIntervalUnit.MONTH_END);
		createUsageAndSaveInDatabase(basepackage.getQuotaProfiles());
		umOperation.resetUsage("sub1", PRODUCT_OFFER_ID, transactionFactory);

		assertValuesForNoRenewalInterval(createExpectedUsage(basepackage.getQuotaProfiles()),hibernateSessionFactory.get(SubscriberUsageData.class));
	}

	@Test
	public void resetUsageForBaseProductOffer() throws OperationFailedException {
		spyQuotaProfileForPackage(2, RenewalIntervalUnit.MONTH_END);
		createUsageAndSaveInDatabase(basepackage.getQuotaProfiles());

		umOperation.resetUsage("sub1", PRODUCT_OFFER_ID, transactionFactory);

		Assert.assertFalse(umOperation.getUsage("sub1", Collections.emptyMap(), transactionFactory).isEmpty());
	}

	@Test
	public void skipResetUsageIfPackageIsNotUMBased() throws OperationFailedException {
		spyQuotaProfileForPackage(2, RenewalIntervalUnit.MONTH_END);
		when(basepackage.getQuotaProfileType()).thenReturn(QuotaProfileType.RnC_BASED);

		umOperation.resetUsage("sub2", PRODUCT_OFFER_ID, transactionFactory);

		Assert.assertTrue(umOperation.getUsage("sub2", Collections.emptyMap(), transactionFactory).isEmpty());
	}

	public void assertValues(List<SubscriberUsageData> expected, List<SubscriberUsageData> actual){
		expected.stream().forEach(usage -> usage.setLastUpdateTime(0));
		actual.stream().forEach(usage -> usage.setLastUpdateTime(0));
		ReflectionAssert.assertLenientEquals(expected,actual);
	}

	public void assertValuesForNoRenewalInterval(List<SubscriberUsageData> expected, List<SubscriberUsageData> actual) throws Exception{

		for(int i =0 ; i<expected.size(); i++){
			expected.get(i).setLastUpdateTime(0);
			actual.get(i).setLastUpdateTime(0);

			if(actual.get(i).getBillingCycleResetTime()<expected.get(i).getBillingCycleResetTime()){
				throw new Exception("Billing Cycle not set properly when there is no renewal interval set in base package");
			}


			expected.get(i).setBillingCycleResetTime(0);
			actual.get(i).setBillingCycleResetTime(0);
		}

		ReflectionAssert.assertLenientEquals(expected,actual);
	}
	private ProductOffer getProductOffer(BasePackage base) {
		ProductOfferData productOfferData =  new ProductOfferDataFactory()
				.withId(PRODUCT_OFFER_ID).withName("name").withStatus("Random")
				.withMode("LIVE").withType("BASE")
				.withDataServicePkgId(base.getId()).build();
		return new ProductOffer(
				productOfferData.getId(), productOfferData.getName(), productOfferData.getDescription(),
				PkgType.BASE, PkgMode.LIVE, productOfferData.getValidityPeriod(),
				null, productOfferData.getSubscriptionPrice() != null ? productOfferData.getSubscriptionPrice() : 0d,
                productOfferData.getCreditBalance() != null ? productOfferData.getCreditBalance() : 0d,
				null, null,null,
				productOfferData.getDataServicePkgId(), productOfferData.getGroupList(), productOfferData.getAvailabilityStartDate(),
				productOfferData.getAvailabilityEndDate(), PolicyStatus.SUCCESS,  null, null,
                (Objects.nonNull(productOfferData.isFnFOffer()) && productOfferData.isFnFOffer().booleanValue()),
                productOfferData.getParam1(), productOfferData.getParam2(), policyRepository,null,null,new HashMap<>(),productOfferData.getCurrency()
		);
	}
}
