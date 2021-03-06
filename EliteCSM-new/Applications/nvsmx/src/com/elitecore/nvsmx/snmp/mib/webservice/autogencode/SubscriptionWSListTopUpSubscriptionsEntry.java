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
 * The class is used for implementing the "SubscriptionWSListTopUpSubscriptionsEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.9.10.1.
 */
public class SubscriptionWSListTopUpSubscriptionsEntry implements SubscriptionWSListTopUpSubscriptionsEntryMBean, Serializable {

	public SubscriptionWSListTopUpSubscriptionsEntry() {
		// TODO Auto-generated constructor stub
	}
    /**
     * Variable for storing the value of "ListTopUpSubscriptionsResultCodeCounters".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.10.1.3".
     *
     * "SubscriptionWS method wsListTopUpSubscriptions ResultCode Specific counters."
     *
     */
    protected Long ListTopUpSubscriptionsResultCodeCounters = new Long(1);

    /**
     * Variable for storing the value of "ListTopUpSubscriptionsResultCodeName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.10.1.2".
     *
     * "subscriptionWS wsListTopUpSubscriptions ResultCode Name."
     *
     */
    protected String ListTopUpSubscriptionsResultCodeName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "ListTopUpSubscriptionsResultCode".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.10.1.1".
     *
     * "SubscriptionWS method wsListTopUpSubscriptions ResultCode"
     *
     */
    protected Integer ListTopUpSubscriptionsResultCode = new Integer(1);


    /**
     * Constructor for the "SubscriptionWSListTopUpSubscriptionsEntry" group.
     */
    public SubscriptionWSListTopUpSubscriptionsEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "ListTopUpSubscriptionsResultCodeCounters" variable.
     */
    public Long getListTopUpSubscriptionsResultCodeCounters() throws SnmpStatusException {
        return ListTopUpSubscriptionsResultCodeCounters;
    }

    /**
     * Getter for the "ListTopUpSubscriptionsResultCodeName" variable.
     */
    public String getListTopUpSubscriptionsResultCodeName() throws SnmpStatusException {
        return ListTopUpSubscriptionsResultCodeName;
    }

    /**
     * Getter for the "ListTopUpSubscriptionsResultCode" variable.
     */
    public Integer getListTopUpSubscriptionsResultCode() throws SnmpStatusException {
        return ListTopUpSubscriptionsResultCode;
    }

}
