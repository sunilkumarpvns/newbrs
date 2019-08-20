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
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.servicepolicy.handler.ServiceHandler;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class RnCReportHandler extends ServiceHandler {

    private static final String MODULE = "RNC-REPORT-HNDLR";
    private RnCQuotaReportHandlerFactory factory;
    private ABMFOperationHandler abmfOperationHandler;

    public RnCReportHandler(PCRFServiceContext serviceContext, RnCQuotaReportHandlerFactory factory) {
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

        SubscriberRnCNonMonetaryBalance subscriberRnCNonMonetaryBalance = getCurrentRnCNonMonetaryBalance(executionContext, response);

        boolean isMonetaryTransaction= isMonetaryReservationExist(request, response)
                || (request.getPCRFEvents().contains(PCRFEvent.SESSION_STOP) && isMonetaryUnAccountedQuotaExist(response));

        SubscriberMonetaryBalance subscriberMonetaryBalance=null;
        if(isMonetaryTransaction) {
            try {
                subscriberMonetaryBalance = getCurrentMonetaryBalance(executionContext, response);
            } catch (OperationFailedException e) {
                if (getLogger().isWarnLogLevel()) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Unable to deduct reported usage. Reason: " + e.getMessage());
                    }
                    getLogger().trace(MODULE, e);
                }
            }
        }

        if (isNull(subscriberRnCNonMonetaryBalance) && isNull(subscriberMonetaryBalance)) {
            return;
        }

        response.setReportSummary(new ReportSummary());

        Map<String, RnCNonMonetaryBalance> abmfCopyOfRnCNonMonetaryBalance = new HashMap<>();
        Map<String, MonetaryBalance> abmfCopyOfMonetaryBalance = new HashMap<>();

        if(nonNull(request.getReportedMSCCs())) {
            deductReportedUsage(executionContext, request, response, abmfCopyOfMonetaryBalance, abmfCopyOfRnCNonMonetaryBalance);
        }

        if (request.getPCRFEvents().contains(PCRFEvent.SESSION_STOP)) {
            closeReservation(response, abmfCopyOfRnCNonMonetaryBalance, abmfCopyOfMonetaryBalance);
            closeUnAccountedQuota(response, executionContext, abmfCopyOfRnCNonMonetaryBalance, abmfCopyOfMonetaryBalance);
            response.setQuotaReservation(null);
            response.setUnAccountedQuota(null);
        } else {
            if (nonNull(response.getQuotaReservation()) && response.getQuotaReservation().get().isEmpty()) {
                response.setQuotaReservation(null);
            }

            if (nonNull(response.getUnAccountedQuota()) && response.getUnAccountedQuota().get().isEmpty()) {
                response.setUnAccountedQuota(null);
            }
        }

        if(nonNull(response.getReportSummary())){
            ProductOffer productOffer;
            for(ReportedUsageSummary reportedUsageSummary : response.getReportSummary().getReportedUsageSummaries()){
                if(Strings.isNullOrBlank(reportedUsageSummary.getProductOfferName()) == false){
                    productOffer = getServerContext().getPolicyRepository().getProductOffer().byName(reportedUsageSummary.getProductOfferName());
                    reportedUsageSummary.setCurrency(isNull(productOffer)?null:productOffer.getCurrency());
                    if(nonNull(productOffer) && productOffer.isFnFOffer()){
                        reportedUsageSummary.setTariffType(PCRFKeyValueConstants.TARIFF_TYPE_FNF.val);
                    }
                    else{
                        reportedUsageSummary.setTariffType(PCRFKeyValueConstants.TARIFF_TYPE_NORMAL.val);
                    }
                }
            }
        }

        if(getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, response.getReportSummary().toString());
        }

        abmfOperationHandler.abmfMonetaryOperation(executionContext, response, abmfCopyOfMonetaryBalance);
        abmfOperationHandler.abmfRnCNonMonetaryOperation(executionContext, response, abmfCopyOfRnCNonMonetaryBalance);

        if(getLogger().isInfoLogLevel()) {
            if(nonNull(subscriberRnCNonMonetaryBalance)) {
                getLogger().info(MODULE, "Non Monetary Balance After Report Handler Processing: " + subscriberRnCNonMonetaryBalance.toString());
            }

            if(nonNull(subscriberMonetaryBalance) && isMonetaryTransaction){
                getLogger().info(MODULE, "Monetary Balance After Report Handler Processing: " + subscriberMonetaryBalance.toString());
            }
        }
    }

    private void closeUnAccountedQuota(PCRFResponse response,
                                       ExecutionContext executionContext,
                                       Map<String, RnCNonMonetaryBalance> abmfCopyOfRnCNonMonetaryBalances,
                                       Map<String, MonetaryBalance> abmfCopyOfSubscriberMonetaryBalances) {

        if (isNull(response.getUnAccountedQuota())) {
            return;
        }

        for (Map.Entry<Long, MSCC> entry : response.getUnAccountedQuota().get()) {

            MSCC unAccountedQuotaEntry = entry.getValue();

            RnCNonMonetaryBalance rnCNonMonetaryBalance = null;
            RnCNonMonetaryBalance nonMonetaryBalanceForABMF = null;
            MonetaryBalance monetaryBalance = null;
            MonetaryBalance monetaryBalanceForABMF = null;

            if (nonNull(unAccountedQuotaEntry.getGrantedServiceUnits().getBalanceId())) {
                rnCNonMonetaryBalance = getBalance(unAccountedQuotaEntry, response.getCurrentRnCNonMonetaryBalance());

                if (rnCNonMonetaryBalance == null) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Unable to deduct non monetary balance for reported usage for Rg:" + unAccountedQuotaEntry.getRatingGroup() + ". Reason: Non monetary balance not found");
                    }

                    continue;
                }

                nonMonetaryBalanceForABMF = getOrCreateCopyForAbmf(abmfCopyOfRnCNonMonetaryBalances, rnCNonMonetaryBalance);
            } else if (nonNull(unAccountedQuotaEntry.getGrantedServiceUnits().getMonetaryBalanceId())) {
                monetaryBalance = getBalance(unAccountedQuotaEntry, response.getCurrentMonetaryBalance());

                if (monetaryBalance == null) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Unable to deduct monetary balance for reported usage for Rg:" + unAccountedQuotaEntry.getRatingGroup() + ". Reason: Monetary balance not found");
                    }

                    continue;
                }

                monetaryBalanceForABMF = getOrCreateCopyForAbmf(abmfCopyOfSubscriberMonetaryBalances, monetaryBalance);
            }


            RnCReportedQuotaProcessor unAccountedQuotaHandler = factory.createUnAccountedQuotaHandler(
                    monetaryBalance,
                    rnCNonMonetaryBalance,
                    executionContext,
                    unAccountedQuotaEntry,
                    monetaryBalanceForABMF,
                    nonMonetaryBalanceForABMF);
            unAccountedQuotaHandler.handle();

            unAccountedQuotaHandler.getReportedUsageSummary().setCoreSessionId(response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));
            response.getReportSummary().add(unAccountedQuotaHandler.getReportedUsageSummary());
        }
    }

    private boolean isMonetaryReservationExist(PCRFRequest request, PCRFResponse response) {
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

                if (nonNull(quotaReservationEntry.getGrantedServiceUnits().getMonetaryBalanceId())) {
                    return true;
                }
            }
            return false;
        }

        List<MSCC> reportedMSCCs = request.getReportedMSCCs();

        if(CollectionUtils.isEmpty(reportedMSCCs)){
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

            if (nonNull(reservation.getGrantedServiceUnits().getMonetaryBalanceId())) {
                return true;
            }
        }
        return false;


    }

    private boolean isMonetaryUnAccountedQuotaExist(PCRFResponse response){
        if (isNull(response.getUnAccountedQuota())) {
            return false;
        }

        for (Map.Entry<Long, MSCC> entry : response.getUnAccountedQuota().get()) {

            MSCC unAccountedQuotaEntry = entry.getValue();
            if (nonNull(unAccountedQuotaEntry.getGrantedServiceUnits().getMonetaryBalanceId())) {
                return true;
            }
        }
        return false;
    }

    private MonetaryBalance getOrCreateCopyForAbmf(Map<String, MonetaryBalance> abmfCopyOfSubscriberMonetaryBalances, MonetaryBalance monetaryBalance) {
        MonetaryBalance balance = abmfCopyOfSubscriberMonetaryBalances.get(monetaryBalance.getId());

        if (isNull(balance)) {
            balance = monetaryBalance.copy();
            abmfCopyOfSubscriberMonetaryBalances.put(balance.getId(), balance);
        }

        return balance;
    }

    private RnCNonMonetaryBalance getOrCreateCopyForAbmf(Map<String, RnCNonMonetaryBalance> abmfCopyOfSubscriberRnCNonMonitoryBalances, RnCNonMonetaryBalance rnCNonMonetaryBalance) {

        if(rnCNonMonetaryBalance == null){
            return null;
        }
        RnCNonMonetaryBalance balance = abmfCopyOfSubscriberRnCNonMonitoryBalances.get(rnCNonMonetaryBalance.getId());

        if (isNull(balance)) {
            balance = rnCNonMonetaryBalance.copy();
            abmfCopyOfSubscriberRnCNonMonitoryBalances.put(balance.getId(), balance);
        }

        return balance;
    }

    private void closeReservation(PCRFResponse response, Map<String, RnCNonMonetaryBalance> abmfCopyOfRnCNonMonetaryBalance, Map<String, MonetaryBalance> abmfCopyOfSubscriberMonetaryBalance) {

        for (Map.Entry<Long, MSCC> entry : response.getQuotaReservation().get()) {

            MSCC quotaReservationEntry = entry.getValue();

            GyServiceUnits grantedServiceUnits = quotaReservationEntry.getGrantedServiceUnits();
            if (grantedServiceUnits.isNonMonetaryReservationRequired() == false && grantedServiceUnits.getReservedMonetaryBalance() < 0) {
                continue;
            }

            RnCNonMonetaryBalance rnCNonMonetaryBalance = null;
            RnCNonMonetaryBalance abmfCopyOfNonMonetaryBalance = null;
            MonetaryBalance monetaryBalance = null;
            MonetaryBalance abmfCopyOfMonetaryBalance = null;

            if(nonNull(grantedServiceUnits.getBalanceId()) && grantedServiceUnits.isNonMonetaryReservationRequired()) {
                rnCNonMonetaryBalance = getBalance(quotaReservationEntry, response.getCurrentRnCNonMonetaryBalance());

                if (rnCNonMonetaryBalance == null) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Unable to deduct reported usage for Rg:" + quotaReservationEntry.getRatingGroup() + ". Reason: Non-Monetary balance not found");
                    }

                    continue;
                }
                abmfCopyOfNonMonetaryBalance = getOrCreateCopyForAbmf(abmfCopyOfRnCNonMonetaryBalance, rnCNonMonetaryBalance);
            } else if (nonNull(grantedServiceUnits.getMonetaryBalanceId()) && grantedServiceUnits.getReservedMonetaryBalance() >= 0) {
                monetaryBalance = getBalance(quotaReservationEntry, response.getCurrentMonetaryBalance());

                if (monetaryBalance == null) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Unable to deduct monetary balance for reported usage for Rg:" + quotaReservationEntry.getRatingGroup() + ". Reason: Monetary balance not found");
                    }

                    continue;
                }
                abmfCopyOfMonetaryBalance = getOrCreateCopyForAbmf(abmfCopyOfSubscriberMonetaryBalance, monetaryBalance);
            }

            RnCReportedQuotaProcessor closeReservationQuotaProcessor =  factory.createCloseReservationQuotaProcessor(quotaReservationEntry,
                    monetaryBalance,
                    rnCNonMonetaryBalance,
                    abmfCopyOfMonetaryBalance,
                    abmfCopyOfNonMonetaryBalance);

            closeReservationQuotaProcessor.handle();

            response.getReportSummary().add(closeReservationQuotaProcessor.getReportedUsageSummary());
        }
    }

    private void deductReportedUsage(ExecutionContext executionContext,
                                     PCRFRequest request,
                                     PCRFResponse response,
                                     Map<String, MonetaryBalance> abmfCopyOfSubscriberMonetaryBalance,
                                     Map<String, RnCNonMonetaryBalance> abmfCopyOfSubscriberRnCNonMonetaryBalance) {

        for (MSCC reportedQuota : request.getReportedMSCCs()) {

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

            RnCNonMonetaryBalance rnCNonMonetaryBalance = null;
            MonetaryBalance monetaryBalance = null;
            MonetaryBalance monetaryBalanceForABMF = null;

            if(quotaReservationEntry.getGrantedServiceUnits().getBalanceId() != null) {
                rnCNonMonetaryBalance = getBalance(quotaReservationEntry, response.getCurrentRnCNonMonetaryBalance());

                if (rnCNonMonetaryBalance == null) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Unable to deduct reported usage for Rg:" + quotaReservationEntry.getRatingGroup() + ". Reason: Non-Monetary balance not found");
                    }

                    continue;
                }
            } else if (nonNull(quotaReservationEntry.getGrantedServiceUnits().getMonetaryBalanceId())) {
                monetaryBalance = getBalance(quotaReservationEntry, response.getCurrentMonetaryBalance());

                if (monetaryBalance == null) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Unable to deduct monetary balance for reported usage for Rg:" + reportedQuota.getRatingGroup() + ". Reason: Monetary balance not found");
                    }

                    continue;
                }

                monetaryBalanceForABMF = getOrCreateCopyForAbmf(abmfCopyOfSubscriberMonetaryBalance, monetaryBalance);
            }

            MSCC unaccountedQuota = null;
            if (response.getUnAccountedQuota() != null) {
                unaccountedQuota = response.getUnAccountedQuota().remove(quotaReservationEntry.getRatingGroup());
            }


            RnCReportedQuotaProcessor reportHandler = factory.createReportHandler(reportedQuota,
                    request,
                    quotaReservationEntry,
                    unaccountedQuota,
                    rnCNonMonetaryBalance,
                    monetaryBalance,
                    executionContext,
                    getOrCreateCopyForAbmf(abmfCopyOfSubscriberRnCNonMonetaryBalance, rnCNonMonetaryBalance),
                    monetaryBalanceForABMF);

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

        boolean isRadiusSession = SessionTypeConstant.RADIUS.val.equals(request.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val));
        boolean isRoSession = SessionTypeConstant.RO.val.equals(request.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val));

		if(isRadiusSession==false && isRoSession==false){
			return false;
		}

		boolean isEventRequest = PCRFKeyValueConstants.REQUEST_TYPE_EVENT_REQUEST.val.equals(request.getAttribute(PCRFKeyConstants.REQUEST_TYPE.val));
		if (isEventRequest) {
			return false;
		}

		boolean isRadiusDataServiceRequest = isRadiusSession && PCRFKeyValueConstants.DATA_SERVICE_ID.val.equals(request.getAttribute(PCRFKeyConstants.CS_SERVICE.val));

        if(isRoSession==false && isRadiusDataServiceRequest) {
            return false;
        }

        if(request.getPCRFEvents().contains(PCRFEvent.SESSION_STOP)){
            return true;
        }else {
            return CollectionUtils.isNotEmpty(request.getReportedMSCCs());
        }
    }

    @Override
    public String getName() {
        return "RnC-REPORT-HANDLER";
    }

    private SubscriberMonetaryBalance getCurrentMonetaryBalance(ExecutionContext executionContext, PCRFResponse response) throws OperationFailedException {
        
    	SubscriberMonetaryBalance currentMonetaryBalance = executionContext.getCurrentMonetaryBalance();
        SubscriberMonetaryBalance newBalance = currentMonetaryBalance.copy();

        response.setCurrentMonetaryBalance(newBalance);
        response.setPreviousMonetaryBalance(currentMonetaryBalance);

        return newBalance;
    }

    private SubscriberRnCNonMonetaryBalance getCurrentRnCNonMonetaryBalance(ExecutionContext executionContext, PCRFResponse response) {
        SubscriberRnCNonMonetaryBalance currentNonMonetaryBalance;
        try {
            currentNonMonetaryBalance = executionContext.getCurrentRnCNonMonetaryBalance();
        } catch (OperationFailedException e) {
            if (getLogger().isWarnLogLevel()) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Unable to deduct reported usage. Reason: " + e.getMessage());
                }
                getLogger().trace(MODULE, e);
            }
            return null;
        }

        SubscriberRnCNonMonetaryBalance newBalance = currentNonMonetaryBalance.copy();

        response.setCurrentRnCNonMonetaryBalance(newBalance);
        response.setPreviousRnCNonMonetaryBalance(currentNonMonetaryBalance);

        return newBalance;
    }

    private MonetaryBalance getBalance(MSCC entry, SubscriberMonetaryBalance newBalance) {

        if(isNull(newBalance)){
            return null;
        }

        GyServiceUnits serviceUnits = entry.getGrantedServiceUnits();
        return newBalance.getBalanceById(serviceUnits.getMonetaryBalanceId());
    }

    private RnCNonMonetaryBalance getBalance(MSCC entry, SubscriberRnCNonMonetaryBalance newBalance) {

        if(isNull(newBalance)){
            return null;
        }

        GyServiceUnits serviceUnits = entry.getGrantedServiceUnits();
        return newBalance.getBalanceById(serviceUnits.getBalanceId());
    }
}
