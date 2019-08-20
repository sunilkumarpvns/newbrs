package com.elitecore.netvertex.pm;

import junitparams.JUnitParamsRunner;

import org.junit.Ignore;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
@Ignore
public class QoSInformationTest {/*

	private static Function<PCCRule, String> pccRuleListToServiceIdentifierToPCCRuleFunction;
	

	@BeforeClass
	public static void setUp() {

		pccRuleListToServiceIdentifierToPCCRuleFunction = new PCCRuleListToServiceIdentifierToPCCRuleFunction();
	}

	@Test
	public void test_setSessionQoS_should_override_previous_qos_defination() {

		QoSInformation qoSInformation = new QoSInformation();

		IPCANQoS oldSessionQoS = IPCanQoSFactory.createSessionQoSWithRandomVal();
		qoSInformation.setQoSProfileDetail(oldSessionQoS);

		IPCANQoS newSessionQoS = createSessionQoSWithRandomVal();
		qoSInformation.setQoSProfileDetail(newSessionQoS);

		assertSame(newSessionQoS, qoSInformation.getSessionQoS());
	}

	@Test
	public void test_setSessionQoS_should_clear_previously_set_QoSProfileAction() {

		QoSInformation qoSInformation = new QoSInformation();

		qoSInformation.setRejectAction("test");

		IPCANQoS newSessionQoS = createSessionQoSWithRandomVal();
		qoSInformation.setQoSProfileDetail(newSessionQoS);

		assertNull(qoSInformation.getAction());
	}

	@Test
	public void test_setRejectAction_should_set_QoSProfileAction_as_REJECT_with_reject_cause() {

		QoSInformation qoSInformation = new QoSInformation();

		String rejectCause = "test";
		qoSInformation.setRejectAction(rejectCause);

		assertSame(QoSProfileAction.REJECT, qoSInformation.getAction());
		assertSame(rejectCause, qoSInformation.getRejectCause());
	}

	@Test
	public void test_setRejectAction_should_not_set_QoSProfileAction_as_REJECT_with_reject_cause_if_any_SessionQoS_already_set() {

		QoSInformation qoSInformation = new QoSInformation();

		IPCANQoS newSessionQoS = createSessionQoSWithRandomVal();
		qoSInformation.setQoSProfileDetail(newSessionQoS);

		String rejectCause = "test";
		qoSInformation.setRejectAction(rejectCause);

		assertNull(qoSInformation.getAction());
		assertNull(qoSInformation.getRejectCause());
	}

	@Test
	public void test_reset_should_flush_all_the_status_related_to_sessionQoS_PCCRules_reject_Action() {
		QoSInformation qoSInformation = new QoSInformation();

		IPCANQoS newSessionQoS = createSessionQoSWithRandomVal();
		qoSInformation.setQoSProfileDetail(newSessionQoS);

		PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().build();
		List<PCCRule> pccRules = Arrays.asList(pccRule);
		qoSInformation.setPCCRules(pccRules);

		assertSame(newSessionQoS, qoSInformation.getSessionQoS());
		assertEquals(Collectionz.asHashMap(pccRules, pccRuleListToServiceIdentifierToPCCRuleFunction), qoSInformation.getPCCRules());

		qoSInformation.reset();

		assertNull(qoSInformation.getSessionQoS());
		assertNull(qoSInformation.getPCCRules());

		String rejectCause = "test";
		qoSInformation.setRejectAction(rejectCause);
		assertSame(QoSProfileAction.REJECT, qoSInformation.getAction());
		assertSame(rejectCause, qoSInformation.getRejectCause());

		qoSInformation.reset();

		assertNull(qoSInformation.getAction());
		assertNull(qoSInformation.getRejectCause());

	}

	

	@Test
	public void test_elect_should_set_SessionQoS_if_not_session_QoS_previouly_set() {
		QoSInformation qoSInformation = new QoSInformation();

		QoSProfileDetail sessionQoS = createSessionQoSWithRandomVal();

		assertTrue(qoSInformation.elect(sessionQoS));
		assertSame(sessionQoS, qoSInformation.getQosProfileDetail().getSessionQoS());
	}

	@Test
	public void test_elect_should_set_SessionQoS_if_reject_action_previouly_set() {
		QoSInformation qoSInformation = new QoSInformation();

		String rejectCause = "test";
		qoSInformation.setRejectAction(rejectCause);

		IPCANQoS sessionQoS = createSessionQoSWithRandomVal();

		assertTrue(qoSInformation.elect(sessionQoS));
		assertSame(sessionQoS, qoSInformation.getSessionQoS());
	}

	@Test
	public void test_elect_should_set_SessionQoS_if_previouly_set_QoS_has_lower_qos() {
		QoSInformation qoSInformation = new QoSInformation();

		String rejectCause = "test";
		qoSInformation.setRejectAction(rejectCause);

		IPCANQoS sessionQoS = createSessionQoSWithRandomVal();
		qoSInformation.elect(sessionQoS);

		IPCANQoS newSessionQoS = createSessionQoSHasHigherQoSThan(sessionQoS);

		assertTrue(qoSInformation.elect(newSessionQoS));
		assertSame(newSessionQoS, qoSInformation.getSessionQoS());
	}

	@Test
	public void test_elect_should_set_SessionQoS_if_previouly_set_QoS_has_equal_qos() {
		QoSInformation qoSInformation = new QoSInformation();

		String rejectCause = "test";
		qoSInformation.setRejectAction(rejectCause);

		IPCANQoS sessionQoS = createSessionQoSWithRandomVal();
		qoSInformation.elect(sessionQoS);

		IPCANQoS newSessionQoS = createSessionQoSHasEqualQoSTo(sessionQoS);

		assertTrue(qoSInformation.elect(newSessionQoS));
		assertSame(newSessionQoS, qoSInformation.getSessionQoS());
	}

	@Test
	public void test_elect_should_set_SessionQoS_if_previouly_set_QoS_has_higher_qos() {
		QoSInformation qoSInformation = new QoSInformation();

		String rejectCause = "test";
		qoSInformation.setRejectAction(rejectCause);

		IPCANQoS sessionQoS = createSessionQoSWithRandomVal();
		qoSInformation.elect(sessionQoS);

		IPCANQoS newSessionQoS = createSessionQoSHasLowerQoSThan(sessionQoS);

		assertFalse(qoSInformation.elect(newSessionQoS));
		assertSame(sessionQoS, qoSInformation.getSessionQoS());
	}

	@Test
	public void test_setPCCRules_should_override_any_previously_set_pccRules() {

		QoSInformation qoSInformation = new QoSInformation();

		PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().build();
		List<PCCRule> pccRules = Arrays.asList(pccRule);
		qoSInformation.setPCCRules(pccRules);

		Map<String, PCCRule> serviceIdentifierToPCCRule = Collectionz.asHashMap(pccRules, pccRuleListToServiceIdentifierToPCCRuleFunction);

		assertEquals(serviceIdentifierToPCCRule, qoSInformation.getPCCRules());

		qoSInformation.setPCCRules(serviceIdentifierToPCCRule);

		assertSame(serviceIdentifierToPCCRule, qoSInformation.getPCCRules());
		
		PCCRule newPCCRule = PCCRuleFactory.createPCCRuleWithRandomQoS().build();
		List<PCCRule> newPCCRules = Arrays.asList(newPCCRule);
		qoSInformation.setPCCRules(newPCCRules);
		
		Map<String, PCCRule> serviceIdentifierToNewPCCRule = Collectionz.asHashMap(newPCCRules, pccRuleListToServiceIdentifierToPCCRuleFunction);

		assertEquals(serviceIdentifierToNewPCCRule, qoSInformation.getPCCRules());

		qoSInformation.setPCCRules(serviceIdentifierToNewPCCRule);

		assertSame(serviceIdentifierToNewPCCRule, qoSInformation.getPCCRules());

	}

	
	public Object[][] data_provider_for_elect_should_set_pccRule_if_no_pccRule_was_set_for_pccRule_service() {
		
		RatingGroup ratingGroup = new RatingGroup("1", "defaultRatingGroup", "", 1);
		ServiceDataFlow serviceDataFlow = new ServiceDataFlow("allow", "tcp", "any", "0", "any", "0");
		ServiceType httpService = new ServiceType("2", "http", 2, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
		ServiceType ftpService = new ServiceType("3", "ftp", 3, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
		
		return new Object[][] {
			$(Arrays.asList(PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(httpService).build(), 
					PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(ftpService).build())),
			$(Arrays.asList(PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(httpService).build())),
			$(Collections.emptyList())
		};
	}
	@Test
	@Parameters(method="data_provider_for_elect_should_set_pccRule_if_no_pccRule_was_set_for_pccRule_service")
	public void test_elect_should_set_pccRule_if_no_pccRule_was_set_for_pccRule_service(List<PCCRule> pccRules) {

		QoSInformation qosInformation = new QoSInformation();
		
		if(pccRules.isEmpty() == false) {			
			qosInformation.setPCCRules(pccRules);
		}

		PCCRule pccRule = createPCCRuleWithRandomQoS().build();
		qosInformation.elect(pccRule);

		
		Map<String, PCCRule> serviceIdentifierToPCCRule = Collectionz
				.asHashMap(pccRules, pccRuleListToServiceIdentifierToPCCRuleFunction);
		
		serviceIdentifierToPCCRule.put(pccRule.getServiceIdentifier(), pccRule);

		assertEquals(serviceIdentifierToPCCRule, qosInformation.getPCCRules());
	}
	
	
	public Object[][] data_provider_for_elect_with_list_should_set_pccRule_if_no_pccRule_was_set_for_pccRule_service() {
		
		RatingGroup ratingGroup = new RatingGroup("1", "defaultRatingGroup", "", 1);
		ServiceDataFlow serviceDataFlow = new ServiceDataFlow("allow", "tcp", "any", "0", "any", "0");
		ServiceType httpService = new ServiceType("2", "http", 2, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
		ServiceType ftpService = new ServiceType("3", "ftp", 3, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
		
		return new Object[][] {
			$(Arrays.asList(PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(httpService).build(), 
					PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(ftpService).build())),
					
			$(Arrays.asList(PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(httpService).build())),
			
			$(Collections.emptyList())
		};
	}
	
	@Test
	@Parameters(method="data_provider_for_elect_with_list_should_set_pccRule_if_no_pccRule_was_set_for_pccRule_service")
	public void test_elect_with_list_should_set_pccRules_if_no_pccRule_was_set_for_pccRule_service(List<PCCRule> pccRules) {
		
		RatingGroup ratingGroup = new RatingGroup("4", "defaultRatingGroup", "", 1);
		ServiceDataFlow serviceDataFlow = new ServiceDataFlow("allow", "tcp", "any", "0", "any", "0");
		ServiceType facebookService = new ServiceType("4", "facebook", 4, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));

		QoSInformation qosInformation = new QoSInformation();
		if(pccRules.isEmpty() == false) {			
			qosInformation.setPCCRules(pccRules);
		}

		PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().build();
		PCCRule facebookPCC = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(facebookService).build();
		qosInformation.elect(Arrays.asList(pccRule,facebookPCC));

		Map<String, PCCRule> serviceIdentifierToPCCRule = Collectionz
				.asHashMap(pccRules, pccRuleListToServiceIdentifierToPCCRuleFunction);
		
		serviceIdentifierToPCCRule.put(pccRule.getServiceIdentifier(), pccRule);
		serviceIdentifierToPCCRule.put(facebookPCC.getServiceIdentifier(), facebookPCC);

		assertEquals(serviceIdentifierToPCCRule, qosInformation.getPCCRules());

		qosInformation.setPCCRules(serviceIdentifierToPCCRule);

		assertSame(serviceIdentifierToPCCRule, qosInformation.getPCCRules());

	}
	
public Object[][] data_provider_for_elect_with_map_should_set_pccRule_if_no_pccRule_was_set_for_pccRule_service() {
		
		RatingGroup ratingGroup = new RatingGroup("1", "defaultRatingGroup", "", 1);
		ServiceDataFlow serviceDataFlow = new ServiceDataFlow("allow", "tcp", "any", "0", "any", "0");
		ServiceType httpService = new ServiceType("2", "http", 2, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
		ServiceType ftpService = new ServiceType("3", "ftp", 3, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
		
		return new Object[][] {
			$(Arrays.asList(PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(httpService).build(), 
					PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(ftpService).build())),
					
			$(Arrays.asList(PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(httpService).build())),
			
			$(Collections.emptyList())
		};
	}
	
	@Test
	@Parameters(method="data_provider_for_elect_with_map_should_set_pccRule_if_no_pccRule_was_set_for_pccRule_service")
	public void test_elect_with_map_should_set_pccRules_if_no_pccRule_was_set_for_pccRule_service(List<PCCRule> pccRules) {
		
		RatingGroup ratingGroup = new RatingGroup("4", "defaultRatingGroup", "", 1);
		ServiceDataFlow serviceDataFlow = new ServiceDataFlow("allow", "tcp", "any", "0", "any", "0");
		ServiceType facebookService = new ServiceType("4", "facebook", 4, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));

		QoSInformation qosInformation = new QoSInformation();
		if(pccRules.isEmpty() == false) {			
			qosInformation.setPCCRules(pccRules);
		}

		PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().build();
		PCCRule facebookPCC = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(facebookService).build();
		qosInformation.elect(Collectionz
				.asHashMap(Arrays.asList(pccRule,facebookPCC), pccRuleListToServiceIdentifierToPCCRuleFunction));

		Map<String, PCCRule> serviceIdentifierToPCCRule = Collectionz
				.asHashMap(pccRules, pccRuleListToServiceIdentifierToPCCRuleFunction);
		
		serviceIdentifierToPCCRule.put(pccRule.getServiceIdentifier(), pccRule);
		serviceIdentifierToPCCRule.put(facebookPCC.getServiceIdentifier(), facebookPCC);

		assertEquals(serviceIdentifierToPCCRule, qosInformation.getPCCRules());

		qosInformation.setPCCRules(serviceIdentifierToPCCRule);

		assertSame(serviceIdentifierToPCCRule, qosInformation.getPCCRules());

	}

	@Test
	public void test_elect_should_set_pccRule_if_previous_pccRule_has_lower_qos_for_service() {

		QoSInformation qosInformation = new QoSInformation();

		PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().build();

		qosInformation.elect(pccRule);

		PCCRule newPCCRule = PCCRuleFactory.createPCCRuleHasHigherQoSThan(pccRule);

		qosInformation.elect(newPCCRule);

		Map<String, PCCRule> serviceIdentifierToPCCRule = Collectionz
				.asHashMap(Arrays.asList(newPCCRule), pccRuleListToServiceIdentifierToPCCRuleFunction);

		assertEquals(serviceIdentifierToPCCRule, qosInformation.getPCCRules());

	}
	
	@Test
	public void test_elect_should_set_pccRule_if_previous_pccRule_has_equal_qos_for_service() {

		QoSInformation qosInformation = new QoSInformation();

		PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().build();

		qosInformation.elect(pccRule);

		PCCRule newPCCRule = PCCRuleFactory.createPCCRuleHasEqualQoSTo(pccRule);

		qosInformation.elect(newPCCRule);

		Map<String, PCCRule> serviceIdentifierToPCCRule = Collectionz
				.asHashMap(Arrays.asList(newPCCRule), pccRuleListToServiceIdentifierToPCCRuleFunction);

		assertEquals(serviceIdentifierToPCCRule, qosInformation.getPCCRules());

	}

	@Test
	public void test_elect_should_not_set_pccRule_if_previous_pccRule_has_higher_qos_for_service() {

		QoSInformation qosInformation = new QoSInformation();

		PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().build();

		qosInformation.elect(pccRule);

		PCCRule newPCCRule = PCCRuleFactory.createPCCRuleHasLowerQoSThan(pccRule);

		qosInformation.elect(newPCCRule);

		Map<String, PCCRule> serviceIdentifierToPCCRule = Collectionz
				.asHashMap(Arrays.asList(pccRule), pccRuleListToServiceIdentifierToPCCRuleFunction);

		assertEquals(serviceIdentifierToPCCRule, qosInformation.getPCCRules());

	}
	
	
	public Object[][] data_provider_for_elect_with_list_should_set_pccRules_if_previous_pccRules_has_lower_qos_for_same_service() {
		
		RatingGroup ratingGroup = new RatingGroup("1", "defaultRatingGroup", "", 1);
		ServiceDataFlow serviceDataFlow = new ServiceDataFlow("allow", "tcp", "any", "0", "any", "0");
		ServiceType httpService = new ServiceType("2", "http", 2, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
		ServiceType ftpService = new ServiceType("3", "ftp", 3, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
		
		return new Object[][] {
			$(Arrays.asList(PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(httpService).build(), 
					createPCCRuleWithRandomQoS().withServiceType(ftpService).build())),
					
			$(Arrays.asList(PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(httpService).build())),
			
			$(Collections.emptyList())
		};
	}
	
	@Test
	public void test_elect_with_list_should_set_pccRules_if_previous_pccRules_has_lower_qos_for_same_service() {
		
		RatingGroup ratingGroup = new RatingGroup("1", "defaultRatingGroup", "", 1);
		ServiceDataFlow serviceDataFlow = new ServiceDataFlow("allow", "tcp", "any", "0", "any", "0");
		ServiceType httpService = new ServiceType("2", "http", 2, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
		ServiceType ftpService = new ServiceType("3", "ftp", 3, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));

		QoSInformation qosInformation = new QoSInformation();

		PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().build();
		PCCRule httpPCCRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(httpService).build();
		PCCRule ftpPCCRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(ftpService).build();

		qosInformation.elect(Arrays.asList(pccRule, httpPCCRule, ftpPCCRule));

		PCCRule newPCCRule = PCCRuleFactory.createPCCRuleHasHigherQoSThan(pccRule);
		PCCRule newHTTPPCCRules = PCCRuleFactory.createPCCRuleHasHigherQoSThan(httpPCCRule);
		PCCRule newFTPPCCRules = PCCRuleFactory.createPCCRuleHasHigherQoSThan(ftpPCCRule);

		qosInformation.elect(Arrays.asList(newPCCRule,newHTTPPCCRules,newFTPPCCRules));

		Map<String, PCCRule> serviceIdentifierToPCCRule = Collectionz
				.asHashMap(Arrays.asList(newPCCRule,newHTTPPCCRules,newFTPPCCRules), pccRuleListToServiceIdentifierToPCCRuleFunction);

		assertEquals(serviceIdentifierToPCCRule, qosInformation.getPCCRules());

	}
	
	@Test
	public void test_elect_with_list_should_set_pccRule_if_previous_pccRule_has_equal_qos_for_service() {
		
		RatingGroup ratingGroup = new RatingGroup("1", "defaultRatingGroup", "", 1);
		ServiceDataFlow serviceDataFlow = new ServiceDataFlow("allow", "tcp", "any", "0", "any", "0");
		ServiceType httpService = new ServiceType("2", "http", 2, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
		ServiceType ftpService = new ServiceType("3", "ftp", 3, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));

		QoSInformation qosInformation = new QoSInformation();

		PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().build();
		PCCRule httpPCCRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(httpService).build();
		PCCRule ftpPCCRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(ftpService).build();

		qosInformation.elect(Arrays.asList(pccRule, httpPCCRule, ftpPCCRule));

		PCCRule newPCCRule = PCCRuleFactory.createPCCRuleHasEqualQoSTo(pccRule);
		PCCRule newHTTPPCCRules = PCCRuleFactory.createPCCRuleHasEqualQoSTo(httpPCCRule);
		PCCRule newFTPPCCRules = PCCRuleFactory.createPCCRuleHasEqualQoSTo(ftpPCCRule);

		qosInformation.elect(Arrays.asList(newPCCRule,newHTTPPCCRules,newFTPPCCRules));

		Map<String, PCCRule> serviceIdentifierToPCCRule = Collectionz
				.asHashMap(Arrays.asList(newPCCRule,newHTTPPCCRules,newFTPPCCRules), pccRuleListToServiceIdentifierToPCCRuleFunction);

		assertEquals(serviceIdentifierToPCCRule, qosInformation.getPCCRules());

	}

	@Test
	public void test_elect_with_list_should_not_set_pccRule_if_previous_pccRule_has_higher_qos_for_service() {
		
		RatingGroup ratingGroup = new RatingGroup("1", "defaultRatingGroup", "", 1);
		ServiceDataFlow serviceDataFlow = new ServiceDataFlow("allow", "tcp", "any", "0", "any", "0");
		ServiceType httpService = new ServiceType("2", "http", 2, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
		ServiceType ftpService = new ServiceType("3", "ftp", 3, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));

		QoSInformation qosInformation = new QoSInformation();

		PCCRule pccRule = createPCCRuleWithRandomQoS().build();
		PCCRule httpPCCRule = createPCCRuleWithRandomQoS().withServiceType(httpService).build();
		PCCRule ftpPCCRule = createPCCRuleWithRandomQoS().withServiceType(ftpService).build();

		qosInformation.elect(Arrays.asList(pccRule, httpPCCRule, ftpPCCRule));

		PCCRule newPCCRule = createPCCRuleHasLowerQoSThan(pccRule);
		PCCRule newHTTPPCCRules = createPCCRuleHasLowerQoSThan(httpPCCRule);
		PCCRule newFTPPCCRules = createPCCRuleHasLowerQoSThan(ftpPCCRule);

		qosInformation.elect(Arrays.asList(newPCCRule,newHTTPPCCRules,newFTPPCCRules));

		Map<String, PCCRule> serviceIdentifierToPCCRule = Collectionz
				.asHashMap(Arrays.asList(pccRule, httpPCCRule, ftpPCCRule), pccRuleListToServiceIdentifierToPCCRuleFunction);

		assertEquals(serviceIdentifierToPCCRule, qosInformation.getPCCRules());

	}
	
	@Test
	public void test_elect_with_map_should_set_pccRules_if_previous_pccRules_has_lower_qos_for_same_service() {
		
		RatingGroup ratingGroup = new RatingGroup("1", "defaultRatingGroup", "", 1);
		ServiceDataFlow serviceDataFlow = new ServiceDataFlow("allow", "tcp", "any", "0", "any", "0");
		ServiceType httpService = new ServiceType("2", "http", 2, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
		ServiceType ftpService = new ServiceType("3", "ftp", 3, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));

		QoSInformation qosInformation = new QoSInformation();

		PCCRule pccRule = createPCCRuleWithRandomQoS().build();
		PCCRule httpPCCRule = createPCCRuleWithRandomQoS().withServiceType(httpService).build();
		PCCRule ftpPCCRule = createPCCRuleWithRandomQoS().withServiceType(ftpService).build();

		qosInformation.elect(Arrays.asList(pccRule, httpPCCRule, ftpPCCRule));

		PCCRule newPCCRule = createPCCRuleHasHigherQoSThan(pccRule);
		PCCRule newHTTPPCCRules = createPCCRuleHasHigherQoSThan(httpPCCRule);
		PCCRule newFTPPCCRules = createPCCRuleHasHigherQoSThan(ftpPCCRule);

		qosInformation.elect(Collectionz
				.asHashMap(Arrays.asList(newPCCRule,newHTTPPCCRules,newFTPPCCRules), pccRuleListToServiceIdentifierToPCCRuleFunction));

		Map<String, PCCRule> serviceIdentifierToPCCRule = Collectionz
				.asHashMap(Arrays.asList(newPCCRule,newHTTPPCCRules,newFTPPCCRules), pccRuleListToServiceIdentifierToPCCRuleFunction);

		assertEquals(serviceIdentifierToPCCRule, qosInformation.getPCCRules());

	}
	
	@Test
	public void test_elect_with_map_should_set_pccRule_if_previous_pccRule_has_equal_qos_for_service() {
		
		RatingGroup ratingGroup = new RatingGroup("1", "defaultRatingGroup", "", 1);
		ServiceDataFlow serviceDataFlow = new ServiceDataFlow("allow", "tcp", "any", "0", "any", "0");
		ServiceType httpService = new ServiceType("2", "http", 2, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
		ServiceType ftpService = new ServiceType("3", "ftp", 3, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));

		QoSInformation qosInformation = new QoSInformation();

		PCCRule pccRule = createPCCRuleWithRandomQoS().build();
		PCCRule httpPCCRule = createPCCRuleWithRandomQoS().withServiceType(httpService).build();
		PCCRule ftpPCCRule = createPCCRuleWithRandomQoS().withServiceType(ftpService).build();

		qosInformation.elect(Arrays.asList(pccRule, httpPCCRule, ftpPCCRule));

		PCCRule newPCCRule = createPCCRuleHasEqualQoSTo(pccRule);
		PCCRule newHTTPPCCRules = createPCCRuleHasEqualQoSTo(httpPCCRule);
		PCCRule newFTPPCCRules = createPCCRuleHasEqualQoSTo(ftpPCCRule);

		qosInformation.elect(Collectionz
				.asHashMap(Arrays.asList(newPCCRule,newHTTPPCCRules,newFTPPCCRules), pccRuleListToServiceIdentifierToPCCRuleFunction));

		Map<String, PCCRule> serviceIdentifierToPCCRule = Collectionz
				.asHashMap(Arrays.asList(newPCCRule,newHTTPPCCRules,newFTPPCCRules), pccRuleListToServiceIdentifierToPCCRuleFunction);

		assertEquals(serviceIdentifierToPCCRule, qosInformation.getPCCRules());

	}

	@Test
	public void test_elect_with_map_should_not_set_pccRule_if_previous_pccRule_has_higher_qos_for_service() {
		
		RatingGroup ratingGroup = new RatingGroup("1", "defaultRatingGroup", "", 1);
		ServiceDataFlow serviceDataFlow = new ServiceDataFlow("allow", "tcp", "any", "0", "any", "0");
		ServiceType httpService = new ServiceType("2", "http", 2, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
		ServiceType ftpService = new ServiceType("3", "ftp", 3, Arrays.asList(serviceDataFlow), Arrays.asList(ratingGroup));
		

		QoSInformation qosInformation = new QoSInformation();

		PCCRule pccRule = createPCCRuleWithRandomQoS().build();
		PCCRule httpPCCRule = createPCCRuleWithRandomQoS().withServiceType(httpService).build();
		PCCRule ftpPCCRule = createPCCRuleWithRandomQoS().withServiceType(ftpService).build();

		qosInformation.elect(Arrays.asList(pccRule, httpPCCRule, ftpPCCRule));

		PCCRule newPCCRule = createPCCRuleHasLowerQoSThan(pccRule);
		PCCRule newHTTPPCCRules = createPCCRuleHasLowerQoSThan(httpPCCRule);
		PCCRule newFTPPCCRules = createPCCRuleHasLowerQoSThan(ftpPCCRule);

		qosInformation.elect(Collectionz
				.asHashMap(Arrays.asList(newPCCRule,newHTTPPCCRules,newFTPPCCRules), pccRuleListToServiceIdentifierToPCCRuleFunction));

		Map<String, PCCRule> serviceIdentifierToPCCRule = Collectionz
				.asHashMap(Arrays.asList(pccRule, httpPCCRule, ftpPCCRule), pccRuleListToServiceIdentifierToPCCRuleFunction);

		assertEquals(serviceIdentifierToPCCRule, qosInformation.getPCCRules());

	}

	private static class PCCRuleListToServiceIdentifierToPCCRuleFunction implements Function<PCCRule, String> {

		@Override
		public String apply(PCCRule pccRule) {
			return pccRule.getServiceIdentifier();
		}

	}

*/}
