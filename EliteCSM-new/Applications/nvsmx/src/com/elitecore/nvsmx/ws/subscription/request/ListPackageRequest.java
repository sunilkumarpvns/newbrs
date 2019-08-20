package com.elitecore.nvsmx.ws.subscription.request;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;


/**
 * Created by aditya on 9/6/16.
 */
public class ListPackageRequest extends BaseWebServiceRequest {

    private String packageId;
    private String packageName;
    private String packageType;
    private String currency;
    private String parameter1;
    private String parameter2;
	private String webServiceName;
	private String webServiceMethodName;

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

    public String getCurrency() { return currency; }

    public void setCurrency(String currency) { this.currency = currency; }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

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

    public ListPackageRequest(String packageId, String packageName, String packageType, String currency, String parameter1, String parameter2,
    	String webServiceName, String webServiceMethodName) {
    	
        this.packageId = packageId;
        this.packageName = packageName;
        this.packageType = packageType;
        this.currency = currency;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
    }

    @XmlTransient
	@Override
	public String getWebServiceName() {
		return webServiceName;
	}

    @XmlTransient
	@Override
	public String getWebServiceMethodName() {
		return webServiceMethodName;
	}
}
