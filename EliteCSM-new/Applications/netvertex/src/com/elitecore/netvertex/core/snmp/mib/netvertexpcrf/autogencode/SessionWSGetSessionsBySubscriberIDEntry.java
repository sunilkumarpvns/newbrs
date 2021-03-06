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
 * The class is used for implementing the "SessionWSGetSessionsBySubscriberIDEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.11.7.1.
 */
public class SessionWSGetSessionsBySubscriberIDEntry implements SessionWSGetSessionsBySubscriberIDEntryMBean, Serializable {

    /**
     * Variable for storing the value of "GetSessionsBySubscriberIDResultCodeCounters".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.11.7.1.3".
     *
     * "SessionManagementWS method wsGetSessionsBySubscriberIdentity ResultCode Specific statistics."
     *
     */
    protected Long GetSessionsBySubscriberIDResultCodeCounters = new Long(1);

    /**
     * Variable for storing the value of "GetSessionsBySubscriberIDResultCodeName".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.11.7.1.2".
     *
     * "SessionManagementWS method wsGetSessionsBySubscriberIdentity ResultCode Name."
     *
     */
    protected String GetSessionsBySubscriberIDResultCodeName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "GetSessionsBySubscriberIDResultCode".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.11.7.1.1".
     *
     * "SessionManagementWS method wsGetSessionsBySubscriberID ResultCode."
     *
     */
    protected Integer GetSessionsBySubscriberIDResultCode = new Integer(1);


    /**
     * Constructor for the "SessionWSGetSessionsBySubscriberIDEntry" group.
     */
    public SessionWSGetSessionsBySubscriberIDEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "GetSessionsBySubscriberIDResultCodeCounters" variable.
     */
    public Long getGetSessionsBySubscriberIDResultCodeCounters() throws SnmpStatusException {
        return GetSessionsBySubscriberIDResultCodeCounters;
    }

    /**
     * Getter for the "GetSessionsBySubscriberIDResultCodeName" variable.
     */
    public String getGetSessionsBySubscriberIDResultCodeName() throws SnmpStatusException {
        return GetSessionsBySubscriberIDResultCodeName;
    }

    /**
     * Getter for the "GetSessionsBySubscriberIDResultCode" variable.
     */
    public Integer getGetSessionsBySubscriberIDResultCode() throws SnmpStatusException {
        return GetSessionsBySubscriberIDResultCode;
    }

}
