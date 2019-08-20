package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;

import java.util.Objects;

public class CloseRnCUnAccountedQuotaHandler extends RnCReportedQuotaProcessor {


    private MSCC quotaReservationEntry;
    private MonetaryBalance monetaryBalance;
    private MonetaryBalance abmfCopyOfMonetaryBalance;
    private ReportedUsageSummary reportedUsageSummary;

    public CloseRnCUnAccountedQuotaHandler(MSCC quotaReservationEntry,
                                           MonetaryBalance monetaryBalance,
                                           PolicyRepository policyRepository,
                                           MonetaryBalance abmfCopyOfMonetaryBalance) {
        super(policyRepository);
        this.quotaReservationEntry = quotaReservationEntry;
        this.monetaryBalance = monetaryBalance;
        this.abmfCopyOfMonetaryBalance = abmfCopyOfMonetaryBalance;
        this.reportedUsageSummary = new ReportedUsageSummary(quotaReservationEntry.getRatingGroup(), quotaReservationEntry.getServiceIdentifiers());
    }


    @Override
    public void handle() {

        GyServiceUnits grantedServiceUnits = quotaReservationEntry.getGrantedServiceUnits();

        reportedUsageSummary.setReportOperation(ReportOperation.CLOSE_UNACCOUNTED_QUOTA);
        reportedUsageSummary.setTimePulse(grantedServiceUnits.getTimePulse());
        reportedUsageSummary.setRateMinorUnit(grantedServiceUnits.getRateMinorUnit());

        reportedUsageSummary.setDiscount(grantedServiceUnits.getDiscount());
        reportedUsageSummary.setRate(grantedServiceUnits.getActualRate());
        reportedUsageSummary.setRateMinorUnit(grantedServiceUnits.getRateMinorUnit());

        reportedUsageSummary.setPreviousUnAccountedTime(quotaReservationEntry.getGrantedServiceUnits().getTime());

        long calculatedTimePulse = RnCPulseCalculator.ceil(quotaReservationEntry.getGrantedServiceUnits().getTime(), grantedServiceUnits.getTimePulse());

        reportedUsageSummary.setCalculatedTimePulse(calculatedTimePulse);

        reportedUsageSummary.setRateCardName(grantedServiceUnits.getRateCardName());
        reportedUsageSummary.setRateCardId(grantedServiceUnits.getRateCardId());
        reportedUsageSummary.setRateCardGroupName(grantedServiceUnits.getRateCardGroupName());
        reportedUsageSummary.setPackageId(grantedServiceUnits.getPackageId(), PolicyManager.getInstance().getPackageName(grantedServiceUnits.getPackageId()));
        reportedUsageSummary.setProductOfferName(Objects.isNull(PolicyManager.getInstance().getProductOffer().byId(grantedServiceUnits.getProductOfferId())) ? null :
                PolicyManager.getInstance().getProductOffer().byId(grantedServiceUnits.getProductOfferId()).getName());
        reportedUsageSummary.setRevenueCode(grantedServiceUnits.getRevenueCode());


        long deductableTime = RnCPulseCalculator.multiply(calculatedTimePulse, grantedServiceUnits.getTimePulse());

        if (monetaryBalance != null) {
        	
            double deductableMoney = RnCRateCalculator.calculateMoney(deductableTime, grantedServiceUnits.getRate(), grantedServiceUnits.getRateMinorUnit());
            double actualDeductableMoney = RnCRateCalculator.calculateMoney(deductableTime, grantedServiceUnits.getActualRate(), grantedServiceUnits.getRateMinorUnit());

            reportedUsageSummary.setDiscountAmount(actualDeductableMoney-deductableMoney);
            reportedUsageSummary.setDeductedMonetaryBalance(deductableMoney);
            monetaryBalance.substract(deductableMoney);
            abmfCopyOfMonetaryBalance.substract(deductableMoney);
        }
   

    }


    
    @Override
    public ReportedUsageSummary getReportedUsageSummary() {
        return reportedUsageSummary;
    }
}
