package com.elitecore.netvertex.usagemetering;

import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.ABMFconfigurationImpl;
import com.elitecore.corenetvertex.spr.SPInterface;
import com.elitecore.corenetvertex.spr.ServiceUsage;
import com.elitecore.corenetvertex.spr.SubscriberRepositoryImpl;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.UsageProvider;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.service.pcrf.DummyPCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@RunWith(JUnitParamsRunner.class)
@Ignore
public class UMHandlerTest {

    public static final String INR = "INR";
    private PCRFRequest pcrfRequest;
	private PCRFResponse pcrfResponse;
	private UMHandler umHandler;
	private Function<List<SubscriberUsage>, Map<String,Map<String,SubscriberUsage>>> transformer = new ListToMapFuntion();
	private Function<Collection<SubscriberUsage>, List<SubscriberUsage>> collectionTransformer = new CollectionToListFuntion();
	
	public void setUp(List<UsageMonitoringInfo> reportedInfoList,
					  LinkedHashMap<String,String> reservationMap, Map<String, Map<String, SubscriberUsage>> currentUsages, MeteringLevelTestConfig meteringLevelTestConfig){
		
		pcrfRequest = meteringLevelTestConfig.getPCRFReq();
		pcrfResponse = meteringLevelTestConfig.getPCRFRes();
		pcrfResponse.setUsageReservations(reservationMap);
		pcrfResponse.setSubscriptions(meteringLevelTestConfig.getAddOnSubscriptions());
		pcrfRequest.setReportedUsageInfoList(reportedInfoList);

		
		String subscriberIdentity = pcrfRequest.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
		String dataPackageName = pcrfRequest.getAttribute(PCRFKeyConstants.SUB_DATA_PACKAGE.val);
		
		

		SPRInfo sprInfo = new SPRInfoImpl.SPRInfoBuilder().
						withSubscriberIdentity(subscriberIdentity).
						withProductOffer(dataPackageName).
						build();


        pcrfRequest.setSPRInfo(sprInfo);


		if(Maps.isNullOrEmpty(currentUsages) == false){
			pcrfResponse.setServiceUsage(new ServiceUsage(currentUsages));
		} else {
			pcrfResponse.setServiceUsage(new ServiceUsage(Maps.<String, Map<String, SubscriberUsage>>newLinkedHashMap()));
		}


		umHandler = new UMHandler(meteringLevelTestConfig.getPCRFServiceContext());
		
	}
	

	@Test
    public void test_handle_request_should_skipp_usage_metering_when_reported_usage_is_empty() {

        PCRFResponse pcrfResponse = mock(PCRFResponse.class, (Answer<Object>) invocation -> {
            throw new RuntimeException();
        });
		
		DummyPCRFServiceContext dummyPCRFServiceContext = new DummyPCRFServiceContext();
		
		umHandler = new UMHandler(dummyPCRFServiceContext);
		
		PCRFRequest pcrfRequest = mock(PCRFRequest.class, RETURNS_SMART_NULLS);
		
		when(pcrfRequest.getPCRFEvents()).thenReturn(EnumSet.of(PCRFEvent.USAGE_REPORT));
		
		umHandler.handleRequest(pcrfRequest, pcrfResponse, new ExecutionContext(pcrfRequest, pcrfResponse, CacheAwareDDFTable.getInstance(), INR));
	}


