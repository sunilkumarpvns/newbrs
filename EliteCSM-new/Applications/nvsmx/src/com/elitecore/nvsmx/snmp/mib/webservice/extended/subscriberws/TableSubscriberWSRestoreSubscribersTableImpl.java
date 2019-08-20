package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;


//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB.
//

// java imports
//
import java.io.Serializable;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSRestoreSubscribersEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriberProvisioningWSRestoreSubscribersTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

 
public class TableSubscriberWSRestoreSubscribersTableImpl extends TableSubscriberProvisioningWSRestoreSubscribersTable implements Serializable {
				
	private static final long serialVersionUID = 1L;
	private static final String MODULE = "SUBSCRIBER-WS-wsRestoreSubscribers-STAT-TABLE";

	public TableSubscriberWSRestoreSubscribersTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}
	
	@Override
	public synchronized void addEntry(SubscriberWSRestoreSubscribersEntryMBean entry, ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
		
		if(server != null){
			try {
				server.registerMBean(entry, name);
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "SubscriberProvisioningWS method wsRestoreSubscribers ResultCode statistics entry for ( " +entry.getRestoreSubscribersResultCodeName()+","+entry.getRestoreSubscribersResultCode()+" ) " +
						" not added in TableSubscriberProvisioningWSRestoreSubscribersTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}

}
