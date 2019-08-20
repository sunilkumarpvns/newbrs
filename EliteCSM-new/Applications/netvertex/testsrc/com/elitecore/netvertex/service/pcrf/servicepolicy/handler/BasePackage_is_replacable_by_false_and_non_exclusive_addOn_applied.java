package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import org.junit.Ignore;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
@Ignore
public class BasePackage_is_replacable_by_false_and_non_exclusive_addOn_applied {/*
	
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

	public class BestQoS {
		
		
		@Test
		public void test_process_should_select_best_qos_from_non_exclusive_addOn_subscription_QoS_whose_qos_is_higher_than_base_package_qos_and_other_subscription() throws OperationFailedException {
			
			String methodName = "test_process_should_select_best_qos_from_non_exclusive_addOn_subscription_QoS_whose_qos_is_higher_than_base_package_qos_and_other_subscription";
			logExecutionStart(methodName);
			BasePackage basePackage = BasePackageFactory.createNotReplacableByAddOnBasePackage(basePackageName);
			AddOn nonExclusiveAddOn = AddOnPackageFactory.createNonExclusiveAddOn("non exclusive").hasHigherQosThan(basePackage).build();
			AddOnSubscription nonExclusiveSubscription = SubscriptionFactory.createSubscriptionFor(nonExclusiveAddOn).build();
			
			AddOn nonExclusive2 = AddOnPackageFactory.createNonExclusiveAddOn("non exclusive2").hasLowerQosThan(nonExclusiveAddOn).build();
			AddOnSubscription nonExclusive2Subscription = SubscriptionFactory.createSubscriptionFor(nonExclusive2).build();
			
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(nonExclusiveSubscription.getAddonSubscriptionId(), nonExclusiveSubscription);
			addOnSubscriptions.put(nonExclusive2Subscription.getAddonSubscriptionId(), nonExclusive2Subscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = sprBuilder.build();
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
			getLogger().debug("", pcrfResponse.toString());
			logExecutionStop(methodName);
			
			logValidationStart(methodName);
			assertSame(pcrfResponse.getQoSInformation().getSessionQoS(),AddOnPackageFactory.findHigestQoS(nonExclusiveAddOn));
			logValidationStop(methodName);
		}
		
		@Test
		public void test_process_should_select_best_qos_from_non_exclusive_addOn_subscription_QoS_whose_qos_is_equal_to_base_package_qos_but_higher_than_other_subscription() throws OperationFailedException {
			
			String methodName = "test_process_should_select_best_qos_from_non_exclusive_addOn_subscription_QoS_whose_qos_is_equal_to_base_package_qos_but_higher_than_other_subscription";
			logExecutionStart(methodName);
			
			BasePackage basePackage = BasePackageFactory.createNotReplacableByAddOnBasePackage(basePackageName);
			
			AddOn nonExclusive = AddOnPackageFactory.createNonExclusiveAddOn("non exclusive").hasEqualQosThan(basePackage).build();
			AddOnSubscription nonExclusiveSubscription = SubscriptionFactory.createSubscriptionFor(nonExclusive).build();
			
			AddOn nonExclusive2 = AddOnPackageFactory.createNonExclusiveAddOn("non exclusive2").hasLowerQosThan(nonExclusive).build();
			AddOnSubscription nonExclusive2Subscription = SubscriptionFactory.createSubscriptionFor(nonExclusive2).build();
			
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(nonExclusiveSubscription.getAddonSubscriptionId(), nonExclusiveSubscription);
			addOnSubscriptions.put(nonExclusive2Subscription.getAddonSubscriptionId(), nonExclusive2Subscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = sprBuilder.build();
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
			getLogger().debug("", pcrfResponse.toString());
			logExecutionStop(methodName);
			
			logValidationStart(methodName);
			assertSame(pcrfResponse.getQoSInformation().getSessionQoS(),AddOnPackageFactory.findHigestQoS(nonExclusive));
			logValidationStop(methodName);
		}
		
		@Test
		public void test_process_should_select_qos_from_subscription_which_expired_earliest_if_both_subscription_has_same_qos_but_have_higher_qos_than_base_package() throws OperationFailedException {
			
			String methodName = "test_process_should_select_qos_from_subscription_which_expired_earliest_if_both_subscription_has_same_qos_but_have_higher_qos_than_base_package";
			logExecutionStart(methodName);
			BasePackage basePackage = BasePackageFactory.createNotReplacableByAddOnBasePackage(basePackageName);
			
			AddOn nonExclusive = AddOnPackageFactory.createNonExclusiveAddOn("non exclusive").hasEqualQosThan(basePackage).build();
			
			AddOn nonExclusive2 = AddOnPackageFactory.createNonExclusiveAddOn("non exclusive2").hasEqualQosThan(nonExclusive).build();
			AddOnSubscription nonExclusive2Subscription = SubscriptionFactory.createSubscriptionFor(nonExclusive2).build();
			AddOnSubscription nonExclusiveSubscription = SubscriptionFactory.createSubscriptionFor(nonExclusive).build();
			
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(nonExclusiveSubscription.getAddonSubscriptionId(), nonExclusiveSubscription);
			addOnSubscriptions.put(nonExclusive2Subscription.getAddonSubscriptionId(), nonExclusive2Subscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = sprBuilder.build();
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
			getLogger().debug("", pcrfResponse.toString());
			logExecutionStop(methodName);
			
			logValidationStart(methodName);
			assertSame(pcrfResponse.getQoSInformation().getSessionQoS(),AddOnPackageFactory.findHigestQoS(nonExclusive2));
			logValidationStop(methodName);
			
		}
		
		@Test
		public void test_process_should_select_best_qos_from_base_package_whose_qos_is_higher_than_subscription_qos() throws OperationFailedException {
			
			String methodName = "test_process_should_select_best_qos_from_base_package_whose_qos_is_higher_than_subscription_qos";
			logExecutionStart(methodName);
			
			BasePackage basePackage = BasePackageFactory.createNotReplacableByAddOnBasePackage(basePackageName);
			
			AddOn nonExclusive = AddOnPackageFactory.createNonExclusiveAddOn("non exclusive").hasLowerQosThan(basePackage).build();
			AddOnSubscription nonExclusiveSubscription = SubscriptionFactory.createSubscriptionFor(nonExclusive).build();
			
			AddOn nonExclusive2 = AddOnPackageFactory.createNonExclusiveAddOn("non exclusive2").hasLowerQosThan(nonExclusive).build();
			AddOnSubscription nonExclusive2Subscription = SubscriptionFactory.createSubscriptionFor(nonExclusive2).build();
			
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(nonExclusiveSubscription.getAddonSubscriptionId(), nonExclusiveSubscription);
			addOnSubscriptions.put(nonExclusive2Subscription.getAddonSubscriptionId(), nonExclusive2Subscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = sprBuilder.build();
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
			getLogger().debug("", pcrfResponse.toString());
			
			logExecutionStop(methodName);
			
			logValidationStart(methodName);
			assertSame(pcrfResponse.getQoSInformation().getSessionQoS(),BasePackageFactory.findHigestQoS(basePackage));
			logValidationStop(methodName);
		}
		
		@Test
		public void test_process_should_select_best_QoS_from_multiplse_non_exclusive_addOns_subscription_when_base_package_qos_has_not_applied() throws OperationFailedException {
		
			String methodName = "test_process_should_select_best_QoS_from_multiplse_non_exclusive_addOns_subscription_when_base_package_qos_has_not_applied_than_subscriptions_qos";
			
			logExecutionStart(methodName);
			BasePackage basePackage = BasePackageFactory.createNotReplacableByAddOnBasePackage(basePackageName);
			
			AddOn addOn = AddOnPackageFactory.createNonExclusiveAddOn("non exclusive").hasLowerQosThan(basePackage).build();
			AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
			
			AddOn exclusive2 = AddOnPackageFactory.createNonExclusiveAddOn("non exclusive2").hasHigherQosThan(addOn).build();
			AddOnSubscription exclusive2Subscription = SubscriptionFactory.createSubscriptionFor(exclusive2).build();
			
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(exclusive2Subscription.getAddonSubscriptionId(), exclusive2Subscription);
			addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
			
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
			logExecutionStop(methodName);
			
			logValidationStart(methodName);
			
			assertReflectionEquals(AddOnPackageFactory.findHigestQoS(exclusive2), 
					pcrfResponse.getQoSInformation().getSessionQoS(), ReflectionComparatorMode.LENIENT_ORDER);
			logValidationStop(methodName);
		}
		
		@Test
		public void test_process_should_select_base_package_QoS_when_non_exclusive_addOn_QoS_not_selected() throws OperationFailedException {
			
			String methodName = "test_process_should_select_base_package_QoS_when_non_exclusive_addOn_QoS_not_selected";
			logExecutionStart(methodName);
			
			BasePackage basePackage = BasePackageFactory.createNotReplacableByAddOnBasePackage(basePackageName);
			
			QuotaProfile quotaProfileDetail = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail quotaProfileDetail = QoSProfileDetailFactory.createQoSProfile().hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).forEachServicesHasPCCRule().build();
			QoSProfile addOnQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfileDetail).hasHSQLevelQoSProfileDetail(quotaProfileDetail).build();
			
			AddOn addOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(), "exclusive").addQoSProfile(addOnQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = sprBuilder.build();
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
			
			logExecutionStop(methodName);
			
			logValidationStart(methodName);
			assertSame(pcrfResponse.getQoSInformation().getSessionQoS(),BasePackageFactory.findHigestQoS(basePackage));
			logValidationStop(methodName);
		}
	}
	
	
	
	
	public class PCCRuleSelection {
		@Test
		public void test_process_should_select_pccRules_that_have_best_session_QoS_from_multiplse_non_exclusive_addOns_subscription_and_base() throws OperationFailedException {
			
			String methodName = "test_process_should_select_pccRules_that_have_best_session_QoS_from_multiplse_non_exclusive_addOns_subscription_and_base";
			logExecutionStart(methodName);
		
			RatingGroup ratingGroup = new RatingGroup("1", "defaultRatingGroup", "", 1);
			ServiceDataFlow serviceDataFlow = new ServiceDataFlow("allow", "tcp", "any", "0", "any", "0");
			ServiceType httpService = new ServiceType("2", "http", 2, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
			ServiceType ftpService = new ServiceType("3", "ftp", 3, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
			ServiceType p2pService = new ServiceType("4", "ftp", 4, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
			ServiceType telnetService = new ServiceType("5", "ftp", 5, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
			
			///Base Package
			PCCRule ftpPCCRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(ftpService).build();
			PCCRule httpPCCRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(httpService).build();
			PCCRule p2pPCCRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(p2pService).build();
			PCCRule telnetPCCRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(telnetService).build();
			
			QuotaProfile quotaProfileDetail = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail qosProfileDetail = QoSProfileDetailFactory.createQoSProfile().
												hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).
												pccRules(Arrays.asList(httpPCCRule,ftpPCCRule,p2pPCCRule)).build();
			QoSProfile qosProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfileDetail).hasHSQLevelQoSProfileDetail(qosProfileDetail).build();
			
			BasePackage basePackage = new BasePackage.BasePackageBuilder(UUID.randomUUID().toString(),basePackageName).addQoSProfile(qosProfile).build();
			
			
			///Exclusive AddOn
			PCCRule exclusiveAddOnFtpPCCRule = PCCRuleFactory.createPCCRuleHasLowerQoSThan(ftpPCCRule);
			PCCRule exclusiveAddOnHttpPCCRule = PCCRuleFactory.createPCCRuleHasHigherQoSThan(httpPCCRule);
			PCCRule exclusiveAddOnP2PCCRule = PCCRuleFactory.createPCCRuleHasEqualQoSTo(p2pPCCRule);
			PCCRule exclusiveAddOnTelnetCCRule = PCCRuleFactory.createPCCRuleHasEqualQoSTo(telnetPCCRule);
			
			QuotaProfile exclusiveAddOnQuotaProfile = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail exclusiveAddOnQoSProfileDetail = QoSProfileDetailFactory.createQoSProfile().
					hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).
					pccRules(Arrays.asList(exclusiveAddOnHttpPCCRule,exclusiveAddOnFtpPCCRule,exclusiveAddOnP2PCCRule,exclusiveAddOnTelnetCCRule)).build();
			QoSProfile exclusiveAddQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(exclusiveAddOnQuotaProfile).hasHSQLevelQoSProfileDetail(exclusiveAddOnQoSProfileDetail).build();
			AddOn exclusiveAddOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(),"non exclusive").
					addQoSProfile(exclusiveAddQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			
			
			///Exclusive AddOn 2
			PCCRule exclusiveAddOn2FtpPCCRule = PCCRuleFactory.createPCCRuleHasLowerQoSThan(exclusiveAddOnFtpPCCRule);
			PCCRule exclusiveAddOn2HttpPCCRule = PCCRuleFactory.createPCCRuleHasLowerQoSThan(exclusiveAddOnHttpPCCRule);
			PCCRule exclusiveAddOn2P2PPCCRule = PCCRuleFactory.createPCCRuleHasHigherQoSThan(exclusiveAddOnP2PCCRule);
			
			
			QuotaProfile exclusiveAdd2OnQuotaProfile = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail exclusiveAdd2OnQoSProfileDetail = QoSProfileDetailFactory.createQoSProfile().
					hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).
					pccRules(Arrays.asList(exclusiveAddOn2FtpPCCRule,exclusiveAddOn2HttpPCCRule,exclusiveAddOn2P2PPCCRule)).withSessionQoSLowerThan(exclusiveAddOnQoSProfileDetail.getSessionQoS()).build();
			QoSProfile exclusiveAdd2QoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(exclusiveAdd2OnQuotaProfile).hasHSQLevelQoSProfileDetail(exclusiveAdd2OnQoSProfileDetail).build();
			AddOn exclusiveAdd2 = new AddOn.AddOnBuilder(UUID.randomUUID().toString(),"non exclusive2").
					addQoSProfile(exclusiveAdd2QoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			
			
			AddOnSubscription exclusiveAddOnSubscription = SubscriptionFactory.createSubscriptionFor(exclusiveAddOn).build();
			AddOnSubscription exclusiveAddOn2addOnSubscription = SubscriptionFactory.createSubscriptionFor(exclusiveAdd2).build();
			
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(exclusiveAddOnSubscription.getAddonSubscriptionId(), exclusiveAddOnSubscription);
			addOnSubscriptions.put(exclusiveAddOn2addOnSubscription.getAddonSubscriptionId(), exclusiveAddOn2addOnSubscription);
			
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
			packageUsages.put(exclusiveAddOnSubscription.getAddonSubscriptionId(), exclusiveAddOnSubscriberUsages);
			packageUsages.put(exclusiveAddOn2addOnSubscription.getAddonSubscriptionId(), exclusiveAdd2OnSubscriberUsages);
			
			when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
			when(subscriberRepository.getSubscriptions(Mockito.anyString())).thenReturn(addOnSubscriptions);
			
			ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
			subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
			getLogger().debug("", pcrfResponse.toString());
			
			logExecutionStop(methodName);
			
			logValidationStart(methodName);
			List<PCCRule> expectedPCCRules = Arrays.asList(ftpPCCRule, exclusiveAddOnHttpPCCRule, exclusiveAddOn2P2PPCCRule,exclusiveAddOnTelnetCCRule);
			
			assertReflectionEquals(pcrfResponse.getActivePCCRules(), expectedPCCRules, ReflectionComparatorMode.LENIENT_ORDER);
			assertReflectionEquals(pcrfResponse.getInstallablePCCRules(), expectedPCCRules, ReflectionComparatorMode.LENIENT_ORDER);
			assertNull(pcrfResponse.getRemovablePCCRules());
			logValidationStop(methodName);
		}
		
		@Test
		public void test_process_should_select_pccRule_from_subscription_which_expired_earliest_if_both_subscription_has_same_pccRule_qos_but_have_higher_pccRule_qos_than_base_package() throws OperationFailedException {
		
			String methodName = "test_process_should_select_pccRule_from_subscription_which_expired_earliest_if_both_subscription_has_same_pccRule_qos_but_have_higher_pccRule_qos_than_base_package";
			
			logExecutionStart(methodName);
			
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
			
			BasePackage basePackage = new BasePackage.BasePackageBuilder(UUID.randomUUID().toString(),basePackageName).addQoSProfile(qosProfile).build();
			
			
			///Exclusive AddOn
			PCCRule nonExclusiveAddOnFtpPCCRule = PCCRuleFactory.createPCCRuleHasHigherQoSThan(ftpPCCRule);
			PCCRule nonExclusiveAddOnHttpPCCRule = PCCRuleFactory.createPCCRuleHasHigherQoSThan(httpPCCRule);
			
			QuotaProfile exclusiveAddOnQuotaProfile = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail exclusiveAddOnQoSProfileDetail = QoSProfileDetailFactory.createQoSProfile()
					.hasQuotaProfileDetail(exclusiveAddOnQuotaProfile.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).
					pccRules(Arrays.asList(nonExclusiveAddOnHttpPCCRule,nonExclusiveAddOnFtpPCCRule)).build();
			QoSProfile exclusiveAddQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(exclusiveAddOnQuotaProfile).hasHSQLevelQoSProfileDetail(exclusiveAddOnQoSProfileDetail).build();
			AddOn nonexclusiveAddOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(),"non exclusive").
					addQoSProfile(exclusiveAddQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			
			
			///Exclusive AddOn 2
			PCCRule nonExclusiveAddOn2FtpPCCRule = PCCRuleFactory.createPCCRuleHasEqualQoSTo(nonExclusiveAddOnFtpPCCRule);
			PCCRule nonExclusiveAddOn2HttpPCCRule = PCCRuleFactory.createPCCRuleHasEqualQoSTo(nonExclusiveAddOnHttpPCCRule);
			
			QuotaProfile nonExclusiveAdd2OnQuotaProfile = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail nonExclusiveAdd2OnQoSProfileDetail = QoSProfileDetailFactory.createQoSProfile().
					hasQuotaProfileDetail(nonExclusiveAdd2OnQuotaProfile.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).
					pccRules(Arrays.asList(nonExclusiveAddOn2FtpPCCRule,nonExclusiveAddOn2HttpPCCRule)).withSessionQoSEqualTo(exclusiveAddOnQoSProfileDetail.getSessionQoS()).build();
			QoSProfile notExclusiveAdd2QoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(nonExclusiveAdd2OnQuotaProfile).hasHSQLevelQoSProfileDetail(nonExclusiveAdd2OnQoSProfileDetail).build();
			AddOn notExclusiveAddOn2 = new AddOn.AddOnBuilder(UUID.randomUUID().toString(),"non exclusive2").
					addQoSProfile(notExclusiveAdd2QoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			
			
			AddOnSubscription nonExclusiveAddOn2Subscription = SubscriptionFactory.createSubscriptionFor(notExclusiveAddOn2).build();
			AddOnSubscription nonExclusiveAddOnSubscription = SubscriptionFactory.createSubscriptionFor(nonexclusiveAddOn).build();
			
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(nonExclusiveAddOnSubscription.getAddonSubscriptionId(), nonExclusiveAddOnSubscription);
			addOnSubscriptions.put(nonExclusiveAddOn2Subscription.getAddonSubscriptionId(), nonExclusiveAddOn2Subscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = sprBuilder.build();
			SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			Map<String, SubscriberUsage> subscriberUsages = UsageFactory.createUsageFor(basePackage).hasUsageLowerThanHSQForAllServices().build();
			Map<String, SubscriberUsage> nonExclusiveAddOnSubscriberUsages = UsageFactory.createUsageFor(nonexclusiveAddOn).hasUsageLowerThanHSQForAllServices().build();
			Map<String, SubscriberUsage> nonExclusiveAdd2OnSubscriberUsages = UsageFactory.createUsageFor(nonexclusiveAddOn).hasUsageLowerThanHSQForAllServices().build();
			
			Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
			packageUsages.put(basePackage.getId(), subscriberUsages);
			packageUsages.put(nonExclusiveAddOn2Subscription.getAddonSubscriptionId(), nonExclusiveAdd2OnSubscriberUsages);
			packageUsages.put(nonExclusiveAddOnSubscription.getAddonSubscriptionId(), nonExclusiveAddOnSubscriberUsages);
			
			when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
			when(subscriberRepository.getSubscriptions(Mockito.anyString())).thenReturn(addOnSubscriptions);
			
			ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
			subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
			
			List<PCCRule> expectedPCCRules = Arrays.asList(nonExclusiveAddOn2HttpPCCRule, nonExclusiveAddOn2FtpPCCRule);
			
			getLogger().debug("", pcrfResponse.toString());
			logExecutionStop(methodName);
			
			logValidationStart(methodName);
			assertReflectionEquals(expectedPCCRules, pcrfResponse.getActivePCCRules(), ReflectionComparatorMode.LENIENT_ORDER);
			assertReflectionEquals(expectedPCCRules, pcrfResponse.getInstallablePCCRules(), ReflectionComparatorMode.LENIENT_ORDER);
			assertNull(pcrfResponse.getRemovablePCCRules());
			logValidationStop(methodName);
		}
		
		@Test
		public void test_process_should_select_non_exclusive_non_addOn_pccRules_if_non_exclusive_addOn_has_equal_pccrule_qos_than_basepackage_pccrule() throws OperationFailedException {
			
			String methodName = "executing: test_process_should_select_non_exclusive_addOn_pccRules_if_non_exclusive_addOn_has_equal_pccrule_qos_than_basepackage_pccrule";
			logExecutionStart(methodName);
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
			
			BasePackage basePackage = new BasePackage.BasePackageBuilder(UUID.randomUUID().toString(),basePackageName).addQoSProfile(qosProfile).build();
			
			
			///Exclusive AddOn
			PCCRule nonExclusiveAddOnFtpPCCRule = PCCRuleFactory.createPCCRuleHasHigherQoSThan(ftpPCCRule);
			PCCRule nonExclusiveAddOnHttpPCCRule = PCCRuleFactory.createPCCRuleHasHigherQoSThan(httpPCCRule);
			
			QuotaProfile exclusiveAddOnQuotaProfile = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail exclusiveAddOnQoSProfileDetail = QoSProfileDetailFactory.createQoSProfile()
					.hasQuotaProfileDetail(exclusiveAddOnQuotaProfile.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).
					pccRules(Arrays.asList(nonExclusiveAddOnHttpPCCRule,nonExclusiveAddOnFtpPCCRule)).build();
			QoSProfile nonExclusiveAddQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(exclusiveAddOnQuotaProfile).hasHSQLevelQoSProfileDetail(exclusiveAddOnQoSProfileDetail).build();
			AddOn nonexclusiveAddOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(),"non exclusive").
					addQoSProfile(nonExclusiveAddQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			
			AddOnSubscription nonExclusiveAddOnSubscription = SubscriptionFactory.createSubscriptionFor(nonexclusiveAddOn).build();
			
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(nonExclusiveAddOnSubscription.getAddonSubscriptionId(), nonExclusiveAddOnSubscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = sprBuilder.build();
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
			
			getLogger().debug("", pcrfResponse.toString());
			logExecutionStop(methodName);
			
			logValidationStart(methodName);
			List<PCCRule> expectedPCCRules = Arrays.asList(nonExclusiveAddOnHttpPCCRule, nonExclusiveAddOnFtpPCCRule);
			assertReflectionEquals(expectedPCCRules, pcrfResponse.getActivePCCRules(),ReflectionComparatorMode.LENIENT_ORDER);
			assertReflectionEquals(expectedPCCRules, pcrfResponse.getInstallablePCCRules(), ReflectionComparatorMode.LENIENT_ORDER);
			assertNull(pcrfResponse.getRemovablePCCRules());
			logValidationStop(methodName);
			
		}
		
		@Test
		public void test_process_should_select_base_package_pccRules_when_non_exclusive_addOn_QoS_not_selected() throws OperationFailedException {
			
			String methodName = "test_process_should_select_base_package_pccRules_when_non_exclusive_addOn_QoS_not_selected";
			logExecutionStart(methodName);
			
			QoSProfile basePackageQoSProfile = QosProfileFactory.createSimpleProfile();
			BasePackage basePackage = new BasePackage.BasePackageBuilder(UUID.randomUUID().toString(), basePackageName).
					addQoSProfile(basePackageQoSProfile).build();
			
			QuotaProfile quotaProfileDetail = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
			QoSProfileDetail quotaProfileDetail = QoSProfileDetailFactory.createQoSProfile().hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).forEachServicesHasPCCRule().build();
			QoSProfile addOnQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfileDetail).hasHSQLevelQoSProfileDetail(quotaProfileDetail).build();
			
			AddOn addOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(), "exclusive")
						.addQoSProfile(addOnQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
			AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
			LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
			addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
			
			PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
			when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
			
			SPRInfo spr = sprBuilder.build();
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

			logExecutionStop(methodName);
			
			logValidationStart(methodName);
			assertEquals(pcrfResponse.getActivePCCRules(), basePackageQoSProfile.getPCCRules());
			assertEquals(pcrfResponse.getInstallablePCCRules(), basePackageQoSProfile.getPCCRules());
			assertNull(pcrfResponse.getRemovablePCCRules());
			logValidationStop(methodName);
			
		}
		
		
	}
	
	@Test
	public void test_process_should_reject_session_when_no_policy_statisfied() throws OperationFailedException {

		String methodName = "test_process_should_reject_session_when_no_policy_statisfied";
		
		logExecutionStart(methodName);
		BasePackage basePackage = BasePackageFactory.createNotReplacableByAddOnBasePackage(basePackageName);
		PolicyRepository dummyPolicyRepository = new DummyPolicyRepository.PolicyRepositoryBuilder().withBasePackage(basePackage).build();
		when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
		
		QuotaProfile quotaProfileDetail = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
		QoSProfileDetail quotaProfileDetail = QoSProfileDetailFactory.createQoSProfile().hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).forEachServicesHasPCCRule().build();
		QoSProfile addOnQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfileDetail).hasHSQLevelQoSProfileDetail(quotaProfileDetail).build();
		
		AddOn addOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(), "exclusive").addQoSProfile(addOnQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
		AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
		LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
		addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
		
		
		SPRInfo spr = sprBuilder.build();
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

		logExecutionStop(methodName);
		
		logValidationStart(methodName);
		assertNull(pcrfResponse.getQoSInformation().getSessionQoS());
		assertNull(pcrfResponse.getActivePCCRules());
		assertNull(pcrfResponse.getInstallablePCCRules());
		assertNull(pcrfRequest.getRemovedPCCRules());
		assertEquals(PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.val));
		logValidationStop(methodName);
	}
	
	@Test
	public void test_process_should_provide_reject_session_even_if_base_package_reject_usage_session_even_though_non_exclusive_addOn_QoS_selected() throws OperationFailedException {
		
		
		String methodName = "test_process_should_provide_reject_session_even_if_base_package_reject_usage_session_even_though_non_exclusive_addOn_QoS_selected";
		
		logExecutionStart(methodName);
		String rejectReason = "reject";
		QoSProfileDetail qoSProfileDetail = QoSProfileDetailFactory.createQoSProfile().rejectAction(rejectReason).build();
		
		QoSProfile qosProfile = QosProfileFactory.createQosProfile().hasHSQLevelQoSProfileDetail(qoSProfileDetail).build();
		
		BasePackage basePackage = new BasePackageBuilder(UUID.randomUUID().toString(), basePackageName).addQoSProfile(qosProfile).build();
		
		QuotaProfile quotaProfileDetail = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
		QoSProfileDetail quotaProfileDetail = QoSProfileDetailFactory.createQoSProfile().hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).forEachServicesHasPCCRule().build();
		QoSProfile addOnQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfileDetail).hasHSQLevelQoSProfileDetail(quotaProfileDetail).build();
		
		AddOn addOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(), "exclusive").addQoSProfile(addOnQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
		AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
		LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
		addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
		
		PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
		when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
		
		SPRInfo spr = sprBuilder.build();
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
		
		logExecutionStop(methodName);
		
		logValidationStart(methodName);
		assertNull(pcrfResponse.getQoSInformation().getSessionQoS());
		assertNull(pcrfResponse.getActivePCCRules());
		assertNull(pcrfResponse.getInstallablePCCRules());
		assertNull(pcrfRequest.getRemovedPCCRules());
		assertEquals(rejectReason, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.val));
		logValidationStop(methodName);
		
	}
	
	@Test
	public void test_process_should_reject_session_if_exclusive_addOn_QoS_not_selected_and_base_package_is_selected() throws OperationFailedException {
		
		String methodName = "test_process_should_reject_session_if_exclusive_addOn_QoS_not_selected_and_base_package_is_selected";
		
		logExecutionStart(methodName);
		String rejectReason = "reject";
		QoSProfileDetail qoSProfileDetail = QoSProfileDetailFactory.createQoSProfile().rejectAction(rejectReason).build();
		
		QoSProfile qosProfile = QosProfileFactory.createQosProfile().hasHSQLevelQoSProfileDetail(qoSProfileDetail).build();
		
		BasePackage basePackage = new BasePackageBuilder(UUID.randomUUID().toString(), basePackageName).addQoSProfile(qosProfile).build();
		
		QuotaProfile quotaProfileDetail = QuotaProfileFactory.createQuotaProfileWithRandomUsage(UUID.randomUUID().toString());
		QoSProfileDetail quotaProfileDetail = QoSProfileDetailFactory.createQoSProfile().hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID)).forEachServicesHasPCCRule().build();
		QoSProfile addOnQoSProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfileDetail).hasHSQLevelQoSProfileDetail(quotaProfileDetail).build();
		
		AddOn addOn = new AddOn.AddOnBuilder(UUID.randomUUID().toString(), "exclusive").addQoSProfile(addOnQoSProfile).wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).build();
		AddOnSubscription addOnSubscription = SubscriptionFactory.createSubscriptionFor(addOn).build();
		LinkedHashMap<String, AddOnSubscription> addOnSubscriptions = new LinkedHashMap<String, AddOnSubscription>();
		addOnSubscriptions.put(addOnSubscription.getAddonSubscriptionId(), addOnSubscription);
		
		PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
		when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
		
		SPRInfo spr = sprBuilder.build();
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
		logExecutionStop(methodName);
		
		logValidationStart(methodName);
		assertNull(pcrfResponse.getQoSInformation().getSessionQoS());
		assertNull(pcrfResponse.getActivePCCRules());
		assertNull(pcrfResponse.getInstallablePCCRules());
		assertNull(pcrfRequest.getRemovedPCCRules());
		assertEquals(rejectReason, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.val));
		logValidationStop(methodName);
		
	}
	
	private static void logValidationStop(String methodName) {
		getLogger().debug("", "Executing:" +  methodName);
	}

	private static void logValidationStart(String methodName) {
		getLogger().debug("", "Executing: completed for:" +methodName);
	}

	private static void logExecutionStart(String methodName) {
		getLogger().debug("", "Validation start for :" +methodName);
	}
	
	private static void logExecutionStop(String methodName) {
		getLogger().debug("", "Validation completed for :" +methodName);
	}
	
	

*/}
