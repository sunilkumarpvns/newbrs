/**
 * 
 */
package com.elitecore.netvertex.gateway.diameter.transaction;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.netvertex.core.transaction.BaseTransaction;
import com.elitecore.netvertex.core.transaction.EventTypes;
import com.elitecore.netvertex.core.transaction.TransactionAction;
import com.elitecore.netvertex.core.transaction.TransactionType;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.transaction.action.ActionHandler;
import com.elitecore.netvertex.gateway.diameter.transaction.session.SessionKeys;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
/**
 * 
 * Transaction for New Service Request
 * <table>
 * 	<tr>
 * 		<td><b>STATE</b></td><td><b>EVENT</b></td><td><b>ACTION</b></td><td><b>NEXT-STATE</b></td>
 * 	</tr>
 * 	<tr>
 * 		<td>IDLE</td><td>PCRF_RES_RCVD</td><td>SessionDisconnectAction (CreatePCRFRes, SendRAR)</td><td>COMPLETE</td>
 * 	</tr>
 *</table>
 * @author harsh 
 */
public class SessionDisconnectTransaction extends BaseTransaction {

	private static final String MODULE = "SESS-DISCT-TRNSCT";	
	private static Map<String, TransactionAction> actionHandlersMap;
	
	static {
		actionHandlersMap = new HashMap<String, TransactionAction>(1,1);
		actionHandlersMap.put(EventTypes.SESSION_DISCONNECT, TransactionAction.SESSION_DISCONNECT);
	}
	
	public SessionDisconnectTransaction(DiameterGatewayControllerContext diameterGatewayControllerContext) {
		super(diameterGatewayControllerContext);
	}

	@Override
	public String getType() {		
		return TransactionType.SESSION_DISCONNECT;
	}
	
	@Override
	public synchronized void process(String eventType, PCRFRequest request){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Event Received: " + eventType + " for Transaction-Id: " + getTransactionId());
		getTransactionSession().put(SessionKeys.PCRF_REQUEST, request);
		if(TransactionState.IDLE == this.transactionState){
			if(!EventTypes.SESSION_DISCONNECT.equals(eventType)){
				LogManager.getLogger().error(MODULE, "Can't process " + getType() + " transaction. Reason: expected event = " + EventTypes.SESSION_DISCONNECT 
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
	public synchronized void resume(DiameterAnswer answer) {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, answer.toString());
	}
	
	@Override
	public synchronized void resume(PCRFResponse response) {			
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Response Received(Transaction-ID: "+ getTransactionId()  +" ) from PCRF Service: " + response);
	}
	
	private ActionHandler getActionHandler(String event){
		TransactionAction transactionAction = actionHandlersMap.get(event);
		if(transactionAction == null){
			return null;
		}
		return transactionAction.createActionHandler(getTransactionContext());
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException{
		return new SessionDisconnectTransaction(getControllerContext());
	}

}
