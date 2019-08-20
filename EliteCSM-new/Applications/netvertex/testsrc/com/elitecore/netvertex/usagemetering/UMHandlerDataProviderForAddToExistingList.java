package com.elitecore.netvertex.usagemetering;


public class UMHandlerDataProviderForAddToExistingList {/*

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
	private static List<AddOnSubscription> addOnSubscriptions;

	public static void setUp() {

		// Base Package
		BasePackage basePackage = Mockito.mock(BasePackage.class);
		when(basePackage.getName()).thenReturn(BASE_PACKAGE_SUFFICS + 1);
		when(basePackage.getId()).thenReturn(BASE_PACKAGE_ID_SUFFICS + 1);

		QuotaProfile quotaProfileDetail = mock(QuotaProfile.class);
		when(quotaProfileDetail.getId()).thenReturn(QUOTA_PROFILE_SUFFICS + 1);
		when(basePackage.getQuotaProfileByMonitoringKey(PCC_RULE_SUFFICS + 1)).thenReturn(quotaProfileDetail);
		when(basePackage.getServiceId(PCC_RULE_SUFFICS + 1)).thenReturn(SERVICE_ID_SUFFICS + 1);
		when(basePackage.getQuotaProfileByMonitoringKey(PCC_RULE_SUFFICS + 2)).thenReturn(quotaProfileDetail);
		when(basePackage.getServiceId(PCC_RULE_SUFFICS + 2)).thenReturn(SERVICE_ID_SUFFICS + 1);
		when(basePackage.getQuotaProfileByMonitoringKey(PCC_RULE_SUFFICS + 3)).thenReturn(quotaProfileDetail);
		when(basePackage.getServiceId(PCC_RULE_SUFFICS + 3)).thenReturn(SERVICE_ID_SUFFICS + 1);

		QuotaProfile quotaProfile2 = mock(QuotaProfile.class);
		when(quotaProfile2.getId()).thenReturn(QUOTA_PROFILE_SUFFICS + 2);
		when(basePackage.getServiceId(PCC_RULE_SUFFICS + 4)).thenReturn(SERVICE_ID_SUFFICS + 2);
		when(basePackage.getQuotaProfileByMonitoringKey(PCC_RULE_SUFFICS + 4)).thenReturn(quotaProfile2);

		// AddOn1
		AddOn addOn1 = Mockito.mock(AddOn.class);
		when(addOn1.getId()).thenReturn(ADD_ON_ID_SUFFICS + 1);
		when(addOn1.getValidityPeriodUnit()).thenReturn(ValidityPeriodUnit.DAY);
		when(addOn1.getUsageResetInterval()).thenReturn(2);

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

		// AddOn2
		AddOn addOn2 = Mockito.mock(AddOn.class);
		when(addOn2.getId()).thenReturn(ADD_ON_ID_SUFFICS + 2);
		when(addOn2.getValidityPeriodUnit()).thenReturn(ValidityPeriodUnit.DAY);
		when(addOn2.getUsageResetInterval()).thenReturn(1);

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

		policyRepository = Mockito.mock(PolicyRepository.class);
		when(policyRepository.getBasePackageById(BASE_PACKAGE_ID_SUFFICS + 1)).thenReturn(basePackage);
		when(policyRepository.getAddOnById(ADD_ON_ID_SUFFICS + 1)).thenReturn(addOn1);
		when(policyRepository.getAddOnById(ADD_ON_ID_SUFFICS + 2)).thenReturn(addOn2);

		serverContext = new DummyNetvertexServerContextImpl();
		serverContext.setPolicyRepository(policyRepository);

		serviceContext = new DummyPCRFServiceContext();
		serviceContext.setServerContext(serverContext);

		pcrfReq = new PCRFRequestImpl();
		pcrfReq.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_PACKAGE.val, BASE_PACKAGE_SUFFICS + 1);
		pcrfReq.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, SUBSCRIBER_IDENTITY);
		pcrfReq.setAttribute(PCRFKeyConstants.SUB_CUI.val, SUBSCRIBER_IDENTITY);

		pcrfRes = new PCRFResponseImpl();
		pcrfRes.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_PACKAGE.val, BASE_PACKAGE_SUFFICS + 1);
		pcrfRes.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, SUBSCRIBER_IDENTITY);
		pcrfRes.setAttribute(PCRFKeyConstants.SUB_CUI.val, SUBSCRIBER_IDENTITY);

		executionContext = new ExecutionContext(null, pcrfReq, pcrfRes);

		AddOnSubscription addOnSubscription1 = new AddOnSubscription.AddOnSubscriptionBuilder().
				withAddonSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1).
				withAddOn(addOn1).
				withSubscriberIdentity(SUBSCRIBER_IDENTITY).
				withEndTime(new Timestamp(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1))).
				build();

		AddOnSubscription addOnSubscription2 = new AddOnSubscription.AddOnSubscriptionBuilder().
				withAddonSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2).
				withAddOn(addOn1).
				withSubscriberIdentity(SUBSCRIBER_IDENTITY).
				withEndTime(new Timestamp(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(1))).
				build();

		AddOnSubscription addOnSubscription3 = new AddOnSubscription.AddOnSubscriptionBuilder().
				withAddonSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3).
				withAddOn(addOn2).
				withSubscriberIdentity(SUBSCRIBER_IDENTITY).
				withEndTime(new Timestamp(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(365))).
				build();

		AddOnSubscription addOnSubscription4 = new AddOnSubscription.AddOnSubscriptionBuilder().
				withAddonSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4).
				withAddOn(addOn2).
				withSubscriberIdentity(SUBSCRIBER_IDENTITY).
				withEndTime(new Timestamp(executionContext.getCurrentTime().getTimeInMillis() + TimeUnit.DAYS.toMillis(365))).
				build();

		addOnSubscriptions = Arrays.asList(addOnSubscription1, addOnSubscription2, addOnSubscription3, addOnSubscription4);



	}

	public static Object[][] provider_for_UM_for_new_current_usage_list_should_be_created_with_reported_usage_and_existing_usage() throws Exception {

		setUp();

		MeteringLevelTestConfig meteringLevelTestConfig = new MeteringLevelTestBuilder().withNetVertexServerContext(serverContext)
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

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 2, BASE_PACKAGE_ID_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 3, BASE_PACKAGE_ID_SUFFICS + 1))),
						// reported usage
						Arrays.asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 1, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 2, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 3, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						Arrays.asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1,BASE_PACKAGE_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.build()
						)
						,

						Arrays.asList(
								new SubscriberUsageBuilder(NEW_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1,BASE_PACKAGE_ID_SUFFICS + 1)
										.withAllTypeUsage(300, 300, 300, 300)
										.build(),
								new SubscriberUsageBuilder(NEW_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS + 1)
										.withAllTypeUsage(300, 300, 300, 300)
										.build()
						)
						,

						meteringLevelTestConfig

				},

				new Object[]{
						"Case : 1 usage reported for all the services only for AddOn",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 5, ADD_ON_SUBSCRIPTION_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 6, ADD_ON_SUBSCRIPTION_SUFFICS + 2),
								newEntry(PCC_RULE_SUFFICS + 9, ADD_ON_SUBSCRIPTION_SUFFICS + 3),
								newEntry(PCC_RULE_SUFFICS + 12, ADD_ON_SUBSCRIPTION_SUFFICS + 4))),

						// reported usage
						Arrays.asList(
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
						Arrays.asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build()
						)
						,

						Arrays.asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(NEW_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.build(),

								new SubscriberUsageBuilder(NEW_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(NEW_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.build(),

								new SubscriberUsageBuilder(NEW_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(NEW_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.build(),

								new SubscriberUsageBuilder(NEW_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(NEW_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.build(),

								new SubscriberUsageBuilder(NEW_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.build()
						)
						,

						meteringLevelTestConfig

				},

				{
						"Case : 2 usage reported for some services only for base package",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 2, BASE_PACKAGE_ID_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 3, BASE_PACKAGE_ID_SUFFICS + 1))),
						// reported usage
						Arrays.asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 1, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 2, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 4, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						Arrays.asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1,BASE_PACKAGE_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.build()
						)
						,

						Arrays.asList(
								new SubscriberUsageBuilder(NEW_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1,BASE_PACKAGE_ID_SUFFICS + 1)
										.withAllTypeUsage(200, 200, 200, 200).build(),

								new SubscriberUsageBuilder(NEW_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS + 1)
										.withAllTypeUsage(200, 200, 200, 200).build()
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
						Arrays.asList(
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
						Arrays.asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build()
						)
						,

						Arrays.asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(NEW_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(NEW_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(NEW_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(NEW_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4).build()
						)
						,

						meteringLevelTestConfig


				},

				{
						"Case : 3 usage reported for new services  only for base package",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 4, BASE_PACKAGE_ID_SUFFICS + 1))),
						// reported usage
						Arrays.asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 4, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						Arrays.asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1,BASE_PACKAGE_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS + 1)
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

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 4, BASE_PACKAGE_ID_SUFFICS + 1))),
						// reported usage
						Arrays.asList(
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
						Arrays.asList(
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
						Arrays.asList(
								// addOnSubscription1

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription3

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
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

		MeteringLevelTestConfig meteringLevelTestConfig = new MeteringLevelTestBuilder().withNetVertexServerContext(serverContext)
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

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 2, BASE_PACKAGE_ID_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 3, BASE_PACKAGE_ID_SUFFICS + 1))),
						// reported usage
						Arrays.asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 1, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 2, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 3, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						Arrays.asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1,BASE_PACKAGE_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
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
						Arrays.asList(
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
						Arrays.asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build()
						)
						,

						null,

						meteringLevelTestConfig

				},

				{
						"Case : 2 some usage reported for new services",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 2, BASE_PACKAGE_ID_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 4, BASE_PACKAGE_ID_SUFFICS + 1))),
						// reported usage
						Arrays.asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 1, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 2, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 4, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						Arrays.asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1,BASE_PACKAGE_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.build()
						)
						,
						null,

						meteringLevelTestConfig

				},

				{
						"Case : 2 some usage reported for AddOn",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 5, ADD_ON_SUBSCRIPTION_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 6, ADD_ON_SUBSCRIPTION_SUFFICS + 2),
								newEntry(PCC_RULE_SUFFICS + 9, ADD_ON_SUBSCRIPTION_SUFFICS + 3),
								newEntry(PCC_RULE_SUFFICS + 12, ADD_ON_SUBSCRIPTION_SUFFICS + 4))),

						// reported usage
						Arrays.asList(
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
						Arrays.asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.build()
						)
						,

						null,

						meteringLevelTestConfig

				},

				{
						"Case : 3 all usage reported for new services only base package",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 4, BASE_PACKAGE_ID_SUFFICS + 1))),
						// reported usage
						Arrays.asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 4, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						Arrays.asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1,BASE_PACKAGE_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis()).build()
						)
						,

						null,

						meteringLevelTestConfig

				},

				{
						"Case : 3 all usage reported for new services",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 5, ADD_ON_SUBSCRIPTION_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 6, ADD_ON_SUBSCRIPTION_SUFFICS + 2),
								newEntry(PCC_RULE_SUFFICS + 9, ADD_ON_SUBSCRIPTION_SUFFICS + 3),
								newEntry(PCC_RULE_SUFFICS + 12, ADD_ON_SUBSCRIPTION_SUFFICS + 4))),

						// reported usage
						Arrays.asList(
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
						Arrays.asList(
								// addOnSubscription1

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription3

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis()).build()
						)
						,

						null,

						meteringLevelTestConfig

				},
		};
	}

	public static Object[][] provider_for_UM_for_new_current_usage_list_should_be_created_with_reported_usage_and_existing_usage_have_reseted_daily_usage()
			throws Exception {

		setUp();

		MeteringLevelTestConfig meteringLevelTestConfig = new MeteringLevelTestBuilder().withNetVertexServerContext(serverContext)
				.withServiceContext(serviceContext)
				.withPcrfReq(pcrfReq)
				.withPcrfRes(pcrfRes)
				.withExecutionContext(executionContext)
				.withAddOnSubscription(addOnSubscriptions)
				.withPolicyRepository(policyRepository)
				.build();

		return new Object[][] {

				{
						"Case : 1 usage reported for all the services",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 2, BASE_PACKAGE_ID_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 3, BASE_PACKAGE_ID_SUFFICS + 1))),
						// reported usage
						Arrays.asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 1, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 2, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 3, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						Arrays.asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1,BASE_PACKAGE_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.build()
						)
						,
						null,

						meteringLevelTestConfig

				},

				{

						"Case : 2  usage reported for all the services only for AddOn",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 5, ADD_ON_SUBSCRIPTION_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 6, ADD_ON_SUBSCRIPTION_SUFFICS + 2),
								newEntry(PCC_RULE_SUFFICS + 9, ADD_ON_SUBSCRIPTION_SUFFICS + 3),
								newEntry(PCC_RULE_SUFFICS + 12, ADD_ON_SUBSCRIPTION_SUFFICS + 4))),

						// reported usage
						Arrays.asList(
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
						Arrays.asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS+3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS+4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS +3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS+5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS+6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build()
						)
						,

						null,

						meteringLevelTestConfig


				},

				{
						"Case : 3 some usage reported for new services only for base package",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 2, BASE_PACKAGE_ID_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 4, BASE_PACKAGE_ID_SUFFICS + 1))),
						// reported usage
						Arrays.asList(
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 1, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 2, new ServiceUnitBuilder()
										.withAllType(100).build()),
								UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(PCC_RULE_SUFFICS + 4, new ServiceUnitBuilder()
										.withAllType(100).build())
						),

						// previous usage
						Arrays.asList(
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 1,BASE_PACKAGE_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 1, QUOTA_PROFILE_SUFFICS + 1, BASE_PACKAGE_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.build()
						)
						,
						null,

						meteringLevelTestConfig

				},

				{

						"Case : 4 some usage reported for new services only for AddOn",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 5, ADD_ON_SUBSCRIPTION_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 6, ADD_ON_SUBSCRIPTION_SUFFICS + 2),
								newEntry(PCC_RULE_SUFFICS + 9, ADD_ON_SUBSCRIPTION_SUFFICS + 3),
								newEntry(PCC_RULE_SUFFICS + 12, ADD_ON_SUBSCRIPTION_SUFFICS + 4))),

						// reported usage
						Arrays.asList(
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
						Arrays.asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100).build()
						)
						,

						null,

						meteringLevelTestConfig


				},
				{

						"Case : 5 all usage reported for new services for addOn only",

						newLinkedHashMap(asList(newEntry(PCC_RULE_SUFFICS + 5, ADD_ON_SUBSCRIPTION_SUFFICS + 1),
								newEntry(PCC_RULE_SUFFICS + 6, ADD_ON_SUBSCRIPTION_SUFFICS + 2),
								newEntry(PCC_RULE_SUFFICS + 9, ADD_ON_SUBSCRIPTION_SUFFICS + 3),
								newEntry(PCC_RULE_SUFFICS + 12, ADD_ON_SUBSCRIPTION_SUFFICS + 4))),

						// reported usage
						Arrays.asList(
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
						Arrays.asList(
								// addOnSubscription1

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1).build(),

								// addOnSubscription3

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis())
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1).build()
						)
						,

						null,

						meteringLevelTestConfig


				}
		};
	}


	public static Object[][] provider_for_UM_for_new_current_usage_list_should_be_created_with_reported_usage_and_existing_usage_have_reseted_billingCycle_usage() throws Exception {

		setUp();

		MeteringLevelTestConfig meteringLevelTestConfig = new MeteringLevelTestBuilder().withNetVertexServerContext(serverContext)
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
						Arrays.asList(
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
						Arrays.asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 3, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 4, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 5, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, SERVICE_ID_SUFFICS + 6, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build()
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
						Arrays.asList(
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
						Arrays.asList(
								// addOnSubscription1
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 3, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription3
								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build()
						)
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
						Arrays.asList(
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
						Arrays.asList(
								// addOnSubscription1

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 1)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription2

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 4, ADD_ON_ID_SUFFICS + 1)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 2)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription3

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 6, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 3)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build(),

								// addOnSubscription4

								new SubscriberUsageBuilder(DEFAULT_SERVICE_ID, SUBSCRIBER_IDENTITY, DEFAULT_SERVICE_ID, QUOTA_PROFILE_SUFFICS + 5, ADD_ON_ID_SUFFICS + 2)
										.withAllTypeUsage(100, 100, 100, 100)
										.withSubscriptionId(ADD_ON_SUBSCRIPTION_SUFFICS + 4)
										.withDailyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 100)
										.withWeeklyResetTime(executionContext.getCurrentTime().getTimeInMillis() + 1)
										.withBillingCycleResetTime(executionContext.getCurrentTime().getTimeInMillis()).build()
						)
						,

						null,

						meteringLevelTestConfig


				}
		};
	}



*/}
