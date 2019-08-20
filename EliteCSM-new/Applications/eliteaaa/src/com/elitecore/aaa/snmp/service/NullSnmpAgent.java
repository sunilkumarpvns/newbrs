package com.elitecore.aaa.snmp.service;

import java.net.UnknownHostException;

import javax.annotation.Nonnull;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.serverx.alert.listeners.SnmpAlertProcessor;
import com.sun.management.snmp.agent.SnmpMib;

/**
 * Place holder SNMP Agent that does not perform any task like 
 * initialization, sending trap, mib registration etc.
 * 
 * @author sanjay.dhamelia
 */
public class NullSnmpAgent extends EliteAAASNMPAgent {

	private String serviceRemarks;
	/**
	 * Default NullSnmpAgent with ip:port = 0.0.0.0:161
	 * Remarks is always an INVALID_LICENSE as this will
	 * instantiate when license is not taken.  
	 * @param serverContext
	 * @param enterpriseOid - oid for server identification.
	 */
	public NullSnmpAgent(@Nonnull AAAServerContext serverContext, @Nonnull String enterpriseOid) {
		super(serverContext, enterpriseOid);
		this.serviceRemarks = ServiceRemarks.INVALID_LICENSE.remark;
	}
	
	/**
	 * Creates null agent containing information that will be displayed in CLI.
	 * This constructor is useful when SNMP agent {@link EliteAAASNMPAgent} fails 
	 * to initialize.
	 * 
	 * Remarks can be INVALID_CONFIGURATION or PROBLEM_BINDING_IP_PORT when 
	 * actual snmp agent creation is failed.
	 * 
	 * @param serverContext
	 * @param enterpriseOid - oid for server identification.
	 * @param ipAddress - IP Address of snmp agent
	 * @param port - port of snmp agent
	 */
	public NullSnmpAgent(@Nonnull AAAServerContext serverContext, @Nonnull String enterpriseOid, @Nonnull String ipAddress, int port) {
		super(serverContext, enterpriseOid, ipAddress, port);
		this.serviceRemarks = ServiceRemarks.INVALID_CONFIGURATION.remark + " or " + 
				ServiceRemarks.PROBLEM_BINDING_IP_PORT.remark;
	}
	
	@Override
	public void init() throws InitializationFailedException {
		// no op
	}
	
	@Override
	public void sendTrap(SystemAlert alert,SnmpAlertProcessor snmpAlertProcessor) {
		// no op
	}

	@Override
	public void registerMib(SnmpMib snmpMib) {
		// no op
	}
	
	@Override
	public void stop() {
		// no op
	}
	
	
	/***
	 * This method is required to display service remarks in CLI.
	 * There are only two remarks when {@link NullSnmpAgent} is 
	 * created either {@link UnknownHostException} or when IP-PORT 
	 * Binding is failed.
	 */
	@Override
	public String getRemarks() {
		return serviceRemarks;
	}
}