package com.elitecore.netvertex.rnc;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.data.ResultCode;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.util.ResetTimeUtility;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

/*
* To save reserved monetary and non-monetary balance in initial and update request.
* */
public class ABMFReservationOperationHandler {

    private static final String MODULE = "ABMF-RESERVATION-HNDLR";

    public void handle(PCRFRequest request, PCRFResponse response, ExecutionContext executionContext) {

        List<MonetaryBalance> monetaryBalanceList = null;
        List<NonMonetoryBalance> nonMonetoryBalanceList = null;

        if(Objects.isNull(response.getQuotaReservation())) {
            return;
        }

        if (Objects.equals(response.getAttribute(PCRFKeyConstants.RESERVATION_REQUIRED.val), PCRFKeyValueConstants.RESERVATION_REQUIRED_FALSE.val)) {


            for (Map.Entry<Long, MSCC> entry: response.getQuotaReservation().get()) {
                MSCC mscc = entry.getValue();

                if (mscc.getResultCode() != ResultCode.SUCCESS) {
                    continue;
                }

                mscc.getGrantedServiceUnits().setReservedMonetaryBalance(0);
                mscc.getGrantedServiceUnits().setReservationRequired(false);
            }
            return;
        }



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

            NonMonetoryBalance nonMonetoryBalance = getNonMonetaryReservation(mscc, response, nonMonetoryBalanceId);
            if(Objects.nonNull(nonMonetoryBalance)){
                if (Objects.isNull(nonMonetoryBalanceList)) {
                    nonMonetoryBalanceList = new ArrayList<>();
                }
                nonMonetoryBalanceList.add(nonMonetoryBalance);
            }
        }

        try {

            if(Objects.nonNull(monetaryBalanceList)) {
                executionContext.getDDFTable().getMonetaryBalanceOp().reserve(monetaryBalanceList);
            }

            if(Objects.nonNull(nonMonetoryBalanceList)){
                executionContext.getDDFTable().reserveBalance(nonMonetoryBalanceList.get(0).getSubscriberIdentity(), nonMonetoryBalanceList);
            }

        } catch (OperationFailedException ex) {
            getLogger().error(MODULE, "Error while performing reserve balance update. Reason:" + ex.getMessage());
            getLogger().trace(MODULE, ex);
        }

    }

    private NonMonetoryBalance getNonMonetaryReservation(MSCC mscc, PCRFResponse response, String nonMonetoryBalanceId) {
        if(mscc.getGrantedServiceUnits().isNonMonetaryReservationRequired() == false) {
            return null;
        }

        SubscriberNonMonitoryBalance subscriberNonMonitoryBalance = null;
        if (Objects.nonNull(nonMonetoryBalanceId)) {
            subscriberNonMonitoryBalance = response.getCurrentNonMonetoryBalance();

            if (subscriberNonMonitoryBalance == null) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Unable to update deducted non-monetary balance for Rg:" + mscc.getRatingGroup() + ". Reason: Non Monetory balance not found");
                }

                return null;
            }
        }

        if(mscc.getGrantedServiceUnits().getVolume() > 0 || mscc.getGrantedServiceUnits().getTime() > 0){

            if(Objects.nonNull(subscriberNonMonitoryBalance)){
                NonMonetoryBalance nonMonetoryBalance = subscriberNonMonitoryBalance.getBalanceById(nonMonetoryBalanceId);
                NonMonetoryBalance copyOfNonMonetoryBalance = nonMonetoryBalance.copy();
                copyOfNonMonetoryBalance.setReservationVolume(mscc.getGrantedServiceUnits().getVolume());
                copyOfNonMonetoryBalance.setReservationTime(mscc.getGrantedServiceUnits().getTime());
                return copyOfNonMonetoryBalance;
            }
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