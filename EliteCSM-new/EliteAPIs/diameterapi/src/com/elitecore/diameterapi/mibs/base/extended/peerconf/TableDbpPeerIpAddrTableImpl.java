package com.elitecore.diameterapi.mibs.base.extended.peerconf;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.mibs.base.autogen.DbpPeerIpAddrEntryMBean;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpPeerIpAddrTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableDbpPeerIpAddrTableImpl extends TableDbpPeerIpAddrTable{

	private static final String MODULE = "PEER-IP-ADDRESS-TABLE";
	
	public TableDbpPeerIpAddrTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}

	@Override
	public synchronized void addEntry(DbpPeerIpAddrEntryMBean entry,ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "Failed to Register Entry of Peer,Reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "Failed to Register Entry of Peer,Reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "Failed to Register Entry of Peer,Reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
}
