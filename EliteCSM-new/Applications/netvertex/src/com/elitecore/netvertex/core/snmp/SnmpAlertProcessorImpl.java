package com.elitecore.netvertex.core.snmp;

import java.net.UnknownHostException;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.alert.SnmpRequestType;
import com.elitecore.core.serverx.alert.TrapVersion;
import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.serverx.alert.listeners.BaseAlertProcessor;
import com.elitecore.core.serverx.alert.listeners.SnmpAlertProcessor;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.alerts.conf.TrapAlertListenerConfiguration;
import com.sun.management.snmp.manager.SnmpParameters;
import com.sun.management.snmp.manager.SnmpPeer;

public class SnmpAlertProcessorImpl extends BaseAlertProcessor implements SnmpAlertProcessor{

	private TrapAlertListenerConfiguration configuration;
	private SnmpPeer snmpPeer;
	private boolean initialized;

	public SnmpAlertProcessorImpl(NetVertexServerContext context,
			TrapAlertListenerConfiguration configuration) {
		super(context);
		this.configuration = configuration;
		initialized = false;
	}

	@Override
	public synchronized void init() throws InitializationFailedException {
		if(initialized ){
			return ;
		}
		SnmpParameters parameters =new SnmpParameters();
		parameters.setRdCommunity(configuration.getCommunity());
		try {
			// SnmpPeer tries to resolve host if it fails it will throw UnknownHostException
			snmpPeer = new SnmpPeer(configuration.getServerIp(),configuration.getPort());
			
		} catch (UnknownHostException e) {
			throw new InitializationFailedException(e);
		}
		
		if(configuration.getSnmpRequestType() == SnmpRequestType.INFORM){
			// Incrementing by One, as SNMP Max Tries defines no of inform requests sent till response received
			snmpPeer.setMaxTries(configuration.getRetryCount() + 1);  
			
			snmpPeer.setTimeout(configuration.getRequestTimeout());
			parameters.setInformCommunity(configuration.getCommunity());
		}
		
		snmpPeer.setParams(parameters);
		
		initialized = true ;
	}

	@Override
	public void handleSystemAlert(SystemAlert alert) {
		((NetVertexServerContext)serverContext).sendSnmpTrap(alert , this);
	}

	@Override
	public String getAlertProcessorType() {
		return TRAP_ALERT_PROCESSOR_ID;
	}

	
	@Override
	public String getAlertProcessorId() {
		return configuration.getListenerId();
	}

	@Override
	public SnmpPeer getSnmpPeer() {
		return snmpPeer;
	}

	@Override
	public String getName(){
		return configuration.getName();
	}
	@Override
	public boolean isAdvanceTrap(){
		return configuration.isAdvanceTrap();
	}

	@Override
	public TrapVersion getVersion() {
		return configuration.getTrapVersion();
	}

	@Override
	public SnmpRequestType getSnmpRequestType() {
		return configuration.getSnmpRequestType();
	}
}