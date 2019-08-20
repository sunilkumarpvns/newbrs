package com.elitecore.nvsmx.ws.interceptor;

import com.elitecore.nvsmx.ws.packagemanagement.request.ClonePackageRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.CloneProductOfferRequest;
import com.elitecore.nvsmx.ws.resetusage.request.ResetBillingCycleWSRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionQueryByIPRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionQueryRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionReAuthByCoreSessionIdRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionReAuthBySubscriberIdRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddAlternateIdWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddBulkSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddSubscriberProfileBulkWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddSubscriberProfileWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.ChangeBaseProductOfferWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.ChangeImsPackageWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.DeleteSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.DeleteSubscribersWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.GetAlternateIdWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.GetSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.ListSubscribersWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.MigrateSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.PurgeSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.PurgeSubscribersWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.RestoreSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.RestoreSubscribersWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.SubscriberProvisioningWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.UpdateAlternateIdWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.UpdateSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.BalanceEnquiryRequest;
import com.elitecore.nvsmx.ws.subscription.request.BoDQueryRequest;
import com.elitecore.nvsmx.ws.subscription.request.ChangeBillDayWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.ChangeDataAddOnSubscriptionWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.ChangeRnCAddOnProductOfferWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.ChangeTopUpSubscriptionWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.FnFOperationRequest;
import com.elitecore.nvsmx.ws.subscription.request.ListPackageRequest;
import com.elitecore.nvsmx.ws.subscription.request.ListPackagesRequest;
import com.elitecore.nvsmx.ws.subscription.request.ListProductOfferRequest;
import com.elitecore.nvsmx.ws.subscription.request.ListSubscriptionWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.ListUsageMonitoringInformationWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.MonetaryBalanceEnquiryRequest;
import com.elitecore.nvsmx.ws.subscription.request.MonetaryOperationRequest;
import com.elitecore.nvsmx.ws.subscription.request.NonMonitoryBalanceEnquiryRequest;
import com.elitecore.nvsmx.ws.subscription.request.RnCBalanceEnquiryRequest;
import com.elitecore.nvsmx.ws.subscription.request.SubscribeAddOnWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.SubscribeBodWsRequest;
import com.elitecore.nvsmx.ws.subscription.request.SubscribeMonetaryRechargePlanWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.SubscribeTopUpWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.TopUpQueryRequest;
import com.elitecore.nvsmx.ws.subscription.request.UpdateCreditLimitWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.UpdateMonetaryBalanceRequest;
import org.apache.commons.lang.StringUtils;

import static com.elitecore.commons.base.Strings.isNullOrBlank;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.ThreadContext.put;

/**
 * @author Prakash Pala
 * @since 28-Dec-2018
 * DiagnosticContextInjector is used for adding important operation specific parameters in ThreadContext (MDC).
 * It is used in cxf rest flow.
 */
