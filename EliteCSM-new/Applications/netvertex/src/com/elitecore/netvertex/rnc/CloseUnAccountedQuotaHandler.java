package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;

import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class CloseUnAccountedQuotaHandler extends ReportedQuotaProcessor {


    private static final String MODULE = "CLOSE-UNACCOUNTED-QUOTA-HANDLER";
    private MSCC quotaReservationEntry;
    private NonMonetoryBalance nonMonetoryBalance;
    private MonetaryBalance monetoryBalance;
    private NonMonetoryBalance abmfCopyOfNonMonetoryBalance;
    private MonetaryBalance abmfCopyOfMonetoryBalance;
    private ExecutionContext executionContext;
    private ReportedUsageSummary reportedUsageSummary;

    public CloseUnAccountedQuotaHandler(MSCC quotaReservationEntry,
                                        NonMonetoryBalance nonMonetoryBalance,
                                        MonetaryBalance monetoryBalance,
                                        ExecutionContext executionContext,
                                        PolicyRepository policyRepository,
                                        NonMonetoryBalance abmfCopyOfNonMonetoryBalance,
                                        MonetaryBalance abmfCopyOfMonetoryBalance,
                                        ReportedUsageSummary reportedUsageSummary) {
        super(policyRepository);
        this.quotaReservationEntry = quotaReservationEntry;
        this.nonMonetoryBalance = nonMonetoryBalance;
        this.monetoryBalance = monetoryBalance;
        this.executionContext = executionContext;
        this.abmfCopyOfNonMonetoryBalance = abmfCopyOfNonMonetoryBalance;
        this.abmfCopyOfMonetoryBalance = abmfCopyOfMonetoryBalance;
        this.reportedUsageSummary = reportedUsageSummary;
    }


    @Override
    public void handle() {

        RnCQuotaProfileDetail quotaProfileDetail = getQuotaProfileDetailAndSetPackageDetail(nonMonetoryBalance, reportedUsageSummary);


        reportedUsageSummary.setReportOperation(ReportOperation.CLOSE_UNACCOUNTED_QUOTA);
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

        reportedUsageSummary.setVolumePulse(quotaProfileDetail.getVolumePulseInBytes());
        reportedUsageSummary.setTimePulse(quotaProfileDetail.getTimePulseInSeconds());
        reportedUsageSummary.setProductOfferName(Objects.isNull(PolicyManager.getInstance().getProductOffer().byId(quotaReservationEntry.getGrantedServiceUnits().getProductOfferId())) ? null :
                PolicyManager.getInstance().getProductOffer().byId(quotaReservationEntry.getGrantedServiceUnits().getProductOfferId()).getName());

        reportedUsageSummary.setRevenueCode(quotaReservationEntry.getGrantedServiceUnits().getRevenueCode());
        reportedUsageSummary.setPreviousUnAccountedVolume(quotaReservationEntry.getGrantedServiceUnits().getVolume());
        reportedUsageSummary.setPreviousUnAccountedTime(quotaReservationEntry.getGrantedServiceUnits().getTime());

        long calculatedVolumePulse = quotaProfileDetail.getVolumePulseCalculator().ceil(quotaReservationEntry.getGrantedServiceUnits().getVolume());
        long calculatedTimePulse = quotaProfileDetail.getTimePulseCalculator().ceil(quotaReservationEntry.getGrantedServiceUnits().getTime());

        reportedUsageSummary.setCalculatedVolumePulse(calculatedVolumePulse);
        reportedUsageSummary.setCalculatedTimePulse(calculatedTimePulse);

        long deductableVolume = quotaProfileDetail.getVolumePulseCalculator().multiply(calculatedVolumePulse);
        long deductableTime = quotaProfileDetail.getTimePulseCalculator().multiply(calculatedTimePulse);

        reportedUsageSummary.setDeductedVolumeBalance(deductableVolume);
        reportedUsageSummary.setDeductedTimeBalance(deductableTime);


        deduct(deductableVolume, deductableTime);

        if (monetoryBalance != null) {
            double deductableMoney = quotaProfileDetail.getRateCalculator().calculate(calculatedVolumePulse, calculatedTimePulse);
            reportedUsageSummary.setDeductedMonetaryBalance(deductableMoney);
            monetoryBalance.substract(deductableMoney);
            abmfCopyOfMonetoryBalance.substract(deductableMoney);
        }

    }

    private void deduct(long deductableVolume, long deductableTime) {

        NonMonetaryOperationUtils.subtract(nonMonetoryBalance,
                deductableVolume,
                deductableTime,
                executionContext);
        NonMonetaryOperationUtils.subtract(abmfCopyOfNonMonetoryBalance, deductableVolume, deductableTime, executionContext);
    }

    @Override
    public ReportedUsageSummary getReportedUsageSummary() {
        return reportedUsageSummary;
    }
}
