/**
 * 
 */
package com.elitecore.ssp.util.constants;


/**
 * @author aditya
 *
 */
public enum ApprovalStates {
	
	APPROVAL_PENDING(6 , "Approval Pending"),
	REJECTED(7 , "Rejected"),
    APPROVED(8,"Approved");
    
	
	public final int state;
	public final String value;
   
	private ApprovalStates(int state,String value){
		this.state=state;
		this.value=value;
	}
	
	public int getState(){
		return state;
	}

	
	public String getValue() {
		return value;
	}
	

	public String getStringVal() {
		return String.valueOf(state);
	}

	
	public static ApprovalStates fromValue(int state) {
		switch (state) {
		case 6:
			return APPROVAL_PENDING;
		case 7: 
			return REJECTED;
		case 8:
			return APPROVED;

		default:
			return  null;
		} 
	}
		
	public static ApprovalStates fromName(String subscriptionStatus) {
		if(subscriptionStatus == null){
			return null;
		}
		subscriptionStatus = subscriptionStatus.trim();
		if(APPROVAL_PENDING.value.equalsIgnoreCase(subscriptionStatus) )
			return APPROVAL_PENDING;
		else if (APPROVED.value.equalsIgnoreCase(subscriptionStatus)) {
			return APPROVED;
		}else if (REJECTED.value.equalsIgnoreCase(subscriptionStatus))
	     	return REJECTED;
		else 
			return null;
	}

}
