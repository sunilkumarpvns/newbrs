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
 * The class is used for implementing the "SubscriptionWSListRnCPackagesEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.9.24.1.
 */
public class SubscriptionWSListRnCPackagesEntry implements SubscriptionWSListRnCPackagesEntryMBean, Serializable {

    /**
     * Variable for storing the value of "ListRnCPackagesResultCodeCounters".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.24.1.3".
     *
     * "SubscriptionWS method wsListRnCPackages resultCode specific Counters."
     *
     */
    protected Long ListRnCPackagesResultCodeCounters = new Long(1);

    /**
     * Variable for storing the value of "ListRnCPackagesResultCodeName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.24.1.2".
     *
     * "subscriptionWS wsListRnCPackages ResultCode Name."
     *
     */
    protected String ListRnCPackagesResultCodeName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "ListRnCPackagesResultCode".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.24.1.1".
     *
     * "SubscriptionWS method wsListRnCPackages ResultCode"
     *
     */
    protected Integer ListRnCPackagesResultCode = new Integer(1);


    /**
     * Constructor for the "SubscriptionWSListRnCPackagesEntry" group.
     */
    public SubscriptionWSListRnCPackagesEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "ListRnCPackagesResultCodeCounters" variable.
     */
    public Long getListRnCPackagesResultCodeCounters() throws SnmpStatusException {
        return ListRnCPackagesResultCodeCounters;
    }

    /**
     * Getter for the "ListRnCPackagesResultCodeName" variable.
     */
    public String getListRnCPackagesResultCodeName() throws SnmpStatusException {
        return ListRnCPackagesResultCodeName;
    }

    /**
     * Getter for the "ListRnCPackagesResultCode" variable.
     */
    public Integer getListRnCPackagesResultCode() throws SnmpStatusException {
        return ListRnCPackagesResultCode;
    }

}
