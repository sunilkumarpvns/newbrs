package com.elitecore.aaa.diameter.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;



@XmlType(propOrder={})
public class PeerConnectionParameters {

	private TransportProtocols transportProtocol;
	private int socketReceiveBuffetSize;
	private int socketSendBuffetSize;
	private boolean isTCPNagleAlgoEnabled;
	private int dwrDuration;
	private int initConnectionDuration;
	private boolean sendDPREvent;
	private SecurityStandard securityStandard;
	private long timeout;

	public PeerConnectionParameters() {
		securityStandard = SecurityStandard.NONE;
	}
	
	@XmlElement(name="timeout",type=long.class, defaultValue="3000")
	public long getTimeout() {
		return timeout;
	}

	@XmlElement(name="transport-protocol",type=TransportProtocols.class, defaultValue="TCP")
	public TransportProtocols getTransportProtocol() {
		return transportProtocol;
	}

	@XmlElement(name = "socket-receive-buffer-size",type = int.class,defaultValue ="-1")
	public int getSocketReceiveBuffetSize() {
		return socketReceiveBuffetSize;
	}

	@XmlElement(name = "socket-send-buffer-size",type = int.class,defaultValue ="-1")
	public int getSocketSendBuffetSize() {
		return socketSendBuffetSize;
	}

	@XmlElement(name = "tcp-nagle-algorithm",type = boolean.class, defaultValue="false")
	public boolean getIsTCPNagleAlgoEnabled() {
		return isTCPNagleAlgoEnabled;
	}

	@XmlElement(name = "dwr-duration",type = int.class, defaultValue="60")
	public int getDwrDuration() {
		return dwrDuration;
	}

	@XmlElement(name = "init-connection-duration",type = int.class, defaultValue="60")
	public int getInitConnectionDuration() {
		return initConnectionDuration;
	}

	@XmlElement(name = "send-dpr-event",type =boolean.class, defaultValue="false")
	public boolean getIsSendDPREvent() {
		return sendDPREvent;
	}
	
	@XmlElement(name = "security-standard",type = SecurityStandard.class,defaultValue="NONE")
	public SecurityStandard getSecurityStandard() {
		return securityStandard;
	}
	
	

	public void setTransportProtocol(TransportProtocols transportProtocol) {
		this.transportProtocol = transportProtocol;
	}


	public void setSocketReceiveBuffetSize(int socketReceiveBuffetSize) {
		this.socketReceiveBuffetSize = socketReceiveBuffetSize;
	}


	public void setSocketSendBuffetSize(int socketSendBuffetSize) {
		this.socketSendBuffetSize = socketSendBuffetSize;
	}


	public void setIsTCPNagleAlgoEnabled(boolean isTCPNagleAlgoEnabled) {
		this.isTCPNagleAlgoEnabled = isTCPNagleAlgoEnabled;
	}


	public void setDwrDuration(int dwrDuration) {
		this.dwrDuration = dwrDuration;
	}


	public void setInitConnectionDuration(int initConnectionDuration) {
		this.initConnectionDuration = initConnectionDuration;
	}


	public void setIsSendDPREvent(boolean sendDPREvent) {
		this.sendDPREvent = sendDPREvent;
	}
	
	public void setSecurityStandard(SecurityStandard securityStandard) {
		this.securityStandard = securityStandard;
	}
	
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

	public boolean isSendDPREvent() {
		return sendDPREvent;
	}
	
	

}
