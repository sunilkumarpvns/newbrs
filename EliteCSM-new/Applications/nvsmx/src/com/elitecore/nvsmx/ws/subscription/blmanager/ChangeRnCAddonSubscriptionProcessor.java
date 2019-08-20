package com.elitecore.nvsmx.ws.subscription.blmanager;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.policydesigner.model.subscriber.SubscriberDAO;
import com.elitecore.nvsmx.system.groovy.SubscriptionWsScript;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.ws.interceptor.WebServiceInterceptor;
import com.elitecore.nvsmx.ws.interceptor.WebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.subscription.data.SubscriptionData;
import com.elitecore.nvsmx.ws.subscription.request.ChangeRnCAddOnProductOfferWSRequest;
import com.elitecore.nvsmx.ws.subscription.response.SubscriptionResponse;
import com.elitecore.nvsmx.ws.util.ReAuthUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class ChangeRnCAddonSubscriptionProcessor implements WSRequestProcessor {

    private static final String MODULE = "CHNG-RNC-ADDON-PRODUCT-OFFR-PRCSR";
    private static final String SUCCESS =  "SUCCESS";

    private List<WebServiceInterceptor> interceptors;
    private List<SubscriptionWsScript> scripts;
    private StaffData adminStaff;

    public ChangeRnCAddonSubscriptionProcessor(List<WebServiceInterceptor> interceptors, List<SubscriptionWsScript> scripts) {
        this.interceptors = interceptors;
        this.scripts = scripts;
        adminStaff = new StaffData();
        adminStaff.setUserName("admin");
    }


    @Override
    public void preProcess(WebServiceRequest request) {
        for (WebServiceInterceptor interceptor : interceptors) {
            interceptor.requestReceived(request);
        }

        for (SubscriptionWsScript groovyScript : scripts) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
            }

            ChangeRnCAddOnProductOfferWSRequest wsRequest = (ChangeRnCAddOnProductOfferWSRequest) request;

            try {
                groovyScript.preChangeRnCAddonProductOffer(wsRequest);
            } catch (Exception e) {
                getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName()
                        + getMessage(wsRequest.getSubscriberId(), wsRequest.getAlternateId()) + ". Reason: " + e.getMessage());
                getLogger().trace(MODULE, e);
            }
        }
    }

    private String getMessage(String subscriberId, String alternateId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" for subscriber Id: ").append(subscriberId)
                .append(" and alternate Id: ").append(alternateId);
        return sb.toString();
    }

    @Override
    public SubscriptionResponse process(WebServiceRequest wsRequest, StaffData staffData) {
        ChangeRnCAddOnProductOfferWSRequest request = (ChangeRnCAddOnProductOfferWSRequest) wsRequest;

        String subscriberId = request.getSubscriberId();
        String rncAddOnSubscriptionId = request.getRncAddOnSubscriptionId();
        String subscriptionStatusValue = request.getSubscriptionStatusValue();
        String subscriptionStatusName = request.getSubscriptionStatusName();
        String rncAddOnProductOfferName = request.getRncAddOnProductOfferName();
        String subscriptionOrder = request.getSubscriptionOrder();
        String requestIpAddress = wsRequest.getRequestIpAddress();
        Integer updateAction = request.getUpdateAction();

        ProductOffer productOffer = null;

        if (Strings.isNullOrBlank(rncAddOnSubscriptionId)) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Trying to change RnC addon product offer subscription by addon name. Reason: RnC addon subscription id not received");
            }
            if (Strings.isNullOrBlank(rncAddOnProductOfferName)) {
                getLogger().error(MODULE, "Unable to change RnC addon product offer subscription. Reason: RnC addon product offer id or addon name not received");
                return new SubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "RnC addon subscription id or addon name not received", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
            }else{
                productOffer = getProductOfferByName(rncAddOnProductOfferName);
            }

            if (Strings.isNullOrBlank(subscriptionOrder)) {
                getLogger().error(MODULE, "Unable to change RnC addon product offer subscription. Reason: RnC addon product offer order(OLDEST/LATEST) not received");
                return new SubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "RnC addon product offer order(OLDEST/LATEST) not received", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
            }
        }

        SubscriptionState newSubscriptionState = getSubscriptionStatus(subscriptionStatusValue, subscriptionStatusName);
        if (newSubscriptionState == null) {
            getLogger().error(MODULE, "Unable to change RnC addon subscription for id: " + rncAddOnSubscriptionId
                    + "). Reason: Invalid subscription status received");
            return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid subscription status received", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
        } else if (newSubscriptionState != SubscriptionState.STARTED
                && newSubscriptionState != SubscriptionState.UNSUBSCRIBED) {
            getLogger().error(MODULE, "Unable to change addOn product offer subscription for addOn subscription id: " + rncAddOnSubscriptionId
                    + "). Reason: Invalid subscription status received");
            return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid Subscription Status received", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
        }

        if(request.getStartTime()!= null && request.getStartTime() == Long.MIN_VALUE) {
            getLogger().error(MODULE, "Unable to change RnC addOn product offer subscription. Reason: Invalid Start time received");
            return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid Start time received ", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
        }
        if( request.getEndTime()!= null && request.getEndTime() == Long.MIN_VALUE) {
            getLogger().error(MODULE, "Unable to change RnC addOn product offer subscription. Reason: Invalid End time received");
            return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid End time received ", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
        }

        if(Objects.nonNull(request.getPriority()) && request.getPriority() < 1) {
            getLogger().error(MODULE, "Unable to subscribe addOn. Reason: Invalid priority value");
            return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Priority shoudld be greater than 1 ", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        }

        if (Strings.isNullOrBlank(rncAddOnSubscriptionId)) {
            if (productOffer == null) {
                getLogger().error(MODULE, "Unable to change RnC addon product offer subscription. Reason: ACTIVE RnC addon not found with name: " + rncAddOnProductOfferName);
                return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "ACTIVE RnC addon not found with name: " + rncAddOnProductOfferName, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
            }
            return updateSubscriptionByProductOfferName(request, subscriberId, rncAddOnProductOfferName, updateAction, productOffer, newSubscriptionState, requestIpAddress);
        } else {
            return updateSubscriptionBySubscriptionId(request, subscriberId, updateAction, rncAddOnSubscriptionId, rncAddOnProductOfferName, newSubscriptionState, requestIpAddress);
        }


    }

    private SubscriptionResponse updateSubscriptionBySubscriptionId(ChangeRnCAddOnProductOfferWSRequest request, String subscriberId, Integer updateAction, String rncAddOnSubscriptionId, String rncAddOnProductOfferName, SubscriptionState newSubscriptionState, String requestIpAddress) {
        Subscription subscription = null;
        try {
            if (Strings.isNullOrBlank(subscriberId)) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Subscriber ID not received");
                }
                String alternateId = request.getAlternateId();

                if (Strings.isNullOrBlank(alternateId)) {
                    LogManager.getLogger().error(MODULE, "Unable to RnC addon product offer subscription. Reason: Identity parameter missing");
                    return new SubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                            ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
                }

                if (Strings.isNullOrBlank(rncAddOnProductOfferName) == false) {

                    subscription = SubscriberDAO.getInstance().getSubscribedAddOnByAlternateId(alternateId, rncAddOnSubscriptionId, adminStaff);

                    if (subscription == null) {
                        return new SubscriptionResponse(ResultCode.NOT_FOUND.code, "RnC addon product offer subscription not found by alternate id: " + alternateId, null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
                    }

                    ProductOffer productOffer = getProductOfferById(subscription.getProductOfferId());

                    if (productOffer.getName().equals(rncAddOnProductOfferName) == false) {
                        getLogger().error(MODULE, "Error while changing RnC addon product offer subscription with subscription(id:" + rncAddOnSubscriptionId
                                + ") for alternate id: " + alternateId + ". Reason: RnC addon name: " + productOffer.getName()
                                + " from subscription and provided RnC addon product offer subscription name: "
                                + rncAddOnProductOfferName
                                + " do not match");
                        return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "RnC addon name: " + productOffer.getName()
                                + " from subscription and provided RnC addon product offer subscription name: " + rncAddOnProductOfferName
                                + " do not match", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
                    }
                }

                SPRInfo sprInfo = SubscriberDAO.getInstance().getSubscriberByAlternateId(alternateId, adminStaff);
                subscription = SubscriberDAO.getInstance().changeAddOnSubscription(sprInfo, rncAddOnSubscriptionId, newSubscriptionState
                        , request.getStartTime(), request.getEndTime(), request.getPriority(), request.getRejectReason(), adminStaff, request.getParameter1(), request.getParameter2(), requestIpAddress);

            } else {
                if (Strings.isNullOrBlank(rncAddOnProductOfferName) == false) {

                    subscription = SubscriberDAO.getInstance().getSubscribedAddOnBySubscriberId(subscriberId, rncAddOnSubscriptionId, adminStaff);

                    if (subscription == null) {
                        return new SubscriptionResponse(ResultCode.NOT_FOUND.code, "RnC addon product offer subscription not found by subscriber id: " + subscriberId, null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
                    }

                    ProductOffer productOffer = getProductOfferById(subscription.getProductOfferId());

                    if (productOffer.getName().equals(rncAddOnProductOfferName) == false) {
                        getLogger().error(MODULE, "Error while changing RnC addon product offer subscription with subscription(id:" + rncAddOnSubscriptionId
                                + ") for subscriber ID: " + subscriberId + ". Reason: RnC addon name: " + productOffer.getName()
                                + " from subscription and provided RnC addon product offer subscription name: "
                                + rncAddOnProductOfferName
                                + " do not match");
                        return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "RnC addon name: " + productOffer.getName()
                                + " from subscription and provided RnC addon product offer subscription name: " + rncAddOnProductOfferName
                                + " do not match", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
                    }
                }
                SPRInfo sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberId, adminStaff);
                subscription = SubscriberDAO.getInstance().changeAddOnSubscription(sprInfo, rncAddOnSubscriptionId, newSubscriptionState
                        , request.getStartTime(), request.getEndTime(), request.getPriority(), request.getRejectReason(), adminStaff, request.getParameter1(), request.getParameter2(), requestIpAddress);
            }

        } catch (OperationFailedException e) {
            getLogger().error(MODULE, "Error while changing RnC addon product offer subscription with subscription id:" + rncAddOnSubscriptionId + ". Reason:"
                    + e.getMessage());
            if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
                getLogger().trace(MODULE, e);
            }
            return new SubscriptionResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason:" + e.getMessage(), null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while changing RnC addon product offer subscription with subscription id :" + rncAddOnSubscriptionId + ". Reason:"
                    + e.getMessage());
            getLogger().trace(e);
            return new SubscriptionResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
        }

        List<Subscription> subscriptions = Collectionz.newArrayList();
        subscriptions.add(subscription);
        List<SubscriptionData> subscriptionData = createSubscriptionData(subscriptions);

        try {
            ReAuthUtil.doReAuthBySubscriberId(subscription.getSubscriberIdentity(), updateAction);
        } catch (OperationFailedException e) {
            getLogger().error(MODULE, "Error while performing re-auth for subscriber id:" + subscription.getSubscriberIdentity() + ". Reason: " + e.getMessage());
            if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
                getLogger().trace(MODULE, e);
            }
        }

        return new SubscriptionResponse(ResultCode.SUCCESS.code, SUCCESS, subscriptionData, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
    }

    private SubscriptionResponse updateSubscriptionByProductOfferName(ChangeRnCAddOnProductOfferWSRequest request, String subscriberId, String rncAddOnProductOfferName, Integer updateAction, ProductOffer productOffer, SubscriptionState newSubscriptionState, String requestIpAddress) {
        List<Subscription> orderedSubscriptions;
        try {
            SubscriptionOrder subscriptionOrder = SubscriptionOrder.fromVal(request.getSubscriptionOrder());
            if (subscriptionOrder == null) {
                return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid subscription order: " + request.getSubscriptionOrder() + ", Possible options: OLDEST/LATEST", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
            }
            if (Strings.isNullOrBlank(subscriberId)) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE,"Subscriber ID not received");
                }

                String alternateId = request.getAlternateId();

                if (Strings.isNullOrBlank(alternateId)) {
                    getLogger().error(MODULE, "Unable to change RnC addon product offer(" + productOffer.getName() + ") subscription. Reason: Identity parameter missing");
                    return new SubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                            ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
                }

                SPRInfo sprInfo = SubscriberDAO.getInstance().getSubscriberByAlternateId(alternateId, adminStaff);

                SubscriptionResponse subscriptionResponse = checkSubscriberProfileInfo(productOffer, alternateId, sprInfo, request);
                if (subscriptionResponse != null){
                    return subscriptionResponse;
                }

                List<Subscription> subscriptions = SubscriberDAO.getInstance().getSubscriptionsByAlternateId(alternateId, adminStaff);

                if (Collectionz.isNullOrEmpty(subscriptions)) {
                    return new SubscriptionResponse(ResultCode.NOT_FOUND.code, "Unable to change RnC addon product offer(" + productOffer.getName() + ") subscription. Reason: No addOn subcription found with alternateId: " + alternateId, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
                }

                orderedSubscriptions = subscriptionOrder.getSubscription(subscriptions, productOffer.getName());
                for (int index = 0; index < orderedSubscriptions.size(); index++) {
                    if (orderedSubscriptions.get(index).getType() != SubscriptionType.RO_ADDON) {
                        orderedSubscriptions.remove(index);
                    }
                }

                if (orderedSubscriptions.isEmpty()) {
                    return new SubscriptionResponse(ResultCode.NOT_FOUND.code, "Unable to change RnC addon product offer(" + productOffer.getName() + ") subscription. Reason: AddOn subcription not found with name : " + productOffer.getName(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
                }

                for (int index = 0; index < orderedSubscriptions.size(); index++) {
                    SubscriberDAO.getInstance().changeAddOnSubscription(sprInfo, orderedSubscriptions.get(index).getId()
                            , newSubscriptionState, request.getStartTime(), request.getEndTime(), request.getPriority(), request.getRejectReason()
                            , adminStaff, request.getParameter1(), request.getParameter2(), requestIpAddress);
                }
            } else {
                SPRInfo sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberId, adminStaff);

                SubscriptionResponse subscriptionResponse = checkSubscriberProfileInfo(productOffer, subscriberId, sprInfo, request);
                if (subscriptionResponse != null){
                    return subscriptionResponse;
                }

                List<Subscription> addOnSubscriptions = SubscriberDAO.getInstance().getSubscriptions(subscriberId, adminStaff);

                if (Collectionz.isNullOrEmpty(addOnSubscriptions)) {
                    return new SubscriptionResponse(ResultCode.NOT_FOUND.code, "Unable to change RnC addon product offer(" + productOffer.getName() + ") subscription. Reason: No addOn subcription found with subscriberId: " + subscriberId, null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
                }

                orderedSubscriptions = subscriptionOrder.getSubscription(addOnSubscriptions, productOffer.getName());
                for (int index = 0; index < orderedSubscriptions.size(); index++) {
                    if (orderedSubscriptions.get(index).getType() != SubscriptionType.RO_ADDON) {
                        orderedSubscriptions.remove(index);
                    }
                }

                if (orderedSubscriptions.isEmpty()) {
                    return new SubscriptionResponse(ResultCode.NOT_FOUND.code, "Unable to change RnC addon product offer(" + productOffer.getName() + ") subscription. Reason: AddOn subcription not found with name : " + rncAddOnProductOfferName, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
                }

                for (int index = 0; index < orderedSubscriptions.size(); index++) {
                    SubscriberDAO.getInstance().changeAddOnSubscription(sprInfo, orderedSubscriptions.get(index).getId()
                            , newSubscriptionState, request.getStartTime(), request.getEndTime(), request.getPriority(), request.getRejectReason()
                            , adminStaff, request.getParameter1(), request.getParameter2(), requestIpAddress);
                }
            }
        } catch (OperationFailedException e) {
            getLogger().error(MODULE, "Error while changing RnC addon product offer("+productOffer.getName()+") subscription for subscriber(" + subscriberId + "). Reason:" + e.getMessage());
            if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
                getLogger().trace(MODULE, e);
            }
            return new SubscriptionResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while changing RnC addon product offer("+productOffer.getName()+") subscription for subscriber(" + subscriberId + "). Reason:" + e.getMessage());
            getLogger().trace(e);
            return new SubscriptionResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        }

        List<Subscription> subscriptionDatas = Collectionz.newArrayList();
        subscriptionDatas.addAll(orderedSubscriptions);
        List<SubscriptionData> subscriptionData = createSubscriptionData(subscriptionDatas);

        try {
            ReAuthUtil.doReAuthBySubscriberId(subscriberId, updateAction);
        } catch (OperationFailedException e) {
            getLogger().error(MODULE, "Error while performing re-auth for subscriber Id:" + subscriberId + ". Reason: " + e.getMessage());
            if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
                getLogger().trace(MODULE, e);
            }
        }

        return new SubscriptionResponse(ResultCode.SUCCESS.code, SUCCESS, subscriptionData, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
    }

    private SubscriptionResponse checkSubscriberProfileInfo(ProductOffer productOffer, String id, SPRInfo sprInfo, ChangeRnCAddOnProductOfferWSRequest request) {
        if (sprInfo == null) {
            getLogger().error(MODULE, "Unable to change RnC addon product offer(" + productOffer.getName() + ") subscription. Reason: Subscriber not found with Id: " + id);
            return new SubscriptionResponse(ResultCode.NOT_FOUND.code,
                    ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        }

        if(SubscriberStatus.INACTIVE.name().equals(sprInfo.getStatus())){
            getLogger().error(MODULE, "Unable to change RnC addon product offer(" + productOffer.getName() + ") subscription. Reason: Subscriber status is InActive for subscriber Id: " + sprInfo.getSubscriberIdentity());
            return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
                    ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Subscriber Status is InActive", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        }

        if(sprInfo.getProfileExpiredHours() >= 0){
            getLogger().error(MODULE, "Unable to change RnC addOn product offer(" + productOffer.getName() + ") subscription. Reason: Subscriber Profile has expired for subscriber Id: " + sprInfo.getSubscriberIdentity());
            return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
                    ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Subscriber Profile has expired", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        }
        return null;
    }

    private List<SubscriptionData> createSubscriptionData(List<Subscription> subscriptions) {
        List<SubscriptionData> subscriptionDatas = Collectionz.newArrayList();
        for(Subscription subscription : subscriptions) {
            subscriptionDatas.add(SubscriptionData.fromSubscription(subscription));
        }
        return subscriptionDatas;
    }

    private ProductOffer getProductOfferByName(String name) {
        return DefaultNVSMXContext.getContext().getPolicyRepository().getProductOffer().addOn().byName(name);
    }

    private ProductOffer getProductOfferById(String id) {
        return DefaultNVSMXContext.getContext().getPolicyRepository().getProductOffer().addOn().byId(id);
    }

    private SubscriptionState getSubscriptionStatus(String subscriptionStatusValue, String subscriptionStatusName) {
        SubscriptionState subscriptionState = SubscriptionState.fromStringValue(subscriptionStatusValue);
        if (subscriptionState == null) {
            subscriptionState = SubscriptionState.fromName(subscriptionStatusName);
        }

        if (SubscriptionState.SUBSCRIBED == subscriptionState) {
            subscriptionState = SubscriptionState.STARTED;
        }
        return subscriptionState;
    }

    private enum SubscriptionOrder {

        OLDEST("OLDEST") {
            @Override
            List<Subscription> getSubscription(List<Subscription> subscriptions, String addOnName) {

                Map<String, List<Subscription>> packageNameWiseSubscriptions = new HashMap<>();
                createRnCPackageWiseSubscriptionList(subscriptions, addOnName, packageNameWiseSubscriptions);
                List<Subscription> finalList = new ArrayList<>();
                Subscription oldestSubscription = null;
                for (Map.Entry<String, List<Subscription>> entry : packageNameWiseSubscriptions.entrySet()) {
                    oldestSubscription = entry.getValue().get(0);
                    for (int i = 1; i < entry.getValue().size(); i++) {
                        if (entry.getValue().get(i).getStartTime().before(oldestSubscription.getStartTime())) {
                            oldestSubscription = entry.getValue().get(i);

                        }
                    }
                    finalList.add(oldestSubscription);
                }
                return finalList;
            }
        },
        LATEST("LATEST") {
            @Override
            List<Subscription> getSubscription(List<Subscription> addOnSubscriptions, String addOnName) {

                Map<String, List<Subscription>> packageNameWiseSubscriptions = new HashMap<>();
                createRnCPackageWiseSubscriptionList(addOnSubscriptions, addOnName, packageNameWiseSubscriptions);
                List<Subscription> finalList = new ArrayList<>();
                Subscription latestSubscription = null;
                for (Map.Entry<String, List<Subscription>> entry : packageNameWiseSubscriptions.entrySet()) {
                    latestSubscription = entry.getValue().get(0);
                    for (int i = 1; i < entry.getValue().size(); i++) {
                        if (entry.getValue().get(i).getStartTime().after(latestSubscription.getStartTime())) {
                            latestSubscription = entry.getValue().get(i);
                        }
                    }
                    finalList.add(latestSubscription);
                }
                return finalList;
            }
        };

        private static void createRnCPackageWiseSubscriptionList(List<Subscription> addOnSubscriptions, String addOnName, Map<String, List<Subscription>> packageNameWiseSubscriptions) {
            for (int index = 0; index < addOnSubscriptions.size(); index++) {

                Subscription subscription = addOnSubscriptions.get(index);

                ProductOffer productOffer = DefaultNVSMXContext.getContext().getPolicyRepository().getProductOffer().byId(subscription.getProductOfferId());

                if (productOffer == null) {
                    continue;
                }

                if (productOffer.getName().equals(addOnName)) {
                    if (packageNameWiseSubscriptions.containsKey(subscription.getPackageId())) {
                        packageNameWiseSubscriptions.get(subscription.getPackageId()).add(subscription);
                    } else {
                        List<Subscription> itrSubscription = new ArrayList<>();
                        itrSubscription.add(subscription);
                        packageNameWiseSubscriptions.put(subscription.getPackageId(), itrSubscription);
                    }
                }

            }
        }

        private String name;

        private SubscriptionOrder(String name) {
            this.name = name;
        }

        static SubscriptionOrder fromVal(String name) {

            if (OLDEST.name.equalsIgnoreCase(name)) {
                return OLDEST;
            } else if (LATEST.name.equalsIgnoreCase(name)) {
                return LATEST;
            }

            return null;
        }

        abstract List<Subscription> getSubscription(List<Subscription> addOnSubscriptions, String addOnName);
    }

    @Override
    public void postProcess(WebServiceRequest request, WebServiceResponse response) {
        for (WebServiceInterceptor interceptor : interceptors) {
            interceptor.responseReceived(response);
        }

        for (SubscriptionWsScript groovyScript : scripts) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
            }
            ChangeRnCAddOnProductOfferWSRequest wsRequest = (ChangeRnCAddOnProductOfferWSRequest) request;

            try {
                groovyScript.postChangeRnCAddonProductOffer(wsRequest, (SubscriptionResponse) response);
            } catch (Exception e) {
                getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName()
                        + getMessage(wsRequest.getSubscriberId(), wsRequest.getAlternateId()) + ". Reason: " + e.getMessage());
                getLogger().trace(MODULE, e);
            }
        }
    }
}
