/**
 *
 */
package com.elitecore.classicrating.commons.data;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.elitecore.classicrating.api.IRequestParameters;
import com.elitecore.classicrating.base.IBaseConstant;

/**
 * @author sheetalsoni
 *
 */
public class CDRData {

    private int CDRId;
    private long sessionTime;
    private long acctInputOctets;
    private long acctOutputOctets;
    private long volume;
    private double usageCost;
    private String customerName;
    private String customerIdentifier;
    private String calledStationId;
    private String eventType;
    private String serviceType;
    private Timestamp callStart;
    private Timestamp callEnd;
    private String nasIPAddress;
    private String sessionId;

    public CDRData() {
    }

    /**
     *  Creates an instance of CDR Data by using the requestParameters
     * @param requestParameters
     */
    public CDRData(IRequestParameters requestParameters) throws ParseException {

        doNormalisation(requestParameters);
    }
    
    public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
    public int getCDRId() {
        return CDRId;
    }

    public String toString() {
        return new String("CDR Data = [ " + "I/P Octets = " + acctInputOctets + ", " + "O/P Octets = " + acctOutputOctets + ", " + "Call-End = " + callEnd + ", " + "Call-Start = " + callStart + ", " + "Called-Station-Id = " + calledStationId + ", " + "Customer-Id = " + customerIdentifier + ", " + "Customer Name = " + customerName + ", " + "Event Type = " + eventType + ", " + "Service Type = " + serviceType + ", " + "Session Time = " + sessionTime + ", " + "Usage Cost = " + usageCost + ", " + "Volume = " + volume + " ]");
    }

    public void setCDRId(int id) {
        this.CDRId = id;
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

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public double getUsageCost() {
        return usageCost;
    }

    public void setUsageCost(double usageCost) {
        this.usageCost = usageCost;
    }

    public String getNASIPAddress() {
        return nasIPAddress;
    }

    public void setNASIPAddress(String nasIPAddress) {
        this.nasIPAddress = nasIPAddress;
    }

    public void doNormalisation(IRequestParameters request) throws ParseException, NumberFormatException {


        this.setCustomerIdentifier(request.get(IBaseConstant.USERID));
        this.setServiceType(request.get(IBaseConstant.SERVICE_TYPE));
        this.setEventType(request.get(IBaseConstant.EVENT_TYPE));
        this.setVolume(0);
        
        if (request.get(IBaseConstant.ACCT_SESSION_ID) != null) {
            this.setSessionId(request.get(IBaseConstant.ACCT_SESSION_ID));
        }
        
        if (request.get(IBaseConstant.CALL_START) != null) {
            this.setCallStart(convertStringToTimestamp(request.get(IBaseConstant.CALL_START)));
        }

        if (request.get(IBaseConstant.CALL_END) != null) {
            this.setCallEnd(convertStringToTimestamp(request.get(IBaseConstant.CALL_END)));
        }

        if (request.get(IBaseConstant.ACCT_SESSION_TIME) != null) {
            this.setSessionTime(Long.parseLong(request.get(IBaseConstant.ACCT_SESSION_TIME)));
        }

        if (this.getServiceType().equalsIgnoreCase(IBaseConstant.DATA)) {
            if (request.get(IBaseConstant.ACCT_INPUT_OCTETS) != null) {
                this.setAcctInputOctets(Long.parseLong(request.get(IBaseConstant.ACCT_INPUT_OCTETS)));
                this.setVolume(this.getVolume() + this.getAcctInputOctets());
            }

            if (request.get(IBaseConstant.ACCT_OUTPUT_OCTETS) != null) {
                this.setAcctOutputOctets(Long.parseLong(request.get(IBaseConstant.ACCT_OUTPUT_OCTETS)));
                this.setVolume(this.getVolume() + this.getAcctOutputOctets());
            }
        }

        if (this.getServiceType().equalsIgnoreCase(IBaseConstant.VOIP)) {
            if (request.get(IBaseConstant.CALLED_STATION_ID) != null) {
                this.setCalledStationId(request.get(IBaseConstant.CALLED_STATION_ID));
            }
       }

    }

    public Timestamp convertStringToTimestamp(String stringDate) throws ParseException {
        final SimpleDateFormat simpleDateFormatter_1 = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
        final SimpleDateFormat simpleDateFormatter_2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final SimpleDateFormat simpleDateFormatter_3 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date dateValue = null;
        
        try{
        	dateValue = simpleDateFormatter_1.parse(stringDate);
        }catch(ParseException ex1){
        	
        	  try{
        		  dateValue = simpleDateFormatter_2.parse(stringDate);
        	  }catch(ParseException ex2){
        		  try{
        			  dateValue = simpleDateFormatter_3.parse(stringDate);
            	  }catch(ParseException ex3){
            		  throw ex3;
        		  }
        	  }
        }
        Timestamp timestamp = new Timestamp(dateValue.getTime());
        return timestamp;
    }
}
