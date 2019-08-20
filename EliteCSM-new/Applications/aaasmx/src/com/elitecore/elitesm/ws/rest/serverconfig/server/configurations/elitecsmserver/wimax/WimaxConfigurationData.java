package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.wimax;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.elitesm.util.constants.RestValidationMessages;

@XmlType(propOrder = {"accountingCapabilities","idleModeNotificationCapabilities","haRkLifetimeInSeconds","dhcpRkLifetimeInSeconds"})
@XmlRootElement(name = "wimax")
public class WimaxConfigurationData{

	@NotEmpty(message="Accounting Capabilities must be specified")
	private String accountingCapabilities;
	
	@NotEmpty(message="Idle Mode Notification Capabilities must be specified")
	private String idleModeNotificationCapabilities;
	private String haRkLifetimeInSeconds;
	private String dhcpRkLifetimeInSeconds;
		
	public WimaxConfigurationData(){
		setHaRkLifetimeInSeconds("172800");
		setDhcpRkLifetimeInSeconds("172800");
	}
	
	@XmlElement(name = "accounting-capabilities")
	@Pattern(regexp=RestValidationMessages.REGEX_ACC_CAPABILITIES,message="Invalid value of Accounting Capabilities(Flow-based accounting / IP-session-based accounting )")
	public String getAccountingCapabilities() {
		return accountingCapabilities;
	}

	public void setAccountingCapabilities(String accountingCapabilities) {
		this.accountingCapabilities = accountingCapabilities;
	}

	@XmlElement(name = "idle-mode-notification-capabilities")
	@Pattern(regexp=RestValidationMessages.REGEX_NOFITICATION_CAPABILITIES,message="Invalid value of Idle Mode Notification Capabilities(Not Required / Required )")
	public String getIdleModeNotificationCapabilities() {
		return idleModeNotificationCapabilities;
	}

	public void setIdleModeNotificationCapabilities(String idleModeNotificationCapabilities) {
		this.idleModeNotificationCapabilities = idleModeNotificationCapabilities;
	}

	@XmlElement(name = "ha-rk-lifetime")
	@Pattern(regexp=RestValidationMessages.REGEX_NUMERIC,message="HA RK Lifetime value must be numeric")
	public String getHaRkLifetimeInSeconds() {
		return haRkLifetimeInSeconds;
	}
	
	public void setHaRkLifetimeInSeconds(String haRkLifetimeInSeconds) {
		this.haRkLifetimeInSeconds = haRkLifetimeInSeconds;
	}

	@XmlElement(name = "dhcp-rk-lifetime")
	@Pattern(regexp=RestValidationMessages.REGEX_NUMERIC,message="DHCP RK Lifetime value must be numeric")
	public String getDhcpRkLifetimeInSeconds() {
		return dhcpRkLifetimeInSeconds;
	}

	public void setDhcpRkLifetimeInSeconds(String dhcpRkLifetimeInSeconds) {
		this.dhcpRkLifetimeInSeconds = dhcpRkLifetimeInSeconds;
	}
	
}