	@Test
	@Parameters(source=UMHandlerDataProviderForReplaceList.class)
    public void test_handler_request_do_proper_usage_metering_and_provide_proper_usage_list_that_need_to_be_replace(String message,
                                                                                                                    LinkedHashMap<String, String> reservationMap,
                                                                                                                    List<UsageMonitoringInfo> reportedInfoList,
                                                                                                                    List<SubscriberUsage> previousUsages,
                                                                                                                    List<SubscriberUsage> expectedInsertList,
                                                                                                                    MeteringLevelTestConfig meteringLevelTestConfig) throws OperationFailedException {
		
		setUp(reportedInfoList, reservationMap, transformer.apply(previousUsages), meteringLevelTestConfig);

		TestableSubscriberRepository testableSubscriberRepository =  new TestableSubscriberRepository (null, null, null, meteringLevelTestConfig.getPolicyRepository());

        SPRInfoImpl sprInfo = (SPRInfoImpl) pcrfRequest.getSPRInfo();
        sprInfo.setUsageProvider(testableSubscriberRepository);


		ExecutionContext executionContext = spy(meteringLevelTestConfig.getExecutionContext());
		when(executionContext.getSubscriptions()).thenReturn(meteringLevelTestConfig.getAddOnSubscriptions());
		
		umHandler.process(pcrfRequest, pcrfResponse, executionContext);

        assertReflectionEquals(message, expectedInsertList, collectionTransformer.apply(testableSubscriberRepository.getReplaceUsages()), ReflectionComparatorMode.LENIENT_ORDER);
	}



/*
	@Test
	@Parameters(source=UMHandlerDataProviderForAddToExistingList.class)

	public void test_handler_request_do_proper_usage_metering_and_provide_proper_usage_list_that_need_to_be_add_to_existing(String message,
									LinkedHashMap<String,String> reservationMap,
									List<UsageMonitoringInfo> reportedInfoList,
                        			List<SubscriberUsage> previousUsages,
                        			List<SubscriberUsage> expectedAddToExistingList,
                        			MeteringLevelTestConfig meteringLevelTestConfig) throws OperationFailedException {
		
		setUp(reportedInfoList, reservationMap, transformer.apply(previousUsages), meteringLevelTestConfig);

		TestableSubscriberRepository testableSubscriberRepository =  new TestableSubscriberRepository (null, null, null, meteringLevelTestConfig.getPolicyRepository());

		ExecutionContext executionContext = spy(meteringLevelTestConfig.getExecutionContext());
		when(executionContext.getSubscriptions()).thenReturn(meteringLevelTestConfig.getAddOnSubscriptions());
		
		umHandler.process(pcrfRequest, pcrfResponse, executionContext);
		
		assertReflectionEquals(message, expectedAddToExistingList, testableSubscriberRepository.getAddToExistingUsages(), ReflectionComparatorMode.LENIENT_ORDER);
	}



	@Test
	@Parameters(source=UMHandlerDataProviderForInsertUsageList.class)
	public void test_handler_request_do_proper_usage_metering_and_provide_proper_usage_list_that_need_to_be_insert(String message,
																												   LinkedHashMap<String,String> reservationMap,
																												   List<UsageMonitoringInfo> reportedInfoList,
																												   List<SubscriberUsage> previousUsages,
																												   List<SubscriberUsage> expectedInsertList,
																												   MeteringLevelTestConfig meteringLevelTestConfig) throws OperationFailedException {
		
		setUp(reportedInfoList, reservationMap, transformer.apply(previousUsages), meteringLevelTestConfig);

		TestableSubscriberRepository testableSubscriberRepository =  new TestableSubscriberRepository (null, null, null, meteringLevelTestConfig.getPolicyRepository());

		ExecutionContext executionContext = spy(meteringLevelTestConfig.getExecutionContext());
		
		umHandler.process(pcrfRequest, pcrfResponse, executionContext);
		
		assertReflectionEquals(message, expectedInsertList, collectionTransformer.apply(testableSubscriberRepository.getInsertUsages()), ReflectionComparatorMode.LENIENT_ORDER);
	}



	@Test
	@Parameters(source=UMHandlerDataProviderForNewCurrentUsageList.class)
	public void test_handler_request_do_proper_usage_metering_and_provide_proper_new_current_usage(String message,
									LinkedHashMap<String, String> reservationMap,
									List<UsageMonitoringInfo> reportedInfoList,
                        			List<SubscriberUsage> previousUsages,
                        			List<SubscriberUsage> currentUsages,
                        			MeteringLevelTestConfig meteringLevelTestConfig) throws OperationFailedException {
		
		setUp(reportedInfoList, reservationMap, transformer.apply(previousUsages), meteringLevelTestConfig);

		TestableSubscriberRepository testableSubscriberRepository =  new TestableSubscriberRepository (null, null, null, meteringLevelTestConfig.getPolicyRepository());
		ExecutionContext executionContext = spy(meteringLevelTestConfig.getExecutionContext());
		when(executionContext.getSubscriptions()).thenReturn(meteringLevelTestConfig.getAddOnSubscriptions());
		
		umHandler.process(pcrfRequest, pcrfResponse, executionContext);
		
		assertReflectionEquals(message, transformer.apply(currentUsages), pcrfResponse.getCurrentUsage(), ReflectionComparatorMode.LENIENT_ORDER);
	}
*/


