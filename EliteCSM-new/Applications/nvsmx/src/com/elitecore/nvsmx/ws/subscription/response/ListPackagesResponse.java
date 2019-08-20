package com.elitecore.nvsmx.ws.subscription.response;

import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.subscription.data.PackageInfo;
import com.elitecore.nvsmx.ws.subscription.data.RnCPackageInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

public class ListPackagesResponse implements WebServiceResponse {

    private Integer responseCode;
    private String responseMessage;
    private String parameter1;
    private String parameter2;
    private String webServiceName;
    private String webServiceMethodName;

    private List<RnCPackageInfo> rncPackages;
    private List<PackageInfo> dataPackages;

    public ListPackagesResponse(Integer responseCode,
                                String responseMessage,
                                String parameter1,
                                String parameter2,
                                String webServiceName,
                                String webServiceMethodName,
                                List<RnCPackageInfo> rncPackages,
                                List<PackageInfo> dataPackages) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.webServiceName = webServiceName;
        this.webServiceMethodName = webServiceMethodName;
        this.rncPackages = rncPackages;
        this.dataPackages = dataPackages;
    }

    @Override
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

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    @XmlTransient
    @Override
    @JsonIgnore
    public String getWebServiceName() {
        return webServiceName;
    }

    public void setWebServiceName(String webServiceName) {
        this.webServiceName = webServiceName;
    }

    @XmlTransient
    @Override
    @JsonIgnore
    public String getWebServiceMethodName() {
        return webServiceMethodName;
    }

    public void setWebServiceMethodName(String webServiceMethodName) {
        this.webServiceMethodName = webServiceMethodName;
    }

    public List<RnCPackageInfo> getRncPackages() {
        return rncPackages;
    }

    public void setRncPackages(List<RnCPackageInfo> rncPackages) {
        this.rncPackages = rncPackages;
    }

    public List<PackageInfo> getDataPackages() {
        return dataPackages;
    }

    public void setDataPackages(List<PackageInfo> dataPackages) {
        this.dataPackages = dataPackages;
    }
}
