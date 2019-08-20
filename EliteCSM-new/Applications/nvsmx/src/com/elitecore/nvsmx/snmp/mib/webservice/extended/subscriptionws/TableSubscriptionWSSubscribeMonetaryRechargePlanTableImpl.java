package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSSubscribeMonetaryRechargePlanEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriptionWSSubscribeMonetaryRechargePlanTable;
import com.elitecore.nvsmx.ws.subscription.ISubscriptionWS;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class TableSubscriptionWSSubscribeMonetaryRechargePlanTableImpl extends TableSubscriptionWSSubscribeMonetaryRechargePlanTable {
        private static final long serialVersionUID = 1L;
        private static final String MODULE = "SUBSCRIPTION-WS-wsSubscribeMonetaryRechargePlan-STAT-TABLE";

        public TableSubscriptionWSSubscribeMonetaryRechargePlanTableImpl(SnmpMib myMib, MBeanServer server) {
            super(myMib, server);
        }

        @Override
        public synchronized void addEntry(SubscriptionWSSubscribeMonetaryRechargePlanEntryMBean entry, ObjectName name) throws SnmpStatusException {
            super.addEntry(entry, name);

            if(server != null){
                try {
                    server.registerMBean(entry, name);
                } catch (Exception e) {
                    LogManager.getLogger().error(MODULE, "SubscriptionWS method " + ISubscriptionWS.WS_SUBSCRIBE_MONETARY_RECHARGE_PLAN
                            + " ResultCode statistics entry for ( "
                            + entry.getSubscribeMonetaryRechargePlanResultCodeName() + "," + entry.getSubscribeMonetaryRechargePlanResultCode() + " ) " +
                            " not added in TableSubscriptionWSSubscribeMonetaryRechargePlanTable. Reason: " + e.getMessage());
                    LogManager.getLogger().trace(MODULE, e);
                }
            }
        }
}
