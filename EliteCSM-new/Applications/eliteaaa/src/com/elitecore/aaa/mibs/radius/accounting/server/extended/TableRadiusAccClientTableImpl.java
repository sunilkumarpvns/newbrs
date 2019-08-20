package com.elitecore.aaa.mibs.radius.accounting.server.extended;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.aaa.mibs.radius.accounting.server.autogen.RadiusAccClientEntryMBean;
import com.elitecore.aaa.mibs.radius.accounting.server.autogen.TableRadiusAccClientTable;
import com.elitecore.commons.logging.LogManager;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableRadiusAccClientTableImpl extends TableRadiusAccClientTable{

	private static final String MODULE = "RAD-ACCT-CLIENT-TABLE";

	public TableRadiusAccClientTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}
	
	@Override
	public synchronized void addEntry(RadiusAccClientEntryMBean entry,
			ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Accounting Client Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Accounting Client Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Accounting Client Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
}