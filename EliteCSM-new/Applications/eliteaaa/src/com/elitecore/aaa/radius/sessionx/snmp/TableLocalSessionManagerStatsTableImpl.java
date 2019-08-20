package com.elitecore.aaa.radius.sessionx.snmp;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.aaa.radius.sessionx.snmp.localsm.autogen.LocalSessionManagerEntryMBean;
import com.elitecore.aaa.radius.sessionx.snmp.localsm.autogen.TableLocalSessionManagerStatsTable;
import com.elitecore.commons.logging.LogManager;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableLocalSessionManagerStatsTableImpl extends TableLocalSessionManagerStatsTable{

	private final static String MODULE = "LOCAL-SM-TABLE";
	
	public TableLocalSessionManagerStatsTableImpl(SnmpMib myMib,MBeanServer server) {
		super(myMib, server);
	}
	
	@Override
	public synchronized void addEntry(LocalSessionManagerEntryMBean entry,ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Local Session Manager Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Local Session Manager Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Local Session Manager Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			}
		}
	}
}
