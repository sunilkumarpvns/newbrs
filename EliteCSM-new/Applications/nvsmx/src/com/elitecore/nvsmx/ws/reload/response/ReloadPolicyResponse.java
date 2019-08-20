package com.elitecore.nvsmx.ws.reload.response;

import com.elitecore.nvsmx.ws.reload.data.PolicyCacheDetail;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by aditya on 4/20/17.
 */
@XmlRootElement
public class ReloadPolicyResponse {
    private Integer responseCode;
    private String responseMessage;
    private PolicyCacheDetail localPolicyCacheDetail;
    private List<RemoteRMIResponse> remoteRMIResponseList;
    private String parameter1;
    private String parameter2;


    public ReloadPolicyResponse(Integer responseCode, String responseMessage, PolicyCacheDetail localPolicyCacheDetail, List<RemoteRMIResponse> remoteRMIResponseList, String parameter1, String parameter2) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.localPolicyCacheDetail = localPolicyCacheDetail;
        this.remoteRMIResponseList = remoteRMIResponseList;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
    }

    public ReloadPolicyResponse() {
    }



    @XmlElement(name="responseCode")
    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    @XmlElement(name="responseMessage")
    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }


    @XmlElement(name="parameter1")
    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    @XmlElement(name="parameter2")
    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }


    public PolicyCacheDetail getLocalPolicyCacheDetail() {
        return localPolicyCacheDetail;
    }

    public void setLocalPolicyCacheDetail(PolicyCacheDetail localPolicyCacheDetail) {
        this.localPolicyCacheDetail = localPolicyCacheDetail;
    }

    public List<RemoteRMIResponse> getRemoteRMIResponseList() {
        return remoteRMIResponseList;
    }

    public void setRemoteRMIResponseList(List<RemoteRMIResponse> remoteRMIResponseList) {
        this.remoteRMIResponseList = remoteRMIResponseList;
    }
}
