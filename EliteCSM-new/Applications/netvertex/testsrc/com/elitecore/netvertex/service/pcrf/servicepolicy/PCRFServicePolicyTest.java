package com.elitecore.netvertex.service.pcrf.servicepolicy;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.RequestAction;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.core.driver.spr.DriverConfiguration;
import com.elitecore.netvertex.service.pcrf.DummyPCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.PccServicePolicyConfiguration;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.impl.PccServicePolicyConfigurationImpl;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.ServiceHandlerFactory;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.ServiceHandlerType;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(JUnitParamsRunner.class)
public class PCRFServicePolicyTest {

    public static final String INR = "INR";
    @Mock private PccServicePolicyConfigurationImpl servicePolicyConfiguration;
	private DummyNetvertexServerContextImpl netVertexServerContext;
	private DummyNetvertexServerConfiguration netvertexServerConfiguration;
	@Mock private ServiceHandlerFactory serviceHandlerFactory;
	public @Rule ExpectedException exception = ExpectedException.none();
	
	private DummyPCRFServiceContext pcrfServiceContext;
	private PCRFServicePolicy pcrfServicePolicy;
	private SystemParameterConfiguration systemParameterConfiguration;

	@Before
	public void setUp() throws InitializationFailedException {
		MockitoAnnotations.initMocks(this);
		systemParameterConfiguration = new SystemParameterConfiguration();
		systemParameterConfiguration.setDeploymentMode(DeploymentMode.PCC);
		netVertexServerContext = new DummyNetvertexServerContextImpl();
		netvertexServerConfiguration = new DummyNetvertexServerConfiguration();
		netvertexServerConfiguration.setSystemParameterConfiguration(systemParameterConfiguration);
		netVertexServerContext.setNetvertexServerConfiguration(netvertexServerConfiguration);
		pcrfServiceContext = new DummyPCRFServiceContext();
		pcrfServiceContext.setServerContext(netVertexServerContext);
		pcrfServiceContext = spy(pcrfServiceContext);
		
		Map<Integer, Integer> syGatewaysMap = new HashMap<>();
		Map<Long, Integer> cdrDriverMap = new HashMap<Long, Integer>();
		
		syGatewaysMap.put(1, 1);
		cdrDriverMap.put(1L, 1);

        when(servicePolicyConfiguration.getIdentityAttribute()).thenReturn("USERNAME");

		doReturn(UUID.randomUUID().toString()).when(servicePolicyConfiguration).getSyGateway();
		doReturn(mock(DriverConfiguration.class)).when(servicePolicyConfiguration).getPolicyCdrDriver();

		pcrfServicePolicy = new PCRFServicePolicy(servicePolicyConfiguration, serviceHandlerFactory, pcrfServiceContext);
	}
	
	/**
	 * Passing all ServiceHandlerType in parameters
	 * 
	 */
	public Object[][] dataProvider_for_init_should_throw_InitializtionFailedException() throws InitializationFailedException{
		
		Object [][] serviceHandlerTypeParameters = new Object[ServiceHandlerType.values().length -1][];
		int i = 0;


		for(ServiceHandlerType type : ServiceHandlerType.values()){

			//FIXME REMOVE CHECK WHEN CHARGIN CDR SUPPORT PROVIDED
			if(ServiceHandlerType.CHARGING_CDR_HANDLER == type) {
				continue;
			}

			serviceHandlerTypeParameters[i++] = new Object[]{
				type	
			};
		}
		return serviceHandlerTypeParameters;
	}
	
	/**
	 * This test to check init method should throw InitializationFailedException by all handlers,
	 * 
	 * @param type
	 * @throws InitializationFailedException
	 */
	@Test
	@Parameters(method="dataProvider_for_init_should_throw_InitializtionFailedException")
	public void test_init_should_throw_InitializtionFailedException(ServiceHandlerType type) throws InitializationFailedException{
		ServiceHandlerFactory serviceHandlerFactory = mock(ServiceHandlerFactory.class);
		when(serviceHandlerFactory.serviceHandlerOf(Matchers.same(type), Mockito.any(PccServicePolicyConfiguration.class))).thenThrow(new InitializationFailedException() );
	
		exception.expect(InitializationFailedException.class);
		
		pcrfServicePolicy = new PCRFServicePolicy(servicePolicyConfiguration, serviceHandlerFactory, pcrfServiceContext);
		pcrfServicePolicy.init();
	}
	
