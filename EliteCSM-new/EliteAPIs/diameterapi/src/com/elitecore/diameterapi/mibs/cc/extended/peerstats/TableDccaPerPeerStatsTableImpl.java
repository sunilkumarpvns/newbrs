package com.elitecore.diameterapi.mibs.cc.extended.peerstats;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.mibs.cc.autogen.DccaPerPeerStatsEntryMBean;
import com.elitecore.diameterapi.mibs.cc.autogen.TableDccaPerPeerStatsTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableDccaPerPeerStatsTableImpl extends TableDccaPerPeerStatsTable{

	
	private static final String MODULE = "CCA-PEER-STATS-TABLE";

	public TableDccaPerPeerStatsTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}

	@Override
	public synchronized void addEntry(DccaPerPeerStatsEntryMBean entry,ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "PerPeerStats Entry for peer(" + entry.getDccaPeerIdentity() + ") not added in DccaPerPeerStatsTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "PerPeerStats Entry for peer(" + entry.getDccaPeerIdentity() + ") not added in DccaPerPeerStatsTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "PerPeerStats Entry for peer(" + entry.getDccaPeerIdentity() + ") not added in DccaPerPeerStatsTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
}