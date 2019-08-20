package com.elitecore.aaa.mibs.radius.accounting.server.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RADIUS-ACC-SERVER-MIB.
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
 * The class contains metadata definitions for "RADIUS-ACC-SERVER-MIB".
 * Call SnmpOid.setSnmpOidTable(new RADIUS_ACC_SERVER_MIBOidTable()) to load the metadata in the SnmpOidTable.
 */
public class RADIUS_ACC_SERVER_MIBOidTable extends SnmpOidTableSupport implements Serializable {

    /**
     * Default constructor. Initialize the Mib tree.
     */
    public RADIUS_ACC_SERVER_MIBOidTable() {
        super("RADIUS_ACC_SERVER_MIB");
        loadMib(varList);
    }

    static SnmpOidRecord varList [] = {
        new SnmpOidRecord("radiusAccServTotalMalformedRequests", "1.3.6.1.2.1.67.2.1.1.1.9", "C"),
        new SnmpOidRecord("radiusAccServTotalResponses", "1.3.6.1.2.1.67.2.1.1.1.8", "C"),
        new SnmpOidRecord("radiusAccServTotalDupRequests", "1.3.6.1.2.1.67.2.1.1.1.7", "C"),
        new SnmpOidRecord("radiusAccServTotalInvalidRequests", "1.3.6.1.2.1.67.2.1.1.1.6", "C"),
        new SnmpOidRecord("radiusAccServTotalRequests", "1.3.6.1.2.1.67.2.1.1.1.5", "C"),
        new SnmpOidRecord("radiusAccClientTable", "1.3.6.1.2.1.67.2.1.1.1.14", "TA"),
        new SnmpOidRecord("radiusAccClientEntry", "1.3.6.1.2.1.67.2.1.1.1.14.1", "EN"),
        new SnmpOidRecord("radiusAccServMalformedRequests", "1.3.6.1.2.1.67.2.1.1.1.14.1.9", "C"),
        new SnmpOidRecord("radiusAccServBadAuthenticators", "1.3.6.1.2.1.67.2.1.1.1.14.1.8", "C"),
        new SnmpOidRecord("radiusAccServResponses", "1.3.6.1.2.1.67.2.1.1.1.14.1.7", "C"),
        new SnmpOidRecord("radiusAccServDupRequests", "1.3.6.1.2.1.67.2.1.1.1.14.1.6", "C"),
        new SnmpOidRecord("radiusAccServRequests", "1.3.6.1.2.1.67.2.1.1.1.14.1.5", "C"),
        new SnmpOidRecord("radiusAccServPacketsDropped", "1.3.6.1.2.1.67.2.1.1.1.14.1.4", "C"),
        new SnmpOidRecord("radiusAccClientID", "1.3.6.1.2.1.67.2.1.1.1.14.1.3", "S"),
        new SnmpOidRecord("radiusAccClientAddress", "1.3.6.1.2.1.67.2.1.1.1.14.1.2", "IP"),
        new SnmpOidRecord("radiusAccServUnknownTypes", "1.3.6.1.2.1.67.2.1.1.1.14.1.11", "C"),
        new SnmpOidRecord("radiusAccServNoRecords", "1.3.6.1.2.1.67.2.1.1.1.14.1.10", "C"),
        new SnmpOidRecord("radiusAccClientIndex", "1.3.6.1.2.1.67.2.1.1.1.14.1.1", "I"),
        new SnmpOidRecord("radiusAccServConfigReset", "1.3.6.1.2.1.67.2.1.1.1.4", "I"),
        new SnmpOidRecord("radiusAccServTotalUnknownTypes", "1.3.6.1.2.1.67.2.1.1.1.13", "C"),
        new SnmpOidRecord("radiusAccServTotalNoRecords", "1.3.6.1.2.1.67.2.1.1.1.12", "C"),
        new SnmpOidRecord("radiusAccServResetTime", "1.3.6.1.2.1.67.2.1.1.1.3", "T"),
        new SnmpOidRecord("radiusAccServTotalPacketsDropped", "1.3.6.1.2.1.67.2.1.1.1.11", "C"),
        new SnmpOidRecord("radiusAccServUpTime", "1.3.6.1.2.1.67.2.1.1.1.2", "T"),
        new SnmpOidRecord("radiusAccServTotalBadAuthenticators", "1.3.6.1.2.1.67.2.1.1.1.10", "C"),
        new SnmpOidRecord("radiusAccServIdent", "1.3.6.1.2.1.67.2.1.1.1.1", "S"),
        new SnmpOidRecord("radiusMIB", "1.3.6.1.2.1.67", "ID"),
        new SnmpOidRecord("radiusAccServMIBGroup", "1.3.6.1.2.1.67.2.1.2.2.1", "OBG")    };
}
