package com.elitecore.aaa.core.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.DHCPKeysConfiguration;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.Reloadable;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;

@XmlType(propOrder = {})
@XmlRootElement(name = "keys")
@ConfigurationProperties(moduleName ="DHCP_KEYS_CONFIGURABLE",synchronizeKey ="KEYS", readWith = XMLReader.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf"},name = "dhcp-keys-conf")
public class DHCPKeysConfigurable extends Configurable implements DHCPKeysConfiguration{

	private boolean dhcpKeysCleanupEnabled =false ;
	private long dhcpKeysCleanupInterval = 600;
	private long dhcpRkThresholdTime = 86400;
	
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
	@XmlElement(name = "dhcp-rk-threshold-time",type = long.class,defaultValue ="86400")
	@Reloadable(type = long.class)
	public long getDhcpRkthresholdTime() {
		return dhcpRkThresholdTime;
	}

	public void setDhcpRkthresholdTime(long dhcpRkThresholdTime){
		this.dhcpRkThresholdTime = dhcpRkThresholdTime;
	}
	
	@Override
	@XmlElement(name = "dhcp-keys-cleanup-enabled",type = boolean.class,defaultValue ="false")
	@Reloadable(type = boolean.class)
	public boolean getIsDhcpKeysCleanupEnabled() {
		return dhcpKeysCleanupEnabled;
	}
	
	public void setIsDhcpKeysCleanupEnabled(boolean dhcpKeysCleanupEnabled){
		this.dhcpKeysCleanupEnabled = dhcpKeysCleanupEnabled;
	}
	
	@Override
	@XmlElement(name = "dhcp-keys-cleanup-interval",type = long.class,defaultValue ="600")
	@Reloadable(type = long.class)
	public long getDhcpKeysCleanupInterval() {
		return dhcpKeysCleanupInterval;
	}
	
	public void setDhcpKeysCleanupInterval(long dhcpKeysCleanupInterval){
		this.dhcpKeysCleanupInterval = dhcpKeysCleanupInterval;
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println("--DHCP Keys Configuration--");
		out.println("------------------------------");
		out.println("DHCP keys cleanup enabled: " + dhcpKeysCleanupEnabled);
		out.println("DHCP keys cleanup interval: " + dhcpKeysCleanupInterval);
		out.println("DHCP RK Threshold time: " + dhcpRkThresholdTime);
		out.close();
		return writer.toString();
	}
}