public class DiagnosticContextInjector {
    private static final String WEB_SERVICE_NAME = "WebServiceName";
    private static final String WEB_METHOD_NAME = "WebMethodName";
    private static final String SUBSCRIBER_ID = "SubscriberID";
    private static final String ALTERNATE_ID = "AlternateID";
    private static final String SERVICE_ID = "ServiceID";
    private static final String SERVICE_NAME = "ServiceName";
    private static final String TOTAL_BALANCE = "TotalBalance";
    private static final String PACKAGE_ID = "PackageID";
    private static final String PACKAGE_NAME = "PackageName";
    private static final String PACKAGE_TYPE = "PackageType";
    private static final String TYPE = "Type";
    private static final String MODE = "Mode";
    private static final String NEXT_BILL_DAY = "NextBillDay";
    private static final String SUBSCRIPTION_STATUS_VALUE = "SubscriptionStatusValue";
    private static final String SUBSCRIPTION_STATUS_NAME = "SubscriptionStatusName";
    private static final String PRODUCT_OFFER_ID = "ProductOfferID";
    private static final String SUBSCRIPTION_ID = "SubscriptionID";
    private static final String PRODUCT_OFFER_NAME = "ProductOfferName";
    private static final String AMOUNT = "Amount";
    private static final String CREDIT_LIMIT = "CreditLimit";
    private static final String APPLICABLE_BILLING_CYCLE = "ApplicableBilingCycle";
    private static final String BOD_ID = "BoDID";
    private static final String BOD_NAME = "BoDName";
    private static final String CURRENT_PACKAGE_NAME = "CurrentPackageName";
    private static final String NEW_PACKAGE_NAME = "NewPackageName";
    private static final String CURRENT_SUBSCRIBER_ID = "CurrentSubscriberIdentity";
    private static final String NEW_SUBSCRIBER_ID = "NewSubscriberIdentity";
    private static final String CURRENT_PRODUCT_OFFER_NAME = "CurrentProductOfferName";
    private static final String NEW_PRODUCT_OFFER_NAME = "NewProductOfferName";
    private static final String NEW_ALTERNATE_ID = "NewAlternateID";
    private static final String SESSION_IP = "SessionIP";
    private static final String SESSION_TYPE = "SessionType";
    private static final String CORE_SESSION_ID = "CoreSessionID";
    private static final String RESET_BILLING_CYCLE_DATE = "ResetBillingCycleDate";
    private static final String STATUS = "Status";
    private static final String PACKAGE_TO_BE_CLONED = "PackageName";
    private static final String CLONED_PACKAGE_NAME = "ClonedPackageName" ;
    private static final String PRODUCT_OFFER_TO_BE_CLONED = "ProductOfferName";
    private static final String CLONED_PRODUCT_OFFER_NAME = "ClonedProductOfferName" ;
    private static final String FNF_GROUP_NAME = "GroupName";
    private static final String FNF_MEMBERS = "Members";
    private static final String FNF_OPERATION = "Operation";

    //--------- Subscription ---------

    public void add(MonetaryOperationRequest monetaryOperationRequest) {
        putBasicWebServiceInfo(monetaryOperationRequest);
        putSubscriberAndAlternateID(monetaryOperationRequest.getSubscriberId()
                , monetaryOperationRequest.getAlternateId());
        putServiceIDAndName(monetaryOperationRequest.getServiceId()
                , monetaryOperationRequest.getServiceName());

        if(isNull(monetaryOperationRequest.getTotalBalance()) == false){
            put(TOTAL_BALANCE, String.valueOf(monetaryOperationRequest.getTotalBalance()));
        }
    }

    public void add(ListPackagesRequest listPackagesRequest){
        putBasicWebServiceInfo(listPackagesRequest);
        if(isNullOrBlank(listPackagesRequest.getPackageId()) == false){
            put(PACKAGE_ID, listPackagesRequest.getPackageId());
        }
        if(isNullOrBlank(listPackagesRequest.getPackageName()) == false){
            put(PACKAGE_NAME, listPackagesRequest.getPackageName());
        }
        if(isNullOrBlank(listPackagesRequest.getPackageType()) == false){
            put(PACKAGE_TYPE, listPackagesRequest.getPackageType());
        }
        if(isNullOrBlank(listPackagesRequest.getType()) == false){
            put(TYPE, listPackagesRequest.getType());
        }
        if(isNullOrBlank(listPackagesRequest.getMode()) == false){
            put(MODE, listPackagesRequest.getMode());
        }
    }

    public void add(ChangeBillDayWSRequest changeBillDayWSRequest) {
        putBasicWebServiceInfo(changeBillDayWSRequest);
        putSubscriberAndAlternateID(changeBillDayWSRequest.getSubscriberId()
                , changeBillDayWSRequest.getAlternateId());

        if(isNull(changeBillDayWSRequest.getNextBillDate()) == false){
            put(NEXT_BILL_DAY, String.valueOf(changeBillDayWSRequest.getNextBillDate()));
        }
    }

    public void add(ListSubscriptionWSRequest listSubscriptionWSRequest) {
        putBasicWebServiceInfo(listSubscriptionWSRequest);
        putSubscriberAndAlternateID(listSubscriptionWSRequest.getSubscriberId()
                , listSubscriptionWSRequest.getAlternateId());
        putSubscriptionStatusValueAndName(listSubscriptionWSRequest.getSubscriptionStatusValue()
                , listSubscriptionWSRequest.getSubscriptionStatusName());
    }

