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

public class NonFinalReportedQuotaProcessor extends ReportedQuotaProcessor {

    private static final String MODULE = "NON-FINAL-REPORT-HANDLER";
    private MSCC reportedUsage;
    private MSCC quotaReservationEntry;
    private NonMonetoryBalance nonMonetoryBalance;
    private MonetaryBalance monetoryBalance;
    private NonMonetoryBalance abmfCopyOfNonMonetoryBalance;
    private MonetaryBalance abmfCopyOfMonetoryBalance;
    private MSCC previousUnAccountedQuota;
    private ExecutionContext executionContext;
    private MSCC unAccountedQuota;
    private ReportedUsageSummary reportedUsageSummary;

    public NonFinalReportedQuotaProcessor(MSCC reportedUsage,
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
        this.nonMonetoryBalance = nonMonetoryBalance;
        this.monetoryBalance = monetoryBalance;
        this.executionContext = executionContext;
        this.previousUnAccountedQuota = unAccountedQuota;
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
        RnCQuotaProfileDetail quotaProfileDetail = getQuotaProfileDetailAndSetPackageDetail(nonMonetoryBalance,reportedUsageSummary);

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


        deductQuotaFromCurrentInMemoryUsage(quotaProfileDetail);
        deductQuotaFromABMFUsage(quotaProfileDetail);

        if(grantedServiceUnits.isNonMonetaryReservationRequired()) {
            reportedUsageSummary.setReserveVolume(grantedServiceUnits.getVolume());
            reportedUsageSummary.setReserveTime(grantedServiceUnits.getTime());
        }

        if(grantedServiceUnits.getReservedMonetaryBalance() >= 0) {
            reportedUsageSummary.setReserveMonetaryBalance(grantedServiceUnits.getReservedMonetaryBalance());
        }


    }

    private void deductQuotaFromABMFUsage(RnCQuotaProfileDetail quotaProfileDetail) {
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

        if (previousUnAccountedQuota != null) {
            reportedUsageSummary.setPreviousUnAccountedVolume(previousUnAccountedQuota.getGrantedServiceUnits().getVolume());
            reportedUsageSummary.setPreviousUnAccountedTime(previousUnAccountedQuota.getGrantedServiceUnits().getTime());
            volume += previousUnAccountedQuota.getGrantedServiceUnits().getVolume();
            time += previousUnAccountedQuota.getGrantedServiceUnits().getTime();
        }

        long calculatedVolumePulse;
        long calculatedTimePulse;

        if(quotaReservationEntry.getFinalUnitIndiacation() != null){
            calculatedVolumePulse = quotaProfileDetail.getVolumePulseCalculator().ceil(volume);
            calculatedTimePulse = quotaProfileDetail.getTimePulseCalculator().ceil(time);
        } else{
            calculatedVolumePulse = quotaProfileDetail.getVolumePulseCalculator().floor(volume);
            calculatedTimePulse = quotaProfileDetail.getTimePulseCalculator().floor(time);
        }


        reportedUsageSummary.setCalculatedVolumePulse(calculatedVolumePulse);
        reportedUsageSummary.setCalculatedTimePulse(calculatedTimePulse);

        long deductableVolume = quotaProfileDetail.getVolumePulseCalculator().multiply(calculatedVolumePulse);
        long deductableTime = quotaProfileDetail.getTimePulseCalculator().multiply(calculatedTimePulse);

        reportedUsageSummary.setDeductedVolumeBalance(deductableVolume);
        reportedUsageSummary.setDeductedTimeBalance(deductableTime);

        NonMonetaryOperationUtils.subtract(abmfCopyOfNonMonetoryBalance, deductableVolume, deductableTime, executionContext);

        NonMonetaryOperationUtils.closeReservation(abmfCopyOfNonMonetoryBalance, quotaReservationEntry.getGrantedServiceUnits());

        long unAccountedTime = time - deductableTime;
        long unAccountedVolume = volume - deductableVolume;

        if (unAccountedVolume > 0 || unAccountedTime > 0) {
            unAccountedQuota = quotaReservationEntry.copy();
            GyServiceUnits grantedServiceUnits = unAccountedQuota.getGrantedServiceUnits();
            grantedServiceUnits.setTime(unAccountedTime);
            grantedServiceUnits.setVolume(unAccountedVolume);
            reportedUsageSummary.setCurrentUnAccountedTime(unAccountedTime);
            reportedUsageSummary.setCurrentUnAccountedVolume(unAccountedVolume);
        }

        if (abmfCopyOfMonetoryBalance != null) {

            double deductableMoney = quotaProfileDetail.getRateCalculator().calculate(calculatedVolumePulse, calculatedTimePulse);
            reportedUsageSummary.setDeductedMonetaryBalance(deductableMoney);
            abmfCopyOfMonetoryBalance.substract(deductableMoney);
            MonetaryOperationUtils.closeReservation(abmfCopyOfMonetoryBalance, quotaReservationEntry.getGrantedServiceUnits());
        }

    }

    private void deductQuotaFromCurrentInMemoryUsage(RnCQuotaProfileDetail quotaProfileDetail) {
        long volume = 0;
        long time = 0;
        if(quotaProfileDetail.getQuotaUnit() != QuotaUsageType.TIME) {
            volume = reportedUsage.getUsedServiceUnits().getVolume();
        }
        if(quotaProfileDetail.getQuotaUnit() != QuotaUsageType.VOLUME) {
            time = reportedUsage.getUsedServiceUnits().getTime();
        }


        long deductableVolumePulse;
        long deductableTimePulse;

        if (previousUnAccountedQuota != null) {
            deductableVolumePulse = quotaProfileDetail.getVolumePulseCalculator().ceil(volume + previousUnAccountedQuota.getGrantedServiceUnits().getVolume());
            deductableTimePulse = quotaProfileDetail.getTimePulseCalculator().ceil(time + previousUnAccountedQuota.getGrantedServiceUnits().getTime());
        } else {
            deductableVolumePulse = quotaProfileDetail.getVolumePulseCalculator().ceil(volume);
            deductableTimePulse = quotaProfileDetail.getTimePulseCalculator().ceil(time);
        }

        long deductableVolume = quotaProfileDetail.getVolumePulseCalculator().multiply(deductableVolumePulse);
        long deductableTime = quotaProfileDetail.getTimePulseCalculator().multiply(deductableTimePulse);

        NonMonetaryOperationUtils.subtract(nonMonetoryBalance, deductableVolume, deductableTime, executionContext);

        NonMonetaryOperationUtils.closeReservation(nonMonetoryBalance, quotaReservationEntry.getGrantedServiceUnits());


        if (monetoryBalance != null) {

            double deductableMoney = quotaProfileDetail.getRateCalculator().calculate(deductableVolumePulse, deductableTimePulse);

            monetoryBalance.substract(deductableMoney);
            MonetaryOperationUtils.closeReservation(monetoryBalance, quotaReservationEntry.getGrantedServiceUnits());
        }

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
