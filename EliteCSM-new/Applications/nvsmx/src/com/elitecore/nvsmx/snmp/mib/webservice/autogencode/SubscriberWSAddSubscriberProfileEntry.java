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
 * The class is used for implementing the "SubscriberWSAddSubscriberProfileEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.10.7.1.
 */
public class SubscriberWSAddSubscriberProfileEntry implements SubscriberWSAddSubscriberProfileEntryMBean, Serializable {

	public SubscriberWSAddSubscriberProfileEntry() {
		// TODO Auto-generated constructor stub
	}
    /**
     * Variable for storing the value of "AddSubscriberProfileResultCodeCounters".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.7.1.3".
     *
     * "SubscriberProvisioningWS method wSAddSubscriberProfile ResultCode Specific statistics."
     *
     */
    protected Long AddSubscriberProfileResultCodeCounters = new Long(1);

    /**
     * Variable for storing the value of "AddSubscriberProfileResultCodeName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.7.1.2".
     *
     * "SubscriberProvisioningWS method wSAddSubscriberProfile ResultCode Name."
     *
     */
    protected String AddSubscriberProfileResultCodeName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "AddSubscriberProfileResultCode".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.7.1.1".
     *
     * "SubscriberProvisioningWS method wSAddSubscriberProfile ResultCode."
     *
     */
    protected Integer AddSubscriberProfileResultCode = new Integer(1);


    /**
     * Constructor for the "SubscriberWSAddSubscriberProfileEntry" group.
     */
    public SubscriberWSAddSubscriberProfileEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "AddSubscriberProfileResultCodeCounters" variable.
     */
    public Long getAddSubscriberProfileResultCodeCounters() throws SnmpStatusException {
        return AddSubscriberProfileResultCodeCounters;
    }

    /**
     * Getter for the "AddSubscriberProfileResultCodeName" variable.
     */
    public String getAddSubscriberProfileResultCodeName() throws SnmpStatusException {
        return AddSubscriberProfileResultCodeName;
    }

    /**
     * Getter for the "AddSubscriberProfileResultCode" variable.
     */
    public Integer getAddSubscriberProfileResultCode() throws SnmpStatusException {
        return AddSubscriberProfileResultCode;
    }

}