    public void add(ListProductOfferRequest listProductOfferRequest) {
        putBasicWebServiceInfo(listProductOfferRequest);
    }

    public void add(TopUpQueryRequest topUpQueryRequest) {
        putBasicWebServiceInfo(topUpQueryRequest);
        putSubscriberAndAlternateID(topUpQueryRequest.getSubscriberId()
                , topUpQueryRequest.getAlternateId());

        if(isNullOrBlank(topUpQueryRequest.getPackageName()) == false){
            put(PACKAGE_NAME, topUpQueryRequest.getPackageName());
        }
    }

    public void add(BoDQueryRequest bodQueryRequest) {
        putBasicWebServiceInfo(bodQueryRequest);
        putSubscriberAndAlternateID(bodQueryRequest.getSubscriberId()
                , bodQueryRequest.getAlternateId());

        if(isNullOrBlank(bodQueryRequest.getOfferName()) == false){
            put(PRODUCT_OFFER_NAME, bodQueryRequest.getOfferName());
        }
    }

    public void add(SubscribeAddOnWSRequest subscribeAddOnWSRequest) {
        putBasicWebServiceInfo(subscribeAddOnWSRequest);
        putSubscriberAndAlternateID(subscribeAddOnWSRequest.getSubscriberId()
                , subscribeAddOnWSRequest.getAlternateId());
        putSubscriptionStatusValueAndName(subscribeAddOnWSRequest.getSubscriptionStatusValue()
                , subscribeAddOnWSRequest.getSubscriptionStatusName());

        if(isNullOrBlank(subscribeAddOnWSRequest.getAddOnProductOfferId()) == false){
            put(PRODUCT_OFFER_ID, subscribeAddOnWSRequest.getAddOnProductOfferId());
        }
        if(isNullOrBlank(subscribeAddOnWSRequest.getAddOnPackageName()) == false){
            put(PACKAGE_NAME, subscribeAddOnWSRequest.getAddOnPackageName());
        }
    }

    public void add(SubscribeTopUpWSRequest subscribeTopUpWSRequest) {
        putBasicWebServiceInfo(subscribeTopUpWSRequest);
        putSubscriberAndAlternateID(subscribeTopUpWSRequest.getSubscriberId()
                , subscribeTopUpWSRequest.getAlternateId());
        putSubscriptionStatusValueAndName(subscribeTopUpWSRequest.getSubscriptionStatusValue()
                , subscribeTopUpWSRequest.getSubscriptionStatusName());

        if(isNullOrBlank(subscribeTopUpWSRequest.getTopUpPackageId()) == false){
            put(PACKAGE_ID, subscribeTopUpWSRequest.getTopUpPackageId());
        }
        if(isNullOrBlank(subscribeTopUpWSRequest.getTopUpPackageName()) == false){
            put(PACKAGE_NAME, subscribeTopUpWSRequest.getTopUpPackageName());
        }
    }

    public void add(ChangeDataAddOnSubscriptionWSRequest changeDataAddOnSubscriptionWSRequest) {
        putBasicWebServiceInfo(changeDataAddOnSubscriptionWSRequest);
        putSubscriberAndAlternateID(changeDataAddOnSubscriptionWSRequest.getSubscriberId()
                , changeDataAddOnSubscriptionWSRequest.getAlternateId());
        putSubscriptionStatusValueAndName(changeDataAddOnSubscriptionWSRequest.getSubscriptionStatusValue()
                , changeDataAddOnSubscriptionWSRequest.getSubscriptionStatusName());

        if(isNullOrBlank(changeDataAddOnSubscriptionWSRequest.getDataAddOnSubscriptionId()) == false){
            put(SUBSCRIPTION_ID, changeDataAddOnSubscriptionWSRequest.getDataAddOnSubscriptionId());
        }
        if(isNullOrBlank(changeDataAddOnSubscriptionWSRequest.getDataAddOnProductOfferName()) == false){
            put(PRODUCT_OFFER_NAME, changeDataAddOnSubscriptionWSRequest.getDataAddOnProductOfferName());
        }
    }

