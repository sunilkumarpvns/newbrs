package com.elitecore.nvsmx.ws.subscriberprovisioning.request;

import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

public class AddAlternateIdWSRequest extends BaseWebServiceRequest {
    private String subscriberId;
    private String alternateId;
    private String parameter1;
    private String parameter2;
    private String webServiceName;
    private String webServiceMethodName;


    public AddAlternateIdWSRequest(String subscriberId, String alternateId, String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
        this.subscriberId = subscriberId;
        this.alternateId = alternateId;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.webServiceName = webServiceName;
        this.webServiceMethodName = webServiceMethodName;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public String getAlternateId() {
        return alternateId;
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

    @Override
    public String getWebServiceName() {
        return webServiceName;
    }

    @Override
    public String getWebServiceMethodName() {
        return webServiceMethodName;
    }
}
