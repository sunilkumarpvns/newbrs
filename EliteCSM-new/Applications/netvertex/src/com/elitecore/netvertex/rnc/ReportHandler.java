package com.elitecore.netvertex.rnc;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.data.ResultCode;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.servicepolicy.handler.ServiceHandler;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.Map.Entry;

public class ReportHandler extends ServiceHandler {

    private static final String MODULE = "REPORT-HNDLR";
    private QuotaReportHandlerFactory factory;
    private ABMFOperationHandler abmfOperationHandler;

    public ReportHandler(PCRFServiceContext serviceContext, QuotaReportHandlerFactory factory) {
        super(serviceContext);
        this.factory = factory;
        this.abmfOperationHandler = new ABMFOperationHandler();
    }


    @Override
    protected void process(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {
        PCRFRequest request = (PCRFRequest) serviceRequest;
        PCRFResponse response = (PCRFResponse) serviceResponse;


        QuotaReservation quotaReservation = response.getQuotaReservation();

        if (quotaReservation == null) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Unable to deduct reported usage. Reason: Quota reservation not found");
            }
            return;
        }

        SubscriberNonMonitoryBalance subscriberNonMonitoryBalance = getCurrentNonMonetaryBalance(executionContext, response);

        if (Objects.isNull(subscriberNonMonitoryBalance)) {
            return;
        }

