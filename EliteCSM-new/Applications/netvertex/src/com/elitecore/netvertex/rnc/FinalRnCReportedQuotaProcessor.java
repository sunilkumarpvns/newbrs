package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;

import java.util.Date;
import java.util.Objects;

public class FinalRnCReportedQuotaProcessor extends RnCReportedQuotaProcessor {

    private MSCC reportedUsage;
    private MSCC quotaReservationEntry;
    private MSCC unAccountedQuota;
    private MonetaryBalance monetaryBalance;
    private MonetaryBalance abmfCopyOfMonetaryBalance;
    private ReportedUsageSummary reportedUsageSummary;


    public FinalRnCReportedQuotaProcessor(MSCC reportedUsage,
                                          MSCC quotaReservationEntry,
                                          MSCC unAccountedQuota,
                                          MonetaryBalance monetaryBalance,
                                          PolicyRepository policyRepository,
                                          MonetaryBalance abmfCopyOfMonetaryBalance) {
        super(policyRepository);
        this.reportedUsage = reportedUsage;
        this.quotaReservationEntry = quotaReservationEntry;
        this.unAccountedQuota = unAccountedQuota;
        this.monetaryBalance = monetaryBalance;
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


        reportedUsageSummary.setSessionStopTime(new Date(System.currentTimeMillis()));

        if(grantedServiceUnits.getReservedMonetaryBalance() >= 0) {
            reportedUsageSummary.setReserveMonetaryBalance(grantedServiceUnits.getReservedMonetaryBalance());
        }
        reportedUsageSummary.setRevenueCode(grantedServiceUnits.getRevenueCode());

        deductBalance(grantedServiceUnits);

    }


    private void deductBalance(GyServiceUnits grantedServiceUnits) {


        long time = reportedUsage.getUsedServiceUnits().getTime();

        reportedUsageSummary.setReportedTime(time);

        if (unAccountedQuota != null) {
            reportedUsageSummary.setPreviousUnAccountedVolume(unAccountedQuota.getGrantedServiceUnits().getVolume());
            reportedUsageSummary.setPreviousUnAccountedTime(unAccountedQuota.getGrantedServiceUnits().getTime());
            time += unAccountedQuota.getGrantedServiceUnits().getTime();
        }

        long calculatedTimePulse = RnCPulseCalculator.ceil(time, grantedServiceUnits.getTimePulse());

        reportedUsageSummary.setCalculatedTimePulse(calculatedTimePulse);

        long deductableTime = RnCPulseCalculator.multiply(calculatedTimePulse, grantedServiceUnits.getTimePulse());

        double deductableMoney = RnCRateCalculator.calculateMoney(deductableTime, grantedServiceUnits.getRate(), grantedServiceUnits.getRateMinorUnit());
        double actualDeductableMoney = RnCRateCalculator.calculateMoney(deductableTime, grantedServiceUnits.getActualRate(), grantedServiceUnits.getRateMinorUnit());

        reportedUsageSummary.setDiscountAmount(actualDeductableMoney-deductableMoney);
        abmfCopyOfMonetaryBalance.substract(deductableMoney);
        reportedUsageSummary.setDeductedMonetaryBalance(deductableMoney);
        MonetaryOperationUtils.closeReservation(abmfCopyOfMonetaryBalance, quotaReservationEntry.getGrantedServiceUnits());

        monetaryBalance.substract(deductableMoney);
        MonetaryOperationUtils.closeReservation(monetaryBalance, quotaReservationEntry.getGrantedServiceUnits());

    }


    @Override
    public ReportedUsageSummary getReportedUsageSummary() {
        return reportedUsageSummary;
    }
}
