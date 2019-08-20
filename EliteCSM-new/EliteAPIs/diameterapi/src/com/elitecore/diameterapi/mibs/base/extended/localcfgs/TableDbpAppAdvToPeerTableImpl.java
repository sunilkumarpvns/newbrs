package com.elitecore.diameterapi.mibs.base.extended.localcfgs;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.mibs.base.autogen.DbpAppAdvToPeerEntryMBean;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpAppAdvToPeerTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableDbpAppAdvToPeerTableImpl extends TableDbpAppAdvToPeerTable {
	
	private static final String MODULE = "DBP-APP-ADV-TO-PEER-TBL";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TableDbpAppAdvToPeerTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}
	
	@Override
	public synchronized void addEntry(DbpAppAdvToPeerEntryMBean entry,
			ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null) {
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "Failed to Register Entry for Application: " + 
						entry.getDbpAppAdvToPeerIndex() + " advertised to Peer with Index: " + 
						entry.getDbpPeerIndex()+ ", Reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "Failed to Register Entry for Application: " + 
						entry.getDbpAppAdvToPeerIndex() + " advertised to Peer with Index: " + 
						entry.getDbpPeerIndex()+ ", Reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "Failed to Register Entry for Application: " + 
						entry.getDbpAppAdvToPeerIndex() + " advertised to Peer with Index: " + 
						entry.getDbpPeerIndex()+ ", Reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}

}
