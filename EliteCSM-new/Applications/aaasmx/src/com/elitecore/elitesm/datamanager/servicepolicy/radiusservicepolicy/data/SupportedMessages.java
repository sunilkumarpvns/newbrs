package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "supported-messages")
public class SupportedMessages {
	
	private String authenticationMessageEnabled;
	private String accountingMessageEnabled;
	
	@XmlElement(name = "authentication-message", defaultValue = "false")
	@Pattern(regexp = "true|false", message="Invalid Authentication Message. Value could be 'true' and 'false'")
	public String getAuthenticationMessageEnabled() {
		return authenticationMessageEnabled;
	}
	
	public void setAuthenticationMessageEnabled(String authenticationMessageEnabled) {
		this.authenticationMessageEnabled = authenticationMessageEnabled;
	}
	
	@XmlElement(name = "accounting-message", defaultValue = "false")
	@Pattern(regexp = "true|false", message="Invalid Accounting Message. Value could be 'true' and 'false'")

	public String getAccountingMessageEnabled() {
		return accountingMessageEnabled;
	}
	public void setAccountingMessageEnabled(String accountingMessageEnabled) {
		this.accountingMessageEnabled = accountingMessageEnabled;
	}
}
