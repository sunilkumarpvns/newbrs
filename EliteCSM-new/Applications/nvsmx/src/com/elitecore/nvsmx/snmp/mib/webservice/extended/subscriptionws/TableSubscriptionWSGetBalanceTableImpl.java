package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;


//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB.
//

// java imports
//
import java.io.Serializable;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSGetBalanceEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriptionWSGetBalanceTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

 
public class TableSubscriptionWSGetBalanceTableImpl extends TableSubscriptionWSGetBalanceTable implements Serializable {
				
	private static final long serialVersionUID = 1L;
	private static final String MODULE = "SUBSCRIPTION-WS-wsGetBalance-STAT-TABLE";

	public TableSubscriptionWSGetBalanceTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}
	
	@Override
	public synchronized void addEntry(SubscriptionWSGetBalanceEntryMBean entry, ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "SubscriptionWS method wsGetBalance ResultCode statistics entry for ( " +entry.getGetBalanceResultCodeName()+","+entry.getGetBalanceResultCode()+" ) " +
						" not added in TableSubscriptionWSGetBalanceTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
	
}