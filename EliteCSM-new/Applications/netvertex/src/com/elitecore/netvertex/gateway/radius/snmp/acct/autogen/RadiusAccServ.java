package com.elitecore.netvertex.gateway.radius.snmp.acct.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RADIUS-ACC-SERVER-MIB.
//

// java imports
//
import java.io.Serializable;

// jmx imports
//
import javax.management.MBeanServer;
import com.sun.management.snmp.SnmpString;
import com.sun.management.snmp.SnmpStatusException;

// jdmk imports
//
import com.sun.management.snmp.agent.SnmpMib;

/**
 * The class is used for implementing the "RadiusAccServ" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.67.2.1.1.1.
 */
public class RadiusAccServ implements RadiusAccServMBean, Serializable {

    /**
     * Variable for storing the value of "RadiusAccServTotalMalformedRequests".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.9".
     *
     * "The number of malformed RADIUS Accounting-Request
     * packets received. Bad authenticators or unknown
     * types are not included as malformed Access-Requests."
     *
     */
    protected Long RadiusAccServTotalMalformedRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusAccServTotalResponses".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.8".
     *
     * "The number of RADIUS Accounting-Response packets sent."
     *
     */
    protected Long RadiusAccServTotalResponses = new Long(1);

    /**
     * Variable for storing the value of "RadiusAccServTotalDupRequests".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.7".
     *
     * "The number of duplicate RADIUS Accounting-Request
     * 
     * packets received."
     *
     */
    protected Long RadiusAccServTotalDupRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusAccServTotalInvalidRequests".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.6".
     *
     * "The number of RADIUS Accounting-Request packets
     * received from unknown addresses."
     *
     */
    protected Long RadiusAccServTotalInvalidRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusAccServTotalRequests".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.5".
     *
     * "The number of packets received on the
     * accounting port."
     *
     */
    protected Long RadiusAccServTotalRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusAccClientTable".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.14".
     *
     * "The (conceptual) table listing the RADIUS accounting
     * clients with which the server shares a secret."
     *
     */
    protected TableRadiusAccClientTable RadiusAccClientTable;

    /**
     * Variable for storing the value of "RadiusAccServConfigReset".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.4".
     *
     * "Status/action object to reinitialize any persistent
     * server state.  When set to reset(2), any persistent
     * server state (such as a process) is reinitialized as if
     * the server had just been started.  This value will
     * never be returned by a read operation.  When read, one
     * of the following values will be returned:
     * other(1) - server in some unknown state;
     * initializing(3) - server (re)initializing;
     * running(4) - server currently running."
     *
     */
    protected EnumRadiusAccServConfigReset RadiusAccServConfigReset = new EnumRadiusAccServConfigReset();

    /**
     * Variable for storing the value of "RadiusAccServTotalUnknownTypes".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.13".
     *
     * "The number of RADIUS packets of unknowntype which
     * were received."
     *
     */
    protected Long RadiusAccServTotalUnknownTypes = new Long(1);

    /**
     * Variable for storing the value of "RadiusAccServTotalNoRecords".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.12".
     *
     * "The number of RADIUS Accounting-Request packets
     * which were received and responded to but not
     * recorded."
     *
     */
    protected Long RadiusAccServTotalNoRecords = new Long(1);

    /**
     * Variable for storing the value of "RadiusAccServResetTime".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.3".
     *
     * "If the server has a persistent state (e.g., a process)
     * and supports a `reset' operation (e.g., can be told to
     * re-read configuration files), this value will be the
     * time elapsed (in hundredths of a second) since the
     * server was `reset.'  For software that does not
     * have persistence or does not support a `reset' operation,
     * 
     * this value will be zero."
     *
     */
    protected Long RadiusAccServResetTime = new Long(1);

    /**
     * Variable for storing the value of "RadiusAccServTotalPacketsDropped".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.11".
     *
     * "The number of incoming packets silently discarded
     * for a reason other than malformed, bad authenticators,
     * or unknown types."
     *
     */
    protected Long RadiusAccServTotalPacketsDropped = new Long(1);

    /**
     * Variable for storing the value of "RadiusAccServUpTime".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.2".
     *
     * "If the server has a persistent state (e.g., a process),
     * this value will be the time elapsed (in hundredths of a
     * second) since the server process was started.
     * For software without persistent state, this value will
     * be zero."
     *
     */
    protected Long RadiusAccServUpTime = new Long(1);

    /**
     * Variable for storing the value of "RadiusAccServTotalBadAuthenticators".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.10".
     *
     * "The number of RADIUS Accounting-Request packets
     * which contained invalid Signature attributes."
     *
     */
    protected Long RadiusAccServTotalBadAuthenticators = new Long(1);

