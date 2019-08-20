package com.elitecore.aaa.mibs.radius.dynauth.server.extended;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.aaa.mibs.radius.dynauth.client.autogen.RadiusDynAuthServerEntryMBean;
import com.elitecore.aaa.mibs.radius.dynauth.client.autogen.TableRadiusDynAuthServerTable;
import com.elitecore.commons.logging.LogManager;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableRadiusDynAuthServerTableImpl extends TableRadiusDynAuthServerTable{

	private static final String MODULE = "RADIUS-DYNAUTH-SERVER-TABLE";
	
	public TableRadiusDynAuthServerTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}

	@Override
	public synchronized void addEntry(RadiusDynAuthServerEntryMBean entry,
			ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Dyna-Auth Server Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Dyna-Auth Server Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Dyna-Auth Server Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}		
	}
}
