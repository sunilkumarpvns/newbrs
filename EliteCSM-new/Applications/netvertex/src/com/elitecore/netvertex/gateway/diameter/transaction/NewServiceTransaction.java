package com.elitecore.netvertex.gateway.diameter.transaction;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.GatewayTypeConstant;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
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

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * 
 * Transaction for New Service Request
 * <table>
 * 	<tr>
 * 		<td>STATE</td><td>EVENT</td><td>ACTION</td><td>NEXT-STATE</td>
 * 	</tr>
 * 	<tr>
 * 		<td>IDLE</td><td>AAR_RCVD</td><td>RxAuthorize (CreatePCRFReq,Fetch(Gx/Rx)Session,SubmitReq)</td><td>Wait-For-Auth-Res</td>
 * 	</tr>
 * <tr>
 * 		<td>Wait-For-Auth-Res</td><td>SUCCESS_RES_RCVD</td><td>RULE-INSTALL (GenerateRAR,SendRAR)</td><td>Wait-Rule-Install-Ack</td>
 * </tr>
 * <tr>
 * 		<td>Wait-Rule-Install-Ack</td><td>FAILED_RES_RCVD</td><td>SEND-FAILURE-ACK-TO-AF (CreateAAA,SendAAA)</td><td>COMPLETE</td>
 * </tr>
 * <tr>
 * 		<td>Wait-Rule-Install-Ack</td><td>UNKNOWN_SESSION_ID_RCVD</td><td>REMOVE Gx SESSION, SEND-FAILURE-ACK-TO-AF (CreateAAA,SendAAA)</td><td>COMPLETE</td>
 * </tr>
 *
 * 	<tr>
 * 		<td>Wait-Rule-Install-Ack</td><td>RULE-INSTALL-SUCCESS</td><td>SEND-SUCCESS-ACK-TO-AF (CreateAAA,SendAAA)</td><td>COMPLETE</td>
 * 	</tr>
 * 	<tr>
 * 		<td>Wait-Rule-Install-Ack</td><td>RULE-INSTALL-FAILURE</td><td>SEND-FAILURE-ACK-TO-AF (CreateAAA,SendAAA)</td><td>COMPLETE</td>
 * 	</tr>
 *  
 *</table>
 * @author jatin 
 */

public class NewServiceTransaction extends BaseTransaction{
	private static final String MODULE = "NW-SER-TRNSCT";	
	private static Map<String, TransactionAction> actionHandlersMap;
	
	static {
		actionHandlersMap = new HashMap<String, TransactionAction>(1,1);
	
		actionHandlersMap.put(EventTypes.AAR_RCVD, TransactionAction.RX_AUTHORIZE);
		actionHandlersMap.put(EventTypes.SUCCESS_RES_RCVD, TransactionAction.RULE_INSTALL);
		actionHandlersMap.put(EventTypes.FAILED_RES_RCVD, TransactionAction.SEND_FAILURE_ACK_TO_AF);
		actionHandlersMap.put(EventTypes.RULE_INSTALL_SUCCESS, TransactionAction.SEND_SUCCESS_ACK_TO_AF);
		actionHandlersMap.put(EventTypes.RULE_INSTALL_FAIL, TransactionAction.SEND_FAILURE_ACK_TO_AF);
	}
	
	public NewServiceTransaction(DiameterGatewayControllerContext diameterGatewayControllerContext) {
		super(diameterGatewayControllerContext);
	}

	@Override
	public String getType() {		
		return TransactionType.NEW_SERVICE_REQUEST;
	}
	
