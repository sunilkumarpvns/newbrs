package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSGetAlternateIdEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriberProvisioningWSGetAlternateIdTable;
import com.elitecore.nvsmx.ws.subscriberprovisioning.rest.RESTSubscriberProvisioningWS;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class TableSubscriberProvisioningWSGetAlternateIdTableImpl extends TableSubscriberProvisioningWSGetAlternateIdTable {
	private static final long serialVersionUID = 1L;
	private static final String MODULE = "SUBSCRIBER-WS-wsGetAlternateId-STAT-TABLE";

	public TableSubscriberProvisioningWSGetAlternateIdTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}

	@Override
	public synchronized void addEntry(SubscriberWSGetAlternateIdEntryMBean entry, ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);

		if (server != null) {
			try {
				server.registerMBean(entry, name);
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "SubscriberProvisioningWS method "
						+ RESTSubscriberProvisioningWS.WS_GET_ALTERNATE_ID
						+ " ResultCode statistics entry for ( "
						+ entry.getGetAlternateIdResultCodeName() + "," + entry.getGetAlternateIdResultCode() + " ) " +
						" not added in TableSubscriberProvisioningWSGetAlternateIdTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
}
