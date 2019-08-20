package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSGetRnCBalanceEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriptionWSGetRnCBalanceTable;
import com.elitecore.nvsmx.ws.subscription.ISubscriptionWS;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableSubscriptionWSGetRnCBalanceTableImpl extends TableSubscriptionWSGetRnCBalanceTable {
    private static final long serialVersionUID = 1L;
    private static final String MODULE = "SUBSCRIPTION-WS-wsGetRnCBalance-STAT-TABLE";

    public TableSubscriptionWSGetRnCBalanceTableImpl(SnmpMib myMib, MBeanServer server) {
        super(myMib, server);
    }

    @Override
    public synchronized void addEntry(SubscriptionWSGetRnCBalanceEntryMBean entry, ObjectName name) throws SnmpStatusException {
        super.addEntry(entry, name);

        if(server != null){
            try {
                server.registerMBean(entry, name);
            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "SubscriptionWS method " + ISubscriptionWS.WS_GET_RNC_BALANCE
                        + " ResultCode statistics entry for ( "
                        + entry.getGetRnCBalanceResultCodeName() + "," + entry.getGetRnCBalanceResultCode() + " ) " +
                        " not added in TableSubscriptionWSGetRnCBalanceTableImpl. Reason: " + e.getMessage());
                LogManager.getLogger().trace(MODULE, e);
            }
        }
    }
}
