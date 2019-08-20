package com.elitecore.netvertex.gateway.diameter.application.handler.tgpp;

import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.netvertex.core.transaction.EventTypes;
import com.elitecore.netvertex.core.transaction.Transaction;
import com.elitecore.netvertex.core.transaction.TransactionType;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.application.handler.ApplicationHandler;

public class TGPPGxAppHandler implements ApplicationHandler {
	
	private static final String MODULE = "3GPP-GX-APP-HDLR";
	private DiameterGatewayControllerContext context;
	
	public TGPPGxAppHandler(DiameterGatewayControllerContext context) {
		this.context = context;
	}

	@Override
	public void handleReceivedRequest(Session session,DiameterRequest diameterRequest) {
		
		if(CommandCode.CREDIT_CONTROL.code != diameterRequest.getCommandCode()){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Skipping further processing. Reason: Unsupported Gx request");
			return;
		}
		IDiameterAVP requestType = diameterRequest.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);

		String transactionType = null;
		String eventType = null;
		if(requestType.getInteger() == DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST){
			transactionType = TransactionType.SESSION_START;
			eventType = EventTypes.SESSION_START;
		}else if(requestType.getInteger() == DiameterAttributeValueConstants.DIAMETER_UPDATE_REQUEST){
			transactionType = TransactionType.SESSION_UPDATE;
			eventType = EventTypes.SESSION_UPDATE;
			
			List<IDiameterAVP> diameterAVPs = diameterRequest.getAVPList(DiameterAVPConstants.TGPP_CHARGING_RULE_REPORT);
			if(diameterAVPs != null && !diameterAVPs.isEmpty()){
				for(IDiameterAVP chargingRuleReportAVP : diameterAVPs){
					IDiameterAVP chargingRuleStatusAVP = ((AvpGrouped)chargingRuleReportAVP).getSubAttribute(DiameterAVPConstants.TGPP_PCC_RULE_STATUS);
					if(chargingRuleStatusAVP != null && chargingRuleStatusAVP.getInteger() == DiameterAttributeValueConstants.TGPP_PCC_RULE_STATUS_INACTIVE){
						transactionType = TransactionType.RULE_REMOVE;
						eventType = EventTypes.RULE_REMOVED;
						break;
					}
				}
			}
			
		}else {
			transactionType = TransactionType.SESSION_STOP;
			eventType = EventTypes.SESSION_STOP;
		}
		
		
		if(transactionType != null && eventType != null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Create " + transactionType + " transaction for " +  eventType + " transaction event");
			Transaction transaction = context.createTransaction(transactionType);
			transaction.process(eventType,diameterRequest);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Skipping further processing. Reason: Unsupported event for gx requst");
			return;
		}
	}

	@Override
	public void handleReceivedResponse(Session session, DiameterRequest diameterRequest, DiameterAnswer diameterAnswer) {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Received Gx Answer: " + diameterAnswer);
		}

		Transaction transaction= context.removeTransaction(diameterAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID) + String.valueOf(diameterRequest.getHop_by_hopIdentifier()));
		if(transaction != null){
			transaction.resume(diameterAnswer);
		}
	}

	@Override
	public void handleTimeoutRequest(Session session,DiameterRequest diameterRequest) {
		Transaction transaction= context.removeTransaction(diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID)+ String.valueOf(diameterRequest.getHop_by_hopIdentifier()));
		if(transaction != null){
			transaction.resume(diameterRequest);
		}
	}
}
