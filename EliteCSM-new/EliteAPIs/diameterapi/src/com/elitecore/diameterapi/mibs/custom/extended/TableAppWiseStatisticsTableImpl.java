package com.elitecore.diameterapi.mibs.custom.extended;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.mibs.custom.autogen.AppWiseStatisticsEntryMBean;
import com.elitecore.diameterapi.mibs.custom.autogen.TableAppWiseStatisticsTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableAppWiseStatisticsTableImpl extends TableAppWiseStatisticsTable {

	private static final String MODULE = "APP-STAT-TABLE";

	public TableAppWiseStatisticsTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}
	
	@Override
	public synchronized void addEntry(AppWiseStatisticsEntryMBean entry, ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "App statistics entry for peer (" +entry.getAppwPeerIdentity() 
											+") for app(" + entry.getAppwApplicationName() +"-"+ entry.getApplicationID() 
											+ ") not added in AppWiseStatisticsTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}

}