        boolean isMonetaryTransaction = isMonetaryReservationExist(request, response) || (request.getPCRFEvents().contains(PCRFEvent.SESSION_STOP) && isMonetaryUnAccountedQuotaExist(response));
        if(isMonetaryTransaction) {
            try {
                getCurrentMonetaryBalance(executionContext, response);
            } catch (OperationFailedException e) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Unable to deduct reported usage. Reason: " + e.getMessage());
                }
                getLogger().trace(MODULE, e);
                return;
            }
        }

        response.setReportSummary(new ReportSummary());

		int reportedMSCCSize = CollectionUtils.isEmpty(request.getReportedMSCCs()) ? 0 : request.getReportedMSCCs().size();

        Map<String, MonetaryBalance> abmfCopyOfMonetaryBalance = new HashMap<>(reportedMSCCSize);

        Map<String, NonMonetoryBalance> abmfCopyOfNonMonetaryBalance = new HashMap<>(reportedMSCCSize);

        deductReportedUsage(executionContext, request, response, abmfCopyOfMonetaryBalance, abmfCopyOfNonMonetaryBalance);


        if (request.getPCRFEvents().contains(PCRFEvent.SESSION_STOP)) {
            closeReservation(response, abmfCopyOfMonetaryBalance, abmfCopyOfNonMonetaryBalance);
            closeUnAccountedQuota(response, executionContext, abmfCopyOfNonMonetaryBalance, abmfCopyOfMonetaryBalance);
            response.setQuotaReservation(null);
            response.setUnAccountedQuota(null);
        } else {
            if (Objects.nonNull(response.getQuotaReservation()) && response.getQuotaReservation().get().isEmpty()) {
                response.setQuotaReservation(null);
            }

            if (Objects.nonNull(response.getUnAccountedQuota()) && response.getUnAccountedQuota().get().isEmpty()) {
                response.setUnAccountedQuota(null);
            }

        }

        postProcessingForRnCPackage(response);

        if(getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, response.getReportSummary().toString());
        }

        abmfOperationHandler.abmfMonetaryOperation(executionContext, response, abmfCopyOfMonetaryBalance);
        abmfOperationHandler.abmfNonMonetaryOperation(executionContext, response, abmfCopyOfNonMonetaryBalance);

        if(getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Balance After Report Handler Processing: " + response.getCurrentNonMonetoryBalance().toString());
        }
    }

    private void postProcessingForRnCPackage(PCRFResponse response) {

        for (ReportedUsageSummary reportedUsageSummary : response.getReportSummary().getReportedUsageSummaries()) {
            setRevenueDetail(reportedUsageSummary);
            setCurrencyDetail(reportedUsageSummary);
        }
    }

    private void setCurrencyDetail(ReportedUsageSummary reportedUsageSummary){
        UserPackage userPackage = getServerContext().getPolicyRepository().getPkgDataById(reportedUsageSummary.getPackageId());
        if (Objects.nonNull(userPackage)) {
            reportedUsageSummary.setCurrency(userPackage.getCurrency());
            reportedUsageSummary.setExponent(Currency.getInstance(userPackage.getCurrency()).getDefaultFractionDigits());
        }
    }

    private void setRevenueDetail(ReportedUsageSummary reportedUsageSummary) {

        QuotaProfile quotaProfile = getServerContext().getPolicyRepository().getQuotaProfile(reportedUsageSummary.getPackageId(), reportedUsageSummary.getQuotaProfileId());
        if (Objects.nonNull(quotaProfile)) {
            for (Map<String, QuotaProfileDetail> quotaProfileFupWise : quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()) {
                for (Entry<String, QuotaProfileDetail> quotaProfileServiceLevel : quotaProfileFupWise.entrySet()) {
                    RncProfileDetail rnCProfileDetail = (RncProfileDetail) quotaProfileServiceLevel.getValue();

                    if (Objects.nonNull(rnCProfileDetail)
                            && rnCProfileDetail.getRatingGroup().getIdentifier() == reportedUsageSummary.getRatingGroup()
                            && reportedUsageSummary.getLevel() == rnCProfileDetail.getFupLevel()
                            && Strings.isNullOrEmpty(rnCProfileDetail.getRevenueDetail()) == false) {
                        reportedUsageSummary.setRevenueCode(rnCProfileDetail.getRevenueDetail());
                    }
                }
            }
        }
    }

    private boolean isMonetaryReservationExist(PCRFRequest request,PCRFResponse response) {

        if(request.getPCRFEvents().contains(PCRFEvent.SESSION_STOP)){
            Set<Map.Entry<Long, MSCC>> quotaReservations = response.getQuotaReservation().get();

            if (CollectionUtils.isEmpty(quotaReservations)) {
                return false;
            }
            for (Map.Entry<Long, MSCC> entry : quotaReservations) {

                MSCC quotaReservationEntry = entry.getValue();

                if (quotaReservationEntry == null) {
                    continue;
                }

                if (ResultCode.BALANCE_DOES_NOT_EXISTS == quotaReservationEntry.getResultCode()) {
                    continue;
                }

                if (Objects.nonNull(quotaReservationEntry.getGrantedServiceUnits().getMonetaryBalanceId())) {
                    return true;
                }
            }
            return false;
        }

        List<MSCC> reportedMSCCs = request.getReportedMSCCs();

        if (CollectionUtils.isEmpty(reportedMSCCs)) {
            return false;
        }

        QuotaReservation quotaReservation = response.getQuotaReservation();

        for (MSCC reservation : reportedMSCCs) {

            reservation = quotaReservation.get(reservation.getRatingGroup());

            if (reservation == null) {
                continue;
            }

            if (ResultCode.BALANCE_DOES_NOT_EXISTS == reservation.getResultCode()) {
                continue;
            }

            if (Objects.nonNull(reservation.getGrantedServiceUnits().getMonetaryBalanceId())) {
                return true;
            }
        }
        return false;
    }

    private boolean isMonetaryUnAccountedQuotaExist(PCRFResponse response){
        if (Objects.isNull(response.getUnAccountedQuota())) {
            return false;
        }

        for (Map.Entry<Long, MSCC> entry : response.getUnAccountedQuota().get()) {

            MSCC unAccountedQuotaEntry = entry.getValue();
            if (Objects.nonNull(unAccountedQuotaEntry.getGrantedServiceUnits().getMonetaryBalanceId())) {
                return true;
            }
        }
        return false;
    }

    private void closeUnAccountedQuota(PCRFResponse response,
                                       ExecutionContext executionContext,
                                       Map<String, NonMonetoryBalance> abmfCopyOfSubscriberNonMonitoryBalances,
                                       Map<String, MonetaryBalance> abmfCopyOfSubscriberMonetaryBalances) {

        if (Objects.isNull(response.getUnAccountedQuota())) {
            return;
        }

        for (Map.Entry<Long, MSCC> entry : response.getUnAccountedQuota().get()) {

            MSCC unAccountedQuotaEntry = entry.getValue();

            NonMonetoryBalance nonMonetoryBalance = null;
            if(unAccountedQuotaEntry.getGrantedServiceUnits().getBalanceId() != null) {
                nonMonetoryBalance = getBalance(unAccountedQuotaEntry, response.getCurrentNonMonetoryBalance());

                if (nonMonetoryBalance == null) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Unable to deduct reported usage for Rg:" + unAccountedQuotaEntry.getRatingGroup() + ". Reason: Non-Monetory balance not found");
                    }

                    continue;
                }
            }

            MonetaryBalance monetaryBalance = null;
            MonetaryBalance monetaryBalanceForABMF = null;
            if (Objects.nonNull(unAccountedQuotaEntry.getGrantedServiceUnits().getMonetaryBalanceId())) {
                monetaryBalance = getBalance(unAccountedQuotaEntry, response.getCurrentMonetaryBalance());

                if (monetaryBalance == null) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Unable to deduct monetary balance for reported usage for Rg:" + unAccountedQuotaEntry.getRatingGroup() + ". Reason: Monetory balance not found");
                    }

                    continue;
                }

                monetaryBalanceForABMF = getOrCreateCopyForAbmf(abmfCopyOfSubscriberMonetaryBalances, monetaryBalance);


            }


            ReportedQuotaProcessor unAccountedQuotaHandler = factory.createUnAccountedQuotaHandler(
                    nonMonetoryBalance,
                    monetaryBalance,
                    executionContext,
                    unAccountedQuotaEntry,
                    getOrCreateCopyForAbmf(abmfCopyOfSubscriberNonMonitoryBalances, nonMonetoryBalance),
                    monetaryBalanceForABMF,
                    null);
            unAccountedQuotaHandler.handle();

            response.getReportSummary().add(unAccountedQuotaHandler.getReportedUsageSummary());

        }


    }

    private MonetaryBalance getOrCreateCopyForAbmf(Map<String, MonetaryBalance> abmfCopyOfSubscriberMonetaryBalances, MonetaryBalance monetaryBalance) {
        MonetaryBalance balance = abmfCopyOfSubscriberMonetaryBalances.get(monetaryBalance.getId());

        if (Objects.isNull(balance)) {
            balance = monetaryBalance.copy();
            abmfCopyOfSubscriberMonetaryBalances.put(balance.getId(), balance);
        }

        return balance;
    }

    private NonMonetoryBalance getOrCreateCopyForAbmf(Map<String, NonMonetoryBalance> abmfCopyOfSubscriberNonMonitoryBalances, NonMonetoryBalance nonMonetoryBalance) {

        if(nonMonetoryBalance == null){
            return null;
        }
        NonMonetoryBalance balance = abmfCopyOfSubscriberNonMonitoryBalances.get(nonMonetoryBalance.getId());

        if (Objects.isNull(balance)) {
            balance = nonMonetoryBalance.copy();
            abmfCopyOfSubscriberNonMonitoryBalances.put(balance.getId(), balance);
        }


        return balance;
    }

    private void closeReservation(PCRFResponse response, Map<String, MonetaryBalance> abmfCopyOfSubscriberMonetaryBalance, Map<String, NonMonetoryBalance> abmfCopyOfSubscriberNonMonitoryBalance) {

        for (Map.Entry<Long, MSCC> entry : response.getQuotaReservation().get()) {

            MSCC quotaReservationEntry = entry.getValue();

            GyServiceUnits grantedServiceUnits = quotaReservationEntry.getGrantedServiceUnits();
            if (grantedServiceUnits == null) {
            	continue;
			}

            if (grantedServiceUnits.isNonMonetaryReservationRequired() == false && grantedServiceUnits.getReservedMonetaryBalance() < 0) {
                continue;
            }

            NonMonetoryBalance nonMonetoryBalance = null;
            if(grantedServiceUnits.getBalanceId() != null) {
                nonMonetoryBalance = getBalance(quotaReservationEntry, response.getCurrentNonMonetoryBalance());

                if (nonMonetoryBalance == null) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Unable to deduct reported usage for Rg:" + quotaReservationEntry.getRatingGroup() + ". Reason: Non-Monetory balance not found");
                    }

                    continue;
                }
            }


            MonetaryBalance monetaryBalance = null;
            if (Objects.nonNull(grantedServiceUnits.getMonetaryBalanceId()) && grantedServiceUnits.getReservedMonetaryBalance() >= 0) {
                monetaryBalance = getBalance(quotaReservationEntry, response.getCurrentMonetaryBalance());

                if (monetaryBalance == null) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Unable to deduct monetary balance for reported usage for Rg:" + quotaReservationEntry.getRatingGroup() + ". Reason: Monetory balance not found");
                    }

                    continue;
                }
            }


            NonMonetoryBalance abmfCopyOfNonMonetaryBalance = null;
            if (grantedServiceUnits.isNonMonetaryReservationRequired()) {
                abmfCopyOfNonMonetaryBalance = getOrCreateCopyForAbmf(abmfCopyOfSubscriberNonMonitoryBalance, nonMonetoryBalance);
            }
            MonetaryBalance abmfCopyOfMonetaryBalance = null;
            if (Objects.nonNull(monetaryBalance) && grantedServiceUnits.getReservedMonetaryBalance() >= 0) {
                abmfCopyOfMonetaryBalance = getOrCreateCopyForAbmf(abmfCopyOfSubscriberMonetaryBalance, monetaryBalance);
            }

            ReportedQuotaProcessor closeReservationQuotaProcessor =  factory.createCloseReservationQuotaProcessor(quotaReservationEntry,
                    nonMonetoryBalance,
                    monetaryBalance,
                    abmfCopyOfNonMonetaryBalance,
                    abmfCopyOfMonetaryBalance,
                    null);

            closeReservationQuotaProcessor.handle();

            response.getReportSummary().add(closeReservationQuotaProcessor.getReportedUsageSummary());


        }
    }

    private void deductReportedUsage(ExecutionContext executionContext,
                                     PCRFRequest request,
                                     PCRFResponse response,
                                     Map<String, MonetaryBalance> abmfCopyOfSubscriberMonetaryBalance,
                                     Map<String, NonMonetoryBalance> abmfCopyOfSubscriberNonMonitoryBalance) {

    	if (CollectionUtils.isEmpty(request.getReportedMSCCs())) {
    		if (getLogger().isDebugLogLevel()) {
    			getLogger().debug(MODULE, "Reported MSCC not found from pcrf request");
			}
			return;
		}

        for (MSCC reportedQuota : request.getReportedMSCCs()) {

            if(Objects.isNull(reportedQuota.getUsedServiceUnits())){
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Unable to deduct reported usage for Rg:" + reportedQuota.getRatingGroup() + ". Reason: Used service units not found");
                }
                continue;
            }

            MSCC quotaReservationEntry = response.getQuotaReservation().remove(reportedQuota.getRatingGroup());

            if (quotaReservationEntry == null) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Unable to deduct reported usage for Rg:" + reportedQuota.getRatingGroup() + ". Reason: Quota reservation not found");
                }
                continue;
            }

            if (ResultCode.BALANCE_DOES_NOT_EXISTS == quotaReservationEntry.getResultCode()) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Unable to deduct reported usage for Rg:" + reportedQuota.getRatingGroup() + ". Reason: Balance insufficient");
                }
                continue;
            }

            NonMonetoryBalance nonMonetoryBalance = null;
            if(quotaReservationEntry.getGrantedServiceUnits().getBalanceId() != null) {
                nonMonetoryBalance = getBalance(quotaReservationEntry, response.getCurrentNonMonetoryBalance());

                if (nonMonetoryBalance == null) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Unable to deduct reported usage for Rg:" + quotaReservationEntry.getRatingGroup() + ". Reason: Non-Monetory balance not found");
                    }

                    continue;
                }
            }
            MonetaryBalance monetaryBalance = null;
            MonetaryBalance monetoryBalanceForABMF = null;
            if (Objects.nonNull(quotaReservationEntry.getGrantedServiceUnits().getMonetaryBalanceId())) {
                monetaryBalance = getBalance(quotaReservationEntry, response.getCurrentMonetaryBalance());

                if (monetaryBalance == null) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Unable to deduct monetary balance for reported usage for Rg:" + reportedQuota.getRatingGroup() + ". Reason: Monetory balance not found");
                    }

                    continue;
                }

                monetoryBalanceForABMF = getOrCreateCopyForAbmf(abmfCopyOfSubscriberMonetaryBalance, monetaryBalance);
            }

            MSCC unaccountedQuota = null;
            if (response.getUnAccountedQuota() != null) {
                unaccountedQuota = response.getUnAccountedQuota().remove(quotaReservationEntry.getRatingGroup());
            }


            ReportedQuotaProcessor reportHandler = factory.createReportHandler(reportedQuota,
                    request,
                    quotaReservationEntry,
                    unaccountedQuota,
                    nonMonetoryBalance,
                    monetaryBalance,
                    executionContext,
                    getOrCreateCopyForAbmf(abmfCopyOfSubscriberNonMonitoryBalance, nonMonetoryBalance),
                    monetoryBalanceForABMF,
                    null);

            reportHandler.handle();
            reportHandler.getReportedUsageSummary().setCoreSessionId(response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));

            MSCC unAccountedUsage = reportHandler.getUnAccountedUsage();

            response.getReportSummary().add(reportHandler.getReportedUsageSummary());

            if (unAccountedUsage != null) {
                QuotaReservation unAccountedQuota = response.getUnAccountedQuota();

                if (unAccountedQuota == null) {
                    unAccountedQuota = new QuotaReservation();
                    response.setUnAccountedQuota(unAccountedQuota);
                }

                unAccountedQuota.put(unAccountedUsage);
            }

        }
    }

    @Override
    protected boolean isApplicable(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
        PCRFRequest request = (PCRFRequest) serviceRequest;

        boolean isGySession = SessionTypeConstant.GY.val.equals(request.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val));
        boolean isRadiusSession = SessionTypeConstant.RADIUS.val.equals(request.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val));

        if(isGySession == false && isRadiusSession == false){
            return false;
        }

        boolean isDataServiceRequest = PCRFKeyValueConstants.DATA_SERVICE_ID.val.equals(request.getAttribute(PCRFKeyConstants.CS_SERVICE.val));
        if (isDataServiceRequest == false) {
            return false;
        }

        if (request.getPCRFEvents().contains(PCRFEvent.SESSION_START)) {
            return false;
        } else if (request.getPCRFEvents().contains(PCRFEvent.SESSION_STOP)) {
            return true;
        } else {
            return CollectionUtils.isNotEmpty(request.getReportedMSCCs());
        }
    }

    @Override
    public String getName() {
        return "REPORT-HANDLER";
    }

    private SubscriberNonMonitoryBalance getCurrentNonMonetaryBalance(ExecutionContext executionContext, PCRFResponse response) {
        SubscriberNonMonitoryBalance currentNonMonetaryBalance;
        try {
            currentNonMonetaryBalance = executionContext.getCurrentNonMonetoryBalance();
        } catch (OperationFailedException e) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Unable to deduct reported usage. Reason: " + e.getMessage());
            }
            if (com.elitecore.corenetvertex.spr.exceptions.ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
                getLogger().trace(MODULE, e);
            }
            return null;
        }

        SubscriberNonMonitoryBalance newBalance = currentNonMonetaryBalance.copy();

        response.setCurrentNonMonetoryBalance(newBalance);
        response.setPreviousNonMonetoryBalance(currentNonMonetaryBalance);

        return newBalance;
    }

    private SubscriberMonetaryBalance getCurrentMonetaryBalance(ExecutionContext executionContext, PCRFResponse response) throws OperationFailedException {
        SubscriberMonetaryBalance currentMonetaryBalance = executionContext.getCurrentMonetaryBalance();
        SubscriberMonetaryBalance newBalance = currentMonetaryBalance.copy();

        response.setCurrentMonetaryBalance(newBalance);
        response.setPreviousMonetaryBalance(currentMonetaryBalance);

        return newBalance;
    }

    private MonetaryBalance getBalance(MSCC entry, SubscriberMonetaryBalance newBalance) {

        GyServiceUnits serviceUnits = entry.getGrantedServiceUnits();

        return newBalance.getBalanceById(serviceUnits.getMonetaryBalanceId());
    }

    private NonMonetoryBalance getBalance(MSCC entry, SubscriberNonMonitoryBalance newBalance) {

        GyServiceUnits serviceUnits = entry.getGrantedServiceUnits();
        return newBalance.getBalanceById(serviceUnits.getBalanceId());
    }
}