	/**
	 * When IllegalArgumentException is thrown from ServiceHandlerFactory, it should be catch in init() and 
	 * only InitializationFailedException should be thrown.
	 * 
	 * @param type
	 * @throws Exception
	 */
	@Test @Parameters(method="dataProvider_for_init_should_throw_InitializtionFailedException")
	public void test_init_should_handle_IllegalArgumentException(ServiceHandlerType type) throws Exception {
		ServiceHandlerFactory serviceHandlerFactory = mock(ServiceHandlerFactory.class);
		when(serviceHandlerFactory.serviceHandlerOf(Matchers.same(type), Mockito.any(PccServicePolicyConfiguration.class))).thenThrow(new IllegalArgumentException());
	
		exception.expect(InitializationFailedException.class);
		
		pcrfServicePolicy = new PCRFServicePolicy(servicePolicyConfiguration, serviceHandlerFactory, pcrfServiceContext);
		pcrfServicePolicy.init();
	}
	
	
	/**
	 * When ServicePolicyAction is configured as Drop Request then it should not create any Service Handler
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_init_no_handler_should_created_when_Action_is_DropRequest() throws Exception {
		doReturn(RequestAction.DROP_REQUEST).when(servicePolicyConfiguration).getAction();
		
		ServiceHandlerFactory serviceHandlerFactory = mock(ServiceHandlerFactory.class);
		
		pcrfServicePolicy = new PCRFServicePolicy(servicePolicyConfiguration, serviceHandlerFactory,pcrfServiceContext);
		pcrfServicePolicy.init();
		
		verify(serviceHandlerFactory, never()).serviceHandlerOf(any(ServiceHandlerType.class), any(PccServicePolicyConfiguration.class));
	}
	
	
	public Object[][] dataProvider_For_test_init_when_identityAttribute_is_null_or_empty() {
		return new Object[][]{
				{
					""
				},
				{
					null
				}
		};
	}
	
	@Test @Parameters(method="dataProvider_For_test_init_when_identityAttribute_is_null_or_empty")
	public void test_init_should_throw_InitializationFailedException_when_identityAttribute_is_null_or_empty(String identityAttribute) throws Exception {
		doReturn(identityAttribute).when(servicePolicyConfiguration).getIdentityAttribute();
		
		ServiceHandlerFactory serviceHandlerFactory = mock(ServiceHandlerFactory.class);
		exception.expect(InitializationFailedException.class);
		exception.expectMessage("Missing Identity Attribute");
		
		pcrfServicePolicy = new PCRFServicePolicy(servicePolicyConfiguration, serviceHandlerFactory, pcrfServiceContext);
		pcrfServicePolicy.init();
		
		verify(serviceHandlerFactory, never()).serviceHandlerOf(any(ServiceHandlerType.class), any(PccServicePolicyConfiguration.class));
	}
	
	
	public Object[][] dataProvider_For_test_init_When_invalid_Ruleset_configured() {
		return new Object[][]{
				{
					"=Quota"
				},
				{
					"ffd="
				},
				{
					"seconds > && usage > 100 "
				},
				{
					"!usage"
				},
				{
					"abc == d"
				},				
		};
	}
	
	@Test @Parameters(method="dataProvider_For_test_init_When_invalid_Ruleset_configured")
	public void test_init_Should_throw_InitializationFailedException_When_invalid_RuleSet_configured(String ruleset) throws Exception {
		doReturn(ruleset).when(servicePolicyConfiguration).getRuleset();
		
		ServiceHandlerFactory serviceHandlerFactory = mock(ServiceHandlerFactory.class);
		exception.expect(InitializationFailedException.class);
		
		pcrfServicePolicy = new PCRFServicePolicy(servicePolicyConfiguration, serviceHandlerFactory, pcrfServiceContext);
		pcrfServicePolicy.init();
		
	}
	
	public Object[][] dataProvider_for_test_assignRequest() {
		return new Object[][]{
				{
					"\"a\" = \"a\"", true
				},
				{
					null, true
				},
				{
					"\"a\" = \"b\"", false
				}			
		};
	}
	
	@Test @Parameters(method="dataProvider_for_test_assignRequest")
	public void test_assignRequest_should_return_boolean_according_to_expression_configured(String ruleset, boolean expectedResult) throws Exception {
		doReturn(ruleset).when(servicePolicyConfiguration).getRuleset();
		
		pcrfServicePolicy = new PCRFServicePolicy(servicePolicyConfiguration, serviceHandlerFactory, pcrfServiceContext);
		pcrfServicePolicy.init();
		
		PCRFRequestImpl pcrfRequest = new PCRFRequestImpl();
		PCRFResponseImpl pcrfResponse = new PCRFResponseImpl();
		
		Assert.assertEquals("Request Assign Status : ", expectedResult, pcrfServicePolicy.assignRequest(pcrfRequest, pcrfResponse));
	}


	@Test
	public void test_PolicyHandlerShouldNot_created_when_DeployementMode_is_OCS() throws Exception {
		systemParameterConfiguration.setDeploymentMode(DeploymentMode.OCS);
		ServiceHandlerFactory serviceHandlerFactory = mock(ServiceHandlerFactory.class);

		pcrfServicePolicy = new PCRFServicePolicy(servicePolicyConfiguration, serviceHandlerFactory,pcrfServiceContext);
		pcrfServicePolicy.init();

		verify(serviceHandlerFactory, never()).serviceHandlerOf(eq(ServiceHandlerType.SUBSCRIBER_POLICY_HANDLER), any(PccServicePolicyConfiguration.class));
	}

	@Test
	public void test_DataRnCHandlerShouldNot_created_when_DeployementMode_is_PCRF() throws Exception {
		systemParameterConfiguration.setDeploymentMode(DeploymentMode.PCRF);
		ServiceHandlerFactory serviceHandlerFactory = mock(ServiceHandlerFactory.class);

		pcrfServicePolicy = new PCRFServicePolicy(servicePolicyConfiguration, serviceHandlerFactory,pcrfServiceContext);
		pcrfServicePolicy.init();

		verify(serviceHandlerFactory, never()).serviceHandlerOf(eq(ServiceHandlerType.DATA_RNC_HANDLER), any(PccServicePolicyConfiguration.class));
	}
}
