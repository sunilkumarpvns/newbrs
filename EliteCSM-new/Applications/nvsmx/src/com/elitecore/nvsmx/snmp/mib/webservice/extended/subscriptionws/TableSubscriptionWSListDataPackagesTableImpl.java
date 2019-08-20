package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;


//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB.
//

// java imports
//

import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSListDataPackagesEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSListPackagesEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriptionWSListDataPackagesTable;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriptionWSListPackagesTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.Serializable;


public class TableSubscriptionWSListDataPackagesTableImpl extends TableSubscriptionWSListDataPackagesTable implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = "SUBSCRIPTION-WS-wsListDataPackages-STAT-TABLE";

	public TableSubscriptionWSListDataPackagesTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}
	
	@Override
	public synchronized void addEntry(SubscriptionWSListDataPackagesEntryMBean entry, ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "SubscriptionWS method wsListDataPackages ResultCode statistics entry for ( " +entry.getListDataPackagesResultCodeName()+","+entry.getListDataPackagesResultCode()+" ) " +
						" not added in TableSubscriptionWSListDataPackagesTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
	
}