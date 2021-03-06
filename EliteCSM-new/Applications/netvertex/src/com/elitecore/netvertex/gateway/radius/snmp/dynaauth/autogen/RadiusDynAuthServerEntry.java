package com.elitecore.netvertex.gateway.radius.snmp.dynaauth.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RADIUS-DYNAUTH-CLIENT-MIB.
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
 * The class is used for implementing the "RadiusDynAuthServerEntry" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.145.1.2.1.
 */
public class RadiusDynAuthServerEntry implements RadiusDynAuthServerEntryMBean, Serializable {

    /**
     * Variable for storing the value of "RadiusDynAuthClientCounterDiscontinuity".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.32".
     *
     * "The time (in hundredths of a second) since the
     * last counter discontinuity.  A discontinuity may
     * be the result of a reinitialization of the DAC
     * module within the managed entity."
     *
     */
    protected Long RadiusDynAuthClientCounterDiscontinuity = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientUnknownTypes".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.31".
     *
     * "The number of incoming packets of unknown types
     * that were received on the Dynamic Authorization port.
     * This counter may experience a discontinuity when the
     * DAC module (re)starts, as indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientUnknownTypes = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientCoAPacketsDropped".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.30".
     *
     * "The number of incoming CoA-Ack and CoA-NAK from this
     * Dynamic Authorization Server silently discarded by the
     * client application for some reason other than
     * malformed, bad authenticators, or unknown types.  This
     * counter may experience a discontinuity when the DAC
     * module (re)starts, as indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientCoAPacketsDropped = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientCoARequests".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.19".
     *
     * "The number of RADIUS CoA-Requests sent to this
     * Dynamic Authorization Server.  This also includes
     * CoA requests that have a Service-Type attribute
     * with value 'Authorize Only'.  This counter may
     * experience a discontinuity when the DAC module
     * (re)starts, as indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientCoARequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientDisconPacketsDropped".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.18".
     *
     * "The number of incoming Disconnect-Ack and
     * Disconnect-NAK packets from this Dynamic Authorization
     * Server silently discarded by the client application for
     * some reason other than malformed, bad authenticators,
     * or unknown types.  This counter may experience a
     * discontinuity when the DAC module (re)starts, as
     * indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientDisconPacketsDropped = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientDisconTimeouts".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.17".
     *
     * "The number of Disconnect request timeouts to this
     * server.  After a timeout, the client may retry to the
     * same server or give up.  A retry to the same server is
     * counted as a retransmit and as a timeout.  A send
     * to a different server is counted as a
     * Disconnect-Request and as a timeout.  This counter
     * may experience a discontinuity when the DAC module
     * (re)starts, as indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientDisconTimeouts = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientDisconPendingRequests".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.16".
     *
     * "The number of RADIUS Disconnect-request packets
     * destined for this server that have not yet timed out
     * or received a response.  This variable is incremented
     * when an Disconnect-Request is sent and decremented
     * due to receipt of a Disconnect-Ack, a Disconnect-NAK,
     * a timeout, or a retransmission."
     *
     */
    protected Long RadiusDynAuthClientDisconPendingRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientDisconBadAuthenticators".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.15".
     *
     * "The number of RADIUS Disconnect-Ack and Disconnect-NAK
     * packets that contained invalid Authenticator field
     * received from this Dynamic Authorization Server.  This
     * counter may experience a discontinuity when the DAC
     * module (re)starts, as indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientDisconBadAuthenticators = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientMalformedDisconResponses".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.14".
     *
     * "The number of malformed RADIUS Disconnect-Ack and
     * Disconnect-NAK packets received from this Dynamic
     * Authorization Server.  Bad authenticators and unknown
     * types are not included as malformed Disconnect-Ack and
     * Disconnect-NAK packets.  This counter may experience a
     * discontinuity when the DAC module (re)starts, as
     * indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientMalformedDisconResponses = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientDisconNakSessNoContext".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.13".
     *
     * "The number of RADIUS Disconnect-NAK packets
     * received from this Dynamic Authorization Server
     * because no session context was found; i.e., it
     * includes an Error-Cause attribute with value 503
     * ('Session Context Not Found').  This counter may
     * experience a discontinuity when the DAC module
     * (re)starts, as indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientDisconNakSessNoContext = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientDisconNakAuthOnlyRequest".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.12".
     *
     * "The number of RADIUS Disconnect-NAK packets
     * that include a Service-Type attribute with value
     * 'Authorize Only' received from this Dynamic
     * Authorization Server.  This counter may experience a
     * discontinuity when the DAC module (re)starts, as
     * 
     * 
     * 
     * indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientDisconNakAuthOnlyRequest = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientDisconNaks".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.11".
     *
     * "The number of RADIUS Disconnect-NAK packets
     * received from this Dynamic Authorization Server.
     * This includes the RADIUS Disconnect-NAK packets
     * received with a Service-Type attribute with value
     * 'Authorize Only' and the RADIUS Disconnect-NAK
     * packets received if no session context was found.  This
     * counter may experience a discontinuity when the DAC
     * module (re)starts, as indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientDisconNaks = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientDisconAcks".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.10".
     *
     * "The number of RADIUS Disconnect-ACK packets
     * received from this Dynamic Authorization Server.  This
     * counter may experience a discontinuity when the DAC
     * module (re)starts, as indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientDisconAcks = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientDisconRetransmissions".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.9".
     *
     * "The number of RADIUS Disconnect-request packets
     * retransmitted to this RADIUS Dynamic Authorization
     * Server.  Disconnect-NAK packets received from unknown
     * addresses.  This counter may experience a discontinuity
     * when the DAC module (re)starts, as indicated by the
     * value of radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientDisconRetransmissions = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientDisconAuthOnlyRequests".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.8".
     *
     * "The number of RADIUS Disconnect-Requests that include a
     * Service-Type attribute with value 'Authorize Only'
     * sent to this Dynamic Authorization Server.
     * Disconnect-NAK packets received from unknown addresses.
     * This counter may experience a discontinuity when the
     * DAC module (re)starts, as indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientDisconAuthOnlyRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientCoATimeouts".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.29".
     *
     * "The number of CoA request timeouts to this server.
     * After a timeout, the client may retry to the same
     * server or give up.  A retry to the same server is
     * counted as a retransmit and as a timeout.  A send to
     * a different server is counted as a CoA-Request and
     * as a timeout.  This counter may experience a
     * discontinuity when the DAC module (re)starts, as
     * indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientCoATimeouts = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientDisconRequests".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.7".
     *
     * "The number of RADIUS Disconnect-Requests sent
     * to this Dynamic Authorization Server.  This also
     * includes the RADIUS Disconnect-Requests that have a
     * Service-Type attribute with value 'Authorize Only'.
     * Disconnect-NAK packets received from unknown addresses.
     * This counter may experience a discontinuity when the
     * DAC module (re)starts, as indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientDisconRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientCoAPendingRequests".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.28".
     *
     * "The number of RADIUS CoA-request packets destined for
     * this server that have not yet timed out or received a
     * response.  This variable is incremented when an
     * CoA-Request is sent and decremented due to receipt of
     * a CoA-Ack, a CoA-NAK, or a timeout, or a
     * retransmission."
     *
     */
    protected Long RadiusDynAuthClientCoAPendingRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientRoundTripTime".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.6".
     *
     * "The time interval (in hundredths of a second) between
     * the most recent Disconnect or CoA request and the
     * receipt of the corresponding Disconnect or CoA reply.
     * A value of zero is returned if no reply has been
     * received yet from this server."
     *
     */
    protected Long RadiusDynAuthClientRoundTripTime = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientCoABadAuthenticators".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.27".
     *
     * "The number of RADIUS CoA-Ack and CoA-NAK packets
     * that contained invalid Authenticator field
     * received from this Dynamic Authorization Server.
     * This counter may experience a discontinuity when the
     * DAC module (re)starts, as indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientCoABadAuthenticators = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServerID".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.5".
     *
     * "The NAS-Identifier of the RADIUS Dynamic Authorization
     * Server referred to in this table entry.  This is not
     * necessarily the same as sysName in MIB II."
     *
     */
    protected String RadiusDynAuthServerID = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "RadiusDynAuthClientMalformedCoAResponses".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.26".
     *
     * "The number of malformed RADIUS CoA-Ack and CoA-NAK
     * packets received from this Dynamic Authorization
     * Server.  Bad authenticators and unknown types are
     * not included as malformed CoA-Ack and CoA-NAK packets.
     * This counter may experience a discontinuity when the
     * DAC module (re)starts, as indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientMalformedCoAResponses = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServerClientPortNumber".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.4".
     *
     * "The UDP destination port that the RADIUS Dynamic
     * Authorization Client is using to send requests to this
     * server.  The value zero is invalid."
     *
     */
    protected Long RadiusDynAuthServerClientPortNumber = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientCoANakSessNoContext".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.25".
     *
     * "The number of RADIUS CoA-NAK packets received from
     * this Dynamic Authorization Server because no session
     * context was found; i.e., it includes an Error-Cause
     * attribute with value 503 ('Session Context Not Found').
     * This counter may experience a discontinuity when the
     * DAC module (re)starts as indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientCoANakSessNoContext = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServerAddress".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.3".
     *
     * "The IP address value of the RADIUS Dynamic
     * Authorization Server referred to in this table entry
     * using the version neutral IP address format.  The type
     * of this address is determined by the value of the
     * radiusDynAuthServerAddressType object."
     *
     */
    protected Byte[] RadiusDynAuthServerAddress = { new Byte("74"), new Byte("68"), new Byte("77"), new Byte("75")};

