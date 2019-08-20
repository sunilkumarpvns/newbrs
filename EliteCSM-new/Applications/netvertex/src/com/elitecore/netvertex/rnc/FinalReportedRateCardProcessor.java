package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class FinalReportedRateCardProcessor extends ReportedQuotaProcessor {

    private MSCC reportedMSCC;
    private MSCC grantedMSCC;
    private MSCC unAccountedQuota;
    private MonetaryBalance monetoryBalance;
    private MonetaryBalance abmfCopyOfMonetoryBalance;
    private ReportedUsageSummary reportedUsageSummary;
    private DataRateCard rateCardDetails;

    public FinalReportedRateCardProcessor(MSCC reportedMSCC,
                                       MSCC grantedMSCC,
                                       MSCC unAccountedQuota,
                                       MonetaryBalance monetoryBalance,
                                       PolicyRepository policyRepository,
                                       MonetaryBalance abmfCopyOfMonetoryBalance,
                                       ReportedUsageSummary reportedUsageSummary,
                                       DataRateCard dataRateCard) {
        super(policyRepository);
        this.reportedMSCC = reportedMSCC;
        this.grantedMSCC = grantedMSCC;
        this.unAccountedQuota = unAccountedQuota;
        this.monetoryBalance = monetoryBalance;
        this.abmfCopyOfMonetoryBalance = abmfCopyOfMonetoryBalance;
        this.reportedUsageSummary = reportedUsageSummary;
        this.rateCardDetails = dataRateCard;

    }

    @Override
    public void handle() {

        GyServiceUnits grantedServiceUnits = grantedMSCC.getGrantedServiceUnits();

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

        reportedUsageSummary.setSessionStopTime(new Date(System.currentTimeMillis()));
        reportedUsageSummary.setRevenueCode(grantedServiceUnits.getRevenueCode());

        if(grantedServiceUnits.getReservedMonetaryBalance() >= 0) {
            reportedUsageSummary.setReserveMonetaryBalance(grantedServiceUnits.getReservedMonetaryBalance());
        }

        if (Uom.isVolumeUnit(rateCardDetails.getPulseUom())) {

            reportedUsageSummary.setVolumePulse(grantedServiceUnits.getPulseMinorUnit());

            deductVolumeBalance();
        }else if(Uom.isTimeUnit(rateCardDetails.getPulseUom())){

            reportedUsageSummary.setTimePulse(grantedServiceUnits.getPulseMinorUnit());

            deductTimeBalance();
        }
    }


    private void deductVolumeBalance() {

        GyServiceUnits grantedServiceUnits = grantedMSCC.getGrantedServiceUnits();
        GyServiceUnits usedServiceUnits = reportedMSCC.getUsedServiceUnits();
        long volume = usedServiceUnits.getVolume();

        reportedUsageSummary.setReportedVolume(volume);

        if (unAccountedQuota != null) {
            reportedUsageSummary.setPreviousUnAccountedVolume(unAccountedQuota.getGrantedServiceUnits().getVolume());
            volume += unAccountedQuota.getGrantedServiceUnits().getVolume();
        }

        long calculatedVolumePulse = grantedServiceUnits.calculateCeilPulse(volume);
        reportedUsageSummary.setCalculatedVolumePulse(calculatedVolumePulse);

        long deductableVolume = grantedServiceUnits.calculateDeductableQuota(calculatedVolumePulse);
        reportedUsageSummary.setDeductedVolumeBalance(deductableVolume);

        processMonetaryBalance(grantedServiceUnits, deductableVolume);

    }

    private void processMonetaryBalance(GyServiceUnits grantedServiceUnits, long calculatedPulse) {
        BigDecimal deductableMoney = grantedServiceUnits.calculateDeductableMoney(calculatedPulse);
        abmfCopyOfMonetoryBalance.substract(deductableMoney.doubleValue());
        monetoryBalance.substract(deductableMoney.doubleValue());
        MonetaryOperationUtils.closeReservation(abmfCopyOfMonetoryBalance, grantedServiceUnits);
        MonetaryOperationUtils.closeReservation(monetoryBalance, grantedServiceUnits);
        reportedUsageSummary.setDeductedMonetaryBalance(deductableMoney.doubleValue());
    }

    private void deductTimeBalance() {

        GyServiceUnits grantedServiceUnits = grantedMSCC.getGrantedServiceUnits();
        GyServiceUnits usedServiceUnits = reportedMSCC.getUsedServiceUnits();
        long time = usedServiceUnits.getTime();
        reportedUsageSummary.setReportedTime(time);

        if (unAccountedQuota != null) {
            reportedUsageSummary.setPreviousUnAccountedTime(unAccountedQuota.getGrantedServiceUnits().getTime());
            time += unAccountedQuota.getGrantedServiceUnits().getTime();
        }

        long calculatedTimePulse = grantedServiceUnits.calculateCeilPulse(time);
        reportedUsageSummary.setCalculatedTimePulse(calculatedTimePulse);

        long deductableTime = grantedServiceUnits.calculateDeductableQuota(calculatedTimePulse);
        reportedUsageSummary.setDeductedTimeBalance(deductableTime);

        processMonetaryBalance(grantedServiceUnits, deductableTime);
    }


    @Override
    public ReportedUsageSummary getReportedUsageSummary() {
        return reportedUsageSummary;
    }
}
