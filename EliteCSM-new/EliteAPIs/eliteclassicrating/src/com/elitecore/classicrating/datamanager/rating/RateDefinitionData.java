/**
 * 
 */
package com.elitecore.classicrating.datamanager.rating;

/**
 * @author sheetalsoni
 *
 */
public class RateDefinitionData {
	
	private int packageId; 
	private String serviceType; 
	private String prefixGroup; 
	private String customerSubType;
	private String eventType;   
	private double rate;
	private long sessionTime;
	private String version;
	
	public int getPackageId() {
		return packageId;
	}
	public void setPackageId(int packageId) {
		this.packageId = packageId;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getPrefixGroup() {
		return prefixGroup;
	}
	public void setPrefixGroup(String prefixGroup) {
		this.prefixGroup = prefixGroup;
	}
	public String getCustomerSubType() {
		return customerSubType;
	}
	public void setCustomerSubType(String customerSubType) {
		this.customerSubType = customerSubType;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	public long getSessionTime() {
		return sessionTime;
	}
	public void setSessionTime(long sessionTime) {
		this.sessionTime = sessionTime;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getEventType() {
		return eventType;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Package ID = " + packageId)
		.append("  Service Type = " + packageId)
		.append("  Prefix = " + prefixGroup)
		.append("  SubType = " + customerSubType)
		.append("  Version = " + version)
		.append("  EventType= " + eventType);
		
		return sb.toString();
	}
	
	
	
}
