package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSAddAlternateIdEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriberProvisioningWSAddAlternateIdTable;
import com.elitecore.nvsmx.ws.subscriberprovisioning.rest.RESTSubscriberProvisioningWS;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class TableSubscriberProvisioningWSAddAlternateIdTableImpl extends TableSubscriberProvisioningWSAddAlternateIdTable {
	private static final long serialVersionUID = 1L;
	private static final String MODULE = "SUBSCRIBER-WS-wsAddAlternateId-STAT-TABLE";

	public TableSubscriberProvisioningWSAddAlternateIdTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}

	@Override
	public synchronized void addEntry(SubscriberWSAddAlternateIdEntryMBean entry, ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);

		if (server != null) {
			try {
				server.registerMBean(entry, name);
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "SubscriberProvisioningWS method "
						+ RESTSubscriberProvisioningWS.WS_ADD_ALTERNATEID
						+ " ResultCode statistics entry for ( "
						+ entry.getAddAlternateIdResultCodeName() + "," + entry.getAddAlternateIdResultCode() + " ) " +
						" not added in TableSubscriberProvisioningWSAddAlternateIdTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
}
