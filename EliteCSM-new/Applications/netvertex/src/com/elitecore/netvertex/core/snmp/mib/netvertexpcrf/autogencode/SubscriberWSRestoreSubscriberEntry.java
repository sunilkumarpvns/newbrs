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
 * The class is used for implementing the "SubscriberWSRestoreSubscriberEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.10.17.1.
 */
public class SubscriberWSRestoreSubscriberEntry implements SubscriberWSRestoreSubscriberEntryMBean, Serializable {

    /**
     * Variable for storing the value of "RestoreSubscriberResultCodeCounters".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.17.1.3".
     *
     * "SubscriberProvisioningWS method wsRestoreSubscriber ResultCode Specific statistics."
     *
     */
    protected Long RestoreSubscriberResultCodeCounters = new Long(1);

    /**
     * Variable for storing the value of "RestoreSubscriberResultCodeName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.17.1.2".
     *
     * "SubscriberProvisioningWS method wsRestoreSubscriber ResultCode Name."
     *
     */
    protected String RestoreSubscriberResultCodeName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "RestoreSubscriberResultCode".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.17.1.1".
     *
     * "SubscriberProvisioningWS method wsRestoreSubscriber ResultCode."
     *
     */
    protected Integer RestoreSubscriberResultCode = new Integer(1);


    /**
     * Constructor for the "SubscriberWSRestoreSubscriberEntry" group.
     */
    public SubscriberWSRestoreSubscriberEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "RestoreSubscriberResultCodeCounters" variable.
     */
    public Long getRestoreSubscriberResultCodeCounters() throws SnmpStatusException {
        return RestoreSubscriberResultCodeCounters;
    }

    /**
     * Getter for the "RestoreSubscriberResultCodeName" variable.
     */
    public String getRestoreSubscriberResultCodeName() throws SnmpStatusException {
        return RestoreSubscriberResultCodeName;
    }

    /**
     * Getter for the "RestoreSubscriberResultCode" variable.
     */
    public Integer getRestoreSubscriberResultCode() throws SnmpStatusException {
        return RestoreSubscriberResultCode;
    }

}