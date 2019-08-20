package com.elitecore.aaa.mibs.rm.charging.client.snmp;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.aaa.mibs.rm.charging.client.snmp.autogen.ChargingServerEntryMBean;
import com.elitecore.aaa.mibs.rm.charging.client.snmp.autogen.TableChargingServerStatisticsTable;
import com.elitecore.commons.logging.LogManager;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableChargingServerStatisticsTableImpl extends TableChargingServerStatisticsTable{


	private static final String MODULE = "CHARGING-SERVER-TABLE";

	public TableChargingServerStatisticsTableImpl(SnmpMib myMib,MBeanServer server) {
		super(myMib, server);
	}

	@Override
	public synchronized void addEntry(ChargingServerEntryMBean entry,ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Charging Server Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Charging Server Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Charging Server Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	
	}
}
