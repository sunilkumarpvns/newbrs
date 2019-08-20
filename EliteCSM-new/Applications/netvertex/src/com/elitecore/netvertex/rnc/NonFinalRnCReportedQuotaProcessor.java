package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;

import java.util.Objects;

public class NonFinalRnCReportedQuotaProcessor extends RnCReportedQuotaProcessor {

    private MSCC reportedUsage;
    private MSCC quotaReservationEntry;
    private MonetaryBalance monetaryBalance;
    private MonetaryBalance abmfCopyOfMonetaryBalance;
    private MSCC previousUnAccountedQuota;
    private MSCC unAccountedQuota;
    private ReportedUsageSummary reportedUsageSummary;

    public NonFinalRnCReportedQuotaProcessor(MSCC reportedUsage,
                                             MSCC quotaReservationEntry,
                                             MSCC unAccountedQuota,
                                             MonetaryBalance monetaryBalance,
                                             PolicyRepository policyRepository,
                                             MonetaryBalance abmfCopyOfMonetaryBalance) {
        super(policyRepository);
        this.reportedUsage = reportedUsage;
        this.quotaReservationEntry = quotaReservationEntry;
        this.monetaryBalance = monetaryBalance;
        this.previousUnAccountedQuota = unAccountedQuota;
        this.abmfCopyOfMonetaryBalance = abmfCopyOfMonetaryBalance;
        this.reportedUsageSummary = new ReportedUsageSummary(quotaReservationEntry.getRatingGroup(), quotaReservationEntry.getServiceIdentifiers());
    }

    @Override
    public void handle() {

        GyServiceUnits grantedServiceUnits = quotaReservationEntry.getGrantedServiceUnits();
        
        reportedUsageSummary.setReserveMonetaryBalance(grantedServiceUnits.getReservedMonetaryBalance());
        
        if(grantedServiceUnits.isNonMonetaryReservationRequired()) {
            reportedUsageSummary.setReserveTime(grantedServiceUnits.getTime());
        }
        reportedUsageSummary.setReportingReason(reportedUsage.getReportingReason());
        reportedUsageSummary.setReportOperation(ReportOperation.fromReportingReason(reportedUsage.getReportingReason()));
        
        reportedUsageSummary.setTimePulse(grantedServiceUnits.getTimePulse());
        reportedUsageSummary.setRateMinorUnit(grantedServiceUnits.getRateMinorUnit());

        reportedUsageSummary.setDiscount(grantedServiceUnits.getDiscount());
        reportedUsageSummary.setRate(grantedServiceUnits.getActualRate());
        reportedUsageSummary.setRateMinorUnit(grantedServiceUnits.getRateMinorUnit());

        reportedUsageSummary.setRateCardName(grantedServiceUnits.getRateCardName());
        reportedUsageSummary.setRateCardId(grantedServiceUnits.getRateCardId());
        reportedUsageSummary.setRateCardGroupName(grantedServiceUnits.getRateCardGroupName());
        reportedUsageSummary.setPackageId(grantedServiceUnits.getPackageId(), PolicyManager.getInstance().getPackageName(grantedServiceUnits.getPackageId()));
        reportedUsageSummary.setProductOfferName(Objects.isNull(PolicyManager.getInstance().getProductOffer().byId(grantedServiceUnits.getProductOfferId())) ? null :
                PolicyManager.getInstance().getProductOffer().byId(grantedServiceUnits.getProductOfferId()).getName());
        reportedUsageSummary.setRevenueCode(grantedServiceUnits.getRevenueCode());


        deductQuotaFromCurrentInMemoryUsage(grantedServiceUnits);
        deductQuotaFromABMFUsage(grantedServiceUnits);
 
        if(grantedServiceUnits.getReservedMonetaryBalance() >= 0) {
            reportedUsageSummary.setReserveMonetaryBalance(grantedServiceUnits.getReservedMonetaryBalance());
        }

    }

    private void deductQuotaFromABMFUsage(GyServiceUnits grantedServiceUnits) {
    	
        long time = reportedUsage.getUsedServiceUnits().getTime();

        reportedUsageSummary.setReportedTime(time);

        if (previousUnAccountedQuota != null) {
            reportedUsageSummary.setPreviousUnAccountedTime(previousUnAccountedQuota.getGrantedServiceUnits().getTime());
            time += previousUnAccountedQuota.getGrantedServiceUnits().getTime();
        }

        long calculatedTimePulse;

        calculatedTimePulse = RnCPulseCalculator.floor(time, grantedServiceUnits.getTimePulse());

        reportedUsageSummary.setCalculatedTimePulse(calculatedTimePulse);

        long deductableTime = RnCPulseCalculator.multiply(calculatedTimePulse, grantedServiceUnits.getTimePulse());

        long unAccountedTime = time - deductableTime;

        if (unAccountedTime > 0) {
            unAccountedQuota = quotaReservationEntry.copy();
            GyServiceUnits unAccountedUnits = unAccountedQuota.getGrantedServiceUnits();
            unAccountedUnits.setTime(unAccountedTime);
            reportedUsageSummary.setCurrentUnAccountedTime(unAccountedTime);
        }

        if (abmfCopyOfMonetaryBalance != null) {

            double deductableMoney = RnCRateCalculator.calculateMoney(deductableTime, grantedServiceUnits.getRate(), grantedServiceUnits.getRateMinorUnit());
            double actualDeductableMoney = RnCRateCalculator.calculateMoney(deductableTime, grantedServiceUnits.getActualRate(), grantedServiceUnits.getRateMinorUnit());

            reportedUsageSummary.setDiscountAmount(actualDeductableMoney-deductableMoney);
            reportedUsageSummary.setDeductedMonetaryBalance(deductableMoney);
            abmfCopyOfMonetaryBalance.substract(deductableMoney);
            MonetaryOperationUtils.closeReservation(abmfCopyOfMonetaryBalance, quotaReservationEntry.getGrantedServiceUnits());
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

        double deductableMoney = RnCRateCalculator.calculateMoney(deductableTime, grantedServiceUnits.getRate(), grantedServiceUnits.getRateMinorUnit());

        monetaryBalance.substract(deductableMoney);
        MonetaryOperationUtils.closeReservation(monetaryBalance, quotaReservationEntry.getGrantedServiceUnits());

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
