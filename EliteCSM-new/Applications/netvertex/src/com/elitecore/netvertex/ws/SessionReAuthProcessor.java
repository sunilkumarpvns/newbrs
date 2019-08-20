package com.elitecore.netvertex.ws;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.corenetvertex.constants.GatewayTypeConstant;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.util.SessionReAuthUtil;
import com.elitecore.netvertex.EliteNetVertexServer.NetvertexGatewayController;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import org.apache.commons.lang.StringUtils;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class SessionReAuthProcessor {
	private static final String MODULE = "SESS-REAUTH-PRCSR";

	private final NetvertexGatewayController gatewayController;
	private Map<String, SessionData> pcrfSessions;
	private Map<String, SessionData> ocsSessions;

	public SessionReAuthProcessor(NetvertexGatewayController gatewayController) {
		this.pcrfSessions = new HashMap<>(1);
		this.ocsSessions = new HashMap<>(1);
		this.gatewayController = gatewayController;
	}

	public void addSession(SessionData sessionData) {
		SessionTypeConstant sessionTypeConstant = SessionTypeConstant.fromValue(sessionData.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal()));
		String ipAddress = getIpAddress(sessionData);
		correlate(sessionData, sessionTypeConstant, ipAddress);
	}

	/**
	 * BEHAVIOUR
	 *
	 * 1. When both session found with same IP then PCRF Session will be selected for reauthorized
	 * 2. When both session found with different IP then Both Session will be selected for reauthorized (Not possible In real scenario)
	 * 3. When both session found with different IP then Both Session will be selected for reauthorized
	 * 4. When Individual session found then that Session will be selected for reauthorized
	 * 5. When Both session found with no IP then both session will be selected for reauthorized
	 *
	 * @param sessionData
	 * @param sessionTypeConstant
	 * @param ipAddress
	 */
	private void correlate(SessionData sessionData, SessionTypeConstant sessionTypeConstant, String ipAddress) {
		if (SessionReAuthUtil.isPCRFSession(sessionTypeConstant)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Adding session: " + sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.getVal())
						+ " in PCRF re-auth list");
			}

			pcrfSessions.put(ipAddress, sessionData);

			/* When IP Address is not there in session, do not correlate sessions.
			 * ReAuth will be generated for both sessions.
			 */
			if (ipAddress == null) {
				return;
			}

			if (ocsSessions.containsKey(ipAddress)) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Removing session: " + sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.getVal())
							+ " from OCS re-auth list. Reason: PCRF Session found for same IP Address: " + ipAddress);
				}
				ocsSessions.remove(ipAddress);
			}
		} else if (SessionReAuthUtil.isOCSSession(sessionTypeConstant) && (ipAddress == null || pcrfSessions.containsKey(ipAddress) == false)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Adding session: " + sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.getVal())
						+ " in OCS re-auth list");
			}
			ocsSessions.put(ipAddress, sessionData);
		}
	}

	public void reAuth(boolean isForceFullReAuth) {
		processReAuth(isForceFullReAuth, pcrfSessions.values());
		processReAuth(isForceFullReAuth, ocsSessions.values());
	}

	private void processReAuth(boolean isForceFullReAuth, Collection<SessionData> sessions) {
		for (SessionData session : sessions) {
			SessionTypeConstant sessionType = SessionTypeConstant.fromValue(session.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal()));
			PCRFRequest request = new PCRFRequestImpl();
			PCRFPacketUtil.buildPCRFRequest(session, request);

			if (isForceFullReAuth) {
				request.setAttribute(PCRFKeyConstants.SESSION_RE_AUTH.val, PCRFKeyValueConstants.FORCEFUL_SESSION_RE_AUTH.val);
			}

			if (GatewayTypeConstant.RADIUS == sessionType.gatewayType) {
				if (isRadiusServiceEnabled()) {
                    if (getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, "Re Authorizing session with core session id: "+session.getValue(PCRFKeyConstants.CS_CORESESSION_ID.val)+" and last update time: "+session.getLastUpdateTime());
                    }
					gatewayController.getRadiusGatewayController().handleSessionReAuthorization(request);
				} else {
					getLogger().warn(MODULE, "Unable to reauthorize session for session ID: " + session.getValue(PCRFKeyConstants.CS_SESSION_ID.val)
							+ ". Reason: Radius is not enabled");
				}
			} else if (GatewayTypeConstant.DIAMETER == sessionType.gatewayType) {
				if (isDiameterServiceEnabled()) {
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Re Authorizing session with core session id: "+session.getValue(PCRFKeyConstants.CS_CORESESSION_ID.val)+" and last update time: "+session.getLastUpdateTime());
                    }
					gatewayController.getDiameterGatewayController().handleSessionReAuthorization(request);
				} else {
					getLogger().warn(MODULE, "Unable to reauthorize session for session ID: " + session.getValue(PCRFKeyConstants.CS_SESSION_ID.val)
							+ ". Reason: Diameter is not enabled");
				}
			}
		}
	}

	private boolean isDiameterServiceEnabled() {
		return gatewayController.getDiameterGatewayController() != null &&
				gatewayController.getDiameterGatewayController().isEnabled();
	}

	private boolean isRadiusServiceEnabled() {
		return gatewayController.getRadiusGatewayController() != null
				&& gatewayController.getRadiusGatewayController().isEnabled();
	}

	private String getIpAddress(SessionData sessionData) {
		String ipAddress = sessionData.getValue(PCRFKeyConstants.CS_SESSION_IPV4.val);
		if (StringUtils.isEmpty(ipAddress)) {
			ipAddress = sessionData.getValue(PCRFKeyConstants.CS_SESSION_IPV6.val);
		}
		return ipAddress;
	}

	public Map<String, SessionData> getPCRFSessions() {
		return pcrfSessions;
	}

	public Map<String, SessionData> getOCSSessions() {
		return ocsSessions;
	}
}
