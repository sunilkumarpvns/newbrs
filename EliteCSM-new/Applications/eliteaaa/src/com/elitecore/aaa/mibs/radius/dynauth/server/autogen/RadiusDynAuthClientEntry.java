package com.elitecore.aaa.mibs.radius.dynauth.server.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RADIUS-DYNAUTH-SERVER-MIB.
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
 * The class is used for implementing the "RadiusDynAuthClientEntry" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.146.1.2.1.
 */
public class RadiusDynAuthClientEntry implements RadiusDynAuthClientEntryMBean, Serializable {

    /**
     * Variable for storing the value of "RadiusDynAuthServCoANaks".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.19".
     *
     * "The number of RADIUS CoA-NAK packets sent to
     * this Dynamic Authorization Client.  This includes
     * the RADIUS CoA-NAK packets sent with a Service-Type
     * attribute with value 'Authorize Only' and the RADIUS
     * CoA-NAK packets sent because no session context was
     * found.  This counter may experience a discontinuity
     * when the DAS module (re)starts, as indicated by the
     * value of radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServCoANaks = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServCoAAcks".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.18".
     *
     * "The number of RADIUS CoA-ACK packets sent to this
     * Dynamic Authorization Client.  This counter may
     * experience a discontinuity when the DAS module
     * 
     * 
     * 
     * (re)starts, as indicated by the value of
     * radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServCoAAcks = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServDupCoARequests".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.17".
     *
     * "The number of duplicate RADIUS CoA-Request packets
     * received from this Dynamic Authorization Client.  This
     * counter may experience a discontinuity when the DAS
     * module (re)starts, as indicated by the value of
     * radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServDupCoARequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServCoAAuthOnlyRequests".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.16".
     *
     * "The number of RADIUS CoA-requests that include a
     * Service-Type attribute with value 'Authorize Only'
     * received from this Dynamic Authorization Client.  This
     * counter may experience a discontinuity when the DAS
     * module (re)starts, as indicated by the value of
     * radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServCoAAuthOnlyRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServCoARequests".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.15".
     *
     * "The number of RADIUS CoA-requests received from this
     * Dynamic Authorization Client.  This also includes
     * the CoA requests that have a Service-Type attribute
     * with value 'Authorize Only'.  This counter may
     * experience a discontinuity when the DAS module
     * (re)starts, as indicated by the value of
     * radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServCoARequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServDisconPacketsDropped".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.14".
     *
     * "The number of incoming Disconnect-Requests
     * from this Dynamic Authorization Client silently
     * discarded by the server application for some reason
     * other than malformed, bad authenticators, or unknown
     * types.  This counter may experience a discontinuity
     * when the DAS module (re)starts, as indicated by the
     * value of radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServDisconPacketsDropped = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServDisconBadAuthenticators".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.13".
     *
     * "The number of RADIUS Disconnect-Request packets
     * that contained an invalid Authenticator field
     * received from this Dynamic Authorization Client.  This
     * counter may experience a discontinuity when the DAS
     * module (re)starts, as indicated by the value of
     * radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServDisconBadAuthenticators = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServMalformedDisconRequests".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.12".
     *
     * "The number of malformed RADIUS Disconnect-Request
     * packets received from this Dynamic Authorization
     * Client.  Bad authenticators and unknown types are not
     * included as malformed Disconnect-Requests.  This counter
     * may experience a discontinuity when the DAS module
     * (re)starts, as indicated by the value of
     * radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServMalformedDisconRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServDisconUserSessRemoved".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.11".
     *
     * "The number of user sessions removed for the
     * Disconnect-Requests received from this
     * Dynamic Authorization Client.  Depending on site-
     * specific policies, a single Disconnect request
     * can remove multiple user sessions.  In cases where
     * this Dynamic Authorization Server has no
     * knowledge of the number of user sessions that
     * are affected by a single request, each such
     * Disconnect-Request will count as a single
     * affected user session only.  This counter may experience
     * a discontinuity when the DAS module (re)starts, as
     * indicated by the value of
     * radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServDisconUserSessRemoved = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServDisconNakSessNoContext".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.10".
     *
     * "The number of RADIUS Disconnect-NAK packets
     * sent to this Dynamic Authorization Client
     * because no session context was found.  This counter may
     * 
     * 
     * 
     * experience a discontinuity when the DAS module
     * (re)starts, as indicated by the value of
     * radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServDisconNakSessNoContext = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServDisconNakAuthOnlyRequests".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.9".
     *
     * "The number of RADIUS Disconnect-NAK packets that
     * include a Service-Type attribute with value
     * 'Authorize Only' sent to this Dynamic Authorization
     * Client.  This counter may experience a discontinuity
     * when the DAS module (re)starts, as indicated by the
     * value of radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServDisconNakAuthOnlyRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServDisconNaks".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.8".
     *
     * "The number of RADIUS Disconnect-NAK packets
     * sent to this Dynamic Authorization Client.  This
     * includes the RADIUS Disconnect-NAK packets sent
     * with a Service-Type attribute with value 'Authorize
     * Only' and the RADIUS Disconnect-NAK packets sent
     * because no session context was found.  This counter
     * may experience a discontinuity when the DAS module
     * (re)starts, as indicated by the value of
     * radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServDisconNaks = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServDisconAcks".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.7".
     *
     * "The number of RADIUS Disconnect-ACK packets sent to
     * this Dynamic Authorization Client.  This counter may
     * experience a discontinuity when the DAS module
     * (re)starts, as indicated by the value of
     * radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServDisconAcks = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServerCounterDiscontinuity".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.27".
     *
     * "The time (in hundredths of a second) since the
     * last counter discontinuity.  A discontinuity may
     * be the result of a reinitialization of the DAS
     * module within the managed entity."
     *
     */
    protected Long RadiusDynAuthServerCounterDiscontinuity = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServDupDisconRequests".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.6".
     *
     * "The number of duplicate RADIUS Disconnect-Request
     * packets received from this Dynamic Authorization
     * Client.  This counter may experience a discontinuity
     * when the DAS module (re)starts, as indicated by the
     * value of radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServDupDisconRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServDisconAuthOnlyRequests".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.5".
     *
     * "The number of RADIUS Disconnect-Requests that include
     * a Service-Type attribute with value 'Authorize Only'
     * received from this Dynamic Authorization Client.  This
     * counter may experience a discontinuity when the DAS
     * module (re)starts, as indicated by the value of
     * radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServDisconAuthOnlyRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServUnknownTypes".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.26".
     *
     * "The number of incoming packets of unknown types that
     * were received on the Dynamic Authorization port.  This
     * counter may experience a discontinuity when the DAS
     * 
     * 
     * 
     * module (re)starts, as indicated by the value of
     * radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServUnknownTypes = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServDisconRequests".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.4".
     *
     * "The number of RADIUS Disconnect-Requests received
     * from this Dynamic Authorization Client.  This also
     * includes the RADIUS Disconnect-Requests that have a
     * Service-Type attribute with value 'Authorize Only'.
     * This counter may experience a discontinuity when the
     * 
     * 
     * 
     * DAS module (re)starts as indicated by the value of
     * radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServDisconRequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServCoAPacketsDropped".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.25".
     *
     * "The number of incoming CoA packets from this
     * Dynamic Authorization Client silently discarded
     * by the server application for some reason other than
     * malformed, bad authenticators, or unknown types.  This
     * counter may experience a discontinuity when the DAS
     * module (re)starts, as indicated by the value of
     * radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServCoAPacketsDropped = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServCoABadAuthenticators".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.24".
     *
     * "The number of RADIUS CoA-Request packets that
     * contained an invalid Authenticator field received
     * from this Dynamic Authorization Client.  This counter
     * may experience a discontinuity when the DAS module
     * (re)starts, as indicated by the value of
     * radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServCoABadAuthenticators = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientAddress".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.3".
     *
     * "The IP address value of the RADIUS Dynamic
     * Authorization Client referred to in this table entry,
     * using the version neutral IP address format.  The type
     * of this address is determined by the value of
     * the radiusDynAuthClientAddressType object."
     *
     */
    protected Byte[] RadiusDynAuthClientAddress = { new Byte("74"), new Byte("68"), new Byte("77"), new Byte("75")};

