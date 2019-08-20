package com.elitecore.netvertex.rnc;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.rnc.MonetaryOperationUtils.BalanceDiff;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.netvertex.rnc.MonetaryOperationUtils.BalanceDiff.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ABMFOperationHandler {


    private static final String MODULE = "ABMF-OPERATION";


    public void abmfMonetaryOperation(ExecutionContext executionContext, PCRFResponse response, Map<String, MonetaryBalance> abmfCopyOfMonetaryBalance) {


        SubscriberMonetaryBalance subscriberOldMonetaryBalance = response.getPreviousMonetaryBalance();

        List<MonetaryBalance> directDebits = null;
        List<MonetaryBalance> reserveAndReports = null;
        List<MonetaryBalance> reserves = null;

        for (Entry<String, MonetaryBalance> stringMonetaryBalanceEntry : abmfCopyOfMonetaryBalance.entrySet()) {

            String id = stringMonetaryBalanceEntry.getKey();

            MonetaryBalance serviceBalance = subscriberOldMonetaryBalance.getBalanceById(id);
            MonetaryBalance newMonetaryBalance = stringMonetaryBalanceEntry.getValue();

            BalanceDiff balanceDiff = MonetaryOperationUtils.diff(serviceBalance, newMonetaryBalance);

            if (balanceDiff == NO_DIFF) {
                continue;
            }

            newMonetaryBalance.setTotalReservation(newMonetaryBalance.getTotalReservation() - serviceBalance.getTotalReservation());
            newMonetaryBalance.setAvailBalance(serviceBalance.getAvailBalance() - newMonetaryBalance.getAvailBalance());

            if (balanceDiff == BALANCE_DIFF) {
                directDebits = createIfNull(directDebits);
                directDebits.add(newMonetaryBalance);
            } else if (balanceDiff == RESERVATION_DIFF) {
                reserves = createIfNull(reserves);
                reserves.add(newMonetaryBalance);
            } else {
                reserveAndReports = createIfNull(reserveAndReports);
                reserveAndReports.add(newMonetaryBalance);
            }

            SubscriberMonetaryBalance accountedMonetaryBalance = response.getAccountedMonetaryBalance();
            if (isNull(accountedMonetaryBalance)) {
                accountedMonetaryBalance = new SubscriberMonetaryBalance(TimeSource.systemTimeSource());
                response.setAccountedMonetaryBalance(accountedMonetaryBalance);
            }

            accountedMonetaryBalance.addMonitoryBalances(newMonetaryBalance);
        }

        try {
            if (nonNull(directDebits)) {
                executionContext.getDDFTable().getMonetaryBalanceOp().directDebitBalance(directDebits);
            }


            if (nonNull(reserveAndReports)) {
                executionContext.getDDFTable().getMonetaryBalanceOp().reserveAndReport(reserveAndReports);
            }

            if (nonNull(reserves)) {
                executionContext.getDDFTable().getMonetaryBalanceOp().reserve(reserves);
            }
        } catch (OperationFailedException ex) {
            ///FIXME dump in CDR if fail
            getLogger().error(MODULE, "Error while performing ABMF Operation for monetary balance. Reason: " + ex.getMessage());
            getLogger().trace(MODULE, ex);
        }
    }

    private <T> List<T> createIfNull(List<T> directDebits) {
        if (Objects.isNull(directDebits)) {
            directDebits = new ArrayList<>(2);
        }
        return directDebits;
    }

    public void abmfNonMonetaryOperation(ExecutionContext executionContext,
                                         PCRFResponse pcrfResponse,
                                         Map<String, NonMonetoryBalance> abmfCopyOfNonMonetaryBalance) {
        List<NonMonetoryBalance> directDebitBalances = null;
        List<NonMonetoryBalance> reportAndReserveBalances = null;
        List<NonMonetoryBalance> reserveBalances = null;


        for (Entry<String, NonMonetoryBalance> nonMonetoryBalanceEntry : abmfCopyOfNonMonetaryBalance.entrySet()) {
            NonMonetoryBalance oldBalance = pcrfResponse.getPreviousNonMonetoryBalance().getBalanceById(nonMonetoryBalanceEntry.getKey());
            NonMonetoryBalance newBalance = nonMonetoryBalanceEntry.getValue();


            BalanceDiff diff = NonMonetaryOperationUtils.diff(oldBalance, newBalance);

            if (diff == NO_DIFF) {
                continue;
            }

            newBalance.setBillingCycleAvailableVolume(oldBalance.getBillingCycleAvailableVolume() - newBalance.getBillingCycleAvailableVolume());
            newBalance.setBillingCycleAvailableTime(oldBalance.getBillingCycleAvailableTime() - newBalance.getBillingCycleAvailableTime());
            newBalance.setDailyVolume(newBalance.getDailyVolume() - oldBalance.getDailyVolume());
            newBalance.setDailyTime(newBalance.getDailyTime() - oldBalance.getDailyTime());
            newBalance.setWeeklyVolume(newBalance.getWeeklyVolume() - oldBalance.getWeeklyVolume());
            newBalance.setWeeklyTime(newBalance.getWeeklyTime() - oldBalance.getWeeklyTime());
            newBalance.setReservationVolume(newBalance.getReservationVolume() - oldBalance.getReservationVolume());
            newBalance.setReservationTime(newBalance.getReservationTime() - oldBalance.getReservationTime());


            if (diff == BALANCE_DIFF) {
                directDebitBalances = createIfNull(directDebitBalances);
                directDebitBalances.add(newBalance);
            } else if (diff == RESERVATION_DIFF) {
                reserveBalances = createIfNull(reserveBalances);
                reserveBalances.add(newBalance);
            } else {
                reportAndReserveBalances = createIfNull(reportAndReserveBalances);
                reportAndReserveBalances.add(newBalance);
            }

            SubscriberNonMonitoryBalance accountedNonMonetaryBalance = pcrfResponse.getAccountedNonMonetaryBalance();
            if (isNull(accountedNonMonetaryBalance)) {
                accountedNonMonetaryBalance = new SubscriberNonMonitoryBalance(new ArrayList<>(2));
                pcrfResponse.setAccountedNonMonetaryBalance(accountedNonMonetaryBalance);
            }

            accountedNonMonetaryBalance.addBalance(newBalance);
        }

        try {
            if (nonNull(directDebitBalances)) {
                executionContext.getDDFTable().directDebitBalance(directDebitBalances.get(0).getSubscriberIdentity(),
                        directDebitBalances);
            }

            if (nonNull(reportAndReserveBalances)) {
                executionContext.getDDFTable().reportAndReserveBalance(reportAndReserveBalances.get(0).getSubscriberIdentity(),
                        reportAndReserveBalances);
            }

            if (nonNull(reserveBalances)) {
                executionContext.getDDFTable().reserveBalance(reserveBalances.get(0).getSubscriberIdentity(),
                        reserveBalances);
            }
        } catch (OperationFailedException ex) {
            ///FIXME dump in CDR if fail
            getLogger().error(MODULE, "Error while performing ABMF Operation for non-monetary balance. Reason: " + ex.getMessage());
            getLogger().trace(MODULE, ex);
        }

    }

    public void abmfRnCNonMonetaryOperation(ExecutionContext executionContext,
                                         PCRFResponse pcrfResponse,
                                         Map<String, RnCNonMonetaryBalance> abmfCopyOfRnCNonMonetaryBalance) {
        List<RnCNonMonetaryBalance> directDebitBalances = null;
        List<RnCNonMonetaryBalance> reportAndReserveBalances = null;
        List<RnCNonMonetaryBalance> reserveBalances = null;


        for (Entry<String, RnCNonMonetaryBalance> nonMonetoryBalanceEntry : abmfCopyOfRnCNonMonetaryBalance.entrySet()) {
            RnCNonMonetaryBalance oldBalance = pcrfResponse.getPreviousRnCNonMonetaryBalance().getBalanceById(nonMonetoryBalanceEntry.getKey());
            RnCNonMonetaryBalance newBalance = nonMonetoryBalanceEntry.getValue();


            BalanceDiff diff = RnCNonMonetaryOperationUtils.diff(oldBalance, newBalance);

            if (diff == NO_DIFF) {
                continue;
            }

            newBalance.setBillingCycleAvailable(oldBalance.getBillingCycleAvailable() - newBalance.getBillingCycleAvailable());
            newBalance.setDailyLimit(newBalance.getDailyLimit() - oldBalance.getDailyLimit());
            newBalance.setWeeklyLimit(newBalance.getWeeklyLimit() - oldBalance.getWeeklyLimit());
            newBalance.setReservationTime(newBalance.getReservationTime() - oldBalance.getReservationTime());

            if (diff == BALANCE_DIFF) {
                directDebitBalances = createIfNull(directDebitBalances);
                directDebitBalances.add(newBalance);
            } else if (diff == RESERVATION_DIFF) {
                reserveBalances = createIfNull(reserveBalances);
                reserveBalances.add(newBalance);
            } else {
                reportAndReserveBalances = createIfNull(reportAndReserveBalances);
                reportAndReserveBalances.add(newBalance);
            }

            SubscriberRnCNonMonetaryBalance accountedRnCNonMonetaryBalance = pcrfResponse.getAccountedRnCNonMonetaryBalance();
            if (isNull(accountedRnCNonMonetaryBalance)) {
                accountedRnCNonMonetaryBalance = new SubscriberRnCNonMonetaryBalance(new ArrayList<>(2));
                pcrfResponse.setAccountedRnCNonMonetaryBalance(accountedRnCNonMonetaryBalance);
            }

            accountedRnCNonMonetaryBalance.addBalance(newBalance);
        }

        try {
            if (nonNull(directDebitBalances)) {
                executionContext.getDDFTable().reportRnCBalance(directDebitBalances.get(0).getSubscriberIdentity(),
                        directDebitBalances);
            }

            if (nonNull(reportAndReserveBalances)) {
                executionContext.getDDFTable().reportAndReserveRnCBalance(reportAndReserveBalances.get(0).getSubscriberIdentity(),
                        reportAndReserveBalances);
            }

            if (nonNull(reserveBalances)) {
                executionContext.getDDFTable().reserveRnCBalance(reserveBalances.get(0).getSubscriberIdentity(),
                        reserveBalances);
            }
        } catch (OperationFailedException ex) {
            ///FIXME dump in CDR if fail
            getLogger().error(MODULE, "Error while performing ABMF Operation for non-monetary balance. Reason: " + ex.getMessage());
            getLogger().trace(MODULE, ex);
        }

    }
}
