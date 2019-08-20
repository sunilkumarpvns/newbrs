package com.elitecore.aaa.mibs.radius.authentication.client.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RADIUS-AUTH-CLIENT-MIB.
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
 * The class is used for implementing the "RadiusAuthServerEntry" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.67.1.2.1.1.3.1.
 */
public class RadiusAuthServerEntry implements RadiusAuthServerEntryMBean, Serializable {

    /**
     * Variable for storing the value of "RadiusAuthClientAccessChallenges".
     * The variable is identified by: "1.3.6.1.2.1.67.1.2.1.1.3.1.9".
     *
     * "The number of RADIUS Access-Challenge packets
     * (valid or invalid) received from this server."
     *
     */
    protected Long RadiusAuthClientAccessChallenges = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthClientAccessRejects".
     * The variable is identified by: "1.3.6.1.2.1.67.1.2.1.1.3.1.8".
     *
     * "The number of RADIUS Access-Reject packets
     * (valid or invalid) received from this server."
     *
     */
    protected Long RadiusAuthClientAccessRejects = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthClientAccessAccepts".
     * The variable is identified by: "1.3.6.1.2.1.67.1.2.1.1.3.1.7".
     *
     * "The number of RADIUS Access-Accept packets
     * (valid or invalid) received from this server."
     *
     */
    protected Long RadiusAuthClientAccessAccepts = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthClientPacketsDropped".
     * The variable is identified by: "1.3.6.1.2.1.67.1.2.1.1.3.1.15".
     *
     * "The number of RADIUS packets of which were
     * received from this server on the authentication port
     * and dropped for some other reason."
     *
     */
    protected Long RadiusAuthClientPacketsDropped = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthClientAccessRetransmissions".
     * The variable is identified by: "1.3.6.1.2.1.67.1.2.1.1.3.1.6".
     *
     * "The number of RADIUS Access-Request packets
     * retransmitted to this RADIUS authentication server."
     *
     */
    protected Long RadiusAuthClientAccessRetransmissions = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthClientUnknownTypes".
     * The variable is identified by: "1.3.6.1.2.1.67.1.2.1.1.3.1.14".
     *
     * "The number of RADIUS packets of unknown type which
     * were received from this server on the authentication port."
     *
     */
    protected Long RadiusAuthClientUnknownTypes = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthClientAccessRequests".
     * The variable is identified by: "1.3.6.1.2.1.67.1.2.1.1.3.1.5".
     *
     * "The number of RADIUS Access-Request packets sent
     * to this server. This does not include retransmissions."
     *
     */
    protected Long RadiusAuthClientAccessRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthClientTimeouts".
     * The variable is identified by: "1.3.6.1.2.1.67.1.2.1.1.3.1.13".
     *
     * "The number of authentication timeouts to this server.
     * After a timeout the client may retry to the same
     * server, send to a different server, or
     * give up. A retry to the same server is counted as a
     * retransmit as well as a timeout. A send to a different
     * server is counted as a Request as well as a timeout."
     *
     */
    protected Long RadiusAuthClientTimeouts = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthClientRoundTripTime".
     * The variable is identified by: "1.3.6.1.2.1.67.1.2.1.1.3.1.4".
     *
     * "The time interval (in hundredths of a second) between
     * the most recent Access-Reply/Access-Challenge and the
     * Access-Request that matched it from this RADIUS
     * authentication server."
     *
     */
    protected Long RadiusAuthClientRoundTripTime = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthClientPendingRequests".
     * The variable is identified by: "1.3.6.1.2.1.67.1.2.1.1.3.1.12".
     *
     * "The number of RADIUS Access-Request packets
     * destined for this server that have not yet timed out
     * or received a response. This variable is incremented
     * when an Access-Request is sent and decremented due to
     * receipt of an Acess-Accept, Access-Reject or
     * Access-Challenge, a timeout or retransmission."
     *
     */
    protected Long RadiusAuthClientPendingRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthClientServerPortNumber".
     * The variable is identified by: "1.3.6.1.2.1.67.1.2.1.1.3.1.3".
     *
     * "The UDP port the client is using to send requests to
     * this server."
     *
     */
    protected Integer RadiusAuthClientServerPortNumber = new Integer(1);

    /**
     * Variable for storing the value of "RadiusAuthServerAddress".
     * The variable is identified by: "1.3.6.1.2.1.67.1.2.1.1.3.1.2".
     *
     * "The IP address of the RADIUS authentication server
     * referred to in this table entry."
     *
     */
    protected String RadiusAuthServerAddress = new String("192.9.9.100");

    /**
     * Variable for storing the value of "RadiusAuthClientBadAuthenticators".
     * The variable is identified by: "1.3.6.1.2.1.67.1.2.1.1.3.1.11".
     *
     * "The number of RADIUS Access-Response packets
     * containing invalid authenticators or Signature
     * attributes received from this server."
     *
     */
    protected Long RadiusAuthClientBadAuthenticators = new Long(1);

