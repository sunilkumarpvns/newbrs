package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSChangeBillDayEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSSubscribeBoDEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriptionWSSubscribeBoDTable;
import com.elitecore.nvsmx.ws.subscription.ISubscriptionWS;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class TableSubscriptionWSSubscribeBoDTableImpl extends TableSubscriptionWSSubscribeBoDTable {

    private static final long serialVersionUID = 1L;
    private static final String MODULE = "SUBSCRIPTION-WS-wsSubscribeBoD-STAT-TABLE";

    public TableSubscriptionWSSubscribeBoDTableImpl(SnmpMib myMib, MBeanServer server) {
        super(myMib, server);
    }

    @Override
    public synchronized void addEntry(SubscriptionWSSubscribeBoDEntryMBean entry, ObjectName name) throws SnmpStatusException {
        super.addEntry(entry, name);

        if (server != null) {
            try {
                server.registerMBean(entry, name);
            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "SubscriptionWS method " + ISubscriptionWS.WS_SUBSCRIBE_BOD
                        + " ResultCode statistics entry for ( "
                        + entry.getSubscribeBoDResultCodeName() + "," + entry.getSubscribeBoDResultCode() + " ) " +
                        " not added in TableSubscriptionWSSubscribeBoDTableImpl. Reason: " + e.getMessage());
                LogManager.getLogger().trace(MODULE, e);
            }
        }
    }
}
