package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;

import java.util.Objects;

public class NonFinalRnCNonMonetaryReportedQuotaProcessor extends RnCReportedQuotaProcessor {

    private MSCC reportedUsage;
    private MSCC quotaReservationEntry;
    private RnCNonMonetaryBalance rnCNonMonetaryBalance;
    private RnCNonMonetaryBalance abmfCopyOfRnCNonMonetaryBalance;
    private ExecutionContext executionContext;
    private MSCC previousUnAccountedQuota;
    private MSCC unAccountedQuota;
    private ReportedUsageSummary reportedUsageSummary;

    public NonFinalRnCNonMonetaryReportedQuotaProcessor(MSCC reportedUsage,
                                             MSCC quotaReservationEntry,
                                             MSCC unAccountedQuota,
                                             RnCNonMonetaryBalance rnCNonMonetaryBalance,
                                             ExecutionContext executionContext,
                                             PolicyRepository policyRepository,
                                             RnCNonMonetaryBalance abmfCopyOfRnCNonMonetaryBalance) {
        super(policyRepository);
        this.reportedUsage = reportedUsage;
        this.quotaReservationEntry = quotaReservationEntry;
        this.rnCNonMonetaryBalance = rnCNonMonetaryBalance;
        this.executionContext = executionContext;
        this.previousUnAccountedQuota = unAccountedQuota;
        this.abmfCopyOfRnCNonMonetaryBalance = abmfCopyOfRnCNonMonetaryBalance;
        this.reportedUsageSummary = new ReportedUsageSummary(quotaReservationEntry.getRatingGroup(), quotaReservationEntry.getServiceIdentifiers());
    }

    @Override
    public void handle() {

        GyServiceUnits grantedServiceUnits = quotaReservationEntry.getGrantedServiceUnits();

        if(grantedServiceUnits.isNonMonetaryReservationRequired()) {
            reportedUsageSummary.setReserveTime(grantedServiceUnits.getTime());
        }

        reportedUsageSummary.setReportingReason(reportedUsage.getReportingReason());
        reportedUsageSummary.setReportOperation(ReportOperation.fromReportingReason(reportedUsage.getReportingReason()));

        reportedUsageSummary.setTimePulse(grantedServiceUnits.getTimePulse());

        reportedUsageSummary.setRateCardName(grantedServiceUnits.getRateCardName());
        reportedUsageSummary.setRateCardId(grantedServiceUnits.getRateCardId());
        reportedUsageSummary.setRateCardGroupName(grantedServiceUnits.getRateCardGroupName());
        reportedUsageSummary.setPackageId(grantedServiceUnits.getPackageId(), PolicyManager.getInstance().getPackageName(grantedServiceUnits.getPackageId()));
        reportedUsageSummary.setProductOfferName(Objects.isNull(PolicyManager.getInstance().getProductOffer().byId(grantedServiceUnits.getProductOfferId())) ? null :
                PolicyManager.getInstance().getProductOffer().byId(grantedServiceUnits.getProductOfferId()).getName());
        reportedUsageSummary.setRevenueCode(grantedServiceUnits.getRevenueCode());


        deductQuotaFromCurrentInMemoryUsage(grantedServiceUnits);
        deductQuotaFromABMFUsage(grantedServiceUnits);
    }

    private void deductQuotaFromABMFUsage(GyServiceUnits grantedServiceUnits) {

        long time = reportedUsage.getUsedServiceUnits().getTime();
        reportedUsageSummary.setReportedTime(time);

        if (previousUnAccountedQuota != null) {
            reportedUsageSummary.setPreviousUnAccountedTime(previousUnAccountedQuota.getGrantedServiceUnits().getTime());
            time += previousUnAccountedQuota.getGrantedServiceUnits().getTime();
        }

        long calculatedTimePulse = RnCPulseCalculator.floor(time, grantedServiceUnits.getTimePulse());
        reportedUsageSummary.setCalculatedTimePulse(calculatedTimePulse);

        long deductableTime = RnCPulseCalculator.multiply(calculatedTimePulse, grantedServiceUnits.getTimePulse());
        reportedUsageSummary.setDeductedTimeBalance(deductableTime);

        RnCNonMonetaryOperationUtils.subtract(abmfCopyOfRnCNonMonetaryBalance, deductableTime, executionContext);

        RnCNonMonetaryOperationUtils.closeReservation(abmfCopyOfRnCNonMonetaryBalance, quotaReservationEntry.getGrantedServiceUnits());

        long unAccountedTime = time - deductableTime;
        if (unAccountedTime > 0) {
            unAccountedQuota = quotaReservationEntry.copy();
            GyServiceUnits unAccountedUnits = unAccountedQuota.getGrantedServiceUnits();
            unAccountedUnits.setTime(unAccountedTime);
            reportedUsageSummary.setCurrentUnAccountedTime(unAccountedTime);
        }
    }

    private void deductQuotaFromCurrentInMemoryUsage(GyServiceUnits grantedServiceUnits) {

        long deductableTimePulse;

        if (previousUnAccountedQuota != null) {
            deductableTimePulse = RnCPulseCalculator.ceil( (reportedUsage.getUsedServiceUnits().getTime() + previousUnAccountedQuota.getGrantedServiceUnits().getTime()), grantedServiceUnits.getTimePulse());
        } else {
            deductableTimePulse = RnCPulseCalculator.ceil(reportedUsage.getUsedServiceUnits().getTime(), grantedServiceUnits.getTimePulse());
        }

        long deductableTime = RnCPulseCalculator.multiply(deductableTimePulse, grantedServiceUnits.getTimePulse());

        RnCNonMonetaryOperationUtils.subtract(rnCNonMonetaryBalance, deductableTime, executionContext);

        RnCNonMonetaryOperationUtils.closeReservation(rnCNonMonetaryBalance, quotaReservationEntry.getGrantedServiceUnits());

    }


    @Override
    public MSCC getUnAccountedUsage() {
        return unAccountedQuota;
    }

    @Override
    public ReportedUsageSummary getReportedUsageSummary() {
        return reportedUsageSummary;
    }
}
