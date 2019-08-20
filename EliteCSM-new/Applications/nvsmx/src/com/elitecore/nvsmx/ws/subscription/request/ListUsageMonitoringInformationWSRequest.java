package com.elitecore.nvsmx.ws.subscription.request;

import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

import javax.xml.bind.annotation.XmlTransient;


public class ListUsageMonitoringInformationWSRequest extends BaseWebServiceRequest {
	
	private String subscriberId;
	private String alternateId;
	private String packageId;
	private String packageName;
	private String quotaProfileId;
	private String quotaProfileName;
	private String serviceId;
	private String serviceName;
	private String parameter1;
	private String parameter2;
	private String webServiceName;
	private String webServiceMethodName;

	public ListUsageMonitoringInformationWSRequest(String subscriberId, String alternateId,
			String packageId, String packageName, String quotaProfileId, String quotaProfileName, String serviceId, String serviceName,
			String parameter1, String parameter2,String webServiceName, String webServiceMethodName) {
		this.subscriberId = subscriberId;
		this.alternateId = alternateId;
		this.packageId = packageId;
		this.packageName = packageName;
		this.quotaProfileId = quotaProfileId;
		this.quotaProfileName = quotaProfileName;
		this.serviceId = serviceId;
		this.serviceName = serviceName;
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

	public String getQuotaProfileId() {
		return quotaProfileId;
	}

	public void setQuotaProfileId(String quotaProfileId) {
		this.quotaProfileId = quotaProfileId;
	}

	public String getQuotaProfileName() {
		return quotaProfileName;
	}

	public void setQuotaProfileName(String quotaProfileName) {
		this.quotaProfileName = quotaProfileName;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getParameter1() {
		return parameter1;
	}

	public String getParameter2() {
		return parameter2;
	}

	@Override
	public void visit(DiagnosticContextInjector manager) {
		manager.add(this);
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);
		
		builder.append("Subscriber Id", subscriberId);
		builder.append("Alternate Id", alternateId);
		builder.append("Package Id", packageId);
		builder.append("Package Name", packageName);
		builder.append("QoutaProfile Id", quotaProfileId);
		builder.append("QoutaProfile Name", quotaProfileName);
		builder.append("Service Id", serviceId);
		builder.append("Service Name", serviceName);
		builder.append("Parameter 1", getParameter1());
		builder.append("Parameter 2", getParameter2());
		
		return builder.toString();
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
