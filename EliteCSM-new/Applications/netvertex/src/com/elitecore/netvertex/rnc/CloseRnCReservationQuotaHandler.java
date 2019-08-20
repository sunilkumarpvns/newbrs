package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;

import java.util.Objects;

public class CloseRnCReservationQuotaHandler extends RnCReportedQuotaProcessor {


    private MSCC quotaReservationEntry;
    private MonetaryBalance monetaryBalance;
    private MonetaryBalance abmfCopyOfMonetaryBalance;
    private ReportedUsageSummary reportedUsageSummary;

    public CloseRnCReservationQuotaHandler(MSCC quotaReservationEntry,
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


        reportedUsageSummary.setReportOperation(ReportOperation.CLOSE_RESERVATION);

        GyServiceUnits grantedServiceUnits = quotaReservationEntry.getGrantedServiceUnits();
        reportedUsageSummary.setReserveMonetaryBalance(grantedServiceUnits.getReservedMonetaryBalance());
        
        if(grantedServiceUnits.isNonMonetaryReservationRequired()) {
            reportedUsageSummary.setReserveTime(grantedServiceUnits.getTime());
        }

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


        MonetaryOperationUtils.closeReservation(abmfCopyOfMonetaryBalance, grantedServiceUnits);
        MonetaryOperationUtils.closeReservation(monetaryBalance, grantedServiceUnits);

    }


    @Override
    public ReportedUsageSummary getReportedUsageSummary() {
        return reportedUsageSummary;
    }
}
