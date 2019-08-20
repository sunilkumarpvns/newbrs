package com.elitecore.netvertex.gateway.diameter.transaction;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.GatewayTypeConstant;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
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
 * Transaction for STR
 *<table>
 * <tr>
 * 		<td>STATE</td><td>EVENT</td><td>ACTION</td><td>NEXT-STATE</td>
 * </tr>
 * <tr>
 * 		<td>IDLE</td><td>SESSION_TERMINATION</td><td>RxAuthorizeAction(Create PCRFRequest, Submit request, Remove core rx session)</td><td>WAIT_FOR_AUTH_RES</td>
 * </tr>
 * <tr>
 * 		<td>WAIT_FOR_AUTH_RES</td><td>RES_RCVD</td><td>StopService (Send STA,Locate Gx Session,Send RAR*,SubmitPCRFRequest, Delete SessionRule)</td><td>WAIT_FOR_RAA/COMPLETE</td>
 * </tr>
 *</table>
 * @author harsh 
 */

public class SessionTerminationTransaction extends BaseTransaction {

	public static final String MODULE = "SESS-TERMI-TRNSCT";
	private static Map<String, TransactionAction> actionHandlersMap;

	static {
		actionHandlersMap = new HashMap<String, TransactionAction>(1,1);
		actionHandlersMap.put(EventTypes.SESSION_TERMINATION, TransactionAction.RX_AUTHORIZE);
		actionHandlersMap.put(EventTypes.PCRF_RES_RCVD,  TransactionAction.STOP_SERVICE);
	}

	public SessionTerminationTransaction(DiameterGatewayControllerContext diameterGatewayControllerContext) {
		super(diameterGatewayControllerContext);
		
	}

	@Override
	public String getType() {
		return TransactionType.SESSION_TERMINATION;
	}

	@Override
	public synchronized void process(String eventType, DiameterRequest request) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Event Received: " + eventType + " for Transaction-Id: " + getTransactionId());
		getTransactionSession().put(SessionKeys.SESSION_TYPE, SessionTypeConstant.RX.getVal());
		getTransactionSession().put(SessionKeys.GATEWAY_TYPE, GatewayTypeConstant.DIAMETER.getVal());
		getTransactionSession().put(SessionKeys.DIAMETER_REQUEST, request);
		
		if(TransactionState.IDLE == this.transactionState){
			
			if(!EventTypes.SESSION_TERMINATION.equals(eventType)){
				LogManager.getLogger().error(MODULE, "Can't process " + getType() + " transaction. Reason: expected event = " + EventTypes.SESSION_TERMINATION 
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
		if(TransactionState.WAIT_FOR_AUTH_RES == this.transactionState){			
			getTransactionSession().put(SessionKeys.PCRF_RESPONSE, response);
			ActionHandler actionHandler = getActionHandler(EventTypes.PCRF_RES_RCVD);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());		
			this.transactionState = actionHandler.handle();
		} else {
			LogManager.getLogger().error(MODULE, "Can't resume " + getType() + ". Reason: No Action for Transaction-State = " + transactionState);
		}
		
	}
	
	private ActionHandler getActionHandler(String event){
		TransactionAction transactionAction = actionHandlersMap.get(event);
		return transactionAction.createActionHandler(getTransactionContext());
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException{
		return new SessionTerminationTransaction(getControllerContext());
	}

}
