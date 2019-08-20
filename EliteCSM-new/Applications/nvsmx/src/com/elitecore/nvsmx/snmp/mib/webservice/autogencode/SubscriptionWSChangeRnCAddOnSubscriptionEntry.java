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
 * The class is used for implementing the "SubscriptionWSChangeRnCAddOnSubscriptionEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.9.20.1.
 */
public class SubscriptionWSChangeRnCAddOnSubscriptionEntry implements SubscriptionWSChangeRnCAddOnSubscriptionEntryMBean, Serializable {

    /**
     * Variable for storing the value of "ChangeRnCAddOnSubscriptionResultCodeCounters".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.20.1.3".
     *
     * "SubscriptionWS method wsChangeRnCAddOnSubscription resultCode specific Counters."
     *
     */
    protected Long ChangeRnCAddOnSubscriptionResultCodeCounters = new Long(1);

    /**
     * Variable for storing the value of "ChangeRnCAddOnSubscriptionResultCodeName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.20.1.2".
     *
     * "subscriptionWS wsChangeRnCAddOnSubscription ResultCode Name."
     *
     */
    protected String ChangeRnCAddOnSubscriptionResultCodeName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "ChangeRnCAddOnSubscriptionResultCode".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.20.1.1".
     *
     * "SubscriptionWS method wsChangeRnCAddOnSubscription ResultCode"
     *
     */
    protected Integer ChangeRnCAddOnSubscriptionResultCode = new Integer(1);


    /**
     * Constructor for the "SubscriptionWSChangeRnCAddOnSubscriptionEntry" group.
     */
    public SubscriptionWSChangeRnCAddOnSubscriptionEntry(SnmpMib myMib) {
    }

    public SubscriptionWSChangeRnCAddOnSubscriptionEntry() {
    }

    /**
     * Getter for the "ChangeRnCAddOnSubscriptionResultCodeCounters" variable.
     */
    public Long getChangeRnCAddOnSubscriptionResultCodeCounters() throws SnmpStatusException {
        return ChangeRnCAddOnSubscriptionResultCodeCounters;
    }

    /**
     * Getter for the "ChangeRnCAddOnSubscriptionResultCodeName" variable.
     */
    public String getChangeRnCAddOnSubscriptionResultCodeName() throws SnmpStatusException {
        return ChangeRnCAddOnSubscriptionResultCodeName;
    }

    /**
     * Getter for the "ChangeRnCAddOnSubscriptionResultCode" variable.
     */
    public Integer getChangeRnCAddOnSubscriptionResultCode() throws SnmpStatusException {
        return ChangeRnCAddOnSubscriptionResultCode;
    }

}
