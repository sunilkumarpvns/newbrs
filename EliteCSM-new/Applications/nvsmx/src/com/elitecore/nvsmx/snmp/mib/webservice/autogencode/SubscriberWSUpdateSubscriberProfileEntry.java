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
 * The class is used for implementing the "SubscriberWSUpdateSubscriberProfileEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.10.8.1.
 */
public class SubscriberWSUpdateSubscriberProfileEntry implements SubscriberWSUpdateSubscriberProfileEntryMBean, Serializable {

	public SubscriberWSUpdateSubscriberProfileEntry() {
		// TODO Auto-generated constructor stub
	}
    /**
     * Variable for storing the value of "UpdateSubscriberProfileResultCodeCounters".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.8.1.3".
     *
     * "SubscriberProvisioningWS method wsUpdateSubscriberProfile ResultCode Specific statistics."
     *
     */
    protected Long UpdateSubscriberProfileResultCodeCounters = new Long(1);

    /**
     * Variable for storing the value of "UpdateSubscriberProfileResultCodeName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.8.1.2".
     *
     * "SubscriberProvisioningWS method wsUpdateSubscriberProfile ResultCode Name."
     *
     */
    protected String UpdateSubscriberProfileResultCodeName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "UpdateSubscriberProfileResultCode".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.8.1.1".
     *
     * "SubscriberProvisioningWS method wsUpdateSubscriberProfile ResultCode."
     *
     */
    protected Integer UpdateSubscriberProfileResultCode = new Integer(1);


    /**
     * Constructor for the "SubscriberWSUpdateSubscriberProfileEntry" group.
     */
    public SubscriberWSUpdateSubscriberProfileEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "UpdateSubscriberProfileResultCodeCounters" variable.
     */
    public Long getUpdateSubscriberProfileResultCodeCounters() throws SnmpStatusException {
        return UpdateSubscriberProfileResultCodeCounters;
    }

    /**
     * Getter for the "UpdateSubscriberProfileResultCodeName" variable.
     */
    public String getUpdateSubscriberProfileResultCodeName() throws SnmpStatusException {
        return UpdateSubscriberProfileResultCodeName;
    }

    /**
     * Getter for the "UpdateSubscriberProfileResultCode" variable.
     */
    public Integer getUpdateSubscriberProfileResultCode() throws SnmpStatusException {
        return UpdateSubscriberProfileResultCode;
    }

}
