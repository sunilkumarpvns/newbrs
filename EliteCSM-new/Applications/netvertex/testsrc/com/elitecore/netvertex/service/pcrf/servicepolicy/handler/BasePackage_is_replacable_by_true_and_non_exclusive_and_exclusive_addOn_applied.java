package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import org.junit.Ignore;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
@Ignore
public class BasePackage_is_replacable_by_true_and_non_exclusive_and_exclusive_addOn_applied {/*
	

	
	@Mock private NetVertexServerContext serverContext;
	@Mock private PCRFServiceContext serviceContext;
	@Mock private SubscriberRepository subscriberRepository;
	
	private PCRFResponseBuilder pcrfResponseBuilder;
	private PCRFRequestBuilder pcrfRequestBuilder;
	private SPRInfoBuilder sprBuilder;
	private DummyPolicyRepository.PolicyRepositoryBuilder policyRepositoryBuilder;
	
	private String subscriberIdentity = "1234";
	private String basePackageName = "Base package";
	
	@Before
	public void setUp() {
		initMocks(this);
		
		policyRepositoryBuilder = new DummyPolicyRepository.PolicyRepositoryBuilder();
		sprBuilder = new SPRInfoBuilder().withSubscriberPackage(basePackageName);
		pcrfRequestBuilder = new PCRFRequestBuilder().addSubscriberIdentity(subscriberIdentity);
		pcrfResponseBuilder = new PCRFResponseBuilder().addSubscriberIdentity(subscriberIdentity);
		
		when(serviceContext.getServerContext()).thenReturn(serverContext);
	}
	
	
	public class SessionQoSSelection {
		
		@Test
		public void test_process_should_select_exclusive_addOn_QoS_if_non_exclusive_addOn_is_lower() throws OperationFailedException {
			
			BasePackage basePackage = BasePackageFactory.createReplacableByAddOnBasePackage(basePackageName);
			
			
			AddOn addOn = AddOnPackageFactory.createExclusiveAddOn("exclusive").hasLowerQosThan(basePackage).build();
			AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
			
			AddOn addOn2 = AddOnPackageFactory.createNonExclusiveAddOn("non-exclusive").hasLowerQosThan(addOn).build();
			AddOnSubscription addOnSubscription2 = SubscriptionFactory.createSubscriptionFor(addOn2).build();
			
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
			addOnSubscriptions.put(addOnSubscription2.getAddonSubscriptionId(), addOnSubscription2);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = sprBuilder.build();
			SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			Map<String, SubscriberUsage> subscriberUsages = UsageFactory.createUsageFor(basePackage).hasUsageExceededForAllServices().build();
			
			Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
			packageUsages.put(basePackage.getId(), subscriberUsages);
			
			when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
			when(subscriberRepository.getSubscriptions(Mockito.anyString())).thenReturn(addOnSubscriptions);
			
			ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
			subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
			getLogger().debug("", pcrfResponse.toString());
			
			assertSame(pcrfResponse.getQoSInformation().getSessionQoS(),AddOnPackageFactory.findHigestQoS(addOn));
		}
		
		@Test
		public void test_process_should_select_non_exclusive_addOn_QoS_if_exclusive_addOn_is_lower() throws OperationFailedException {
			
			BasePackage basePackage = BasePackageFactory.createReplacableByAddOnBasePackage(basePackageName);
			
			
			AddOn addOn = AddOnPackageFactory.createExclusiveAddOn("exclusive").hasLowerQosThan(basePackage).build();
			AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
			
			AddOn addOn2 = AddOnPackageFactory.createNonExclusiveAddOn("non-exclusive").hasHigherQosThan(addOn).build();
			AddOnSubscription addOnSubscription2 = SubscriptionFactory.createSubscriptionFor(addOn2).build();
			
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
			addOnSubscriptions.put(addOnSubscription2.getAddonSubscriptionId(), addOnSubscription2);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = sprBuilder.build();
			SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			Map<String, SubscriberUsage> subscriberUsages = UsageFactory.createUsageFor(basePackage).hasUsageExceededForAllServices().build();
			
			Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
			packageUsages.put(basePackage.getId(), subscriberUsages);
			
			when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
			when(subscriberRepository.getSubscriptions(Mockito.anyString())).thenReturn(addOnSubscriptions);
			
			ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
			subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
			getLogger().debug("", pcrfResponse.toString());
			
			assertSame(pcrfResponse.getQoSInformation().getSessionQoS(),AddOnPackageFactory.findHigestQoS(addOn2));
		}
		
		@Test
		public void test_process_should_select_non_exclusive_addOn_QoS_if_exclusive_addOn_is_equal() throws OperationFailedException {
			
			BasePackage basePackage = BasePackageFactory.createReplacableByAddOnBasePackage(basePackageName);
			
			
			AddOn addOn = AddOnPackageFactory.createExclusiveAddOn("exclusive").hasLowerQosThan(basePackage).build();
			AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
			
			AddOn addOn2 = AddOnPackageFactory.createNonExclusiveAddOn("non-exclusive").hasEqualQosThan(addOn).build();
			AddOnSubscription addOnSubscription2 = SubscriptionFactory.createSubscriptionFor(addOn2).build();
			
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
			addOnSubscriptions.put(addOnSubscription2.getAddonSubscriptionId(), addOnSubscription2);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = sprBuilder.build();
			SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			Map<String, SubscriberUsage> subscriberUsages = UsageFactory.createUsageFor(basePackage).hasUsageExceededForAllServices().build();
			
			Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
			packageUsages.put(basePackage.getId(), subscriberUsages);
			
			when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
			when(subscriberRepository.getSubscriptions(Mockito.anyString())).thenReturn(addOnSubscriptions);
			
			ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
			subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
			getLogger().debug("", pcrfResponse.toString());
			
			assertSame(pcrfResponse.getQoSInformation().getSessionQoS(),AddOnPackageFactory.findHigestQoS(addOn2));
		}
		
		@Test
		public void test_process_should_select_non_exclusive_addOn_QoS_when_exclusive_addOn_not_applied() throws OperationFailedException {
			
			BasePackage basePackage = BasePackageFactory.createReplacableByAddOnBasePackage(basePackageName);
			
			QuotaProfile quotaProfileDetail = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail quotaProfileDetail = QoSProfileDetailFactory.createQoSProfile().hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).forEachServicesHasPCCRule().build();
			QoSProfile addOnQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfileDetail).hasHSQLevelQoSProfileDetail(quotaProfileDetail).build();
			
			AddOn addOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(), "exclusive").exclusiveAddOn()
					.addQoSProfile(addOnQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
			
			AddOn exclusive2 = AddOnPackageFactory.createNonExclusiveAddOn("non-exclusive").build();
			AddOnSubscription exclusive2Subscription = SubscriptionFactory.createSubscriptionFor(exclusive2).build();
			
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
			addOnSubscriptions.put(exclusive2Subscription.getAddonSubscriptionId(), exclusive2Subscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = sprBuilder.build();
			SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			Map<String, SubscriberUsage> subscriberUsages = UsageFactory.createUsageFor(basePackage).hasUsageLowerThanHSQForAllServices().build();
			Map<String, SubscriberUsage> addOnUsages = UsageFactory.createUsageFor(addOn).hasUsageExceededForAllServices().build();
			
			Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
			packageUsages.put(basePackage.getId(), subscriberUsages);
			packageUsages.put(addOnSubscription.getAddonSubscriptionId(), addOnUsages);
			
			when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
			when(subscriberRepository.getSubscriptions(Mockito.anyString())).thenReturn(addOnSubscriptions);
			
			ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
			subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
			getLogger().debug("", pcrfResponse.toString());
			
			assertSame(pcrfResponse.getQoSInformation().getSessionQoS(),AddOnPackageFactory.findHigestQoS(exclusive2));
		}
		
		@Test
		public void test_process_should_select_exclusive_addOn_QoS_when_non_exclusive_addOn_not_applied() throws OperationFailedException {
			
			BasePackage basePackage = BasePackageFactory.createReplacableByAddOnBasePackage(basePackageName);
			
			AddOn addOn = AddOnPackageFactory.createExclusiveAddOn("exclusive").hasLowerQosThan(basePackage).build();
			AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
			
			QuotaProfile quotaProfileDetail = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail quotaProfileDetail = QoSProfileDetailFactory.createQoSProfile().hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).forEachServicesHasPCCRule().build();
			QoSProfile addOnQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfileDetail).hasHSQLevelQoSProfileDetail(quotaProfileDetail).build();
			
			AddOn nonExclusive = new AddOn.AddOnBuilder(UUID.randomUUID().toString(), "non-exclusive")
					.addQoSProfile(addOnQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			AddOnSubscription exclusive2Subscription = SubscriptionFactory.createSubscriptionFor(nonExclusive).build();
			
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
			addOnSubscriptions.put(exclusive2Subscription.getAddonSubscriptionId(), exclusive2Subscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = sprBuilder.build();
			SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			Map<String, SubscriberUsage> subscriberUsages = UsageFactory.createUsageFor(basePackage).hasUsageLowerThanHSQForAllServices().build();
			Map<String, SubscriberUsage> addOnUsages = UsageFactory.createUsageFor(nonExclusive).hasUsageExceededForAllServices().build();
			
			Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
			packageUsages.put(basePackage.getId(), subscriberUsages);
			packageUsages.put(exclusive2Subscription.getAddonSubscriptionId(), addOnUsages);
			
			when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
			when(subscriberRepository.getSubscriptions(Mockito.anyString())).thenReturn(addOnSubscriptions);
			
			ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
			subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
			getLogger().debug("", pcrfResponse.toString());
			
			assertSame(pcrfResponse.getQoSInformation().getSessionQoS(),AddOnPackageFactory.findHigestQoS(addOn));
		}
		
		@Test
		public void test_process_should_select_base_package_QoS_when_addOn_not_applied() throws OperationFailedException {
			
			BasePackage basePackage = BasePackageFactory.createReplacableByAddOnBasePackage(basePackageName);
			
			QuotaProfile quotaProfileDetail = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail quotaProfileDetail = QoSProfileDetailFactory.createQoSProfile().hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).forEachServicesHasPCCRule().build();
			QoSProfile addOnQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfileDetail).hasHSQLevelQoSProfileDetail(quotaProfileDetail).build();
			
			AddOn addOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(), "non-exclusive")
			.addQoSProfile(addOnQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
			
			
			QuotaProfile quotaProfile2 = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail quotaProfileDetail2 = QoSProfileDetailFactory.createQoSProfile().hasQuotaProfileDetail(quotaProfile2.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).forEachServicesHasPCCRule().build();
			QoSProfile addOnQoSProfile2 = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfile2).hasHSQLevelQoSProfileDetail(quotaProfileDetail2).build();
			AddOn addOn2 = new AddOn.AddOnBuilder(UUID.randomUUID().toString(), "non-exclusive")
			.addQoSProfile(addOnQoSProfile2).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			AddOnSubscription addOnSubscription2 = SubscriptionFactory.createSubscriptionFor(addOn2).build();
			
			
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
			addOnSubscriptions.put(addOnSubscription2.getAddonSubscriptionId(), addOnSubscription2);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = sprBuilder.build();
			SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			Map<String, SubscriberUsage> basePackageUsages = UsageFactory.createUsageFor(basePackage).hasUsageLowerThanHSQForAllServices().build();
			Map<String, SubscriberUsage> addOnUsages = UsageFactory.createUsageFor(addOn).hasUsageExceededForAllServices().build();
			Map<String, SubscriberUsage> addOn2Usages = UsageFactory.createUsageFor(addOn2).hasUsageExceededForAllServices().build();
			
			Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
			packageUsages.put(basePackage.getId(), basePackageUsages);
			packageUsages.put(addOnSubscription.getAddonSubscriptionId(), addOnUsages);
			packageUsages.put(addOnSubscription2.getAddonSubscriptionId(), addOn2Usages);
			
			when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
			when(subscriberRepository.getSubscriptions(Mockito.anyString())).thenReturn(addOnSubscriptions);
			
			ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
			subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
			getLogger().debug("", pcrfResponse.toString());
			
			assertSame(pcrfResponse.getQoSInformation().getSessionQoS(),BasePackageFactory.findHigestQoS(basePackage));
		}
		
		
	}
	
	
	public class PCCRuleSelection {
		
		@Test
		public void test_process_should_select_pccRules_that_have_best_QoS_from_multiplse_non_exclusive_addOn_and_exclusive_addOns_subscription() throws OperationFailedException {
		
			RatingGroup ratingGroup = new RatingGroup("1", "defaultRatingGroup", "", 1);
			ServiceDataFlow serviceDataFlow = new ServiceDataFlow("allow", "tcp", "any", "0", "any", "0");
			ServiceType httpService = new ServiceType("2", "http", 2, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
			ServiceType ftpService = new ServiceType("3", "ftp", 3, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
			///Base Package
			PCCRule ftpPCCRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(ftpService).build();
			PCCRule httpPCCRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(httpService).build();
			
			QuotaProfile quotaProfileDetail = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail qosProfileDetail = QoSProfileDetailFactory.createQoSProfile().
												hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).
												pccRules(Arrays.asList(httpPCCRule,ftpPCCRule)).build();
			QoSProfile qosProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfileDetail).hasHSQLevelQoSProfileDetail(qosProfileDetail).build();
			
			BasePackage basePackage = new BasePackage.BasePackageBuilder(UUID.randomUUID().toString(),basePackageName).replacableByAddOn().addQoSProfile(qosProfile).build();
			
			
			///Exclusive AddOn
			PCCRule nonExclusiveAddOnFtpPCCRule = PCCRuleFactory.createPCCRuleHasLowerQoSThan(ftpPCCRule);
			PCCRule nonExclusiveAddOnHttpPCCRule = PCCRuleFactory.createPCCRuleHasLowerQoSThan(httpPCCRule);
			
			QuotaProfile nonExclusiveAddOnQuotaProfile = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail nonExclusiveAddOnQoSProfileDetail = QoSProfileDetailFactory.createQoSProfile().
					hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).
					pccRules(Arrays.asList(nonExclusiveAddOnHttpPCCRule,nonExclusiveAddOnFtpPCCRule)).build();
			QoSProfile nonExclusiveAddQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(nonExclusiveAddOnQuotaProfile).hasHSQLevelQoSProfileDetail(nonExclusiveAddOnQoSProfileDetail).build();
			AddOn nonExclusiveAddOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(),"non exclusive").
					addQoSProfile(nonExclusiveAddQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			
			
			///Exclusive AddOn 2
			PCCRule nonExclusiveAddOn2FtpPCCRule = PCCRuleFactory.createPCCRuleHasLowerQoSThan(nonExclusiveAddOnFtpPCCRule);
			PCCRule nonExclusiveAddOn2HttpPCCRule = PCCRuleFactory.createPCCRuleHasHigherQoSThan(nonExclusiveAddOnHttpPCCRule);
			QuotaProfile nonExclusiveAdd2OnQuotaProfile = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail nonExclusiveAdd2OnQoSProfileDetail = QoSProfileDetailFactory.createQoSProfile().
					hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).
					pccRules(Arrays.asList(nonExclusiveAddOn2FtpPCCRule,nonExclusiveAddOn2HttpPCCRule)).withSessionQoSLowerThan(nonExclusiveAddOnQoSProfileDetail.getSessionQoS()).build();
			QoSProfile nonExclusiveAdd2QoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(nonExclusiveAdd2OnQuotaProfile).hasHSQLevelQoSProfileDetail(nonExclusiveAdd2OnQoSProfileDetail).build();
			AddOn nonExclusiveAdd2 = new AddOn.AddOnBuilder(UUID.randomUUID().toString(),"non exclusive2").exclusiveAddOn().
					addQoSProfile(nonExclusiveAdd2QoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			
			
			AddOnSubscription nonExclusiveAddOnSubscription = SubscriptionFactory.createSubscriptionFor(nonExclusiveAddOn).build();
			AddOnSubscription nonExclusiveAddOn2addOnSubscription = SubscriptionFactory.createSubscriptionFor(nonExclusiveAdd2).build();
			
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(nonExclusiveAddOnSubscription.getAddonSubscriptionId(), nonExclusiveAddOnSubscription);
			addOnSubscriptions.put(nonExclusiveAddOn2addOnSubscription.getAddonSubscriptionId(), nonExclusiveAddOn2addOnSubscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = sprBuilder.build();
			SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			Map<String, SubscriberUsage> subscriberUsages = UsageFactory.createUsageFor(basePackage).hasUsageExceededForAllServices().build();
			Map<String, SubscriberUsage> nonExclusiveAddOnSubscriberUsages = UsageFactory.createUsageFor(nonExclusiveAddOn).hasUsageLowerThanHSQForAllServices().build();
			Map<String, SubscriberUsage> nonExclusiveAdd2OnSubscriberUsages = UsageFactory.createUsageFor(nonExclusiveAddOn).hasUsageLowerThanHSQForAllServices().build();
			
			Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
			packageUsages.put(basePackage.getId(), subscriberUsages);
			packageUsages.put(nonExclusiveAddOnSubscription.getAddonSubscriptionId(), nonExclusiveAddOnSubscriberUsages);
			packageUsages.put(nonExclusiveAddOn2addOnSubscription.getAddonSubscriptionId(), nonExclusiveAdd2OnSubscriberUsages);
			
			when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
			when(subscriberRepository.getSubscriptions(Mockito.anyString())).thenReturn(addOnSubscriptions);
			
			ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
			subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
			getLogger().debug("", pcrfResponse.toString());
			
			List<PCCRule> expectedPCCRules = Arrays.asList(nonExclusiveAddOn2HttpPCCRule, nonExclusiveAddOnFtpPCCRule);
			
			assertReflectionEquals(pcrfResponse.getActivePCCRules(), expectedPCCRules, ReflectionComparatorMode.LENIENT_ORDER);
			assertReflectionEquals(pcrfResponse.getInstallablePCCRules(), expectedPCCRules, ReflectionComparatorMode.LENIENT_ORDER);
			assertNull(pcrfResponse.getRemovablePCCRules());
		}
		
		@Test
		public void test_process_should_select_pccRules_of_non_exclusive_addOn_subscription_which_if_both_exclusive_non_exclusive_addOn_subscription_has_same_pccRule_qos() throws OperationFailedException {
		
			RatingGroup ratingGroup = new RatingGroup("1", "defaultRatingGroup", "", 1);
			ServiceDataFlow serviceDataFlow = new ServiceDataFlow("allow", "tcp", "any", "0", "any", "0");
			ServiceType httpService = new ServiceType("2", "http", 2, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
			ServiceType ftpService = new ServiceType("3", "ftp", 3, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
			///Base Package
			PCCRule ftpPCCRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(ftpService).build();
			PCCRule httpPCCRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(httpService).build();
			
			QuotaProfile quotaProfileDetail = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail qosProfileDetail = QoSProfileDetailFactory.createQoSProfile().
												hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).
												pccRules(Arrays.asList(httpPCCRule,ftpPCCRule)).build();
			QoSProfile qosProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfileDetail).hasHSQLevelQoSProfileDetail(qosProfileDetail).build();
			
			BasePackage basePackage = new BasePackage.BasePackageBuilder(UUID.randomUUID().toString(),basePackageName).replacableByAddOn().addQoSProfile(qosProfile).build();
			
			
			///Exclusive AddOn
			PCCRule exclusiveAddOnFtpPCCRule = PCCRuleFactory.createPCCRuleHasLowerQoSThan(ftpPCCRule);
			PCCRule exclusiveAddOnHttpPCCRule = PCCRuleFactory.createPCCRuleHasLowerQoSThan(httpPCCRule);
			
			QuotaProfile exclusiveAddOnQuotaProfile = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail exclusiveAddOnQoSProfileDetail = QoSProfileDetailFactory.createQoSProfile().
					hasQuotaProfileDetail(exclusiveAddOnQuotaProfile.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).
					pccRules(Arrays.asList(exclusiveAddOnHttpPCCRule,exclusiveAddOnFtpPCCRule)).build();
			QoSProfile exclusiveAddQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(exclusiveAddOnQuotaProfile).hasHSQLevelQoSProfileDetail(exclusiveAddOnQoSProfileDetail).build();
			AddOn exclusiveAddOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(),"non-exclusive").exclusiveAddOn()
					.addQoSProfile(exclusiveAddQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			
			
			///Exclusive AddOn 2
			PCCRule exclusiveAddOn2FtpPCCRule = PCCRuleFactory.createPCCRuleHasEqualQoSTo(exclusiveAddOnFtpPCCRule);
			PCCRule exclusiveAddOn2HttpPCCRule = PCCRuleFactory.createPCCRuleHasEqualQoSTo(exclusiveAddOnHttpPCCRule);
			QuotaProfile exclusiveAdd2OnQuotaProfile = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail exclusiveAdd2OnQoSProfileDetail = QoSProfileDetailFactory.createQoSProfile().
					hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).
					pccRules(Arrays.asList(exclusiveAddOn2FtpPCCRule,exclusiveAddOn2HttpPCCRule)).withSessionQoSEqualTo(exclusiveAddOnQoSProfileDetail.getSessionQoS()).build();
			QoSProfile exclusiveAdd2QoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(exclusiveAdd2OnQuotaProfile).hasHSQLevelQoSProfileDetail(exclusiveAdd2OnQoSProfileDetail).build();
			AddOn exclusiveAdd2 = new AddOn.AddOnBuilder(UUID.randomUUID().toString(),"non-exclusive")
					.addQoSProfile(exclusiveAdd2QoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			
			
			AddOnSubscription exclusiveAddOn2subscription = SubscriptionFactory.createSubscriptionFor(exclusiveAdd2).build();
			AddOnSubscription exclusiveAddOnSubscription = SubscriptionFactory.createSubscriptionFor(exclusiveAddOn).build();
			
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(exclusiveAddOnSubscription.getAddonSubscriptionId(), exclusiveAddOnSubscription);
			addOnSubscriptions.put(exclusiveAddOn2subscription.getAddonSubscriptionId(), exclusiveAddOn2subscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = sprBuilder.build();
			SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			Map<String, SubscriberUsage> subscriberUsages = UsageFactory.createUsageFor(basePackage).hasUsageLowerThanHSQForAllServices().build();
			Map<String, SubscriberUsage> exclusiveAddOnSubscriberUsages = UsageFactory.createUsageFor(exclusiveAddOn).hasUsageLowerThanHSQForAllServices().build();
			Map<String, SubscriberUsage> exclusiveAdd2OnSubscriberUsages = UsageFactory.createUsageFor(exclusiveAddOn).hasUsageLowerThanHSQForAllServices().build();
			
			Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
			packageUsages.put(basePackage.getId(), subscriberUsages);
			packageUsages.put(exclusiveAddOn2subscription.getAddonSubscriptionId(), exclusiveAdd2OnSubscriberUsages);
			packageUsages.put(exclusiveAddOnSubscription.getAddonSubscriptionId(), exclusiveAddOnSubscriberUsages);
			
			when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
			when(subscriberRepository.getSubscriptions(Mockito.anyString())).thenReturn(addOnSubscriptions);
			
			ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
			subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
			
			List<PCCRule> expectedPCCRules = Arrays.asList(exclusiveAddOn2HttpPCCRule, exclusiveAddOn2FtpPCCRule);
			
			getLogger().debug("", pcrfResponse.toString());
			assertReflectionEquals(exclusiveAdd2OnQoSProfileDetail.getSessionQoS(), pcrfResponse.getQoSInformation().getSessionQoS(), ReflectionComparatorMode.LENIENT_ORDER);
			assertReflectionEquals(expectedPCCRules, pcrfResponse.getActivePCCRules(), ReflectionComparatorMode.LENIENT_ORDER);
			assertReflectionEquals(expectedPCCRules, pcrfResponse.getInstallablePCCRules(), ReflectionComparatorMode.LENIENT_ORDER);
			assertNull(pcrfResponse.getRemovablePCCRules());
		}
		
		@Test
		public void test_process_should_select_non_exclusive_addOn_pccRules_when_exclusive_addOn_not_applied() throws OperationFailedException {
			
			BasePackage basePackage = BasePackageFactory.createReplacableByAddOnBasePackage(basePackageName);
			
			
			///Exclusive AddOn
			PCCRule exclusiveAddOnFtpPCCRule = PCCRuleFactory.createPCCRuleWithRandomQoS().build();
			PCCRule exclusiveAddOnHttpPCCRule = PCCRuleFactory.createPCCRuleWithRandomQoS().build();
			
			QuotaProfile exclusiveAddOnQuotaProfile = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail exclusiveAddOnQoSProfileDetail = QoSProfileDetailFactory.createQoSProfile().
					hasQuotaProfileDetail(exclusiveAddOnQuotaProfile.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).
					pccRules(Arrays.asList(exclusiveAddOnHttpPCCRule,exclusiveAddOnFtpPCCRule)).build();
			QoSProfile exclusiveAddQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(exclusiveAddOnQuotaProfile).hasHSQLevelQoSProfileDetail(exclusiveAddOnQoSProfileDetail).build();
			AddOn exclusiveAddOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(),"non-exclusive").exclusiveAddOn()
					.addQoSProfile(exclusiveAddQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(exclusiveAddOn).build();
			
			//Non-Exclusive addOn
			QoSProfile addOnQoSProfile = QosProfileFactory.createQosProfileHasLowerQoSThan(BasePackageFactory.findHigestQoS(basePackage));
			AddOn addOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(),"non exclusive").
					addQoSProfile(addOnQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			
			AddOnSubscription addOnSubscription2 = SubscriptionFactory.createSubscriptionFor(addOn).build();
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(addOnSubscription2.getAddonSubscriptionId(), addOnSubscription2);
			addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = sprBuilder.build();
			SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			Map<String, SubscriberUsage> subscriberUsages = UsageFactory.createUsageFor(basePackage).hasUsageLowerThanHSQForAllServices().build();
			Map<String, SubscriberUsage> addOnUsages = UsageFactory.createUsageFor(exclusiveAddOn).hasUsageExceededForAllServices().build();
			
			Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
			packageUsages.put(basePackage.getId(), subscriberUsages);
			packageUsages.put(addOnSubscription.getAddonSubscriptionId(), addOnUsages);
			
			when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
			when(subscriberRepository.getSubscriptions(Mockito.anyString())).thenReturn(addOnSubscriptions);
			
			ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
			subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
			getLogger().debug("", pcrfResponse.toString());
			
			assertEquals(pcrfResponse.getActivePCCRules(), addOnQoSProfile.getPCCRules());
			assertEquals(pcrfResponse.getInstallablePCCRules(), addOnQoSProfile.getPCCRules());
			assertNull(pcrfResponse.getRemovablePCCRules());
		}
		
		@Test
		public void test_process_should_select_exclusive_addOn_pccRules_when_non_exclusive_addOn_not_applied() throws OperationFailedException {
			
			BasePackage basePackage = BasePackageFactory.createReplacableByAddOnBasePackage(basePackageName);
			
			
			///Exclusive AddOn
			PCCRule exclusiveAddOnFtpPCCRule = PCCRuleFactory.createPCCRuleWithRandomQoS().build();
			PCCRule exclusiveAddOnHttpPCCRule = PCCRuleFactory.createPCCRuleWithRandomQoS().build();
			
			QuotaProfile exclusiveAddOnQuotaProfile = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail exclusiveAddOnQoSProfileDetail = QoSProfileDetailFactory.createQoSProfile().
					hasQuotaProfileDetail(exclusiveAddOnQuotaProfile.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).
					pccRules(Arrays.asList(exclusiveAddOnHttpPCCRule,exclusiveAddOnFtpPCCRule)).build();
			QoSProfile exclusiveAddQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(exclusiveAddOnQuotaProfile).hasHSQLevelQoSProfileDetail(exclusiveAddOnQoSProfileDetail).build();
			AddOn exclusiveAddOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(),"non-exclusive")
					.addQoSProfile(exclusiveAddQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(exclusiveAddOn).build();
			
			//Non-Exclusive addOn
			QoSProfile addOnQoSProfile = QosProfileFactory.createQosProfileHasLowerQoSThan(BasePackageFactory.findHigestQoS(basePackage));
			AddOn addOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(),"exclusive").exclusiveAddOn().
					addQoSProfile(addOnQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			
			AddOnSubscription addOnSubscription2 = SubscriptionFactory.createSubscriptionFor(addOn).build();
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(addOnSubscription2.getAddonSubscriptionId(), addOnSubscription2);
			addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = sprBuilder.build();
			SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			Map<String, SubscriberUsage> subscriberUsages = UsageFactory.createUsageFor(basePackage).hasUsageLowerThanHSQForAllServices().build();
			Map<String, SubscriberUsage> addOnUsages = UsageFactory.createUsageFor(exclusiveAddOn).hasUsageExceededForAllServices().build();
			
			Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
			packageUsages.put(basePackage.getId(), subscriberUsages);
			packageUsages.put(addOnSubscription.getAddonSubscriptionId(), addOnUsages);
			
			when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
			when(subscriberRepository.getSubscriptions(Mockito.anyString())).thenReturn(addOnSubscriptions);
			
			ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
			subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
			getLogger().debug("", pcrfResponse.toString());
			
			assertEquals(pcrfResponse.getActivePCCRules(), addOnQoSProfile.getPCCRules());
			assertEquals(pcrfResponse.getInstallablePCCRules(), addOnQoSProfile.getPCCRules());
			assertNull(pcrfResponse.getRemovablePCCRules());
		}
		
		@Test
		public void test_process_should_select_base_package_pccRules_when_non_exclusive_addOn_and_exclusive_addOn_not_selected() throws OperationFailedException {
			
			QoSProfile basePackageQoSProfile = QosProfileFactory.createSimpleProfile();
			BasePackage basePackage = new BasePackage.BasePackageBuilder(UUID.randomUUID().toString(), basePackageName).
					addQoSProfile(basePackageQoSProfile).replacableByAddOn().build();
			
			//Exclusive
			QuotaProfile quotaProfileDetail = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail quotaProfileDetail = QoSProfileDetailFactory.createQoSProfile().hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).forEachServicesHasPCCRule().build();
			QoSProfile addOnQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfileDetail).hasHSQLevelQoSProfileDetail(quotaProfileDetail).build();
			
			AddOn addOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(), "exclusive").exclusiveAddOn()
					.addQoSProfile(addOnQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
			
			//Non-Exclusive
			QuotaProfile quotaProfile2 = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail quotaProfileDetail2 = QoSProfileDetailFactory.createQoSProfile().hasQuotaProfileDetail(quotaProfile2.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).forEachServicesHasPCCRule().build();
			QoSProfile addOnQoSProfile2 = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfile2).hasHSQLevelQoSProfileDetail(quotaProfileDetail2).build();
			
			AddOn addOn2 = new AddOn.AddOnBuilder(UUID.randomUUID().toString(), "not exclusive")
					.addQoSProfile(addOnQoSProfile2).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			AddOnSubscription addOnSubscription2 = SubscriptionFactory.createSubscriptionFor(addOn2).build();
			
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
			addOnSubscriptions.put(addOnSubscription2.getAddonSubscriptionId(), addOnSubscription2);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = sprBuilder.build();
			SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			Map<String, SubscriberUsage> basePackageUsages = UsageFactory.createUsageFor(basePackage).hasUsageLowerThanHSQForAllServices().build();
			Map<String, SubscriberUsage> addOnUsages = UsageFactory.createUsageFor(addOn).hasUsageExceededForAllServices().build();
			Map<String, SubscriberUsage> addOn2Usages = UsageFactory.createUsageFor(addOn2).hasUsageExceededForAllServices().build();
			
			Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
			packageUsages.put(basePackage.getId(), basePackageUsages);
			packageUsages.put(addOnSubscription.getAddonSubscriptionId(), addOnUsages);
			packageUsages.put(addOnSubscription2.getAddonSubscriptionId(), addOn2Usages);
			
			when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
			when(subscriberRepository.getSubscriptions(Mockito.anyString())).thenReturn(addOnSubscriptions);
			
			ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
			subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
			getLogger().debug("", pcrfResponse.toString());
			
			assertEquals(pcrfResponse.getActivePCCRules(), basePackageQoSProfile.getPCCRules());
			assertEquals(pcrfResponse.getInstallablePCCRules(), basePackageQoSProfile.getPCCRules());
			assertNull(pcrfResponse.getRemovablePCCRules());
			
		}
	}
	
	
	
	
	
	
	@Test
	public void test_process_should_reject_session_when_no_addOn_and_base_package_applied() throws OperationFailedException {
		
		BasePackage basePackage = BasePackageFactory.createReplacableByAddOnBasePackage(basePackageName);
		PolicyRepository dummyPolicyRepository = new DummyPolicyRepository.PolicyRepositoryBuilder().withBasePackage(basePackage).build();
		when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
		
		QuotaProfile quotaProfileDetail = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
		QoSProfileDetail quotaProfileDetail = QoSProfileDetailFactory.createQoSProfile().hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).forEachServicesHasPCCRule().build();
		QoSProfile addOnQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfileDetail).hasHSQLevelQoSProfileDetail(quotaProfileDetail).build();
		
		AddOn addOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(), "non-exclusive")
				.addQoSProfile(addOnQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
		AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
		
		
		QuotaProfile quotaProfile2 = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
		QoSProfileDetail quotaProfileDetail2 = QoSProfileDetailFactory.createQoSProfile().hasQuotaProfileDetail(quotaProfile2.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).forEachServicesHasPCCRule().build();
		QoSProfile addOnQoSProfile2 = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfile2).hasHSQLevelQoSProfileDetail(quotaProfileDetail2).build();
		AddOn addOn2 = new AddOn.AddOnBuilder(UUID.randomUUID().toString(), "non-exclusive").exclusiveAddOn()
		.addQoSProfile(addOnQoSProfile2).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
		AddOnSubscription addOnSubscription2 = SubscriptionFactory.createSubscriptionFor(addOn2).build();
		
		LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
		addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
		addOnSubscriptions.put(addOnSubscription2.getAddonSubscriptionId(), addOnSubscription2);
		
		
		SPRInfo spr = sprBuilder.build();
		SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
		
		PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
		PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
		
		Map<String, SubscriberUsage> basePackageUsages = UsageFactory.createUsageFor(basePackage).hasUsageExceededForAllServices().build();
		Map<String, SubscriberUsage> addOnUsages = UsageFactory.createUsageFor(addOn).hasUsageExceededForAllServices().build();
		Map<String, SubscriberUsage> addOn2Usages = UsageFactory.createUsageFor(addOn2).hasUsageExceededForAllServices().build();
		
		Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
		packageUsages.put(basePackage.getId(), basePackageUsages);
		packageUsages.put(addOnSubscription.getAddonSubscriptionId(), addOnUsages);
		packageUsages.put(addOnSubscription2.getAddonSubscriptionId(), addOn2Usages);
		
		when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
		when(subscriberRepository.getSubscriptions(Mockito.anyString())).thenReturn(addOnSubscriptions);
		
		ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
		subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
		getLogger().debug("", pcrfResponse.toString());
		
		assertNull(pcrfResponse.getQoSInformation().getSessionQoS());
		assertNull(pcrfResponse.getActivePCCRules());
		assertNull(pcrfResponse.getInstallablePCCRules());
		assertNull(pcrfRequest.getRemovedPCCRules());
		assertEquals(PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.val));
	}
	
	
	
	@Test
	public void test_process_should_reject_session_if_no_addOn_applied_and_base_package_is_selected_and_base_package_select_reject_rule() throws OperationFailedException {
		
		String rejectReason = "reject";
		QoSProfileDetail qoSProfileDetail = QoSProfileDetailFactory.createQoSProfile().rejectAction(rejectReason).build();
		
		QoSProfile qosProfile = QosProfileFactory.createQosProfile().hasHSQLevelQoSProfileDetail(qoSProfileDetail).build();
		
		BasePackage basePackage = new BasePackageBuilder(UUID.randomUUID().toString(), basePackageName).replacableByAddOn().addQoSProfile(qosProfile).build();
		
		QuotaProfile quotaProfileDetail = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
		QoSProfileDetail quotaProfileDetail = QoSProfileDetailFactory.createQoSProfile().hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).forEachServicesHasPCCRule().build();
		QoSProfile addOnQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfileDetail).hasHSQLevelQoSProfileDetail(quotaProfileDetail).build();
		
		AddOn addOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(), "non-exclusive")
					.addQoSProfile(addOnQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
		AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
		
		QuotaProfile quotaProfile2 = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
		QoSProfileDetail quotaProfileDetail2 = QoSProfileDetailFactory.createQoSProfile().hasQuotaProfileDetail(quotaProfile2.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).forEachServicesHasPCCRule().build();
		QoSProfile addOnQoSProfile2 = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfile2).hasHSQLevelQoSProfileDetail(quotaProfileDetail2).build();
		AddOn addOn2 = new AddOn.AddOnBuilder(UUID.randomUUID().toString(), "exclusive").exclusiveAddOn()
				.addQoSProfile(addOnQoSProfile2).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
		AddOnSubscription addOnSubscription2 = SubscriptionFactory.createSubscriptionFor(addOn2).build();

	
		LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
		addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
		addOnSubscriptions.put(addOnSubscription2.getAddonSubscriptionId(), addOnSubscription);
		
		PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
		when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
		
		SPRInfo spr = sprBuilder.build();
		SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
		
		PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
		PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
		
		Map<String, SubscriberUsage> basePackageUsages = UsageFactory.createUsageFor(basePackage).hasUsageLowerThanHSQForAllServices().build();
		Map<String, SubscriberUsage> addOnUsages = UsageFactory.createUsageFor(addOn).hasUsageExceededForAllServices().build();
		Map<String, SubscriberUsage> addOnUsages2 = UsageFactory.createUsageFor(addOn2).hasUsageExceededForAllServices().build();
		
		Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
		packageUsages.put(basePackage.getId(), basePackageUsages);
		packageUsages.put(addOnSubscription.getAddonSubscriptionId(), addOnUsages);
		packageUsages.put(addOnSubscription2.getAddonSubscriptionId(), addOnUsages2);
		
		when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
		when(subscriberRepository.getSubscriptions(Mockito.anyString())).thenReturn(addOnSubscriptions);
		
		ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
		subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
		getLogger().debug("", pcrfResponse.toString());
		
		
		assertNull(pcrfResponse.getQoSInformation().getSessionQoS());
		assertNull(pcrfResponse.getActivePCCRules());
		assertNull(pcrfResponse.getInstallablePCCRules());
		assertNull(pcrfRequest.getRemovedPCCRules());
		assertEquals(rejectReason, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.val));
	}
	
	
	
	
	
	

	

*/}
