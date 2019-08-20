package com.elitecore.nvsmx.ws.subscription.request;

import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

import javax.xml.bind.annotation.XmlTransient;

public class SubscribeTopUpWSRequest extends BaseWebServiceRequest {

	private String parentId;
	private String subscriberId;
	private String alternateId;
	private String cui;
	private String topUpPackageId;
	private String topUpPackageName;
	private String subscriptionStatusValue;
	private String subscriptionStatusName;
	private Long startTime;
	private Long endTime;
	private Integer updateAction;
	private final Integer priority;
	private String parameter1;
	private String parameter2;
	private String webServiceName;
	private String webServiceMethodName;
	private boolean updateBalanceIndication;


	public SubscribeTopUpWSRequest(String parentId, String subscriberId, String alternateId, Integer updateAction, String cui, String topUpPackageId,
								   String topUpPackageName, String subscriptionStatusValue, String subscriptionStatusName, Long startTime, Long endTime, Integer priority, boolean updateBalanceIndication, String parameter1, String parameter2,
								   String webServiceName, String webServiceMethodName) {
		this.parentId = parentId;
		this.subscriberId = subscriberId;
		this.alternateId = alternateId;
		this.updateAction = updateAction;
		this.cui = cui;
		this.topUpPackageId = topUpPackageId;
		this.topUpPackageName = topUpPackageName;
		this.subscriptionStatusValue = subscriptionStatusValue;
		this.subscriptionStatusName = subscriptionStatusName;
		this.startTime = startTime;
		this.endTime = endTime;
		this.priority = priority;
		this.updateBalanceIndication=updateBalanceIndication;
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;

	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
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

	public String getTopUpPackageId() {
		return topUpPackageId;
	}

	public void setTopUpPackageId(String topUpPackageId) {
		this.topUpPackageId = topUpPackageId;
	}

	public String getTopUpPackageName() {
		return topUpPackageName;
	}

	public void setTopUpPackageName(String topUpPackageName) {
		this.topUpPackageName = topUpPackageName;
	}

	public String getSubscriptionStatusValue() {
		return subscriptionStatusValue;
	}

	public void setSubscriptionStatusValue(String subscriptionStatusValue) {
		this.subscriptionStatusValue = subscriptionStatusValue;
	}

	public String getSubscriptionStatusName() {
		return subscriptionStatusName;
	}

	public void setSubscriptionStatusName(String subscriptionStatusName) {
		this.subscriptionStatusName = subscriptionStatusName;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Integer getUpdateAction() {
		return updateAction;
	}

	public void setUpdateAction(Integer updateAction) {
		this.updateAction = updateAction;
	}

	public Integer getPriority() {
		return priority;
	}

	public String getParameter1() {return parameter1;}
	public String getParameter2() {return parameter2;}

	public boolean getUpdateBalanceIndication() {
		return updateBalanceIndication;
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
		builder.append("Parent Id", parentId);
		builder.append("Cui", cui);
		builder.append("TopUpPackage Id", topUpPackageId);
		builder.append("TopUpPackage Name", topUpPackageName);
		builder.append("Subscription Status Name", subscriptionStatusName);
		builder.append("Subscription Status Value", subscriptionStatusValue);
		builder.append("Start Time", startTime);
		builder.append("End Time", endTime);
		builder.append("Priority", priority);

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


