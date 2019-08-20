package com.elitecore.aaa.core.conf.impl;

import com.elitecore.aaa.core.conf.AAATrapAlertHandlerConfiguration;

public class AAATrapAlertHandlerConfigurationImpl implements AAATrapAlertHandlerConfiguration {

	private String name;	
	private String comunity;
	private String ip;
	private int port;
	private int trapVersion;
	
	public String getComunity() {
	
		return this.comunity;
	}

	public String getIP() {
		
		return this.ip;
	}

	public String getName() {

		return this.name;
	}

	
	public int getPort() {

		return this.port;
	}

	
	public int getTrapVersion() {

		return this.trapVersion;
	}


	public void setComunity(String comunity) {
        this.comunity = comunity;		
	}
	
	public void setIP(String ip) {
		
		this.ip = ip;
	}

	
	public void setName(String name) {

		this.name = name;
	}

	
	public void setPort(int port) {

		this.port = port;
	}

	
	public void setTrapVersion(int trapVersion) {
		
		this.trapVersion = trapVersion;
	}

}