    public void add(ChangeTopUpSubscriptionWSRequest changeTopUpSubscriptionWSRequest) {
        putBasicWebServiceInfo(changeTopUpSubscriptionWSRequest);
        putSubscriberAndAlternateID(changeTopUpSubscriptionWSRequest.getSubscriberId()
                , changeTopUpSubscriptionWSRequest.getAlternateId());
        putSubscriptionStatusValueAndName(changeTopUpSubscriptionWSRequest.getSubscriptionStatusValue()
                , changeTopUpSubscriptionWSRequest.getSubscriptionStatusName());

        if(isNullOrBlank(changeTopUpSubscriptionWSRequest.getTopUpSubscriptionId()) == false){
            put(SUBSCRIPTION_ID, changeTopUpSubscriptionWSRequest.getTopUpSubscriptionId());
        }
        if(isNullOrBlank(changeTopUpSubscriptionWSRequest.getTopUpName()) == false){
            put(PACKAGE_NAME, changeTopUpSubscriptionWSRequest.getTopUpName());
        }
    }

    public void add(UpdateMonetaryBalanceRequest updateMonetaryBalanceRequest) {
        putBasicWebServiceInfo(updateMonetaryBalanceRequest);
        putSubscriberAndAlternateID(updateMonetaryBalanceRequest.getSubscriberId()
                , updateMonetaryBalanceRequest.getAlternateId());
        putServiceIDAndName(updateMonetaryBalanceRequest.getServiceId()
                , updateMonetaryBalanceRequest.getServiceName());

        if(isNull(updateMonetaryBalanceRequest.getAmount()) == false){
            put(AMOUNT, String.valueOf(updateMonetaryBalanceRequest.getAmount()));
        }
    }

    public void add(ListUsageMonitoringInformationWSRequest listUsageMonitoringInformationWSRequest) {
        putBasicWebServiceInfo(listUsageMonitoringInformationWSRequest);
        putSubscriberAndAlternateID(listUsageMonitoringInformationWSRequest.getSubscriberId()
                , listUsageMonitoringInformationWSRequest.getAlternateId());
        putServiceIDAndName(listUsageMonitoringInformationWSRequest.getServiceId()
                , listUsageMonitoringInformationWSRequest.getServiceName());
    }

    public void add(BalanceEnquiryRequest balanceEnquiryRequest) {
        putBasicWebServiceInfo(balanceEnquiryRequest);
        putSubscriberAndAlternateID(balanceEnquiryRequest.getSubscriberId()
                , balanceEnquiryRequest.getAlternateId());
    }

    public void add(NonMonitoryBalanceEnquiryRequest nonMonitoryBalanceEnquiryRequest) {
        putBasicWebServiceInfo(nonMonitoryBalanceEnquiryRequest);
        putSubscriberAndAlternateID(nonMonitoryBalanceEnquiryRequest.getSubscriberId()
                , nonMonitoryBalanceEnquiryRequest.getAlternateId());
    }

    public void add(RnCBalanceEnquiryRequest rnCBalanceEnquiryRequest) {
        putBasicWebServiceInfo(rnCBalanceEnquiryRequest);
        putSubscriberAndAlternateID(rnCBalanceEnquiryRequest.getSubscriberId()
                , rnCBalanceEnquiryRequest.getAlternateId());
    }

    public void add(MonetaryBalanceEnquiryRequest monetaryBalanceEnquiryRequest) {
        putBasicWebServiceInfo(monetaryBalanceEnquiryRequest);
        putSubscriberAndAlternateID(monetaryBalanceEnquiryRequest.getSubscriberId()
                , monetaryBalanceEnquiryRequest.getAlternateId());
    }

