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
 * The class is used for implementing the "SubscriberWSUpdateAlternateIdEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.10.25.1.
 */
public class SubscriberWSUpdateAlternateIdEntry implements SubscriberWSUpdateAlternateIdEntryMBean, Serializable {

    /**
     * Variable for storing the value of "UpdateAlternateIdResultCodeCounters".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.25.1.3".
     *
     * "SubscriberProvisioningWS method wsUpdateAlternateId ResultCode Specific statistics."
     *
     */
    protected Long UpdateAlternateIdResultCodeCounters = new Long(1);

    /**
     * Variable for storing the value of "UpdateAlternateIdResultCodeName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.25.1.2".
     *
     * "SubscriberProvisioningWS method wsUpdateAlternateId ResultCode Name."
     *
     */
    protected String UpdateAlternateIdResultCodeName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "UpdateAlternateIdResultCode".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.25.1.1".
     *
     * "SubscriberProvisioningWS method wsUpdateAlternateId ResultCode."
     *
     */
    protected Integer UpdateAlternateIdResultCode = new Integer(1);


    /**
     * Constructor for the "SubscriberWSUpdateAlternateIdEntry" group.
     */
    public SubscriberWSUpdateAlternateIdEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "UpdateAlternateIdResultCodeCounters" variable.
     */
    public Long getUpdateAlternateIdResultCodeCounters() throws SnmpStatusException {
        return UpdateAlternateIdResultCodeCounters;
    }

    /**
     * Getter for the "UpdateAlternateIdResultCodeName" variable.
     */
    public String getUpdateAlternateIdResultCodeName() throws SnmpStatusException {
        return UpdateAlternateIdResultCodeName;
    }

    /**
     * Getter for the "UpdateAlternateIdResultCode" variable.
     */
    public Integer getUpdateAlternateIdResultCode() throws SnmpStatusException {
        return UpdateAlternateIdResultCode;
    }

}
