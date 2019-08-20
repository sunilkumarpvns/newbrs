package com.elitecore.diameterapi.mibs.custom.extended;

import static com.elitecore.commons.logging.LogManager.getLogger;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.elitecore.diameterapi.mibs.custom.autogen.PeerInfoEntryMBean;
import com.elitecore.diameterapi.mibs.custom.autogen.TablePeerInfoTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TablePeerInfoTableImpl extends TablePeerInfoTable {
	
	private static final String MODULE = "PEER-INFO-TABLE";

	public TablePeerInfoTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}
	
	@Override
	public synchronized void addEntry(PeerInfoEntryMBean entry, ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (Exception e) {
				getLogger().error(MODULE, "Peer entry for peer(" + entry.getPeerIdentity() + ") not added in PeerInfoTable. Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}	
	}

}
