package com.elitecore.aaa.mibs.radius.authentication.server.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RADIUS-AUTH-SERVER-MIB.
//

// java imports
//
import java.io.Serializable;

import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;
// jmx imports
//
// jdmk imports
//

/**
 * The class is used for implementing the "RadiusAuthClientEntry" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.67.1.1.1.1.15.1.
 */
public class RadiusAuthClientEntry implements RadiusAuthClientEntryMBean, Serializable {

    /**
     * Variable for storing the value of "RadiusAuthServMalformedAccessRequests".
     * The variable is identified by: "1.3.6.1.2.1.67.1.1.1.1.15.1.9".
     *
     * "The number of malformed RADIUS Access-Request
     * packets received from this client.
     * Bad authenticators and unknown types are not included as
     * malformed Access-Requests."
     *
     */
    protected Long RadiusAuthServMalformedAccessRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthServAccessChallenges".
     * The variable is identified by: "1.3.6.1.2.1.67.1.1.1.1.15.1.8".
     *
     * "The number of RADIUS Access-Challenge packets
     * sent to this client."
     *
     */
    protected Long RadiusAuthServAccessChallenges = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthServAccessRejects".
     * The variable is identified by: "1.3.6.1.2.1.67.1.1.1.1.15.1.7".
     *
     * "The number of RADIUS Access-Reject packets
     * sent to this client."
     *
     */
    protected Long RadiusAuthServAccessRejects = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthServAccessAccepts".
     * The variable is identified by: "1.3.6.1.2.1.67.1.1.1.1.15.1.6".
     *
     * "The number of RADIUS Access-Accept packets
     * sent to this client."
     *
     */
    protected Long RadiusAuthServAccessAccepts = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthServDupAccessRequests".
     * The variable is identified by: "1.3.6.1.2.1.67.1.1.1.1.15.1.5".
     *
     * "The number of duplicate RADIUS Access-Request
     * packets received from this client."
     *
     */
    protected Long RadiusAuthServDupAccessRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthServAccessRequests".
     * The variable is identified by: "1.3.6.1.2.1.67.1.1.1.1.15.1.4".
     *
     * "The number of packets received on the authentication
     * port from this client."
     *
     */
    protected Long RadiusAuthServAccessRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthServUnknownTypes".
     * The variable is identified by: "1.3.6.1.2.1.67.1.1.1.1.15.1.12".
     *
     * "The number of RADIUS packets of unknown type which
     * were received from this client."
     *
     */
    protected Long RadiusAuthServUnknownTypes = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthClientID".
     * The variable is identified by: "1.3.6.1.2.1.67.1.1.1.1.15.1.3".
     *
     * "The NAS-Identifier of the RADIUS authentication client
     * referred to in this table entry. This is not necessarily
     * the same as sysName in MIB II."
     *
     */
    protected String RadiusAuthClientID = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "RadiusAuthClientAddress".
     * The variable is identified by: "1.3.6.1.2.1.67.1.1.1.1.15.1.2".
     *
     * "The NAS-IP-Address of the RADIUS authentication client
     * referred to in this table entry."
     *
     */
    protected String RadiusAuthClientAddress = new String("192.9.9.100");

    /**
     * Variable for storing the value of "RadiusAuthServPacketsDropped".
     * The variable is identified by: "1.3.6.1.2.1.67.1.1.1.1.15.1.11".
     *
     * "The number of incoming packets from this
     * client silently discarded for some reason other
     * than malformed, bad authenticators or
     * unknown types."
     *
     */
    protected Long RadiusAuthServPacketsDropped = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthServBadAuthenticators".
     * The variable is identified by: "1.3.6.1.2.1.67.1.1.1.1.15.1.10".
     *
     * "The number of RADIUS Authentication-Request packets
     * which contained invalid Signature attributes received
     * from this client."
     *
     */
    protected Long RadiusAuthServBadAuthenticators = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthClientIndex".
     * The variable is identified by: "1.3.6.1.2.1.67.1.1.1.1.15.1.1".
     *
     * "A number uniquely identifying each RADIUS
     * authentication client with which this server
     * communicates."
     *
     */
    protected Integer RadiusAuthClientIndex = new Integer(1);


    /**
     * Constructor for the "RadiusAuthClientEntry" group.
     */
    public RadiusAuthClientEntry(SnmpMib myMib) {
    }

    public RadiusAuthClientEntry() {
	}
    
    /**
     * Getter for the "RadiusAuthServMalformedAccessRequests" variable.
     */
    public Long getRadiusAuthServMalformedAccessRequests() throws SnmpStatusException {
        return RadiusAuthServMalformedAccessRequests;
    }

    /**
     * Getter for the "RadiusAuthServAccessChallenges" variable.
     */
    public Long getRadiusAuthServAccessChallenges() throws SnmpStatusException {
        return RadiusAuthServAccessChallenges;
    }

    /**
     * Getter for the "RadiusAuthServAccessRejects" variable.
     */
    public Long getRadiusAuthServAccessRejects() throws SnmpStatusException {
        return RadiusAuthServAccessRejects;
    }

    /**
     * Getter for the "RadiusAuthServAccessAccepts" variable.
     */
    public Long getRadiusAuthServAccessAccepts() throws SnmpStatusException {
        return RadiusAuthServAccessAccepts;
    }

    /**
     * Getter for the "RadiusAuthServDupAccessRequests" variable.
     */
    public Long getRadiusAuthServDupAccessRequests() throws SnmpStatusException {
        return RadiusAuthServDupAccessRequests;
    }

    /**
     * Getter for the "RadiusAuthServAccessRequests" variable.
     */
    public Long getRadiusAuthServAccessRequests() throws SnmpStatusException {
        return RadiusAuthServAccessRequests;
    }

    /**
     * Getter for the "RadiusAuthServUnknownTypes" variable.
     */
    public Long getRadiusAuthServUnknownTypes() throws SnmpStatusException {
        return RadiusAuthServUnknownTypes;
    }

    /**
     * Getter for the "RadiusAuthClientID" variable.
     */
    public String getRadiusAuthClientID() throws SnmpStatusException {
        return RadiusAuthClientID;
    }

    /**
     * Getter for the "RadiusAuthClientAddress" variable.
     */
    public String getRadiusAuthClientAddress() throws SnmpStatusException {
        return RadiusAuthClientAddress;
    }

    /**
     * Getter for the "RadiusAuthServPacketsDropped" variable.
     */
    public Long getRadiusAuthServPacketsDropped() throws SnmpStatusException {
        return RadiusAuthServPacketsDropped;
    }

    /**
     * Getter for the "RadiusAuthServBadAuthenticators" variable.
     */
    public Long getRadiusAuthServBadAuthenticators() throws SnmpStatusException {
        return RadiusAuthServBadAuthenticators;
    }

    /**
     * Getter for the "RadiusAuthClientIndex" variable.
     */
    public Integer getRadiusAuthClientIndex() throws SnmpStatusException {
        return RadiusAuthClientIndex;
    }

}
