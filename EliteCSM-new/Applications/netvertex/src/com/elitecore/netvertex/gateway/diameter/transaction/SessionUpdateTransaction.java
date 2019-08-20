package com.elitecore.netvertex.gateway.diameter.transaction;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.GatewayTypeConstant;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.netvertex.core.transaction.BaseTransaction;
import com.elitecore.netvertex.core.transaction.EventTypes;
import com.elitecore.netvertex.core.transaction.TransactionAction;
import com.elitecore.netvertex.core.transaction.TransactionType;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.transaction.action.ActionHandler;
import com.elitecore.netvertex.gateway.diameter.transaction.session.SessionKeys;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.HashMap;
import java.util.Map;

/***
 * 
 * Transaction for Update Request with Event-Trigger
 *<table>
 * <tr>
 * 		<td>STATE</td><td>EVENT</td><td>ACTION</td><td>NEXT-STATE</td>
 * </tr>
 * <tr>
 * 		<td>IDLE</td><td>Session-Update</td><td>AUTHORIZE (CreatePCRFReq,SubmitReq)</td><td>Wait-For-Auth-Res</td>
 * </tr>
 * <tr>
 * 		<td>Wait-For-Auth-Res</td><td>SUCCESS_RES_RCVD</td><td>SendAckOfCCR</td><td>COMPLETE</td>
 * </tr>
 * <tr>
 * 		<td>Wait-For-Auth-Res</td><td>FAILED_RES_RCVD</td><td>SendAckOfCCR</td><td>COMPLETE</td>
 * </tr>
  
 *</table>
 * @author harsh 
 */

public class SessionUpdateTransaction extends BaseTransaction{
	
	public static final String MODULE = "SESS-UPDATE-TRNSCT";
	private static Map<String, TransactionAction> actionHandlersMap;

	static {
		actionHandlersMap = new HashMap<String, TransactionAction>(1,1);
		actionHandlersMap.put(EventTypes.SESSION_UPDATE, TransactionAction.AUTHORIZE);
		actionHandlersMap.put(EventTypes.SUCCESS_RES_RCVD, TransactionAction.SEND_CCA);
		actionHandlersMap.put(EventTypes.FAILED_RES_RCVD,  TransactionAction.SEND_CCA);
	}

	public SessionUpdateTransaction(DiameterGatewayControllerContext diameterGatewayControllerContext) {
		super(diameterGatewayControllerContext);
		
		
	}

	@Override
	public String getType() {
		return TransactionType.SESSION_UPDATE;
	}

	@Override
	public synchronized void process(String eventType, DiameterRequest request) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Event Received: " + eventType + " for Transaction-Id: " + getTransactionId());
		if(request.getApplicationID() == ApplicationIdentifier.TGPP_S9.applicationId){
			getTransactionSession().put(SessionKeys.SESSION_TYPE, SessionTypeConstant.S9.getVal());
		} else {
			getTransactionSession().put(SessionKeys.SESSION_TYPE, SessionTypeConstant.GX.getVal());
		}
		getTransactionSession().put(SessionKeys.GATEWAY_TYPE, GatewayTypeConstant.DIAMETER.getVal());
		getTransactionSession().put(SessionKeys.DIAMETER_REQUEST, request);
		
		if(TransactionState.IDLE == this.transactionState) {
			
			if(!EventTypes.SESSION_UPDATE.equals(eventType)){
				LogManager.getLogger().error(MODULE, "Can't process " + getType() + " transaction. Reason: expected event = " + EventTypes.SESSION_UPDATE 
						+" while event = " + eventType + " received ");
				return;
			}
			
			ActionHandler actionHandler = getActionHandler(eventType);
			if(actionHandler == null){
				LogManager.getLogger().error(MODULE, "Can't process " + getType() + " transaction. Reason: no Action found for event = " + eventType);
				return;
			}
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());
			
		this.transactionState = actionHandler.handle();
		} else {
			LogManager.getLogger().error(MODULE, "Can't process " + getType() + " transaction. Reason: transaction is not in " + TransactionState.IDLE + " state");
	}

	}

	@Override
	public synchronized void resume(PCRFResponse response) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Response Received(Transaction-ID: "+ getTransactionId()  +" ) from PCRF Service: " + response);
		getTransactionSession().put(SessionKeys.PCRF_RESPONSE, response);
		if(isSuccess(response)){
			ActionHandler actionHandler = getActionHandler(EventTypes.SUCCESS_RES_RCVD);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());
			this.transactionState = actionHandler.handle();
		}else{
			ActionHandler actionHandler = getActionHandler(EventTypes.FAILED_RES_RCVD);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());	
			this.transactionState = actionHandler.handle();
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "TransactionId: " + getTransactionId() + " is completed");
	}
	
	private ActionHandler getActionHandler(String event){
		TransactionAction transactionAction = actionHandlersMap.get(event);
		return transactionAction.createActionHandler(getTransactionContext());
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException{
		return new SessionUpdateTransaction(getControllerContext());
	}

}
