package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;

import java.util.Objects;

public class CloseRnCNonMonetaryUnAccountedQuotaHandler extends RnCReportedQuotaProcessor {

    private MSCC quotaReservationEntry;
    private RnCNonMonetaryBalance rnCNonMonetaryBalance;
    private RnCNonMonetaryBalance abmfCopyOfRnCNonMonetaryBalance;
    private ExecutionContext executionContext;
    private ReportedUsageSummary reportedUsageSummary;

    public CloseRnCNonMonetaryUnAccountedQuotaHandler(MSCC quotaReservationEntry,
                                           RnCNonMonetaryBalance rnCNonMonetaryBalance,
                                           ExecutionContext executionContext,
                                           PolicyRepository policyRepository,
                                           RnCNonMonetaryBalance abmfCopyOfRnCNonMonetaryBalance) {
        super(policyRepository);
        this.quotaReservationEntry = quotaReservationEntry;
        this.rnCNonMonetaryBalance = rnCNonMonetaryBalance;
        this.executionContext = executionContext;
        this.abmfCopyOfRnCNonMonetaryBalance = abmfCopyOfRnCNonMonetaryBalance;
        this.reportedUsageSummary = new ReportedUsageSummary(quotaReservationEntry.getRatingGroup(), quotaReservationEntry.getServiceIdentifiers());
    }


    @Override
    public void handle() {

        GyServiceUnits grantedServiceUnits = quotaReservationEntry.getGrantedServiceUnits();

        reportedUsageSummary.setReportOperation(ReportOperation.CLOSE_UNACCOUNTED_QUOTA);
        reportedUsageSummary.setTimePulse(grantedServiceUnits.getTimePulse());

        reportedUsageSummary.setPreviousUnAccountedTime(quotaReservationEntry.getGrantedServiceUnits().getTime());

        long calculatedTimePulse = RnCPulseCalculator.ceil(quotaReservationEntry.getGrantedServiceUnits().getTime(), grantedServiceUnits.getTimePulse());

        reportedUsageSummary.setCalculatedTimePulse(calculatedTimePulse);

        reportedUsageSummary.setRateCardName(grantedServiceUnits.getRateCardName());
        reportedUsageSummary.setRateCardId(grantedServiceUnits.getRateCardId());
        reportedUsageSummary.setRateCardGroupName(grantedServiceUnits.getRateCardGroupName());
        reportedUsageSummary.setPackageId(grantedServiceUnits.getPackageId(), PolicyManager.getInstance().getPackageName(grantedServiceUnits.getPackageId()));
        reportedUsageSummary.setProductOfferName(Objects.isNull(PolicyManager.getInstance().getProductOffer().byId(grantedServiceUnits.getProductOfferId())) ? null :
                PolicyManager.getInstance().getProductOffer().byId(grantedServiceUnits.getProductOfferId()).getName());


        long deductableTime = RnCPulseCalculator.multiply(calculatedTimePulse, grantedServiceUnits.getTimePulse());

        reportedUsageSummary.setDeductedTimeBalance(deductableTime);
        reportedUsageSummary.setRevenueCode(grantedServiceUnits.getRevenueCode());

        deduct(deductableTime);
    }

    private void deduct(long deductableTime) {

        RnCNonMonetaryOperationUtils.subtract(rnCNonMonetaryBalance,
                deductableTime,
                executionContext);
        RnCNonMonetaryOperationUtils.subtract(abmfCopyOfRnCNonMonetaryBalance, deductableTime, executionContext);
    }

    @Override
    public ReportedUsageSummary getReportedUsageSummary() {
        return reportedUsageSummary;
    }
}