    /**
     * Variable for storing the value of "RadiusDynAuthClientAddressType".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.2".
     *
     * "The type of IP address of the RADIUS Dynamic
     * Authorization Client referred to in this table entry."
     *
     */
    protected EnumRadiusDynAuthClientAddressType RadiusDynAuthClientAddressType = new EnumRadiusDynAuthClientAddressType();

    /**
     * Variable for storing the value of "RadiusDynAuthServMalformedCoARequests".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.23".
     *
     * "The number of malformed RADIUS CoA-Request packets
     * received from this Dynamic Authorization Client.  Bad
     * authenticators and unknown types are not included as
     * malformed CoA-Requests.  This counter may experience a
     * discontinuity when the DAS module (re)starts, as
     * indicated by the value of
     * radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServMalformedCoARequests = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServCoAUserSessChanged".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.22".
     *
     * "The number of user sessions authorization
     * changed for the CoA-Requests received from this
     * Dynamic Authorization Client.  Depending on site-
     * specific policies, a single CoA request can change
     * multiple user sessions' authorization.  In cases where
     * this Dynamic Authorization Server has no knowledge of
     * the number of user sessions that are affected by a
     * single request, each such CoA-Request will
     * count as a single affected user session only.  This
     * counter may experience a discontinuity when the DAS
     * module (re)starts, as indicated by the value of
     * radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServCoAUserSessChanged = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthClientIndex".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.1".
     *
     * "A number uniquely identifying each RADIUS Dynamic
     * Authorization Client with which this Dynamic
     * Authorization Server communicates.  This number is
     * allocated by the agent implementing this MIB module
     * and is unique in this context."
     *
     */
    protected Integer RadiusDynAuthClientIndex = new Integer(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServCoANakSessNoContext".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.21".
     *
     * "The number of RADIUS CoA-NAK packets sent to this
     * Dynamic Authorization Client because no session context
     * was found.  This counter may experience a discontinuity
     * when the DAS module (re)starts, as indicated by the
     * value of radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServCoANakSessNoContext = new Long(1);

    /**
     * Variable for storing the value of "RadiusDynAuthServCoANakAuthOnlyRequests".
     * The variable is identified by: "1.3.6.1.2.1.146.1.2.1.20".
     *
     * "The number of RADIUS CoA-NAK packets that include a
     * Service-Type attribute with value 'Authorize Only'
     * sent to this Dynamic Authorization Client.  This counter
     * may experience a discontinuity when the DAS module
     * (re)starts, as indicated by the value of
     * radiusDynAuthServerCounterDiscontinuity."
     *
     */
    protected Long RadiusDynAuthServCoANakAuthOnlyRequests = new Long(1);


    /**
     * Constructor for the "RadiusDynAuthClientEntry" group.
     */
    public RadiusDynAuthClientEntry(SnmpMib myMib) {
    }

    public RadiusDynAuthClientEntry() {
	}
    
    /**
     * Getter for the "RadiusDynAuthServCoANaks" variable.
     */
    public Long getRadiusDynAuthServCoANaks() throws SnmpStatusException {
        return RadiusDynAuthServCoANaks;
    }

    /**
     * Getter for the "RadiusDynAuthServCoAAcks" variable.
     */
    public Long getRadiusDynAuthServCoAAcks() throws SnmpStatusException {
        return RadiusDynAuthServCoAAcks;
    }

    /**
     * Getter for the "RadiusDynAuthServDupCoARequests" variable.
     */
    public Long getRadiusDynAuthServDupCoARequests() throws SnmpStatusException {
        return RadiusDynAuthServDupCoARequests;
    }

    /**
     * Getter for the "RadiusDynAuthServCoAAuthOnlyRequests" variable.
     */
    public Long getRadiusDynAuthServCoAAuthOnlyRequests() throws SnmpStatusException {
        return RadiusDynAuthServCoAAuthOnlyRequests;
    }

    /**
     * Getter for the "RadiusDynAuthServCoARequests" variable.
     */
    public Long getRadiusDynAuthServCoARequests() throws SnmpStatusException {
        return RadiusDynAuthServCoARequests;
    }

    /**
     * Getter for the "RadiusDynAuthServDisconPacketsDropped" variable.
     */
    public Long getRadiusDynAuthServDisconPacketsDropped() throws SnmpStatusException {
        return RadiusDynAuthServDisconPacketsDropped;
    }

    /**
     * Getter for the "RadiusDynAuthServDisconBadAuthenticators" variable.
     */
    public Long getRadiusDynAuthServDisconBadAuthenticators() throws SnmpStatusException {
        return RadiusDynAuthServDisconBadAuthenticators;
    }

    /**
     * Getter for the "RadiusDynAuthServMalformedDisconRequests" variable.
     */
    public Long getRadiusDynAuthServMalformedDisconRequests() throws SnmpStatusException {
        return RadiusDynAuthServMalformedDisconRequests;
    }

    /**
     * Getter for the "RadiusDynAuthServDisconUserSessRemoved" variable.
     */
    public Long getRadiusDynAuthServDisconUserSessRemoved() throws SnmpStatusException {
        return RadiusDynAuthServDisconUserSessRemoved;
    }

    /**
     * Getter for the "RadiusDynAuthServDisconNakSessNoContext" variable.
     */
    public Long getRadiusDynAuthServDisconNakSessNoContext() throws SnmpStatusException {
        return RadiusDynAuthServDisconNakSessNoContext;
    }

    /**
     * Getter for the "RadiusDynAuthServDisconNakAuthOnlyRequests" variable.
     */
    public Long getRadiusDynAuthServDisconNakAuthOnlyRequests() throws SnmpStatusException {
        return RadiusDynAuthServDisconNakAuthOnlyRequests;
    }

    /**
     * Getter for the "RadiusDynAuthServDisconNaks" variable.
     */
    public Long getRadiusDynAuthServDisconNaks() throws SnmpStatusException {
        return RadiusDynAuthServDisconNaks;
    }

    /**
     * Getter for the "RadiusDynAuthServDisconAcks" variable.
     */
    public Long getRadiusDynAuthServDisconAcks() throws SnmpStatusException {
        return RadiusDynAuthServDisconAcks;
    }

    /**
     * Getter for the "RadiusDynAuthServerCounterDiscontinuity" variable.
     */
    public Long getRadiusDynAuthServerCounterDiscontinuity() throws SnmpStatusException {
        return RadiusDynAuthServerCounterDiscontinuity;
    }

    /**
     * Getter for the "RadiusDynAuthServDupDisconRequests" variable.
     */
    public Long getRadiusDynAuthServDupDisconRequests() throws SnmpStatusException {
        return RadiusDynAuthServDupDisconRequests;
    }

    /**
     * Getter for the "RadiusDynAuthServDisconAuthOnlyRequests" variable.
     */
    public Long getRadiusDynAuthServDisconAuthOnlyRequests() throws SnmpStatusException {
        return RadiusDynAuthServDisconAuthOnlyRequests;
    }

    /**
     * Getter for the "RadiusDynAuthServUnknownTypes" variable.
     */
    public Long getRadiusDynAuthServUnknownTypes() throws SnmpStatusException {
        return RadiusDynAuthServUnknownTypes;
    }

    /**
     * Getter for the "RadiusDynAuthServDisconRequests" variable.
     */
    public Long getRadiusDynAuthServDisconRequests() throws SnmpStatusException {
        return RadiusDynAuthServDisconRequests;
    }

    /**
     * Getter for the "RadiusDynAuthServCoAPacketsDropped" variable.
     */
    public Long getRadiusDynAuthServCoAPacketsDropped() throws SnmpStatusException {
        return RadiusDynAuthServCoAPacketsDropped;
    }

    /**
     * Getter for the "RadiusDynAuthServCoABadAuthenticators" variable.
     */
    public Long getRadiusDynAuthServCoABadAuthenticators() throws SnmpStatusException {
        return RadiusDynAuthServCoABadAuthenticators;
    }

    /**
     * Getter for the "RadiusDynAuthClientAddress" variable.
     */
    public Byte[] getRadiusDynAuthClientAddress() throws SnmpStatusException {
        return RadiusDynAuthClientAddress;
    }

    /**
     * Getter for the "RadiusDynAuthClientAddressType" variable.
     */
    public EnumRadiusDynAuthClientAddressType getRadiusDynAuthClientAddressType() throws SnmpStatusException {
        return RadiusDynAuthClientAddressType;
    }

    /**
     * Getter for the "RadiusDynAuthServMalformedCoARequests" variable.
     */
    public Long getRadiusDynAuthServMalformedCoARequests() throws SnmpStatusException {
        return RadiusDynAuthServMalformedCoARequests;
    }

    /**
     * Getter for the "RadiusDynAuthServCoAUserSessChanged" variable.
     */
    public Long getRadiusDynAuthServCoAUserSessChanged() throws SnmpStatusException {
        return RadiusDynAuthServCoAUserSessChanged;
    }

    /**
     * Getter for the "RadiusDynAuthClientIndex" variable.
     */
    public Integer getRadiusDynAuthClientIndex() throws SnmpStatusException {
        return RadiusDynAuthClientIndex;
    }

    /**
     * Getter for the "RadiusDynAuthServCoANakSessNoContext" variable.
     */
    public Long getRadiusDynAuthServCoANakSessNoContext() throws SnmpStatusException {
        return RadiusDynAuthServCoANakSessNoContext;
    }

    /**
     * Getter for the "RadiusDynAuthServCoANakAuthOnlyRequests" variable.
     */
    public Long getRadiusDynAuthServCoANakAuthOnlyRequests() throws SnmpStatusException {
        return RadiusDynAuthServCoANakAuthOnlyRequests;
    }

}
