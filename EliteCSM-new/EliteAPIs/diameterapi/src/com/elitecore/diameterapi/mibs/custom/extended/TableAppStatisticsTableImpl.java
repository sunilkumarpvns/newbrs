package com.elitecore.diameterapi.mibs.custom.extended;

import static com.elitecore.commons.logging.LogManager.getLogger;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.elitecore.diameterapi.mibs.custom.autogen.ApplicationStatisticsEntryMBean;
import com.elitecore.diameterapi.mibs.custom.autogen.TableAppStatisticsTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableAppStatisticsTableImpl extends TableAppStatisticsTable {
	
	private static final String MODULE = "APP-STAT-TABLE";

	public TableAppStatisticsTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}

	@Override
	public synchronized void addEntry(ApplicationStatisticsEntryMBean entry, ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (Exception e) {
				getLogger().error(MODULE, "App statistics entry for app(" + entry.getApplicationName() +"-" + entry.getApplicationID() + ") not added in AppStatisticsTable. Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
	}
}
