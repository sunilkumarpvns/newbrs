package com.elitecore.diameterapi.mibs.base.extended.peerconf;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.mibs.base.autogen.DbpAppAdvFromPeerEntryMBean;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpAppAdvFromPeerTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableDbpAppAdvFromPeerTableImpl extends
		TableDbpAppAdvFromPeerTable {

	private static final String MODULE = "APP-ADV-FROM-PEER-TBL";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TableDbpAppAdvFromPeerTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
    }
	
	@Override
	public synchronized void addEntry(DbpAppAdvFromPeerEntryMBean entry,
			ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null) {
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "Failed to Register Entry for Application: " + 
						entry.getDbpAppAdvFromPeerIndex() + 
						" of peer with index: " + entry.getDbpPeerIndex() +", Reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "Failed to Register Entry for Application: " + 
						entry.getDbpAppAdvFromPeerIndex() + 
						" of peer with index: " + entry.getDbpPeerIndex() +", Reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "Failed to Register Entry for Application: " + 
						entry.getDbpAppAdvFromPeerIndex() + 
						" of peer with index: " + entry.getDbpPeerIndex() +", Reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
}
