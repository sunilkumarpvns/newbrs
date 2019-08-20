package com.elitecore.nvsmx.ws.subscriberprovisioning.blmanager;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.spr.params.ChangeBaseProductOfferParams;
import com.elitecore.nvsmx.policydesigner.model.subscriber.SubscriberDAO;
import com.elitecore.nvsmx.system.groovy.SubscriberProvisioningWsScript;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.ws.interceptor.WebServiceInterceptor;
import com.elitecore.nvsmx.ws.interceptor.WebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.ChangeBaseProductOfferWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.response.SubscriberProvisioningResponse;
import com.elitecore.nvsmx.ws.util.ReAuthUtil;
import com.elitecore.nvsmx.ws.util.UpdateActions;

import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class ChangeBaseProductOfferProcessor implements WSRequestProcessor {

    private static final String MODULE = "CHNG-BASE-PRODUCT-OFFR-PRCSR";

    private List<WebServiceInterceptor> interceptors;
    private List<SubscriberProvisioningWsScript> scripts;

    public ChangeBaseProductOfferProcessor(List<WebServiceInterceptor> interceptors, List<SubscriberProvisioningWsScript> scripts) {
        this.interceptors = interceptors;
        this.scripts = scripts;
    }


    @Override
    public void preProcess(WebServiceRequest request) {
        for (WebServiceInterceptor interceptor : interceptors) {
            interceptor.requestReceived(request);
        }

        for (SubscriberProvisioningWsScript groovyScript : scripts) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
            }

            ChangeBaseProductOfferWSRequest wsRequest = (ChangeBaseProductOfferWSRequest) request;

            try {
                groovyScript.preChangeBaseProductOffer(wsRequest);
            } catch (Exception e) {
                getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName()
                        + getMessage(wsRequest.getSubscriberID(), wsRequest.getAlternateId()) + ". Reason: " + e.getMessage());
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
    public SubscriberProvisioningResponse process(WebServiceRequest wsRequest, StaffData staffData) {
        ChangeBaseProductOfferWSRequest request = (ChangeBaseProductOfferWSRequest) wsRequest;

        SubscriberProvisioningResponse subscriberProvisioningResponse=null;
        String subscriberID = request.getSubscriberID();

        try{

            int result = 0;
            SPRInfo subscriber = null;
            String newProductOfferName = request.getNewProductOfferName();

            if (Strings.isNullOrBlank(newProductOfferName)) {
                getLogger().error(MODULE, "Unable to change base product Offer for subscriber: " + subscriberID
                        + ". Reason: New product offer must be provided");
                return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                        ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: New product offer name must be provided"
                        , null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
            }

            if (Strings.isNullOrBlank(subscriberID)) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE,"Subscriber ID not received");
                }

                String alternateId = request.getAlternateId();
                if (Strings.isNullOrBlank(alternateId)) {
                    LogManager.getLogger().error(MODULE, "Unable to change base product offer for subscriber. Reason: Identity parameter missing");
                    return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
                }

                subscriber = SubscriberDAO.getInstance().getSubscriberByAlternateId(alternateId, staffData);

            } else {

                subscriber = SubscriberDAO.getInstance().getSubscriberById(subscriberID, staffData);
            }

            if (subscriber == null) {
                getLogger().error(MODULE, "Unable to change base product offer for subscriber: " + subscriberID
                        + ". Reason: Subscriber not found from repository");
                return new SubscriberProvisioningResponse(ResultCode.NOT_FOUND.code,
                        ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found from repository"
                        , null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
            }

            subscriberID = subscriber.getSubscriberIdentity();
            String currentPackageName = request.getCurrentProductOfferName();

            if (Strings.isNullOrBlank(currentPackageName) == false) {

                if (currentPackageName.equals(subscriber.getProductOffer()) == false) {
                    getLogger().error(MODULE, "Unable to change product for subscriber: " + subscriberID
                            + ". Reason: Subscriber current product offer: " + subscriber.getProductOffer()
                            + " and current product offer parameter: " + currentPackageName + " are not same");
                    return new SubscriberProvisioningResponse(ResultCode.PRECONDITION_FAILED.code,
                            ResultCode.PRECONDITION_FAILED.name + ". Reason: Subscriber's current product offer: "
                                    + subscriber.getProductOffer() + " and current product offer parameter: " + currentPackageName + " are not same"
                            , null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
                }

            }

            ProductOffer currentProductOffer = getProductOffer(subscriber.getProductOffer());

            if (subscriber.getProductOffer().equals(newProductOfferName)) {
                processReset(subscriber, request.getRequestIpAddress());
                result = 1;
            } else {
                String currentProductOfferId = null;
                if (currentProductOffer != null) {
                    currentProductOfferId = currentProductOffer.getId();
                }

                ChangeBaseProductOfferParams params = new ChangeBaseProductOfferParams.Builder()
                        .withSubscriberId(subscriber.getSubscriberIdentity())
                        .withAlternateId(request.getAlternateId())
                        .withCurrentProductOfferId(currentProductOfferId)
                        .withNewProductOfferName(newProductOfferName)
                        .withParam1(request.getParameter1())
                        .withParam2(request.getParameter2())
                        .withParam3(request.getParameter3())
                        .build();

                result = SubscriberDAO.getInstance().changeDataPackageBySubscriberId(params, staffData, request.getRequestIpAddress());
            }

            UpdateActions updateAction = UpdateActions.fromValue(request.getUpdateAction());
            if (updateAction == null) {
                if (LogManager.getLogger().isInfoLogLevel()) {
                    LogManager.getLogger().info(MODULE, "Performing NO_ACTION, Invalid value "+(request.getUpdateAction()==null?"":request.getUpdateAction())+" received for parameter 'updateAction'. Valid values are 0/1/2");
                }
            } else {
                postUpdate(subscriberID, request.getUpdateAction(), getResultCode(result));
            }

            ResultCode resultcode = getResultCode(result);
            subscriberProvisioningResponse= new SubscriberProvisioningResponse(resultcode.code, resultcode.name, null,null, null, request.getWebServiceName(), request.getWebServiceMethodName());

        } catch(OperationFailedException e){
            getLogger().error(MODULE, "Change product offer operation failed for Subscriber : "
                    + (subscriberID != null ? subscriberID : request.getAlternateId())
                    + ". Reason:"+e.getMessage());
            if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
                getLogger().trace(MODULE, e);
            }
            subscriberProvisioningResponse = new SubscriberProvisioningResponse(e.getErrorCode().code,e.getErrorCode().name+". Reason: "+e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
            return subscriberProvisioningResponse;
        } catch(Exception e){
            LogManager.getLogger().error(MODULE, "Change product offer operation failed for Subscriber : "
                    + (subscriberID != null ? subscriberID : request.getAlternateId())
                    + ".Reason:"+e.getMessage());
            LogManager.getLogger().trace(MODULE, e);
            subscriberProvisioningResponse = new SubscriberProvisioningResponse(ResultCode.INTERNAL_ERROR.code,"Failure : Reason("+e.getMessage()+")", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
            return subscriberProvisioningResponse;
        }

        return subscriberProvisioningResponse;
    }

    private void processReset(SPRInfo sprInfo, String requestIpAddress) throws OperationFailedException {
        SubscriberDAO.getInstance().processReset(sprInfo, requestIpAddress);

    }

    private ProductOffer getProductOffer(String name) {
        return DefaultNVSMXContext.getContext().getPolicyRepository().getProductOffer().base().byName(name);
    }

    @Override
    public void postProcess(WebServiceRequest request, WebServiceResponse response) {
        for (WebServiceInterceptor interceptor : interceptors) {
            interceptor.responseReceived(response);
        }

        for (SubscriberProvisioningWsScript groovyScript : scripts) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
            }
            ChangeBaseProductOfferWSRequest wsRequest = (ChangeBaseProductOfferWSRequest) request;

            try {
                groovyScript.postChangeBaseProductOffer(wsRequest, (SubscriberProvisioningResponse) response);
            } catch (Exception e) {
                getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName()
                        + getMessage(wsRequest.getSubscriberID(), wsRequest.getAlternateId()) + ". Reason: " + e.getMessage());
                getLogger().trace(MODULE, e);
            }
        }
    }

    private void postUpdate(String subscriberID, Integer updateAction, ResultCode resultcode) {

        if (resultcode == ResultCode.SUCCESS) {
            try {
                ReAuthUtil.doReAuthBySubscriberId(SubscriberDAO.getInstance().getStrippedSubscriberIdentity(subscriberID), updateAction);
            } catch (OperationFailedException e) {
                getLogger().error(MODULE, "Error while performing re-auth for subscriber Id:" + subscriberID + ". Reason: " + e.getMessage());
                if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
                    getLogger().trace(MODULE, e);
                }
            }
        }
    }

    private ResultCode getResultCode(int result) {
        if(result > 0 ){
            return  ResultCode.SUCCESS;
        }else{
            return ResultCode.NOT_FOUND;
        }
    }


}