	@Override
	public synchronized void process(String eventType,DiameterRequest request) {
		if(getLogger().isLogLevel(LogLevel.DEBUG))
			getLogger().debug(MODULE, "Event Received: " + eventType + " for Transaction-Id: " + getTransactionId());
		
		getTransactionSession().put(SessionKeys.SESSION_TYPE, SessionTypeConstant.RX.getVal());
		getTransactionSession().put(SessionKeys.GATEWAY_TYPE, GatewayTypeConstant.DIAMETER.getVal());
		getTransactionSession().put(SessionKeys.DIAMETER_REQUEST, request);
		
		if(TransactionState.IDLE == this.transactionState){
			if(EventTypes.AAR_RCVD.equals(eventType) == false){
				getLogger().error(MODULE, "Can't process " + getType() + " transaction. Reason: expected event = " + EventTypes.AAR_RCVD 
						+" while event = " + eventType + " received ");
				return;
			}
				
				// AuthorizeAction
			ActionHandler actionHandler = getActionHandler(eventType);
			if(actionHandler == null){
				getLogger().error(MODULE, "Can't process " + getType() + " transaction. Reason: no Action found for event = " + eventType);
				return;
			}
			
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());
			
				this.transactionState = actionHandler.handle();				
		} else {
			getLogger().error(MODULE, "Can't process " + getType() + " transaction. Reason: transaction is not in " + TransactionState.IDLE + " state");
			}				
	}
	
	@Override
	public synchronized void resume(DiameterAnswer answer) {
		if (getLogger().isLogLevel(LogLevel.DEBUG))
			getLogger().debug(MODULE, "Diameter Answer Received(Transaction-ID: " + getTransactionId() + " ) : " + answer);
		
		getTransactionSession().put(SessionKeys.RAA_PACKET, answer);
		
		if (TransactionState.WAIT_RULE_INSTALL_ACK == this.transactionState) {
			IDiameterAVP resultCodeAvp = answer.getAVP(DiameterAVPConstants.RESULT_CODE);
			
			if (resultCodeAvp != null && ResultCode.DIAMETER_SUCCESS.code == resultCodeAvp.getInteger()) {
				// SendSuccessAckToAFAction
				ActionHandler actionHandler = getActionHandler(EventTypes.RULE_INSTALL_SUCCESS);
				if (getLogger().isDebugLogLevel())
					getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());
				
				this.transactionState = actionHandler.handle();

			} else {
				// SendFailureAckToAFAction
				ActionHandler actionHandler = getActionHandler(EventTypes.RULE_INSTALL_FAIL);
				if (getLogger().isDebugLogLevel())
					getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());
					
				this.transactionState = actionHandler.handle();
			}
		}
	}

	/**
	 * Resuming NewServiceTransaction processing when RAR request is timeout and perform action on event {@link EventTypes} RULE_INSTALL_FAILUER
	 */
	@Override
	public synchronized void resume(DiameterRequest request) {
		if(getLogger().isLogLevel(LogLevel.DEBUG))
			getLogger().debug(MODULE, "Timeout Request Received(Transaction-ID: "+ getTransactionId()  +" ) : " + request);
		
		getTransactionContext().getTransactionSession().put(SessionKeys.RAR_PACKET, request);
		
		if(TransactionState.WAIT_RULE_INSTALL_ACK.equals(this.transactionState)){
			ActionHandler actionHandler = getActionHandler(EventTypes.RULE_INSTALL_FAIL);
			if(getLogger().isLogLevel(LogLevel.INFO))
				getLogger().info(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());		
			this.transactionState = actionHandler.handle();
		} else {
			getLogger().error(MODULE, "Can't resume " + getType() + ". Reason: No Action for Transaction-State = " + transactionState);
		}
	}

	@Override
	public synchronized void resume(PCRFResponse response) {
		if(getLogger().isLogLevel(LogLevel.DEBUG))
			getLogger().debug(MODULE, "Response Received(Transaction-ID: "+ getTransactionId()  +" ) from PCRF Service: " + response);
		
		if(TransactionState.WAIT_FOR_AUTH_RES == this.transactionState){			
			
			getTransactionSession().put(SessionKeys.PCRF_RESPONSE, response);
			
			if(PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val.equals(response.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()))){
				// RuleInstallAction
				ActionHandler actionHandler = getActionHandler(EventTypes.SUCCESS_RES_RCVD);
				if(getLogger().isLogLevel(LogLevel.DEBUG))
					getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());		
				this.transactionState = actionHandler.handle();
			}else{
				// SendFailureAckToAFAction
				ActionHandler actionHandler = getActionHandler(EventTypes.FAILED_RES_RCVD);
				if(getLogger().isLogLevel(LogLevel.DEBUG))
					getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());		
				this.transactionState = actionHandler.handle();
			}			
		} else {
			getLogger().error(MODULE, "Can't resume " + getType() + ". Reason: No Action for Transaction-State = " + transactionState);
		}
	}
	
	private ActionHandler getActionHandler(String event){
		TransactionAction transactionAction = actionHandlersMap.get(event);
		return transactionAction.createActionHandler(getTransactionContext());
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new NewServiceTransaction(getControllerContext());
	}

}
	
