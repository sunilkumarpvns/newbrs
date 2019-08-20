package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.servicepolicy.handler.ServiceHandler;
import com.elitecore.netvertex.core.session.SessionOperation;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;

/**
 * 
 * @author Harsh Patel
 *
 */
//TODO Remove this handler after implementing Restructure of session management for RADIUS Flow
public class SessionHandler extends ServiceHandler{

	private static final String MODULE = "SESS-HDLR";
	private static final String NAME = "Session";
	
	private SessionOperation sessionOperation;
	
	public SessionHandler(PCRFServiceContext pcrfServiceContext, SessionOperation sessionOperation) {
		super(pcrfServiceContext);
		this.sessionOperation = sessionOperation;
	}
	
	
	@Override
	protected boolean isApplicable(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
		PCRFResponse pcrfResponse = (PCRFResponse) serviceResponse;
		return  SessionTypeConstant.RADIUS.val.equals(pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val));
	}
	
	@Override
	protected void process(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {
		PCRFRequest request = (PCRFRequest) serviceRequest;
		PCRFResponse response = (PCRFResponse) serviceResponse;
		
		String coreSessionId = response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal());
		if(request.getPCRFEvents().contains(PCRFEvent.GATEWAY_REBOOT) == false){
			if(coreSessionId == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Skipping Session handling. Reason: Session-ID not found");
				return;
			}
		}
		
			
		if(request.getPCRFEvents().contains(PCRFEvent.SESSION_START)){
			
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Creating new core session for CoreSession-ID: " + coreSessionId);
				
			sessionOperation.createCoreSession(response);

		} else if(request.getPCRFEvents().contains(PCRFEvent.SESSION_UPDATE)
				|| request.getPCRFEvents().contains(PCRFEvent.SESSION_RESET)){

			if(request.isSessionFound() == true){
				if(getLogger().isLogLevel(LogLevel.INFO))
					getLogger().info(MODULE, "Updating core session for CoreSession-ID: " + coreSessionId);

				sessionOperation.updateCoreSession(response);
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Creating new core session for CoreSession-ID: " + coreSessionId);
				sessionOperation.createCoreSession(response);
			}
			
		}else if(request.getPCRFEvents().contains(PCRFEvent.SESSION_STOP)){
			
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Deleting core session for CoreSession-ID: " + coreSessionId);

			sessionOperation.deleteCoreSessionByCoreSessionId(response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));
			
		
		}else if(request.getPCRFEvents().contains(PCRFEvent.GATEWAY_REBOOT)){
			String gatewayAddress = request.getAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal());
			
			if(gatewayAddress == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Skipping session deletion. Reason: CS_GATEWAY_ADDRESS not found");
				return;
			}
			try {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Deleting sessions for Gateway: " + gatewayAddress);
				
				Criteria sessionRuleCriteria = sessionOperation.getSessionRuleCriteria();
				sessionRuleCriteria.add(Restrictions.eq(PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, gatewayAddress));
				sessionOperation.deleteSessionRule(sessionRuleCriteria);
				
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Deleting session rule for gateway: " + gatewayAddress);
				
				Criteria coreSessionCriteria = sessionOperation.getCoreSessionCriteria();
				coreSessionCriteria.add(Restrictions.eq(PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, gatewayAddress));
				sessionOperation.deleteCoreSession(coreSessionCriteria);
			} catch (SessionException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Error in deleting core session for CS_GATEWAY_ADDRESS = " + gatewayAddress +". Reason : " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
		
	}



	@Override
	public String getName() {
		return NAME;
	}
}
