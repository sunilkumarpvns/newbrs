package com.elitecore.aaa.mibs.rm.ippool.client.snmp;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.aaa.mibs.rm.ippool.client.snmp.autogen.IpPoolServerEntryMBean;
import com.elitecore.aaa.mibs.rm.ippool.client.snmp.autogen.TableIpPoolServerStatisticsTable;
import com.elitecore.commons.logging.LogManager;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableIpPoolServerStatisticsTableImpl extends TableIpPoolServerStatisticsTable{

	private static final String MODULE = "IP-POOL-SERVER-TABLE";
	
	public TableIpPoolServerStatisticsTableImpl(SnmpMib myMib,MBeanServer server) {
		super(myMib, server);
	}

	@Override
	public synchronized void addEntry(IpPoolServerEntryMBean entry,
			ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "Failed to register IP-Pool Server Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "Failed to register IP-Pool Server Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "Failed to register IP-Pool Server Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
}
