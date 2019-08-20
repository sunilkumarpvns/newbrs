package com.elitecore.diameterapi.mibs.custom.extended;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.mibs.custom.autogen.PeerIpAddrEntryMBean;
import com.elitecore.diameterapi.mibs.custom.autogen.TablePeerIpAddrTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TablePeerIpAddrTableImpl extends TablePeerIpAddrTable {

	private static final String MODULE = "PEER-IP-ADD-TABLE";

	public TablePeerIpAddrTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}
	
	@Override
	public synchronized void addEntry(PeerIpAddrEntryMBean entry, ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "Peer IP address for peer(" + entry.getPeerIpAddrPeerIdentity() + ") not added in PeerIpAddressTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}

}
