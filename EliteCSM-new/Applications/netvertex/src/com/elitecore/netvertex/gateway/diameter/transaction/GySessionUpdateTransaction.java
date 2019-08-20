package com.elitecore.netvertex.gateway.diameter.transaction;

import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.core.transaction.BaseTransaction;
import com.elitecore.netvertex.core.transaction.EventTypes;
import com.elitecore.netvertex.core.transaction.TransactionAction;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.application.handler.tgpp.DiameterAnswerListener;
import com.elitecore.netvertex.gateway.diameter.application.handler.tgpp.GyTransactionType;
import com.elitecore.netvertex.gateway.diameter.transaction.action.ActionHandler;
import com.elitecore.netvertex.gateway.diameter.transaction.session.SessionKeys;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.HashMap;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class GySessionUpdateTransaction extends BaseTransaction {

	public static final String MODULE = "GY-SESS-UPDT-TRNSCT";
	private static Map<String, TransactionAction> actionHandlersMap;

	static {
		actionHandlersMap = new HashMap<String, TransactionAction>(1, 1);
		actionHandlersMap.put(EventTypes.SESSION_UPDATE, TransactionAction.GY_AUTHORIZE);
		actionHandlersMap.put(EventTypes.PCRF_RES_RCVD, TransactionAction.GY_SEND_CCA);

	}

	public GySessionUpdateTransaction(DiameterGatewayControllerContext diameterGatewayControllerContext) {
		super(diameterGatewayControllerContext);
	}

	@Override
	public String getType() {
		return GyTransactionType.SESSION_UPDATE.name();
	}

	@Override
	public synchronized void process(String event, DiameterRequest request) {
		throw new UnsupportedOperationException("Process method is deprecated");
	}

	@Override
	public synchronized void process(String eventType, DiameterRequest request, DiameterAnswerListener diameterAnswerListener) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Event Received: " + eventType + " for Transaction-Id: " + getTransactionId());
		}

		getTransactionSession().put(SessionKeys.DIAMETER_REQUEST, request);
		getTransactionSession().put(SessionKeys.DIAMETER_ANSWER_LISTENER, diameterAnswerListener);

		if (TransactionState.IDLE != this.transactionState) {
			getLogger().error(MODULE, "Can't process " + getType() + " transaction. Reason: transaction is not in " + TransactionState.IDLE + " state");
			return;
		}

		if (EventTypes.SESSION_UPDATE.equals(eventType) == false) {
			getLogger().error(MODULE, "Can't process " + getType() + " transaction. Reason: expected event = " + EventTypes.SESSION_START
					+ " while event = " + eventType + " received ");
			return;
		}

		ActionHandler actionHandler = getActionHandler(eventType);
		if (actionHandler == null) {
			getLogger().error(MODULE, "Can't process " + getType() + " transaction. Reason: no Action found for event = " + eventType);
			return;
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());
		}
		
		this.transactionState = actionHandler.handle();
	}

	@Override
	public synchronized void resume(PCRFResponse response) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Response Received(Transaction-ID: " + getTransactionId() + " ) from PCRF Service: " + response);
		}
		getTransactionSession().put(SessionKeys.PCRF_RESPONSE, response);
		
		if (TransactionState.WAIT_FOR_AUTH_RES == this.transactionState) {
			ActionHandler actionHandler = getActionHandler(EventTypes.PCRF_RES_RCVD);

			if (actionHandler == null) {
				getLogger().error(MODULE, "Can't resume " + getType() + ". Reason: No Action for Event = " + EventTypes.PCRF_RES_RCVD);
				return;
			}

			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());
			}
			this.transactionState = actionHandler.handle();
		} else {
			getLogger().error(MODULE, "Can't resume " + getType() + ". Reason: No Action for Transaction-State = " + transactionState);
		}

	}

	private ActionHandler getActionHandler(String event) {
		TransactionAction transactionAction = actionHandlersMap.get(event);
		if (transactionAction == null) {
			return null;
		}
		return transactionAction.createActionHandler(getTransactionContext());
	}

	@Override
	public Object clone() {
		return new GySessionUpdateTransaction(getControllerContext());
	}

}
