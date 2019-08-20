package com.elitecore.nvsmx.ws.subscription.request;

import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

import java.math.BigDecimal;

public class SubscribeMonetaryRechargePlanWSRequest extends BaseWebServiceRequest {
	private String subscriberId;
	private String alternateId;
	private String cui;
	private String monetaryRechargePlanId;
	private String monetaryRechargePlanName;
	private Integer updateAction;
	private boolean updateBalanceIndication;
	private BigDecimal price;
	private String webServiceName;
	private String webServiceMethodName;
	private String parameter1;
	private String parameter2;
	private long expiryDate;




	public SubscribeMonetaryRechargePlanWSRequest(String subscriberId, String alternateId, Integer updateAction, String cui, String monetaryRechargePlanId,
                                                  String monetaryRechargePlanName,
                                                  boolean updateBalanceIndication, BigDecimal price, long expiryDate,String webServiceName, String webServiceMethodName, String parameter1,
												  String parameter2) {
		this.subscriberId = subscriberId;
		this.alternateId = alternateId;
		this.updateAction = updateAction;
		this.cui = cui;
		this.monetaryRechargePlanId = monetaryRechargePlanId;
		this.monetaryRechargePlanName = monetaryRechargePlanName;
		this.updateBalanceIndication = updateBalanceIndication;
		this.price = price;
		this.expiryDate=expiryDate;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
		this.parameter1 = parameter1;
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

	public String getCui() {
		return cui;
	}

	public void setCui(String cui) {
		this.cui = cui;
	}

	public String getMonetaryRechargePlanId() {
		return monetaryRechargePlanId;
	}

	public void setMonetaryRechargePlanId(String monetaryRechargePlanId) {
		this.monetaryRechargePlanId = monetaryRechargePlanId;
	}

	public String getMonetaryRechargePlanName() {
		return monetaryRechargePlanName;
	}

	public void setMonetaryRechargePlanName(String monetaryRechargePlanName) {
		this.monetaryRechargePlanName = monetaryRechargePlanName;
	}

	public Integer getUpdateAction() {
		return updateAction;
	}

	public void setUpdateAction(Integer updateAction) {
		this.updateAction = updateAction;
	}

	public String getWebServiceName() {
		return webServiceName;
	}

	public String getWebServiceMethodName() {
		return webServiceMethodName;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public long getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(long expiryDate) {
		this.expiryDate = expiryDate;
	}

	public boolean getUpdateBalanceIndication() {
		return updateBalanceIndication;
	}

	public void setUpdateBalanceIndication(boolean updateBalanceIndication) {
		this.updateBalanceIndication = updateBalanceIndication;
	}

	public String getParameter1() {return parameter1;}
	public String getParameter2() {return parameter2;}

	@Override
	public void visit(DiagnosticContextInjector manager) {
		manager.add(this);
	}
}
