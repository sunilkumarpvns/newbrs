package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSChangeAlternateIdStatusEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriberProvisioningWSChangeAlternateIdStatusTable;
import com.elitecore.nvsmx.ws.subscriberprovisioning.rest.RESTSubscriberProvisioningWS;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class TableSubscriberProvisioningWSChangeAlternateIdStatusTableImpl extends TableSubscriberProvisioningWSChangeAlternateIdStatusTable {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = "SUBSCRIBER-WS-wsChangeAlternateIdStatus-STAT-TABLE";
	public TableSubscriberProvisioningWSChangeAlternateIdStatusTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}

	@Override
	public synchronized void addEntry(SubscriberWSChangeAlternateIdStatusEntryMBean entry, ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);

		if (server != null) {
			try {
				server.registerMBean(entry, name);
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "SubscriberProvisioningWS method "
						+ RESTSubscriberProvisioningWS.WS_CHANGE_ALTERNATEID_STATUS
						+ " ResultCode statistics entry for ( "
						+ entry.getChangeAlternateIdStatusResultCodeName() + "," + entry.getChangeAlternateIdStatusResultCode() + " ) " +
						" not added in TableSubscriberProvisioningWSChangeAlternateIdStatusTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
}
