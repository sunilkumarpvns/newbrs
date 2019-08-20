package com.elitecore.nvsmx.ws.subscriberprovisioning.response;

import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.subscriberprovisioning.data.Entry;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

@XmlRootElement
public class AlternateIdProvisioningResponse implements WebServiceResponse {
    private Integer responseCode;
    private String responseMessage;
    private String parameter1;
    private String parameter2;
    private String subscriberId;
    private List<Entry> alternateIds;
    private String webServiceName;
    private String webServiceMethodName;

    public AlternateIdProvisioningResponse() {
    }

     public AlternateIdProvisioningResponse(Integer responseCode, String responseMessage,String subscriberId, List<Entry> alternateIds, String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.subscriberId = subscriberId;
        this.alternateIds = alternateIds;
        this.webServiceName = webServiceName;
        this.webServiceMethodName = webServiceMethodName;
    }



    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    //TODO provide proper impmentation
    @XmlTransient
    @Override
    @JsonIgnore
    public String getWebServiceMethodName() {
        return webServiceMethodName;
    }

    //TODO provide proper implementation
    @XmlTransient
    @Override
    @JsonIgnore
    public String getWebServiceName() {
        return webServiceName;
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public List<Entry> getAlternateIds() {
        return alternateIds;
    }

    public void setAlternateIds(List<Entry> alternateIds) {
        this.alternateIds = alternateIds;
    }


}
