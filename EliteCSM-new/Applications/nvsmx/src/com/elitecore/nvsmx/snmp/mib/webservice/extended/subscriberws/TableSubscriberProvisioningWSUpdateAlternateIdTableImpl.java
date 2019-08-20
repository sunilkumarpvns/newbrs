package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSUpdateAlternateIdEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriberProvisioningWSUpdateAlternateIdTable;
import com.elitecore.nvsmx.ws.subscriberprovisioning.rest.RESTSubscriberProvisioningWS;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class TableSubscriberProvisioningWSUpdateAlternateIdTableImpl extends TableSubscriberProvisioningWSUpdateAlternateIdTable {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = "SUBSCRIBER-WS-wsUpdateAlternateId-STAT-TABLE";

	public TableSubscriberProvisioningWSUpdateAlternateIdTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}

	@Override
	public synchronized void addEntry(SubscriberWSUpdateAlternateIdEntryMBean entry, ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);

		if (server != null) {
			try {
				server.registerMBean(entry, name);
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "SubscriberProvisioningWS method "
						+ RESTSubscriberProvisioningWS.WS_GET_ALTERNATE_ID
						+ " ResultCode statistics entry for ( "
						+ entry.getUpdateAlternateIdResultCodeName() + "," + entry.getUpdateAlternateIdResultCode() + " ) " +
						" not added in TableSubscriberProvisioningWSUpdateAlternateIdTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
}
