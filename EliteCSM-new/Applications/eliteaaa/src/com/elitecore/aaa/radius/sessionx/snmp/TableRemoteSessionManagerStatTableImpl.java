package com.elitecore.aaa.radius.sessionx.snmp;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.aaa.radius.sessionx.snmp.remotesm.autogen.RemoteSessionManagerEntryMBean;
import com.elitecore.aaa.radius.sessionx.snmp.remotesm.autogen.TableRemoteSessionManagerStatTable;
import com.elitecore.commons.logging.LogManager;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableRemoteSessionManagerStatTableImpl extends TableRemoteSessionManagerStatTable{

	private final static String MODULE = "REMOTE-SM-TABLE";
	
	public TableRemoteSessionManagerStatTableImpl(SnmpMib myMib,MBeanServer server) {
		super(myMib, server);
	}
	
	@Override
	public synchronized void addEntry(RemoteSessionManagerEntryMBean entry,ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Remote Session Manager Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Remote Session Manager Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Remote Session Manager Entry in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			}

		}
	}

}
