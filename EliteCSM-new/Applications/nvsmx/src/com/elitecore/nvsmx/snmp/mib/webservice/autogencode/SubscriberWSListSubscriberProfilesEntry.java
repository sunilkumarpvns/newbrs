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
 * The class is used for implementing the "SubscriberWSListSubscriberProfilesEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.10.13.1.
 */
public class SubscriberWSListSubscriberProfilesEntry implements SubscriberWSListSubscriberProfilesEntryMBean, Serializable {

	public SubscriberWSListSubscriberProfilesEntry() {
		// TODO Auto-generated constructor stub
	}
    /**
     * Variable for storing the value of "ListSubscriberProfilesResultCodeCounters".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.13.1.3".
     *
     * "SubscriberProvisioningWS method wsListSubscriberProfiles ResultCode Specific statistics."
     *
     */
    protected Long ListSubscriberProfilesResultCodeCounters = new Long(1);

    /**
     * Variable for storing the value of "ListSubscriberProfilesResultCodeName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.13.1.2".
     *
     * "SubscriberProvisioningWS method wsListSubscriberProfiles ResultCode Name."
     *
     */
    protected String ListSubscriberProfilesResultCodeName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "ListSubscriberProfilesResultCode".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.13.1.1".
     *
     * "SubscriberProvisioningWS method wsListSubscriberProfiles ResultCode."
     *
     */
    protected Integer ListSubscriberProfilesResultCode = new Integer(1);


    /**
     * Constructor for the "SubscriberWSListSubscriberProfilesEntry" group.
     */
    public SubscriberWSListSubscriberProfilesEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "ListSubscriberProfilesResultCodeCounters" variable.
     */
    public Long getListSubscriberProfilesResultCodeCounters() throws SnmpStatusException {
        return ListSubscriberProfilesResultCodeCounters;
    }

    /**
     * Getter for the "ListSubscriberProfilesResultCodeName" variable.
     */
    public String getListSubscriberProfilesResultCodeName() throws SnmpStatusException {
        return ListSubscriberProfilesResultCodeName;
    }

    /**
     * Getter for the "ListSubscriberProfilesResultCode" variable.
     */
    public Integer getListSubscriberProfilesResultCode() throws SnmpStatusException {
        return ListSubscriberProfilesResultCode;
    }

}
