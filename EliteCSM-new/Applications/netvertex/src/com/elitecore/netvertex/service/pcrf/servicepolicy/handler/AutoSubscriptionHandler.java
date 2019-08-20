package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.offer.ProductOfferAutoSubscription;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriptionNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriptionRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionParameter;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionResult;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.UnauthorizedActionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.servicepolicy.handler.ServiceHandler;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.util.PCRFValueProvider;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.elitecore.corenetvertex.spr.ddf.SubscriptionParameter.create;

public class AutoSubscriptionHandler extends ServiceHandler {
    private static final String MODULE = "AUTO-SUBSCRIPTION-HANDLER";

    public AutoSubscriptionHandler(PCRFServiceContext serviceContext) {
        super(serviceContext);
    }

    @Override
    protected void process(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {


        PCRFRequest pcrfRequest = (PCRFRequest) serviceRequest;
        PCRFResponse pcrfResponse = (PCRFResponse) serviceResponse;


        String subscriberIdentity = pcrfRequest.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
        ProductOffer productOffer = getProductOffer(pcrfRequest);

        if(Objects.isNull(productOffer)) {
            return;
        }

        List<ProductOfferAutoSubscription> autoSubscriptions = productOffer.getProductOfferAutoSubscriptions();

        if(CollectionUtils.isEmpty(autoSubscriptions)) {
            if(getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping auto subscription for subscriber: "+ subscriberIdentity +". Reason: No auto subscription attached with base product offer:" + productOffer.getName());
            }
            return;
        }

        LinkedHashMap<String, Subscription> subscriptions;
        try {
            subscriptions = executionContext.getSubscriptions();
        } catch (OperationFailedException e) {
            getLogger().error(MODULE, "Skipping Auto Subscription for subscriber: "+ subscriberIdentity +". Reason:" + e.getMessage());
            getLogger().trace(MODULE, e);
            return;
        }

        Set<String> subscribedProductOfferIds = new HashSet<>();

        subscriptions.values().forEach(subscription -> subscribedProductOfferIds.add(subscription.getProductOfferId()));

        ValueProvider valueProvider = new PCRFValueProvider(pcrfRequest, pcrfResponse);

        for (ProductOfferAutoSubscription autoSubscription : autoSubscriptions) {
            boolean applied  = subscribed(executionContext, pcrfRequest, pcrfResponse, subscribedProductOfferIds, valueProvider, autoSubscription);

            if(applied) {
                break;
            }
        }

    }

    private boolean subscribed(ExecutionContext executionContext,
                               PCRFRequest pcrfRequest, PCRFResponse pcrfResponse,
                               Set<String> subscribedProductOfferIds,
                               ValueProvider valueProvider,
                               ProductOfferAutoSubscription autoSubscription) {

        ProductOffer autoSubscriptionProductOffer = autoSubscription.getAddOnProductOfferData();
        if(Objects.isNull(autoSubscriptionProductOffer)){
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Skipping auto subscription of product offer:" + autoSubscription.getAddOnProductOfferName()+". Reason: AddOn product offer not found in policy repository");
            }
            return false;
        }

        if(autoSubscriptionProductOffer.getPolicyStatus() != PolicyStatus.SUCCESS) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Skipping auto subscription of product offer:"+ autoSubscription.getAddOnProductOfferName()+". Reason: product offer is not in success state");
            }

