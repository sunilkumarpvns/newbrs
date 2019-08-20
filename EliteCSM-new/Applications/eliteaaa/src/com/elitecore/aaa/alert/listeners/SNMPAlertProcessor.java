package com.elitecore.aaa.alert.listeners;

import java.net.UnknownHostException;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.SnmpRequestType;
import com.elitecore.core.serverx.alert.TrapVersion;
import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.serverx.alert.listeners.BaseAlertProcessor;
import com.elitecore.core.serverx.alert.listeners.SnmpAlertProcessor;
import com.sun.management.snmp.manager.SnmpParameters;
import com.sun.management.snmp.manager.SnmpPeer;

public class SNMPAlertProcessor extends BaseAlertProcessor implements
		SnmpAlertProcessor {

	private String trapProcessorId;
	private String ipAddress;
	private int port;
	private String community;
	private boolean advanceTrap = true;
	private String alertProcessorName;
	private SnmpPeer snmpPeer;
	private TrapVersion trapVersion;
	private SnmpRequestType snmpRequestType;

	public SNMPAlertProcessor(ServerContext serverContext,String alertProcessorName, String trapProcessorId, String ipAddress,
			int port, int trapVersion, String community, boolean advanceTrap) {
		super(serverContext);
		this.alertProcessorName = alertProcessorName;
		this.trapProcessorId = trapProcessorId; 
		this.ipAddress =ipAddress;
		this.port = port;
		this.trapVersion = TrapVersion.fromValue(Integer.toString(trapVersion));
		this.community = community;
		this.advanceTrap = advanceTrap;
		snmpRequestType = SnmpRequestType.TRAP;
	}

	@Override
	public void handleSystemAlert(SystemAlert alert) {
		serverContext.sendSnmpTrap(alert, this);
	}

	@Override
	public String getAlertProcessorType() {
		return BaseAlertProcessor.TRAP_ALERT_PROCESSOR_ID;
	}
	
	@Override
	public String getAlertProcessorId() {
		return this.trapProcessorId;
	}

	@Override
	public void init() throws InitializationFailedException {

		SnmpParameters parameters = new SnmpParameters();
		parameters.setRdCommunity(community);
		try {
			snmpPeer = new SnmpPeer(ipAddress, port);
			snmpPeer.setParams(parameters);
		} catch (UnknownHostException e) {
			throw new InitializationFailedException(e);
		}
	}

	@Override
	public String getName() {
		return alertProcessorName;
	}

	@Override
	public SnmpPeer getSnmpPeer() {
		return snmpPeer;
	}

	@Override
	public TrapVersion getVersion() {
		return trapVersion;
	}

	@Override
	public boolean isAdvanceTrap() {
		return advanceTrap;
	}

	@Override
	public SnmpRequestType getSnmpRequestType() {
		return snmpRequestType;
	}
}
