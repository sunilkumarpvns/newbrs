package com.elitecore.netvertexsm.datamanager.subscriptions.data;



public class AddOnOCSBSSRelSubscriptionData {

	private Long addOnSubscriptionID;
	private Long addOnPackageID;
	private String addOnPackageName;
	
	private String subscriberID;
	private String parentID;
	
	private Long subscriptionTime;
	private Long subscriptionStartTime;
	private Long subscriptionEndTime;
	private String subscriptionStatusValue;
	private String subscriptionStatusName;
	private String rejectReason;
	private Long lastUpdateTime;
	private String ocsBssRelationID;
	private Long hsqValue;
	private Long   balance;
	
	private String ocsCorrelationID;
	private String bssCorrelationID;
	/**
	 * @return the addOnSubscriptionID
	 */
	public Long getAddOnSubscriptionID() {
		return addOnSubscriptionID;
	}
	/**
	 * @return the addOnPackageID
	 */
	public Long getAddOnPackageID() {
		return addOnPackageID;
	}
	/**
	 * @return the addOnPackageName
	 */
	public String getAddOnPackageName() {
		return addOnPackageName;
	}
	/**
	 * @return the subscriberID
	 */
	public String getSubscriberID() {
		return subscriberID;
	}
	/**
	 * @return the parentID
	 */
	public String getParentID() {
		return parentID;
	}
	/**
	 * @return the subscriptionTime
	 */
	public Long getSubscriptionTime() {
		return subscriptionTime;
	}
	/**
	 * @return the subscriptionStartTime
	 */
	public Long getSubscriptionStartTime() {
		return subscriptionStartTime;
	}
	/**
	 * @return the subscriptionEndTime
	 */
	public Long getSubscriptionEndTime() {
		return subscriptionEndTime;
	}
	/**
	 * @return the subscriptionStatusValue
	 */
	public String getSubscriptionStatusValue() {
		return subscriptionStatusValue;
	}
	/**
	 * @return the subscriptionStatusName
	 */
	public String getSubscriptionStatusName() {
		return subscriptionStatusName;
	}
	/**
	 * @return the rejectReason
	 */
	public String getRejectReason() {
		return rejectReason;
	}
	/**
	 * @return the lastUpdateTime
	 */
	public Long getLastUpdateTime() {
		return lastUpdateTime;
	}
	/**
	 * @return the ocsBssRelationID
	 */
	public String getOcsBssRelationID() {
		return ocsBssRelationID;
	}
	/**
	 * @return the hsqValue
	 */
	public Long getHsqValue() {
		return hsqValue;
	}
	/**
	 * @return the balance
	 */
	public Long getBalance() {
		return balance;
	}
	/**
	 * @param addOnSubscriptionID the addOnSubscriptionID to set
	 */
	public void setAddOnSubscriptionID(Long addOnSubscriptionID) {
		this.addOnSubscriptionID = addOnSubscriptionID;
	}
	/**
	 * @param addOnPackageID the addOnPackageID to set
	 */
	public void setAddOnPackageID(Long addOnPackageID) {
		this.addOnPackageID = addOnPackageID;
	}
	/**
	 * @param addOnPackageName the addOnPackageName to set
	 */
	public void setAddOnPackageName(String addOnPackageName) {
		this.addOnPackageName = addOnPackageName;
	}
	/**
	 * @param subscriberID the subscriberID to set
	 */
	public void setSubscriberID(String subscriberID) {
		this.subscriberID = subscriberID;
	}
	/**
	 * @param parentID the parentID to set
	 */
	public void setParentID(String parentID) {
		this.parentID = parentID;
	}
	/**
	 * @param subscriptionTime the subscriptionTime to set
	 */
	public void setSubscriptionTime(Long subscriptionTime) {
		this.subscriptionTime = subscriptionTime;
	}
	/**
	 * @param subscriptionStartTime the subscriptionStartTime to set
	 */
	public void setSubscriptionStartTime(Long subscriptionStartTime) {
		this.subscriptionStartTime = subscriptionStartTime;
	}
	/**
	 * @param subscriptionEndTime the subscriptionEndTime to set
	 */
	public void setSubscriptionEndTime(Long subscriptionEndTime) {
		this.subscriptionEndTime = subscriptionEndTime;
	}
	/**
	 * @param subscriptionStatusValue the subscriptionStatusValue to set
	 */
	public void setSubscriptionStatusValue(String subscriptionStatusValue) {
		this.subscriptionStatusValue = subscriptionStatusValue;
	}
	/**
	 * @param subscriptionStatusName the subscriptionStatusName to set
	 */
	public void setSubscriptionStatusName(String subscriptionStatusName) {
		this.subscriptionStatusName = subscriptionStatusName;
	}
	/**
	 * @param rejectReason the rejectReason to set
	 */
	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}
	/**
	 * @param lastUpdateTime the lastUpdateTime to set
	 */
	public void setLastUpdateTime(Long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	/**
	 * @param ocsBssRelationID the ocsBssRelationID to set
	 */
	public void setOcsBssRelationID() {
		this.ocsBssRelationID = this.ocsBssRelationID+"-"+this.bssCorrelationID;
	}
	/**
	 * @param hsqValue the hsqValue to set
	 */
	public void setHsqValue(Long hsqValue) {
		this.hsqValue = hsqValue;
	}
	/**
	 * @param balance the balance to set
	 */
	public void setBalance(Long balance) {
		this.balance = balance;
	}
	/**
	 * @return the ocsCorrelationID
	 */
	public String getOcsCorrelationID() {
		return ocsCorrelationID;
	}
	/**
	 * @return the bssCorrelationID
	 */
	public String getBssCorrelationID() {
		return bssCorrelationID;
	}
	/**
	 * @param ocsCorrelationID the ocsCorrelationID to set
	 */
	public void setOcsCorrelationID(String ocsCorrelationID) {
		this.ocsCorrelationID = ocsCorrelationID;
	}
	/**
	 * @param bssCorrelationID the bssCorrelationID to set
	 */
	public void setBssCorrelationID(String bssCorrelationID) {
		this.bssCorrelationID = bssCorrelationID;
	} 
	
/*	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		
		if (obj == null){
			return false;
		}
		
		if (getClass() != obj.getClass()){
			return false;
		}
		
		AddOnOCSBSSData other = (AddOnOCSBSSData) obj;
		if (bssCorelation != other.bssCorelation){
			return false;
		}
		return ocsCorelation.equals(other.ocsCorelation);
		}
	*/
	/*@Override
	public String toString() {
		return "AddOnOCSBSSData [ocsCorelation=" + ocsCorelation
				+ ", bssCorelation=" + bssCorelation + "]";
	}*/
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((ocsBssRelationID == null) ? 0 : ocsBssRelationID.hashCode());
		result = prime
				* result
				+ ((ocsCorrelationID == null) ? 0 : ocsCorrelationID.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AddOnOCSBSSRelSubscriptionData other = (AddOnOCSBSSRelSubscriptionData) obj;
		if (ocsBssRelationID == null) {
			if (other.ocsBssRelationID != null)
				return false;
		} else if (!ocsBssRelationID.equals(other.ocsBssRelationID))
			return false;
		if (ocsCorrelationID == null) {
			if (other.ocsCorrelationID != null)
				return false;
		} else if (!ocsCorrelationID.equals(other.ocsCorrelationID))
			return false;
		return true;
	}
	
}
