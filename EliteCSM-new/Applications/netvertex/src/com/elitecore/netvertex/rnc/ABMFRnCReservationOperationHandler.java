package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.data.ResultCode;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

/*
* To save reserved monetary and non-monetary balance in initial and update request.
* */
public class ABMFRnCReservationOperationHandler {

    private static final String MODULE = "ABMF-RNC-RESERVATION-HNDLR";

    public void handle(PCRFResponse response, ExecutionContext executionContext) {

        if (Objects.equals(response.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()), PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val) == false) {
            return;
        }

        if (Objects.equals(response.getAttribute(PCRFKeyConstants.RESERVATION_REQUIRED.val), PCRFKeyValueConstants.RESERVATION_REQUIRED_FALSE.val)) {
            return;
        }

        if(Objects.isNull(response.getQuotaReservation())) {
            return;
        }

        List<MonetaryBalance> monetaryBalanceList = null;
        List<RnCNonMonetaryBalance> nonMonetaryBalanceList = null;

        for (Map.Entry<Long, MSCC> entry: response.getQuotaReservation().get()) {
            MSCC mscc = entry.getValue();

            if (mscc.getResultCode() != ResultCode.SUCCESS) {
                continue;
            }

            String monetoryBalanceId = mscc.getGrantedServiceUnits().getMonetaryBalanceId();
            String nonMonetoryBalanceId = mscc.getGrantedServiceUnits().getBalanceId();

            MonetaryBalance monetaryBalance = getMonetaryReservation(mscc, response, monetoryBalanceId);
            if(Objects.nonNull(monetaryBalance)){
                if(Objects.isNull(monetaryBalanceList)) {
                    monetaryBalanceList = new ArrayList<>();
                }
                monetaryBalanceList.add(monetaryBalance);
            }

            RnCNonMonetaryBalance rnCNonMonetaryBalance = getNonMonetaryReservation(mscc, response, nonMonetoryBalanceId);
            if(Objects.nonNull(rnCNonMonetaryBalance)){
                if (Objects.isNull(nonMonetaryBalanceList)) {
                    nonMonetaryBalanceList = new ArrayList<>();
                }
                nonMonetaryBalanceList.add(rnCNonMonetaryBalance);
            }
        }

        try {

            if(Objects.nonNull(monetaryBalanceList)) {
                executionContext.getDDFTable().getMonetaryBalanceOp().reserve(monetaryBalanceList);
            }

            if(Objects.nonNull(nonMonetaryBalanceList)){
                executionContext.getDDFTable().reserveRnCBalance(nonMonetaryBalanceList.get(0).getSubscriberIdentity(), nonMonetaryBalanceList);
            }

        } catch (OperationFailedException ex) {
            getLogger().error(MODULE, "Error while performing reserve balance update. Reason:" + ex.getMessage());
            getLogger().trace(MODULE, ex);
        }
    }

    private RnCNonMonetaryBalance getNonMonetaryReservation(MSCC mscc, PCRFResponse response, String nonMonetoryBalanceId) {
        if(mscc.getGrantedServiceUnits().isNonMonetaryReservationRequired() == false) {
            return null;
        }

        SubscriberRnCNonMonetaryBalance subscriberRnCNonMonetaryBalance = null;
        if (Objects.nonNull(nonMonetoryBalanceId)) {
            subscriberRnCNonMonetaryBalance = response.getCurrentRnCNonMonetaryBalance();

            if (subscriberRnCNonMonetaryBalance == null) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Unable to update deducted non-monetary balance for Rg:" + mscc.getRatingGroup() + ". Reason: Non Monetory balance not found");
                }

                return null;
            }
        }

        if(Objects.nonNull(subscriberRnCNonMonetaryBalance) && mscc.getGrantedServiceUnits().getTime() > 0){

            RnCNonMonetaryBalance rnCNonMonetaryBalance = subscriberRnCNonMonetaryBalance.getBalanceById(nonMonetoryBalanceId);
            RnCNonMonetaryBalance copyOfNonMonetoryBalance = rnCNonMonetaryBalance.copy();
            copyOfNonMonetoryBalance.setReservationTime(mscc.getGrantedServiceUnits().getTime());
            return copyOfNonMonetoryBalance;
        }

        return null;
    }
  
    private MonetaryBalance getMonetaryReservation(MSCC mscc, PCRFResponse response, String monetoryBalanceId) {
        if (mscc.getGrantedServiceUnits().getReservedMonetaryBalance() <= 0) {
            return null;
        }

        SubscriberMonetaryBalance subscriberMonetaryBalance = null;
        if (Objects.nonNull(monetoryBalanceId)) {
            subscriberMonetaryBalance = response.getCurrentMonetaryBalance();

            if (subscriberMonetaryBalance == null) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Unable to update deducted monetary balance for Rg:" + mscc.getRatingGroup() + ". Reason: Monetory balance not found");
                }

                return null;
            }
        }


        if(Objects.nonNull(subscriberMonetaryBalance)){
            MonetaryBalance monetaryBalance = subscriberMonetaryBalance.getBalanceById(monetoryBalanceId);
            MonetaryBalance copyOfMonetaryBalance = monetaryBalance.copy();
            copyOfMonetaryBalance.setTotalReservation(mscc.getGrantedServiceUnits().getReservedMonetaryBalance());

            return copyOfMonetaryBalance;
        }
        return null;
    }
}
