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
 * The class is used for implementing the "SessionWSResultCodeWiseEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.11.6.1.
 */
public class SessionWSResultCodeWiseEntry implements SessionWSResultCodeWiseEntryMBean, Serializable {

    /**
     * Variable for storing the value of "SessionWSResultCodeCounters".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.11.6.1.3".
     *
     * "SessionManagementWS ResultCode statistics."
     *
     */
    protected Long SessionWSResultCodeCounters = new Long(1);

    /**
     * Variable for storing the value of "SessionWSResultCodeName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.11.6.1.2".
     *
     * "SessionManagementWS ResultCode Name."
     *
     */
    protected String SessionWSResultCodeName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "SessionWSResultCode".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.11.6.1.1".
     *
     * "SessionManagementWS ResultCode."
     *
     */
    protected Integer SessionWSResultCode = new Integer(1);


    /**
     * Constructor for the "SessionWSResultCodeWiseEntry" group.
     */
    public SessionWSResultCodeWiseEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "SessionWSResultCodeCounters" variable.
     */
    public Long getSessionWSResultCodeCounters() throws SnmpStatusException {
        return SessionWSResultCodeCounters;
    }

    /**
     * Getter for the "SessionWSResultCodeName" variable.
     */
    public String getSessionWSResultCodeName() throws SnmpStatusException {
        return SessionWSResultCodeName;
    }

    /**
     * Getter for the "SessionWSResultCode" variable.
     */
    public Integer getSessionWSResultCode() throws SnmpStatusException {
        return SessionWSResultCode;
    }

}
