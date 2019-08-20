package com.elitecore.diameterapi.mibs.custom.extended;

import static com.elitecore.commons.logging.LogManager.getLogger;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.elitecore.diameterapi.mibs.custom.autogen.ResultCodeStatisticsEntryMBean;
import com.elitecore.diameterapi.mibs.custom.autogen.TableResultCodeStatisticsTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableResultCodeStatisticsTableImpl extends TableResultCodeStatisticsTable {

	private static final String MODULE = "RESULT-CODE-STAT-TABLE";

	public TableResultCodeStatisticsTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}
	@Override
	public synchronized void addEntry(ResultCodeStatisticsEntryMBean entry, ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (Exception e) {
				getLogger().error(MODULE, "Result-code statistics entry for peer (" +entry.getRcwPeerIdentity() 
											+") for app(" + entry.getRcwApplicationName() +"-"+ entry.getApplicationID() 
											+ ") for result-code(" + entry.getRcwApplicationName() +"-" + entry.getResultCode() + ") not added in ResultCodeStatisticsTable. Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
	}

}