    /**
     * Variable for storing the value of "RadiusAccServIdent".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.1".
     *
     * "The implementation identification string for the
     * RADIUS accounting server software in use on the
     * system, for example; `FNS-2.1'"
     *
     */
    protected String RadiusAccServIdent = new String("JDMK 5.1");


    /**
     * Constructor for the "RadiusAccServ" group.
     * If the group contains a table, the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public RadiusAccServ(SnmpMib myMib) {
        RadiusAccClientTable = new TableRadiusAccClientTable (myMib);
    }


    /**
     * Constructor for the "RadiusAccServ" group.
     * If the group contains a table, the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public RadiusAccServ(SnmpMib myMib, MBeanServer server) {
        RadiusAccClientTable = new TableRadiusAccClientTable (myMib, server);
    }

    public RadiusAccServ() {
	}


	/**
     * Getter for the "RadiusAccServTotalMalformedRequests" variable.
     */
    public Long getRadiusAccServTotalMalformedRequests() throws SnmpStatusException {
        return RadiusAccServTotalMalformedRequests;
    }

    /**
     * Getter for the "RadiusAccServTotalResponses" variable.
     */
    public Long getRadiusAccServTotalResponses() throws SnmpStatusException {
        return RadiusAccServTotalResponses;
    }

    /**
     * Getter for the "RadiusAccServTotalDupRequests" variable.
     */
    public Long getRadiusAccServTotalDupRequests() throws SnmpStatusException {
        return RadiusAccServTotalDupRequests;
    }

    /**
     * Getter for the "RadiusAccServTotalInvalidRequests" variable.
     */
    public Long getRadiusAccServTotalInvalidRequests() throws SnmpStatusException {
        return RadiusAccServTotalInvalidRequests;
    }

    /**
     * Getter for the "RadiusAccServTotalRequests" variable.
     */
    public Long getRadiusAccServTotalRequests() throws SnmpStatusException {
        return RadiusAccServTotalRequests;
    }

    /**
     * Access the "RadiusAccClientTable" variable.
     */
    public TableRadiusAccClientTable accessRadiusAccClientTable() throws SnmpStatusException {
        return RadiusAccClientTable;
    }

    /**
     * Access the "RadiusAccClientTable" variable as a bean indexed property.
     */
    public RadiusAccClientEntryMBean[] getRadiusAccClientTable() throws SnmpStatusException {
        return RadiusAccClientTable.getEntries();
    }

    /**
     * Getter for the "RadiusAccServConfigReset" variable.
     */
    public EnumRadiusAccServConfigReset getRadiusAccServConfigReset() throws SnmpStatusException {
        return RadiusAccServConfigReset;
    }

    /**
     * Setter for the "RadiusAccServConfigReset" variable.
     */
    public void setRadiusAccServConfigReset(EnumRadiusAccServConfigReset x) throws SnmpStatusException {
        RadiusAccServConfigReset = x;
    }

    /**
     * Checker for the "RadiusAccServConfigReset" variable.
     */
    public void checkRadiusAccServConfigReset(EnumRadiusAccServConfigReset x) throws SnmpStatusException {
        //
        // Add your own checking policy.
        //
    }

    /**
     * Getter for the "RadiusAccServTotalUnknownTypes" variable.
     */
    public Long getRadiusAccServTotalUnknownTypes() throws SnmpStatusException {
        return RadiusAccServTotalUnknownTypes;
    }

    /**
     * Getter for the "RadiusAccServTotalNoRecords" variable.
     */
    public Long getRadiusAccServTotalNoRecords() throws SnmpStatusException {
        return RadiusAccServTotalNoRecords;
    }

    /**
     * Getter for the "RadiusAccServResetTime" variable.
     */
    public Long getRadiusAccServResetTime() throws SnmpStatusException {
        return RadiusAccServResetTime;
    }

    /**
     * Getter for the "RadiusAccServTotalPacketsDropped" variable.
     */
    public Long getRadiusAccServTotalPacketsDropped() throws SnmpStatusException {
        return RadiusAccServTotalPacketsDropped;
    }

    /**
     * Getter for the "RadiusAccServUpTime" variable.
     */
    public Long getRadiusAccServUpTime() throws SnmpStatusException {
        return RadiusAccServUpTime;
    }

    /**
     * Getter for the "RadiusAccServTotalBadAuthenticators" variable.
     */
    public Long getRadiusAccServTotalBadAuthenticators() throws SnmpStatusException {
        return RadiusAccServTotalBadAuthenticators;
    }

    /**
     * Getter for the "RadiusAccServIdent" variable.
     */
    public String getRadiusAccServIdent() throws SnmpStatusException {
        return RadiusAccServIdent;
    }

}
