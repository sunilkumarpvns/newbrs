package com.elitecore.aaa.diameter.service.application.handlers;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.plugins.core.DiameterPluginManager;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterPluginHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.PluginEntryDataBuilder;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.plugins.DiameterPlugin;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class DiameterPluginHandlerTest {

	private static final String DUMMY_ARGUMENT = "DUMMY_ARGUMENT";
	
	private DiameterPluginHandlerData diameterPluginHandlerData;
	private DiameterPluginHandler diameterPluginHandler;
	private Map<String, DiameterPlugin> nameToPluginMap = new HashMap<String, DiameterPlugin>();

	private ApplicationRequest request;
	private ApplicationResponse response;
	private DiameterRequest diameterRequest;
	private RadiusRequestExecutor<ApplicationRequest, ApplicationResponse> executor;

	@Mock private DiameterServiceContext diameterServiceContext;
	@Mock private AAAServerContext aaaServerContext;
	@Mock private DiameterPluginManager diameterPluginManager;
	@Mock private DiameterPlugin plugin1;
	@Mock private DiameterPlugin plugin2;
	
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();


	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		diameterRequest = new DiameterRequest();
		request = new ApplicationRequest(diameterRequest);
		response = new ApplicationResponse(diameterRequest);

		diameterPluginHandlerData = new DiameterPluginHandlerData();
		requestWithUsername("test");
		createHandler();
	}
	
	@Test
	public void pluginIsAppliedIfNoRulesetConfigured() throws Exception {
		addPluginEntry(anEntry().withRuleset(null).pluginName("plugin1").onResponse());
		
		createHandler();
		
		diameterPluginHandler.handleRequest(request, response, ISession.NO_SESSION);

		verify(plugin1).handleOutMessage(eq(diameterRequest), eq(response.getDiameterAnswer()),
				eq(ISession.NO_SESSION), eq(DUMMY_ARGUMENT), any(PluginCallerIdentity.class));
	}
	
	@Test
	public void pluginIsAppliedIfRulesetIsSatisfied() throws Exception {
		addPluginEntry(anEntry().withRuleset("0:1=\"test\"").onResponse());
		
		requestWithUsername("test");
		
		createHandler();
		
		diameterPluginHandler.handleRequest(request, response, ISession.NO_SESSION);
		
		verify(plugin1).handleOutMessage(eq(diameterRequest), eq(response.getDiameterAnswer()),
				eq(ISession.NO_SESSION), eq(DUMMY_ARGUMENT), any(PluginCallerIdentity.class));
	}
	
	@Test
	public void entriesAreSkippedIfRulesetIsNotSatisfied() throws Exception {
		addPluginEntry(anEntry().withRuleset("0:1=\"test1\"").onResponse().pluginName("plugin1"));
		addPluginEntry(anEntry().withRuleset("0:4=\"1.1.1.1\"").onResponse().pluginName("plugin2"));
		
		request.getDiameterRequest().addAvp(DiameterAVPConstants.NAS_IP_ADDRESS, "2.2.2.2");
		requestWithUsername("test1");
		
		createHandler();
		
		diameterPluginHandler.handleRequest(request, response, ISession.NO_SESSION);

		verify(plugin1).handleOutMessage(eq(diameterRequest), eq(response.getDiameterAnswer()),
				eq(ISession.NO_SESSION), eq(DUMMY_ARGUMENT), any(PluginCallerIdentity.class));
		verifyZeroInteractions(plugin2);
	}
	
	@Test
	public void pluginsAreAppliedInSameSequenceAsConfigured() throws Exception {
		addPluginEntry(anEntry().withRuleset("0:1=\"test\"").onResponse().pluginName("plugin1"));
		addPluginEntry(anEntry().withRuleset("0:1=\"test\"").onResponse().pluginName("plugin2"));
		
		requestWithUsername("test");
		createHandler();
		
		diameterPluginHandler.handleRequest(request, response, ISession.NO_SESSION);

		InOrder order = inOrder(plugin1, plugin2);
		order.verify(plugin1).handleOutMessage(eq(diameterRequest), eq(response.getDiameterAnswer()),
				eq(ISession.NO_SESSION), eq(DUMMY_ARGUMENT), any(PluginCallerIdentity.class));
		order.verify(plugin2).handleOutMessage(eq(diameterRequest), eq(response.getDiameterAnswer()),
				eq(ISession.NO_SESSION), eq(DUMMY_ARGUMENT), any(PluginCallerIdentity.class));
	}
	
	@Test
	public void appliesPluginInMethodIfRequestIsConfiguredInPluginEntry() throws Exception {
		addPluginEntry(anEntry().withRuleset("0:1=\"test\"").pluginName("plugin1").onRequest());
		
		createHandler();
	
		diameterPluginHandler.handleRequest(request, response, ISession.NO_SESSION);

		verify(plugin1).handleInMessage(eq(diameterRequest), eq(response.getDiameterAnswer()),
				eq(ISession.NO_SESSION), eq(DUMMY_ARGUMENT), any(PluginCallerIdentity.class));

	}

	@Test
	public void appliesPluginOutMethodIfResponseIsConfiguredInPluginEntry() throws Exception {
		addPluginEntry(anEntry().withRuleset("0:1=\"test\"").pluginName("plugin1").onResponse());

		createHandler();
		
		diameterPluginHandler.handleRequest(request, response, ISession.NO_SESSION);

		verify(plugin1).handleOutMessage(eq(diameterRequest), eq(response.getDiameterAnswer()),
				eq(ISession.NO_SESSION), eq(DUMMY_ARGUMENT), any(PluginCallerIdentity.class));
	}
	
	@Test
	public void continuesProcessingOtherEntriesIfPluginExecutionOfAnyEntryFails() throws InitializationFailedException {
		addPluginEntry(anEntry().withRuleset("0:1=\"test\"").pluginName("plugin1").onResponse());
		addPluginEntry(anEntry().withRuleset("0:1=\"test\"").pluginName("plugin2").onResponse());
		
		createHandler();
		
		doThrow(new RuntimeException()).when(plugin1).handleOutMessage(eq(diameterRequest), eq(response.getDiameterAnswer()),
				eq(ISession.NO_SESSION), eq(DUMMY_ARGUMENT), any(PluginCallerIdentity.class));
		
		diameterPluginHandler.handleRequest(request, response, ISession.NO_SESSION);
		
		verify(plugin2).handleOutMessage(eq(diameterRequest), eq(response.getDiameterAnswer()),
				eq(ISession.NO_SESSION), eq(DUMMY_ARGUMENT), any(PluginCallerIdentity.class));
	}

	@Test
	public void defaultResponseBehaviourIsAlwaysDisabled() throws Exception {
		addPluginEntry(anEntry().withRuleset("0:1=\"test\"").onRequest());
		
		createHandler();

		assertFalse(diameterPluginHandler.isResponseBehaviorApplicable());
	}

	@Test
	public void isAlwaysEligible() throws Exception {
		addPluginEntry(anEntry().withRuleset("0:1=\"test\"").onRequest());
		
		createHandler();
		
		assertTrue(diameterPluginHandler.isEligible(request, response));
	}

	private void addPluginEntry(PluginEntryDataBuilder entryBuilder) {
		diameterPluginHandlerData.setPolicyName("Test");
		diameterPluginHandlerData.getPluginEntries().add(entryBuilder.pluginEntryData);
	}
	
	private static PluginEntryDataBuilder anEntry() {
		return new PluginEntryDataBuilder();
	}
	
	private void requestWithUsername(String userName) {
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.USER_NAME, diameterRequest, userName);
	}
	
	private void createHandler() throws InitializationFailedException {
		nameToPluginMap.put("plugin1", plugin1);
		nameToPluginMap.put("plugin2", plugin2);
		doReturn(aaaServerContext).when(diameterServiceContext).getServerContext();
		doReturn(diameterPluginManager).when(aaaServerContext).getDiameterPluginManager();
		doReturn(nameToPluginMap).when(diameterPluginManager).getNameToPluginMap();
		
		diameterPluginHandler = new DiameterPluginHandler(diameterServiceContext, diameterPluginHandlerData);

		diameterPluginHandler.init();
		
		executor = new RadiusRequestExecutor<ApplicationRequest, ApplicationResponse>(diameterPluginHandler, request, response);
		request.setExecutor(executor);
	}

}
