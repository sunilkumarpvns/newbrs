package com.elitecore.aaa.mibs.radius.authentication.client.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RADIUS-AUTH-CLIENT-MIB.
//

// java imports
//
import java.io.Serializable;

// jmx imports
//
import com.sun.management.snmp.SnmpOidRecord;

// jdmk imports
//
import com.sun.management.snmp.SnmpOidTableSupport;

/**
 * The class contains metadata definitions for "RADIUS-AUTH-CLIENT-MIB".
 * Call SnmpOid.setSnmpOidTable(new RADIUS_AUTH_CLIENT_MIBOidTable()) to load the metadata in the SnmpOidTable.
 */
public class RADIUS_AUTH_CLIENT_MIBOidTable extends SnmpOidTableSupport implements Serializable {

    /**
     * Default constructor. Initialize the Mib tree.
     */
    public RADIUS_AUTH_CLIENT_MIBOidTable() {
        super("RADIUS_AUTH_CLIENT_MIB");
        loadMib(varList);
    }

    static SnmpOidRecord varList [] = {
        new SnmpOidRecord("radiusAuthServerTable", "1.3.6.1.2.1.67.1.2.1.1.3", "TA"),
        new SnmpOidRecord("radiusAuthServerEntry", "1.3.6.1.2.1.67.1.2.1.1.3.1", "EN"),
        new SnmpOidRecord("radiusAuthClientAccessChallenges", "1.3.6.1.2.1.67.1.2.1.1.3.1.9", "C"),
        new SnmpOidRecord("radiusAuthClientAccessRejects", "1.3.6.1.2.1.67.1.2.1.1.3.1.8", "C"),
        new SnmpOidRecord("radiusAuthClientAccessAccepts", "1.3.6.1.2.1.67.1.2.1.1.3.1.7", "C"),
        new SnmpOidRecord("radiusAuthClientPacketsDropped", "1.3.6.1.2.1.67.1.2.1.1.3.1.15", "C"),
        new SnmpOidRecord("radiusAuthClientAccessRetransmissions", "1.3.6.1.2.1.67.1.2.1.1.3.1.6", "C"),
        new SnmpOidRecord("radiusAuthClientUnknownTypes", "1.3.6.1.2.1.67.1.2.1.1.3.1.14", "C"),
        new SnmpOidRecord("radiusAuthClientAccessRequests", "1.3.6.1.2.1.67.1.2.1.1.3.1.5", "C"),
        new SnmpOidRecord("radiusAuthClientTimeouts", "1.3.6.1.2.1.67.1.2.1.1.3.1.13", "C"),
        new SnmpOidRecord("radiusAuthClientRoundTripTime", "1.3.6.1.2.1.67.1.2.1.1.3.1.4", "T"),
        new SnmpOidRecord("radiusAuthClientPendingRequests", "1.3.6.1.2.1.67.1.2.1.1.3.1.12", "G"),
        new SnmpOidRecord("radiusAuthClientServerPortNumber", "1.3.6.1.2.1.67.1.2.1.1.3.1.3", "I"),
        new SnmpOidRecord("radiusAuthServerAddress", "1.3.6.1.2.1.67.1.2.1.1.3.1.2", "IP"),
        new SnmpOidRecord("radiusAuthClientBadAuthenticators", "1.3.6.1.2.1.67.1.2.1.1.3.1.11", "C"),
        new SnmpOidRecord("radiusAuthServerIndex", "1.3.6.1.2.1.67.1.2.1.1.3.1.1", "I"),
        new SnmpOidRecord("radiusAuthClientMalformedAccessResponses", "1.3.6.1.2.1.67.1.2.1.1.3.1.10", "C"),
        new SnmpOidRecord("radiusAuthClientIdentifier", "1.3.6.1.2.1.67.1.2.1.1.2", "S"),
        new SnmpOidRecord("radiusAuthClientInvalidServerAddresses", "1.3.6.1.2.1.67.1.2.1.1.1", "C"),
        new SnmpOidRecord("radiusAuthClientMIBGroup", "1.3.6.1.2.1.67.1.2.2.2.1", "OBG"),
        new SnmpOidRecord("radiusMIB", "1.3.6.1.2.1.67", "ID")    };
}