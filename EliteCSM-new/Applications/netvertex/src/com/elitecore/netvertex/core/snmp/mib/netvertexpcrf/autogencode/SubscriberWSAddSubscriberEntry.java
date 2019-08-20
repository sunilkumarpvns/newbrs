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
 * The class is used for implementing the "SubscriberWSAddSubscriberEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.10.20.1.
 */
public class SubscriberWSAddSubscriberEntry implements SubscriberWSAddSubscriberEntryMBean, Serializable {

    /**
     * Variable for storing the value of "AddSubscriberResultCodeCounters".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.20.1.3".
     *
     * "SubscriberProvisioningWS method wsAddSubscriber ResultCode Specific statistics."
     *
     */
    protected Long AddSubscriberResultCodeCounters = new Long(1);

    /**
     * Variable for storing the value of "AddSubscriberResultCodeName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.20.1.2".
     *
     * "SubscriberProvisioningWS method wsAddSubscriber ResultCode Name."
     *
     */
    protected String AddSubscriberResultCodeName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "AddSubscriberResultCode".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.20.1.1".
     *
     * "SubscriberProvisioningWS method wsAddSubscriber ResultCode."
     *
     */
    protected Integer AddSubscriberResultCode = new Integer(1);


    /**
     * Constructor for the "SubscriberWSAddSubscriberEntry" group.
     */
    public SubscriberWSAddSubscriberEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "AddSubscriberResultCodeCounters" variable.
     */
    public Long getAddSubscriberResultCodeCounters() throws SnmpStatusException {
        return AddSubscriberResultCodeCounters;
    }

    /**
     * Getter for the "AddSubscriberResultCodeName" variable.
     */
    public String getAddSubscriberResultCodeName() throws SnmpStatusException {
        return AddSubscriberResultCodeName;
    }

    /**
     * Getter for the "AddSubscriberResultCode" variable.
     */
    public Integer getAddSubscriberResultCode() throws SnmpStatusException {
        return AddSubscriberResultCode;
    }

}
