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
 * The class is used for implementing the "SubscriptionWSMethodWiseEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.9.5.1.
 */
public class SubscriptionWSMethodWiseEntry implements SubscriptionWSMethodWiseEntryMBean, Serializable {

    /**
     * Variable for storing the value of "SubscriptionWSMethodAvgTPS".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.5.1.6".
     *
     * "Method wise average TPS in SubscriptionWS for last one Minute"
     *
     */
    protected Long SubscriptionWSMethodAvgTPS = new Long(1);

    /**
     * Variable for storing the value of "SubscriptionWSMethodLastMinuteTotalRequests".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.5.1.5".
     *
     * "Total number of SubscriptionWS method wise Requests received in Last Minute by Policy Designer."
     *
     */
    protected Long SubscriptionWSMethodLastMinuteTotalRequests = new Long(1);

    /**
     * Variable for storing the value of "SubscriptionWSMethodTotalResponses".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.5.1.4".
     *
     * "Total number of SubscriptionWS method wise Responses given by particular method  in Policy Designer."
     *
     */
    protected Long SubscriptionWSMethodTotalResponses = new Long(1);

    /**
     * Variable for storing the value of "SubscriptionWSMethodTotalRequests".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.5.1.3".
     *
     * "Total number of SubscriptionWS method wise Requests received by particular method in Policy Designer."
     *
     */
    protected Long SubscriptionWSMethodTotalRequests = new Long(1);

    /**
     * Variable for storing the value of "SubscriptionWSMethodName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.5.1.2".
     *
     * "SubscriptionWS method's Name"
     *
     */
    protected String SubscriptionWSMethodName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "SubscriptionWSMethodIndex".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.5.1.1".
     *
     * "SubscriptionWS method's Index"
     *
     */
    protected Integer SubscriptionWSMethodIndex = new Integer(1);


    /**
     * Constructor for the "SubscriptionWSMethodWiseEntry" group.
     */
    public SubscriptionWSMethodWiseEntry(SnmpMib myMib) {
    }

    public SubscriptionWSMethodWiseEntry() {
    }

    /**
     * Getter for the "SubscriptionWSMethodAvgTPS" variable.
     */
    public Long getSubscriptionWSMethodAvgTPS() throws SnmpStatusException {
        return SubscriptionWSMethodAvgTPS;
    }

    /**
     * Getter for the "SubscriptionWSMethodLastMinuteTotalRequests" variable.
     */
    public Long getSubscriptionWSMethodLastMinuteTotalRequests() throws SnmpStatusException {
        return SubscriptionWSMethodLastMinuteTotalRequests;
    }

    /**
     * Getter for the "SubscriptionWSMethodTotalResponses" variable.
     */
    public Long getSubscriptionWSMethodTotalResponses() throws SnmpStatusException {
        return SubscriptionWSMethodTotalResponses;
    }

    /**
     * Getter for the "SubscriptionWSMethodTotalRequests" variable.
     */
    public Long getSubscriptionWSMethodTotalRequests() throws SnmpStatusException {
        return SubscriptionWSMethodTotalRequests;
    }

    /**
     * Getter for the "SubscriptionWSMethodName" variable.
     */
    public String getSubscriptionWSMethodName() throws SnmpStatusException {
        return SubscriptionWSMethodName;
    }

    /**
     * Getter for the "SubscriptionWSMethodIndex" variable.
     */
    public Integer getSubscriptionWSMethodIndex() throws SnmpStatusException {
        return SubscriptionWSMethodIndex;
    }

}
