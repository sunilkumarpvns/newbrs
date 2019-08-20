package com.elitecore.nvsmx.ws.subscriberprovisioning.request;

import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;


public class ChangeBaseProductOfferWSRequest extends BaseWebServiceRequest {

    private String currentProductOfferName;
    private String newProductOfferName;
    private String subscriberID;
    private String alternateId;
    private Integer updateAction;
    private String parameter1;
    private String parameter2;
    private String parameter3;
    private String webServiceName;
    private String webServiceMethodName;

    public ChangeBaseProductOfferWSRequest(String currentProductOfferName, String newProductOfferName,
                                           String subscriberID, String alternateId, Integer updateAction,
                                           String webServiceName, String webServiceMethodName, String parameter1, String parameter2, String parameter3) {
        super();
        this.currentProductOfferName = currentProductOfferName;
        this.newProductOfferName = newProductOfferName;
        this.subscriberID = subscriberID;
        this.alternateId = alternateId;
        this.updateAction = updateAction;
        this.webServiceName = webServiceName;
        this.webServiceMethodName = webServiceMethodName;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.parameter3 = parameter3;
    }

    public String getCurrentProductOfferName() {
        return currentProductOfferName;
    }

    public void setCurrentProductOfferName(String currentProductOfferName) {
        this.currentProductOfferName = currentProductOfferName;
    }

    public String getNewProductOfferName() {
        return newProductOfferName;
    }

    public void setNewProductOfferName(String newProductOfferName) {
        this.newProductOfferName = newProductOfferName;
    }

    public String getSubscriberID() {
        return subscriberID;
    }

    public void setSubscriberID(String subscriberID) {
        this.subscriberID = subscriberID;
    }

    public String getAlternateId() {
        return alternateId;
    }

    public void setAlternateId(String alternateId) {
        this.alternateId = alternateId;
    }

    public Integer getUpdateAction() {
        return updateAction;
    }

    public void setUpdateAction(Integer updateAction) {
        this.updateAction = updateAction;
    }

    public String getWebServiceName() {
        return webServiceName;
    }

    public void setWebServiceName(String webServiceName) {
        this.webServiceName = webServiceName;
    }

    public String getWebServiceMethodName() {
        return webServiceMethodName;
    }

    public void setWebServiceMethodName(String webServiceMethodName) {
        this.webServiceMethodName = webServiceMethodName;
    }

    @Override
    public String getParameter1() {
        return parameter1;
    }

    @Override
    public String getParameter2() {
        return parameter2;
    }

    @Override
    public void visit(DiagnosticContextInjector manager) {
        manager.add(this);
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public String getParameter3() {
        return parameter3;
    }

    public void setParameter3(String parameter3) {
        this.parameter3 = parameter3;
    }

}
