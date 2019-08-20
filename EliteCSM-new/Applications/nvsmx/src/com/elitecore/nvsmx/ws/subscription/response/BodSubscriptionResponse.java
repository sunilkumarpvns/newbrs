package com.elitecore.nvsmx.ws.subscription.response;

import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.subscription.data.BodSubscriptionData;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

@XmlRootElement
public class BodSubscriptionResponse implements WebServiceResponse {
    private Integer responseCode;
    private String responseMessage;
    private String parameter1;
    private String parameter2;

    private List<BodSubscriptionData> bodSubscriptionData;
    private String webServiceName;
    private String webServiceMethodName;

    public BodSubscriptionResponse(){}

    public BodSubscriptionResponse(Integer responseCode,
                                String responseMessage,
                                List<BodSubscriptionData> bodSubscriptionData,
                                String parameter1, String parameter2,
                                String webServiceName, String webServiceMethodName) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.bodSubscriptionData = bodSubscriptionData;
        this.webServiceName = webServiceName;
        this.webServiceMethodName = webServiceMethodName;
    }

    public Integer getResponseCode() { return responseCode; }

    public void setResponseCode(Integer responseCode) { this.responseCode = responseCode; }

    public String getResponseMessage() { return responseMessage; }

    public void setResponseMessage(String responseMessage) { this.responseMessage = responseMessage; }

    public String getParameter1() { return parameter1; }

    public void setParameter1(String parameter1) { this.parameter1 = parameter1; }

    public String getParameter2() { return parameter2; }

    public void setParameter2(String parameter2) { this.parameter2 = parameter2; }

    public List<BodSubscriptionData> getBodSubscriptionData() { return bodSubscriptionData; }

    public void setBodSubscriptionData(List<BodSubscriptionData> bodSubscriptionData) { this.bodSubscriptionData = bodSubscriptionData; }

    @XmlTransient
    @Override
    @JsonIgnore
    public String getWebServiceMethodName() {
        return webServiceMethodName;
    }

    @XmlTransient
    @Override
    @JsonIgnore
    public String getWebServiceName() {
        return webServiceName;
    }

}
