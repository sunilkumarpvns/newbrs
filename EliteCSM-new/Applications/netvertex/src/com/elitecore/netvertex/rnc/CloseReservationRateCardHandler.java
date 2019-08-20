package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;

import java.util.Objects;

public class CloseReservationRateCardHandler extends ReportedQuotaProcessor {

    private MSCC reservedMSCC;
    private MonetaryBalance monetaryBalance;
    private MonetaryBalance abmfCopyOfMonetoryBalance;
    private ReportedUsageSummary reportedUsageSummary;

    public CloseReservationRateCardHandler(MSCC reservedMSCC,
                                           MonetaryBalance monetaryBalance,
                                           PolicyRepository policyRepository,
                                           MonetaryBalance abmfCopyOfMonetoryBalance,
                                           ReportedUsageSummary reportedUsageSummary) {
        super(policyRepository);
        this.reservedMSCC = reservedMSCC;
        this.monetaryBalance = monetaryBalance;
        this.abmfCopyOfMonetoryBalance = abmfCopyOfMonetoryBalance;
        this.reportedUsageSummary = reportedUsageSummary;
    }


    @Override
    public void handle() {

        reportedUsageSummary.setReportOperation(ReportOperation.CLOSE_RESERVATION);

        GyServiceUnits grantedServiceUnits = reservedMSCC.getGrantedServiceUnits();
        reportedUsageSummary.setRevenueCode(grantedServiceUnits.getRevenueCode());
        reportedUsageSummary.setRate(grantedServiceUnits.getActualRate());
        reportedUsageSummary.setRateMinorUnit(grantedServiceUnits.getRateMinorUnit());

        reportedUsageSummary.setRateCardName(grantedServiceUnits.getRateCardName());
        reportedUsageSummary.setRateCardId(grantedServiceUnits.getRateCardId());
        reportedUsageSummary.setRateCardGroupName(grantedServiceUnits.getRateCardGroupName());
        reportedUsageSummary.setPackageId(grantedServiceUnits.getPackageId(), PolicyManager.getInstance().getPackageName(grantedServiceUnits.getPackageId()));
        reportedUsageSummary.setProductOfferName(Objects.isNull(PolicyManager.getInstance().getProductOffer().byId(grantedServiceUnits.getProductOfferId())) ? null :
                PolicyManager.getInstance().getProductOffer().byId(grantedServiceUnits.getProductOfferId()).getName());

        reportedUsageSummary.setReserveMonetaryBalance(grantedServiceUnits.getReservedMonetaryBalance());
        MonetaryOperationUtils.closeReservation(abmfCopyOfMonetoryBalance, grantedServiceUnits);
        MonetaryOperationUtils.closeReservation(monetaryBalance, grantedServiceUnits);
    }


    @Override
    public ReportedUsageSummary getReportedUsageSummary() {
        return reportedUsageSummary;
    }
}
