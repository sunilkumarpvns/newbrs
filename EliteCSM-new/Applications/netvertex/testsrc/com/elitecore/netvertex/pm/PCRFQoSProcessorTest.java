package com.elitecore.netvertex.pm;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pm.constants.SelectionResult;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.BillingCycleAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.DailyAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.WeeklyAllowedUsage;
import com.elitecore.corenetvertex.pm.util.MockBasePackage;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl.SPRInfoBuilder;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.util.SubscriptionUtil;
import com.elitecore.netvertex.core.util.PCRFRequestBuilder;
import com.elitecore.netvertex.core.util.PCRFResponseBuilder;
import com.elitecore.netvertex.pm.qos.rnc.RnCBaseQoSProfileDetail;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.elitecore.corenetvertex.constants.FieldValueConstants.NAME;
import static com.elitecore.corenetvertex.pm.constants.SelectionResult.FULLY_APPLIED;
import static com.elitecore.corenetvertex.pm.constants.SelectionResult.NOT_APPLIED;
import static com.elitecore.corenetvertex.pm.constants.SelectionResult.PARTIALLY_APPLIED;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(HierarchicalContextRunner.class)
public class PCRFQoSProcessorTest {

	public static final String QUOTA_PROFILE_NAME = "QUOTA_PROFILE_NAME";
	public static final String DETAIL_ID = "id";
	public static final String DETAIL_NAME = "name";
	PCRFQoSProcessor pcrfQoSProcessor;
	Method method;
	BasePolicyContext policyContext;
	QoSInformation qosInformation;
	private QoSProfile qoSProfile;
	private static final String PCC_RULE_ID = UUID.randomUUID().toString();
	private static final String PCC_RULE_NAME = UUID.randomUUID().toString();
	private static final String PACKAGE_ID = UUID.randomUUID().toString();
	private String dummyName = "TEST_QUOTA_PROFILE";
	private String quotaProfileId = "TEST_QUOTA_PROFILE";
	private final String name = "rateCardName";
	private final String id = "id";
	private static final String keyOne = "k1";
	private static final String keyTwo = "k2";
	@Before
	public void setUp() throws Exception {

		policyContext = mock(BasePolicyContext.class);

		qosInformation = spy(new QoSInformation());
		pcrfQoSProcessor = new PCRFQoSProcessor(qosInformation);

        method = PCRFQoSProcessor.class.getDeclaredMethod("isLevelApplicable", PolicyContext.class, com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail.class);
        method.setAccessible(true);

        qoSProfile = mock(QoSProfile.class);
	}

	@Test //addon package qos selection called
	public void subscriptionFoundAddOnQoSSelectionProcessed() {
		Subscription subscription = createSubscription();
		UserPackage userPackage = mock(AddOn.class);
		QoSProfileDetail qoSProfileDetail = spy(QoSProfileDetail.class);

		doReturn(PkgType.ADDON).when(userPackage).getPackageType();
		doReturn(qoSProfileDetail).when(qoSProfile).getHSQLevelQoSDetail();
		doReturn(FULLY_APPLIED).when(qoSProfileDetail).apply(policyContext, qosInformation, null);

		pcrfQoSProcessor.process(qoSProfile, policyContext, userPackage, subscription);

		verify(qosInformation, times(1)).startAddOnQoSSelection(userPackage, subscription.getId());
		verify(qosInformation, never()).startPackageQoSSelection(userPackage);
	}

	@Test //base package qos selection called
	public void subscriptionNotFoundBasePackageQoSSelectionCalled() {
		Subscription subscription = null;
		UserPackage userPackage = mock(BasePackage.class);
		QoSProfileDetail qoSProfileDetail = mock(QoSProfileDetail.class);

		doReturn(PkgType.BASE).when(userPackage).getPackageType();
		doReturn(qoSProfileDetail).when(qoSProfile).getHSQLevelQoSDetail();
		doReturn(FULLY_APPLIED).when(qoSProfileDetail).apply(policyContext, qosInformation, null);

		pcrfQoSProcessor.process(qoSProfile, policyContext, userPackage, subscription);

		verify(qosInformation, times(1)).startPackageQoSSelection(userPackage);
	}

	@Test //data rate card
	public void rateCardIsConfiguredAndUserInternationallyRoamingWithInternationDataRoamingFlagOffInSPRThenSelectionResultNotApplied() {
		Subscription subscription = createSubscription();

		PCRFResponse pcrfResponse = new PCRFResponseBuilder().
				addAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.val, "true").build();
		PCRFRequest pcrfRequest = new PCRFRequestBuilder(TimeSource.systemTimeSource()).
				withSubscriberProfile(new SPRInfoBuilder().withPaygInternationalDataRoaming(false).build()).build();

