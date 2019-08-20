package com.elitecore.netvertex.gateway.diameter.transaction;
/**
 * Transaction for Service Registration
 * <table>
 * <tr>
 * <td>STATE</td><td>EVENT</td><td>ACTION</td><td>NEXT-STATE</td>
 * </tr>
 * <tr>
 * <td>IDLE</td><td>AAR_RCVD</td><td>RxAuthorize (CreatePCRFReq,Fetch(Gx/Rx)Session,SubmitReq)</td><td>Wait-For-Auth-Res</td>
 * </tr>
 * <tr>
 * <td>Wait-For-Auth-Res</td><td>SUCCESS_RES_RCVD</td><td>SEND-SUCCESS-ACK-TO-AF (CreateAAA,SendAAA)</td><td>COMPLETE</td>
 * </tr>
 * <tr>
 * <td>Wait-For-Auth-Res</td><td>FAILED_RES_RCVD</td><td>SEND-FAILURE-ACK-TO-AF (CreateAAA,SendAAA)</td><td>COMPLETE</td>
 * </tr>
 * <p>
 * </table>
 *
 * @author harsh
 */

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.GatewayTypeConstant;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
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

public class ServiceRegistrationTransaction extends BaseTransaction {
    private static final String MODULE = "SER-REG-TRNSCT";
    private static Map<String, TransactionAction> actionHandlersMap;

    static {
        actionHandlersMap = new HashMap<String, TransactionAction>(1, 1);
        actionHandlersMap.put(EventTypes.AAR_RCVD, TransactionAction.RX_AUTHORIZE);
        actionHandlersMap.put(EventTypes.SUCCESS_RES_RCVD, TransactionAction.SEND_SUCCESS_ACK_TO_AF);
        actionHandlersMap.put(EventTypes.FAILED_RES_RCVD, TransactionAction.SEND_FAILURE_ACK_TO_AF);
    }

    public ServiceRegistrationTransaction(DiameterGatewayControllerContext diameterGatewayControllerContext) {
        super(diameterGatewayControllerContext);


    }

    @Override
    public String getType() {
        return TransactionType.SERVICE_REG;
    }

    @Override
    public synchronized void process(String eventType, DiameterRequest request) {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
            LogManager.getLogger().debug(MODULE, "Event Received: " + eventType + " for Transaction-Id: " + getTransactionId());
        getTransactionSession().put(SessionKeys.SESSION_TYPE, SessionTypeConstant.RX.getVal());
        getTransactionSession().put(SessionKeys.GATEWAY_TYPE, GatewayTypeConstant.DIAMETER.getVal());
        getTransactionSession().put(SessionKeys.DIAMETER_REQUEST, request);

        if (TransactionState.IDLE == this.transactionState) {
            if (!EventTypes.AAR_RCVD.equals(eventType)) {
                LogManager.getLogger().error(MODULE, "Can't process " + getType() + " transaction. Reason: expected event = " + EventTypes.AAR_RCVD
                        + " while event = " + eventType + " received ");
                return;
            }

            // AuthorizeAction
            ActionHandler actionHandler = getActionHandler(eventType);
            if (actionHandler == null) {
                LogManager.getLogger().error(MODULE, "Can't process " + getType() + " transaction. Reason: no Action found for event = " + eventType);
                return;
            }

            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                LogManager.getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());
            this.transactionState = actionHandler.handle();
        } else {
            LogManager.getLogger().error(MODULE, "Can't process " + getType() + " transaction. Reason: transaction is not in " + TransactionState.IDLE + " state");
        }
    }

    @Override
    public synchronized void resume(PCRFResponse response) {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
            LogManager.getLogger().debug(MODULE, "Response Received(Transaction-ID: " + getTransactionId() + " ) from PCRF Service: " + response);

        if (TransactionState.WAIT_FOR_AUTH_RES == this.transactionState) {

            getTransactionSession().put(SessionKeys.PCRF_RESPONSE, response);

            if (PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val.equals(response.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()))) {
                // SendSuccessAckToAFAction
                ActionHandler actionHandler = getActionHandler(EventTypes.SUCCESS_RES_RCVD);
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                    LogManager.getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());
                this.transactionState = actionHandler.handle();
            } else {
                // SendFailureAckToAFAction
                ActionHandler actionHandler = getActionHandler(EventTypes.FAILED_RES_RCVD);
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                    LogManager.getLogger().debug(MODULE, "Performing action " + actionHandler.getName() + " for TransactionId: " + getTransactionId());
                this.transactionState = actionHandler.handle();
            }
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
        return new ServiceRegistrationTransaction(getControllerContext());
    }

}
