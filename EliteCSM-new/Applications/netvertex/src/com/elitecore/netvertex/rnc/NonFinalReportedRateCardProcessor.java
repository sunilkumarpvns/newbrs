package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;

import java.math.BigDecimal;
import java.util.Objects;

public class NonFinalReportedRateCardProcessor extends ReportedQuotaProcessor {

    private MSCC reportedMSCC;
    private MSCC grantedMSCC;
    private MonetaryBalance monetoryBalance;
    private MonetaryBalance abmfCopyOfMonetoryBalance;
    private MSCC previousUnAccountedQuota;
    private MSCC unAccountedQuota;
    private ReportedUsageSummary reportedUsageSummary;
    private DataRateCard rateCardDetails;

    public NonFinalReportedRateCardProcessor(MSCC reportedMSCC,
                                          MSCC grantedMSCC,
                                          MSCC previousUnAccountedQuota,
                                          MonetaryBalance monetoryBalance,
                                          PolicyRepository policyRepository,
                                          MonetaryBalance abmfCopyOfMonetoryBalance,
                                          ReportedUsageSummary reportedUsageSummary,
                                          DataRateCard dataRateCard) {
        super(policyRepository);
        this.reportedMSCC = reportedMSCC;
        this.grantedMSCC = grantedMSCC;
        this.monetoryBalance = monetoryBalance;
        this.previousUnAccountedQuota = previousUnAccountedQuota;
        this.abmfCopyOfMonetoryBalance = abmfCopyOfMonetoryBalance;
        this.reportedUsageSummary = reportedUsageSummary;
        this.rateCardDetails = dataRateCard;
    }

    @Override
    public void handle() {

        GyServiceUnits grantedServiceUnits = grantedMSCC.getGrantedServiceUnits();
        if(grantedServiceUnits.getReservedMonetaryBalance() >= 0) {
            reportedUsageSummary.setReserveMonetaryBalance(grantedServiceUnits.getReservedMonetaryBalance());
        }
        reportedUsageSummary.setReportingReason(reportedMSCC.getReportingReason());
        reportedUsageSummary.setReportOperation(ReportOperation.fromReportingReason(reportedMSCC.getReportingReason()));

        reportedUsageSummary.setRate(grantedServiceUnits.getRate());
        reportedUsageSummary.setRateMinorUnit(grantedServiceUnits.getRateMinorUnit());

        reportedUsageSummary.setRateCardName(grantedServiceUnits.getRateCardName());
        reportedUsageSummary.setRateCardId(grantedServiceUnits.getRateCardId());
        reportedUsageSummary.setRateCardGroupName(grantedServiceUnits.getRateCardGroupName());
        reportedUsageSummary.setPackageId(grantedServiceUnits.getPackageId(), PolicyManager.getInstance().getPackageName(grantedServiceUnits.getPackageId()));
        reportedUsageSummary.setProductOfferName(Objects.isNull(PolicyManager.getInstance().getProductOffer().byId(grantedServiceUnits.getProductOfferId())) ? null :
                PolicyManager.getInstance().getProductOffer().byId(grantedServiceUnits.getProductOfferId()).getName());
        reportedUsageSummary.setRevenueCode(grantedServiceUnits.getRevenueCode());

        if (Uom.isVolumeUnit(rateCardDetails.getPulseUom())) {

            reportedUsageSummary.setVolumePulse(grantedServiceUnits.getPulseMinorUnit());

            deductVolumeFromCurrentInMemoryUsage();
            deductVolumeFromABMFUsage();
        }else if(Uom.isTimeUnit(rateCardDetails.getPulseUom())){

            reportedUsageSummary.setTimePulse(grantedServiceUnits.getPulseMinorUnit());

            deductTimeFromCurrentInMemoryUsage();
            deductTimeFromABMFUsage();
        }
    }

