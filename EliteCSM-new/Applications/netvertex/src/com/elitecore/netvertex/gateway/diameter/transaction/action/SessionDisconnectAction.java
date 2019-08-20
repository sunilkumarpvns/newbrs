package com.elitecore.netvertex.gateway.diameter.transaction.action;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.gateway.diameter.transaction.session.SessionKeys;
import com.elitecore.netvertex.gateway.diameter.transaction.session.TransactionSession;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;

public class SessionDisconnectAction extends ActionHandler{

	private static final String NAME = "SESS-DISCONNECT";
	private static final String MODULE = "SESSIONDISCONNECT";
	
	public SessionDisconnectAction(DiameterTransactionContext transactionContext) {
		super(transactionContext);
	}

	@Override
	public TransactionState handle() {
		TransactionSession session = getTransactionContext().getTransactionSession();
		PCRFRequest pcrfRequest = session.get(SessionKeys.PCRF_REQUEST);
		if(pcrfRequest != null){
			
			PCRFResponse response =  new PCRFResponseImpl();
			for(String key : pcrfRequest.getKeySet()){
				response.setAttribute(key, pcrfRequest.getAttribute(key));
			}
			
			response.setSessionStartTime(pcrfRequest.getSessionStartTime());
			
			if(SessionTypeConstant.GX.getVal().equals(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.getVal()))){
				LogManager.getLogger().info(MODULE, "PCRFResponse for sending RAR " + pcrfRequest);
				if(response.getAttribute(PCRFKeyConstants.SESSION_RELEASE_CAUSE.getVal()) == null)
						response.setAttribute(PCRFKeyConstants.SESSION_RELEASE_CAUSE.getVal(), PCRFKeyValueConstants.SRC_UNSPECIFIED_REASON.val);
				DiameterRequest request = getTransactionContext().getControllerContext().buildRAR(response);
				if(LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, " RAR request " + request);
				}
				getTransactionContext().sendRequest(request, response.getAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.val), true);
				
			}else if(SessionTypeConstant.RX.getVal().equals(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.getVal()))){
				LogManager.getLogger().info(MODULE, "PCRFResponse for sending ASR " + pcrfRequest);
				if(response.getAttribute(PCRFKeyConstants.ABORT_CAUSE.getVal()) == null)
					response.setAttribute(PCRFKeyConstants.ABORT_CAUSE.getVal(), PCRFKeyValueConstants.AC_BEARER_RELEASED.val);
				DiameterRequest request = getTransactionContext().getControllerContext().buildASR(response);
				if(LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE," ASR request " + request);
				}
				getTransactionContext().sendRequest(request, response.getAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.val), true);
			}else{
				LogManager.getLogger().debug(MODULE, "Invalid session type. skip sending session disconnect request");
			}
			
		}else{
			LogManager.getLogger().debug(MODULE, "No PCRFRequest found. so skip sending RAR");
		}
		return TransactionState.COMPLETE;
	}

	@Override
	public String getName() {
		return NAME;
	}

}
