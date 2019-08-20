package com.elitecore.aaa.diameter.service.application.handlers;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.wimax.WimaxSessionManager;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;

public class DiameterWimaxAcctHandler implements DiameterApplicationHandler<ApplicationRequest, ApplicationResponse>{

	private static final String MODULE = "DIA-WIMAX-ACCT-HDLR";
	private ServiceContext serviceContext;

	public DiameterWimaxAcctHandler(ServiceContext serviceContext) {
		this.serviceContext = serviceContext;
	}

	@Override
	public void init() throws InitializationFailedException {
		
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		
	}

	@Override
	public boolean isEligible(ApplicationRequest request, ApplicationResponse response) {
		return true;
	}

	@Override
	public void handleRequest(ApplicationRequest request,
			ApplicationResponse response, ISession session) {
		if (request.getAVP(DiameterAVPConstants.ACCOUNTING_RECORD_TYPE) == null) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Wimax Session will not be removed, Reason: " 
						+ DiameterAVPConstants.ACCOUNTING_RECORD_TYPE_STR  
						+ " AVP not arrived in request with HbH-ID=" 
						+ request.getDiameterRequest().getHop_by_hopIdentifier() 
						+ ", EtE-ID=" + request.getDiameterRequest().getEnd_to_endIdentifier() 
						+ ", Session-ID=" + request.getDiameterRequest().getAVPValue(DiameterAVPConstants.SESSION_ID));
			}
			return;
		}
		if (DiameterAttributeValueConstants.STOP_RECORD == request.getAVP(DiameterAVPConstants.ACCOUNTING_RECORD_TYPE).getInteger() ) {
			removeWimaxSession(request.getDiameterRequest());
		}
	}

	private void removeWimaxSession(DiameterRequest diameterRequest) {
		
		if (isSessionContinue(diameterRequest)) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Wimax Session will not be removed, Reason: Session-Continue is true for request with HbH-ID=" 
						+ diameterRequest.getHop_by_hopIdentifier() 
						+ ", EtE-ID=" + diameterRequest.getEnd_to_endIdentifier() 
						+ ", Session-ID=" + diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
			}
			return;
		}
		WimaxSessionManager wimaxSessionManager = ((AAAServerContext)serviceContext.getServerContext()).getWimaxSessionManager();
		
		String callingStationID = diameterRequest.getAVPValue(DiameterAVPConstants.CALLING_STATION_ID);
		if (callingStationID != null) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Removing Wimax Session for "
						+ DiameterAVPConstants.CALLING_STATION_ID_STR + " : " +  callingStationID);
			}
			wimaxSessionManager.removeSessionByCallingStationID(callingStationID);
			return;
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, DiameterAVPConstants.CALLING_STATION_ID_STR 
						+ " not found for request with HbH-ID=" 
						+ diameterRequest.getHop_by_hopIdentifier() 
						+ ", EtE-ID=" + diameterRequest.getEnd_to_endIdentifier() 
						+ ", Session-ID=" + diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
			}
		}
		
		String username = diameterRequest.getAVPValue(DiameterAVPConstants.USER_NAME);
		if (username != null) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Removing Wimax Session for " 
						+ DiameterAVPConstants.USER_NAME_STR + " : " +  username);
			}
			wimaxSessionManager.removeSessionByUsername(username);
			return;
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, DiameterAVPConstants.USER_NAME_STR 
						+ " not found for request with HbH-ID=" 
						+ diameterRequest.getHop_by_hopIdentifier() 
						+ ", EtE-ID=" + diameterRequest.getEnd_to_endIdentifier() 
						+ ", Session-ID=" + diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
			}
		}

		String cui = diameterRequest.getAVPValue(DiameterAVPConstants.CUI);
		if (cui != null) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Removing Wimax Session for " 
						+ DiameterAVPConstants.CUI_STR + " : " +  cui);
			}
			wimaxSessionManager.removeSessionByCUI(cui);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, DiameterAVPConstants.CUI_STR 
						+ " not found for request with HbH-ID=" 
						+ diameterRequest.getHop_by_hopIdentifier() 
						+ ", EtE-ID=" + diameterRequest.getEnd_to_endIdentifier() 
						+ ", Session-ID=" + diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
			}
		}
	}

	private boolean isSessionContinue(DiameterRequest diameterRequest) {
		IDiameterAVP sessionContinueAvp = diameterRequest.getAVP(DiameterConstants.WIMAX_VENDOR_ID + ":"  + WimaxAttrConstants.SESSION_CONTINUE.getIntValue());
		return sessionContinueAvp != null && sessionContinueAvp.getInteger() > 0;
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}

}
