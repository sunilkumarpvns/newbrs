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
 * The class is used for implementing the "SubscriberWSAddSubscribersEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.10.21.1.
 */
public class SubscriberWSAddSubscribersEntry implements SubscriberWSAddSubscribersEntryMBean, Serializable {

	public SubscriberWSAddSubscribersEntry() {
		// TODO Auto-generated constructor stub
	}
    /**
     * Variable for storing the value of "AddSubscribersResultCodeCounters".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.21.1.3".
     *
     * "SubscriberProvisioningWS method wsAddSubscribers ResultCode Specific statistics."
     *
     */
    protected Long AddSubscribersResultCodeCounters = new Long(1);

    /**
     * Variable for storing the value of "AddSubscribersResultCodeName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.21.1.2".
     *
     * "SubscriberProvisioningWS method wsAddSubscribers ResultCode Name."
     *
     */
    protected String AddSubscribersResultCodeName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "AddSubscribersResultCode".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.10.21.1.1".
     *
     * "SubscriberProvisioningWS method wsAddSubscribers ResultCode."
     *
     */
    protected Integer AddSubscribersResultCode = new Integer(1);


    /**
     * Constructor for the "SubscriberWSAddSubscribersEntry" group.
     */
    public SubscriberWSAddSubscribersEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "AddSubscribersResultCodeCounters" variable.
     */
    public Long getAddSubscribersResultCodeCounters() throws SnmpStatusException {
        return AddSubscribersResultCodeCounters;
    }

    /**
     * Getter for the "AddSubscribersResultCodeName" variable.
     */
    public String getAddSubscribersResultCodeName() throws SnmpStatusException {
        return AddSubscribersResultCodeName;
    }

    /**
     * Getter for the "AddSubscribersResultCode" variable.
     */
    public Integer getAddSubscribersResultCode() throws SnmpStatusException {
        return AddSubscribersResultCode;
    }

}
