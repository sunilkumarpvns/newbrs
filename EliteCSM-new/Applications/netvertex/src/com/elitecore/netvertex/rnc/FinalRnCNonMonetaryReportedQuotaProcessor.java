package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;

import java.util.Date;
import java.util.Objects;

public class FinalRnCNonMonetaryReportedQuotaProcessor extends RnCReportedQuotaProcessor {

    private MSCC reportedUsage;
    private MSCC quotaReservationEntry;
    private MSCC unAccountedQuota;
    private RnCNonMonetaryBalance rnCNonMonetaryBalance;
    private ExecutionContext executionContext;
    private RnCNonMonetaryBalance abmfCopyOfRnCNonMonetaryBalance;
    private ReportedUsageSummary reportedUsageSummary;


    public FinalRnCNonMonetaryReportedQuotaProcessor(MSCC reportedUsage,
                                          MSCC quotaReservationEntry,
                                          MSCC unAccountedQuota,
                                          RnCNonMonetaryBalance rnCNonMonetaryBalance,
                                          ExecutionContext executionContext,
                                          PolicyRepository policyRepository,
                                          RnCNonMonetaryBalance abmfCopyOfRnCNonMonetaryBalance) {
        super(policyRepository);
        this.reportedUsage = reportedUsage;
        this.quotaReservationEntry = quotaReservationEntry;
        this.unAccountedQuota = unAccountedQuota;
        this.rnCNonMonetaryBalance = rnCNonMonetaryBalance;
        this.executionContext = executionContext;
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


        reportedUsageSummary.setSessionStopTime(new Date(System.currentTimeMillis()));

        deductBalance(grantedServiceUnits);

    }


    private void deductBalance(GyServiceUnits grantedServiceUnits) {


        long time = reportedUsage.getUsedServiceUnits().getTime();

        reportedUsageSummary.setReportedTime(time);

        if (unAccountedQuota != null) {
            reportedUsageSummary.setPreviousUnAccountedTime(unAccountedQuota.getGrantedServiceUnits().getTime());
            time += unAccountedQuota.getGrantedServiceUnits().getTime();
        }

        long calculatedTimePulse = RnCPulseCalculator.ceil(time, grantedServiceUnits.getTimePulse());

        reportedUsageSummary.setCalculatedTimePulse(calculatedTimePulse);

        long deductableTime = RnCPulseCalculator.multiply(calculatedTimePulse, grantedServiceUnits.getTimePulse());

        reportedUsageSummary.setDeductedTimeBalance(deductableTime);

        RnCNonMonetaryOperationUtils.subtract(abmfCopyOfRnCNonMonetaryBalance, deductableTime, executionContext);

        RnCNonMonetaryOperationUtils.closeReservation(abmfCopyOfRnCNonMonetaryBalance, quotaReservationEntry.getGrantedServiceUnits());

        RnCNonMonetaryOperationUtils.subtract(rnCNonMonetaryBalance, deductableTime, executionContext);

        RnCNonMonetaryOperationUtils.closeReservation(rnCNonMonetaryBalance, quotaReservationEntry.getGrantedServiceUnits());
    }


    @Override
    public ReportedUsageSummary getReportedUsageSummary() {
        return reportedUsageSummary;
    }
}
