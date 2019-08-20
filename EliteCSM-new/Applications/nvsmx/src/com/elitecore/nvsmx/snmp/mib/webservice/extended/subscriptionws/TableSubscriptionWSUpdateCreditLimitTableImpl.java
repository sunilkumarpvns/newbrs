package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSUpdateCreditLimitEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriptionWSUpdateCreditLimitTable;
import com.elitecore.nvsmx.ws.subscription.ISubscriptionWS;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class TableSubscriptionWSUpdateCreditLimitTableImpl extends TableSubscriptionWSUpdateCreditLimitTable {

    private static final long serialVersionUID = 1L;
    private static final String MODULE = "SUBSCRIPTION-WS-wsUpdateCreditLimit-STAT-TABLE";

    public TableSubscriptionWSUpdateCreditLimitTableImpl(SnmpMib myMib, MBeanServer server) {
        super(myMib, server);
    }

    @Override
    public synchronized void addEntry(SubscriptionWSUpdateCreditLimitEntryMBean entry, ObjectName name) throws SnmpStatusException {
        super.addEntry(entry, name);

        if(server != null){
            try {
                server.registerMBean(entry, name);
            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "SubscriptionWS method " + ISubscriptionWS.WS_UPDATE_MONETARY_BALANCE
                        + " ResultCode statistics entry for ( "
                        + entry.getUpdateCreditLimitResultCodeName() + "," + entry.getUpdateCreditLimitResultCode() + " ) " +
                        " not added in TableSubscriptionWSUpdateCreditLimitTableImpl. Reason: " + e.getMessage());
                LogManager.getLogger().trace(MODULE, e);
            }
        }
    }
}
