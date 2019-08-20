package com.elitecore.nvsmx.ws.resetusage.request;

import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

import javax.xml.bind.annotation.XmlTransient;

public class ResetBillingCycleWSRequest extends BaseWebServiceRequest {
	private String subscriberID;
	private String alternateID;
	private String productOfferName;
	private Long resetBillingCycleDate;
	private String resetReason;
	private String parameter1;
	private String parameter2;
	private String parameter3;
	private String webServiceName;
	private String webServiceMethodName;
	
	
	public ResetBillingCycleWSRequest(String subscriberID,
									  String alternateID, String productOfferName, Long resetBillingCycleDate,
									  String resetReason, String parameter1, String parameter2,
									  String parameter3, String webServiceName, String webServiceMethodName) {
		super();
		this.subscriberID = subscriberID;
		this.alternateID = alternateID;
		this.productOfferName = productOfferName;
		this.resetBillingCycleDate = resetBillingCycleDate;
		this.resetReason = resetReason;
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.parameter3 = parameter3;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
	}
	public String getSubscriberID() {
		return subscriberID;
	}
	public void setSubscriberID(String subscriberID) {
		this.subscriberID = subscriberID;
	}
	public String getAlternateID() {
		return alternateID;
	}
	public void setAlternateID(String alternateID) {
		this.alternateID = alternateID;
	}
	public String getProductOfferName() {
		return productOfferName;
	}
	public void setProductOfferName(String productOfferName) {
		this.productOfferName = productOfferName;
	}
	public Long getResetBillingCycleDate() {
		return resetBillingCycleDate;
	}
	public void setResetBillingCycleDate(Long resetBillingCycleDate) {
		this.resetBillingCycleDate = resetBillingCycleDate;
	}
	public String getResetReason() {
		return resetReason;
	}
	public void setResetReason(String resetReason) {
		this.resetReason = resetReason;
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
	public String getParameter3() {
		return parameter3;
	}
	public void setParameter3(String parameter3) {
		this.parameter3 = parameter3;
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
	
	public String toString(){
		ToStringBuilder toStringBuilder = new ToStringBuilder(this,
				ToStringStyle.CUSTOM_TO_STRING_STYLE).append("Subscriber Id",subscriberID)
				.append("Alternate Id", alternateID)
				.append("Product Offer Name", productOfferName)
				.append("Reset Billing Cycle Date", resetBillingCycleDate)
				.append("Reset Reason", resetReason)
				.append("Parameter1", parameter1)
				.append("Parameter2", parameter2)
				.append("Parameter3", parameter3);
		return toStringBuilder.toString();
	}

}
