package com.elitecore.nvsmx.ws.subscription.request;

import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

public class ListPackagesRequest extends BaseWebServiceRequest {

    private String packageId;
    private String packageName;
    private String packageType;
    private String type;
    private String mode;
    private String group;
    private String currency;
    private String parameter1;
    private String parameter2;
    private String webServiceName;
    private String webServiceMethodName;

    public ListPackagesRequest(String packageId,
                               String packageName,
                               String packageType,
                               String type,
                               String mode,
                               String group,
                               String currency,
                               String parameter1,
                               String parameter2,
                               String webServiceName,
                               String webServiceMethodName) {
        this.packageId = packageId;
        this.packageName = packageName;
        this.packageType = packageType;
        this.type = type;
        this.mode=mode;
        this.group=group;
        this.currency=currency;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.webServiceName = webServiceName;
        this.webServiceMethodName = webServiceMethodName;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getGroup() { return group; }

    public void setGroup(String group) { this.group = group; }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String getWebServiceName() {
        return webServiceName;
    }

    public void setWebServiceName(String webServiceName) {
        this.webServiceName = webServiceName;
    }

    public String getParameter1() {return parameter1;}
    public String getParameter2() {return parameter2;}

    @Override
    public String getWebServiceMethodName() {
        return webServiceMethodName;
    }

    public void setWebServiceMethodName(String webServiceMethodName) {
        this.webServiceMethodName = webServiceMethodName;
    }

    @Override
    public void visit(DiagnosticContextInjector manager) {
        manager.add(this);
    }
}
