package com.elitecore.netvertex.gateway.radius.snmp.acct.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RADIUS-ACC-SERVER-MIB.
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
 * The class is used for implementing the "RadiusAccClientEntry" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.67.2.1.1.1.14.1.
 */
public class RadiusAccClientEntry implements RadiusAccClientEntryMBean, Serializable {

    /**
     * Variable for storing the value of "RadiusAccServMalformedRequests".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.14.1.9".
     *
     * "The number of malformed RADIUS Accounting-Request
     * packets which were received from this client.
     * Bad authenticators and unknown types
     * are not included as malformed Accounting-Requests."
     *
     */
    protected Long RadiusAccServMalformedRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusAccServBadAuthenticators".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.14.1.8".
     *
     * "The number of RADIUS Accounting-Request packets
     * which contained invalid  authenticators received
     * from this client."
     *
     */
    protected Long RadiusAccServBadAuthenticators = new Long(1);

    /**
     * Variable for storing the value of "RadiusAccServResponses".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.14.1.7".
     *
     * "The number of RADIUS Accounting-Response packets
     * sent to this client."
     *
     */
    protected Long RadiusAccServResponses = new Long(1);

    /**
     * Variable for storing the value of "RadiusAccServDupRequests".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.14.1.6".
     *
     * "The number of duplicate RADIUS Accounting-Request
     * packets received from this client."
     *
     */
    protected Long RadiusAccServDupRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusAccServRequests".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.14.1.5".
     *
     * "The number of packets received from this
     * client on the accounting port."
     *
     */
    protected Long RadiusAccServRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusAccServPacketsDropped".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.14.1.4".
     *
     * "The number of incoming packets received
     * from this client and silently discarded
     * for a reason other than malformed, bad
     * authenticators, or unknown types."
     *
     */
    protected Long RadiusAccServPacketsDropped = new Long(1);

    /**
     * Variable for storing the value of "RadiusAccClientID".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.14.1.3".
     *
     * "The NAS-Identifier of the RADIUS accounting client
     * referred to in this table entry. This is not necessarily
     * the same as sysName in MIB II."
     *
     */
    protected String RadiusAccClientID = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "RadiusAccClientAddress".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.14.1.2".
     *
     * "The NAS-IP-Address of the RADIUS accounting client
     * referred to in this table entry."
     *
     */
    protected String RadiusAccClientAddress = new String("192.9.9.100");

    /**
     * Variable for storing the value of "RadiusAccServUnknownTypes".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.14.1.11".
     *
     * "The number of RADIUS packets of unknown type which
     * were received from this client."
     *
     */
    protected Long RadiusAccServUnknownTypes = new Long(1);

    /**
     * Variable for storing the value of "RadiusAccServNoRecords".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.14.1.10".
     *
     * "The number of RADIUS Accounting-Request packets
     * which were received and responded to but not
     * recorded."
     *
     */
    protected Long RadiusAccServNoRecords = new Long(1);

    /**
     * Variable for storing the value of "RadiusAccClientIndex".
     * The variable is identified by: "1.3.6.1.2.1.67.2.1.1.1.14.1.1".
     *
     * "A number uniquely identifying each RADIUS accounting
     * client with which this server communicates."
     *
     */
    protected Integer RadiusAccClientIndex = new Integer(1);


    /**
     * Constructor for the "RadiusAccClientEntry" group.
     */
    public RadiusAccClientEntry(SnmpMib myMib) {
    }

    public RadiusAccClientEntry() {
	}

	/**
     * Getter for the "RadiusAccServMalformedRequests" variable.
     */
    public Long getRadiusAccServMalformedRequests() throws SnmpStatusException {
        return RadiusAccServMalformedRequests;
    }

    /**
     * Getter for the "RadiusAccServBadAuthenticators" variable.
     */
    public Long getRadiusAccServBadAuthenticators() throws SnmpStatusException {
        return RadiusAccServBadAuthenticators;
    }

    /**
     * Getter for the "RadiusAccServResponses" variable.
     */
    public Long getRadiusAccServResponses() throws SnmpStatusException {
        return RadiusAccServResponses;
    }

    /**
     * Getter for the "RadiusAccServDupRequests" variable.
     */
    public Long getRadiusAccServDupRequests() throws SnmpStatusException {
        return RadiusAccServDupRequests;
    }

    /**
     * Getter for the "RadiusAccServRequests" variable.
     */
    public Long getRadiusAccServRequests() throws SnmpStatusException {
        return RadiusAccServRequests;
    }

    /**
     * Getter for the "RadiusAccServPacketsDropped" variable.
     */
    public Long getRadiusAccServPacketsDropped() throws SnmpStatusException {
        return RadiusAccServPacketsDropped;
    }

    /**
     * Getter for the "RadiusAccClientID" variable.
     */
    public String getRadiusAccClientID() throws SnmpStatusException {
        return RadiusAccClientID;
    }

    /**
     * Getter for the "RadiusAccClientAddress" variable.
     */
    public String getRadiusAccClientAddress() throws SnmpStatusException {
        return RadiusAccClientAddress;
    }

    /**
     * Getter for the "RadiusAccServUnknownTypes" variable.
     */
    public Long getRadiusAccServUnknownTypes() throws SnmpStatusException {
        return RadiusAccServUnknownTypes;
    }

    /**
     * Getter for the "RadiusAccServNoRecords" variable.
     */
    public Long getRadiusAccServNoRecords() throws SnmpStatusException {
        return RadiusAccServNoRecords;
    }

    /**
     * Getter for the "RadiusAccClientIndex" variable.
     */
    public Integer getRadiusAccClientIndex() throws SnmpStatusException {
        return RadiusAccClientIndex;
    }

}