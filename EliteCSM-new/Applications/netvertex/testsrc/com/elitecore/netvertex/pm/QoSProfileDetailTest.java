package com.elitecore.netvertex.pm;

import org.junit.Ignore;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
@Ignore
public class QoSProfileDetailTest {/*
	
	
	private static final KeyProvider KEY_PROVIDER = new KeyProvider();
	
	
	public class When_current_usage_not_found {
		@Test
		public void test_Apply_should_set_all_pcc_rule_and_session_qos_when_action_is_accept() throws Exception{
			
			FinalQoSSelectionDataBuilder qoSProfileDetailBuilder = new QoSProfileDetail.FinalQoSSelectionDataBuilder("qosProfile1",1);
			QuotaProfileDetail quotaProfileDetail = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "test", CommonConstants.ALL_SERVICE_ID,1).withBillingCycleTime(1).build();
			
			IPCANQoS sessionQoS = new IPCANQoSBuilder().withAAMBRDLInBytes(100).withAAMBRDLInBytes(100).withQCI("1").build();
			
			PCCRule pccRule = new PCCRuleBuilder("1", "test").withServiceType(new ServiceType(CommonConstants.ALL_SERVICE_ID, "any", 1L, null, null)).build();
			PCCRule pccRule2 = new PCCRuleBuilder("1", "test").withServiceType(new ServiceType("2", "any", 1L, null, null)).build();
			List<PCCRule> pccRules = Arrays.asList(pccRule, pccRule2);
			qoSProfileDetailBuilder.pccRules(pccRules);
			qoSProfileDetailBuilder.withQuotaProfileDetails(Arrays.asList(quotaProfileDetail));
			qoSProfileDetailBuilder.withSessionQos(sessionQoS);
			qoSProfileDetailBuilder.withAcceptAction();
			
			QoSProfileDetail qoSProfileDetail = qoSProfileDetailBuilder.build();
			
			QoSInformation qosInformation = new QoSInformation();
			
			Map<String, PCCRule> serviceToPCCRules = Collectionz.asHashMap(pccRules, KEY_PROVIDER);
			
			assertEquals(QoSSelectionResult.FULLY_APPLIED, qoSProfileDetail.apply(null, qosInformation, Collections.<String, SubscriberUsage>emptyMap()));
			assertEquals(serviceToPCCRules, qosInformation.getPCCRules());
			assertEquals(sessionQoS, qosInformation.getSessionQoS());
			
		}
		
		@Test
		public void test_Apply_should_set_reject_reason_when_action_is_reject() throws Exception{
			
			FinalQoSSelectionDataBuilder qoSProfileDetailBuilder = new QoSProfileDetail.FinalQoSSelectionDataBuilder("qosProfile1",1);
			QuotaProfileDetail quotaProfileDetail = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "test", CommonConstants.ALL_SERVICE_ID,1).withBillingCycleTime(1).build();
			
			PCCRule pccRule = new PCCRuleBuilder("1", "test").withServiceType(new ServiceType(CommonConstants.ALL_SERVICE_ID, "any", 1L, null, null)).build();
			qoSProfileDetailBuilder.pccRules(Arrays.asList(pccRule));
			qoSProfileDetailBuilder.withQuotaProfileDetails(Arrays.asList(quotaProfileDetail));
			String rejectCause = "reject";
			qoSProfileDetailBuilder.withAction(QoSProfileAction.REJECT, rejectCause);
			;
			
			QoSProfileDetail qoSProfileDetail = qoSProfileDetailBuilder.build();
			
			QoSInformation qosInformation = new QoSInformation();
			assertEquals(QoSSelectionResult.NOT_APPLIED, qoSProfileDetail.apply(null, qosInformation, Collections.<String, SubscriberUsage>emptyMap()));
			assertEquals(QoSProfileAction.REJECT, qosInformation.getAction());
			assertSame(rejectCause, qosInformation.getRejectCause());
		}
	}
	
	
	public class Whne_current_usage_found {
		
		@Test
		public void test_Apply_should_not_applied_any_qos_when_current_usage_exceeded_for_any_services() {
			
			QuotaProfileDetail quotaProfileDetail = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "test", CommonConstants.ALL_SERVICE_ID,1).withBillingCycleTime(1).build();
			
			SubscriberUsage subscriberUsage = new SubscriberUsageBuilder("1", "12345", CommonConstants.ALL_SERVICE_ID, "q1", "pkg1").withBillingCycleTime(2).build();
			
			HashMap<String, SubscriberUsage> subscriberUsages = new HashMap<String, SubscriberUsage>();
			
			subscriberUsages.put(quotaProfileDetail.getUsageKey(), subscriberUsage);
			
			QoSProfileDetail qoSProfileDetail = new QoSProfileDetail("qosProfile1", QoSProfileAction.ACCEPT,null, quotaProfileDetail, null, 1);
			
			QoSInformation qosInformation = new QoSInformation();
			assertEquals(QoSSelectionResult.NOT_APPLIED, qoSProfileDetail.apply(null, qosInformation, subscriberUsages));
			assertNull(qosInformation.getPCCRules());
			assertNull(qosInformation.getSessionQoS());
			assertNull(qosInformation.getAction());
			assertNull(qosInformation.getRejectCause());
		}
		
		@Test
		public void test_Apply_should_set_reject_reason_when_current_usage_qos_action_is_reject() {
			
			QuotaProfileDetail quotaProfileDetail = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "test", CommonConstants.ALL_SERVICE_ID,1).withBillingCycleTime(3).build();
			String quotaProfileId = "q1";
			SubscriberUsage subscriberUsage = new SubscriberUsageBuilder("1", "12345", CommonConstants.ALL_SERVICE_ID, quotaProfileId, "pkg1").withBillingCycleTime(2).build();
			
			
			HashMap<String, SubscriberUsage> subscriberUsages = new HashMap<String, SubscriberUsage>();
			
			subscriberUsages.put(subscriberUsage.getQuotaProfileIdOrRateCardId() + CommonConstants.USAGE_KEY_SEPARATOR + subscriberUsage.getServiceId(), subscriberUsage);
			
			String rejectCause = "reject";
			QoSProfileDetail qoSProfileDetail = new QoSProfileDetail("qosProfile1", QoSProfileAction.REJECT,rejectCause,quotaProfileDetail, null, 1);
			
			QoSInformation qosInformation = new QoSInformation();
			assertEquals(QoSSelectionResult.NOT_APPLIED, qoSProfileDetail.apply(null, qosInformation, subscriberUsages));
			assertEquals(QoSProfileAction.REJECT, qosInformation.getAction());
			assertSame(rejectCause, qosInformation.getRejectCause());
			
		}
		
		@Test
		public void test_Apply_should_return_fully_applied_result_when_all_pccrule_selected() throws Exception {
			
			String quotaProfileId = "q1";
			FinalQoSSelectionDataBuilder qoSProfileDetailBuilder = new QoSProfileDetail.FinalQoSSelectionDataBuilder("qosProfile1",1);
			QuotaProfileDetail quotaProfileDetail = new QuotaProfileDetail.QuotaProfileDetailBuilder(quotaProfileId, "test", CommonConstants.ALL_SERVICE_ID,1).withBillingCycleTime(3).build();
			QuotaProfileDetail quotaProfileDetail2 = new QuotaProfileDetail.QuotaProfileDetailBuilder(quotaProfileId, "test", "2",1).withBillingCycleTime(3).build();
			QuotaProfileDetail quotaProfileDetail3 = new QuotaProfileDetail.QuotaProfileDetailBuilder(quotaProfileId, "test", "3",1).withBillingCycleTime(3).build();
			
			SubscriberUsage subscriberUsage = new SubscriberUsageBuilder("1", "12345", CommonConstants.ALL_SERVICE_ID, quotaProfileId, "pkg1").withBillingCycleTime(2).build();
			SubscriberUsage subscriberUsage2 = new SubscriberUsageBuilder("1", "12345", "2", quotaProfileId, "pkg1").withBillingCycleTime(2).build();
			
			HashMap<String, SubscriberUsage> subscriberUsages = new HashMap<String, SubscriberUsage>();
			
			subscriberUsages.put(subscriberUsage.getQuotaProfileIdOrRateCardId() + CommonConstants.USAGE_KEY_SEPARATOR + subscriberUsage.getServiceId(), subscriberUsage);
			subscriberUsages.put(subscriberUsage2.getQuotaProfileIdOrRateCardId() + CommonConstants.USAGE_KEY_SEPARATOR + subscriberUsage2.getServiceId(), subscriberUsage2);
			
			PCCRule pccRule = new PCCRuleBuilder("1", "test").withServiceType(new ServiceType(CommonConstants.ALL_SERVICE_ID, "any", 1L, null, null)).build();
			PCCRule pccRule2 = new PCCRuleBuilder("2", "test").withServiceType(new ServiceType("2", "any", 2L, null, null)).build();
			PCCRule pccRule3 = new PCCRuleBuilder("3", "test").withServiceType(new ServiceType("3", "any", 3L, null, null)).build();
			PCCRule pccRule4 = new PCCRuleBuilder("4", "test").withServiceType(new ServiceType("4", "any", 4L, null, null)).build();
			
			IPCANQoS sessionQoS = new IPCANQoSBuilder().withAAMBRDLInBytes(100).withAAMBRDLInBytes(100).withQCI("1").build();
			
			List<PCCRule> pccRules = Arrays.asList(pccRule, pccRule2, pccRule3, pccRule4);
			qoSProfileDetailBuilder.pccRules(pccRules);
			qoSProfileDetailBuilder.withQuotaProfileDetails(Arrays.asList(quotaProfileDetail, quotaProfileDetail2,quotaProfileDetail3));
			qoSProfileDetailBuilder.withSessionQos(sessionQoS);
			qoSProfileDetailBuilder.withAcceptAction();
			
			QoSProfileDetail qoSProfileDetail = qoSProfileDetailBuilder.build();
			
			Map<String, PCCRule> serviceToPCCRules = Collectionz.asHashMap(pccRules, new Function<PCCRule, String>() {

				@Override
				public String apply(PCCRule input) {
					return input.getServiceIdentifier();
				}
				
			});
			
			QoSInformation qosInformation = new QoSInformation();
			assertEquals(QoSSelectionResult.FULLY_APPLIED, qoSProfileDetail.apply(null, qosInformation, subscriberUsages));
			assertEquals(serviceToPCCRules, qosInformation.getPCCRules());
			assertEquals(sessionQoS, qosInformation.getSessionQoS());
		}
		
		@Test
		public void test_Apply_should_return_partially_applied_result_when_all_pccrule_selected() throws Exception {
			
			FinalQoSSelectionDataBuilder qoSProfileDetailBuilder = new QoSProfileDetail.FinalQoSSelectionDataBuilder("qosProfile1",1);
			QuotaProfileDetail quotaProfileDetail = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "test", CommonConstants.ALL_SERVICE_ID,1).withBillingCycleTime(3).build();
			QuotaProfileDetail quotaProfileDetail2 = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "test", "2",1).withBillingCycleTime(1).build();
			QuotaProfileDetail quotaProfileDetail3 = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "test", "3",1).withBillingCycleTime(1).build();
			
			SubscriberUsage subscriberUsage = new SubscriberUsageBuilder("1", "12345", CommonConstants.ALL_SERVICE_ID, "q1", "pkg1").withBillingCycleTime(2).build();
			SubscriberUsage subscriberUsage2 = new SubscriberUsageBuilder("1", "12345", "2", "q1", "pkg1").withBillingCycleTime(4).build();
			
			HashMap<String, SubscriberUsage> subscriberUsages = new HashMap<String, SubscriberUsage>();
			
			subscriberUsages.put(subscriberUsage.getQuotaProfileIdOrRateCardId() + CommonConstants.USAGE_KEY_SEPARATOR + subscriberUsage.getServiceId(), subscriberUsage);
			subscriberUsages.put(subscriberUsage2.getQuotaProfileIdOrRateCardId() + CommonConstants.USAGE_KEY_SEPARATOR + subscriberUsage2.getServiceId(), subscriberUsage2);
			
			PCCRule pccRule = new PCCRuleBuilder("1", "test").withServiceType(new ServiceType(CommonConstants.ALL_SERVICE_ID, "any", 1L, null, null)).build();
			PCCRule pccRule2 = new PCCRuleBuilder("2", "test").withServiceType(new ServiceType("2", "any", 2L, null, null)).build();
			PCCRule pccRule3 = new PCCRuleBuilder("3", "test").withServiceType(new ServiceType("3", "any", 3L, null, null)).build();
			PCCRule pccRule4 = new PCCRuleBuilder("4", "test").withServiceType(new ServiceType("4", "any", 4L, null, null)).build();
			
			IPCANQoS sessionQoS = new IPCANQoSBuilder().withAAMBRDLInBytes(100).withAAMBRDLInBytes(100).withQCI("1").build();
			
			List<PCCRule> pccRules = Arrays.asList(pccRule, pccRule2, pccRule3, pccRule4);
			List<PCCRule> expectedPCCRules = Arrays.asList(pccRule, pccRule3, pccRule4);
			qoSProfileDetailBuilder.pccRules(pccRules);
			qoSProfileDetailBuilder.withQuotaProfileDetails(Arrays.asList(quotaProfileDetail, quotaProfileDetail2,quotaProfileDetail3));
			qoSProfileDetailBuilder.withSessionQos(sessionQoS);
			qoSProfileDetailBuilder.withAcceptAction();
			
			QoSProfileDetail qoSProfileDetail = qoSProfileDetailBuilder.build();
			
			
			
			Map<String, PCCRule> serviceToPCCRules = Collectionz.asHashMap(expectedPCCRules, new Function<PCCRule, String>() {

				@Override
				public String apply(PCCRule pccRule) {
					
					return pccRule.getServiceIdentifier();
				}
				
			});
			
			QoSInformation qosInformation = new QoSInformation();
			assertEquals(QoSSelectionResult.PARTIALLY_APPLIED, qoSProfileDetail.apply(null, qosInformation, subscriberUsages));
			assertEquals(serviceToPCCRules, qosInformation.getPCCRules());
			assertEquals(sessionQoS, qosInformation.getSessionQoS());
		}
		
		@Test
		public void test_Apply_should_select_pccrules_whose_services_usage_not_exceeded() throws Exception {
			
			FinalQoSSelectionDataBuilder qoSProfileDetailBuilder = new QoSProfileDetail.FinalQoSSelectionDataBuilder("qosProfile1",1);
			QuotaProfileDetail quotaProfileDetail = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "test", CommonConstants.ALL_SERVICE_ID,1).withBillingCycleTime(3).build();
			QuotaProfileDetail quotaProfileDetail2 = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "test", "2",1).withBillingCycleTime(1).build();
			QuotaProfileDetail quotaProfileDetail3 = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "test", "3",1).withBillingCycleTime(1).build();
			
			SubscriberUsage subscriberUsage = new SubscriberUsageBuilder("1", "12345", CommonConstants.ALL_SERVICE_ID, "q1", "pkg1").withBillingCycleTime(2).build();
			SubscriberUsage subscriberUsage2 = new SubscriberUsageBuilder("1", "12345", "2", "q1", "pkg1").withBillingCycleTime(4).build();
			
			HashMap<String, SubscriberUsage> subscriberUsages = new HashMap<String, SubscriberUsage>();
			
			subscriberUsages.put(subscriberUsage.getQuotaProfileIdOrRateCardId() + CommonConstants.USAGE_KEY_SEPARATOR + subscriberUsage.getServiceId(), subscriberUsage);
			subscriberUsages.put(subscriberUsage2.getQuotaProfileIdOrRateCardId() + CommonConstants.USAGE_KEY_SEPARATOR + subscriberUsage2.getServiceId(), subscriberUsage2);
			
			PCCRule pccRule = new PCCRuleBuilder("1", "test").withServiceType(new ServiceType(CommonConstants.ALL_SERVICE_ID, "any", 1L, null, null)).build();
			PCCRule pccRule2 = new PCCRuleBuilder("2", "test").withServiceType(new ServiceType("2", "any", 2L, null, null)).build();
			PCCRule pccRule3 = new PCCRuleBuilder("3", "test").withServiceType(new ServiceType("3", "any", 3L, null, null)).build();
			PCCRule pccRule4 = new PCCRuleBuilder("4", "test").withServiceType(new ServiceType("4", "any", 4L, null, null)).build();
			
			IPCANQoS sessionQoS = new IPCANQoSBuilder().withAAMBRDLInBytes(100).withAAMBRDLInBytes(100).withQCI("1").build();
			
			List<PCCRule> pccRules = Arrays.asList(pccRule, pccRule2, pccRule3, pccRule4);
			List<PCCRule> expectedPCCRules = Arrays.asList(pccRule, pccRule3, pccRule4);
			qoSProfileDetailBuilder.pccRules(pccRules);
			qoSProfileDetailBuilder.withQuotaProfileDetails(Arrays.asList(quotaProfileDetail, quotaProfileDetail2,quotaProfileDetail3));
			qoSProfileDetailBuilder.withSessionQos(sessionQoS);
			qoSProfileDetailBuilder.withAcceptAction();
			
			QoSProfileDetail qoSProfileDetail = qoSProfileDetailBuilder.build();
			
			
			
			Map<String, PCCRule> serviceToPCCRules = Collectionz.asHashMap(expectedPCCRules, new Function<PCCRule, String>() {

				@Override
				public String apply(PCCRule pccRule) {
					return pccRule.getServiceIdentifier();
				}
				
			});
			
			QoSInformation qosInformation = new QoSInformation();
			qoSProfileDetail.apply(null, qosInformation, subscriberUsages);
			
			assertEquals(serviceToPCCRules, qosInformation.getPCCRules());
			assertEquals(sessionQoS, qosInformation.getSessionQoS());
		}
		
		
		@Test
		public void test_Apply_should_select_pccrules_whose_services_not_specifed_in_quota_profile_and_any_service_quota_is_not_exceeded() throws Exception {
			
			
			QuotaProfileDetail quotaProfileDetail = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "test", CommonConstants.ALL_SERVICE_ID,1).withBillingCycleTime(3).build();
			QuotaProfileDetail quotaProfileDetail2 = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "test", "2",1).withBillingCycleTime(1).build();
			
			SubscriberUsage subscriberUsage = new SubscriberUsageBuilder("1", "12345", CommonConstants.ALL_SERVICE_ID, "q1", "pkg1").withBillingCycleTime(2).build();
			
			HashMap<String, SubscriberUsage> subscriberUsages = new HashMap<String, SubscriberUsage>();
			
			subscriberUsages.put(subscriberUsage.getQuotaProfileIdOrRateCardId() + CommonConstants.USAGE_KEY_SEPARATOR + subscriberUsage.getServiceId(), subscriberUsage);
			
			PCCRule pccRule = new PCCRuleBuilder("1", "test").withServiceType(new ServiceType(CommonConstants.ALL_SERVICE_ID, "any", 1L, null, null)).build();
			PCCRule pccRule2 = new PCCRuleBuilder("2", "test").withServiceType(new ServiceType("2", "any", 2L, null, null)).build();
			
			
			IPCANQoS sessionQoS = new IPCANQoSBuilder().withAAMBRDLInBytes(100).withAAMBRDLInBytes(100).withQCI("1").build();
			
			List<PCCRule> pccRules = Arrays.asList(pccRule, pccRule2);
			
			///serviceLevelQuotaSpecified
			FinalQoSSelectionDataBuilder qosProfileDetailBuilder = new QoSProfileDetail.FinalQoSSelectionDataBuilder("qosProfile1",1);
			qosProfileDetailBuilder.pccRules(pccRules);
			qosProfileDetailBuilder.withQuotaProfileDetails(Arrays.asList(quotaProfileDetail, quotaProfileDetail2));
			
			qosProfileDetailBuilder.withSessionQos(sessionQoS);
			qosProfileDetailBuilder.withAcceptAction();
			
			QoSProfileDetail qosProfileDetail = qosProfileDetailBuilder.build();
			
			Map<String, PCCRule> serviceToPCCRules = Collectionz.asHashMap(pccRules, KEY_PROVIDER);
			
			QoSInformation qosInformation = new QoSInformation();
			qosProfileDetail.apply(null, qosInformation, subscriberUsages);
			
			assertEquals(serviceToPCCRules, qosInformation.getPCCRules());
			assertEquals(sessionQoS, qosInformation.getSessionQoS());
			
			
			///serviceLevelQuotaNotSpecified
			
			qosProfileDetailBuilder = new QoSProfileDetail.FinalQoSSelectionDataBuilder("qosProfile1",1);
			qosProfileDetailBuilder.pccRules(pccRules);
			qosProfileDetailBuilder.withQuotaProfileDetails(Arrays.asList(quotaProfileDetail));
			
			qosProfileDetailBuilder.withSessionQos(sessionQoS);
			qosProfileDetailBuilder.withAcceptAction();
			
			qosProfileDetail = qosProfileDetailBuilder.build();
			
			serviceToPCCRules = Collectionz.asHashMap(pccRules, KEY_PROVIDER);
			
			qosInformation = new QoSInformation();
			qosProfileDetail.apply(null, qosInformation, subscriberUsages);
			
			assertEquals(serviceToPCCRules, qosInformation.getPCCRules());
			assertEquals(sessionQoS, qosInformation.getSessionQoS());
		}
	}

	
	private static class KeyProvider implements Function<PCCRule, String>{

		@Override
		public String apply(PCCRule pccRule) {
			return pccRule.getServiceIdentifier();
		}
		
		
	}
	
	
	

*/}
