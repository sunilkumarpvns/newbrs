package com.elitecore.nvsmx.snmp.mib.webservice.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB.
//

// java imports
//
import java.io.Serializable;

// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

// jdmk imports
//
import com.sun.management.snmp.agent.SnmpMib;

/**
 * The class is used for implementing the "SubscriptionWSSubscribeBoDEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.9.29.1.
 */
public class SubscriptionWSSubscribeBoDEntry implements SubscriptionWSSubscribeBoDEntryMBean, Serializable {

    /**
     * Variable for storing the value of "SubscribeBoDResultCodeCounters".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.29.1.3".
     *
     * "SubscriptionWS method wsSubscribeBoD resultCode specific Counters."
     *
     */
    protected Long SubscribeBoDResultCodeCounters = new Long(1);

    /**
     * Variable for storing the value of "SubscribeBoDResultCodeName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.29.1.2".
     *
     * "subscriptionWS wsSubscribeBoD ResultCode Name."
     *
     */
    protected String SubscribeBoDResultCodeName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "SubscribeBoDResultCode".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.29.1.1".
     *
     * "SubscriptionWS method wsSubscribeBoD ResultCode"
     *
     */
    protected Integer SubscribeBoDResultCode = new Integer(1);


    /**
     * Constructor for the "SubscriptionWSSubscribeBoDEntry" group.
     */
    public SubscriptionWSSubscribeBoDEntry(SnmpMib myMib) {
    }

    public SubscriptionWSSubscribeBoDEntry() {
    }

    /**
     * Getter for the "SubscribeBoDResultCodeCounters" variable.
     */
    public Long getSubscribeBoDResultCodeCounters() throws SnmpStatusException {
        return SubscribeBoDResultCodeCounters;
    }

    /**
     * Getter for the "SubscribeBoDResultCodeName" variable.
     */
    public String getSubscribeBoDResultCodeName() throws SnmpStatusException {
        return SubscribeBoDResultCodeName;
    }

    /**
     * Getter for the "SubscribeBoDResultCode" variable.
     */
    public Integer getSubscribeBoDResultCode() throws SnmpStatusException {
        return SubscribeBoDResultCode;
    }

}
