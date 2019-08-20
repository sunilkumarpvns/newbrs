package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;

import java.util.Objects;

public class CloseRnCNonMonetaryReservationQuotaHandler extends RnCReportedQuotaProcessor {

    private MSCC quotaReservationEntry;
    private RnCNonMonetaryBalance rnCNonMonetaryBalance;
    private RnCNonMonetaryBalance abmfCopyOfRnCNonMonetaryBalance;
    private ReportedUsageSummary reportedUsageSummary;

    public CloseRnCNonMonetaryReservationQuotaHandler(MSCC quotaReservationEntry,
                                           RnCNonMonetaryBalance rnCNonMonetaryBalance,
                                           PolicyRepository policyRepository,
                                           RnCNonMonetaryBalance abmfCopyOfRnCNonMonetaryBalance) {
        super(policyRepository);
        this.quotaReservationEntry = quotaReservationEntry;
        this.rnCNonMonetaryBalance = rnCNonMonetaryBalance;
        this.abmfCopyOfRnCNonMonetaryBalance = abmfCopyOfRnCNonMonetaryBalance;
        this.reportedUsageSummary = new ReportedUsageSummary(quotaReservationEntry.getRatingGroup(), quotaReservationEntry.getServiceIdentifiers());
    }


    @Override
    public void handle() {


        reportedUsageSummary.setReportOperation(ReportOperation.CLOSE_RESERVATION);

        GyServiceUnits grantedServiceUnits = quotaReservationEntry.getGrantedServiceUnits();

        if(grantedServiceUnits.isNonMonetaryReservationRequired()) {
            reportedUsageSummary.setReserveTime(grantedServiceUnits.getTime());
        }

        reportedUsageSummary.setTimePulse(grantedServiceUnits.getTimePulse());

        reportedUsageSummary.setRateCardName(grantedServiceUnits.getRateCardName());
        reportedUsageSummary.setRateCardId(grantedServiceUnits.getRateCardId());
        reportedUsageSummary.setRateCardGroupName(grantedServiceUnits.getRateCardGroupName());
        reportedUsageSummary.setPackageId(grantedServiceUnits.getPackageId(), PolicyManager.getInstance().getPackageName(grantedServiceUnits.getPackageId()));
        reportedUsageSummary.setProductOfferName(Objects.isNull(PolicyManager.getInstance().getProductOffer().byId(grantedServiceUnits.getProductOfferId())) ? null :
                PolicyManager.getInstance().getProductOffer().byId(grantedServiceUnits.getProductOfferId()).getName());
        reportedUsageSummary.setRevenueCode(grantedServiceUnits.getRevenueCode());

        RnCNonMonetaryOperationUtils.closeReservation(abmfCopyOfRnCNonMonetaryBalance, grantedServiceUnits);
        RnCNonMonetaryOperationUtils.closeReservation(rnCNonMonetaryBalance, grantedServiceUnits);

    }


    @Override
    public ReportedUsageSummary getReportedUsageSummary() {
        return reportedUsageSummary;
    }
}