    /**
     * Variable for storing the value of "RadiusAuthServerIndex".
     * The variable is identified by: "1.3.6.1.2.1.67.1.2.1.1.3.1.1".
     *
     * "A number uniquely identifying each RADIUS
     * Authentication server with which this client
     * communicates."
     *
     */
    protected Integer RadiusAuthServerIndex = new Integer(1);

    /**
     * Variable for storing the value of "RadiusAuthClientMalformedAccessResponses".
     * The variable is identified by: "1.3.6.1.2.1.67.1.2.1.1.3.1.10".
     *
     * "The number of malformed RADIUS Access-Response
     * packets received from this server.
     * Malformed packets include packets with
     * an invalid length. Bad authenticators or
     * Signature attributes or unknown types are not
     * 
     * included as malformed access responses."
     *
     */
    protected Long RadiusAuthClientMalformedAccessResponses = new Long(1);


    /**
     * Constructor for the "RadiusAuthServerEntry" group.
     */
    public RadiusAuthServerEntry(SnmpMib myMib) {
    }

    public RadiusAuthServerEntry() {
	}
	
    /**
     * Getter for the "RadiusAuthClientAccessChallenges" variable.
     */
    public Long getRadiusAuthClientAccessChallenges() throws SnmpStatusException {
        return RadiusAuthClientAccessChallenges;
    }

    /**
     * Getter for the "RadiusAuthClientAccessRejects" variable.
     */
    public Long getRadiusAuthClientAccessRejects() throws SnmpStatusException {
        return RadiusAuthClientAccessRejects;
    }

    /**
     * Getter for the "RadiusAuthClientAccessAccepts" variable.
     */
    public Long getRadiusAuthClientAccessAccepts() throws SnmpStatusException {
        return RadiusAuthClientAccessAccepts;
    }

    /**
     * Getter for the "RadiusAuthClientPacketsDropped" variable.
     */
    public Long getRadiusAuthClientPacketsDropped() throws SnmpStatusException {
        return RadiusAuthClientPacketsDropped;
    }

    /**
     * Getter for the "RadiusAuthClientAccessRetransmissions" variable.
     */
    public Long getRadiusAuthClientAccessRetransmissions() throws SnmpStatusException {
        return RadiusAuthClientAccessRetransmissions;
    }

    /**
     * Getter for the "RadiusAuthClientUnknownTypes" variable.
     */
    public Long getRadiusAuthClientUnknownTypes() throws SnmpStatusException {
        return RadiusAuthClientUnknownTypes;
    }

    /**
     * Getter for the "RadiusAuthClientAccessRequests" variable.
     */
    public Long getRadiusAuthClientAccessRequests() throws SnmpStatusException {
        return RadiusAuthClientAccessRequests;
    }

    /**
     * Getter for the "RadiusAuthClientTimeouts" variable.
     */
    public Long getRadiusAuthClientTimeouts() throws SnmpStatusException {
        return RadiusAuthClientTimeouts;
    }

    /**
     * Getter for the "RadiusAuthClientRoundTripTime" variable.
     */
    public Long getRadiusAuthClientRoundTripTime() throws SnmpStatusException {
        return RadiusAuthClientRoundTripTime;
    }

    /**
     * Getter for the "RadiusAuthClientPendingRequests" variable.
     */
    public Long getRadiusAuthClientPendingRequests() throws SnmpStatusException {
        return RadiusAuthClientPendingRequests;
    }

    /**
     * Getter for the "RadiusAuthClientServerPortNumber" variable.
     */
    public Integer getRadiusAuthClientServerPortNumber() throws SnmpStatusException {
        return RadiusAuthClientServerPortNumber;
    }

    /**
     * Getter for the "RadiusAuthServerAddress" variable.
     */
    public String getRadiusAuthServerAddress() throws SnmpStatusException {
        return RadiusAuthServerAddress;
    }

    /**
     * Getter for the "RadiusAuthClientBadAuthenticators" variable.
     */
    public Long getRadiusAuthClientBadAuthenticators() throws SnmpStatusException {
        return RadiusAuthClientBadAuthenticators;
    }

    /**
     * Getter for the "RadiusAuthServerIndex" variable.
     */
    public Integer getRadiusAuthServerIndex() throws SnmpStatusException {
        return RadiusAuthServerIndex;
    }

    /**
     * Getter for the "RadiusAuthClientMalformedAccessResponses" variable.
     */
    public Long getRadiusAuthClientMalformedAccessResponses() throws SnmpStatusException {
        return RadiusAuthClientMalformedAccessResponses;
    }

}
