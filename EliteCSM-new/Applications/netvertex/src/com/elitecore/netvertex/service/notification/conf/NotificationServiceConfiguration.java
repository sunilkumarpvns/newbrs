package com.elitecore.netvertex.service.notification.conf;


public interface NotificationServiceConfiguration {



	
	/**
	 * This method return the no of records to be processed
	 * @return 
	 */
	public int getNotificationBatchSize();
	

	public long getInitialDelay();
	
	public long getServiceExecutionPeriod();
	
	public int getMaxParallelExecution();

	
	public boolean isEmailNotificationEnabled();

	public boolean isAuthRequired();
	
	public String getMailFrom();

	public String getEmailHost();

	public int getEmailPort();

	public String getEmailUserName();

	public String getEmailPassword();

	
	public boolean isSMSNotificationEnabled();

	public String getSMSServiceURL();

	public String getSMSSender();

	public String getSMSUserName();

	public String getSMSPassword();

	public String getDeliveryURL();

	public String isFlashSMS();
	
	public String getSMSParam1();
	
	public String getSMSParam2();
	
	public String getSMSParam3();

	public String getSMSProtocol();
	
	//	SMPP related methods
	public String getUserName();
	public String getSMPPPassword();
	public String getSMSCConnectionURL();
	public int getTransactionTimeout();
	public int getMaxTimeoutCount();
	public String getSystemType();
	public String getMessageMode();
	public int getValidityPeriod();
	public String getSourceAddress();
	public String getSourceAddrTON();
	public String getSourceAddrNPI();
	public String getDestAddrTON();
	public String getDestAddrNPI();
	
}
