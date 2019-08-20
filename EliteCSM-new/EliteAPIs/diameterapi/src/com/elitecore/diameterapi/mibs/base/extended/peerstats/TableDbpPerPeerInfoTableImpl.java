package com.elitecore.diameterapi.mibs.base.extended.peerstats;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.mibs.base.autogen.DbpPerPeerInfoEntryMBean;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpPerPeerInfoTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableDbpPerPeerInfoTableImpl extends TableDbpPerPeerInfoTable{

	private static final String MODULE = "PEER-INFO-TABLE";

	public TableDbpPerPeerInfoTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}

	@Override
	public synchronized void addEntry(DbpPerPeerInfoEntryMBean entry,ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "Peer Entry for peer(" + entry.getDbpPeerIdentity() + ") not added in Peer Info Table. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "Peer Entry for peer(" + entry.getDbpPeerIdentity() + ") not added in Peer Info Table. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "Peer Entry for peer(" + entry.getDbpPeerIdentity() + ") not added in Peer Info Table. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
}
