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

public class CloseUnAccountedRateCardHandler extends ReportedQuotaProcessor {

    private MSCC unAccountedMSCC;
    private MonetaryBalance monetoryBalance;
    private MonetaryBalance abmfCopyOfMonetoryBalance;
    private ReportedUsageSummary reportedUsageSummary;
    private DataRateCard rateCardDetails;

    public CloseUnAccountedRateCardHandler(MSCC unAccountedMSCC,
                                        MonetaryBalance monetoryBalance,
                                        PolicyRepository policyRepository,
                                        MonetaryBalance abmfCopyOfMonetoryBalance,
                                        ReportedUsageSummary reportedUsageSummary,
                                        DataRateCard dataRateCard) {
        super(policyRepository);
        this.unAccountedMSCC = unAccountedMSCC;
        this.monetoryBalance = monetoryBalance;
        this.abmfCopyOfMonetoryBalance = abmfCopyOfMonetoryBalance;
        this.reportedUsageSummary = reportedUsageSummary;
        this.rateCardDetails = dataRateCard;
    }


    @Override
    public void handle() {

        GyServiceUnits grantedServiceUnits = unAccountedMSCC.getGrantedServiceUnits();
        reportedUsageSummary.setReportOperation(ReportOperation.CLOSE_UNACCOUNTED_QUOTA);

        reportedUsageSummary.setRate(grantedServiceUnits.getActualRate());
        reportedUsageSummary.setRateMinorUnit(grantedServiceUnits.getRateMinorUnit());
        reportedUsageSummary.setRevenueCode(grantedServiceUnits.getRevenueCode());
        reportedUsageSummary.setRateCardName(grantedServiceUnits.getRateCardName());
        reportedUsageSummary.setRateCardId(grantedServiceUnits.getRateCardId());
        reportedUsageSummary.setRateCardGroupName(grantedServiceUnits.getRateCardGroupName());
        reportedUsageSummary.setPackageId(grantedServiceUnits.getPackageId(), PolicyManager.getInstance().getPackageName(grantedServiceUnits.getPackageId()));
        reportedUsageSummary.setProductOfferName(Objects.isNull(PolicyManager.getInstance().getProductOffer().byId(grantedServiceUnits.getProductOfferId())) ? null :
                PolicyManager.getInstance().getProductOffer().byId(grantedServiceUnits.getProductOfferId()).getName());


        if (Uom.isVolumeUnit(rateCardDetails.getPulseUom())) {

            reportedUsageSummary.setVolumePulse(grantedServiceUnits.getPulseMinorUnit());

            deductVolumeBalance();
        }else if(Uom.isTimeUnit(rateCardDetails.getPulseUom())){

            reportedUsageSummary.setTimePulse(grantedServiceUnits.getPulseMinorUnit());

            deductTimeBalance();
        }
    }

    private void deductVolumeBalance() {

        GyServiceUnits grantedServiceUnits = unAccountedMSCC.getGrantedServiceUnits();
        long volume = grantedServiceUnits.getVolume();

        reportedUsageSummary.setPreviousUnAccountedVolume(grantedServiceUnits.getPulseMinorUnit());

        long calculatedVolumePulse = grantedServiceUnits.calculateCeilPulse(volume);
        reportedUsageSummary.setCalculatedVolumePulse(calculatedVolumePulse);

        long deductableVolume = grantedServiceUnits.calculateDeductableQuota(calculatedVolumePulse);
        reportedUsageSummary.setDeductedVolumeBalance(deductableVolume);

        processMonetaryBalance(grantedServiceUnits, deductableVolume);
    }

    private void processMonetaryBalance(GyServiceUnits grantedServiceUnits, long calculatedPulse) {
        BigDecimal deductableMoney = grantedServiceUnits.calculateDeductableMoney(calculatedPulse);
        reportedUsageSummary.setDeductedMonetaryBalance(deductableMoney.doubleValue());
        monetoryBalance.substract(deductableMoney.doubleValue());
        abmfCopyOfMonetoryBalance.substract(deductableMoney.doubleValue());
    }

    private void deductTimeBalance() {
        GyServiceUnits grantedServiceUnits = unAccountedMSCC.getGrantedServiceUnits();
        long time = grantedServiceUnits.getTime();

        reportedUsageSummary.setPreviousUnAccountedTime(grantedServiceUnits.getPulseMinorUnit());

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
