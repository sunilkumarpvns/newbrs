package com.elitecore.nvsmx.ws.subscription.request;

import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

import javax.xml.bind.annotation.XmlTransient;

public class MonetaryBalanceEnquiryRequest extends BaseWebServiceRequest {
    private String subscriberId;
    private String alternateId;
    private String serviceId;
    private String serviceName;
    private String parameter1;
    private String parameter2;
    private String webServiceName;
    private String webServiceMethodName;

    public MonetaryBalanceEnquiryRequest(){
    }

    public MonetaryBalanceEnquiryRequest(String subscriberId, String alternateId, String serviceId, String serviceName, String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
        this.subscriberId = subscriberId;
        this.serviceName = serviceName;
        this.alternateId = alternateId;
        this.serviceId = serviceId;
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

    public String getAlternateId() {
        return alternateId;
    }

    public void setAlternateId(String alternateId) {
        this.alternateId = alternateId;
    }

    public String getServiceId() { return serviceId; }

    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    @XmlTransient
    @Override
    public String getWebServiceName() {
        return webServiceName ;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @XmlTransient
    @Override
    public String getWebServiceMethodName() {
        return webServiceMethodName;
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
}