    /**
     * Variable for storing the value of "RadiusDynAuthClientCoANakAuthOnlyRequest".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.24".
     *
     * "The number of RADIUS CoA-NAK packets that include a
     * Service-Type attribute with value 'Authorize Only'
     * received from this Dynamic Authorization Server.  This
     * counter may experience a discontinuity when the DAC
     * module (re)starts, as indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientCoANakAuthOnlyRequest = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServerAddressType".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.2".
     *
     * "The type of IP address of the RADIUS Dynamic
     * Authorization Server referred to in this table entry."
     *
     */
    protected EnumRadiusDynAuthServerAddressType RadiusDynAuthServerAddressType = new EnumRadiusDynAuthServerAddressType();

    /**
     * Variable for storing the value of "RadiusDynAuthClientCoANaks".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.23".
     *
     * "The number of RADIUS CoA-NAK packets received from
     * this Dynamic Authorization Server.  This includes the
     * RADIUS CoA-NAK packets received with a Service-Type
     * attribute with value 'Authorize Only' and the RADIUS
     * CoA-NAK packets received because no session context
     * 
     * 
     * 
     * was found.  This counter may experience a discontinuity
     * when the DAC module (re)starts, as indicated by the
     * value of radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientCoANaks = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServerIndex".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.1".
     *
     * "A number uniquely identifying each RADIUS Dynamic
     * Authorization Server with which this Dynamic
     * Authorization Client communicates.  This number is
     * allocated by the agent implementing this MIB module
     * and is unique in this context."
     *
     */
    protected Integer RadiusDynAuthServerIndex = new Integer(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientCoAAcks".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.22".
     *
     * "The number of RADIUS CoA-ACK packets received from
     * this Dynamic Authorization Server.  This counter may
     * experience a discontinuity when the DAC module
     * (re)starts, as indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientCoAAcks = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientCoARetransmissions".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.21".
     *
     * "The number of RADIUS CoA-request packets
     * retransmitted to this RADIUS Dynamic Authorization
     * Server.  This counter may experience a discontinuity
     * when the DAC module (re)starts, as indicated by the
     * value of radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientCoARetransmissions = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientCoAAuthOnlyRequest".
     * The variable is identified by: "1.3.6.1.2.1.145.1.2.1.20".
     *
     * "The number of RADIUS CoA-requests that include a
     * Service-Type attribute with value 'Authorize Only'
     * sent to this Dynamic Authorization Client.  This
     * counter may experience a discontinuity when the DAC
     * module (re)starts, as indicated by the value of
     * radiusDynAuthClientCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthClientCoAAuthOnlyRequest = new Long(1);


    /**
     * Constructor for the "RadiusDynAuthServerEntry" group.
     */
    public RadiusDynAuthServerEntry(SnmpMib myMib) {
    }

    public RadiusDynAuthServerEntry() {
	}

	/**
     * Getter for the "RadiusDynAuthClientCounterDiscontinuity" variable.
     */
    public Long getRadiusDynAuthClientCounterDiscontinuity() throws SnmpStatusException {
        return RadiusDynAuthClientCounterDiscontinuity;
    }

    /**
     * Getter for the "RadiusDynAuthClientUnknownTypes" variable.
     */
    public Long getRadiusDynAuthClientUnknownTypes() throws SnmpStatusException {
        return RadiusDynAuthClientUnknownTypes;
    }

    /**
     * Getter for the "RadiusDynAuthClientCoAPacketsDropped" variable.
     */
    public Long getRadiusDynAuthClientCoAPacketsDropped() throws SnmpStatusException {
        return RadiusDynAuthClientCoAPacketsDropped;
    }

    /**
     * Getter for the "RadiusDynAuthClientCoARequests" variable.
     */
    public Long getRadiusDynAuthClientCoARequests() throws SnmpStatusException {
        return RadiusDynAuthClientCoARequests;
    }

    /**
     * Getter for the "RadiusDynAuthClientDisconPacketsDropped" variable.
     */
    public Long getRadiusDynAuthClientDisconPacketsDropped() throws SnmpStatusException {
        return RadiusDynAuthClientDisconPacketsDropped;
    }

    /**
     * Getter for the "RadiusDynAuthClientDisconTimeouts" variable.
     */
    public Long getRadiusDynAuthClientDisconTimeouts() throws SnmpStatusException {
        return RadiusDynAuthClientDisconTimeouts;
    }

    /**
     * Getter for the "RadiusDynAuthClientDisconPendingRequests" variable.
     */
    public Long getRadiusDynAuthClientDisconPendingRequests() throws SnmpStatusException {
        return RadiusDynAuthClientDisconPendingRequests;
    }

    /**
     * Getter for the "RadiusDynAuthClientDisconBadAuthenticators" variable.
     */
    public Long getRadiusDynAuthClientDisconBadAuthenticators() throws SnmpStatusException {
        return RadiusDynAuthClientDisconBadAuthenticators;
    }

    /**
     * Getter for the "RadiusDynAuthClientMalformedDisconResponses" variable.
     */
    public Long getRadiusDynAuthClientMalformedDisconResponses() throws SnmpStatusException {
        return RadiusDynAuthClientMalformedDisconResponses;
    }

    /**
     * Getter for the "RadiusDynAuthClientDisconNakSessNoContext" variable.
     */
    public Long getRadiusDynAuthClientDisconNakSessNoContext() throws SnmpStatusException {
        return RadiusDynAuthClientDisconNakSessNoContext;
    }

    /**
     * Getter for the "RadiusDynAuthClientDisconNakAuthOnlyRequest" variable.
     */
    public Long getRadiusDynAuthClientDisconNakAuthOnlyRequest() throws SnmpStatusException {
        return RadiusDynAuthClientDisconNakAuthOnlyRequest;
    }

    /**
     * Getter for the "RadiusDynAuthClientDisconNaks" variable.
     */
    public Long getRadiusDynAuthClientDisconNaks() throws SnmpStatusException {
        return RadiusDynAuthClientDisconNaks;
    }

    /**
     * Getter for the "RadiusDynAuthClientDisconAcks" variable.
     */
    public Long getRadiusDynAuthClientDisconAcks() throws SnmpStatusException {
        return RadiusDynAuthClientDisconAcks;
    }

    /**
     * Getter for the "RadiusDynAuthClientDisconRetransmissions" variable.
     */
    public Long getRadiusDynAuthClientDisconRetransmissions() throws SnmpStatusException {
        return RadiusDynAuthClientDisconRetransmissions;
    }

    /**
     * Getter for the "RadiusDynAuthClientDisconAuthOnlyRequests" variable.
     */
    public Long getRadiusDynAuthClientDisconAuthOnlyRequests() throws SnmpStatusException {
        return RadiusDynAuthClientDisconAuthOnlyRequests;
    }

    /**
     * Getter for the "RadiusDynAuthClientCoATimeouts" variable.
     */
    public Long getRadiusDynAuthClientCoATimeouts() throws SnmpStatusException {
        return RadiusDynAuthClientCoATimeouts;
    }

    /**
     * Getter for the "RadiusDynAuthClientDisconRequests" variable.
     */
    public Long getRadiusDynAuthClientDisconRequests() throws SnmpStatusException {
        return RadiusDynAuthClientDisconRequests;
    }

    /**
     * Getter for the "RadiusDynAuthClientCoAPendingRequests" variable.
     */
    public Long getRadiusDynAuthClientCoAPendingRequests() throws SnmpStatusException {
        return RadiusDynAuthClientCoAPendingRequests;
    }

    /**
     * Getter for the "RadiusDynAuthClientRoundTripTime" variable.
     */
    public Long getRadiusDynAuthClientRoundTripTime() throws SnmpStatusException {
        return RadiusDynAuthClientRoundTripTime;
    }

    /**
     * Getter for the "RadiusDynAuthClientCoABadAuthenticators" variable.
     */
    public Long getRadiusDynAuthClientCoABadAuthenticators() throws SnmpStatusException {
        return RadiusDynAuthClientCoABadAuthenticators;
    }

    /**
     * Getter for the "RadiusDynAuthServerID" variable.
     */
    public String getRadiusDynAuthServerID() throws SnmpStatusException {
        return RadiusDynAuthServerID;
    }

    /**
     * Getter for the "RadiusDynAuthClientMalformedCoAResponses" variable.
     */
    public Long getRadiusDynAuthClientMalformedCoAResponses() throws SnmpStatusException {
        return RadiusDynAuthClientMalformedCoAResponses;
    }

    /**
     * Getter for the "RadiusDynAuthServerClientPortNumber" variable.
     */
    public Long getRadiusDynAuthServerClientPortNumber() throws SnmpStatusException {
        return RadiusDynAuthServerClientPortNumber;
    }

    /**
     * Getter for the "RadiusDynAuthClientCoANakSessNoContext" variable.
     */
    public Long getRadiusDynAuthClientCoANakSessNoContext() throws SnmpStatusException {
        return RadiusDynAuthClientCoANakSessNoContext;
    }

    /**
     * Getter for the "RadiusDynAuthServerAddress" variable.
     */
    public Byte[] getRadiusDynAuthServerAddress() throws SnmpStatusException {
        return RadiusDynAuthServerAddress;
    }

    /**
     * Getter for the "RadiusDynAuthClientCoANakAuthOnlyRequest" variable.
     */
    public Long getRadiusDynAuthClientCoANakAuthOnlyRequest() throws SnmpStatusException {
        return RadiusDynAuthClientCoANakAuthOnlyRequest;
    }

    /**
     * Getter for the "RadiusDynAuthServerAddressType" variable.
     */
    public EnumRadiusDynAuthServerAddressType getRadiusDynAuthServerAddressType() throws SnmpStatusException {
        return RadiusDynAuthServerAddressType;
    }

    /**
     * Getter for the "RadiusDynAuthClientCoANaks" variable.
     */
    public Long getRadiusDynAuthClientCoANaks() throws SnmpStatusException {
        return RadiusDynAuthClientCoANaks;
    }

    /**
     * Getter for the "RadiusDynAuthServerIndex" variable.
     */
    public Integer getRadiusDynAuthServerIndex() throws SnmpStatusException {
        return RadiusDynAuthServerIndex;
    }

    /**
     * Getter for the "RadiusDynAuthClientCoAAcks" variable.
     */
    public Long getRadiusDynAuthClientCoAAcks() throws SnmpStatusException {
        return RadiusDynAuthClientCoAAcks;
    }

    /**
     * Getter for the "RadiusDynAuthClientCoARetransmissions" variable.
     */
    public Long getRadiusDynAuthClientCoARetransmissions() throws SnmpStatusException {
        return RadiusDynAuthClientCoARetransmissions;
    }

    /**
     * Getter for the "RadiusDynAuthClientCoAAuthOnlyRequest" variable.
     */
    public Long getRadiusDynAuthClientCoAAuthOnlyRequest() throws SnmpStatusException {
        return RadiusDynAuthClientCoAAuthOnlyRequest;
    }

}
