package com.elitecore.nvsmx.ws.subscription.request;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

public class TopUpQueryRequest extends BaseWebServiceRequest {
		
	private String subscriberId;
	private String alternateId;
	private String packageName;
	private String parameter1;
	private String parameter2;
	private String webServiceName;
	private String webServiceMethodName;

	public TopUpQueryRequest(String subscriberId,String alternateId, String packageName,String parameter1, String parameter2
			,String webServiceName, String webServiceMethodName) {
		this.subscriberId = subscriberId;
		this.alternateId = alternateId;
		this.packageName = packageName;
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
		
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

	@Override
	public void visit(DiagnosticContextInjector manager) {
		manager.add(this);
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
	
	public String getAlternateId() {
		return alternateId;
	}
	
	public void setAlternateId(String alternateId) {
		this.alternateId = alternateId;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public void setPackageName(String basePackageName) {
		this.packageName = basePackageName;
	}
	
	@Override
	public String toString() {
	
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);
		
		builder.append("Subscriber Id",subscriberId);
		builder.append("Alternate Id",alternateId);
		builder.append("Package Name",packageName);
		builder.append("Parameter 1", parameter1);
		builder.append("Parameter 2", parameter2);
		
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

