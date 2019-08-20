package com.elitecore.netvertex.ws;

import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.netvertex.EliteNetVertexServer.NetvertexGatewayController;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

@RunWith(HierarchicalContextRunner.class)
public class SessionReAuthProcessorTest {

	private SessionReAuthProcessor sessionReAuthProcessor;
	private NetvertexGatewayController gatewayController;

	@Before
	public void setUp() {
		this.gatewayController = mock(NetvertexGatewayController.class);
		this.sessionReAuthProcessor = new SessionReAuthProcessor(gatewayController);
	}

	private SessionDataImpl createSessionData(String sessionId) {
		SessionDataImpl sessionData = new SessionDataImpl("schemeName");
		sessionData.addValue(PCRFKeyConstants.CS_CORESESSION_ID.val, sessionId);
		return sessionData;
	}


	public class WhenSessionDoNotHaveIpAddress {

		private SessionData pcrfSession;
		private SessionData ocsSession;

		@Before
		public void setUp() {
			pcrfSession = createSessionData("Gx");
			ocsSession = createSessionData("Gy");
			pcrfSession.addValue(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GX.val);
			ocsSession.addValue(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GY.val);
			pcrfSession.addValue(PCRFKeyConstants.CS_SESSION_IPV4.val, null);
			ocsSession.addValue(PCRFKeyConstants.CS_SESSION_IPV4.val, null);
		}

		@Test
		public void pcrfSessionShouldSaved() {
			sessionReAuthProcessor.addSession(pcrfSession);
			assertEquals(1, sessionReAuthProcessor.getPCRFSessions().size());
			assertSame(pcrfSession, sessionReAuthProcessor.getPCRFSessions().values().iterator().next());
		}

		@Test
		public void ocsSessionShouldSaved() {
			ocsSession.addValue(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GY.val);
			ocsSession.addValue(PCRFKeyConstants.CS_SESSION_IPV4.val, null);

			sessionReAuthProcessor.addSession(ocsSession);
			assertEquals(1, sessionReAuthProcessor.getOCSSessions().size());
			assertSame(ocsSession, sessionReAuthProcessor.getOCSSessions().values().iterator().next());
		}

		@Test
		public void bothSessionShouldSaved() {
			sessionReAuthProcessor.addSession(pcrfSession);
			sessionReAuthProcessor.addSession(ocsSession);
			assertEquals(1, sessionReAuthProcessor.getPCRFSessions().size());
			assertEquals(1, sessionReAuthProcessor.getOCSSessions().size());
			assertSame(pcrfSession, sessionReAuthProcessor.getPCRFSessions().values().iterator().next());
			assertSame(ocsSession, sessionReAuthProcessor.getOCSSessions().values().iterator().next());
		}
	}

	public class WhenSessionDoHaveIpAddress {
		private SessionData pcrfSession;
		private SessionData ocsSession;
		private String ipAddress = "192.168.1.1";

		@Before
		public void setUp() {
			pcrfSession = createSessionData("Gx");
			ocsSession = createSessionData("Gy");
			pcrfSession.addValue(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GX.val);
			ocsSession.addValue(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GY.val);
			pcrfSession.addValue(PCRFKeyConstants.CS_SESSION_IPV4.val, ipAddress);
			ocsSession.addValue(PCRFKeyConstants.CS_SESSION_IPV4.val, ipAddress);
		}

		@Test
		public void onlyPCRFSessionShouldSaved() {
			sessionReAuthProcessor.addSession(ocsSession);
			sessionReAuthProcessor.addSession(pcrfSession);

			assertEquals(1, sessionReAuthProcessor.getPCRFSessions().size());
			assertEquals(0, sessionReAuthProcessor.getOCSSessions().size());
			assertSame(pcrfSession, sessionReAuthProcessor.getPCRFSessions().values().iterator().next());
		}
	}
}