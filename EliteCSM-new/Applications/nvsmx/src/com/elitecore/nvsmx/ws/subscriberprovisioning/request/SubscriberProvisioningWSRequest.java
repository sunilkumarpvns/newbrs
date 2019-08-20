package com.elitecore.nvsmx.ws.subscriberprovisioning.request;

import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

import javax.xml.bind.annotation.XmlTransient;
import java.util.List;
import java.util.ListIterator;


public class SubscriberProvisioningWSRequest extends BaseWebServiceRequest {

	private String parameter1;
	private String parameter2;
	private String webServiceName;
	private String webServiceMethodName;

	public SubscriberProvisioningWSRequest(String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
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
	
	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);
		
		builder.append("Parameter 1", parameter1);
		builder.append("Parameter 2", parameter2);
		
		return builder.toString();
	}

	protected void trimList(List<String> listToTrim) {
		ListIterator<String> it = listToTrim.listIterator();
		while (it.hasNext()) {
			it.set(it.next().trim());
		}
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