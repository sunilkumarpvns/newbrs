package com.elitecore.core.notification.sms.smpp;

import org.jsmpp.bean.MessageMode;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.TypeOfNumber;

/**
 * @author Manjil Purohit
 *
 */
public class SMPPConfiguration {
	
	private String userName;
	private String password;
	private String connectionURL;
	private String sourceAddress;
	private int timeout = 3000;			// in milliseconds
	private int maxTimeoutCount = 100;
	private int validityPeriod = 1;		// in hours
	private String systemType;
	private MessageMode messageMode = MessageMode.STORE_AND_FORWARD;
	private TypeOfNumber srcAddrTON = TypeOfNumber.UNKNOWN;
	private NumberingPlanIndicator srcAddrNPI = NumberingPlanIndicator.UNKNOWN;
	private TypeOfNumber destAddrTON = TypeOfNumber.UNKNOWN;
	private NumberingPlanIndicator destAddrNPI = NumberingPlanIndicator.UNKNOWN;
	
	public SMPPConfiguration(String userName, String password, String connectionURL, String sourceAddress, int timeout, int maxTimeoutCount, 
			int validityPeriod, String systemType, String messageMode, String srcAddrTON, String srcAddrNPI, String destAddrTON, String destAddrNPI) {
		this.userName = userName;
		this.password = password;
		this.connectionURL = connectionURL;
		this.sourceAddress = sourceAddress;
		this.systemType = systemType;
		this.timeout = timeout > 0 ? timeout : this.timeout; 
		this.maxTimeoutCount = maxTimeoutCount != 0 ? maxTimeoutCount : this.maxTimeoutCount;
		this.validityPeriod = validityPeriod > 0 ? validityPeriod : this.validityPeriod;
		
		if(messageMode != null) {
			MessageMode tempMessageMode = MessageMode.valueOf(messageMode);
			if(tempMessageMode != null) {
				this.messageMode = tempMessageMode;
			}
		}
		
		if(srcAddrTON != null) {
			TypeOfNumber tempSrcAddrTON = TypeOfNumber.valueOf(srcAddrTON);
			if(tempSrcAddrTON != null) {
				this.srcAddrTON = tempSrcAddrTON;
			}
		}
		
		if(srcAddrNPI != null) {
			NumberingPlanIndicator tempSrcAddrNPI = NumberingPlanIndicator.valueOf(srcAddrNPI);
			if(tempSrcAddrNPI != null) {
				this.srcAddrNPI = tempSrcAddrNPI;
			}
		}
		
		if(destAddrTON != null) {
			TypeOfNumber tempDestAddrTON = TypeOfNumber.valueOf(destAddrTON);
			if(tempDestAddrTON != null) {
				this.destAddrTON = tempDestAddrTON;
			}
		}
		
		if(destAddrNPI != null) {
			NumberingPlanIndicator tempDestAddrNPI = NumberingPlanIndicator.valueOf(destAddrNPI);
			if(tempDestAddrNPI != null) {
				this.destAddrNPI = tempDestAddrNPI;
			}
		}
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}
	
	public String getConnectionURL() {
		return connectionURL;
	}

	public String getSourceAddress() {
		return sourceAddress;
	}

	public int getTimeout() {
		return timeout;
	}

	public int getMaxTimeoutCount() {
		return maxTimeoutCount;
	}

	public int getValidityPeriod() {
		return validityPeriod;
	}

	public String getSystemType() {
		return systemType;
	}

	public MessageMode getMessageMode() {
		return messageMode;
	}

	public TypeOfNumber getSourceAddrTON() {
		return srcAddrTON;
	}

	public NumberingPlanIndicator getSourceAddrNPI() {
		return srcAddrNPI;
	}

	public TypeOfNumber getDestAddrTON() {
		return destAddrTON;
	}

	public NumberingPlanIndicator getDestAddrNPI() {
		return destAddrNPI;
	}

}
