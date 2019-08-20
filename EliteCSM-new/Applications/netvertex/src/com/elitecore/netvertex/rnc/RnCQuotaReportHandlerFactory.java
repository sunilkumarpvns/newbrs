package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.diameter.gy.ReportingReason;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

import java.util.Objects;

public class RnCQuotaReportHandlerFactory {

    private PolicyRepository policyRepository;

    public RnCQuotaReportHandlerFactory(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    public RnCReportedQuotaProcessor createUnAccountedQuotaHandler(MonetaryBalance monetaryBalance,
                                                                      RnCNonMonetaryBalance rnCNonMonetaryBalance,
                                                                      ExecutionContext executionContext,
                                                                      MSCC unaccountedUsage,
                                                                      MonetaryBalance monetaryBalanceForABMF,
                                                                      RnCNonMonetaryBalance nonMonetaryBalanceForABMF) {

        if(Objects.isNull(rnCNonMonetaryBalance)) {
            return new CloseRnCUnAccountedQuotaHandler(
                    unaccountedUsage,
                    monetaryBalance,
                    policyRepository,
                    monetaryBalanceForABMF);
        }else{
            return new CloseRnCNonMonetaryUnAccountedQuotaHandler(
                    unaccountedUsage,
                    rnCNonMonetaryBalance,
                    executionContext,
                    policyRepository,
                    nonMonetaryBalanceForABMF);
        }

    }

    public RnCReportedQuotaProcessor createReportHandler(MSCC reportedUsage,
                                                      PCRFRequest pcrfRequest,
                                                      MSCC quotaRervationEntry,
                                                      MSCC unAccountedQuota,
                                                      RnCNonMonetaryBalance rnCNonMonetaryBalance,
                                                      MonetaryBalance monetaryBalance,
                                                      ExecutionContext executionContext, RnCNonMonetaryBalance abmfCopyOfRnCNonMonetaryBalance,
                                                      MonetaryBalance abmfCopyOfMonetaryBalanceForABMF) {

        if(Objects.isNull(rnCNonMonetaryBalance)) {
            if (reportedUsage.getReportingReason() == ReportingReason.FINAL || pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_STOP)
                    || quotaRervationEntry.getFinalUnitIndiacation() != null) {
                return new FinalRnCReportedQuotaProcessor(reportedUsage,
                        quotaRervationEntry,
                        unAccountedQuota,
                        monetaryBalance,
                        this.policyRepository,
                        abmfCopyOfMonetaryBalanceForABMF);
            }

            return new NonFinalRnCReportedQuotaProcessor(reportedUsage,
                    quotaRervationEntry,
                    unAccountedQuota,
                    monetaryBalance,
                    policyRepository,
                    abmfCopyOfMonetaryBalanceForABMF);
        }else {
            if (reportedUsage.getReportingReason() == ReportingReason.FINAL || pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_STOP)
                    || quotaRervationEntry.getFinalUnitIndiacation() != null) {
                return new FinalRnCNonMonetaryReportedQuotaProcessor(reportedUsage,
                        quotaRervationEntry,
                        unAccountedQuota,
                        rnCNonMonetaryBalance,
                        executionContext,
                        this.policyRepository,
                        abmfCopyOfRnCNonMonetaryBalance);
            }

            return new NonFinalRnCNonMonetaryReportedQuotaProcessor(reportedUsage,
                    quotaRervationEntry,
                    unAccountedQuota,
                    rnCNonMonetaryBalance,
                    executionContext,
                    policyRepository,
                    abmfCopyOfRnCNonMonetaryBalance);
        }
        
    }

    public RnCReportedQuotaProcessor createCloseReservationQuotaProcessor(MSCC quotaReservationEntry,
                                                                       MonetaryBalance monetaryBalance,
                                                                       RnCNonMonetaryBalance rnCNonMonetaryBalance,
                                                                       MonetaryBalance abmfCopyOfMonetaryBalance,
                                                                       RnCNonMonetaryBalance abmfCopyOfNonMonetaryBalance) {
        if(Objects.isNull(rnCNonMonetaryBalance)) {
            return new CloseRnCReservationQuotaHandler(quotaReservationEntry, monetaryBalance, policyRepository, abmfCopyOfMonetaryBalance);
        }else {
            return new CloseRnCNonMonetaryReservationQuotaHandler(quotaReservationEntry, rnCNonMonetaryBalance, policyRepository, abmfCopyOfNonMonetaryBalance);
        }
    }
}
