package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;

import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class CloseReservationQuotaHandler extends ReportedQuotaProcessor {


    private static final String MODULE = "CLOSE-UNACCOUNTED-QUOTA-HANDLER";
    private MSCC quotaReservationEntry;
    private NonMonetoryBalance nonMonetoryBalance;
    private MonetaryBalance monetaryBalance;
    private NonMonetoryBalance abmfCopyOfNonMonetoryBalance;
    private MonetaryBalance abmfCopyOfMonetoryBalance;
    private ReportedUsageSummary reportedUsageSummary;

    public CloseReservationQuotaHandler(MSCC quotaReservationEntry,
                                        NonMonetoryBalance nonMonetoryBalance,
                                        MonetaryBalance monetaryBalance,
                                        PolicyRepository policyRepository,
                                        NonMonetoryBalance abmfCopyOfNonMonetoryBalance,
                                        MonetaryBalance abmfCopyOfMonetoryBalance,
                                        ReportedUsageSummary reportedUsageSummary) {
        super(policyRepository);
        this.quotaReservationEntry = quotaReservationEntry;
        this.nonMonetoryBalance = nonMonetoryBalance;
        this.monetaryBalance = monetaryBalance;
        this.abmfCopyOfNonMonetoryBalance = abmfCopyOfNonMonetoryBalance;
        this.abmfCopyOfMonetoryBalance = abmfCopyOfMonetoryBalance;
        this.reportedUsageSummary = reportedUsageSummary;
    }


    @Override
    public void handle() {

        reportedUsageSummary.setReportOperation(ReportOperation.CLOSE_RESERVATION);

        GyServiceUnits grantedServiceUnits = quotaReservationEntry.getGrantedServiceUnits();
        reportedUsageSummary.setReserveMonetaryBalance(grantedServiceUnits.getReservedMonetaryBalance());
        if(grantedServiceUnits.isNonMonetaryReservationRequired()) {
            reportedUsageSummary.setReserveVolume(grantedServiceUnits.getVolume());
            reportedUsageSummary.setReserveTime(grantedServiceUnits.getTime());
        }
        reportedUsageSummary.setRevenueCode(grantedServiceUnits.getRevenueCode());
        RnCQuotaProfileDetail quotaProfileDetail = getQuotaProfileDetailAndSetPackageDetail(nonMonetoryBalance, reportedUsageSummary);


        if (quotaProfileDetail == null) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Unable to close unaccounted quota for rating-group:"
                        + nonMonetoryBalance.getRatingGroupId()
                        + ". Reason: Quota profile detail not found for pkgId"
                        + nonMonetoryBalance.getPackageId()
                        + ", Quota profile id: " + nonMonetoryBalance.getQuotaProfileId()
                        + " Service Id:" + nonMonetoryBalance.getPackageId());
            }

            return;
        }


        ReportedUsageSummary reportedUsageSummary = new ReportedUsageSummary(quotaReservationEntry.getRatingGroup(), quotaReservationEntry.getServiceIdentifiers());



        if (grantedServiceUnits.isNonMonetaryReservationRequired()) {
            reportedUsageSummary.setReserveTime(grantedServiceUnits.getTime());
            reportedUsageSummary.setReportedVolume(grantedServiceUnits.getVolume());
            NonMonetaryOperationUtils.closeReservation(abmfCopyOfNonMonetoryBalance, grantedServiceUnits);
            NonMonetaryOperationUtils.closeReservation(nonMonetoryBalance, grantedServiceUnits);
        }

        if (Objects.nonNull(monetaryBalance) && grantedServiceUnits.getReservedMonetaryBalance() >= 0) {
            reportedUsageSummary.setReserveMonetaryBalance(grantedServiceUnits.getReservedMonetaryBalance());
            MonetaryOperationUtils.closeReservation(abmfCopyOfMonetoryBalance, grantedServiceUnits);
            MonetaryOperationUtils.closeReservation(monetaryBalance, grantedServiceUnits);
        }

    }


    @Override
    public ReportedUsageSummary getReportedUsageSummary() {
        return reportedUsageSummary;
    }
}
