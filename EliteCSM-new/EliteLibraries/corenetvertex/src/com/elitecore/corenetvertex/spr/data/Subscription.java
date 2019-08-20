package com.elitecore.corenetvertex.spr.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.corenetvertex.constants.SubscriptionState;


public class Subscription {
	
	private String id;
	private String subscriberIdentity;
	private String packageId;
	private Timestamp startTime;
	private Timestamp endTime;
	private SubscriptionState status;
	private String parameter1;
	private String parameter2;
	private int priority;
	private String productOfferId;
	private SubscriptionType type;
	private SubscriptionMetadata metadata;

	private Subscription(){
	}

	public Subscription(String id,
						String subscriberIdentity, String packageId, String productOfferId, Timestamp startTime, Timestamp endTime,
						SubscriptionState status, int priority, SubscriptionType type, SubscriptionMetadata metadata, String parameter1,
						String parameter2) {
		this.id = id;
		this.subscriberIdentity = subscriberIdentity;
		this.packageId = packageId;
		this.productOfferId = productOfferId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
		this.priority = priority;
		this.type = type;
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.metadata = metadata;
	}

	public Subscription(String id,
						String subscriberIdentity, String packageId, String productOfferId, Timestamp startTime, Timestamp endTime,
						SubscriptionState status, int priority, SubscriptionType type, String parameter1,
						String parameter2) {
		this(id,subscriberIdentity,packageId,productOfferId,startTime,endTime,status,priority,type,null,
				parameter1,parameter2);
	}

	public Subscription(Subscription addOnSubscription) {
		this(null, addOnSubscription.getSubscriberIdentity(),
				addOnSubscription.getPackageId(),
				addOnSubscription.getProductOfferId(),
				addOnSubscription.getStartTime(),
				addOnSubscription.getEndTime(),
				addOnSubscription.getStatus(),
				addOnSubscription.getPriority(),
				addOnSubscription.getType(),
				addOnSubscription.getMetadata(),
				addOnSubscription.getParameter1(),
				addOnSubscription.getParameter2());
	}

	public int getPriority() {
		return priority;
	}

	public void setSubscriberIdentity(String subscriberIdentity) {
		this.subscriberIdentity = subscriberIdentity;
	}

	public String getSubscriberIdentity() {
		return subscriberIdentity;
	}
	
	public String getPackageId() {
		return packageId;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public SubscriptionState getStatus() {
		return status;
	}

	public String getId() {
		return id;
	}
	
	public String getParameter1() {
		return parameter1;
	}

	public String getParameter2() {
		return parameter2;
	}
	
	public boolean isExpired(long currentTimeInMillis) {
		return endTime.getTime() < currentTimeInMillis;
	}
	
	public boolean isExpired(Calendar currentTimeCalendar) {
		return endTime.getTime() < currentTimeCalendar.getTimeInMillis();
	}

	public boolean isFutureSubscription(long currentTimeInMillis){
		return startTime.getTime() > currentTimeInMillis;
	}

	public String getProductOfferId() {
		return productOfferId;
	}

	public void setProductOfferId(String productOfferId) {
		this.productOfferId = productOfferId;
	}

	public SubscriptionType getType() {
		return type;
	}

	public String getFnFGroupName() {
		if(metadata==null || metadata.getFnFGroup()==null){
			return null;
		}
		return metadata.getFnFGroup().getName();
	}

	public List<String> getFnFGroupMembers() {
		if(metadata==null || metadata.getFnFGroup()==null){
			return null;
		}
		return metadata.getFnFGroup().getMembers();
	}

	public SubscriptionMetadata getMetadata() {
		return metadata;
	}

	public static class SubscriptionBuilder{
		
		private Subscription subscription;
		
		public SubscriptionBuilder(){
			this.subscription = new Subscription();
		}
		
		
		public SubscriptionBuilder withSubscriberIdentity(String subscriberIdentity) {
			subscription.subscriberIdentity = subscriberIdentity;
			return this;
		}
		
		public SubscriptionBuilder withPackageId(String packageId) {
			subscription.packageId = packageId;
			return this;
		}

		public SubscriptionBuilder withProductOfferId(String productOfferId) {
			subscription.productOfferId = productOfferId;
			return this;
		}

		public SubscriptionBuilder withStartTime(Timestamp startTime) {
			subscription.startTime = startTime;
			return this;
		}

		public SubscriptionBuilder withEndTime(Timestamp endTime) {
			subscription.endTime = endTime;
			return this;
		}

		public SubscriptionBuilder withStatus(SubscriptionState status) {
			subscription.status = status;
			return this;
		}
		
		public SubscriptionBuilder withId(String id) {
			subscription.id = id;
			return this;
		}

		public SubscriptionBuilder withPriority(int priority) {
			subscription.priority = priority;
			return this;
		}

		public SubscriptionBuilder withType(SubscriptionType type) {
			subscription.type = type;
			return this;
		}

		public SubscriptionBuilder withMetaData(SubscriptionMetadata metaData){
			subscription.metadata = metaData;
			return this;
		}
		
		public Subscription build() {
			return subscription;
		}
		
	}


	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out = new IndentingPrintWriter(new PrintWriter(stringWriter));
		toString(out);
		out.close();
		return stringWriter.toString();
	}

	public void toString(IndentingWriter indentingWriter) {
		indentingWriter.println("Subscription [id=" + id + ", subscriberIdentity=" + subscriberIdentity + ", packageId=" + packageId + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", status=" + status
				+ ", priority=" + priority
				+ ", product offer Id=" + productOfferId
				+ ", Type=" + type
				+ ", parameter1=" + parameter1 + ", parameter2=" + parameter2 + "]");
		if(metadata!=null){
			metadata.toString(indentingWriter);
		}
	}

}
