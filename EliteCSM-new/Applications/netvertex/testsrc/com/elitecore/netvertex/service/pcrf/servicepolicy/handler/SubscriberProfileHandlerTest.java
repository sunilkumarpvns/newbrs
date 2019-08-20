package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.conf.MiscellaneousConfiguration;
import com.elitecore.netvertex.core.conf.NetvertexServerConfiguration;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.PccServicePolicyConfiguration;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.Nonnull;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(value=junitparams.JUnitParamsRunner.class)
@Ignore
public class SubscriberProfileHandlerTest {

    private static final String SUBSCRIBER_IDENTITY_VAL = "102";
    public static final String INR = "INR";
    private SubscriberProfileHandler subscriberProfileHandler;
	private ExecutionContext executionContext;
	
	@Mock private CacheAwareDDFTable ddfTable;
	@Mock private MiscellaneousConfiguration miscellaneousConfiguration;
	@Mock private PccServicePolicyConfiguration pcrfServicePolicyConfiguration;
	@Mock private PCRFServiceContext pcrfServiceContext;
	@Mock private NetVertexServerContext serverContext;
	@Mock private PolicyRepository policyRepository;
	

	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	
		NetvertexServerConfiguration netvertexServerConfiguration = mock(NetvertexServerConfiguration.class);
		when(pcrfServicePolicyConfiguration.getIdentityAttribute()).thenReturn(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
		
		when(pcrfServiceContext.getServerContext()).thenReturn(serverContext); 
		when(serverContext.getTaskScheduler()).thenReturn(mock(TaskScheduler.class));
		when(serverContext.getPolicyRepository()).thenReturn(policyRepository);
		
		//mocked to get isSessionCacheEnable true 
		when(serverContext.getServerConfiguration()).thenReturn(netvertexServerConfiguration);
		when(netvertexServerConfiguration.getMiscellaneousParameterConfiguration()).thenReturn(miscellaneousConfiguration);
		when(miscellaneousConfiguration.isSessionCacheEnabled()).thenReturn(true);
	
		
		
		subscriberProfileHandler = new SubscriberProfileHandler(pcrfServiceContext, 
				pcrfServicePolicyConfiguration);
		subscriberProfileHandler.init();
	}
	
