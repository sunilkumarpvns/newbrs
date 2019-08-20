package com.elitecore.aaa.mibs.radius.accounting.client.extended;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.aaa.mibs.radius.accounting.client.autogen.RadiusAccServerEntryMBean;
import com.elitecore.aaa.mibs.radius.accounting.client.autogen.TableRadiusAccServerTable;
import com.elitecore.commons.logging.LogManager;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableRadiusAccServerTableImpl extends TableRadiusAccServerTable{

	private static final String MODULE = "RADIUS-ACCT-SERVER-TABLE";
	
	public TableRadiusAccServerTableImpl(SnmpMib snmpMib,MBeanServer server) {
		super(snmpMib, server);
	}
	
	@Override
	public synchronized void addEntry(RadiusAccServerEntryMBean entry,
			ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Acct Server Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Acct Server Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Acct Server Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
}
