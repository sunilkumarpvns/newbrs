package com.elitecore.netvertex.usagemetering;


import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.SubscriberUsage.SubscriberUsageBuilder;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.pm.AddOn;
import com.elitecore.netvertex.pm.BasePackage;
import com.elitecore.netvertex.service.pcrf.DummyPCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import com.elitecore.netvertex.usagemetering.ServiceUnit.ServiceUnitBuilder;
import com.elitecore.netvertex.usagemetering.factory.UMInfoFactory;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.elitecore.corenetvertex.util.Maps.Entry.newEntry;
import static com.elitecore.netvertex.core.util.Maps.newLinkedHashMap;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UMHandlerDataProviderForReplaceList {

	private static final String DEFAULT_SERVICE_ID = "1";
	private static final String SUBSCRIBER_IDENTITY = "40540";
	private static final String PCC_RULE_SUFFICS = "pccrule";
	private static final String ADD_ON_SUBSCRIPTION_SUFFICS = "AddOnSubscription";
	private static final String ADD_ON_SUFFICS = "AddOn";
	private static final String BASE_PACKAGE_SUFFICS = "basePackage";
	private static final String QUOTA_PROFILE_SUFFICS = "quotaProfileDetail";
	private static final String SERVICE_ID_SUFFICS = "service";
	private static final String BASE_PACKAGE_ID_SUFFICS = "basePackageId";
	private static final String ADD_ON_ID_SUFFICS = "AddOnId";

	private static PolicyRepository policyRepository;
	private static DummyNetvertexServerContextImpl serverContext;

	private static PCRFRequest pcrfReq = null;
	private static PCRFResponse pcrfRes = null;
	private static ExecutionContext executionContext;
	private static DummyPCRFServiceContext serviceContext;
	private static List<Subscription> addOnSubscriptions;

	public static void setUp() {


		// Base Package
		BasePackage basePackage = mock(BasePackage.class);
		when(basePackage.getName()).thenReturn(BASE_PACKAGE_SUFFICS + 1);
		when(basePackage.getId()).thenReturn(BASE_PACKAGE_ID_SUFFICS +1,"productOfferId");
        when(basePackage.getQuotaProfileType()).thenReturn(QuotaProfileType.USAGE_METERING_BASED);

        QuotaProfile quotaProfile = mock(QuotaProfile.class);
        when(quotaProfile.getId()).thenReturn(QUOTA_PROFILE_SUFFICS + 1);
        when(basePackage.getQuotaProfileByMonitoringKey(PCC_RULE_SUFFICS + 1)).thenReturn(quotaProfile);
		when(basePackage.getServiceId(PCC_RULE_SUFFICS + 1)).thenReturn(SERVICE_ID_SUFFICS + 1);
        when(basePackage.getQuotaProfileByMonitoringKey(PCC_RULE_SUFFICS + 2)).thenReturn(quotaProfile);
		when(basePackage.getServiceId(PCC_RULE_SUFFICS + 2)).thenReturn(SERVICE_ID_SUFFICS + 1);
        when(basePackage.getQuotaProfileByMonitoringKey(PCC_RULE_SUFFICS + 3)).thenReturn(quotaProfile);
		when(basePackage.getServiceId(PCC_RULE_SUFFICS + 3)).thenReturn(SERVICE_ID_SUFFICS + 1);

		QuotaProfile quotaProfile2 = mock(QuotaProfile.class);
		when(quotaProfile2.getId()).thenReturn(QUOTA_PROFILE_SUFFICS + 2);
		when(basePackage.getServiceId(PCC_RULE_SUFFICS + 4)).thenReturn(SERVICE_ID_SUFFICS + 2);
		when(basePackage.getQuotaProfileByMonitoringKey(PCC_RULE_SUFFICS + 4)).thenReturn(quotaProfile2);
        when(basePackage.getQuotaProfiles()).thenReturn(Arrays.asList(quotaProfile, quotaProfile2));

		// AddOn1
		AddOn addOn1 = mock(AddOn.class);
		when(addOn1.getId()).thenReturn(ADD_ON_ID_SUFFICS + 1,"productOfferId");
		when(addOn1.getValidityPeriodUnit()).thenReturn(ValidityPeriodUnit.DAY);
        when(addOn1.getQuotaProfileType()).thenReturn(QuotaProfileType.USAGE_METERING_BASED);

		QuotaProfile addOnQuotaProfile = mock(QuotaProfile.class);
		when(addOnQuotaProfile.getId()).thenReturn(QUOTA_PROFILE_SUFFICS + 3);
		when(addOn1.getQuotaProfileByMonitoringKey(PCC_RULE_SUFFICS + 5)).thenReturn(addOnQuotaProfile);
		when(addOn1.getServiceId(PCC_RULE_SUFFICS + 5)).thenReturn(SERVICE_ID_SUFFICS + 3);
		when(addOn1.getQuotaProfileByMonitoringKey(PCC_RULE_SUFFICS + 6)).thenReturn(addOnQuotaProfile);
		when(addOn1.getServiceId(PCC_RULE_SUFFICS + 6)).thenReturn(SERVICE_ID_SUFFICS + 3);
		when(addOn1.getQuotaProfileByMonitoringKey(PCC_RULE_SUFFICS + 7)).thenReturn(addOnQuotaProfile);
		when(addOn1.getServiceId(PCC_RULE_SUFFICS + 7)).thenReturn(SERVICE_ID_SUFFICS + 3);

		QuotaProfile addOnQuotaProfile2 = mock(QuotaProfile.class);
		when(addOnQuotaProfile2.getId()).thenReturn(QUOTA_PROFILE_SUFFICS + 4);
		when(addOn1.getServiceId(PCC_RULE_SUFFICS + 8)).thenReturn(SERVICE_ID_SUFFICS + 4);
		when(addOn1.getQuotaProfileByMonitoringKey(PCC_RULE_SUFFICS + 8)).thenReturn(addOnQuotaProfile2);

        when(addOn1.getQuotaProfiles()).thenReturn(Arrays.asList(addOnQuotaProfile, addOnQuotaProfile2));

		// AddOn2
		AddOn addOn2 = mock(AddOn.class);
		when(addOn2.getId()).thenReturn(ADD_ON_ID_SUFFICS + 2,"productOfferId");
		when(addOn2.getValidityPeriodUnit()).thenReturn(ValidityPeriodUnit.DAY);
        when(addOn2.getQuotaProfileType()).thenReturn(QuotaProfileType.USAGE_METERING_BASED);

		QuotaProfile addOn2quotaProfile = mock(QuotaProfile.class);
		when(addOn2quotaProfile.getId()).thenReturn(QUOTA_PROFILE_SUFFICS + 5);
		when(addOn2.getQuotaProfileByMonitoringKey(PCC_RULE_SUFFICS + 9)).thenReturn(addOn2quotaProfile);
		when(addOn2.getServiceId(PCC_RULE_SUFFICS + 9)).thenReturn(SERVICE_ID_SUFFICS + 5);
		when(addOn2.getQuotaProfileByMonitoringKey(PCC_RULE_SUFFICS + 10)).thenReturn(addOn2quotaProfile);
		when(addOn2.getServiceId(PCC_RULE_SUFFICS + 10)).thenReturn(SERVICE_ID_SUFFICS + 5);
		when(addOn2.getQuotaProfileByMonitoringKey(PCC_RULE_SUFFICS + 11)).thenReturn(addOn2quotaProfile);
		when(addOn2.getServiceId(PCC_RULE_SUFFICS + 11)).thenReturn(SERVICE_ID_SUFFICS + 5);

		QuotaProfile addOn2quotaProfile2 = mock(QuotaProfile.class);
		when(addOn2quotaProfile2.getId()).thenReturn(QUOTA_PROFILE_SUFFICS + 6);
		when(addOn2.getServiceId(PCC_RULE_SUFFICS + 12)).thenReturn(SERVICE_ID_SUFFICS + 6);
		when(addOn2.getQuotaProfileByMonitoringKey(PCC_RULE_SUFFICS + 12)).thenReturn(addOn2quotaProfile2);

        when(addOn1.getQuotaProfiles()).thenReturn(Arrays.asList(addOn2quotaProfile, addOn2quotaProfile2));

		policyRepository = mock(PolicyRepository.class);
		when(policyRepository.getBasePackageDataById(BASE_PACKAGE_ID_SUFFICS +1)).thenReturn(basePackage);
		when(policyRepository.getAddOnById(ADD_ON_ID_SUFFICS + 1)).thenReturn(addOn1);
		when(policyRepository.getAddOnById(ADD_ON_ID_SUFFICS + 2)).thenReturn(addOn2);

		serverContext = new DummyNetvertexServerContextImpl();
		serverContext.setPolicyRepository(policyRepository);

		serviceContext = new DummyPCRFServiceContext();
		serviceContext.setServerContext(serverContext);

		pcrfReq = new PCRFRequestImpl();
		pcrfReq.setAttribute(PCRFKeyConstants.SUB_DATA_PACKAGE.val, BASE_PACKAGE_SUFFICS + 1);
		pcrfReq.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, SUBSCRIBER_IDENTITY);
		pcrfReq.setAttribute(PCRFKeyConstants.SUB_CUI.val, SUBSCRIBER_IDENTITY);


		pcrfRes = new PCRFResponseImpl();
		pcrfRes.setAttribute(PCRFKeyConstants.SUB_DATA_PACKAGE.val, BASE_PACKAGE_SUFFICS + 1);
		pcrfRes.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, SUBSCRIBER_IDENTITY);
		pcrfRes.setAttribute(PCRFKeyConstants.SUB_CUI.val, SUBSCRIBER_IDENTITY);


		executionContext = new ExecutionContext(pcrfReq, pcrfRes, null, "INR");


        Date sessionStartTime = new Date(executionContext.getCurrentTime().getTimeInMillis() - TimeUnit.DAYS.toMillis(50));
        pcrfReq.setSessionStartTime(sessionStartTime);
        pcrfRes.setSessionStartTime(sessionStartTime);

		Subscription addOnSubscription1 = new Subscription.SubscriptionBuilder().
				withId(ADD_ON_SUBSCRIPTION_SUFFICS + 1).
				withPackageId(addOn1.getId()).
				withSubscriberIdentity(SUBSCRIBER_IDENTITY).
				withEndTime(new Timestamp(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1))).
				build();

		Subscription addOnSubscription2 = new Subscription.SubscriptionBuilder().
				withId(ADD_ON_SUBSCRIPTION_SUFFICS + 2).
				withPackageId(addOn1.getId()).
				withSubscriberIdentity(SUBSCRIBER_IDENTITY).
				withEndTime(new Timestamp(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1))).
				build();

		Subscription addOnSubscription3 = new Subscription.SubscriptionBuilder().
				withId(ADD_ON_SUBSCRIPTION_SUFFICS + 3).
				withPackageId(addOn2.getId()).
				withSubscriberIdentity(SUBSCRIBER_IDENTITY).
				withEndTime(new Timestamp(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(365))).
				build();

		Subscription addOnSubscription4 = new Subscription.SubscriptionBuilder().
				withId(ADD_ON_SUBSCRIPTION_SUFFICS + 4).
				withPackageId(addOn2.getId()).
				withSubscriberIdentity(SUBSCRIBER_IDENTITY).
				withEndTime(new Timestamp(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(365))).
				build();

		addOnSubscriptions = asList(addOnSubscription1, addOnSubscription2, addOnSubscription3, addOnSubscription4);



	}

	public static Object[][] provider_for_UM_for_new_current_usage_list_should_be_created_with_reported_usage_and_existing_usage() throws Exception {

		setUp();

		MeteringLevelTestConfig meteringLevelTestConfig = new MeteringLevelTestConfig.MeteringLevelTestBuilder().withNetVertexServerContext(serverContext)
				.withServiceContext(serviceContext)
				.withPcrfReq(pcrfReq)
				.withPcrfRes(pcrfRes)
				.withExecutionContext(executionContext)
				.withPolicyRepository(policyRepository)
				.withAddOnSubscription(addOnSubscriptions)
				.build();

		return new Object[][] {

				{
						"Case : 1 usage reported for all the services only for base package",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1),
												newEntry(PCC_RULE_SUFFICS + 2, BASE_PACKAGE_ID_SUFFICS +1),
												newEntry(PCC_RULE_SUFFICS + 3, BASE_PACKAGE_ID_SUFFICS +1))),
						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 1, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 2, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 3, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.build()
						)
						,

						null,

						meteringLevelTestConfig

				},

				{
						"Case : 1 usage reported for all the services only for AddOn",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 5, ADD_ON_SUBSCRIPTION_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 6, ADD_ON_SUBSCRIPTION_SUFFICS + 2),
								newEntry(PCC_RULE_SUFFICS + 9, ADD_ON_SUBSCRIPTION_SUFFICS + 3),
								newEntry(PCC_RULE_SUFFICS + 12, ADD_ON_SUBSCRIPTION_SUFFICS + 4))),

								// reported usage
								asList(
										UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 5, new ServiceUnit.ServiceUnitBuilder()
												.withAllType(100).build()),
										UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 6, new ServiceUnit.ServiceUnitBuilder()
												.withAllType(100).build()),
										UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 9, new ServiceUnit.ServiceUnitBuilder()
												.withAllType(100).build()),
										UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 12, new ServiceUnitBuilder()
												.withAllType(100).build())
								),

								// previous usage
								asList(
										// addOnSubscription1
										new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
												.withAllTypeUsage(100, 100, 100, 100)
												.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
												.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

										new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
												.withAllTypeUsage(100, 100, 100, 100)
												.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
												.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

										new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
												.withAllTypeUsage(100, 100, 100, 100)
												.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
												.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

										new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
												.withAllTypeUsage(100, 100, 100, 100)
												.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
												.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

										// addOnSubscription2

										new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
												.withAllTypeUsage(100, 100, 100, 100)
												.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
												.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

										new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
												.withAllTypeUsage(100, 100, 100, 100)
												.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
												.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

										new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
												.withAllTypeUsage(100, 100, 100, 100)
												.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
												.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

										new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
												.withAllTypeUsage(100, 100, 100, 100)
												.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
												.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

										// addOnSubscription3
										new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
												.withAllTypeUsage(100, 100, 100, 100)
												.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
												.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

										new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
												.withAllTypeUsage(100, 100, 100, 100)
												.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
												.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

										new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
												.withAllTypeUsage(100, 100, 100, 100)
												.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
												.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

										new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
												.withAllTypeUsage(100, 100, 100, 100)
												.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
												.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

										// addOnSubscription4

										new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
												.withAllTypeUsage(100, 100, 100, 100)
												.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
												.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

										new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
												.withAllTypeUsage(100, 100, 100, 100)
												.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
												.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

										new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
												.withAllTypeUsage(100, 100, 100, 100)
												.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
												.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

										new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
												.withAllTypeUsage(100, 100, 100, 100)
												.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
												.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
												.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build()
								)
								,

								null,

								meteringLevelTestConfig

				},

				{
						"Case : 2 usage reported for some services only for base package",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1),
										newEntry(PCC_RULE_SUFFICS + 2, BASE_PACKAGE_ID_SUFFICS +1),
										newEntry(PCC_RULE_SUFFICS + 4, BASE_PACKAGE_ID_SUFFICS +1))),
						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 1, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 2, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 4, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.build()
						)
						,

						null,

						meteringLevelTestConfig

				},

				{

						"Case : 2 usage reported for some services only for AddOn",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 5, ADD_ON_SUBSCRIPTION_SUFFICS + 1),
												newEntry(PCC_RULE_SUFFICS + 6, ADD_ON_SUBSCRIPTION_SUFFICS + 2),
												newEntry(PCC_RULE_SUFFICS + 9, ADD_ON_SUBSCRIPTION_SUFFICS + 3),
												newEntry(PCC_RULE_SUFFICS + 12, ADD_ON_SUBSCRIPTION_SUFFICS + 4))),

						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 5, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 6, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 9, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 12, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build()
						)
						,

						null,

						meteringLevelTestConfig


				},

				{
						"Case : 3 usage reported for new services  only for base package",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 4, BASE_PACKAGE_ID_SUFFICS +1))),
						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 4, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.build()
						)
						,

						null,

						meteringLevelTestConfig

				},



				{
						"Case : 3 usage reported for new services and no usage for base package only for base package",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 4, BASE_PACKAGE_ID_SUFFICS +1))),
						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 4, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						null
						,

						null,

						meteringLevelTestConfig

				},

				{

						"Case : 3 all usage reported for new services for addOn only",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 5, ADD_ON_SUBSCRIPTION_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 6, ADD_ON_SUBSCRIPTION_SUFFICS + 2),
								newEntry(PCC_RULE_SUFFICS + 9, ADD_ON_SUBSCRIPTION_SUFFICS + 3),
								newEntry(PCC_RULE_SUFFICS + 12, ADD_ON_SUBSCRIPTION_SUFFICS + 4))),

						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 5, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 6, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 9, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 12, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						asList(
								// addOnSubscription1

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription3

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build()
						)
						,

						null,

						meteringLevelTestConfig


				}
		};
	}


	public static Object[][] provider_for_UM_for_new_current_usage_list_should_be_created_with_reported_usage_and_existing_usage_have_reseted_weekly_usage()
			throws Exception {

		setUp();

		MeteringLevelTestConfig meteringLevelTestConfig = new MeteringLevelTestConfig.MeteringLevelTestBuilder().withNetVertexServerContext(serverContext)
				.withServiceContext(serviceContext)
				.withPcrfReq(pcrfReq)
				.withPcrfRes(pcrfRes)
				.withExecutionContext(executionContext)
				.withPolicyRepository(policyRepository)
				.withAddOnSubscription(addOnSubscriptions)
				.build();

		return new Object[][] {

				{
						"Case : 1 usage reported for all the services",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1),
								newEntry(PCC_RULE_SUFFICS + 2, BASE_PACKAGE_ID_SUFFICS +1),
								newEntry(PCC_RULE_SUFFICS + 3, BASE_PACKAGE_ID_SUFFICS +1))),
						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 1, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 2, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 3, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
                                        .withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() - 1).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
                                        .withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() - 1)
										.build()
						)
						,
						asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(400, 400, 400, 400)
										.withWeeklyUsage(0, 0, 0, 0)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(400, 400, 400, 400)
										.withWeeklyUsage(0, 0, 0, 0)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0)
										.build()
						)
						,

						meteringLevelTestConfig

				},

				{

						"Case : 1 usage reported for all the services only for AddOn",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 5, ADD_ON_SUBSCRIPTION_SUFFICS + 1),
												newEntry(PCC_RULE_SUFFICS + 6, ADD_ON_SUBSCRIPTION_SUFFICS + 2),
												newEntry(PCC_RULE_SUFFICS + 9, ADD_ON_SUBSCRIPTION_SUFFICS + 3),
												newEntry(PCC_RULE_SUFFICS + 12, ADD_ON_SUBSCRIPTION_SUFFICS + 4))),

						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 5, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 6, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 9, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 12, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build()
						)
						,

						asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build()
						)
						,

						meteringLevelTestConfig


				},

				{
						"Case : 2 some usage reported for new services",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1),
								newEntry(PCC_RULE_SUFFICS + 2, BASE_PACKAGE_ID_SUFFICS +1),
								newEntry(PCC_RULE_SUFFICS + 4, BASE_PACKAGE_ID_SUFFICS +1))),
						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 1, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 2, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 4, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.build()
						)
						,
						asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(300, 300, 300, 300)
										.withWeeklyUsage(0, 0, 0, 0)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(300, 300, 300, 300)
										.withWeeklyUsage(0, 0, 0, 0)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0)
										.build()
						)
						,

						meteringLevelTestConfig

				},

				{
						"Case : 2 some usage reported for AddOn",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 5, ADD_ON_SUBSCRIPTION_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 6, ADD_ON_SUBSCRIPTION_SUFFICS + 2),
								newEntry(PCC_RULE_SUFFICS + 9, ADD_ON_SUBSCRIPTION_SUFFICS + 3),
								newEntry(PCC_RULE_SUFFICS + 12, ADD_ON_SUBSCRIPTION_SUFFICS + 4))),

						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 5, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 6, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 9, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 12, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.build(),

								// addOnSubscription3
								new SubscriberUsage.SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.build(),

								new SubscriberUsage.SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.build(),

								// addOnSubscription4

								new SubscriberUsage.SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.build()
						)
						,

						asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(0).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(0).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(0).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(0).build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(0).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(0).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(0).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(0).build()
						)
						,

						meteringLevelTestConfig

				},

				{
						"Case : 3 all usage reported for new services only base package",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 4, BASE_PACKAGE_ID_SUFFICS +1))),
						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 4, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis()).build()
						)
						,

						asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withWeeklyUsage(0, 0, 0, 0)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withWeeklyUsage(0, 0, 0, 0)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(0).build()
						)
						,

						meteringLevelTestConfig

				},

				{
						"Case : 3 all usage reported for new services",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 5, ADD_ON_SUBSCRIPTION_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 6, ADD_ON_SUBSCRIPTION_SUFFICS + 2),
								newEntry(PCC_RULE_SUFFICS + 9, ADD_ON_SUBSCRIPTION_SUFFICS + 3),
								newEntry(PCC_RULE_SUFFICS + 12, ADD_ON_SUBSCRIPTION_SUFFICS + 4))),

						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 5, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 6, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 9, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 12, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						asList(
								// addOnSubscription1

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription3

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis()).build()
						)
						,

						asList(
								// addOnSubscription1

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(0).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(0).build(),

								// addOnSubscription3

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(0).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withWeeklyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(0).build()
						)
						,

						meteringLevelTestConfig

				},
		};
	}

	public static Object[][] provider_for_UM_for_new_current_usage_list_should_be_created_with_reported_usage_and_existing_usage_have_reseted_daily_usage()
			throws Exception {

		setUp();

		MeteringLevelTestConfig meteringLevelTestConfig = new MeteringLevelTestConfig.MeteringLevelTestBuilder().withNetVertexServerContext(serverContext)
				.withServiceContext(serviceContext)
				.withPcrfReq(pcrfReq)
				.withPcrfRes(pcrfRes)
				.withExecutionContext(executionContext)
				.withAddOnSubscription(addOnSubscriptions)
				.withPolicyRepository(policyRepository)
				.build();

		return new Object[][] {

				{
                        "Case : 1 usage reported for all the services and reset_dailyUsage",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1),
								newEntry(PCC_RULE_SUFFICS + 2, BASE_PACKAGE_ID_SUFFICS +1),
								newEntry(PCC_RULE_SUFFICS + 3, BASE_PACKAGE_ID_SUFFICS +1))),
						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 1, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 2, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 3, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.build()
						)
						,
						asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(400, 400, 400, 400)
										.withDailyUsage(0, 0, 0, 0)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(400, 400, 400, 400)
										.withDailyUsage(0, 0, 0, 0)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build()
						)
						,

						meteringLevelTestConfig

				},

				{

						"Case : 2  usage reported for all the services only for AddOn",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 5, ADD_ON_SUBSCRIPTION_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 6, ADD_ON_SUBSCRIPTION_SUFFICS + 2),
								newEntry(PCC_RULE_SUFFICS + 9, ADD_ON_SUBSCRIPTION_SUFFICS + 3),
								newEntry(PCC_RULE_SUFFICS + 12, ADD_ON_SUBSCRIPTION_SUFFICS + 4))),

						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 5, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 6, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 9, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 12, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build()
						)
						,

						asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build()
						)
						,

						meteringLevelTestConfig


				},

				{
						"Case : 3 some usage reported for new services only for base package",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1),
								newEntry(PCC_RULE_SUFFICS + 2, BASE_PACKAGE_ID_SUFFICS +1),
								newEntry(PCC_RULE_SUFFICS + 4, BASE_PACKAGE_ID_SUFFICS +1))),
						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 1, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 2, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 4, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.build()
						)
						,
						asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(300, 300, 300, 300)
										.withDailyUsage(0, 0, 0, 0)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS +1,"productOfferId")
										.withAllTypeUsage(300, 300, 300, 300)
										.withDailyUsage(0, 0, 0, 0)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.build()
						)
						,

						meteringLevelTestConfig

				},

				{

						"Case : 4 some usage reported for new services only for AddOn",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 5, ADD_ON_SUBSCRIPTION_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 6, ADD_ON_SUBSCRIPTION_SUFFICS + 2),
								newEntry(PCC_RULE_SUFFICS + 9, ADD_ON_SUBSCRIPTION_SUFFICS + 3),
								newEntry(PCC_RULE_SUFFICS + 12, ADD_ON_SUBSCRIPTION_SUFFICS + 4))),

						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 5, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 6, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 9, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 12, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build()
						)
						,

						asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build()
						)
						,

						meteringLevelTestConfig


				},
				{

						"Case : 5 all usage reported for new services for addOn only",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 5, ADD_ON_SUBSCRIPTION_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 6, ADD_ON_SUBSCRIPTION_SUFFICS + 2),
								newEntry(PCC_RULE_SUFFICS + 9, ADD_ON_SUBSCRIPTION_SUFFICS + 3),
								newEntry(PCC_RULE_SUFFICS + 12, ADD_ON_SUBSCRIPTION_SUFFICS + 4))),

						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 5, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 6, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 9, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 12, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						asList(
								// addOnSubscription1

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1).build(),

								// addOnSubscription3

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1).build()
						)
						,

						asList(
								// addOnSubscription1

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1).build(),

								// addOnSubscription3

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(0)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1).build()
						)
						,

						meteringLevelTestConfig


				}
		};
	}


	public static Object[][] provider_for_UM_for_new_current_usage_list_should_be_created_with_reported_usage_and_existing_usage_have_reseted_billingCycle_usage() throws Exception {

		setUp();

		MeteringLevelTestConfig meteringLevelTestConfig = new MeteringLevelTestConfig.MeteringLevelTestBuilder().withNetVertexServerContext(serverContext)
				.withServiceContext(serviceContext)
				.withPcrfReq(pcrfReq)
				.withPcrfRes(pcrfRes)
				.withExecutionContext(executionContext)
				.withPolicyRepository(policyRepository)
				.withAddOnSubscription(addOnSubscriptions)
				.build();

		return new Object[][] {

				{
						"Case : 1 usage reported for all the services only for AddOn",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 5, ADD_ON_SUBSCRIPTION_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 6, ADD_ON_SUBSCRIPTION_SUFFICS + 2),
								newEntry(PCC_RULE_SUFFICS + 9, ADD_ON_SUBSCRIPTION_SUFFICS + 3),
								newEntry(PCC_RULE_SUFFICS + 12, ADD_ON_SUBSCRIPTION_SUFFICS + 4))),

						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 5, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 6, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 9, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 12, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build()
						)
						,

						asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build()
						)
						,

						meteringLevelTestConfig

				},

				{

						"Case : 2 usage reported for some services only for AddOn",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 5, ADD_ON_SUBSCRIPTION_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 6, ADD_ON_SUBSCRIPTION_SUFFICS + 2),
								newEntry(PCC_RULE_SUFFICS + 9, ADD_ON_SUBSCRIPTION_SUFFICS + 3),
								newEntry(PCC_RULE_SUFFICS + 12, ADD_ON_SUBSCRIPTION_SUFFICS + 4))),

						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 5, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 6, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 9, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 12, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build()
						)
						,

						asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(200, 200, 200, 200)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build()
						)
						,

						meteringLevelTestConfig


				},

				{

						"Case : 3 all usage reported for new services for addOn only",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 5, ADD_ON_SUBSCRIPTION_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 6, ADD_ON_SUBSCRIPTION_SUFFICS + 2),
								newEntry(PCC_RULE_SUFFICS + 9, ADD_ON_SUBSCRIPTION_SUFFICS + 3),
								newEntry(PCC_RULE_SUFFICS + 12, ADD_ON_SUBSCRIPTION_SUFFICS + 4))),

						// reported usage
						asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 5, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 6, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 9, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 12, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						asList(
								// addOnSubscription1

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription3

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build()
						)
						,

						asList(
								// addOnSubscription1

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								// addOnSubscription3

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2,"productOfferId")
										.withAllTypeUsage(100, 100, 100, 100)
										.withBillingCycleUsage(0, 0, 0, 0)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).build()
						)
						,

						meteringLevelTestConfig


				}
		};
	}


}
