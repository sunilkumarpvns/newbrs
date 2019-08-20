package com.elitecore.aaa.rm.service.ippool.snmp;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.aaa.rm.service.ippool.snmp.autogen.IpPoolNASClientEntryMBean;
import com.elitecore.aaa.rm.service.ippool.snmp.autogen.TableIpPoolNASClientStatisticsTable;
import com.elitecore.commons.logging.LogManager;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableIpPoolNASClientStatisticsTableImpl extends TableIpPoolNASClientStatisticsTable{

	private static final String MODULE = "IP-POOL-NAS-CLIENT-TABLE";
	
	public TableIpPoolNASClientStatisticsTableImpl(SnmpMib myMib,MBeanServer server) {
		super(myMib, server);
	}

	@Override
	public synchronized void addEntry(IpPoolNASClientEntryMBean entry,ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "Failed to register NAS Client Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "Failed to register NAS Client Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "Failed to register NAS Client Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			}
		}
	}
}