    public void add(ChangeRnCAddOnProductOfferWSRequest changeRnCAddOnProductOfferWSRequest) {
        putBasicWebServiceInfo(changeRnCAddOnProductOfferWSRequest);
        putSubscriberAndAlternateID(changeRnCAddOnProductOfferWSRequest.getSubscriberId()
                , changeRnCAddOnProductOfferWSRequest.getAlternateId());
        putSubscriptionStatusValueAndName(changeRnCAddOnProductOfferWSRequest.getSubscriptionStatusValue()
                , changeRnCAddOnProductOfferWSRequest.getSubscriptionStatusName());

        if(isNullOrBlank(changeRnCAddOnProductOfferWSRequest.getRncAddOnSubscriptionId()) == false){
            put(SUBSCRIPTION_ID, changeRnCAddOnProductOfferWSRequest.getRncAddOnSubscriptionId());
        }
        if(isNullOrBlank(changeRnCAddOnProductOfferWSRequest.getRncAddOnProductOfferName()) == false){
            put(PRODUCT_OFFER_NAME, changeRnCAddOnProductOfferWSRequest.getRncAddOnProductOfferName());
        }
    }

    public void add(UpdateCreditLimitWSRequest updateCreditLimitWSRequest) {
        putBasicWebServiceInfo(updateCreditLimitWSRequest);
        putSubscriberAndAlternateID(updateCreditLimitWSRequest.getSubscriberId()
                , updateCreditLimitWSRequest.getAlternateId());

        if(isNullOrBlank(updateCreditLimitWSRequest.getCreditLimit()) == false){
            put(CREDIT_LIMIT, updateCreditLimitWSRequest.getCreditLimit());
        }
        if(isNullOrBlank(updateCreditLimitWSRequest.getApplicableBillingCycle()) == false){
            put(APPLICABLE_BILLING_CYCLE, updateCreditLimitWSRequest.getApplicableBillingCycle());
        }
    }

    public void add(SubscribeMonetaryRechargePlanWSRequest subscribeMonetaryRechargePlanWSRequest) {
        putBasicWebServiceInfo(subscribeMonetaryRechargePlanWSRequest);
        putSubscriberAndAlternateID(subscribeMonetaryRechargePlanWSRequest.getSubscriberId()
                , subscribeMonetaryRechargePlanWSRequest.getAlternateId());

        if(isNullOrBlank(subscribeMonetaryRechargePlanWSRequest.getMonetaryRechargePlanId()) == false){
            put(PACKAGE_ID, subscribeMonetaryRechargePlanWSRequest.getMonetaryRechargePlanId());
        }
        if(isNullOrBlank(subscribeMonetaryRechargePlanWSRequest.getMonetaryRechargePlanName()) == false){
            put(PACKAGE_NAME, subscribeMonetaryRechargePlanWSRequest.getMonetaryRechargePlanName());
        }
    }

    public void add(SubscribeBodWsRequest subscribeBodWsRequest) {
        putBasicWebServiceInfo(subscribeBodWsRequest);
        putSubscriberAndAlternateID(subscribeBodWsRequest.getSubscriberId()
                , subscribeBodWsRequest.getAlternateId());
        putSubscriptionStatusValueAndName(subscribeBodWsRequest.getSubscriptionStatusValue()
                , subscribeBodWsRequest.getSubscriptionStatusName());

        if(isNullOrBlank(subscribeBodWsRequest.getBodId()) == false){
            put(BOD_ID, subscribeBodWsRequest.getBodId());
        }
        if(isNullOrBlank(subscribeBodWsRequest.getBodName()) == false){
            put(BOD_NAME, subscribeBodWsRequest.getBodName());
        }
    }

    public void add(ListPackageRequest listPackageRequest) {
        putBasicWebServiceInfo(listPackageRequest);

        if(isNullOrBlank(listPackageRequest.getPackageId()) == false){
            put(PACKAGE_ID, listPackageRequest.getPackageId());
        }
        if(isNullOrBlank(listPackageRequest.getPackageName()) == false){
            put(PACKAGE_NAME, listPackageRequest.getPackageName());
        }
        if(isNullOrBlank(listPackageRequest.getPackageType()) == false){
            put(PACKAGE_TYPE, listPackageRequest.getPackageType());
        }
    }

