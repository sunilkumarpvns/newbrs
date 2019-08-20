package com.elitecore.diameterapi.diameter.stack.application;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.routerx.agent.DiameterTestSupport;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.stack.application.sessionrelease.AppDefaultSessionReleaseIndicator;

/**
 * 
 * @author narendra.pathai
 * @author sanjay.dhamelia
 *
 */
public class ServerApplicationTest extends DiameterTestSupport {

	@Rule public PrintMethodRule printMethodRule = new PrintMethodRule();
	
	private ServerApplication serverApplication = new ServerApplication(getStackContext(), ApplicationIdentifier.CC) {
		
		@Override
		public String getApplicationIdentifier() {
			return ApplicationIdentifier.CC.name();
		}
		
		@Override
		protected SessionReleaseIndiactor createSessionReleaseIndicator(ApplicationEnum applicationEnum) {
			return new AppDefaultSessionReleaseIndicator();
		}
	};

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Test
	public void givenRequestIsAClientInitiatedRequest_ProcessApplicationRequestShouldAddSessionReleaseKey_ToBeRequestingHost_WhichIsMandatoryForSessionReleaseMechanismOnCERAndDPR() {
		DiameterRequest clientInitiatedRequest = createCCInitialRequest();
		serverApplication.processApplicationRequest(getSession(), clientInitiatedRequest);
		
		assertEquals(clientInitiatedRequest.getRequestingHost(), getSession().getParameter(DiameterSession.SESSION_RELEASE_KEY));
	}
	
	@Test
	public void givenRequestIsAClientInitiatedRequest_ProcessApplicationRequestShouldAddOriginHost_ToBeRequestingHost_WhichIsRequiredForServerInitiatedFlowOfRequests() {
		DiameterRequest clientInitiatedRequest = createCCInitialRequest();
		serverApplication.processApplicationRequest(getSession(), clientInitiatedRequest);
		
		assertEquals(clientInitiatedRequest.getRequestingHost(), getSession().getParameter(DiameterAVPConstants.ORIGIN_HOST));
	}
	
	@Test
	public void givenRequestIsAClientInitiatedRequestAndARequestForExistingSession_ProcessApplicationRequestShouldUpdateOriginHost_ToBeRequestingHost() {
		DiameterRequest clientInitiatedRequest = createCCInitialRequestFrom(getStackContext().getPeerData(ORIGINATOR_PEER_1[1]));
		serverApplication.processApplicationRequest(getSession(), clientInitiatedRequest);
		
		DiameterRequest clientInitatedUpdateRequest = createCCUpdateRequestFrom(getStackContext().getPeerData(ORIGINATOR_PEER_2[1]));
		serverApplication.processApplicationRequest(getSession(), clientInitatedUpdateRequest);
		
		assertEquals(clientInitatedUpdateRequest.getRequestingHost(), getSession().getParameter(DiameterAVPConstants.ORIGIN_HOST));
	}
	
	@Test
	public void givenRequestIsAServerInitiatedRequest_ProcessApplicationRequestShouldNotUpdateSessionReleaseKey() {
		DiameterRequest clientInitiatedRequest = createCCInitialRequest();
		serverApplication.processApplicationRequest(getSession(), clientInitiatedRequest);
		serverApplication.processApplicationRequest(getSession(), createRAR(getRouterContext().getPeerData(ROUTING_PEER[1])));
	
		assertEquals(clientInitiatedRequest.getRequestingHost(), getSession().getParameter(DiameterSession.SESSION_RELEASE_KEY));
	}
	
	@Test
	public void givenRequestIsAServerInitiatedRequest_ProcessApplicationRequestShouldNotUpdateOriginHost() {
		DiameterRequest clientInitiatedRequest = createCCInitialRequest();
		serverApplication.processApplicationRequest(getSession(), clientInitiatedRequest);
		serverApplication.processApplicationRequest(getSession(), createRAR(getRouterContext().getPeerData(ROUTING_PEER[1])));
		
		assertEquals(clientInitiatedRequest.getRequestingHost(), getSession().getParameter(DiameterAVPConstants.ORIGIN_HOST));
	}
}
