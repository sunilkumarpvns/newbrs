package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSGetMonetaryBalanceEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriptionWSGetMonetaryBalanceTable;
import com.elitecore.nvsmx.ws.subscription.ISubscriptionWS;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableSubscriptionWSGetMonetaryBalanceTableImpl extends TableSubscriptionWSGetMonetaryBalanceTable {
    private static final long serialVersionUID = 1L;
    private static final String MODULE = "SUBSCRIPTION-WS-wsGetMonetaryBalance-STAT-TABLE";

    public TableSubscriptionWSGetMonetaryBalanceTableImpl(SnmpMib myMib, MBeanServer server) {
        super(myMib, server);
    }

    @Override
    public synchronized void addEntry(SubscriptionWSGetMonetaryBalanceEntryMBean entry, ObjectName name) throws SnmpStatusException {
        super.addEntry(entry, name);

        if(server != null){
            try {
                server.registerMBean(entry, name);
            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "SubscriptionWS method " + ISubscriptionWS.WS_GET_MONETARY_BALANCE
                        + " ResultCode statistics entry for ( "
                        + entry.getGetMonetaryBalanceResultCodeName() + "," + entry.getGetMonetaryBalanceResultCode() + " ) " +
                        " not added in TableSubscriptionWSGetMonetaryBalanceTableImpl. Reason: " + e.getMessage());
                LogManager.getLogger().trace(MODULE, e);
            }
        }
    }
}