    public void add(FnFOperationRequest fnFOperationRequest) {
        putBasicWebServiceInfo(fnFOperationRequest);
        putSubscriberAndAlternateID(fnFOperationRequest.getSubscriberId(), fnFOperationRequest.getAlternateId());
        if (isNullOrBlank(fnFOperationRequest.getGroupName()) == false) {
            put(FNF_GROUP_NAME, fnFOperationRequest.getGroupName());
        }
        if (nonNull(fnFOperationRequest.getMembers())) {
            put(FNF_MEMBERS, fnFOperationRequest.getMembers().toString());
        }
        if (nonNull(fnFOperationRequest.getOperation())) {
            put(FNF_OPERATION, fnFOperationRequest.getOperation().name());
        }
    }

    //--------- Subscriber Provisioing ---------

    public void add(SubscriberProvisioningWSRequest subscriberProvisioningWSRequest) {
        putBasicWebServiceInfo(subscriberProvisioningWSRequest);
    }

    public void add(AddSubscriberWSRequest addSubscriberWSRequest) {
        putBasicWebServiceInfo(addSubscriberWSRequest);
    }

    public void add(AddSubscriberProfileWSRequest addSubscriberProfileWSRequest) {
        putBasicWebServiceInfo(addSubscriberProfileWSRequest);
    }

    public void add(AddSubscriberProfileBulkWSRequest addSubscriberProfileBulkWSRequest) {
        putBasicWebServiceInfo(addSubscriberProfileBulkWSRequest);
    }

    public void add(AddBulkSubscriberWSRequest addBulkSubscriberWSRequest) {
        putBasicWebServiceInfo(addBulkSubscriberWSRequest);
    }

    public void add(UpdateSubscriberWSRequest updateSubscriberWSRequest) {
        putBasicWebServiceInfo(updateSubscriberWSRequest);
        putSubscriberAndAlternateID(updateSubscriberWSRequest.getSubscriberID()
                , updateSubscriberWSRequest.getAlternateId());
    }

    public void add(RestoreSubscriberWSRequest restoreSubscriberWSRequest) {
        putBasicWebServiceInfo(restoreSubscriberWSRequest);
        putSubscriberAndAlternateID(restoreSubscriberWSRequest.getSubscriberId()
                , restoreSubscriberWSRequest.getAlternateId());
    }

    public void add(RestoreSubscribersWSRequest restoreSubscribersWSRequest) {
        putBasicWebServiceInfo(restoreSubscribersWSRequest);
    }

    public void add(PurgeSubscriberWSRequest purgeSubscriberWSRequest) {
        putBasicWebServiceInfo(purgeSubscriberWSRequest);
        putSubscriberAndAlternateID(purgeSubscriberWSRequest.getSubscriberId()
                , purgeSubscriberWSRequest.getAlternateId());
    }

    public void add(PurgeSubscribersWSRequest purgeSubscribersWSRequest) {
        putBasicWebServiceInfo(purgeSubscribersWSRequest);
    }

    public void add(ListSubscribersWSRequest listSubscribersWSRequest) {
        putBasicWebServiceInfo(listSubscribersWSRequest);
    }

    public void add(GetSubscriberWSRequest getSubscriberWSRequest) {
        putBasicWebServiceInfo(getSubscriberWSRequest);
        putSubscriberAndAlternateID(getSubscriberWSRequest.getSubscriberId()
                , getSubscriberWSRequest.getAlternateId());
    }

    public void add(DeleteSubscriberWSRequest deleteSubscriberWSRequest) {
        putBasicWebServiceInfo(deleteSubscriberWSRequest);
        putSubscriberAndAlternateID(deleteSubscriberWSRequest.getSubscriberID()
                , deleteSubscriberWSRequest.getAlternateId());
    }

    public void add(DeleteSubscribersWSRequest deleteSubscribersWSRequest) {
        putBasicWebServiceInfo(deleteSubscribersWSRequest);
    }



