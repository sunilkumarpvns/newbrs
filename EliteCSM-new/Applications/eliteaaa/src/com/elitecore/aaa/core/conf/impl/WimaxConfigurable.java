package com.elitecore.aaa.core.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.WimaxConfiguration;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.Reloadable;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;

@XmlType(propOrder = {})
@XmlRootElement(name = "wimax")
@ConfigurationProperties(moduleName="WIMAX_CONFIGURABLE",synchronizeKey ="WiMAX", readWith = XMLReader.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf"},name = "wimax-conf")
public class WimaxConfigurable extends Configurable implements WimaxConfiguration  {

	private int accountingCapabilities = 1;
	private int idleModeNotificationCapabilities = 0;
	private int haRkLifetimeInSeconds = 86400;
	private int dhcpRkLifetimeInSeconds = 86400;
		
	@XmlElement(name = "accounting-capabilities",type = int.class,defaultValue ="1")
	@Reloadable(type = int.class)
	public int getAccountingCapabilities() {
		return accountingCapabilities;
	}

	public void setAccountingCapabilities(int accountingCapabilities) {
		this.accountingCapabilities = accountingCapabilities;
	}

	@XmlElement(name = "idle-mode-notification-capabilities",type = int.class,defaultValue ="0")
	@Reloadable(type = int.class)
	public int getIdleModeNotificationCapabilities() {
		return idleModeNotificationCapabilities;
	}

	public void setIdleModeNotificationCapabilities(int idleModeNotificationCapabilities) {
		this.idleModeNotificationCapabilities = idleModeNotificationCapabilities;
	}

	@XmlElement(name = "ha-rk-lifetime",type = int.class,defaultValue ="86400")
	@Reloadable(type = int.class)
	public int getHaRkLifetimeInSeconds() {
		return haRkLifetimeInSeconds;
	}
	
	public void setHaRkLifetimeInSeconds(int haRkLifetimeInSeconds) {
		this.haRkLifetimeInSeconds = haRkLifetimeInSeconds;
	}

	@XmlElement(name = "dhcp-rk-lifetime",type = int.class,defaultValue ="86400")
	@Reloadable(type = int.class)
	public int getDhcpRkLifetimeInSeconds() {
		return dhcpRkLifetimeInSeconds;
	}

	public void setDhcpRkLifetimeInSeconds(int dhcpRkLifetimeInSeconds) {
		this.dhcpRkLifetimeInSeconds = dhcpRkLifetimeInSeconds;
	}
	
	@PostRead
	public void postReadProcessing() {
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println("--Wimax Configuration--");
		out.println("------------------------------");
		out.println("Accounting Capabilities: " + accountingCapabilities);
		out.println("Idle Mode Notification Capabilities: " + idleModeNotificationCapabilities);
		out.println("DHCP RK Lifetime: " + dhcpRkLifetimeInSeconds);
		out.println("HA RK Lifetime: " + haRkLifetimeInSeconds);
		out.close();
		return writer.toString();
	}
}
