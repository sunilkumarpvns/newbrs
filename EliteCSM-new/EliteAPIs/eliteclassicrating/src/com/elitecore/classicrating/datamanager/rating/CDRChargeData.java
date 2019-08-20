/**
 * 
 */
package com.elitecore.classicrating.datamanager.rating;

import java.sql.Timestamp;

/**
 * @author sheetalsoni
 *
 */
public class CDRChargeData {
	private int cdrId;   
	private long charge;
	private String customerName;
	private String customerIdentifier;
	private long sessionTime;
	private long acctInputOctets;
	private long acctOutputOctets;
	private String calledStationId;
	private Timestamp callStart;
	private Timestamp callEnd;
	private String eventType; 
	private String serviceType;
	private String nasIp;
	private Timestamp chargedDate;
	private long volume;
    private double usageCost;
    private String sessionId;

	
	
	public Timestamp getChargedDate() {
		return chargedDate;
	}

    public double getUsageCost() {
        return usageCost;
    }
    
    public void setUsageCost(double usageCost){
        this.usageCost=usageCost;
    }
	public void setChargedDate(Timestamp chargedDate) {
		this.chargedDate = chargedDate;
	}
	public int getCdrId() {
		return cdrId;
	}
	public void setCdrId(int cdrId) {
		this.cdrId = cdrId;
	}
	public long getCharge() {
		return charge;
	}
	public void setCharge(long charge) {
		this.charge = charge;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerIdentifier() {
		return customerIdentifier;
	}
	public void setCustomerIdentifier(String customerIdentifier) {
		this.customerIdentifier = customerIdentifier;
	}
	public long getSessionTime() {
		return sessionTime;
	}
	public void setSessionTime(long sessionTime) {
		this.sessionTime = sessionTime;
	}
	public long getAcctInputOctets() {
		return acctInputOctets;
	}
	public void setAcctInputOctets(long acctInputOctets) {
		this.acctInputOctets = acctInputOctets;
	}
	public long getAcctOutputOctets() {
		return acctOutputOctets;
	}
	public void setAcctOutputOctets(long acctOutputOctets) {
		this.acctOutputOctets = acctOutputOctets;
	}
	public String getCalledStationId() {
		return calledStationId;
	}
	public void setCalledStationId(String calledStationId) {
		this.calledStationId = calledStationId;
	}
	public Timestamp getCallStart() {
		return callStart;
	}
	public void setCallStart(Timestamp callStart) {
		this.callStart = callStart;
	}
	public Timestamp getCallEnd() {
		return callEnd;
	}
	public void setCallEnd(Timestamp callEnd) {
		this.callEnd = callEnd;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getNasIp() {
		return nasIp;
	}
	public void setNasIp(String nasIp) {
		this.nasIp = nasIp;
	}
	public long getVolume() {
		return volume;
	}
	public void setVolume(long volume) {
		this.volume = volume;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
}