    public void add(ChangeImsPackageWSRequest changeImsPackageWSRequest) {
        putBasicWebServiceInfo(changeImsPackageWSRequest);
        putSubscriberAndAlternateID(changeImsPackageWSRequest.getSubscriberID()
                , changeImsPackageWSRequest.getAlternateId());

        if(isNullOrBlank(changeImsPackageWSRequest.getCurrentPackageName()) == false){
            put(CURRENT_PACKAGE_NAME, changeImsPackageWSRequest.getCurrentPackageName());
        }
        if(isNullOrBlank(changeImsPackageWSRequest.getNewPackageName()) == false){
            put(NEW_PACKAGE_NAME, changeImsPackageWSRequest.getNewPackageName());
        }
    }

    public void add(MigrateSubscriberWSRequest migrateSubscriberWSRequest) {
        putBasicWebServiceInfo(migrateSubscriberWSRequest);

        if(isNullOrBlank(migrateSubscriberWSRequest.getCurrentSubscriberIdentity()) == false){
            put(CURRENT_SUBSCRIBER_ID, migrateSubscriberWSRequest.getCurrentSubscriberIdentity());
        }
        if(isNullOrBlank(migrateSubscriberWSRequest.getNewSubscriberIdentity()) == false){
            put(NEW_SUBSCRIBER_ID, migrateSubscriberWSRequest.getNewSubscriberIdentity());
        }
    }

    public void add(ChangeBaseProductOfferWSRequest changeBaseProductOfferWSRequest) {
        putBasicWebServiceInfo(changeBaseProductOfferWSRequest);
        putSubscriberAndAlternateID(changeBaseProductOfferWSRequest.getSubscriberID()
                , changeBaseProductOfferWSRequest.getAlternateId());

        if(isNullOrBlank(changeBaseProductOfferWSRequest.getCurrentProductOfferName()) == false){
            put(CURRENT_PRODUCT_OFFER_NAME, changeBaseProductOfferWSRequest.getCurrentProductOfferName());
        }
        if(isNullOrBlank(changeBaseProductOfferWSRequest.getNewProductOfferName()) == false){
            put(NEW_PRODUCT_OFFER_NAME, changeBaseProductOfferWSRequest.getNewProductOfferName());
        }
    }

    public void add(AddAlternateIdWSRequest addAlternateIdWSRequest) {
        putBasicWebServiceInfo(addAlternateIdWSRequest);
        putSubscriberAndAlternateID(addAlternateIdWSRequest.getSubscriberId()
                , addAlternateIdWSRequest.getAlternateId());
    }

    public void add(UpdateAlternateIdWSRequest updateAlternateIdWSRequest) {
        putBasicWebServiceInfo(updateAlternateIdWSRequest);
        putSubscriberAndAlternateID(updateAlternateIdWSRequest.getSubscriberId()
                , updateAlternateIdWSRequest.getCurrentAlternateId());

        if(isNullOrBlank(updateAlternateIdWSRequest.getNewAlternateId()) == false){
            put(NEW_ALTERNATE_ID, updateAlternateIdWSRequest.getNewAlternateId());
        }
        if(isNullOrBlank(updateAlternateIdWSRequest.getStatus()) == false){
            put(STATUS, updateAlternateIdWSRequest.getStatus());
        }
    }

    public void add(GetAlternateIdWSRequest getAlternateIdWSRequest) {
        putBasicWebServiceInfo(getAlternateIdWSRequest);
        putSubscriberAndAlternateID(getAlternateIdWSRequest.getSubscriberId(), null);
    }

    //--------- Session Management ---------

    public void add(SessionQueryByIPRequest sessionQueryByIPRequest) {
        putBasicWebServiceInfo(sessionQueryByIPRequest);
        if(isNullOrBlank(sessionQueryByIPRequest.getSessionIP()) == false){
            put(SESSION_IP, sessionQueryByIPRequest.getSessionIP());
        }
        if(isNullOrBlank(sessionQueryByIPRequest.getSessionType()) == false){
            put(SESSION_TYPE, sessionQueryByIPRequest.getSessionType());
        }
    }

