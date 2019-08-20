package com.elitecore.nvsmx.ws.subscription.request;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

public class ListProductOfferRequest extends BaseWebServiceRequest {

	private String productOfferId;
	private String productOfferName;
	private String type;
	private String mode;
	private String group;
	private String currency;
	private String parameter1;
	private String parameter2;
	private String webServiceName;
	private String webServiceMethodName;


	public ListProductOfferRequest(String productOfferId, String productOfferName, String type, String mode,
								   String group, String currency, String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
		this.productOfferId = productOfferId;
		this.productOfferName = productOfferName;
		this.type = type;
		this.mode = mode;
		this.group = group;
		this.currency = currency;
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
	}

	public String getProductOfferId() {
		return productOfferId;
	}

	public void setProductOfferId(String productOfferId) {
		this.productOfferId = productOfferId;
	}

	public String getProductOfferName() {
		return productOfferName;
	}

	public void setProductOfferName(String productOfferName) {
		this.productOfferName = productOfferName;
	}

	public String getType() { return type; }

	public void setType(String type) { this.type = type; }

	public String getMode() { return mode; }

	public void setMode(String mode) { this.mode = mode; }

	public String getGroup() { return group; }

	public void setGroup(String group) { this.group = group; }


	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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
		builder.append("Product Offer Id", productOfferId);
		builder.append("Product Offer Name", productOfferName);
		builder.append("Type", type);
		builder.append("Mode", mode);
		builder.append("group", group);
		builder.append("currency", currency);
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
