package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.dhcpkey;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.elitesm.util.constants.RestValidationMessages;

@XmlType(propOrder = {"dhcpRkThresholdTime","dhcpKeysCleanupInterval","dhcpKeysCleanupEnabled"})
@XmlRootElement(name = "keys")
public class DHCPKeyConfigurationData{

	@NotEmpty(message="DHCP Keys Cleanup Enables must be specified")
	private String dhcpKeysCleanupEnabled;
	private String dhcpKeysCleanupInterval;
	private String dhcpRkThresholdTime;

	public DHCPKeyConfigurationData() {
		setDhcpKeysCleanupInterval("86400");
		setDhcpRkThresholdTime("600");
	}
	
	@XmlElement(name = "dhcp-rk-threshold-time")
	@Pattern(regexp=RestValidationMessages.REGEX_NUMERIC,message="DHCP RK Threshold Time must be numeric")
	public String getDhcpRkThresholdTime() {
		return dhcpRkThresholdTime;
	}

	public void setDhcpRkThresholdTime(String dhcpRkThresholdTime){
		this.dhcpRkThresholdTime = dhcpRkThresholdTime;
	}
	
	@XmlElement(name = "dhcp-keys-cleanup-enabled")
	@Pattern(regexp=RestValidationMessages.REGEX_TRUE_FALSE,message="Invalid value of DHCP Key Cleanup Enabled(only true/false allow)")
	public String getDhcpKeysCleanupEnabled() {
		return dhcpKeysCleanupEnabled;
	}
	
	public void setDhcpKeysCleanupEnabled(String dhcpKeysCleanupEnabled){
		this.dhcpKeysCleanupEnabled = dhcpKeysCleanupEnabled.toLowerCase();
	}
	
	@XmlElement(name = "dhcp-keys-cleanup-interval")
	@Pattern(regexp=RestValidationMessages.REGEX_NUMERIC,message="DHCP Keys Cleanup Interval must be numeric")
	public String getDhcpKeysCleanupInterval() {
		return dhcpKeysCleanupInterval;
	}
	
	public void setDhcpKeysCleanupInterval(String dhcpKeysCleanupInterval){
		this.dhcpKeysCleanupInterval = dhcpKeysCleanupInterval;
	}
	
}
