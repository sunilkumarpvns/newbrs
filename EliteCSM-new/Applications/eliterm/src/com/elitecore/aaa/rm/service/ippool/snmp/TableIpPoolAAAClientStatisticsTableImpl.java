package com.elitecore.aaa.rm.service.ippool.snmp;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.aaa.rm.service.ippool.snmp.autogen.IpPoolAAAClientEntryMBean;
import com.elitecore.aaa.rm.service.ippool.snmp.autogen.TableIpPoolAAAClientStatisticsTable;
import com.elitecore.commons.logging.LogManager;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableIpPoolAAAClientStatisticsTableImpl extends TableIpPoolAAAClientStatisticsTable {

	private static final String MODULE = "IP-POOL-AAA-CLIENT-TABLE";
	
	public TableIpPoolAAAClientStatisticsTableImpl(SnmpMib myMib,MBeanServer server) {
		super(myMib, server);
	}

	@Override
	public synchronized void addEntry(IpPoolAAAClientEntryMBean entry,ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "Failed to register AAA Client Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "Failed to register AAA Client Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "Failed to register AAA Client Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			}
		}
	}
}
