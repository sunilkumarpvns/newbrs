package com.elitecore.netvertex.gateway;

import java.util.Arrays;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.corenetvertex.constants.GatewayTypeConstant;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayController;
import com.elitecore.netvertex.gateway.radius.RadiusGatewayController;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class GatewayMediatorTest {


	@Mock private SessionLocator sessionLocator;
	@Mock private DiameterGatewayController diameterGatewayController;
	@Mock private RadiusGatewayController radiusGatewayController;

	public @Rule ExpectedException exception = ExpectedException.none();

	private GatewayMediator gatewayMediator;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(sessionLocator.getCoreSessionCriteria()).thenReturn(Mockito.mock(Criteria.class));
		gatewayMediator = new GatewayMediator(sessionLocator);
		gatewayMediator.setDiameterGatewayController(diameterGatewayController);
		gatewayMediator.setRadiusGatewayController(radiusGatewayController);
	}


	public Object[][] data_for_reAuthorization_should_call_proper_receiver_based_on_session_type() {

		Object[][] datas = new Object[SessionTypeConstant.values().length][];

		int index = 0;
		for (SessionTypeConstant sessionTypeConstant : SessionTypeConstant.values()) {
			datas[index++] = new Object[] { sessionTypeConstant};
		}

		return datas;
	}

	@Test
	@Parameters(method="data_for_reAuthorization_should_call_proper_receiver_based_on_session_type")
	public void test_reAuthorization_should_call_proper_receiver_based_on_session_type(SessionTypeConstant sessionTypeConstant) throws Exception {

		SessionData sessionData = new SessionDataImpl("temp");

		sessionData.addValue(PCRFKeyConstants.CS_SESSION_TYPE.val, sessionTypeConstant.val);

		when(sessionLocator.getCoreSessionList(Mockito.<Criteria>any())).thenReturn(Arrays.asList(sessionData));

		gatewayMediator.reauthorize(PCRFKeyConstants.CS_CORESESSION_ID, "abc", "test", false,null);

		if(sessionTypeConstant.gatewayType == GatewayTypeConstant.DIAMETER) {

			Mockito.verify(diameterGatewayController).handleSessionReAuthorization(Mockito.any(PCRFRequest.class));

		} else {

			Mockito.verify(radiusGatewayController).handleSessionReAuthorization(Mockito.any(PCRFRequest.class));
		}
	}


	@Test
	public void test_reAuthorization_should_not_call_receiver_if_session_type_not_found_in_session() throws Exception {

		SessionData sessionData = new SessionDataImpl("temp");

		when(sessionLocator.getCoreSessionList(Mockito.<Criteria>any())).thenReturn(Arrays.asList(sessionData));

		gatewayMediator.reauthorize(PCRFKeyConstants.CS_CORESESSION_ID, "abc", "test", false,null);

		Mockito.verify(diameterGatewayController, Mockito.never()).handleSessionReAuthorization(Mockito.any(PCRFRequest.class));
		Mockito.verify(radiusGatewayController, Mockito.never()).handleSessionReAuthorization(Mockito.any(PCRFRequest.class));

	}


	@Test
	public void test_reAuthorization_should_not_call_receiver_if_session_type_is_invalid() throws Exception {

		SessionData sessionData = new SessionDataImpl("temp");

		sessionData.addValue(PCRFKeyConstants.CS_SESSION_TYPE.val, "xadfdsa");

		when(sessionLocator.getCoreSessionList(Mockito.<Criteria>any())).thenReturn(Arrays.asList(sessionData));

		gatewayMediator.reauthorize(PCRFKeyConstants.CS_CORESESSION_ID, "abc", "test", false,null);

		Mockito.verify(diameterGatewayController, Mockito.never()).handleSessionReAuthorization(Mockito.any(PCRFRequest.class));
		Mockito.verify(radiusGatewayController, Mockito.never()).handleSessionReAuthorization(Mockito.any(PCRFRequest.class));

	}


	@Test
	public void test_reAuthorization_should_not_call_receiver_if_session_not_found() throws Exception {

		gatewayMediator.reauthorize(PCRFKeyConstants.CS_CORESESSION_ID, "abc", "test", false,null);

		Mockito.verify(diameterGatewayController, Mockito.never()).handleSessionReAuthorization(Mockito.any(PCRFRequest.class));
		Mockito.verify(radiusGatewayController, Mockito.never()).handleSessionReAuthorization(Mockito.any(PCRFRequest.class));

	}

	@Test
	public void test_reAuthorization_should_not_call_receiver_if_null_session_found() throws Exception {

		when(sessionLocator.getCoreSessionList(Mockito.<Criteria>any())).thenReturn(null);

		gatewayMediator.reauthorize(PCRFKeyConstants.CS_CORESESSION_ID, "abc", "test", false,null);

		Mockito.verify(diameterGatewayController, Mockito.never()).handleSessionReAuthorization(Mockito.any(PCRFRequest.class));
		Mockito.verify(radiusGatewayController, Mockito.never()).handleSessionReAuthorization(Mockito.any(PCRFRequest.class));

	}

	@Test
	public void test_reAuthorization_should_not_call_when_sessionException_occured() throws Exception {

		when(sessionLocator.getCoreSessionCriteria()).thenThrow(new com.elitecore.core.serverx.sessionx.SessionException("Manual exception"));

		gatewayMediator.reauthorize(PCRFKeyConstants.CS_CORESESSION_ID, "abc", "test", false,null);

		Mockito.verify(diameterGatewayController, Mockito.never()).handleSessionReAuthorization(Mockito.any(PCRFRequest.class));
		Mockito.verify(radiusGatewayController, Mockito.never()).handleSessionReAuthorization(Mockito.any(PCRFRequest.class));

	}

	@Test
	public void test_reAuthorization_should_add_forcefulReAuth_attribute_in_pcrfRequest_when_passed_enabled() throws Exception {

		SessionData sessionData = new SessionDataImpl("temp");

		sessionData.addValue(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GX.val);

		when(sessionLocator.getCoreSessionList(Mockito.<Criteria>any())).thenReturn(Arrays.asList(sessionData));

        ArgumentCaptor<PCRFRequest> actualPCRFRequest = ArgumentCaptor.forClass(PCRFRequest.class);

		gatewayMediator.reauthorize(PCRFKeyConstants.CS_CORESESSION_ID, "test", "abc", true, null);

		verify(diameterGatewayController).handleSessionReAuthorization(actualPCRFRequest.capture());


        assertNotNull(actualPCRFRequest.getValue());
        assertEquals("Mediator should add SESSION-RE-AUTH key with value true", "FORCEFUL", actualPCRFRequest.getValue().getAttribute(PCRFKeyConstants.SESSION_RE_AUTH.val));

	}

    @Test
    public void test_reAuthorization_addReAuthCauseAttributeInPCRFRequest() throws Exception {

        SessionData sessionData = new SessionDataImpl("temp");

        sessionData.addValue(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GX.val);

        when(sessionLocator.getCoreSessionList(Mockito.<Criteria>any())).thenReturn(Arrays.asList(sessionData));

        ArgumentCaptor<PCRFRequest> actualPCRFRequest = ArgumentCaptor.forClass(PCRFRequest.class);

        gatewayMediator.reauthorize(PCRFKeyConstants.CS_CORESESSION_ID, "test", "abc", true, null);

        verify(diameterGatewayController).handleSessionReAuthorization(actualPCRFRequest.capture());

        assertNotNull(actualPCRFRequest.getValue());
        assertEquals("Mediator should add SESSION-RE-CAUSE key with value test", "abc", actualPCRFRequest.getValue().getAttribute(PCRFKeyConstants.RE_AUTH_CAUSE.val));

    }

}
