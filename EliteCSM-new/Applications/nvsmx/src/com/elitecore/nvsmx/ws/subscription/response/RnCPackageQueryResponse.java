package com.elitecore.nvsmx.ws.subscription.response;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.subscription.data.RnCPackageInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;


@XmlRootElement
@XmlType(propOrder={"responseCode", "responseMessage","parameter1","parameter2","rncPackages"})
public class RnCPackageQueryResponse implements WebServiceResponse {

    private Integer responseCode;
    private String responseMessage;
    private String parameter1;
    private String parameter2;
    private String webServiceName;
    private String webServiceMethodName;

    private List<RnCPackageInfo> rncPackages;

    public RnCPackageQueryResponse(){}
    public RnCPackageQueryResponse(Integer responseCode, String responseMessage, String parameter1, String parameter2,
                                   List<RnCPackageInfo> rncPackages, String webServiceName, String webServiceMethodName) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.rncPackages = rncPackages;
        this.webServiceName = webServiceName;
        this.webServiceMethodName = webServiceMethodName;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public String getParameter2() {
        return parameter2;
    }

    public List<RnCPackageInfo> getRncPackages() {
        return rncPackages;
    }

    public void setRncPackages(List<RnCPackageInfo> rncPackages) {
        this.rncPackages = rncPackages;
    }

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
