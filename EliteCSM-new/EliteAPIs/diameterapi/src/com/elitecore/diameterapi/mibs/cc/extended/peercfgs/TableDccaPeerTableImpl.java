package com.elitecore.diameterapi.mibs.cc.extended.peercfgs;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.mibs.cc.autogen.DccaPeerEntryMBean;
import com.elitecore.diameterapi.mibs.cc.autogen.TableDccaPeerTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableDccaPeerTableImpl extends TableDccaPeerTable{

	private static final String MODULE = "CCA-PEER-TABLE";

	public TableDccaPeerTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}

	@Override
	public synchronized void addEntry(DccaPeerEntryMBean entry, ObjectName name)throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "Peer Entry for peer(" + entry.getDccaPeerId() + ") not added in DccaPeerTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "Peer Entry for peer(" + entry.getDccaPeerId() + ") not added in DccaPeerTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "Peer Entry for peer(" + entry.getDccaPeerId() + ") not added in DccaPeerTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
}
