package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSChangeBillDayEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriptionWSChangeBillDayTable;
import com.elitecore.nvsmx.ws.subscription.ISubscriptionWS;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class TableSubscriptionWSChangeBillDayTableImpl extends TableSubscriptionWSChangeBillDayTable {
    private static final long serialVersionUID = 1L;
    private static final String MODULE = "SUBSCRIPTION-WS-wsChangeBillDay-STAT-TABLE";

    public TableSubscriptionWSChangeBillDayTableImpl(SnmpMib myMib, MBeanServer server) {
        super(myMib, server);
    }

    @Override
    public synchronized void addEntry(SubscriptionWSChangeBillDayEntryMBean entry, ObjectName name) throws SnmpStatusException {
        super.addEntry(entry, name);

        if (server != null) {
            try {
                server.registerMBean(entry, name);
            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "SubscriptionWS method " + ISubscriptionWS.WS_CHANGE_BILL_DAY
                        + " ResultCode statistics entry for ( "
                        + entry.getChangeBillDayResultCodeName() + "," + entry.getChangeBillDayResultCode() + " ) " +
                        " not added in TableSubscriptionWSChangeBillDayTableImpl. Reason: " + e.getMessage());
                LogManager.getLogger().trace(MODULE, e);
            }
        }
    }
}
