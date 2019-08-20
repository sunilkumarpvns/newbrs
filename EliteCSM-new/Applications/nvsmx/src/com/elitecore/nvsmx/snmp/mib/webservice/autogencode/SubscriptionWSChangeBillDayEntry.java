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
 * The class is used for implementing the "SubscriptionWSChangeBillDayEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.9.28.1.
 */
public class SubscriptionWSChangeBillDayEntry implements SubscriptionWSChangeBillDayEntryMBean, Serializable {

    /**
     * Variable for storing the value of "ChangeBillDayResultCodeCounters".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.28.1.3".
     *
     * "SubscriptionWS method wsChangeBillDay resultCode specific Counters."
     *
     */
    protected Long ChangeBillDayResultCodeCounters = new Long(1);

    /**
     * Variable for storing the value of "ChangeBillDayResultCodeName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.28.1.2".
     *
     * "subscriptionWS wsChangeBillDay ResultCode Name."
     *
     */
    protected String ChangeBillDayResultCodeName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "ChangeBillDayResultCode".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.28.1.1".
     *
     * "SubscriptionWS method wsChangeBillDay ResultCode"
     *
     */
    protected Integer ChangeBillDayResultCode = new Integer(1);


    /**
     * Constructor for the "SubscriptionWSChangeBillDayEntry" group.
     */
    public SubscriptionWSChangeBillDayEntry(SnmpMib myMib) {
    }

    public SubscriptionWSChangeBillDayEntry() {
    }

    /**
     * Getter for the "ChangeBillDayResultCodeCounters" variable.
     */
    public Long getChangeBillDayResultCodeCounters() throws SnmpStatusException {
        return ChangeBillDayResultCodeCounters;
    }

    /**
     * Getter for the "ChangeBillDayResultCodeName" variable.
     */
    public String getChangeBillDayResultCodeName() throws SnmpStatusException {
        return ChangeBillDayResultCodeName;
    }

    /**
     * Getter for the "ChangeBillDayResultCode" variable.
     */
    public Integer getChangeBillDayResultCode() throws SnmpStatusException {
        return ChangeBillDayResultCode;
    }

}
