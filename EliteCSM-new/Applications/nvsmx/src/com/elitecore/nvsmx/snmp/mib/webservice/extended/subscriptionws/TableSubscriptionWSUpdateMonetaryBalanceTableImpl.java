package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSUpdateMonetaryBalanceEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriptionWSUpdateMonetaryBalanceTable;
import com.elitecore.nvsmx.ws.subscription.ISubscriptionWS;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableSubscriptionWSUpdateMonetaryBalanceTableImpl extends TableSubscriptionWSUpdateMonetaryBalanceTable {

    private static final long serialVersionUID = 1L;
    private static final String MODULE = "SUBSCRIPTION-WS-wsUpdateMonetaryBalance-STAT-TABLE";

    public TableSubscriptionWSUpdateMonetaryBalanceTableImpl(SnmpMib myMib, MBeanServer server) {
        super(myMib, server);
    }

    @Override
    public synchronized void addEntry(SubscriptionWSUpdateMonetaryBalanceEntryMBean entry, ObjectName name) throws SnmpStatusException {
        super.addEntry(entry, name);

        if(server != null){
            try {
                server.registerMBean(entry, name);
            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "SubscriptionWS method " + ISubscriptionWS.WS_UPDATE_MONETARY_BALANCE
                        + " ResultCode statistics entry for ( "
                        + entry.getUpdateMonetaryBalanceResultCodeName() + "," + entry.getUpdateMonetaryBalanceResultCode() + " ) " +
                        " not added in TableSubscriptionWSUpdateMonetaryBalanceTableImpl. Reason: " + e.getMessage());
                LogManager.getLogger().trace(MODULE, e);
            }
        }
    }
}
