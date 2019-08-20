package com.elitecore.nvsmx.ws.subscription.rest.request;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ChangeDataAddOnSubscriptionRestRequest {
	private String subscriberId;
	private String alternateId;
	private String dataAddOnSubscriptionId;
	private String subscriptionStatusValue;
	private String subscriptionStatusName;
	private String dataAddOnProductOfferName;
	private String subscriptionOrder;
	private String startTime;
	private String endTime;
	private String priority;
	private String rejectReason;
	private String updateAction;



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

	public String getDataAddOnSubscriptionId() {
		return dataAddOnSubscriptionId;
	}

	public void setDataAddOnSubscriptionId(String dataAddOnSubscriptionId) {
		this.dataAddOnSubscriptionId = dataAddOnSubscriptionId;
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

	public String getDataAddOnProductOfferName() {
		return dataAddOnProductOfferName;
	}

	public void setDataAddOnProductOfferName(String dataAddOnProductOfferName) {
		this.dataAddOnProductOfferName = dataAddOnProductOfferName;
	}

	public String getSubscriptionOrder() {
		return subscriptionOrder;
	}

	public void setSubscriptionOrder(String subscriptionOrder) {
		this.subscriptionOrder = subscriptionOrder;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getUpdateAction() {
		return updateAction;
	}

	public void setUpdateAction(String updateAction) {
		this.updateAction = updateAction;
	}
}
