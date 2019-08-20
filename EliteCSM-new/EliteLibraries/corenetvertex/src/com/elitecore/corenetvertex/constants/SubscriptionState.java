package com.elitecore.corenetvertex.constants;

/*
 * Subscription states are used to identify various subscription state.
 * BoD and AddOn scheduler uses following subscription states.
 * 
 * State values are used in Trigger 'PROMOTIONALSUBHISTORY'
 * So, any change made in State value must be reflected in Trigger. 
 *  
 * This Trigger maintains Subscription History.
 * Subscription History is displayed in SSP.
 */

public enum SubscriptionState {

	SUBSCRIBED(0  , "Subscribed"),
	START_SCHEDULED(1  , "Start Scheduled"),
	STARTED(2 , "Active"),
	EXPIRY_SCHEDULED(3  , "Expiry Scheduled"),
	EXPIRED(4 , "Expired"), 
	UNSUBSCRIBED(5 , "Unsubscribed"),


	// status regarding parental SSP and enterprise SSP 
	APPROVAL_PENDING(6 , "Approval Pending"),
	REJECTED(7 , "Rejected"),
	UNPROCESSED(8 , "Unprocessed"),
	;

	public final int state;
	public final String name;

	private SubscriptionState(int state , String name){
		this.state = state;
		this.name = name;
	}

	public int getVal(){
		return state;
	}

	public String getStringVal() {
		return String.valueOf(state);
	}

	public String getName() {
		return name;
	}

	public static SubscriptionState fromValue(int state) {
		if (SUBSCRIBED.state == state) {
			return SUBSCRIBED;
		} else if (START_SCHEDULED.state == state) {
			return START_SCHEDULED;
		} else if (STARTED.state == state) {
			return STARTED;
		} else if (EXPIRY_SCHEDULED.state == state) {
			return EXPIRY_SCHEDULED;
		} else if (EXPIRED.state == state) {
			return EXPIRED;
		}else if (UNSUBSCRIBED.state == state) {
			return UNSUBSCRIBED;
		}else if (REJECTED.state == state) {
			return REJECTED;
		}else if (APPROVAL_PENDING.state == state) {
			return APPROVAL_PENDING;
		}else if(UNPROCESSED.state == state){
			return UNPROCESSED;
		}
		return null;
	}

	public static SubscriptionState fromStringValue(String subscriptionStatus) {
		if(subscriptionStatus == null){
			return null;
		}
		
		subscriptionStatus = subscriptionStatus.trim();
		try{
			return fromValue(Integer.parseInt(subscriptionStatus));
		}catch(NumberFormatException e){
			return null;
		}
	}

	public static SubscriptionState fromName(String subscriptionStatus) {
		if(subscriptionStatus == null){
			return null;
		}
		subscriptionStatus = subscriptionStatus.trim();
		if (SUBSCRIBED.name.equalsIgnoreCase(subscriptionStatus)) {
			return SUBSCRIBED;
		}else if (START_SCHEDULED.name.equalsIgnoreCase(subscriptionStatus)) {
			return START_SCHEDULED;
		} else if (STARTED.name.equalsIgnoreCase(subscriptionStatus)) {
			return STARTED;
		}else if (EXPIRY_SCHEDULED.name.equalsIgnoreCase(subscriptionStatus)) {
			return EXPIRY_SCHEDULED;
		}else if (EXPIRED.name.equalsIgnoreCase(subscriptionStatus)) {
			return EXPIRED;
		}else if (UNSUBSCRIBED.name.equalsIgnoreCase(subscriptionStatus)) {
			return UNSUBSCRIBED;
		}else if (APPROVAL_PENDING.name.equalsIgnoreCase(subscriptionStatus)) {
			return APPROVAL_PENDING;
		}else if (REJECTED.name.equalsIgnoreCase(subscriptionStatus)) {
			return REJECTED;
		}else if(UNPROCESSED.name.equalsIgnoreCase(subscriptionStatus)){
			return UNPROCESSED;
		}
		return null;
	}
}