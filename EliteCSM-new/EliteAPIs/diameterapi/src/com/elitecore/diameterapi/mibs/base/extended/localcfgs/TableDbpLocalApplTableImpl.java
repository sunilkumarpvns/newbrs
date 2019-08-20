package com.elitecore.diameterapi.mibs.base.extended.localcfgs;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.mibs.base.autogen.DbpLocalApplEntryMBean;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpLocalApplTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableDbpLocalApplTableImpl extends TableDbpLocalApplTable {

	private static final String MODULE = "DBP-LOCAL-APP-TBL";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TableDbpLocalApplTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}

	@Override
	public synchronized void addEntry(DbpLocalApplEntryMBean entry,
			ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null) {
			try {
				server.registerMBean(entry, name);
			} catch (InstanceAlreadyExistsException e) {
				LogManager.getLogger().error(MODULE, "Failed to Register Entry for Local Application: " + 
						entry.getDbpLocalApplIndex() + ", Reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (MBeanRegistrationException e) {
				LogManager.getLogger().error(MODULE, "Failed to Register Entry for Local Application: " + 
						entry.getDbpLocalApplIndex() + ", Reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (NotCompliantMBeanException e) {
				LogManager.getLogger().error(MODULE, "Failed to Register Entry for Local Application: " + 
						entry.getDbpLocalApplIndex() + ", Reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}

}
