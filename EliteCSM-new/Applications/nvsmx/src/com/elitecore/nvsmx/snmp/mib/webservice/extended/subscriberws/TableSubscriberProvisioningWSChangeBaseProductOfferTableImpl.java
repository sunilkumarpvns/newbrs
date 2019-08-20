package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSChangeBaseProductOfferEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriberProvisioningWSChangeBaseProductOfferTable;
import com.elitecore.nvsmx.ws.subscriberprovisioning.ISubscriberProvisioningWS;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableSubscriberProvisioningWSChangeBaseProductOfferTableImpl extends TableSubscriberProvisioningWSChangeBaseProductOfferTable {

    private static final long serialVersionUID = 1L;
    private static final String MODULE = "SUBSCRIBER-WS-wsChangeBaseProductOffer-STAT-TABLE";

    public TableSubscriberProvisioningWSChangeBaseProductOfferTableImpl(SnmpMib myMib, MBeanServer server) {
        super(myMib, server);
    }

    @Override
    public synchronized void addEntry(SubscriberWSChangeBaseProductOfferEntryMBean entry, ObjectName name) throws SnmpStatusException {
        super.addEntry(entry, name);

        if (server != null) {
            try {
                server.registerMBean(entry, name);
            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "SubscriberProvisioningWS method " + ISubscriberProvisioningWS.WS_CHANGE_BASE_PRODUCT_OFFER
                        + " ResultCode statistics entry for ( "
                        + entry.getChangeBaseProductOfferResultCodeName() + "," + entry.getChangeBaseProductOfferResultCode() + " ) " +
                        " not added in TableSubscriberProvisioningWSChangeBaseProductOfferTable. Reason: " + e.getMessage());
                LogManager.getLogger().trace(MODULE, e);
            }
        }
    }
}
