package com.elitecore.aaa.radius.policies.servicepolicy.conf;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "supported-messages")
public class SupportedMessages {
	private boolean authenticationMessageEnabled;
	private boolean accountingMessageEnabled;

	@XmlElement(name = "authentication-message")
	public boolean isAuthenticationMessageEnabled() {
		return authenticationMessageEnabled;
	}
	public void setAuthenticationMessageEnabled(boolean authenticationMessageEnabled) {
		this.authenticationMessageEnabled = authenticationMessageEnabled;
	}

	@XmlElement(name = "accounting-message")
	public boolean isAccountingMessageEnabled() {
		return accountingMessageEnabled;
	}
	public void setAccountingMessageEnabled(boolean accountingMessageEnabled) {
		this.accountingMessageEnabled = accountingMessageEnabled;
	}
}