		MockBasePackage basePackage = MockBasePackage.create("Base-Package", NAME).quotaProfileTypeIsRnC();

		DataRateCard dataRateCard = new DataRateCard(id, name, keyOne, keyTwo, null, Uom.SECOND, Uom.SECOND);

		doReturn(dataRateCard).when(qoSProfile).getDataRateCard();

		doReturn(pcrfResponse).when(policyContext).getPCRFResponse();
		doReturn(pcrfRequest).when(policyContext).getPCRFRequest();

		assertFalse(pcrfQoSProcessor.process(qoSProfile, policyContext, basePackage, subscription));
	}

	@Test //quota profile
	public void quotaProfileConfiguredAndSubScriberInternationallyRoamingWithInternationalRoamingDisabledInSPR() {
		Subscription subscription = createSubscription();

		PCRFResponse pcrfResponse = new PCRFResponseBuilder().
				addAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.val, "true").build();
		PCRFRequest pcrfRequest = new PCRFRequestBuilder(TimeSource.systemTimeSource()).
				withSubscriberProfile(new SPRInfoBuilder().withPaygInternationalDataRoaming(false).build()).build();

		MockBasePackage basePackage = MockBasePackage.create("Base-Package", NAME).quotaProfileTypeIsRnC();

		qoSProfile = createQoSProfile();
		doReturn(pcrfResponse).when(policyContext).getPCRFResponse();
		doReturn(pcrfRequest).when(policyContext).getPCRFRequest();

		assertFalse(pcrfQoSProcessor.process(qoSProfile, policyContext, basePackage, subscription));
	}

	public class hsqLevelOtherThanFullyApplied {
		Subscription subscription;
		PCRFResponse pcrfResponse;
		PCRFRequest pcrfRequest;
		MockBasePackage basePackage;

		@Before
		public void setUp() {
			subscription = createSubscription();

			pcrfResponse = new PCRFResponseBuilder().
					addAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.val, "true").build();
			pcrfRequest = new PCRFRequestBuilder(TimeSource.systemTimeSource()).
					withSubscriberProfile(new SPRInfoBuilder().withPaygInternationalDataRoaming(true).build()).build();

			basePackage = MockBasePackage.create("Base-Package", NAME).quotaProfileTypeIsRnC();

			qoSProfile = spy(createQoSProfile());
			RnCBaseQoSProfileDetail rnCBaseQoSProfileDetail = spy(createAllServiceRnCQuotaProfileDetail());
			doReturn(rnCBaseQoSProfileDetail).when(qoSProfile).getHSQLevelQoSDetail();
			doReturn(PARTIALLY_APPLIED).when(rnCBaseQoSProfileDetail).apply(policyContext, qosInformation, SelectionResult.NOT_APPLIED);

			doReturn(pcrfResponse).when(policyContext).getPCRFResponse();
			doReturn(pcrfRequest).when(policyContext).getPCRFRequest();
		}

		@Test
		public void fupNotConfigured() {
			doReturn(null).when(qoSProfile).getFupLevelQoSDetails();
			Assert.assertTrue(pcrfQoSProcessor.process(qoSProfile, policyContext, basePackage, subscription));
		}

		@Test
		public void fupNotAppliedReturnTrueForFurtherProcessing() {
			SelectionResult fupSelectionResult = NOT_APPLIED;
			//set fup qos profile detail
			RnCBaseQoSProfileDetail fupQoSProfileDetail = spy(createAllServiceRnCQuotaProfileDetail());
			doReturn(fupSelectionResult).when(fupQoSProfileDetail).apply(policyContext, qosInformation, PARTIALLY_APPLIED);
			List<RnCBaseQoSProfileDetail> fupQoSProfileDetailList = new ArrayList<>();
			fupQoSProfileDetailList.add(fupQoSProfileDetail);
			doReturn(fupQoSProfileDetailList).when(qoSProfile).getFupLevelQoSDetails();

			assertTrue(pcrfQoSProcessor.process(qoSProfile, policyContext, basePackage, subscription));
		}

		@Test
		public void fupFullyAppliedOrPartiallyAppliedReturnTrueProcessing() {
			SelectionResult fupSelectionResult = FULLY_APPLIED;
			RnCBaseQoSProfileDetail fupQoSProfileDetail = spy(createAllServiceRnCQuotaProfileDetail());
			doReturn(fupSelectionResult).when(fupQoSProfileDetail).apply(policyContext, qosInformation, PARTIALLY_APPLIED);
			List<RnCBaseQoSProfileDetail> fupQoSProfileDetailList = new ArrayList<>();
			fupQoSProfileDetailList.add(fupQoSProfileDetail);
			doReturn(fupQoSProfileDetailList).when(qoSProfile).getFupLevelQoSDetails();

			doNothing().when(qosInformation).startProcessingQoSProfile(qoSProfile);

			assertTrue(pcrfQoSProcessor.process(qoSProfile, policyContext, basePackage, subscription));
		}
	}

	@Test
	public void hsqLevelFullyAppliedNoFupLevelQoSInvocations() {
		Subscription subscription = createSubscription();

		PCRFResponse pcrfResponse = new PCRFResponseBuilder().
				addAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.val, "true").build();
		PCRFRequest pcrfRequest = new PCRFRequestBuilder(TimeSource.systemTimeSource()).
				withSubscriberProfile(new SPRInfoBuilder().withPaygInternationalDataRoaming(true).build()).build();

		MockBasePackage basePackage = MockBasePackage.create("Base-Package", NAME).quotaProfileTypeIsRnC();

		qoSProfile = spy(createQoSProfile());
		RnCBaseQoSProfileDetail rnCBaseQoSProfileDetail = spy(createAllServiceRnCQuotaProfileDetail());
		doReturn(rnCBaseQoSProfileDetail).when(qoSProfile).getHSQLevelQoSDetail();
		doReturn(FULLY_APPLIED).when(rnCBaseQoSProfileDetail).apply(policyContext, qosInformation, SelectionResult.NOT_APPLIED);

		doReturn(pcrfResponse).when(policyContext).getPCRFResponse();
		doReturn(pcrfRequest).when(policyContext).getPCRFRequest();

		doNothing().when(qosInformation).startProcessingQoSProfile(qoSProfile);

		assertTrue(pcrfQoSProcessor.process(qoSProfile, policyContext, basePackage, subscription));
		verify(qoSProfile, never()).getFupLevelQoSDetails();
	}

	@Test
	public void hsqNotAppliedAndfupLevelNotConfiguredThenReturnFalseFlag() {
		Subscription subscription = createSubscription();

		PCRFResponse pcrfResponse = new PCRFResponseBuilder().
				addAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.val, "true").build();
		PCRFRequest pcrfRequest = new PCRFRequestBuilder(TimeSource.systemTimeSource()).
				withSubscriberProfile(new SPRInfoBuilder().withPaygInternationalDataRoaming(true).build()).build();

		MockBasePackage basePackage = MockBasePackage.create("Base-Package", NAME).quotaProfileTypeIsRnC();

		qoSProfile = spy(createQoSProfile());
		RnCBaseQoSProfileDetail rnCBaseQoSProfileDetail = spy(createAllServiceRnCQuotaProfileDetail());
		doReturn(rnCBaseQoSProfileDetail).when(qoSProfile).getHSQLevelQoSDetail();
		doReturn(NOT_APPLIED).when(rnCBaseQoSProfileDetail).apply(policyContext, qosInformation, SelectionResult.NOT_APPLIED);

		doReturn(null).when(qoSProfile).getFupLevelQoSDetails();

		doReturn(pcrfResponse).when(policyContext).getPCRFResponse();
		doReturn(pcrfRequest).when(policyContext).getPCRFRequest();

		Assert.assertFalse(pcrfQoSProcessor.process(qoSProfile, policyContext, basePackage, subscription));
	}

	private QoSProfile createQoSProfile() {
		return new QoSProfile(PCC_RULE_ID, PCC_RULE_NAME, PACKAGE_ID, PACKAGE_ID, getQuotaProfile(), null, null,
				0, createAllServiceRnCQuotaProfileDetail(), null, null, null, null);
	}

	private RnCBaseQoSProfileDetail createAllServiceRnCQuotaProfileDetail() {
		RnCQuotaProfileDetail rnCQuotaProfileDetail = new com.elitecore.netvertex.pm.RnCQuotaProfileFactory(UUID.randomUUID().toString(), UUID.randomUUID().toString())
				.dataServiceType(new DataServiceType(CommonConstants.ALL_SERVICE_ID, "test", 1, Collections.emptyList(), Collections.emptyList()))
				.randomBalanceWithRate().create();

		return RnCQoSProfileDetailFactory.createQoSProfile().hasRnCQuota().quotaProfileDetail(rnCQuotaProfileDetail).build();
	}

	private QuotaProfile getQuotaProfile() {
		List<Map<String, QuotaProfileDetail>> fupLevelserviceWiseQuotaProfileDetais= new ArrayList<>();

		RatingGroup rg = new RatingGroup("RATING_GROUP_1", "RATING_GROUP_1", "RATING_GROUP_1", 0);

		Map<String, QuotaProfileDetail > hsq = new HashMap<>();

		fupLevelserviceWiseQuotaProfileDetais.add(hsq);

		DataServiceType dataServiceType1 = new DataServiceType("DATA_SERVICE_TYPE_1", "Rg1", 1, Collections.emptyList(), Arrays.asList(rg));

		Map<AggregationKey, AllowedUsage> allowedUsageMap = getAllowedUsage();

		String pccProfileName = "pccProfileName";
		hsq.put("DATA_SERVICE_TYPE_1", new RncProfileDetail("hsq_Quota_"+dummyName, dataServiceType1, 0, rg, allowedUsageMap, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null,
				QuotaUsageType.VOLUME,null, quotaProfileId, true, pccProfileName, 0, 0, ""));


		return new QuotaProfile("Quota_"+dummyName, dummyName,
				quotaProfileId, BalanceLevel.HSQ,2, RenewalIntervalUnit.MONTH, QuotaProfileType.RnC_BASED,
				fupLevelserviceWiseQuotaProfileDetais, CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.ENABLE.isBooleanValue());
	}

	private Map<AggregationKey, AllowedUsage> getAllowedUsage(){
		Map<AggregationKey, AllowedUsage> allowedUsageMap = new HashMap<>();

		AllowedUsage daily = new DailyAllowedUsage(1024*30,512*30,512*30,7200*30, DataUnit.MB, DataUnit.MB, DataUnit.MB, TimeUnit.SECOND);
		AllowedUsage weekly = new WeeklyAllowedUsage(1024*7,512*7,512*7,7200*7, DataUnit.MB, DataUnit.MB, DataUnit.MB, TimeUnit.SECOND);
		AllowedUsage billingCycle = new BillingCycleAllowedUsage(1024,512,512,7200, DataUnit.MB, DataUnit.MB, DataUnit.MB, TimeUnit.SECOND);

		allowedUsageMap.put(AggregationKey.DAILY, daily);
		allowedUsageMap.put(AggregationKey.WEEKLY, weekly);
		allowedUsageMap.put(AggregationKey.BILLING_CYCLE, billingCycle);

		return  allowedUsageMap;
	}

	private Subscription createSubscription() {
		return new Subscription("abc1", "xyz", "foo", "productOfferId", new Timestamp(100), new Timestamp(1000000L),
				null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, SubscriptionUtil.createMetaData("{\"fnFGroup\":{\"name\":\"fnf\",\"members\":[]}}", "abc1", "test"), null, null);
	}

	@Test
	public void test_level_applicable_true_when_quotaprofiledetail_isnull() throws Exception {

		boolean isLevelApplicable = (boolean) method.invoke(pcrfQoSProcessor, policyContext, null);
        Assert.assertEquals(isLevelApplicable,true);
	}
	
	@Test
	public void test_level_applicable_false() throws Exception {

		PCRFRequest pcrfRequest = mock(PCRFRequest.class);
		PCRFResponse pcrfResponse = mock(PCRFResponse.class);
		SPRInfo sprInfo = mock(SPRInfo.class);
		RnCQuotaProfileDetail quotaProfileDetail = mock(RnCQuotaProfileDetail.class);

		pcrfResponse.setAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.val, PCRFKeyValueConstants.SUBSCRIBER_INTERNATIONAL_ROAMING_TRUE.val);

		doReturn(pcrfRequest).when(policyContext).getPCRFRequest();
		doReturn(pcrfResponse).when(policyContext).getPCRFResponse();
		doReturn(sprInfo).when(pcrfRequest).getSPRInfo();
		doReturn(true).when(quotaProfileDetail).isRateConfigured();
		doReturn(false).when(sprInfo).getPaygInternationalDataRoaming();
		doReturn("true").when(pcrfResponse).getAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.val);

		boolean isLevelApplicable = (boolean) method.invoke(pcrfQoSProcessor, policyContext, quotaProfileDetail);
        Assert.assertEquals(isLevelApplicable, false);
	}	
	
	
	@Test
	public void test_level_applicable_true() throws Exception {

		PCRFRequest pcrfRequest = mock(PCRFRequest.class);
		PCRFResponse pcrfResponse = mock(PCRFResponse.class);
		SPRInfo sprInfo = mock(SPRInfo.class);
		RnCQuotaProfileDetail quotaProfileDetail = mock(RnCQuotaProfileDetail.class);

		pcrfResponse.setAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.val, PCRFKeyValueConstants.SUBSCRIBER_INTERNATIONAL_ROAMING_TRUE.val);
		
		doReturn(pcrfRequest).when(policyContext).getPCRFRequest();
		doReturn(pcrfResponse).when(policyContext).getPCRFResponse();
		doReturn(sprInfo).when(pcrfRequest).getSPRInfo();
		doReturn(true).when(quotaProfileDetail).isRateConfigured();
		doReturn(true).when(sprInfo).getPaygInternationalDataRoaming();
		doReturn("true").when(pcrfResponse).getAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.val);

		boolean isLevelApplicable = (boolean) method.invoke(pcrfQoSProcessor, policyContext, quotaProfileDetail);
        Assert.assertEquals(isLevelApplicable, true);
	}
	
}