	@Test
	public void test_handleRequest_should_reject_request_if_userIdentity_not_found_from_request_and_request_is_not_terminate_request() throws Exception {
		
		PCRFRequest pcrfRequest = createPCRFRequest(null, Arrays.asList(PCRFEvent.AUTHENTICATE, PCRFEvent.SESSION_START));
				
		PCRFResponse pcrfResponse = createPCRFResponse();
		subscriberProfileHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
		assertFalse(pcrfResponse.isFurtherProcessingRequired());
		assertEquals(PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.val));
		
	}
	
	
	public Object[][] dataProviderFor_expiryDate() {
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2030);
		
		return new Object[][] {
				{
					new Timestamp(calendar.getTimeInMillis())
				},
				{
					null
				}
		};
	}
	
	@Test
	@Parameters(method="dataProviderFor_expiryDate")
	public void test_handleRequest_should_process_request_if_expiry_date_is_not_provided_or_futureDate(Timestamp timestamp) throws Exception {
		
		PCRFRequest pcrfRequest = createPCRFRequest(SUBSCRIBER_IDENTITY_VAL, Arrays.asList(PCRFEvent.AUTHENTICATE, PCRFEvent.SESSION_START));
		PCRFResponse pcrfResponse = createPCRFResponse();
		
		executionContext = new ExecutionContext(pcrfRequest, mock(PCRFResponse.class), CacheAwareDDFTable.getInstance(), INR);
		
		SPRInfoImpl expectedSPRInfo = getExpectedSPRInfo();
		
		expectedSPRInfo.setExpiryDate(timestamp);
		
		//when(ddfTable.refreshCache(SUBSCRIBER_IDENTITY_VAL)).thenReturn(expectedSPRInfo);
		
		subscriberProfileHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
		
		assertTrue(pcrfResponse.isFurtherProcessingRequired());
		
	}


	@Test
	public void test_handleRequest_should_process_request_if_user_status_is_active_() throws Exception {
		
		PCRFRequest pcrfRequest = createPCRFRequest(SUBSCRIBER_IDENTITY_VAL, Arrays.asList(PCRFEvent.AUTHENTICATE, PCRFEvent.SESSION_START));
		PCRFResponse pcrfResponse = createPCRFResponse();
		executionContext = new ExecutionContext(pcrfRequest, pcrfResponse, CacheAwareDDFTable.getInstance(),INR);
		SPRInfoImpl expectedSPRInfo = getExpectedSPRInfo();
		expectedSPRInfo.setStatus(SubscriberStatus.ACTIVE.name());
		
		//when(ddfTable.refreshCache(SUBSCRIBER_IDENTITY_VAL)).thenReturn(expectedSPRInfo);
		
		subscriberProfileHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
		
		assertTrue(pcrfResponse.isFurtherProcessingRequired());
	}
	
	@Test
	public void test_handleRequest_should_skipp_processing_if_userIdentity_not_found_from_request_and_request_is_terminate_request() throws Exception {
		PCRFRequest pcrfRequest = createPCRFRequest(null, Arrays.asList(PCRFEvent.AUTHENTICATE, PCRFEvent.SESSION_STOP));
		executionContext = new ExecutionContext(pcrfRequest, mock(PCRFResponse.class), CacheAwareDDFTable.getInstance(),INR);
		PCRFResponse pcrfResponse = createPCRFResponse();
		subscriberProfileHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
		
		
		assertTrue(pcrfResponse.isFurtherProcessingRequired());
		assertNull(pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.val));
		
	}

	public Object[][] dataProviderFor_isApplicable() {
		
		
		return new Object[][] {
				{
					createPCRFRequest(SUBSCRIBER_IDENTITY_VAL, Arrays.asList(PCRFEvent.SESSION_STOP)), false
				},	
				{
					createPCRFRequest(SUBSCRIBER_IDENTITY_VAL, Arrays.asList(PCRFEvent.AUTHENTICATE)), true
				},
				{
					createPCRFRequest(SUBSCRIBER_IDENTITY_VAL, Arrays.asList( PCRFEvent.USAGE_REPORT)), false
				},
				{
					createPCRFRequest(SUBSCRIBER_IDENTITY_VAL, Arrays.asList(PCRFEvent.SESSION_STOP, PCRFEvent.AUTHENTICATE)), true
				},
				{
					createPCRFRequest(SUBSCRIBER_IDENTITY_VAL, Arrays.asList(PCRFEvent.SESSION_STOP, PCRFEvent.USAGE_REPORT)), false
				},
				{
					createPCRFRequest(null, Arrays.asList(PCRFEvent.SESSION_STOP)), false
				}
		};
	}
	
	/*@Test
	@Parameters(method="dataProviderFor_isApplicable")
	public void test_isApplicable_should_return_boolean_value(
			PCRFRequest pcrfRequest,
			boolean expected) throws Exception {
		SubscriberProfileHandlerExt subscriberProfileHandler = new SubscriberProfileHandlerExt(pcrfServiceContext, pcrfServicePolicyConfiguration);
		
		boolean isApplicable = subscriberProfileHandler.isApplicable(pcrfRequest);
		
		assertEquals(expected, isApplicable);
	}
*/
	private @Nonnull SPRInfoImpl getExpectedSPRInfo() {
		SPRInfoImpl sprInfo = new SPRInfoImpl();
		sprInfo.setUserName("user2");
		sprInfo.setPassword("user2");
		sprInfo.setImei("12345");
		sprInfo.setSubscriberIdentity(SUBSCRIBER_IDENTITY_VAL);
		sprInfo.setMsisdn("9797979798");
		sprInfo.setPhone("123456");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2030);
		sprInfo.setExpiryDate(new Timestamp(calendar.getTime().getTime()));
		return sprInfo; 
	}

	

	private @Nonnull PCRFResponse createPCRFResponse() {
		return new PCRFResponseImpl();
	}

	public static @Nonnull PCRFRequest createPCRFRequest(String userIdentity, List<PCRFEvent> events) {
		PCRFRequestImpl request = new PCRFRequestImpl();
		request.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, userIdentity);
		request.setSessionFound(true);
		Set<PCRFEvent> pcrfEvents = new HashSet<PCRFEvent>(2,1);
		pcrfEvents.addAll(events);
		request.setPCRFEvents(pcrfEvents);
		return request;
	}
	
	
	
	private static class SubscriberProfileHandlerExt extends SubscriberProfileHandler {

		public SubscriberProfileHandlerExt(
				PCRFServiceContext pcrfServiceContext,
				PccServicePolicyConfiguration servicePolicyConfiguration
				) {
			super(pcrfServiceContext, servicePolicyConfiguration);
		}
		
		public boolean isApplicable(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
			return super.isApplicable(serviceRequest, serviceResponse);
		}
		
	}
}
