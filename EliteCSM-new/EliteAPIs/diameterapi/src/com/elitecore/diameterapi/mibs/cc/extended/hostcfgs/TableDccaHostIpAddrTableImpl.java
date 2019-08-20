package com.elitecore.diameterapi.mibs.cc.extended.hostcfgs;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.mibs.cc.autogen.DccaHostIpAddrEntryMBean;
import com.elitecore.diameterapi.mibs.cc.autogen.TableDccaHostIpAddrTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableDccaHostIpAddrTableImpl extends TableDccaHostIpAddrTable{

	private static final String MODULE = "CCA-HOST-IP-ADDR-TABLE";

	public TableDccaHostIpAddrTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}

	@Override
	public synchronized void addEntry(DccaHostIpAddrEntryMBean entry,ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "HostIpAddr Entry not added in DccaHostIpAddrTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "HostIpAddr Entry not added in DccaHostIpAddrTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "HostIpAddr Entry not added in DccaHostIpAddrTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
}
