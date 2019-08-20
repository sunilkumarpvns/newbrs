package com.elitecore.nvsmx.ws.subscription.request;

import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

import javax.xml.bind.annotation.XmlTransient;
import java.util.List;


public class SubscribeAddOnWSRequest extends BaseWebServiceRequest {

	private String parentId;
	private String subscriberId;
	private String alternateId;
	private String cui;
	private String addOnProductOfferId;
	private String addOnPackageName;
	private String subscriptionStatusValue;
	private String subscriptionStatusName;
	private Long startTime;
	private Long endTime;
	private Integer updateAction;
	private final Integer priority;
	private String fnFGroupName;
	private List<String> fnFMembers;
	private boolean updateBalanceIndication;
	private String parameter1;
	private String parameter2;
	private String webServiceName;
	private String webServiceMethodName;


	public SubscribeAddOnWSRequest(String parentId, String subscriberId, String alternateId, Integer updateAction, String cui, String addOnPackageId,
								   String addOnPackageName, String subscriptionStatusValue, String subscriptionStatusName, Long startTime, Long endTime, Integer priority,String fnFGroupName,List<String> fnFMembers,boolean updateBalanceIndication, String parameter1, String parameter2
			, String webServiceName, String webServiceMethodName) {
		this.parentId = parentId;
		this.subscriberId = subscriberId;
		this.alternateId = alternateId;
		this.updateAction = updateAction;
		this.cui = cui;
		this.addOnProductOfferId = addOnPackageId;
		this.addOnPackageName = addOnPackageName;
		this.subscriptionStatusValue = subscriptionStatusValue;
		this.subscriptionStatusName = subscriptionStatusName;
		this.startTime = startTime;
		this.endTime = endTime;
		this.priority = priority;
		this.fnFGroupName=fnFGroupName;
		this.fnFMembers = fnFMembers;
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
		this.updateBalanceIndication= updateBalanceIndication;
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

	public String getAddOnProductOfferId() {
		return addOnProductOfferId;
	}

	public void setAddOnProductOfferId(String addOnProductOfferId) {
		this.addOnProductOfferId = addOnProductOfferId;
	}

	public String getAddOnPackageName() {
		return addOnPackageName;
	}

	public void setAddOnPackageName(String addOnPackageName) {
		this.addOnPackageName = addOnPackageName;
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

	public String getParameter1() {return parameter1;}
	public String getParameter2() {return parameter2;}
	public boolean getUpdateBalanceIndication() { return updateBalanceIndication; }

	@Override
	public void visit(DiagnosticContextInjector manager) {
		manager.add(this);
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

	public Integer getPriority() {
		return priority;
	}

	public String getFnFGroupName() {
		return fnFGroupName;
	}

	public void setFnFGroupName(String fnFGroupName) {
		this.fnFGroupName = fnFGroupName;
	}

	public List<String> getFnFMembers() {
		return fnFMembers;
	}

	public void setFnFMembers(List<String> fnFMembers) {
		this.fnFMembers = fnFMembers;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);
		
		builder.append("Subscriber Id", subscriberId);
		builder.append("Alternate Id", alternateId);
		builder.append("Parent Id", parentId);;
		builder.append("Update Action", updateAction);
		builder.append("Cui", cui);
		builder.append("AddonPackage Id", addOnProductOfferId);
		builder.append("AddonPackage Name", addOnPackageName);
		builder.append("Subscription Status Name", subscriptionStatusName);
		builder.append("Subscription Status Value", subscriptionStatusValue);
		builder.append("Start Time", startTime);
		builder.append("End Time", endTime);
		builder.append("Priority", priority);
		builder.append("FnF Members", fnFMembers);
		builder.append("UpdateBalanceIndication", updateBalanceIndication);
		builder.append("Parameter1", parameter1);
		builder.append("Parameter2", parameter2);

		return builder.toString();
	}
}
