package com.elitecore.diameterapi.mibs.base.extended.realmstats;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.mibs.base.autogen.DbpRealmMessageRouteEntryMBean;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpRealmMessageRouteTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableDbpRealmMessageRouteTableImpl extends TableDbpRealmMessageRouteTable{

	private static final String MODULE = "DBP-REALM-MSG-ROUTE-TABLE";

	public TableDbpRealmMessageRouteTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}
	
	@Override
	public synchronized void addEntry(DbpRealmMessageRouteEntryMBean entry,ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Real Entry:" +entry.getDbpRealmMessageRouteRealm()+ "in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Real Entry:" +entry.getDbpRealmMessageRouteRealm()+ "in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "Failed to register Real Entry:" +entry.getDbpRealmMessageRouteRealm()+ "in MBean Server. Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			}
		}
	}
}
