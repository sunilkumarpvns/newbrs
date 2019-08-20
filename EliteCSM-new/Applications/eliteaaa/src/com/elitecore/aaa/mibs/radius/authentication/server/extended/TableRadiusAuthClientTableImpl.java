package com.elitecore.aaa.mibs.radius.authentication.server.extended;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.aaa.mibs.radius.authentication.server.autogen.RadiusAuthClientEntryMBean;
import com.elitecore.aaa.mibs.radius.authentication.server.autogen.TableRadiusAuthClientTable;
import com.elitecore.commons.logging.LogManager;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableRadiusAuthClientTableImpl extends TableRadiusAuthClientTable{

	private static final String MODULE = "RADIUS-CLIENT-TABLE";
	
	public TableRadiusAuthClientTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}

	@Override
	public synchronized void addEntry(RadiusAuthClientEntryMBean entry,ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Auth Client Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Auth Client Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Auth Client Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
}