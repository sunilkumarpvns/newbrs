package com.elitecore.netvertex.gateway;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.corenetvertex.constants.GatewayTypeConstant;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayController;
import com.elitecore.netvertex.gateway.radius.RadiusGatewayController;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class GatewayMediator implements Mediator {

	private static final String MODULE = "MEDIATR";
	private final SessionLocator sessionLocator;

	@Nullable
	private DiameterGatewayController diameterGatewayController;
	@Nullable
	private RadiusGatewayController radiusGatewayController;

	public GatewayMediator(SessionLocator sessionLocator) {
		this.sessionLocator = sessionLocator;
		
	}

	private ILogger getLogger() {
		return LogManager.getLogger();
	}

	@Override
	public void disconnect(PCRFRequest pcrfRequest) {

		String coreSessionID = pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val);

		String gatewayType = pcrfRequest.getAttribute(PCRFKeyConstants.GATEWAY_TYPE.val);

		if (gatewayType == null) {
			getLogger().warn(MODULE, "Unable to disconnet session:" + coreSessionID + ".Reason: " + PCRFKeyConstants.GATEWAY_TYPE.val
					+ " not found in request");
			return;
		}

		if (GatewayTypeConstant.DIAMETER.val.equals(gatewayType)) {
			if (diameterGatewayController == null) {
				getLogger().warn(MODULE, "Unable to disconnet session:" + coreSessionID + ".Reason: Diameter is not enabled");
				return;
			}
		} else if (GatewayTypeConstant.RADIUS.val.equals(gatewayType)) {
			if (radiusGatewayController == null) {
				getLogger().warn(MODULE, "Unable to disconnet session:" + coreSessionID + ".Reason: Radius is not enabled");
				return;
			}
			radiusGatewayController.handleSessionDisconnectRequest(pcrfRequest);
		} else {
			getLogger().warn(MODULE, "Unable to disconnet session:" + coreSessionID + ".Reason: Unsupported gateway type:" + gatewayType);
		}
	}

	/**
	 * ReAuthorize each session found in NetVetrtex Session.
	 * 
	 * Find the receiver based on Session-Type. If session type is not found
	 * then simply log and skip the processing.
	 * 
	 * Forceful ReAthorization is Receiver dependent. Mediator simply indicate
	 * that receiver should do forcefule reAthorization.
	 * 
	 * 
	 *  @param key
	 *            Logical column name on which session need to look-up
	 * @param val
	 *            value of Logical column
	 * @param reAuthCause
	 * @param forcefullReAuth
	 */

	@Override
	public GatewayMediator.ResultCodes reauthorize(@Nonnull PCRFKeyConstants pcrfKey,
                                                   String value,
                                                   @Nonnull String reAuthCause,
                                                   boolean forcefullReAuth,
                                                   @Nullable Map<PCRFKeyConstants, String> additionalParams) {

		if (getLogger().isLogLevel(LogLevel.INFO))
			getLogger().info(MODULE, "Re-Authorizing session for " + pcrfKey + " = " + value);

		try {
			Criteria criteria = sessionLocator.getCoreSessionCriteria();
			criteria.add(Restrictions.eq(pcrfKey.val, value));
			List<SessionData> sessions = sessionLocator.getCoreSessionList(criteria);

			if (sessions == null || sessions.isEmpty()) {
				if (getLogger().isLogLevel(LogLevel.DEBUG)) {
					getLogger().debug(MODULE, "Skipping Re-Authorization for " + pcrfKey + " = " + value +
							" Reason: Core session not found");
				}

				return ResultCodes.SESSION_NOT_FOUND;
			}

			if (getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Core session found for " + pcrfKey + " = " + value);

			int noOfSessionReauthorized = 0;
			for (SessionData session : sessions) {
				
				String sessionTypeStr = session.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal());
				if (sessionTypeStr == null) {
					if (getLogger().isLogLevel(LogLevel.DEBUG)) {
						getLogger().debug(MODULE, "Skipping further processing. Reason: Session type not found");
					}
					continue;
				}

				SessionTypeConstant sessionType = SessionTypeConstant.fromValue(sessionTypeStr);

				if (sessionType == null) {
					getLogger().warn(MODULE, "Skipping Re-Authorization for " + pcrfKey + " = " + value +
							" Reason: Invalid session-type: " + sessionTypeStr);
					continue;
				}
				
				PCRFRequest request = new PCRFRequestImpl();
				PCRFPacketUtil.buildPCRFRequest(session, request);
				
				if(additionalParams != null) {					
					for(Entry<PCRFKeyConstants, String> keyValPair : additionalParams.entrySet()) {
						request.setAttribute(keyValPair.getKey().getVal(), keyValPair.getValue());
					}
				}

				request.setAttribute(PCRFKeyConstants.RE_AUTH_CAUSE.val, reAuthCause);
				
				if (forcefullReAuth == true) {
					request.setAttribute(PCRFKeyConstants.SESSION_RE_AUTH.val, PCRFKeyValueConstants.FORCEFUL_SESSION_RE_AUTH.val);
				}


				if (GatewayTypeConstant.RADIUS == sessionType.gatewayType) {
					if (radiusGatewayController == null) {
						getLogger().warn(MODULE, "Unable to reauthorize session.Reason: radius is not enabled");
						continue;
					}
					radiusGatewayController.handleSessionReAuthorization(request);
					noOfSessionReauthorized++;
				} else if (GatewayTypeConstant.DIAMETER == sessionType.gatewayType) {
					if (diameterGatewayController == null) {
						getLogger().warn(MODULE, "Unable to reauthorize session.Reason: Diameter is not enabled");
						continue;
					}
					diameterGatewayController.handleSessionReAuthorization(request);
					noOfSessionReauthorized++;
				} else {
					getLogger().warn(MODULE, "Skipping Re-Authorization for " + pcrfKey + " = " + value +
							" Reason: Unsupported gateway type:" + sessionType.gatewayType);
					continue;
				}
			}
			
			if (noOfSessionReauthorized == 0) {
				return ResultCodes.SESSION_NOT_FOUND;
			} else {				
				return ResultCodes.SUCCESS;
			}

		} catch (SessionException e) {
			getLogger().error(MODULE, "Unable to Re-Authorize " + pcrfKey + " = " + value +
					". Reason : " + e.getMessage());

			getLogger().trace(MODULE, e);
			return ResultCodes.INTERNAL_ERROR;
		}
	}

	public void setDiameterGatewayController(DiameterGatewayController diameterGatewayController) {
		this.diameterGatewayController = diameterGatewayController;
	}

	public void setRadiusGatewayController(RadiusGatewayController radiusGatewayController) {
		this.radiusGatewayController = radiusGatewayController;
	}
	
	public enum ResultCodes{
		SESSION_NOT_FOUND,
		SERVICE_UNAVAILABLE,
		INTERNAL_ERROR,
		SUCCESS;
		
	}

}
