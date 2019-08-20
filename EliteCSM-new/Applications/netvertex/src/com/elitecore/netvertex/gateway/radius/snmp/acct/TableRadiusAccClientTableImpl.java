package com.elitecore.netvertex.gateway.radius.snmp.acct;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.gateway.radius.snmp.acct.autogen.RadiusAccClientEntryMBean;
import com.elitecore.netvertex.gateway.radius.snmp.acct.autogen.TableRadiusAccClientTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableRadiusAccClientTableImpl extends TableRadiusAccClientTable {
	
	private static final String MODULE = "RAD-ACCT-CLIENT-TABLE";
	private transient NetVertexServerContext context;

	public TableRadiusAccClientTableImpl(NetVertexServerContext context ,SnmpMib myMib) {
		super(myMib);
		this.context = context;
	}
	
	public TableRadiusAccClientTableImpl(NetVertexServerContext context ,SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
		this.context = context;
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
