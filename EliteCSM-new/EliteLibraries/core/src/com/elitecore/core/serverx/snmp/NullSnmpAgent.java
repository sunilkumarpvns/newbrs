package com.elitecore.core.serverx.snmp;

import static com.elitecore.commons.logging.LogManager.getLogger;

import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.serverx.alert.listeners.SnmpAlertProcessor;
import com.sun.management.snmp.SnmpOid;
import com.sun.management.snmp.agent.SnmpMib;

public class NullSnmpAgent extends EliteSnmpAgent {
	
	private static final String MODULE = "NULL-SNMP-AGENT";
	private String serviceRemarks;
	
	public NullSnmpAgent(ServerContext serverContext, SnmpOid enterpriseOid, String ipAddress, int port) {
		super(serverContext, enterpriseOid, ipAddress, port);
		this.serviceRemarks = ServiceRemarks.PROBLEM_BINDING_IP_PORT.remark + " or " + ServiceRemarks.INVALID_CONFIGURATION.remark;
	}
	
	@Override
	public void registerMib(SnmpMib snmpMib) {
		if (getLogger().isWarnLogLevel()) {
			getLogger().warn(MODULE, "SNMP Mib(" + snmpMib.getMibName() + ") will not be registered. Reason: SNMP agent not initialized as " + serviceRemarks);
		}
	}
	
	@Override
	public void sendTrap(SystemAlert alert, SnmpAlertProcessor snmpAlertProcessor) {
		if (getLogger().isWarnLogLevel()) {
			getLogger().warn(MODULE, "SNMP trap(" + alert.getAlert().getName() + ") will not be sent. Reason: SNMP agent not initialized as " + serviceRemarks);
		}
	}
	
	@Override
	public String getRemarks() {
		return serviceRemarks;
	}
	
	@Override
	public String getIPAddress() {
		return "N/A";
	}
}
