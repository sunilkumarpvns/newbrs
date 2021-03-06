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
 * The class is used for implementing the "SubscriberWSPurgeSubscribersEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.10.15.1.
 */
public class SubscriberWSPurgeSubscribersEntry implements SubscriberWSPurgeSubscribersEntryMBean, Serializable {

    /**
     * Variable for storing the value of "PurgeSubscribersResultCodeCounters".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.15.1.3".
     *
     * "SubscriberProvisioningWS method wsPurgeSubscribers ResultCode Specific statistics."
     *
     */
    protected Long PurgeSubscribersResultCodeCounters = new Long(1);

    /**
     * Variable for storing the value of "PurgeSubscribersResultCodeName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.15.1.2".
     *
     * "SubscriberProvisioningWS method wsPurgeSubscribers ResultCode Name."
     *
     */
    protected String PurgeSubscribersResultCodeName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "PurgeSubscribersResultCode".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.15.1.1".
     *
     * "SubscriberProvisioningWS method wsPurgeSubscribers ResultCode."
     *
     */
    protected Integer PurgeSubscribersResultCode = new Integer(1);


    /**
     * Constructor for the "SubscriberWSPurgeSubscribersEntry" group.
     */
    public SubscriberWSPurgeSubscribersEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "PurgeSubscribersResultCodeCounters" variable.
     */
    public Long getPurgeSubscribersResultCodeCounters() throws SnmpStatusException {
        return PurgeSubscribersResultCodeCounters;
    }

    /**
     * Getter for the "PurgeSubscribersResultCodeName" variable.
     */
    public String getPurgeSubscribersResultCodeName() throws SnmpStatusException {
        return PurgeSubscribersResultCodeName;
    }

    /**
     * Getter for the "PurgeSubscribersResultCode" variable.
     */
    public Integer getPurgeSubscribersResultCode() throws SnmpStatusException {
        return PurgeSubscribersResultCode;
    }

}
