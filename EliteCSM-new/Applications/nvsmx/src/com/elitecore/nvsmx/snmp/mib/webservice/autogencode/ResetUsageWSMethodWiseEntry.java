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
 * The class is used for implementing the "ResetUsageWSMethodWiseEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.12.5.1.
 */
public class ResetUsageWSMethodWiseEntry implements ResetUsageWSMethodWiseEntryMBean, Serializable {

	public ResetUsageWSMethodWiseEntry() {
		// TODO Auto-generated constructor stub
	}
    /**
     * Variable for storing the value of "ResetUsageWSMethodAvgTPS".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.12.5.1.6".
     *
     * "Method's average TPS in ResetUsageWS for last one minute."
     *
     */
    protected Long ResetUsageWSMethodAvgTPS = new Long(1);

    /**
     * Variable for storing the value of "ResetUsageWSMethodLastMinuteTotalRequests".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.12.5.1.5".
     *
     * "Total number of ResetUsageWS method wise Requests received in Last Minute by Policy Designer."
     *
     */
    protected Long ResetUsageWSMethodLastMinuteTotalRequests = new Long(1);

    /**
     * Variable for storing the value of "ResetUsageWSMethodTotalResponses".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.12.5.1.4".
     *
     * "Total number of ResetUsageWS method wise Responses given by Policy Designer."
     *
     */
    protected Long ResetUsageWSMethodTotalResponses = new Long(1);

    /**
     * Variable for storing the value of "ResetUsageWSMethodTotalRequests".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.12.5.1.3".
     *
     * "Total number of ResetUsageWS method wise Requests received by Policy Designer."
     *
     */
    protected Long ResetUsageWSMethodTotalRequests = new Long(1);

    /**
     * Variable for storing the value of "ResetUsageWSMethodName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.12.5.1.2".
     *
     * "ResetUsageWS method's Name."
     *
     */
    protected String ResetUsageWSMethodName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "ResetUsageWSMethodIndex".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.12.5.1.1".
     *
     * "ResetUsageWS mMethod's Index."
     *
     */
    protected Integer ResetUsageWSMethodIndex = new Integer(1);


    /**
     * Constructor for the "ResetUsageWSMethodWiseEntry" group.
     */
    public ResetUsageWSMethodWiseEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "ResetUsageWSMethodAvgTPS" variable.
     */
    public Long getResetUsageWSMethodAvgTPS() throws SnmpStatusException {
        return ResetUsageWSMethodAvgTPS;
    }

    /**
     * Getter for the "ResetUsageWSMethodLastMinuteTotalRequests" variable.
     */
    public Long getResetUsageWSMethodLastMinuteTotalRequests() throws SnmpStatusException {
        return ResetUsageWSMethodLastMinuteTotalRequests;
    }

    /**
     * Getter for the "ResetUsageWSMethodTotalResponses" variable.
     */
    public Long getResetUsageWSMethodTotalResponses() throws SnmpStatusException {
        return ResetUsageWSMethodTotalResponses;
    }

    /**
     * Getter for the "ResetUsageWSMethodTotalRequests" variable.
     */
    public Long getResetUsageWSMethodTotalRequests() throws SnmpStatusException {
        return ResetUsageWSMethodTotalRequests;
    }

    /**
     * Getter for the "ResetUsageWSMethodName" variable.
     */
    public String getResetUsageWSMethodName() throws SnmpStatusException {
        return ResetUsageWSMethodName;
    }

    /**
     * Getter for the "ResetUsageWSMethodIndex" variable.
     */
    public Integer getResetUsageWSMethodIndex() throws SnmpStatusException {
        return ResetUsageWSMethodIndex;
    }

}