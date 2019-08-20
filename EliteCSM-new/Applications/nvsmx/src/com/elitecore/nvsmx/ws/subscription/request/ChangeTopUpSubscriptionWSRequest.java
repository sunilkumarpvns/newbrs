package com.elitecore.nvsmx.ws.subscription.request;

import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

import javax.xml.bind.annotation.XmlTransient;

public class ChangeTopUpSubscriptionWSRequest extends BaseWebServiceRequest {

		private String subscriberId;
		private String alternateId;
		private String topUpSubscriptionId;
		private String subscriptionStatusValue;
		private String subscriptionStatusName;
		private String topUpName;
		private String subscriptionOrder;
		private Long startTime;
		private Long endTime;
		private Integer priority;
		private String rejectReason;
		private Integer updateAction;
		private String parameter1;
		private String parameter2;
		private String webServiceName;
		private String webServiceMethodName;

		public ChangeTopUpSubscriptionWSRequest(String subscriberId, String alternateId, Integer updateAction, String topUpSubscriptionId,
												String subscriptionStatusValue, String subscriptionStatusName, String topUpName, String subscriptionOrder, Long startTime, Long endTime, String rejectReason, String parameter1, String parameter2,
												Integer priority, String webServiceName, String webServiceMethodName) {

			this.subscriberId = subscriberId;
			this.alternateId = alternateId;
			this.updateAction = updateAction;
			this.topUpSubscriptionId = topUpSubscriptionId;
			this.subscriptionStatusValue = subscriptionStatusValue;
			this.subscriptionStatusName = subscriptionStatusName;
			this.topUpName = topUpName;
			this.subscriptionOrder = subscriptionOrder;
			this.startTime = startTime;
			this.endTime = endTime;
			this.rejectReason = rejectReason;
			this.priority = priority;
			this.parameter1 = parameter1;
			this.parameter2 = parameter2;
			this.webServiceName = webServiceName;
			this.webServiceMethodName = webServiceMethodName;

		}

		

		public String getTopUpSubscriptionId() {
			return topUpSubscriptionId;
		}



		public void setTopUpSubscriptionId(String topUpSubscriptionId) {
			this.topUpSubscriptionId = topUpSubscriptionId;
		}



		public String getTopUpName() {
			return topUpName;
		}



		public void setTopUpName(String topUpName) {
			this.topUpName = topUpName;
		}



		public String getSubscriptionOrder() {
			return subscriptionOrder;
		}

		public void setSubscriptionOrder(String subscriptionOrder) {
			this.subscriptionOrder = subscriptionOrder;
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

		public String getRejectReason() {
			return rejectReason;
		}

		public void setRejectReason(String rejectReason) {
			this.rejectReason = rejectReason;
		}

		public Integer getUpdateAction() {
			return updateAction;
		}

		public void setUpdateAction(Integer updateAction) {
			this.updateAction = updateAction;
		}

		public String getParameter1() {return parameter1;}
		public String getParameter2() {return parameter2;}

	@Override
	public void visit(DiagnosticContextInjector manager) {
		manager.add(this);
	}

	@Override
		public String toString() {
			ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);
			
			builder.append("Subscriber Id", subscriberId);
			builder.append("Alternate Id", alternateId);
			builder.append("TopUp Subscription Id", topUpSubscriptionId);
			builder.append("Subscription Status Value", subscriptionStatusValue);
			builder.append("Subscription Status Name", subscriptionStatusName);
			builder.append("Start Time", startTime);
			builder.append("End Time", endTime);
			builder.append("Priority", priority);
			builder.append("Reject Reason", rejectReason);
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

	public Integer getPriority() {
		return priority;
	}
}
