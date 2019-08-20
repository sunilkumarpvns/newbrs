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
 * The class is used for implementing the "SubscriptionWSListTopUpPackagesEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.9.15.1.
 */
public class SubscriptionWSListTopUpPackagesEntry implements SubscriptionWSListTopUpPackagesEntryMBean, Serializable {

	public SubscriptionWSListTopUpPackagesEntry() {
		// TODO Auto-generated constructor stub
	}
    /**
     * Variable for storing the value of "ListTopUpPackagesResultCodeCounters".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.15.1.3".
     *
     * "SubscriptionWS method wsListTopUpPackages resultCode specific Counters."
     *
     */
    protected Long ListTopUpPackagesResultCodeCounters = new Long(1);

    /**
     * Variable for storing the value of "ListTopUpPackagesResultCodeName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.15.1.2".
     *
     * "subscriptionWS wsListTopUpPackages ResultCode Name."
     *
     */
    protected String ListTopUpPackagesResultCodeName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "ListTopUpPackagesResultCode".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.15.1.1".
     *
     * "SubscriptionWS method wsListTopUpPackages ResultCode"
     *
     */
    protected Integer ListTopUpPackagesResultCode = new Integer(1);


    /**
     * Constructor for the "SubscriptionWSListTopUpPackagesEntry" group.
     */
    public SubscriptionWSListTopUpPackagesEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "ListTopUpPackagesResultCodeCounters" variable.
     */
    public Long getListTopUpPackagesResultCodeCounters() throws SnmpStatusException {
        return ListTopUpPackagesResultCodeCounters;
    }

    /**
     * Getter for the "ListTopUpPackagesResultCodeName" variable.
     */
    public String getListTopUpPackagesResultCodeName() throws SnmpStatusException {
        return ListTopUpPackagesResultCodeName;
    }

    /**
     * Getter for the "ListTopUpPackagesResultCode" variable.
     */
    public Integer getListTopUpPackagesResultCode() throws SnmpStatusException {
        return ListTopUpPackagesResultCode;
    }

}
