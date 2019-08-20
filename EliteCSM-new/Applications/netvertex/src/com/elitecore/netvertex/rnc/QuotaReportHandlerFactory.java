package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.diameter.gy.ReportingReason;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class QuotaReportHandlerFactory {

    private static final String MODULE = "REPORT-HANDLER-FACTORY";
    private PolicyRepository policyRepository;

    public QuotaReportHandlerFactory(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    public ReportedQuotaProcessor createUnAccountedQuotaHandler(NonMonetoryBalance nonMonetoryBalance,
                                                                      MonetaryBalance monetaryBalance,
                                                                      ExecutionContext executionContext,
                                                                      MSCC unaccountedUsage,
                                                                      NonMonetoryBalance nonMonetaryBalanceForABMF,
                                                                      MonetaryBalance monetaryBalanceForABMF,
                                                                      String systemCurrency) {

        ReportedUsageSummary reportedUsageSummary = new ReportedUsageSummary(unaccountedUsage.getRatingGroup(), unaccountedUsage.getServiceIdentifiers());

        DataRateCard rateCard = getRateCardDetails(unaccountedUsage, reportedUsageSummary);

        if (rateCard != null) {
            return new CloseUnAccountedRateCardHandler(
                    unaccountedUsage,
                    monetaryBalance,
                    policyRepository,
                    monetaryBalanceForABMF,
                    reportedUsageSummary,
                    rateCard);
        } else {
            return new CloseUnAccountedQuotaHandler(
                    unaccountedUsage,
                    nonMonetoryBalance,
                    monetaryBalance,
                    executionContext,
                    policyRepository,
                    nonMonetaryBalanceForABMF,
                    monetaryBalanceForABMF,
                    reportedUsageSummary);
        }

    }

    public ReportedQuotaProcessor createReportHandler(MSCC reportedUsage,
                                                      PCRFRequest pcrfRequest,
                                                      MSCC quotaRervationEntry,
                                                      MSCC unAccountedQuota,
                                                      NonMonetoryBalance nonMonetoryBalance,
                                                      MonetaryBalance monetaryBalance,
                                                      ExecutionContext executionContext,
                                                      NonMonetoryBalance abmfCopyOfNonMonetoryBalance,
                                                      MonetaryBalance abmfCopyOfMonetoryBalanceForABMF,
                                                      String systemCurrency) {

        ReportedUsageSummary reportedUsageSummary = new ReportedUsageSummary(quotaRervationEntry.getRatingGroup(), quotaRervationEntry.getServiceIdentifiers());

        DataRateCard rateCard = getRateCardDetails(quotaRervationEntry, reportedUsageSummary);

        if (reportedUsage.getReportingReason() == ReportingReason.FINAL || pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_STOP) ) {

            if (rateCard != null) {
                return new FinalReportedRateCardProcessor(reportedUsage,
                        quotaRervationEntry,
                        unAccountedQuota,
                        monetaryBalance,
                        this.policyRepository,
                        abmfCopyOfMonetoryBalanceForABMF,
                        reportedUsageSummary,
                        rateCard);
            }else {
                return new FinalReportedQuotaProcessor(reportedUsage,
                        quotaRervationEntry,
                        unAccountedQuota,
                        nonMonetoryBalance,
                        monetaryBalance,
                        executionContext,
                        this.policyRepository,
                        abmfCopyOfNonMonetoryBalance,
                        abmfCopyOfMonetoryBalanceForABMF,
                        reportedUsageSummary);
            }
        }

        if (rateCard != null) {
            return new NonFinalReportedRateCardProcessor(reportedUsage,
                    quotaRervationEntry,
                    unAccountedQuota,
                    monetaryBalance,
                    this.policyRepository,
                    abmfCopyOfMonetoryBalanceForABMF,
                    reportedUsageSummary,
                    rateCard);
        }else {
            return new NonFinalReportedQuotaProcessor(reportedUsage,
                    quotaRervationEntry,
                    unAccountedQuota,
                    nonMonetoryBalance,
                    monetaryBalance,
                    executionContext,
                    policyRepository,
                    abmfCopyOfNonMonetoryBalance,
                    abmfCopyOfMonetoryBalanceForABMF,
                    reportedUsageSummary);
        }
    }

    private DataRateCard getRateCardDetails(MSCC quotaRervationEntry, ReportedUsageSummary reportedUsageSummary) {

        GyServiceUnits grantedServiceUnits = quotaRervationEntry.getGrantedServiceUnits();
        UserPackage dataPackage = policyRepository.getPkgDataById(grantedServiceUnits.getPackageId());

        if(dataPackage == null){
			QuotaTopUp quotaTopUpById = policyRepository.getQuotaTopUpById(grantedServiceUnits.getPackageId());
			if (quotaTopUpById != null) {
        		return null;
			}

            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Unable to deduct quota"
                        + ". Reason: Data package not found for pkgId:"
                        + grantedServiceUnits.getPackageId());
            }
            return null;
        }

        DataRateCard rateCard = dataPackage.getDataRateCard(grantedServiceUnits.getQuotaProfileIdOrRateCardId());
        reportedUsageSummary.setPackageId(dataPackage.getId(), dataPackage.getName());
        if(rateCard != null) {
            reportedUsageSummary.setRateCard(rateCard.getId(), rateCard.getName());
        }
        return rateCard;
    }

    public ReportedQuotaProcessor createCloseReservationQuotaProcessor(MSCC quotaReservationEntry,
                                                                       NonMonetoryBalance nonMonetoryBalance,
                                                                       MonetaryBalance monetaryBalance,
                                                                       NonMonetoryBalance abmfCopyOfNonMonetoryBalance,
                                                                       MonetaryBalance abmfCopyOfMonetoryBalance,
                                                                       String systemCurrency) {

        ReportedUsageSummary reportedUsageSummary = new ReportedUsageSummary(quotaReservationEntry.getRatingGroup(), quotaReservationEntry.getServiceIdentifiers());

        DataRateCard rateCard = getRateCardDetails(quotaReservationEntry, reportedUsageSummary);

        if(rateCard != null) {
            return new CloseReservationRateCardHandler(quotaReservationEntry, monetaryBalance, policyRepository, abmfCopyOfMonetoryBalance, reportedUsageSummary);
        }else{
            return new CloseReservationQuotaHandler(quotaReservationEntry, nonMonetoryBalance, monetaryBalance, policyRepository, abmfCopyOfNonMonetoryBalance, abmfCopyOfMonetoryBalance, reportedUsageSummary);
        }
    }
}