    private void deductVolumeFromABMFUsage() {
        GyServiceUnits usedServiceUnits = reportedMSCC.getUsedServiceUnits();
        long volume = usedServiceUnits.getVolume();

        reportedUsageSummary.setReportedVolume(volume);

        if (previousUnAccountedQuota != null) {
            reportedUsageSummary.setPreviousUnAccountedVolume(previousUnAccountedQuota.getGrantedServiceUnits().getVolume());
            volume += previousUnAccountedQuota.getGrantedServiceUnits().getVolume();
        }

        GyServiceUnits grantedServiceUnits = grantedMSCC.getGrantedServiceUnits();
        long calculatedVolumePulse = grantedServiceUnits.calculateFloorPulse(volume);
        reportedUsageSummary.setCalculatedVolumePulse(calculatedVolumePulse);

        long deductableVolume = grantedServiceUnits.calculateDeductableQuota(calculatedVolumePulse);
        reportedUsageSummary.setDeductedVolumeBalance(deductableVolume);
        long unAccountedVolume = volume - deductableVolume;
        if (unAccountedVolume > 0 ) {
            unAccountedQuota = grantedMSCC.copy();
            unAccountedQuota.getGrantedServiceUnits().setVolume(unAccountedVolume);
            reportedUsageSummary.setCurrentUnAccountedVolume(unAccountedVolume);
        }

        deductMonetaryBalance(abmfCopyOfMonetoryBalance, grantedServiceUnits, deductableVolume);
    }

    private void deductVolumeFromCurrentInMemoryUsage() {

        GyServiceUnits usedServiceUnits = reportedMSCC.getUsedServiceUnits();
        long volume = usedServiceUnits.getVolume();

        GyServiceUnits grantedServiceUnits = grantedMSCC.getGrantedServiceUnits();
        long calculatedVolumePulse;
        if (previousUnAccountedQuota != null) {
            calculatedVolumePulse = grantedServiceUnits.calculateCeilPulse(volume + previousUnAccountedQuota.getGrantedServiceUnits().getVolume());
        } else {
            calculatedVolumePulse = grantedServiceUnits.calculateCeilPulse(volume);
        }
        long deductableVolume = grantedServiceUnits.calculateDeductableQuota(calculatedVolumePulse);

        deductMonetaryBalance(monetoryBalance, grantedServiceUnits, deductableVolume);
    }



    private void deductTimeFromABMFUsage() {
        GyServiceUnits usedServiceUnits = reportedMSCC.getUsedServiceUnits();
        long time = usedServiceUnits.getTime();

        reportedUsageSummary.setReportedTime(time);

        if (previousUnAccountedQuota != null) {
            reportedUsageSummary.setPreviousUnAccountedTime(previousUnAccountedQuota.getGrantedServiceUnits().getTime());
            time += previousUnAccountedQuota.getGrantedServiceUnits().getTime();
        }

        GyServiceUnits grantedServiceUnits = grantedMSCC.getGrantedServiceUnits();
        long calculatedTimePulse = grantedServiceUnits.calculateFloorPulse(time);
        reportedUsageSummary.setCalculatedTimePulse(calculatedTimePulse);

        long deductableTime = grantedServiceUnits.calculateDeductableQuota(calculatedTimePulse);
        reportedUsageSummary.setDeductedTimeBalance(deductableTime);
        long unAccountedTime = time - deductableTime;
        if (unAccountedTime > 0 ) {
            unAccountedQuota = grantedMSCC.copy();
            unAccountedQuota.getGrantedServiceUnits().setTime(unAccountedTime);
            reportedUsageSummary.setCurrentUnAccountedTime(unAccountedTime);
        }

        deductMonetaryBalance(abmfCopyOfMonetoryBalance, grantedServiceUnits, deductableTime);

    }

    private void deductMonetaryBalance(MonetaryBalance monetaryBalance, GyServiceUnits grantedServiceUnits, long calculatedPulse) {
        BigDecimal deductableMoney = grantedServiceUnits.calculateDeductableMoney(calculatedPulse);
        monetaryBalance.substract(deductableMoney.doubleValue());
        MonetaryOperationUtils.closeReservation(monetaryBalance, grantedServiceUnits);
        reportedUsageSummary.setDeductedMonetaryBalance(deductableMoney.doubleValue());
    }

    private void deductTimeFromCurrentInMemoryUsage() {

        GyServiceUnits usedServiceUnits = reportedMSCC.getUsedServiceUnits();
        GyServiceUnits grantedServiceUnits = grantedMSCC.getGrantedServiceUnits();
        long calculatedTimePulse;
        if (previousUnAccountedQuota != null) {
            calculatedTimePulse = grantedServiceUnits.calculateCeilPulse(usedServiceUnits.getTime() + previousUnAccountedQuota.getGrantedServiceUnits().getTime());
        } else {
            calculatedTimePulse = grantedServiceUnits.calculateCeilPulse(usedServiceUnits.getTime());
        }
        long deductableTime = grantedServiceUnits.calculateDeductableQuota(calculatedTimePulse);

        deductMonetaryBalance(monetoryBalance, grantedServiceUnits, deductableTime);

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
