package com.elitecore.netvertex.gateway.diameter.application.handler.tgpp;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.core.transaction.EventTypes;
import com.elitecore.netvertex.core.transaction.Transaction;
import com.elitecore.netvertex.core.transaction.TransactionType;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.application.handler.ApplicationHandler;

public class TGPPRxAppHandler implements ApplicationHandler{
	
	private static final String MODULE = "TGPP-RX-APP-HDLR";
	private DiameterGatewayControllerContext context;
	
	public TGPPRxAppHandler(DiameterGatewayControllerContext context) {
		this.context = context;
	}

	@Override
	public void handleReceivedRequest(Session session, DiameterRequest diameterRequest) {

		String transactionType = null;
		String eventType = null;
		
		if(CommandCode.AUTHENTICATION_AUTHORIZATION.code == diameterRequest.getCommandCode()){
			IDiameterAVP tgppFlowStatus = diameterRequest.getAVP(DiameterAVPConstants.TGPP_MEDIA_COMPONENT_DESCRIPTION+"."
												+DiameterAVPConstants.TGPP_FLOW_STATUS);
			
			if(tgppFlowStatus == null){
				transactionType = TransactionType.SERVICE_REG;
				eventType = EventTypes.AAR_RCVD;
			} else {
				// New Service Request
				transactionType = TransactionType.NEW_SERVICE_REQUEST;
				eventType = EventTypes.AAR_RCVD;
			}

		}else if(CommandCode.SESSION_TERMINATION.getCode() == diameterRequest.getCommandCode()){
			transactionType = TransactionType.SESSION_TERMINATION; 
			eventType = EventTypes.SESSION_TERMINATION;
		}
		
		if(transactionType != null && eventType != null) {
			
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Create " + transactionType + " transaction for " +  eventType + " transaction event");
			
			Transaction transaction = context.createTransaction(transactionType);
			transaction.process(eventType,diameterRequest);
		} else {
			LogManager.getLogger().warn(MODULE, "Skipping further processing. " +
				"Reason: Unsupported Rx request with commandCode = " + diameterRequest.getCommandCode());
		}
	}

	@Override
	public void handleReceivedResponse(Session session, DiameterRequest diameterRequest, DiameterAnswer diameterAnswer) {
		// not need to handle
	}

	@Override
	public void handleTimeoutRequest(Session session, DiameterRequest diameterRequest) {
		// not need to handle
	}
}
