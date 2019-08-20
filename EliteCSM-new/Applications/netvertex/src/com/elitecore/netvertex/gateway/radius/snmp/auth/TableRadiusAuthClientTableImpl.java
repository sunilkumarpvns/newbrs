package com.elitecore.netvertex.gateway.radius.snmp.auth;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.gateway.radius.snmp.auth.autogen.RadiusAuthClientEntryMBean;
import com.elitecore.netvertex.gateway.radius.snmp.auth.autogen.TableRadiusAuthClientTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableRadiusAuthClientTableImpl extends TableRadiusAuthClientTable {

	private static final String MODULE = "RAD-AUTH-CLIENT-TABLE";
	transient private NetVertexServerContext context;

	public TableRadiusAuthClientTableImpl(NetVertexServerContext context ,SnmpMib myMib) {
		super(myMib);
		this.context = context;
	}

	public TableRadiusAuthClientTableImpl(NetVertexServerContext context ,SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
		this.context = context;
	}

	@Override
	public synchronized void addEntry(RadiusAuthClientEntryMBean entry,
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