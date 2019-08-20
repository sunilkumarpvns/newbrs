package com.elitecore.diameterapi.mibs.cc.extended.peercfgs;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.mibs.cc.autogen.DccaPeerVendorEntryMBean;
import com.elitecore.diameterapi.mibs.cc.autogen.TableDccaPeerVendorTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableDccaPeerVendorTableImpl extends TableDccaPeerVendorTable{

	private static final String MODULE = "CCA-PEER-VENDOR-TABLE";

	public TableDccaPeerVendorTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}

	@Override
	public synchronized void addEntry(DccaPeerVendorEntryMBean entry,ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "PeerVendor Entry for peer(" + entry.getDccaPeerVendorId() + ") not added in DccaPeerVendorTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "PeerVendor Entry for peer(" + entry.getDccaPeerVendorId() + ") not added in DccaPeerVendorTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "PeerVendor Entry for peer(" + entry.getDccaPeerVendorId() + ") not added in DccaPeerVendorTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
}