            return false;
        }

        LogicalExpression advanceCondition = autoSubscription.getAdvancedCondition();
        if(Objects.nonNull(advanceCondition) && advanceCondition.evaluate(valueProvider) == false) {
            if(getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping auto subscription of product offer:"+ autoSubscription.getAddOnProductOfferName()+". Reason: Advance Condition:" +  autoSubscription.getAdvanceConditonStr() + " not satisfied");
            }
            return false;
        }

        if(subscribedProductOfferIds.contains(autoSubscriptionProductOffer.getId())) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping auto subscription of product offer:" + autoSubscription.getAddOnProductOfferName() + ". Reason: product offer is already subscribed");
            }
            return true;
        }

        MonetaryBalance mainBalance = null;
        if(autoSubscriptionProductOffer.getSubscriptionPrice() > 0d) {
            try {
                SubscriberMonetaryBalance currentMonetaryBalance = executionContext.getCurrentMonetaryBalance();

                mainBalance = currentMonetaryBalance.getMainBalanceIfExist();
                if (Objects.isNull(mainBalance) ) {
                    if(getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Skipping auto subscription of product offer:"+ autoSubscription.getAddOnProductOfferName()+". Reason: No main monetary balance found");
                    }
                    return true;
                }

                if (mainBalance.getUsableBalance() < autoSubscriptionProductOffer.getSubscriptionPrice()) {
                    if(getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Skipping auto subscription of product offer:"+ autoSubscription.getAddOnProductOfferName()+". Reason: No enough monetary balance");
                    }
                    return true;
                }
            } catch (OperationFailedException e) {
                if(getLogger().isErrorLogLevel()) {
                    getLogger().error(MODULE, "Skipping auto subscription of product offer:"+ autoSubscription.getAddOnProductOfferName()+". Reason: " + e.getMessage() );
                }

                getLogger().trace(MODULE, e);
                return true;
            }
        }


        return subscribeProductOffer(executionContext, pcrfRequest, pcrfResponse, autoSubscriptionProductOffer, mainBalance);
    }

    private boolean subscribeProductOffer(ExecutionContext executionContext, PCRFRequest pcrfRequest, PCRFResponse pcrfResponse, ProductOffer autoSubscriptionProductOffer, MonetaryBalance mainBalance) {
        try {
            SubscriptionParameter subscriptionParameter = createSubscriptionParameter(executionContext, autoSubscriptionProductOffer, mainBalance);

            subscriptionParameter.setStartTime(executionContext.getCurrentTime().getTimeInMillis());
            if(getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Applying auto subscription of product offer:"+ autoSubscriptionProductOffer.getName());
            }

            SubscriptionResult subscriptionResult = executionContext.getDDFTable().autoSubscribeAddOnProductOfferById(subscriptionParameter);


            if(getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Auto subscription of product offer:"+ autoSubscriptionProductOffer.getName() +" applied successfully" );
            }


            subscriptionResult.getSubscriptions().forEach( subscription -> {
                try {
                    executionContext.getSubscriptions().put(subscription.getId(), subscription);
                    SessionTypeConstant sessionType = SessionTypeConstant.fromValue(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val));
                    if(sessionType == SessionTypeConstant.RO) {
                        SubscriptionRnCNonMonetaryBalance rnCBalance = subscriptionResult.getRnCBalance(subscription.getId());
                        if(Objects.nonNull(rnCBalance)) {
                            executionContext.getCurrentRnCNonMonetaryBalance().addBalance(rnCBalance);
                        }
                    } else if(sessionType == SessionTypeConstant.GY) {

                        SubscriptionNonMonitoryBalance balance = subscriptionResult.getBalance(subscription.getId());
                        if(Objects.nonNull(balance)) {
                            executionContext.getCurrentNonMonetoryBalance().addBalance(balance);
                        }
                    }
                } catch (OperationFailedException e) {
                    throw new AssertionError("This should not be called" + e.getMessage(), e);
                }
            });

            if(Objects.nonNull(mainBalance)) {
                mainBalance.substract(autoSubscriptionProductOffer.getSubscriptionPrice());
            }

            Template emailNotification = autoSubscriptionProductOffer.getSubscribeEmailNotification();
            Template smsNotification = autoSubscriptionProductOffer.getSubscribeSmsNotification();
            if(emailNotification != null || smsNotification != null){
                if(getLogger().isDebugLogLevel()){
                    getLogger().debug(MODULE,"Sending Notification for add on subscription name: " + autoSubscriptionProductOffer.getName());
                }
                pcrfResponse.setAttribute(PCRFKeyConstants.PKG_NAME.val,autoSubscriptionProductOffer.getName());
                pcrfResponse.setAttribute(PCRFKeyConstants.PKG_PRICE.val, Strings.valueOf(autoSubscriptionProductOffer.getSubscriptionPrice()));
                getServiceContext().getServerContext().sendNotification(emailNotification,smsNotification,pcrfResponse);
            }

            return true;
        } catch (OperationFailedException | UnauthorizedActionException e) {
            if(getLogger().isErrorLogLevel()) {
                getLogger().error(MODULE, "Skipping auto subscription of product offer:"+ autoSubscriptionProductOffer.getName()+". Reason: " + e.getMessage() );
            }
            getLogger().trace(MODULE, e);
        }
        return false;
    }

    private SubscriptionParameter createSubscriptionParameter(ExecutionContext executionContext, ProductOffer productOffer, MonetaryBalance monetaryBalance) throws OperationFailedException {
        SubscriptionParameter subscriptionParameter = create(executionContext.getSPR(), productOffer);
        subscriptionParameter.setMonetaryBalance(monetaryBalance);

        return subscriptionParameter;
    }

    private ProductOffer getProductOffer(PCRFRequest pcrfRequest) {
        String subscriberPackage = pcrfRequest.getSPRInfo().getProductOffer();
        String subscriberId = pcrfRequest.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);

        if (subscriberPackage == null) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Skipping auto subscription subscriber ID: " + subscriberId + ". Reason: package information not found in subscriber profile");
            }
            return null;
        }

        ProductOffer productOffer = getServerContext().getPolicyRepository().getProductOffer().byName(subscriberPackage);
        if(Objects.isNull(productOffer)){
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Skipping auto subscription subscriber ID: " + subscriberId + ". Reason: product offer(" + subscriberPackage + ") not found in policy repository");
            }
            return null;
        }

        if(productOffer.getPolicyStatus() == PolicyStatus.FAILURE) {
            if (getLogger().isWarnLogLevel()){
                getLogger().warn(MODULE, "Skipping auto subscription subscriber ID: " + subscriberId + ". Reason: product offer(" + subscriberPackage + ") is in fail state");
            }
            return null;
        }

        return productOffer;
    }


    @Override
    protected boolean isApplicable(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
        PCRFRequest request = (PCRFRequest) serviceRequest;

        if(request.getPCRFEvents().contains(PCRFEvent.SESSION_UPDATE) == false){
            return false;
        }

        SessionTypeConstant sessionType = SessionTypeConstant.fromValue(request.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val));
        if(sessionType != SessionTypeConstant.RO && sessionType != SessionTypeConstant.GY) {
            return false;
        }

        if(CollectionUtils.isEmpty(request.getReportedMSCCs())) {
            return false;
        }

        for (MSCC mscc : request.getReportedMSCCs()) {
            GyServiceUnits usedServiceUnits = mscc.getUsedServiceUnits();

            if(Objects.isNull(usedServiceUnits)) {
                continue;
            }

            if(usedServiceUnits.getTime() > 0 || usedServiceUnits.getVolume() > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getName() {
        return MODULE;
    }
}
