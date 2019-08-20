package com.elitecore.netvertex.gateway.diameter.transaction;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.GatewayTypeConstant;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
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

import static com.elitecore.commons.logging.LogManager.getLogger;

/***
 *
 * Transaction for Session Start Request (User Log-On)
 *<table>
 * <tr>
 * 		<td>STATE</td><td>EVENT</td><td>ACTION</td><td>NEXT-STATE</td>
 * </tr>
 * <tr>
 * 		<td>IDLE</td><td>SESSION_START</td><td>AUTHORIZE (CreatePCRFReq,SubmitReq)</td><td>Wait-For-Auth-Res</td>
 * </tr>
 * <tr>
 * 		<td>Wait-For-Auth-Res</td><td>SUCCESS_RES_RCVD</td><td>RULE_PROVISION (GenerateCCA,SendCCA, GenerateRAR, SendRAR)</td><td>COMPLETE/Wait-Rule-Install-Ack</td>
 * </tr>
 * <tr>
 * 		<td>Wait-For-Auth-Res</td><td>FAILED_RES_RCVD</td><td>SendAckOfCCR</td><td>COMPLETE</td>
 * </tr>
 * <tr>
 * 		<td>Wait-Rule-Install-Ack</td><td>RULE-INSTALL-SUCCESS</td><td> Log </td><td>COMPLETE</td>
 * </tr>
 * <tr>
 * 		<td>Wait-Rule-Install-Ack</td><td>RULE-INSTALL-FAILED</td><td> REMOVE_FAILED_PCC_RULES</td><td>Wait-Rule-Remove-Ack</td>
 * </tr>
 * <tr>
 * 		<td>Wait-Rule-Remove-Ack</td><td>RULE-REMOVED</td><td> Log</td><td>COMPLETE</td>
 * </tr>

 *</table>
 * @author jatin
 */


public class SessionStartTransaction extends BaseTransaction {

    private static final String MODULE = "SES-STRT-TRNSCT";
    private static Map<String, TransactionAction> actionHandlersMap;

    static {
        actionHandlersMap = new HashMap<String, TransactionAction>(1, 1);
        actionHandlersMap.put(EventTypes.SESSION_START, TransactionAction.AUTHORIZE);

        actionHandlersMap.put(EventTypes.SUCCESS_RES_RCVD, TransactionAction.RULE_PROVISION);

        actionHandlersMap.put(EventTypes.FAILED_RES_RCVD, TransactionAction.SEND_CCA);

        actionHandlersMap.put(EventTypes.RULE_INSTALL_FAIL, TransactionAction.REMOVE_FAILED_PCC_RULES);

        actionHandlersMap.put(EventTypes.RULE_INSTALL_SUCCESS, TransactionAction.SESSION_UPDATE_ACTION);
    }

    public SessionStartTransaction(DiameterGatewayControllerContext diameterGatewayControllerContext) {
        super(diameterGatewayControllerContext);

    }

    @Override
    public String getType() {
        return TransactionType.SESSION_START;
    }

    @Override
    public synchronized void process(String eventType, DiameterRequest request) {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
            LogManager.getLogger().debug(MODULE, "Event Received: " + eventType + " for Transaction-Id: " + getTransactionId());
        if (request.getApplicationID() == ApplicationIdentifier.TGPP_S9.applicationId) {
            getTransactionSession().put(SessionKeys.SESSION_TYPE, SessionTypeConstant.S9.getVal());
        } else {
            getTransactionSession().put(SessionKeys.SESSION_TYPE, SessionTypeConstant.GX.getVal());
        }

        getTransactionSession().put(SessionKeys.GATEWAY_TYPE, GatewayTypeConstant.DIAMETER.getVal());
        getTransactionSession().put(SessionKeys.DIAMETER_REQUEST, request);

        if (TransactionState.IDLE == this.transactionState) {

            if (!EventTypes.SESSION_START.equals(eventType)) {
                LogManager.getLogger().error(MODULE, "Can't process " + getType() + " transaction. Reason: expected event = " + EventTypes.SESSION_START
                        + " while event = " + eventType + " received ");
                return;
            }

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
    public synchronized void resume(DiameterAnswer answer) {
        // RAA Rcvd
        if (TransactionState.WAIT_RULE_INSTALL_ACK == transactionState) {
            if (isSuccess(answer)) {
                // EventTypes.RULE_INSTALL_SUCCESS Action: Log
                if (getLogger().isDebugLogLevel())
                    getLogger().debug(MODULE, "TransactionId: " + getTransactionId() + " is completed");

                ActionHandler actionHandler = getActionHandler(EventTypes.RULE_INSTALL_SUCCESS);
                if (getLogger().isDebugLogLevel())
                    getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());

                actionHandler.handle();

            } else {
                getTransactionSession().put(SessionKeys.DIAMETER_RESPONSE, answer);

                ActionHandler actionHandler = getActionHandler(EventTypes.RULE_INSTALL_FAIL);
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                    LogManager.getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());
                this.transactionState = actionHandler.handle();
            }
        } else {
            LogManager.getLogger().error(MODULE, "Can't resume " + getType() + ". Reason: No Action for Transaction-State = " + transactionState);
        }
    }

    @Override
    public synchronized void resume(PCRFResponse response) {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
            LogManager.getLogger().debug(MODULE, "Response Received(Transaction-ID: " + getTransactionId() + " ) from PCRF Service: " + response);

        getTransactionSession().put(SessionKeys.PCRF_RESPONSE, response);

        if (TransactionState.WAIT_FOR_AUTH_RES == this.transactionState) {
            if (isSuccess(response)) {
                // Success-Res-Rcvd from PCRF
                ActionHandler actionHandler = getActionHandler(EventTypes.SUCCESS_RES_RCVD);
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                    LogManager.getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());
                this.transactionState = actionHandler.handle();
            } else {
                // Failed-Res-Rcvd from PCRF
                // Send Failed ACK as CCA
                ActionHandler actionHandler = getActionHandler(EventTypes.FAILED_RES_RCVD);
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                    LogManager.getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());
                this.transactionState = actionHandler.handle();
            }
        } else if (TransactionState.WAIT_RULE_REMOVE_ACK == transactionState) {

            // EventTypes.RULE-REMOVED Action: LOG
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                LogManager.getLogger().debug(MODULE, "Rule removed response rcvd, TransactionId: " + getTransactionId() + " is Completed");
        } else {
            LogManager.getLogger().error(MODULE, "Can't resume " + getType() + ". Reason: No Action for Transaction-State = " + transactionState);
        }

    }

    private ActionHandler getActionHandler(String event) {
        TransactionAction transactionAction = actionHandlersMap.get(event);
        return transactionAction.createActionHandler(getTransactionContext());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new SessionStartTransaction(getControllerContext());
    }

}
