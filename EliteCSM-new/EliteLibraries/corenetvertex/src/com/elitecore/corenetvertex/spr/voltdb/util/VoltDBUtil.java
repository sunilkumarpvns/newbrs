package com.elitecore.corenetvertex.spr.voltdb.util;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.db.VoltDBClient;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcCallException;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class VoltDBUtil {

    public static void handleProcedureCallException(ProcCallException e, String message,
                                                    String module, AlertListener alertListener, AtomicInteger totalQueryTimeoutCount, VoltDBClient voltDBClient) throws OperationFailedException {

        ClientResponse clientResponse = e.getClientResponse();
        if(Objects.isNull(clientResponse)) {
            throw new OperationFailedException("Error while getting client response");
        }

        switch (clientResponse.getStatus()) {
            case ClientResponse.CONNECTION_LOST:
                alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, module,
                        "Error while " + message);
                throw new OperationFailedException("Error while " + message + "." + createErrorMessage(clientResponse),
                        ResultCode.SERVICE_UNAVAILABLE);

            case ClientResponse.CONNECTION_TIMEOUT:
                generateAlertForConnectionTimeout(message, module, alertListener, totalQueryTimeoutCount, voltDBClient);
                throw new OperationFailedException("Error while " + message + "." + createErrorMessage(clientResponse),
                        ResultCode.SERVICE_UNAVAILABLE);

            case ClientResponse.SERVER_UNAVAILABLE:
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(module, "VoltDB is Dead. Reason: Service is not available");
                }
                throw new OperationFailedException("Error while " + message + "." + createErrorMessage(clientResponse),
                        ResultCode.SERVICE_UNAVAILABLE);
            default:
                throw new OperationFailedException("Error while " + message + "." + createErrorMessage(clientResponse));
        }
    }

    private static void generateAlertForConnectionTimeout(String message, String module, AlertListener alertListener, AtomicInteger totalQueryTimeoutCount, VoltDBClient voltDBClient) {
        if (totalQueryTimeoutCount.incrementAndGet() > CommonConstants.MAX_QUERY_TIMEOUT_COUNT_DEFAULT) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(module, "total number of query timeouts exceeded then configured max number of query timeouts,so SPR marked as DEAD");
            }
            voltDBClient.markDead();
            totalQueryTimeoutCount.set(0);
        }
        alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, module,
                "Error while " + message);
    }

    public static String createErrorMessage(ClientResponse clientResponse) {
        return "Error Code: " + clientResponse.getStatus()
                + ". Reason: " + clientResponse.getStatusString();
    }

    public static Object createVoltDBArgsForSubscriberProfile(SPRInfo sprInfo) {
        String[] array = new String[SPRFields.values().length - 4];

        int i = 0;

        for (SPRFields sprFields : SPRFields.values()) {

            /*
             * These fields will be skipped from here
             * modified & created date add directly from stored procedure
             * birth date & expiry date will be passed as parameter for procedure call
             */
            if (sprFields == SPRFields.MODIFIED_DATE
                    || sprFields == SPRFields.CREATED_DATE
                    || sprFields == SPRFields.EXPIRY_DATE
                    || sprFields == SPRFields.BIRTH_DATE
                    ) {
                continue;
            }
            array[i++] = Strings.isNullOrBlank(sprFields.getStringValue(sprInfo)) ? null : sprFields.getStringValue(sprInfo);
        }

        return array;
    }


    public static Object createAddMonetaryBalanceArray(MonetaryBalance monetaryBalance) {
        Object[] addMonetaryBalanceArray = new Object[12];
        addMonetaryBalanceArray[0] = monetaryBalance.getId();
        addMonetaryBalanceArray[1] = monetaryBalance.getSubscriberId();
        addMonetaryBalanceArray[2] = monetaryBalance.getServiceId();
        addMonetaryBalanceArray[3] = String.valueOf(monetaryBalance.getAvailBalance());
        addMonetaryBalanceArray[4] = String.valueOf(monetaryBalance.getInitialBalance());
        addMonetaryBalanceArray[5] = String.valueOf(monetaryBalance.getTotalReservation());
        addMonetaryBalanceArray[6] = String.valueOf(monetaryBalance.getCreditLimit());
        addMonetaryBalanceArray[7] = String.valueOf(monetaryBalance.getNextBillingCycleCreditLimit());
        addMonetaryBalanceArray[8] = monetaryBalance.getCurrency();
        addMonetaryBalanceArray[9] = monetaryBalance.getType();
        addMonetaryBalanceArray[10] = monetaryBalance.getParameter1();
        addMonetaryBalanceArray[11] = monetaryBalance.getParameter2();

        return addMonetaryBalanceArray;
    }
}
