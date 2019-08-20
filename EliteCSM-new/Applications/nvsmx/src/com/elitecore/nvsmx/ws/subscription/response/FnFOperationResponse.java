package com.elitecore.nvsmx.ws.subscription.response;

import com.elitecore.corenetvertex.spr.data.FnFGroup;
import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;

public class FnFOperationResponse implements WebServiceResponse{


    private Integer responseCode;
    private String responseMessage;
    private String parameter1;
    private String parameter2;
    private FnFGroup fnFGroup;
    private String webServiceName;
    private String webServiceMethodName;

    public FnFOperationResponse(Integer responseCode, String responseMessage, FnFGroup fnFGroup, String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
        this.webServiceMethodName = webServiceMethodName;
        this.webServiceName = webServiceName;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.fnFGroup = fnFGroup;
    }

    @Override
    public String getWebServiceMethodName() {
        return webServiceMethodName;
    }

    @Override
    public String getWebServiceName() {
        return webServiceName;
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
    public Integer getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public FnFGroup getFnFGroup() {
        return fnFGroup;
    }
}
