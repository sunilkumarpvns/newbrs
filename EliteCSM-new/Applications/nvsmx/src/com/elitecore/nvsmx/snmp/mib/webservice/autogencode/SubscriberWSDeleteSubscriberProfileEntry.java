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
 * The class is used for implementing the "SubscriberWSDeleteSubscriberProfileEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.10.9.1.
 */
public class SubscriberWSDeleteSubscriberProfileEntry implements SubscriberWSDeleteSubscriberProfileEntryMBean, Serializable {

	public SubscriberWSDeleteSubscriberProfileEntry() {
		// TODO Auto-generated constructor stub
	}
    /**
     * Variable for storing the value of "DeleteSubscriberProfileResultCodeCounters".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.9.1.3".
     *
     * "SubscriberProvisioningWS method wsDeleteSubscriberProfile ResultCode Specific statistics."
     *
     */
    protected Long DeleteSubscriberProfileResultCodeCounters = new Long(1);

    /**
     * Variable for storing the value of "DeleteSubscriberProfileResultCodeName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.9.1.2".
     *
     * "SubscriberProvisioningWS method wsDeleteSubscriberProfile ResultCode Name."
     *
     */
    protected String DeleteSubscriberProfileResultCodeName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "DeleteSubscriberProfileResultCode".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.9.1.1".
     *
     * "SubscriberProvisioningWS method wsDeleteSubscriberProfile ResultCode."
     *
     */
    protected Integer DeleteSubscriberProfileResultCode = new Integer(1);


    /**
     * Constructor for the "SubscriberWSDeleteSubscriberProfileEntry" group.
     */
    public SubscriberWSDeleteSubscriberProfileEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "DeleteSubscriberProfileResultCodeCounters" variable.
     */
    public Long getDeleteSubscriberProfileResultCodeCounters() throws SnmpStatusException {
        return DeleteSubscriberProfileResultCodeCounters;
    }

    /**
     * Getter for the "DeleteSubscriberProfileResultCodeName" variable.
     */
    public String getDeleteSubscriberProfileResultCodeName() throws SnmpStatusException {
        return DeleteSubscriberProfileResultCodeName;
    }

    /**
     * Getter for the "DeleteSubscriberProfileResultCode" variable.
     */
    public Integer getDeleteSubscriberProfileResultCode() throws SnmpStatusException {
        return DeleteSubscriberProfileResultCode;
    }

}