	private class ListToMapFuntion implements Function<List<SubscriberUsage>, Map<String, Map<String, SubscriberUsage>>> {

		@Override
		public Map<String, Map<String, SubscriberUsage>> apply(List<SubscriberUsage> subscriberUsages) {

			if(subscriberUsages == null) {
				return null;
			}
			
			Map<String,Map<String,SubscriberUsage>> packageWiseSubscription = new HashMap<String, Map<String,SubscriberUsage>>();
			
			for(SubscriberUsage subscriberUsage : subscriberUsages) {
				Map<String,SubscriberUsage> serviceUsages;
				if(subscriberUsage.getSubscriptionId() == null) {
					serviceUsages = packageWiseSubscription.get(subscriberUsage.getPackageId());
				} else {
					serviceUsages = packageWiseSubscription.get(subscriberUsage.getSubscriptionId());
				}


				
				if(serviceUsages == null){
					serviceUsages = new HashMap<String, SubscriberUsage>();

					if(subscriberUsage.getSubscriptionId() == null) {
						packageWiseSubscription.put(subscriberUsage.getPackageId(), serviceUsages);
					} else {
						packageWiseSubscription.put(subscriberUsage.getSubscriptionId(), serviceUsages);
					}

				}
				
				serviceUsages.put(subscriberUsage.getQuotaProfileId()+ CommonConstants.USAGE_KEY_SEPARATOR +subscriberUsage.getServiceId(), subscriberUsage);
			}
			
			return packageWiseSubscription;
			
		}
		
	}
	
	private class CollectionToListFuntion implements Function<Collection<SubscriberUsage>, List<SubscriberUsage>> {

		@Override
		public List<SubscriberUsage> apply(Collection<SubscriberUsage> subscriberUsages) {
			
			if(subscriberUsages == null){
				return null;
			}
			
			return new ArrayList<SubscriberUsage>(subscriberUsages);	
		}
		
	}

    public static class TestableSubscriberRepository extends SubscriberRepositoryImpl implements UsageProvider {

		private Collection<SubscriberUsage> insertUsages;
		private ArrayList<SubscriberUsage> addToExistingUsages = null;
		private ArrayList<SubscriberUsage> replaceUsages;

		public Collection<SubscriberUsage> getInsertUsages() {
			return insertUsages;
		}

		public ArrayList<SubscriberUsage> getAddToExistingUsages() {
			return addToExistingUsages;
		}

		public ArrayList<SubscriberUsage> getReplaceUsages() {
			return replaceUsages;
		}

		public TestableSubscriberRepository(TransactionFactory transactionFactory, AlertListener alertListener, SPInterface spInterface, PolicyRepository policyRepository) {
            super(null, null, transactionFactory, alertListener, spInterface, policyRepository, null, new ABMFconfigurationImpl(1, 1, 1), null, null, null, null, null, null, null,null,null, "INR");
		}

		@Override
		public void insertNew(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException {
			this.insertUsages = usages;
		}

		@Override
		public void addToExisting(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException {

			if (addToExistingUsages == null) {
				this.addToExistingUsages = new ArrayList<SubscriberUsage>(usages.size());
			}
			this.addToExistingUsages.addAll(usages);
		}

		@Override
		public void replace(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException {

			if (replaceUsages == null) {
				this.replaceUsages = new ArrayList<SubscriberUsage>(usages.size());
			}
			this.replaceUsages.addAll(usages);
		}

	}
	

}
