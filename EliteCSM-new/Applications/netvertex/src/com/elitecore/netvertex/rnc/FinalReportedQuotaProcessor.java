package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;

import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class FinalReportedQuotaProcessor extends ReportedQuotaProcessor {

    private static final String MODULE = "FINAL-REPORTED-QUOTA-PROCESSOR";
    private MSCC reportedUsage;
    private MSCC quotaReservationEntry;
    private MSCC unAccountedQuota;
    private NonMonetoryBalance nonMonetoryBalance;
    private MonetaryBalance monetoryBalance;
    private NonMonetoryBalance abmfCopyOfNonMonetoryBalance;
    private MonetaryBalance abmfCopyOfMonetoryBalance;
    private ExecutionContext executionContext;
    private ReportedUsageSummary reportedUsageSummary;


    public FinalReportedQuotaProcessor(MSCC reportedUsage,
                                       MSCC quotaReservationEntry,
                                       MSCC unAccountedQuota,
                                       NonMonetoryBalance nonMonetoryBalance,
                                       MonetaryBalance monetoryBalance,
                                       ExecutionContext executionContext,
                                       PolicyRepository policyRepository,
                                       NonMonetoryBalance abmfCopyOfNonMonetoryBalance,
                                       MonetaryBalance abmfCopyOfMonetoryBalance,
                                       ReportedUsageSummary reportedUsageSummary) {
        super(policyRepository);
        this.reportedUsage = reportedUsage;
        this.quotaReservationEntry = quotaReservationEntry;
        this.unAccountedQuota = unAccountedQuota;
        this.nonMonetoryBalance = nonMonetoryBalance;
        this.monetoryBalance = monetoryBalance;
        this.executionContext = executionContext;
        this.abmfCopyOfNonMonetoryBalance = abmfCopyOfNonMonetoryBalance;
        this.abmfCopyOfMonetoryBalance = abmfCopyOfMonetoryBalance;
        this.reportedUsageSummary = reportedUsageSummary;

    }

    @Override
    public void handle() {

        GyServiceUnits grantedServiceUnits = quotaReservationEntry.getGrantedServiceUnits();
        reportedUsageSummary.setReserveMonetaryBalance(grantedServiceUnits.getReservedMonetaryBalance());
        if(grantedServiceUnits.isNonMonetaryReservationRequired()) {
            reportedUsageSummary.setReserveVolume(grantedServiceUnits.getVolume());
            reportedUsageSummary.setReserveTime(grantedServiceUnits.getTime());
        }

        reportedUsageSummary.setReportingReason(reportedUsage.getReportingReason());
        reportedUsageSummary.setReportOperation(ReportOperation.fromReportingReason(reportedUsage.getReportingReason()));
        RnCQuotaProfileDetail quotaProfileDetail = getQuotaProfileDetailAndSetPackageDetail(nonMonetoryBalance, reportedUsageSummary);

        if (quotaProfileDetail == null) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Unable to deduct quota for rating-group:"
                        + nonMonetoryBalance.getRatingGroupId()
                        + ". Reason: Quota profile detail not found for pkgId"
                        + nonMonetoryBalance.getPackageId()
                        + ", Quota profile id: " + nonMonetoryBalance.getQuotaProfileId()
                        + " Service Id:" + nonMonetoryBalance.getServiceId());
            }
            return;
        }

        reportedUsageSummary.setVolumePulse(quotaProfileDetail.getVolumePulseInBytes());
        reportedUsageSummary.setTimePulse(quotaProfileDetail.getTimePulseInSeconds());
        reportedUsageSummary.setProductOfferName(Objects.isNull(PolicyManager.getInstance().getProductOffer().byId(grantedServiceUnits.getProductOfferId())) ? null :
                PolicyManager.getInstance().getProductOffer().byId(grantedServiceUnits.getProductOfferId()).getName());
        reportedUsageSummary.setRevenueCode(grantedServiceUnits.getRevenueCode());

        if(grantedServiceUnits.isNonMonetaryReservationRequired()) {
            reportedUsageSummary.setReserveVolume(grantedServiceUnits.getVolume());
            reportedUsageSummary.setReserveTime(grantedServiceUnits.getTime());
        }

        if(grantedServiceUnits.getReservedMonetaryBalance() >= 0) {
            reportedUsageSummary.setReserveMonetaryBalance(grantedServiceUnits.getReservedMonetaryBalance());
        }

        deductBalance(quotaProfileDetail);
    }


    private void deductBalance(RnCQuotaProfileDetail quotaProfileDetail) {
        long volume = 0;
        long time = 0;
        if(quotaProfileDetail.getQuotaUnit() != QuotaUsageType.TIME) {
            volume = reportedUsage.getUsedServiceUnits().getVolume();
        }

        if(quotaProfileDetail.getQuotaUnit() != QuotaUsageType.VOLUME) {
            time = reportedUsage.getUsedServiceUnits().getTime();
        }

        reportedUsageSummary.setReportedVolume(reportedUsage.getUsedServiceUnits().getVolume());
        reportedUsageSummary.setReportedTime(reportedUsage.getUsedServiceUnits().getTime());

        if (unAccountedQuota != null) {
            reportedUsageSummary.setPreviousUnAccountedVolume(unAccountedQuota.getGrantedServiceUnits().getVolume());
            reportedUsageSummary.setPreviousUnAccountedTime(unAccountedQuota.getGrantedServiceUnits().getTime());
            volume += unAccountedQuota.getGrantedServiceUnits().getVolume();
            time += unAccountedQuota.getGrantedServiceUnits().getTime();
        }

        long calculatedVolumePulse = quotaProfileDetail.getVolumePulseCalculator().ceil(volume);
        long calculatedTimePulse = quotaProfileDetail.getTimePulseCalculator().ceil(time);

        reportedUsageSummary.setCalculatedVolumePulse(calculatedVolumePulse);
        reportedUsageSummary.setCalculatedTimePulse(calculatedTimePulse);

        long deductableVolume = quotaProfileDetail.getVolumePulseCalculator().multiply(calculatedVolumePulse);
        long deductableTime = quotaProfileDetail.getTimePulseCalculator().multiply(calculatedTimePulse);

        reportedUsageSummary.setDeductedVolumeBalance(deductableVolume);
        reportedUsageSummary.setDeductedTimeBalance(deductableTime);


        NonMonetaryOperationUtils.subtract(abmfCopyOfNonMonetoryBalance, deductableVolume, deductableTime, executionContext);

        NonMonetaryOperationUtils.closeReservation(abmfCopyOfNonMonetoryBalance, quotaReservationEntry.getGrantedServiceUnits());

        NonMonetaryOperationUtils.subtract(nonMonetoryBalance, deductableVolume, deductableTime, executionContext);

        NonMonetaryOperationUtils.closeReservation(nonMonetoryBalance, quotaReservationEntry.getGrantedServiceUnits());


        if (monetoryBalance != null) {
            double deductableMoney = quotaProfileDetail.getRateCalculator().calculate(calculatedVolumePulse, calculatedTimePulse);
            abmfCopyOfMonetoryBalance.substract(deductableMoney);
            reportedUsageSummary.setDeductedMonetaryBalance(deductableMoney);
            MonetaryOperationUtils.closeReservation(abmfCopyOfMonetoryBalance, quotaReservationEntry.getGrantedServiceUnits());

            monetoryBalance.substract(deductableMoney);
            MonetaryOperationUtils.closeReservation(monetoryBalance, quotaReservationEntry.getGrantedServiceUnits());
        }

    }


    @Override
    public ReportedUsageSummary getReportedUsageSummary() {
        return reportedUsageSummary;
    }
}