    public void add(SessionReAuthByCoreSessionIdRequest sessionReAuthByCoreSessionIdRequest) {
        putBasicWebServiceInfo(sessionReAuthByCoreSessionIdRequest);
        if(isNullOrBlank(sessionReAuthByCoreSessionIdRequest.getCoreSessionId()) == false){
            put(CORE_SESSION_ID, sessionReAuthByCoreSessionIdRequest.getCoreSessionId());
        }
    }

    public void add(SessionQueryRequest sessionQueryRequest) {
        putBasicWebServiceInfo(sessionQueryRequest);
        putSubscriberAndAlternateID(sessionQueryRequest.getSubscriberId()
                , sessionQueryRequest.getAlternateId());

        if(isNullOrBlank(sessionQueryRequest.getSessionType()) == false){
            put(SESSION_TYPE, sessionQueryRequest.getSessionType());
        }
    }

    public void add(SessionReAuthBySubscriberIdRequest sessionReAuthBySubscriberIdRequest) {
        putBasicWebServiceInfo(sessionReAuthBySubscriberIdRequest);
        putSubscriberAndAlternateID(sessionReAuthBySubscriberIdRequest.getSubscriberId()
                , sessionReAuthBySubscriberIdRequest.getAlternateId());
    }

    //--------- Usage ---------

    public void add(ResetBillingCycleWSRequest resetBillingCycleWSRequest) {
        putBasicWebServiceInfo(resetBillingCycleWSRequest);
        putSubscriberAndAlternateID(resetBillingCycleWSRequest.getSubscriberID()
                , resetBillingCycleWSRequest.getAlternateID());

        if(isNullOrBlank(resetBillingCycleWSRequest.getProductOfferName()) == false){
            put(PRODUCT_OFFER_NAME, resetBillingCycleWSRequest.getProductOfferName());
        }
        if(isNull(resetBillingCycleWSRequest.getResetBillingCycleDate()) == false){
            put(RESET_BILLING_CYCLE_DATE, String.valueOf(resetBillingCycleWSRequest.getResetBillingCycleDate()));
        }
    }


    public void add(ClonePackageRequest request) {
        putBasicWebServiceInfo(request);
        if(StringUtils.isNotBlank(request.getName())){
            put(PACKAGE_TO_BE_CLONED,request.getName());
        }
        if(StringUtils.isNotBlank(request.getNewName())){
            put(CLONED_PACKAGE_NAME,request.getNewName());
        }
    }

    public void add(CloneProductOfferRequest request) {
        putBasicWebServiceInfo(request);
        if(StringUtils.isNotBlank(request.getName())){
            put(PRODUCT_OFFER_TO_BE_CLONED,request.getName());
        }
        if(StringUtils.isNotBlank(request.getNewName())){
            put(CLONED_PRODUCT_OFFER_NAME,request.getNewName());
        }
    }

    //--------- Common Methods ---------
    private void putBasicWebServiceInfo(WebServiceRequest request) {
        if(isNullOrBlank(request.getWebServiceName()) == false){
            put(WEB_SERVICE_NAME, request.getWebServiceName());
        }
        if(isNullOrBlank(request.getWebServiceMethodName()) == false){
            put(WEB_METHOD_NAME, request.getWebServiceMethodName());
        }
    }

    private void putSubscriberAndAlternateID(String subscriberID, String alternateID){
        if(isNullOrBlank(subscriberID) == false){
            put(SUBSCRIBER_ID, subscriberID);
        }
        if(isNullOrBlank(alternateID) == false){
            put(ALTERNATE_ID, alternateID);
        }
    }

    private void putSubscriptionStatusValueAndName(String statusValue, String statusName){
        if(isNullOrBlank(statusValue) == false){
            put(SUBSCRIPTION_STATUS_VALUE, statusValue);
        }
        if(isNullOrBlank(statusName) == false){
            put(SUBSCRIPTION_STATUS_NAME, statusName);
        }
    }

    private void putServiceIDAndName(String serviceID, String serviceName){
        if(isNullOrBlank(serviceID) == false){
            put(SERVICE_ID, serviceID);
        }
        if(isNullOrBlank(serviceName) == false){
            put(SERVICE_NAME, serviceName);
        }
    }



}
