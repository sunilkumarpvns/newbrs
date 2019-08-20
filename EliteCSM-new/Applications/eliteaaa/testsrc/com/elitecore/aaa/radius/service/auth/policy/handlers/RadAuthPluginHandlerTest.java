package com.elitecore.aaa.radius.service.auth.policy.handlers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.unitils.reflectionassert.ReflectionAssert;

import com.elitecore.aaa.radius.plugins.core.RadPluginRequestHandler;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyData;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.PluginEntryDataBuilder;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.RadPluginHandlerData;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.plugins.data.PluginMode;
import com.elitecore.core.commons.plugins.data.ServicePolicyFlow;
import com.elitecore.core.commons.plugins.data.ServiceTypeConstants;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

/***
 * @author soniya
 */
public class RadAuthPluginHandlerTest {

	private static final String PLUGIN2_NAME = "plugin2";
	private static final String PLUGIN1_NAME = "plugin1";
	private static final String DUMMY_ARGUMENT = "DUMMY_ARGUMENT";
	
	private RadAuthPluginHandler radAuthPluginHandler;
	private RadAuthRequest request;
	private RadAuthResponse response;
	private RadAuthRequestBuilder radAuthBuilder;
	private RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> executor;

	@Mock private RadAuthServiceContext serviceContext;
	@Mock private RadPluginRequestHandler pluginRequestHandler1;
	@Mock private RadPluginRequestHandler pluginRequestHandler2;
	private RadPluginHandlerData radPluginHandlerData;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		setRadPluginHandlerData();
		createRequestAndResponse();
	}

	@Test
	public void isAlwaysEligible() throws Exception {
		createRadAuthPlugin();

		assertTrue(radAuthPluginHandler.isEligible(request, response));
	}

	@Test
	public void defaultResponseBehaviourIsAlwaysDisabled() throws Exception {
		createRadAuthPlugin();

		assertFalse(radAuthPluginHandler.isResponseBehaviorApplicable());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void pluginIsAppliedIfNoRulesetConfigured() throws Exception {
		addPluginEntry(anEntry().withRuleset(null).onRequest().pluginName(PLUGIN1_NAME));

		when(serviceContext.createPluginRequestHandler(Mockito.anyList(), Mockito.anyList()))
		.thenReturn(pluginRequestHandler1);

		requestWithUsername("test");

		createRadAuthPlugin();

		radAuthPluginHandler.handleRequest(request, response, ISession.NO_SESSION);

		verify(pluginRequestHandler1).handlePreRequest(request, response, ISession.NO_SESSION);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void pluginIsAppliedIfRulesetIsSatisfied() throws Exception {
		addPluginEntry(anEntry().withRuleset("0:1=\"test\"").onRequest().pluginName(PLUGIN1_NAME));
		when(serviceContext.createPluginRequestHandler(Mockito.anyList(), Mockito.anyList()))
		.thenReturn(pluginRequestHandler1);

		requestWithUsername("test");
		createRadAuthPlugin();

		radAuthPluginHandler.handleRequest(request, response, ISession.NO_SESSION);

		verify(pluginRequestHandler1).handlePreRequest(request, response, ISession.NO_SESSION);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void entriesAreSkippedIfRulesetIsNotSatisfied() throws Exception {
		Answer<RadPluginRequestHandler> answer = createAnswer();

		addPluginEntry(anEntry().withRuleset("0:1=\"test\"").onRequest().pluginName(PLUGIN1_NAME));
		addPluginEntry(anEntry().withRuleset("0:1=\"test1\"").onRequest().pluginName(PLUGIN2_NAME));

		when(serviceContext.createPluginRequestHandler(Mockito.anyList(), Mockito.anyList())).thenAnswer(answer);

		requestWithUsername("test");
		createRadAuthPlugin();

		radAuthPluginHandler.handleRequest(request, response, ISession.NO_SESSION);

		verify(pluginRequestHandler1).handlePreRequest(request, response, ISession.NO_SESSION);
		verifyZeroInteractions(pluginRequestHandler2);
	}

	private Answer<RadPluginRequestHandler> createAnswer() {
		Answer<RadPluginRequestHandler> answer = new Answer<RadPluginRequestHandler>() {
			int count = 0;
			@Override
			public RadPluginRequestHandler answer(InvocationOnMock invocation) throws Throwable {
				if (count == 0) {
					count++;
					return pluginRequestHandler1;
				} else {
					return pluginRequestHandler2;
				}
			}
		};
		return answer;
	}

	@Test
	public void pluginsAreAppliedInSameSequenceAsConfigured() throws Exception {
		Answer<RadPluginRequestHandler> answer = createAnswer();

		addPluginEntry(anEntry().withRuleset("0:1=\"test\"").onRequest().pluginName(PLUGIN1_NAME));
		addPluginEntry(anEntry().withRuleset("0:1=\"test\"").onResponse().pluginName(PLUGIN2_NAME));

		ArgumentCaptor<List<PluginEntryDetail>> arg1 = listArgumentCaptorFor(PluginEntryDetail.class);
		ArgumentCaptor<List<PluginEntryDetail>> arg2 = listArgumentCaptorFor(PluginEntryDetail.class);

		when(serviceContext.createPluginRequestHandler(arg1.capture(), arg2.capture())).thenAnswer(answer);

		requestWithUsername("test");
		createRadAuthPlugin();

		radAuthPluginHandler.handleRequest(request, response, ISession.NO_SESSION);

		verify(pluginRequestHandler1).handlePreRequest(request, response, ISession.NO_SESSION);
		verify(pluginRequestHandler2).handlePostRequest(request, response, ISession.NO_SESSION);

		assertPluginEntry(createPluginEntryForPlugin1(), arg1.getAllValues().get(0).get(0));
		assertPluginEntry(createPluginEntryForPlugin2(), arg2.getAllValues().get(1).get(0));

		assertThat(arg2.getAllValues().get(0).isEmpty(), is(true));
		assertThat(arg1.getAllValues().get(1).isEmpty(), is(true));
	}

	private void assertPluginEntry(PluginEntryDetail expectedPluginEntryDetail,
			PluginEntryDetail pluginEntryDetail) {
		ReflectionAssert.assertReflectionEquals(expectedPluginEntryDetail, pluginEntryDetail);
	}

	private PluginEntryDetail createPluginEntryForPlugin2() {
		PluginEntryDetail data = new PluginEntryDetail();
		data.setPluginName(PLUGIN2_NAME);
		data.setPluginArgument(DUMMY_ARGUMENT);
		data.setCallerId(getCallerId(PluginMode.POST, 1, PLUGIN2_NAME));
		return data;
	}

	private PluginEntryDetail createPluginEntryForPlugin1() {
		PluginEntryDetail data = new PluginEntryDetail();
		data.setPluginName(PLUGIN1_NAME);
		data.setPluginArgument(DUMMY_ARGUMENT);
		data.setCallerId(getCallerId(PluginMode.PRE, 0, PLUGIN1_NAME));
		return data;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void invokesPrePluginMethodIfRequestIsConfiguredInPluginEntry() throws Exception {
		addPluginEntry(anEntry().withRuleset("0:1=\"*\"").onRequest().pluginName(PLUGIN1_NAME));
		when(serviceContext.createPluginRequestHandler(Mockito.anyList(), Mockito.anyList())).thenReturn(pluginRequestHandler1);

		requestWithUsername("test");
		createRadAuthPlugin();

		radAuthPluginHandler.handleRequest(request, response, ISession.NO_SESSION);

		verify(pluginRequestHandler1).handlePreRequest(request, response, ISession.NO_SESSION);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void invokesPostPluginMethodIfResponseIsConfiguredInPluginEntry() throws Exception {
		addPluginEntry(anEntry().withRuleset("0:1=\"*\"").onResponse().pluginName(PLUGIN1_NAME));
		when(serviceContext.createPluginRequestHandler(Mockito.anyList(), Mockito.anyList())).thenReturn(pluginRequestHandler1);

		requestWithUsername("test");
		createRadAuthPlugin();

		radAuthPluginHandler.handleRequest(request, response, ISession.NO_SESSION);

		verify(pluginRequestHandler1).handlePostRequest(request, response, ISession.NO_SESSION);
	}

	@SuppressWarnings("unchecked")
	private static <T> ArgumentCaptor<List<T>> listArgumentCaptorFor(Class<T> clazz) {
		ArgumentCaptor<List<T>> listCaptor = (ArgumentCaptor<List<T>>) ArgumentCaptor.forClass((Class<T>) List.class);
		return listCaptor;
	}

	private void createRadAuthPlugin() throws InitializationFailedException {
		radAuthPluginHandler =  new RadAuthPluginHandler(serviceContext, radPluginHandlerData);

		radAuthPluginHandler.init();

		executor = new RadiusRequestExecutor<RadAuthRequest, RadAuthResponse>(radAuthPluginHandler, request, response);
		request.setExecutor(executor);
	}

	private void addPluginEntry(PluginEntryDataBuilder entryBuilder) {
		radPluginHandlerData.getPluginEntries().add(entryBuilder.pluginEntryData);
	}

	private static PluginEntryDataBuilder anEntry() {
		return new PluginEntryDataBuilder();
	}

	private void createRequestAndResponse() throws Exception {

		radAuthBuilder = new RadAuthRequestBuilder()
		.packetType(2);
		request = radAuthBuilder.build();

		response = new RadAuthRequestBuilder()
		.addAttribute("0:18", "access accept")
		.packetType(2)
		.buildResponse();
	}

	private void setRadPluginHandlerData() {
		RadiusServicePolicyData radiusServicePolicyData = new RadiusServicePolicyData();
		radiusServicePolicyData.setName("test");
		radPluginHandlerData = new RadPluginHandlerData();
		radPluginHandlerData.setHandlerName("plugin-handler");
		radPluginHandlerData.setRadiusServicePolicyData(radiusServicePolicyData);
	}

	private void requestWithUsername(String userName) throws Exception {
		request = radAuthBuilder.addAttribute("0:1", userName).build();
	}

	private PluginCallerIdentity getCallerId(PluginMode mode, int index, String pluginName) {
		return PluginCallerIdentity.createAndGetIdentity(ServiceTypeConstants.RAD_AUTH, mode, index, pluginName)
				.setServicePolicyName(radPluginHandlerData.getPolicyName()).setServicePolicyFlow(ServicePolicyFlow.AUTH_FLOW)
				.setPluginHandlerName(radPluginHandlerData.getHandlerName()).getId();
	}
}
