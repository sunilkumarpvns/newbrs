package com.elitecore.netvertex.service.pcrf.servicepolicy;

import java.util.List;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.conf.MiscellaneousConfiguration;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.service.pcrf.PCCRuleExpiryListener;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;

public class ReschedulePcrfRequest implements Runnable{
	
	private static final String MODULE = "RSCH-PCRF-REQ";
	private String coreSessionID;
	private PCCRuleExpiryListener pccRuleExpiryListener;
	private NetVertexServerContext serverContext;
	private SessionLocator sessionLocator;

	public ReschedulePcrfRequest(String coreSessionID, 
				NetVertexServerContext serverContext,
				SessionLocator sessionLocator, 
				PCCRuleExpiryListener pccRuleExpiryListener) {
		this.coreSessionID = coreSessionID;
		this.serverContext = serverContext;
		this.sessionLocator = sessionLocator;
		this.pccRuleExpiryListener = pccRuleExpiryListener;
	}

	
	@Override
	public void run() {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Start scheduling PCRFRequest with Core-Session-ID = " + coreSessionID);
		
		Criteria criteria = null;
		try{
			criteria = sessionLocator.getCoreSessionCriteria();
			
		} catch (SessionException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Unable to schedule PCRFRequest with Core-Session-ID = " + coreSessionID +". Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			
			return;
		}
		
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "fetching Session based on Core-session-ID = " + coreSessionID);
		criteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.getVal(), coreSessionID));
		
		String hint = serverContext.getServerConfiguration().
				getMiscellaneousParameterConfiguration().
				getParameterValue(MiscellaneousConfiguration.CORESESSION_CORESESSIONID);
		if(Strings.isNullOrBlank(hint) == false){
			criteria.setHint(hint);
		}
		
		
		List<SessionData> sessions = sessionLocator.getCoreSessionList(criteria);
		if(sessions == null || sessions.isEmpty()) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "No session found with Core-Session-ID = " + coreSessionID);
			return;
		}
		

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, sessions.size() + " session found with Core-Session-ID = " + coreSessionID);
		
		for(SessionData session : sessions) {
			
			PCRFRequest pcrfRequest = new PCRFRequestImpl();
			PCRFPacketUtil.buildPCRFRequest(session, pcrfRequest);
			if(SessionTypeConstant.RX.val.equals(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val)) == false){
				pcrfRequest.addPCRFEvent(PCRFEvent.REAUTHORIZE);
			}
			
			pcrfRequest.addPCRFEvent(PCRFEvent.AUTHENTICATE);
			pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_UPDATE);
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Rescheduled PCRF Request " +  pcrfRequest);
				
			pccRuleExpiryListener.reAuthSession(pcrfRequest);	
		}
		
	}
}
