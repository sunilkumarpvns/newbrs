package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;


import org.junit.Ignore;

@Ignore
public class BasePackage_is_replacable_by_false_and_no_addOn_applied {/*
	
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
	
	@Test
	public void test_process_should_reject_session_when_base_policy_group_reject_the_session() throws OperationFailedException {
		
		String rejectReason = "reject";
		QoSProfileDetail qoSProfileDetail = QoSProfileDetailFactory.createQoSProfile().rejectAction(rejectReason).build();
		
		QoSProfile qosProfile = QosProfileFactory.createQosProfile().hasHSQLevelQoSProfileDetail(qoSProfileDetail).build();
		
		BasePackage basePackage = new BasePackageBuilder(UUID.randomUUID().toString(), basePackageName).addQoSProfile(qosProfile).build();
		PolicyRepository dummyPolicyRepository = new DummyPolicyRepository.PolicyRepositoryBuilder().withBasePackage(basePackage).build();
		when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
		
		SPRInfo spr = new SPRInfoBuilder().withSubscriberPackage(basePackageName).build();
		SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
		
		PCRFRequest pcrfRequest = pcrfRequestBuilder.withSubscriberProfile(spr).build();
		PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
		
		ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
		subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
		
		assertNull(pcrfResponse.getQoSInformation().getSessionQoS());
		assertNull(pcrfResponse.getActivePCCRules());
		assertNull(pcrfResponse.getInstallablePCCRules());
		assertNull(pcrfRequest.getRemovedPCCRules());
		assertEquals(rejectReason, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.val));
		getLogger().debug("", pcrfResponse.toString());
	}
	
	@Test
	public void test_process_should_reject_session_when_no_policy_statisfied() throws OperationFailedException {
		
		String basePackageName = "Base package";
		String subscriberIdentity = "1234";
		
		BasePackage basePackage = BasePackageFactory.createNotReplacableByAddOnBasePackage(basePackageName);
		PolicyRepository dummyPolicyRepository = new DummyPolicyRepository.PolicyRepositoryBuilder().withBasePackage(basePackage).build();
		when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
		
		SPRInfo spr = new SPRInfoBuilder().withSubscriberPackage(basePackageName).build();
		SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
		
		PCRFRequest pcrfRequest = new PCRFRequestBuilder().addSubscriberIdentity(subscriberIdentity).withSubscriberProfile(spr).build();
		PCRFResponse pcrfResponse = new PCRFResponseBuilder().addSubscriberIdentity(subscriberIdentity).build();
		
		
		pcrfRequest.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberIdentity);
		pcrfResponse.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberIdentity);
		
		Map<String, SubscriberUsage> subscriberUsages = UsageFactory.createUsageFor(basePackage).hasUsageExceededForAllServices().build();
		
		Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
		packageUsages.put(basePackage.getId(), subscriberUsages);
		
		when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
		
		ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
		subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
		
		assertNull(pcrfResponse.getQoSInformation().getSessionQoS());
		assertNull(pcrfResponse.getActivePCCRules());
		assertNull(pcrfResponse.getInstallablePCCRules());
		assertNull(pcrfRequest.getRemovedPCCRules());
		assertEquals(PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.val));
		getLogger().debug("", pcrfResponse.toString());
	}
	
	@Test
	public void test_process_should_select_best_QoS() throws OperationFailedException {
		
		String basePackageName = "Base package";
		String subscriberIdentity = "1234";
		
		BasePackage basePackage = BasePackageFactory.createNotReplacableByAddOnBasePackage(basePackageName);
		PolicyRepository dummyPolicyRepository = policyRepositoryBuilder.withBasePackage(basePackage).build();
		when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
		
		SPRInfo spr = new SPRInfoBuilder().withSubscriberPackage(basePackageName).build();
		SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
		
		PCRFRequest pcrfRequest = new PCRFRequestBuilder().addSubscriberIdentity(subscriberIdentity).withSubscriberProfile(spr).build();
		PCRFResponse pcrfResponse = new PCRFResponseBuilder().addSubscriberIdentity(subscriberIdentity).build();
		
		
		pcrfRequest.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberIdentity);
		pcrfResponse.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberIdentity);
		
		Map<String, SubscriberUsage> subscriberUsages = UsageFactory.createUsageFor(basePackage).hasUsageLowerThanHSQForAllServices().build();
		
		Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
		packageUsages.put(basePackage.getId(), subscriberUsages);
		
		when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
		
		ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
		subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
		
		assertSame(pcrfResponse.getQoSInformation().getSessionQoS(),BasePackageFactory.findHigestQoS(basePackage));
		getLogger().debug("", pcrfResponse.toString());
	}
	
	
	@Test
	public void test_process_should_select_best_PCCRule() throws OperationFailedException {
		
		String basePackageName = "Base package";
		String subscriberIdentity = "1234";

		
		QoSProfile qosProfile = QosProfileFactory.createSimpleProfile();
		BasePackage basePackage = new BasePackage.BasePackageBuilder(UUID.randomUUID().toString(), basePackageName).
				addQoSProfile(qosProfile).build();
		
		PolicyRepository dummyPolicyRepository = new DummyPolicyRepository.PolicyRepositoryBuilder().withBasePackage(basePackage).build();
		when(serverContext.getPolicyRepository()).thenReturn(dummyPolicyRepository);
		
		SPRInfo spr = new SPRInfoBuilder().withSubscriberPackage(basePackageName).build();
		SubscriberPolicyHandler subscriberPolicyHandler = new SubscriberPolicyHandler(serviceContext);
		
		PCRFRequest pcrfRequest = new PCRFRequestBuilder().addSubscriberIdentity(subscriberIdentity).withSubscriberProfile(spr).build();
		PCRFResponse pcrfResponse = new PCRFResponseBuilder().addSubscriberIdentity(subscriberIdentity).build();
		
		pcrfRequest.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberIdentity);
		pcrfResponse.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberIdentity);
		
		Map<String, SubscriberUsage> subscriberUsages = UsageFactory.createUsageFor(basePackage).hasUsageLowerThanHSQForAllServices().build();
		
		Map<String, Map<String, SubscriberUsage>> packageUsages = new HashMap<String, Map<String,SubscriberUsage>>(1,1);
		packageUsages.put(basePackage.getId(), subscriberUsages);
		
		when(subscriberRepository.getCurrentUsage(Mockito.anyString())).thenReturn(packageUsages);
		
		ExecutionContext executionContext = new ExecutionContext(subscriberRepository, pcrfRequest, pcrfResponse);
		subscriberPolicyHandler.process(pcrfRequest, pcrfResponse, executionContext);
		
		assertEquals(pcrfResponse.getActivePCCRules(), qosProfile.getPCCRules());
		assertEquals(pcrfResponse.getInstallablePCCRules(), qosProfile.getPCCRules());
		assertNull(pcrfResponse.getRemovablePCCRules());
		
		getLogger().debug("", pcrfResponse.toString());
	}
	
	
*/}
