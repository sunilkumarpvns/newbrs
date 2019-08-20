package com.elitecore.aaa.diameter.service.application.handlers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.core.drivers.BaseAcctDriver;
import com.elitecore.aaa.core.drivers.IEliteAcctDriver;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.NasServicePolicyConfiguration;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.NasServicePolicyConfigurationData;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.drivers.TransientFailureException;
import com.elitecore.core.commons.plugins.script.DriverScript;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.manager.scripts.ExternalScriptsManager;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class NasAcctHandlerTest {

	private static final String DUMMY_ID = "0";
	
	private DiameterRequest diameterRequest;
	private ApplicationRequest request;
	private ApplicationResponse response;
	private NasAcctHandler nasAccthandler;
	private NasServicePolicyConfiguration policyConfiguration = Mockito.spy(new NasServicePolicyConfigurationData());
	
	@Mock private DiameterServiceContext diameterServiceContext;
	@Mock private AAAServerContext aaaServerContext;
	@Mock private ExternalScriptsManager scriptManager;
	private Map<String, Integer> driverIdToWeightage = new LinkedHashMap<String, Integer>();
	private Map<String, IEliteAcctDriver> driverIdToDriver = new LinkedHashMap<String, IEliteAcctDriver>();
	
	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}
	
	@Before
	public void setup() throws Exception{
		MockitoAnnotations.initMocks(this);
		createRequestAndResponse();
		
		policyConfiguration.getNasAcctDetail().setScript("SampleDriverScript");
		when(diameterServiceContext.getServerContext()).thenReturn(aaaServerContext);

		when(policyConfiguration.getAcctDriverInstanceIdsMap()).thenReturn(driverIdToWeightage);
		when(aaaServerContext.getDiameterDriver(Mockito.anyString())).thenAnswer(new Answer<IEliteAcctDriver>() {

			@Override
			public IEliteAcctDriver answer(InvocationOnMock invocation) throws Throwable {
				return driverIdToDriver.get(invocation.getArgumentAt(0, String.class));
			}
		});
		
		when(diameterServiceContext.getServerContext().getExternalScriptsManager()).thenReturn(scriptManager);
	}
	
	@Test
	public void isAlwaysEligible() throws Exception {
		createHandler();

		assertTrue(nasAccthandler.isEligible(request, response));
	}
	
	@Test
	public void defaultResponseBehaviorIsNotApplicableIfAnyDriverIsAlive() throws Exception {
		FakeDriver driver1 = Mockito.spy(new FakeDriver(aaaServerContext));
		FakeDriver driver2 = Mockito.spy(new FakeDriver(aaaServerContext));
		
		addDriver("1", driver1);
		addDriver("2", driver2);
		
		createHandler();
		
		assertFalse(nasAccthandler.isResponseBehaviorApplicable());
	}
	
	@Test
	public void defaultResponseBehaviorIsApplicableIfAllDriverAreDead() throws Exception {
		FakeDriver driver1 = Mockito.spy(new FakeDriver(aaaServerContext));
		
		addDriver("1", driver1);
		
		createHandler();
		
		driver1.markDead();
		
		assertTrue(nasAccthandler.isResponseBehaviorApplicable());
	}
	
	public class AccountingRecordNumberPresentInRequest {
		
		@Before
		public void setUp() {
			DummyDiameterDictionary.getInstance();
			diameterRequest.addAvp(DiameterAVPConstants.ACCOUNTING_RECORD_NUMBER, DUMMY_ID);
		}

		@Test
		public void driversAreExecutedAsPerLoadBalancingConfiguration() throws InitializationFailedException, DriverProcessFailedException {
			FakeDriver driver1 = Mockito.spy(new FakeDriver(aaaServerContext));
			FakeDriver driver2 = Mockito.spy(new FakeDriver(aaaServerContext));
			
			addDriver("1", driver1);
			addDriver("2", driver2);
			
			createHandler();
			
			nasAccthandler.handleRequest(request, response, null);
			
			verify(driver1).handleAccountingRequest(request);
			
			nasAccthandler.handleRequest(request, response, null);
			
			verify(driver2).handleAccountingRequest(request);
		}
		
		@Test
		public void resultCodeIsDiameterSuccessWhenNASAccountingHandledSuccessfully() throws Exception{
			FakeDriver driver1 = Mockito.spy(new FakeDriver(aaaServerContext));
			
			addDriver("1", driver1);
			
			createHandler();
			
			nasAccthandler.handleRequest(request, response, null);
			
			responseHasResultCode(ResultCode.DIAMETER_SUCCESS);
		}
		
		@Test
		public void nasAccountingDriverExecutedSuccessfully() throws Exception {
			FakeDriver driver1 = Mockito.spy(new FakeDriver(aaaServerContext));
			
			addDriver("1", driver1);
			
			createHandler();
			
			nasAccthandler.handleRequest(request, response, null);
			
			verify(driver1).handleAccountingRequest(request);
		}
		
		@Test
		public void ignoresScriptExecutionIfScriptNameIsNotSet() throws Exception {
			
			FakeDriver driver1 = Mockito.spy(new FakeDriver(aaaServerContext));
			
			addDriver("1", driver1);
			
			policyConfiguration.getNasAcctDetail().setScript(null);
			createHandler();
			
			nasAccthandler.handleRequest(request, response, null);
			
			Mockito.verifyZeroInteractions(diameterServiceContext.getServerContext().getExternalScriptsManager());
		}
		
		@Test
		public void ignoresScriptExecutionIfScriptNameIsBlank() throws Exception {
			
			FakeDriver driver1 = Mockito.spy(new FakeDriver(aaaServerContext));
			
			addDriver("1", driver1);
			
			policyConfiguration.getNasAcctDetail().setScript(" ");
			createHandler();
			nasAccthandler.handleRequest(request, response, null);
			
			Mockito.verify(diameterServiceContext.getServerContext().getExternalScriptsManager(), never())
			.execute("SampleDriverScript", DriverScript.class, "postDriverProcessing", 
					new Class<?>[]{ServiceRequest.class, ServiceResponse.class}, new Object[]{request, response});
		}
		
		@Test 
		public void driverScriptPreHookIsCalledFollowedByPostHook() throws Exception  {
			FakeDriver driver1 = Mockito.spy(new FakeDriver(aaaServerContext));
			
			addDriver("1", driver1);
			
			createHandler();
			
			nasAccthandler.handleRequest(request, response, null);
			
			InOrder order = inOrder(diameterServiceContext.getServerContext().getExternalScriptsManager(), driver1);

			order.verify(diameterServiceContext.getServerContext().getExternalScriptsManager())
			.execute("SampleDriverScript", DriverScript.class, "preDriverProcessing", 
					new Class<?>[]{ServiceRequest.class, ServiceResponse.class}, new Object[]{request, response});
			
			order.verify(driver1).handleAccountingRequest(request);
			
			order.verify(diameterServiceContext.getServerContext().getExternalScriptsManager())
			.execute("SampleDriverScript", DriverScript.class, "postDriverProcessing", 
					new Class<?>[]{ServiceRequest.class, ServiceResponse.class}, new Object[]{request, response});
			
		}
		
		@Test
		public void preHookOfScriptIsExecuted() throws Exception {
			FakeDriver driver1 = Mockito.spy(new FakeDriver(aaaServerContext));
			
			addDriver("1", driver1);
			
			createHandler();
			
			nasAccthandler.handleRequest(request, response, null);
			
			verify(diameterServiceContext.getServerContext().getExternalScriptsManager())
				.execute("SampleDriverScript", DriverScript.class, "preDriverProcessing", new Class<?>[]{ServiceRequest.class, ServiceResponse.class}, new Object[]{request, response});
		}
		
		@Test
		public void postHookOfScriptIsExecuted() throws Exception {
			FakeDriver driver1 = Mockito.spy(new FakeDriver(aaaServerContext));
			
			addDriver("1", driver1);
			
			createHandler();
			
			nasAccthandler.handleRequest(request, response, null);
			
			verify(diameterServiceContext.getServerContext().getExternalScriptsManager())
			.execute("SampleDriverScript",DriverScript.class, "postDriverProcessing", new Class<?>[]{ServiceRequest.class, ServiceResponse.class}, new Object[]{request, response});
		}
	}
	
	public class AccountingRecordNumberAbsentInRequest {
		
		@Test
		public void updatesResultCodeToDiameterOutOfSpace() throws Exception {
			FakeDriver driver1 = Mockito.spy(new FakeDriver(aaaServerContext));
			
			addDriver("1", driver1);
			
			createHandler();

			nasAccthandler.handleRequest(request, response, null);
			responseHasResultCode(ResultCode.DIAMETER_OUT_OF_SPACE);
		}
		
		@Test
		public void hintsSystemToSkipFurtherProcessing() throws Exception {
			FakeDriver driver1 = Mockito.spy(new FakeDriver(aaaServerContext));
			
			addDriver("1", driver1);
			
			createHandler();

			nasAccthandler.handleRequest(request, response, null);
			Assert.assertFalse(response.isFurtherProcessingRequired());
		}
	}
	
	private void createHandler() throws InitializationFailedException {
		nasAccthandler = new NasAcctHandler(diameterServiceContext, policyConfiguration);
		nasAccthandler.init();
	}
	
	private void addDriver(String id, IEliteAcctDriver driver1) {
		driverIdToWeightage.put(id, 1);
		driverIdToDriver.put(id, driver1);
	}
	
	private void createRequestAndResponse() {
		diameterRequest =  new DiameterRequest();
		request =  new ApplicationRequest(diameterRequest);
		response =  new ApplicationResponse(diameterRequest);
	}
	
	private void responseHasResultCode(ResultCode resultCode) {
		DiameterAssertion.assertThat(response.getDiameterAnswer())
		.hasResultCode(resultCode);
	}
	
	private class FakeDriver extends BaseAcctDriver {
		
		public FakeDriver(ServerContext serverContext) {
			super(serverContext);
		}

		@Override
		public void scan() {
			
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public String getTypeName() {
			return "FAKE_DRIVER";
		}

		@Override
		public int getType() {
			return 0;
		}

		@Override
		protected void handleServiceRequest(ServiceRequest serviceRequest)
				throws DriverProcessFailedException {
			
		}

		@Override
		protected void initInternal() throws TransientFailureException,
				DriverInitializationFailedException {
			
		}

		@Override
		protected int getStatusCheckDuration() {
			return 0;
		}
	}
}
