package com.elitecore.aaa.diameter.service.application.listener;

import static org.mockito.Mockito.verify;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.conf.impl.DiameterNasServiceConfigurable;
import com.elitecore.aaa.diameter.plugins.core.DiameterPluginManager;
import com.elitecore.aaa.diameter.policies.applicationpolicy.NasAppPolicy;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.stack.AppListenerInitializationFaildException;
import com.elitecore.diameterapi.plugins.DiameterPlugin;

public class NasApplicationListenerTest {
	@Mock private DiameterNasServiceConfigurable nasApplicationConfiguration;
	@Mock private DiameterSessionManager diameterSessionManager;
	@Mock private DiameterSession session;
	@Mock private DiameterServiceContext serviceContext;
	@Mock private NasAppPolicy nasPolicy;
	@Mock private AAAServerContext aaaServerContext;
	@Mock private DiameterPluginManager diaPluginManager;
	@Mock private SessionFactoryManager sessionFactoryManager;
	private ApplicationRequest request;
	private ApplicationResponse response;
	private DiameterRequest diameterRequest;
	private NasApplicationListener nasAppListener;

	@Before
	public void setUp() throws AppListenerInitializationFaildException {
		MockitoAnnotations.initMocks(this);
		DummyDiameterDictionary.getInstance();
		setUpRequestAndResponse();
		setApplicationIdentifier();
		setUpPolicy();
		nasAppListener = new NasApplicationListener(serviceContext, null, nasApplicationConfiguration,
				diameterSessionManager, sessionFactoryManager);
		nasAppListener.init();
	}

	private void setUpRequestAndResponse() {
		diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.SESSION_TERMINATION.code);
		diameterRequest.addAvp(DiameterAVPConstants.SESSION_ID, "session1");
		request = new ApplicationRequest(diameterRequest);
		response = new ApplicationResponse(diameterRequest);
	}
	
	private void setApplicationIdentifier() {
		Mockito.when(nasApplicationConfiguration.getSupportedApplication()).thenReturn(ApplicationIdentifier.NASREQ);
	}
	
	private void setUpPolicy() {
		Mockito.when(serviceContext.selectServicePolicy(request)).thenReturn(nasPolicy);
		Mockito.when(nasPolicy.isSessionManagementEnabled()).thenReturn(true);
		pulginRegistrationSetUp();
	}
	
	private void pulginRegistrationSetUp() {
		Mockito.when(serviceContext.getServerContext()).thenReturn(aaaServerContext);
		Mockito.when(aaaServerContext.getDiameterPluginManager()).thenReturn(diaPluginManager);
		Mockito.when(diaPluginManager.getNameToPluginMap()).thenReturn(new HashMap<String, DiameterPlugin>());
		Mockito.when(nasApplicationConfiguration.getInPlugins()).thenReturn(null);
		Mockito.when(nasApplicationConfiguration.getOutPlugins()).thenReturn(null);
	}

	@Test
	public void removesRelaventDBSessionWhenStrIsReceived() throws AppListenerInitializationFaildException {
		nasAppListener.handleApplicationRequest(session, request, response);
		nasAppListener.finalPreResponseProcessing(request, response, session);

		verify(diameterSessionManager).delete(request.getDiameterRequest(),response.getDiameterAnswer());
	}

}
