package com.elitecore.nvsmx.ws.subscriberprovisioning.request;

import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

public class GetAlternateIdWSRequest extends BaseWebServiceRequest {

    private String subscriberId;
    private String parameter1;
    private String parameter2;
    private String webServiceName;
    private String webServiceMethodName;


   public GetAlternateIdWSRequest(String subscriberId, String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
        this.subscriberId = subscriberId;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.webServiceName = webServiceName;
        this.webServiceMethodName = webServiceMethodName;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    @Override
    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    @Override
    public String getParameter2() {
        return parameter2;
    }

    @Override
    public void visit(DiagnosticContextInjector manager) {
        manager.add(this);
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    @Override
    public String getWebServiceName() {
        return webServiceName;
    }

    public void setWebServiceName(String webServiceName) {
        this.webServiceName = webServiceName;
    }

    @Override
    public String getWebServiceMethodName() {
        return webServiceMethodName;
    }

    public void setWebServiceMethodName(String webServiceMethodName) {
        this.webServiceMethodName = webServiceMethodName;
    }
}
