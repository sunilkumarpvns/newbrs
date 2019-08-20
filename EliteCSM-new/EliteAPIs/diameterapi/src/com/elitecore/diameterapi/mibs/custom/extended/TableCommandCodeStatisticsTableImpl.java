package com.elitecore.diameterapi.mibs.custom.extended;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.mibs.custom.autogen.CommandCodeStatisticsEntryMBean;
import com.elitecore.diameterapi.mibs.custom.autogen.TableCommandCodeStatisticsTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableCommandCodeStatisticsTableImpl extends TableCommandCodeStatisticsTable {

	private static final String MODULE = "COMMAND-CODE-STAT-TABLE";

	public TableCommandCodeStatisticsTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}
	
	@Override
	public synchronized void addEntry(CommandCodeStatisticsEntryMBean entry, ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "Command-code statistics entry for peer (" +entry.getCcwPeerIdentity() 
											+") for app(" + entry.getCcwApplicationName() +"-"+ entry.getApplicationID() 
											+ ") for command-code(" + entry.getCcwApplicationName() + "-" +  entry.getCommandCode() + ") not added in PeerIpAddressTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
	

}
