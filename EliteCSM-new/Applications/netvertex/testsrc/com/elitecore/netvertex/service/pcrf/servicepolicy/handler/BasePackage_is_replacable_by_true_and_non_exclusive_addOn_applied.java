package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import org.junit.Ignore;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
@Ignore
public class BasePackage_is_replacable_by_true_and_non_exclusive_addOn_applied {/*
	

	
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
		public void test_process_should_select_exclusive_addOn_QoS() throws OperationFailedException {
			
			BasePackage basePackage = BasePackageFactory.createReplacableByAddOnBasePackage(basePackageName);
			AddOn addOn = AddOnPackageFactory.createNonExclusiveAddOn("exclusive").hasLowerQosThan(basePackage).build();
			AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = new SPRInfoBuilder().withSubscriberPackage(basePackageName).build();
			SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			Map<String, SubscriberUsage> subscriberUsages = UsageFactory.createUsageFor(basePackage).hasUsageLowerThanHSQForAllServices().build();
			
			Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
			packageUsages.put(basePackage.getId(), subscriberUsages);
			
			when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
			when(subscriberRepository.getSubscriptions(Mockito.anyString())).thenReturn(addOnSubscriptions);
			
			ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
			subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
			
			assertSame(pcrfResponse.getQoSInformation().getSessionQoS(),AddOnPackageFactory.findHigestQoS(addOn));
			getLogger().debug("", pcrfResponse.toString());
		}
		
		@Test
		public void test_process_should_select_best_QoS_from_multiplse_exclusive_addOns_subscription() throws OperationFailedException {
			
			BasePackage basePackage = BasePackageFactory.createReplacableByAddOnBasePackage(basePackageName);
			
			AddOn addOn = AddOnPackageFactory.createNonExclusiveAddOn("exclusive").hasLowerQosThan(basePackage).build();
			AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
			
			AddOn exclusive2 = AddOnPackageFactory.createNonExclusiveAddOn("exclusive2").hasHigherQosThan(addOn).build();
			AddOnSubscription exclusive2Subscription = SubscriptionFactory.createSubscriptionFor(exclusive2).build();
			
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
			addOnSubscriptions.put(exclusive2Subscription.getAddonSubscriptionId(), exclusive2Subscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = new SPRInfoBuilder().withSubscriberPackage(basePackageName).build();
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
			
			assertSame(pcrfResponse.getQoSInformation().getSessionQoS(),AddOnPackageFactory.findHigestQoS(exclusive2));
			getLogger().debug("", pcrfResponse.toString());
		}
		
		@Test
		public void test_process_should_provide_AddOn_Qos_if_non_exclusive_addOn_QoS_selected_even_if_base_package_reject_usage_session() throws OperationFailedException {
			
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
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = new SPRInfoBuilder().withSubscriberPackage(basePackageName).build();
			SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			Map<String, SubscriberUsage> basePackageUsages = UsageFactory.createUsageFor(basePackage).hasUsageLowerThanHSQForAllServices().build();
			Map<String, SubscriberUsage> addOnUsages = UsageFactory.createUsageFor(addOn).hasUsageLowerThanHSQForAllServices().build();
			
			Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
			packageUsages.put(basePackage.getId(), basePackageUsages);
			packageUsages.put(addOnSubscription.getAddonSubscriptionId(), addOnUsages);
			
			when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
			when(subscriberRepository.getSubscriptions(Mockito.anyString())).thenReturn(addOnSubscriptions);
			
			ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
			subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
			getLogger().debug("", pcrfResponse.toString());
			
			
			assertSame(pcrfResponse.getQoSInformation().getSessionQoS(),AddOnPackageFactory.findHigestQoS(addOn));
		}
		
		@Test
		public void test_process_should_select_base_package_QoS_when_exclusive_addOn_QoS_not_selected() throws OperationFailedException {
			
			BasePackage basePackage = BasePackageFactory.createReplacableByAddOnBasePackage(basePackageName);
			
			QuotaProfile quotaProfileDetail = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail quotaProfileDetail = QoSProfileDetailFactory.createQoSProfile().hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).forEachServicesHasPCCRule().build();
			QoSProfile addOnQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfileDetail).hasHSQLevelQoSProfileDetail(quotaProfileDetail).build();
			
			AddOn addOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(), "non-exclusive")
			.addQoSProfile(addOnQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = new SPRInfoBuilder().withSubscriberPackage(basePackageName).build();
			SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			Map<String, SubscriberUsage> basePackageUsages = UsageFactory.createUsageFor(basePackage).hasUsageLowerThanHSQForAllServices().build();
			Map<String, SubscriberUsage> addOnUsages = UsageFactory.createUsageFor(addOn).hasUsageExceededForAllServices().build();
			
			Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
			packageUsages.put(basePackage.getId(), basePackageUsages);
			packageUsages.put(addOnSubscription.getAddonSubscriptionId(), addOnUsages);
			
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
		public void test_process_should_select_pccRules_that_have_best_QoS_from_multiplse_exclusive_addOns_subscription() throws OperationFailedException {
		
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
			AddOn nonExclusiveAdd2 = new AddOn.AddOnBuilder(UUID.randomUUID().toString(),"non exclusive2").
					addQoSProfile(nonExclusiveAdd2QoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			
			
			AddOnSubscription nonExclusiveAddOnSubscription = SubscriptionFactory.createSubscriptionFor(nonExclusiveAddOn).build();
			AddOnSubscription nonExclusiveAddOn2addOnSubscription = SubscriptionFactory.createSubscriptionFor(nonExclusiveAdd2).build();
			
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(nonExclusiveAddOnSubscription.getAddonSubscriptionId(), nonExclusiveAddOnSubscription);
			addOnSubscriptions.put(nonExclusiveAddOn2addOnSubscription.getAddonSubscriptionId(), nonExclusiveAddOn2addOnSubscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = new SPRInfoBuilder().withSubscriberPackage(basePackageName).build();
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
		public void test_process_should_select_pccRules_that_subscription_which_expired_earliest_if_both_subscription_has_same_pccRule_qos() throws OperationFailedException {
		
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
			AddOn exclusiveAddOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(),"non-exclusive")
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
			
			SPRInfo spr = new SPRInfoBuilder().withSubscriberPackage(basePackageName).build();
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
		public void test_process_should_select_not_exclusive_addOn_pccRules() throws OperationFailedException {
			
			BasePackage basePackage = BasePackageFactory.createReplacableByAddOnBasePackage(basePackageName);
			QoSProfile addOnQoSProfile = QosProfileFactory.createQosProfileHasLowerQoSThan(BasePackageFactory.findHigestQoS(basePackage));
			AddOn addOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(),"not exclusive").
					addQoSProfile(addOnQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			
			AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = new SPRInfoBuilder().withSubscriberPackage(basePackageName).build();
			SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			Map<String, SubscriberUsage> subscriberUsages = UsageFactory.createUsageFor(basePackage).hasUsageLowerThanHSQForAllServices().build();
			
			Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
			packageUsages.put(basePackage.getId(), subscriberUsages);
			
			when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
			when(subscriberRepository.getSubscriptions(Mockito.anyString())).thenReturn(addOnSubscriptions);
			
			ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
			subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
			
			assertEquals(pcrfResponse.getActivePCCRules(), addOnQoSProfile.getPCCRules());
			assertEquals(pcrfResponse.getInstallablePCCRules(), addOnQoSProfile.getPCCRules());
			assertNull(pcrfResponse.getRemovablePCCRules());
			getLogger().debug("", pcrfResponse.toString());
		}
		
		@Test
		public void test_process_should_select_base_package_pccRules_when_not_exclusive_addOn_not_selected() throws OperationFailedException {
			
			QoSProfile basePackageQoSProfile = QosProfileFactory.createSimpleProfile();
			BasePackage basePackage = new BasePackage.BasePackageBuilder(UUID.randomUUID().toString(), basePackageName).
					addQoSProfile(basePackageQoSProfile).replacableByAddOn().build();
			
			QuotaProfile quotaProfileDetail = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail quotaProfileDetail = QoSProfileDetailFactory.createQoSProfile().hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).forEachServicesHasPCCRule().build();
			QoSProfile addOnQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfileDetail).hasHSQLevelQoSProfileDetail(quotaProfileDetail).build();
			
			AddOn addOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(), "not exclusive")
					.addQoSProfile(addOnQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = new SPRInfoBuilder().withSubscriberPackage(basePackageName).build();
			SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			Map<String, SubscriberUsage> basePackageUsages = UsageFactory.createUsageFor(basePackage).hasUsageLowerThanHSQForAllServices().build();
			Map<String, SubscriberUsage> addOnUsages = UsageFactory.createUsageFor(addOn).hasUsageExceededForAllServices().build();
			
			Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
			packageUsages.put(basePackage.getId(), basePackageUsages);
			packageUsages.put(addOnSubscription.getAddonSubscriptionId(), addOnUsages);
			
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
	public void test_process_should_reject_session_when_no_addOn_and_base_package_statisfied() throws OperationFailedException {
		
		BasePackage basePackage = BasePackageFactory.createReplacableByAddOnBasePackage(basePackageName);
		PolicyRepository dummyPolicyRepository = new DummyPolicyRepository.PolicyRepositoryBuilder().withBasePackage(basePackage).build();
		when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
		
		QuotaProfile quotaProfileDetail = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
		QoSProfileDetail quotaProfileDetail = QoSProfileDetailFactory.createQoSProfile().hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).forEachServicesHasPCCRule().build();
		QoSProfile addOnQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfileDetail).hasHSQLevelQoSProfileDetail(quotaProfileDetail).build();
		
		AddOn addOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(), "non-exclusive")
				.addQoSProfile(addOnQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
		AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
		LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
		addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
		
		
		SPRInfo spr = new SPRInfoBuilder().withSubscriberPackage(basePackageName).build();
		SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
		
		PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
		PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
		
		Map<String, SubscriberUsage> basePackageUsages = UsageFactory.createUsageFor(basePackage).hasUsageExceededForAllServices().build();
		Map<String, SubscriberUsage> addOnUsages = UsageFactory.createUsageFor(addOn).hasUsageExceededForAllServices().build();
		
		Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
		packageUsages.put(basePackage.getId(), basePackageUsages);
		packageUsages.put(addOnSubscription.getAddonSubscriptionId(), addOnUsages);
		
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
	public void test_process_should_reject_session_if_not_exclusive_addOn_QoS_not_selected_and_base_package_is_selected_and_base_package_select_reject_rule() throws OperationFailedException {
		
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
		LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
		addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
		
		PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
		when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
		
		SPRInfo spr = new SPRInfoBuilder().withSubscriberPackage(basePackageName).build();
		SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
		
		PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
		PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
		
		Map<String, SubscriberUsage> basePackageUsages = UsageFactory.createUsageFor(basePackage).hasUsageLowerThanHSQForAllServices().build();
		Map<String, SubscriberUsage> addOnUsages = UsageFactory.createUsageFor(addOn).hasUsageExceededForAllServices().build();
		
		Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
		packageUsages.put(basePackage.getId(), basePackageUsages);
		packageUsages.put(addOnSubscription.getAddonSubscriptionId(), addOnUsages);
		
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
