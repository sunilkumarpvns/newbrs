package com.elitecore.nvsmx.ws.subscription.data;

import com.elitecore.corenetvertex.constants.SubscriptionState;

/**
 * 
 * @author Ishani Bhatt
 *
 */
public class TopUpSubscriptionData {

	private String topUpSubscriptionId;
	private String subscriberIdentity;
	private String topUpId;
	private String topUpName;
	private Long startTime;
	private Long endTime;
	private SubscriptionState topUpStatus;
	private String parameter1;
	private String parameter2;
	private Integer priority;
	
	public TopUpSubscriptionData(){}
	public TopUpSubscriptionData(String topUpSubscriptionId, String subscriberIdentity, String topUpId, String topUpName,
								 Long startTime, Long endTime, SubscriptionState topUpStatus, String parameter1, String parameter2, Integer priority) {

		this.topUpSubscriptionId = topUpSubscriptionId;
		this.subscriberIdentity = subscriberIdentity;
		this.topUpId = topUpId;
		this.topUpName = topUpName;
		this.startTime = startTime;
		this.endTime = endTime;
		this.topUpStatus = topUpStatus;
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.priority = priority;
	}
	
	
	public String getTopUpSubscriptionId() {
		return topUpSubscriptionId;
	}


	public void setTopUpSubscriptionId(String topUpSubscriptionId) {
		this.topUpSubscriptionId = topUpSubscriptionId;
	}


	public String getTopUpId() {
		return topUpId;
	}


	public void setTopUpId(String topUpId) {
		this.topUpId = topUpId;
	}


	public String getTopUpName() {
		return topUpName;
	}


	public void setTopUpName(String topUpName) {
		this.topUpName = topUpName;
	}


	public SubscriptionState getTopUpStatus() {
		return topUpStatus;
	}


	public void setTopUpStatus(SubscriptionState topUpStatus) {
		this.topUpStatus = topUpStatus;
	}


	public String getSubscriberIdentity() {
		return subscriberIdentity;
	}
	public Long getStartTime() {
		return startTime;
	}
	public Long getEndTime() {
		return endTime;
	}
	public String getParameter1() {
		return parameter1;
	}
	public String getParameter2() {
		return parameter2;
	}
	public void setSubscriberIdentity(String subscriberIdentity) {
		this.subscriberIdentity = subscriberIdentity;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	public void setParameter1(String parameter1) {
		this.parameter1 = parameter1;
	}
	public void setParameter2(String parameter2) {
		this.parameter2 = parameter2;
	}


	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}
}
