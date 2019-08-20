package com.elitecore.netvertex.gateway.diameter.transaction;

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

import java.util.HashMap;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Transaction for Session ReAuthorization
 * <table>
 * <tr>
 * <td><b>STATE</b></td><td><b>EVENT</b></td><td><b>ACTION</b></td><td><b>NEXT-STATE</b></td>
 * </tr>
 * <tr>
 * <td>IDLE</td><td>SESSION_RE_AUTH</td><td>RuleRemoveAction(SubmitPCRFRequest)</td><td>WAIT_RULE_REMOVE_ACK</td>
 * </tr>
 * <tr>
 * <td>WAIT_RULE_REMOVE_ACK</td><td>PCRF_RES_RCVD</td><td>SessionReAuthAction(SessionRule-Lookup,GenerateRAR,SendRAR)</td><td>Complete</td>
 * </tr>
 * <p>
 * </table>
 *
 * @author Harsh
 */
public class SessionReAuthTransaction extends BaseTransaction {

    private static final String MODULE = "SESS-RE-AUTH-TRNSCT";
    private static Map<String, TransactionAction> actionHandlersMap;

    static {
        actionHandlersMap = new HashMap<String, TransactionAction>(1, 1);
        actionHandlersMap.put(EventTypes.SESSION_RE_AUTH, TransactionAction.RULE_REMOVE);
        actionHandlersMap.put(EventTypes.PCRF_RES_RCVD, TransactionAction.SESSION_RE_AUTH);
        actionHandlersMap.put(EventTypes.RULE_INSTALL_SUCCESS, TransactionAction.SESSION_UPDATE_ACTION);
    }


    public SessionReAuthTransaction(DiameterGatewayControllerContext diameterGatewayControllerContext) {
        super(diameterGatewayControllerContext);
    }

    @Override
    public String getType() {
        return TransactionType.SESSION_RE_AUTH;
    }

    @Override
    public synchronized void process(String eventType, PCRFRequest request) {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
            LogManager.getLogger().debug(MODULE, "Event Received: " + eventType + " for Transaction-Id: " + getTransactionId());
        getTransactionSession().put(SessionKeys.PCRF_REQUEST, request);
        if (TransactionState.IDLE == this.transactionState) {
            if (!EventTypes.SESSION_RE_AUTH.equals(eventType)) {
                LogManager.getLogger().error(MODULE, "Can't process " + getType() + " transaction. Reason: expected event = " + EventTypes.SESSION_RE_AUTH
                        + " while event = " + eventType + " received ");
                return;
            }
            // AuthorizeAction
            ActionHandler actionHandler = getActionHandler(eventType);
            if (actionHandler == null) {
                LogManager.getLogger().error(MODULE, "Can't process " + getType() + " transaction. Reason: no Action found for event = " + eventType);
                return;
            }
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());
            }
            this.transactionState = actionHandler.handle();
        } else {
            LogManager.getLogger().error(MODULE, "Can't process " + getType() + " transaction. Reason: transaction is not in " + TransactionState.IDLE + " state");
        }

    }

    @Override
    public synchronized void resume(PCRFResponse response) {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
            LogManager.getLogger().debug(MODULE, "Response Received(Transaction-ID: " + getTransactionId() + " ) from PCRF Service: " + response);

        getTransactionSession().put(SessionKeys.PCRF_RESPONSE, response);

        if (TransactionState.WAIT_RULE_REMOVE_ACK == this.transactionState) {
            ActionHandler actionHandler = getActionHandler(EventTypes.PCRF_RES_RCVD);
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                LogManager.getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());
            this.transactionState = actionHandler.handle();
        } else {
            LogManager.getLogger().error(MODULE, "Can't resume " + getType() + ". Reason: No Action for Transaction-State = " + transactionState);
        }

    }

    @Override
    public synchronized void resume(DiameterAnswer answer) {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, answer.toString());
        }

        // RAA Received
        if (TransactionState.WAIT_FOR_RAA == transactionState) {
            if (isSuccess(answer)) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "TransactionId: " + getTransactionId() + " is completed.");
                }

                ActionHandler actionHandler = getActionHandler(EventTypes.RULE_INSTALL_SUCCESS);
                if (getLogger().isDebugLogLevel())
                    getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());

                actionHandler.handle();
            }
        } else {
            getLogger().error(MODULE, "Can't resume " + getType() + ". Reason: No Action for Transaction-State = " + transactionState);
        }
    }

    private ActionHandler getActionHandler(String event) {
        TransactionAction transactionAction = actionHandlersMap.get(event);
        return transactionAction.createActionHandler(getTransactionContext());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new SessionReAuthTransaction(getControllerContext());
    }

}