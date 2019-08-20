package com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode;

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
 * The class is used for implementing the "SubscriptionWSGetNonMonetaryBalanceEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.9.23.1.
 */
public class SubscriptionWSGetNonMonetaryBalanceEntry implements SubscriptionWSGetNonMonetaryBalanceEntryMBean, Serializable {

    /**
     * Variable for storing the value of "GetNonMonetaryBalanceResultCodeCounters".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.23.1.3".
     *
     * "SubscriptionWS method wsGetNonMonetaryBalance resultCode specific Counters."
     *
     */
    protected Long GetNonMonetaryBalanceResultCodeCounters = new Long(1);

    /**
     * Variable for storing the value of "GetNonMonetaryBalanceResultCodeName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.23.1.2".
     *
     * "subscriptionWS wsGetNonMonetaryBalance ResultCode Name."
     *
     */
    protected String GetNonMonetaryBalanceResultCodeName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "GetNonMonetaryBalanceResultCode".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.23.1.1".
     *
     * "SubscriptionWS method wsGetNonMonetaryBalance ResultCode"
     *
     */
    protected Integer GetNonMonetaryBalanceResultCode = new Integer(1);


    /**
     * Constructor for the "SubscriptionWSGetNonMonetaryBalanceEntry" group.
     */
    public SubscriptionWSGetNonMonetaryBalanceEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "GetNonMonetaryBalanceResultCodeCounters" variable.
     */
    public Long getGetNonMonetaryBalanceResultCodeCounters() throws SnmpStatusException {
        return GetNonMonetaryBalanceResultCodeCounters;
    }

    /**
     * Getter for the "GetNonMonetaryBalanceResultCodeName" variable.
     */
    public String getGetNonMonetaryBalanceResultCodeName() throws SnmpStatusException {
        return GetNonMonetaryBalanceResultCodeName;
    }

    /**
     * Getter for the "GetNonMonetaryBalanceResultCode" variable.
     */
    public Integer getGetNonMonetaryBalanceResultCode() throws SnmpStatusException {
        return GetNonMonetaryBalanceResultCode;
    }

}
