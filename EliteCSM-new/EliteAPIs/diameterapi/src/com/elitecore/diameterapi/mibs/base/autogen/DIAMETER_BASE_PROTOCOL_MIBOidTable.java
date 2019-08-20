package com.elitecore.diameterapi.mibs.base.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-BASE-PROTOCOL-MIB.
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
 * The class contains metadata definitions for "DIAMETER-BASE-PROTOCOL-MIB".
 * Call SnmpOid.setSnmpOidTable(new DIAMETER_BASE_PROTOCOL_MIBOidTable()) to load the metadata in the SnmpOidTable.
 */
public class DIAMETER_BASE_PROTOCOL_MIBOidTable extends SnmpOidTableSupport implements Serializable {

    /**
     * Default constructor. Initialize the Mib tree.
     */
    public DIAMETER_BASE_PROTOCOL_MIBOidTable() {
        super("DIAMETER_BASE_PROTOCOL_MIB");
        loadMib(varList);
    }

    static SnmpOidRecord varList [] = {
        new SnmpOidRecord("dbpRealmMessageRouteTable", "1.3.6.1.2.1.119.1.6.1", "TA"),
        new SnmpOidRecord("dbpRealmMessageRouteEntry", "1.3.6.1.2.1.119.1.6.1.1", "EN"),
        new SnmpOidRecord("dbpRealmMessageRouteASRsOut", "1.3.6.1.2.1.119.1.6.1.1.19", "C"),
        new SnmpOidRecord("dbpRealmMessageRouteASRsIn", "1.3.6.1.2.1.119.1.6.1.1.18", "C"),
        new SnmpOidRecord("dbpRealmMessageRouteSTAsOut", "1.3.6.1.2.1.119.1.6.1.1.17", "C"),
        new SnmpOidRecord("dbpRealmMessageRouteSTAsIn", "1.3.6.1.2.1.119.1.6.1.1.16", "C"),
        new SnmpOidRecord("dbpRealmMessageRouteSTRsOut", "1.3.6.1.2.1.119.1.6.1.1.15", "C"),
        new SnmpOidRecord("dbpRealmMessageRouteSTRsIn", "1.3.6.1.2.1.119.1.6.1.1.14", "C"),
        new SnmpOidRecord("dbpRealmMessageRouteRAAsOut", "1.3.6.1.2.1.119.1.6.1.1.13", "C"),
        new SnmpOidRecord("dbpRealmMessageRouteRAAsIn", "1.3.6.1.2.1.119.1.6.1.1.12", "C"),
        new SnmpOidRecord("dbpRealmMessageRouteRARsOut", "1.3.6.1.2.1.119.1.6.1.1.11", "C"),
        new SnmpOidRecord("dbpRealmMessageRouteRARsIn", "1.3.6.1.2.1.119.1.6.1.1.10", "C"),
        new SnmpOidRecord("dbpRealmMessageRouteACAsOut", "1.3.6.1.2.1.119.1.6.1.1.9", "C"),
        new SnmpOidRecord("dbpRealmMessageRouteACAsIn", "1.3.6.1.2.1.119.1.6.1.1.8", "C"),
        new SnmpOidRecord("dbpRealmMessageRouteACRsOut", "1.3.6.1.2.1.119.1.6.1.1.7", "C"),
        new SnmpOidRecord("dbpRealmMessageRouteACRsIn", "1.3.6.1.2.1.119.1.6.1.1.6", "C"),
        new SnmpOidRecord("dbpRealmMessageRouteAction", "1.3.6.1.2.1.119.1.6.1.1.5", "I"),
        new SnmpOidRecord("dbpRealmMessageRouteType", "1.3.6.1.2.1.119.1.6.1.1.4", "I"),
        new SnmpOidRecord("dbpRealmMessageRouteReqstsDrop", "1.3.6.1.2.1.119.1.6.1.1.25", "C"),
        new SnmpOidRecord("dbpRealmMessageRouteApp", "1.3.6.1.2.1.119.1.6.1.1.3", "G"),
        new SnmpOidRecord("dbpRealmMessageRoutePendReqstsOut", "1.3.6.1.2.1.119.1.6.1.1.24", "G"),
        new SnmpOidRecord("dbpRealmMessageRouteAccDupReqsts", "1.3.6.1.2.1.119.1.6.1.1.23", "C"),
        new SnmpOidRecord("dbpRealmMessageRouteRealm", "1.3.6.1.2.1.119.1.6.1.1.2", "S"),
        new SnmpOidRecord("dbpRealmMessageRouteIndex", "1.3.6.1.2.1.119.1.6.1.1.1", "G"),
        new SnmpOidRecord("dbpRealmMessageRouteAccRetrans", "1.3.6.1.2.1.119.1.6.1.1.22", "C"),
        new SnmpOidRecord("dbpRealmMessageRouteASAsOut", "1.3.6.1.2.1.119.1.6.1.1.21", "C"),
        new SnmpOidRecord("dbpRealmMessageRouteASAsIn", "1.3.6.1.2.1.119.1.6.1.1.20", "C"),
        new SnmpOidRecord("dbpRealmKnownPeersTable", "1.3.6.1.2.1.119.1.5.1", "TA"),
        new SnmpOidRecord("dbpRealmKnownPeersEntry", "1.3.6.1.2.1.119.1.5.1.1", "EN"),
        new SnmpOidRecord("dbpRealmKnownPeersChosen", "1.3.6.1.2.1.119.1.5.1.1.3", "I"),
        new SnmpOidRecord("dbpRealmKnownPeers", "1.3.6.1.2.1.119.1.5.1.1.2", "G"),
        new SnmpOidRecord("dbpRealmKnownPeersIndex", "1.3.6.1.2.1.119.1.5.1.1.1", "G"),
        new SnmpOidRecord("dbpPerPeerInfoTable", "1.3.6.1.2.1.119.1.4.5.1", "TA"),
        new SnmpOidRecord("dbpPerPeerInfoEntry", "1.3.6.1.2.1.119.1.4.5.1.1", "EN"),
        new SnmpOidRecord("dbpPerPeerStatsRAAsIn", "1.3.6.1.2.1.119.1.4.5.1.1.29", "C"),
        new SnmpOidRecord("dbpPerPeerStatsRARsOut", "1.3.6.1.2.1.119.1.4.5.1.1.28", "C"),
        new SnmpOidRecord("dbpPerPeerStatsRARsIn", "1.3.6.1.2.1.119.1.4.5.1.1.27", "C"),
        new SnmpOidRecord("dbpPerPeerStatsDPAsOut", "1.3.6.1.2.1.119.1.4.5.1.1.26", "C"),
        new SnmpOidRecord("dbpPerPeerStatsDPAsIn", "1.3.6.1.2.1.119.1.4.5.1.1.25", "C"),
        new SnmpOidRecord("dbpPerPeerStatsDPRsOut", "1.3.6.1.2.1.119.1.4.5.1.1.24", "C"),
        new SnmpOidRecord("dbpPerPeerStatsDPRsIn", "1.3.6.1.2.1.119.1.4.5.1.1.23", "C"),
        new SnmpOidRecord("dbpPerPeerStatsDWAsOut", "1.3.6.1.2.1.119.1.4.5.1.1.22", "C"),
        new SnmpOidRecord("dbpPerPeerStatsDWAsIn", "1.3.6.1.2.1.119.1.4.5.1.1.21", "C"),
        new SnmpOidRecord("dbpPerPeerStatsDWRsOut", "1.3.6.1.2.1.119.1.4.5.1.1.20", "C"),
        new SnmpOidRecord("dbpPeerIdentity", "1.3.6.1.2.1.119.1.4.5.1.1.51", "S"),
        new SnmpOidRecord("dbpPerPeerStatsTransportDown", "1.3.6.1.2.1.119.1.4.5.1.1.50", "C"),
        new SnmpOidRecord("dbpPerPeerStatsASAsIn", "1.3.6.1.2.1.119.1.4.5.1.1.9", "C"),
        new SnmpOidRecord("dbpPerPeerStatsASRsOut", "1.3.6.1.2.1.119.1.4.5.1.1.8", "C"),
        new SnmpOidRecord("dbpPerPeerStatsDWRsIn", "1.3.6.1.2.1.119.1.4.5.1.1.19", "C"),
        new SnmpOidRecord("dbpPerPeerStatsASRsIn", "1.3.6.1.2.1.119.1.4.5.1.1.7", "C"),
        new SnmpOidRecord("dbpPerPeerStatsCEAsOut", "1.3.6.1.2.1.119.1.4.5.1.1.18", "C"),
        new SnmpOidRecord("dbpPerPeerStatsPermanentFailures", "1.3.6.1.2.1.119.1.4.5.1.1.49", "C"),
        new SnmpOidRecord("dbpPerPeerStatsTimeoutConnAtmpts", "1.3.6.1.2.1.119.1.4.5.1.1.6", "C"),
        new SnmpOidRecord("dbpPerPeerStatsCEAsIn", "1.3.6.1.2.1.119.1.4.5.1.1.17", "C"),
        new SnmpOidRecord("dbpPerPeerStatsDWCurrentStatus", "1.3.6.1.2.1.119.1.4.5.1.1.5", "I"),
        new SnmpOidRecord("dbpPerPeerStatsCERsOut", "1.3.6.1.2.1.119.1.4.5.1.1.16", "C"),
        new SnmpOidRecord("dbpPerPeerStatsTransientFailures", "1.3.6.1.2.1.119.1.4.5.1.1.48", "C"),
        new SnmpOidRecord("dbpPerPeerInfoWhoInitDisconnect", "1.3.6.1.2.1.119.1.4.5.1.1.4", "I"),
        new SnmpOidRecord("dbpPerPeerStatsCERsIn", "1.3.6.1.2.1.119.1.4.5.1.1.15", "C"),
        new SnmpOidRecord("dbpPerPeerStatsProtocolErrors", "1.3.6.1.2.1.119.1.4.5.1.1.47", "C"),
        new SnmpOidRecord("dbpPerPeerInfoLastDiscCause", "1.3.6.1.2.1.119.1.4.5.1.1.3", "I"),
        new SnmpOidRecord("dbpPerPeerStatsACAsOut", "1.3.6.1.2.1.119.1.4.5.1.1.14", "C"),
        new SnmpOidRecord("dbpPerPeerStatsUnknownTypes", "1.3.6.1.2.1.119.1.4.5.1.1.46", "C"),
        new SnmpOidRecord("dbpPerPeerInfoStateDuration", "1.3.6.1.2.1.119.1.4.5.1.1.2", "T"),
        new SnmpOidRecord("dbpPerPeerStatsACAsIn", "1.3.6.1.2.1.119.1.4.5.1.1.13", "C"),
        new SnmpOidRecord("dbpPerPeerStatsEToEDupMessages", "1.3.6.1.2.1.119.1.4.5.1.1.45", "C"),
        new SnmpOidRecord("dbpPerPeerInfoState", "1.3.6.1.2.1.119.1.4.5.1.1.1", "I"),
        new SnmpOidRecord("dbpPerPeerStatsACRsOut", "1.3.6.1.2.1.119.1.4.5.1.1.12", "C"),
        new SnmpOidRecord("dbpPerPeerStatsHByHDropMessages", "1.3.6.1.2.1.119.1.4.5.1.1.44", "C"),
        new SnmpOidRecord("dbpPerPeerStatsACRsIn", "1.3.6.1.2.1.119.1.4.5.1.1.11", "C"),
        new SnmpOidRecord("dbpPerPeerStatsAccReqstsDropped", "1.3.6.1.2.1.119.1.4.5.1.1.43", "C"),
        new SnmpOidRecord("dbpPerPeerStatsASAsOut", "1.3.6.1.2.1.119.1.4.5.1.1.10", "C"),
        new SnmpOidRecord("dbpPerPeerStatsAccPendReqstsOut", "1.3.6.1.2.1.119.1.4.5.1.1.42", "G"),
        new SnmpOidRecord("dbpPerPeerStatsTotalRetrans", "1.3.6.1.2.1.119.1.4.5.1.1.41", "C"),
        new SnmpOidRecord("dbpPerPeerStatsAccRetrans", "1.3.6.1.2.1.119.1.4.5.1.1.40", "C"),
        new SnmpOidRecord("dbpPerPeerStatsAccsNotRecorded", "1.3.6.1.2.1.119.1.4.5.1.1.39", "C"),
        new SnmpOidRecord("dbpPerPeerStatsMalformedReqsts", "1.3.6.1.2.1.119.1.4.5.1.1.38", "C"),
        new SnmpOidRecord("dbpPerPeerStatsAccDupRequests", "1.3.6.1.2.1.119.1.4.5.1.1.37", "C"),
        new SnmpOidRecord("dbpPerPeerStatsRedirectEvents", "1.3.6.1.2.1.119.1.4.5.1.1.36", "C"),
        new SnmpOidRecord("dbpPerPeerInfoDWReqTimer", "1.3.6.1.2.1.119.1.4.5.1.1.35", "T"),
        new SnmpOidRecord("dbpPerPeerStatsSTAsOut", "1.3.6.1.2.1.119.1.4.5.1.1.34", "C"),
        new SnmpOidRecord("dbpPerPeerStatsSTAsIn", "1.3.6.1.2.1.119.1.4.5.1.1.33", "C"),
        new SnmpOidRecord("dbpPerPeerStatsSTRsOut", "1.3.6.1.2.1.119.1.4.5.1.1.32", "C"),
        new SnmpOidRecord("dbpPerPeerStatsSTRsIn", "1.3.6.1.2.1.119.1.4.5.1.1.31", "C"),
        new SnmpOidRecord("dbpPerPeerStatsRAAsOut", "1.3.6.1.2.1.119.1.4.5.1.1.30", "C"),
        new SnmpOidRecord("dbpPeerVendorTable", "1.3.6.1.2.1.119.1.3.4", "TA"),
        new SnmpOidRecord("dbpPeerVendorEntry", "1.3.6.1.2.1.119.1.3.4.1", "EN"),
        new SnmpOidRecord("dbpPeerVendorRowStatus", "1.3.6.1.2.1.119.1.3.4.1.4", "I"),
        new SnmpOidRecord("dbpPeerVendorStorageType", "1.3.6.1.2.1.119.1.3.4.1.3", "I"),
        new SnmpOidRecord("dbpPeerVendorId", "1.3.6.1.2.1.119.1.3.4.1.2", "I"),
        new SnmpOidRecord("dbpPeerVendorIndex", "1.3.6.1.2.1.119.1.3.4.1.1", "G"),
        new SnmpOidRecord("dbpAppAdvFromPeerTable", "1.3.6.1.2.1.119.1.3.3", "TA"),
        new SnmpOidRecord("dbpAppAdvFromPeerEntry", "1.3.6.1.2.1.119.1.3.3.1", "EN"),
        new SnmpOidRecord("dbpAppAdvFromPeerType", "1.3.6.1.2.1.119.1.3.3.1.3", "I"),
        new SnmpOidRecord("dbpAppAdvFromPeerIndex", "1.3.6.1.2.1.119.1.3.3.1.2", "G"),
        new SnmpOidRecord("dbpAppAdvFromPeerVendorId", "1.3.6.1.2.1.119.1.3.3.1.1", "G"),
        new SnmpOidRecord("dbpPeerIpAddrTable", "1.3.6.1.2.1.119.1.3.2", "TA"),
        new SnmpOidRecord("dbpPeerIpAddrEntry", "1.3.6.1.2.1.119.1.3.2.1", "EN"),
        new SnmpOidRecord("dbpPeerIpAddress", "1.3.6.1.2.1.119.1.3.2.1.3", "S"),
        new SnmpOidRecord("dbpPeerIpAddressType", "1.3.6.1.2.1.119.1.3.2.1.2", "I"),
        new SnmpOidRecord("dbpPeerIpAddressIndex", "1.3.6.1.2.1.119.1.3.2.1.1", "G"),
        new SnmpOidRecord("dbpPeerTable", "1.3.6.1.2.1.119.1.3.1", "TA"),
        new SnmpOidRecord("dbpPeerEntry", "1.3.6.1.2.1.119.1.3.1.1", "EN"),
        new SnmpOidRecord("dbpPeerRowStatus", "1.3.6.1.2.1.119.1.3.1.1.9", "I"),
        new SnmpOidRecord("dbpPeerStorageType", "1.3.6.1.2.1.119.1.3.1.1.8", "I"),
        new SnmpOidRecord("dbpPeerFirmwareRevision", "1.3.6.1.2.1.119.1.3.1.1.7", "S"),
        new SnmpOidRecord("dbpPeerSecurity", "1.3.6.1.2.1.119.1.3.1.1.6", "I"),
        new SnmpOidRecord("dbpPeerTransportProtocol", "1.3.6.1.2.1.119.1.3.1.1.5", "I"),
        new SnmpOidRecord("dbpPeerPortListen", "1.3.6.1.2.1.119.1.3.1.1.4", "G"),
        new SnmpOidRecord("dbpPeerPortConnect", "1.3.6.1.2.1.119.1.3.1.1.3", "G"),
        new SnmpOidRecord("dbpPeerId", "1.3.6.1.2.1.119.1.3.1.1.2", "S"),
        new SnmpOidRecord("dbpPeerIndex", "1.3.6.1.2.1.119.1.3.1.1.1", "G"),
        new SnmpOidRecord("avgIncomingMPS", "1.3.6.1.2.1.119.1.11.4", "C"),
        new SnmpOidRecord("avgRoundTripTime", "1.3.6.1.2.1.119.1.11.3", "C"),
        new SnmpOidRecord("totalActiveRoutingSession", "1.3.6.1.2.1.119.1.11.2", "C"),
        new SnmpOidRecord("totalActiveSession", "1.3.6.1.2.1.119.1.11.1", "C"),
        new SnmpOidRecord("dbpLocalConfigReset", "1.3.6.1.2.1.119.1.2.5", "I"),
        new SnmpOidRecord("dbpLocalResetTime", "1.3.6.1.2.1.119.1.2.4", "T"),
        new SnmpOidRecord("dbpLocalStatsTotalUpTime", "1.3.6.1.2.1.119.1.2.3", "T"),
        new SnmpOidRecord("dbpLocalStatsTotalMessagesOut", "1.3.6.1.2.1.119.1.2.2", "C"),
        new SnmpOidRecord("dbpLocalStatsTotalMessagesIn", "1.3.6.1.2.1.119.1.2.1", "C"),
        new SnmpOidRecord("dbpLocalRealm", "1.3.6.1.2.1.119.1.1.6", "S"),
        new SnmpOidRecord("dbpLocalOriginHost", "1.3.6.1.2.1.119.1.1.5", "S"),
        new SnmpOidRecord("dbpLocalSctpListenPort", "1.3.6.1.2.1.119.1.1.4", "G"),
        new SnmpOidRecord("dbpLocalTcpListenPort", "1.3.6.1.2.1.119.1.1.3", "G"),
        new SnmpOidRecord("dbpLocalId", "1.3.6.1.2.1.119.1.1.1", "S"),
        new SnmpOidRecord("dbpAppAdvToPeerTable", "1.3.6.1.2.1.119.1.1.8", "TA"),
        new SnmpOidRecord("dbpAppAdvToPeerEntry", "1.3.6.1.2.1.119.1.1.8.1", "EN"),
        new SnmpOidRecord("dbpAppAdvToPeerRowStatus", "1.3.6.1.2.1.119.1.1.8.1.5", "I"),
        new SnmpOidRecord("dbpAppAdvToPeerStorageType", "1.3.6.1.2.1.119.1.1.8.1.4", "I"),
        new SnmpOidRecord("dbpAppAdvToPeerServices", "1.3.6.1.2.1.119.1.1.8.1.3", "I"),
        new SnmpOidRecord("dbpAppAdvToPeerIndex", "1.3.6.1.2.1.119.1.1.8.1.2", "G"),
        new SnmpOidRecord("dbpAppAdvToPeerVendorId", "1.3.6.1.2.1.119.1.1.8.1.1", "G"),
        new SnmpOidRecord("dbpLocalApplTable", "1.3.6.1.2.1.119.1.1.7", "TA"),
        new SnmpOidRecord("dbpLocalApplEntry", "1.3.6.1.2.1.119.1.1.7.1", "EN"),
        new SnmpOidRecord("dbpLocalApplRowStatus", "1.3.6.1.2.1.119.1.1.7.1.3", "I"),
        new SnmpOidRecord("dbpLocalApplStorageType", "1.3.6.1.2.1.119.1.1.7.1.2", "I"),
        new SnmpOidRecord("dbpLocalApplIndex", "1.3.6.1.2.1.119.1.1.7.1.1", "G"),
        new SnmpOidRecord("dbpPeerConnectionUpNotifEnabled", "1.3.6.1.2.1.119.1.7.5", "I"),
        new SnmpOidRecord("dbpPeerConnectionDownNotifEnabled", "1.3.6.1.2.1.119.1.7.4", "I"),
        new SnmpOidRecord("dbpPermanentFailureNotifEnabled", "1.3.6.1.2.1.119.1.7.3", "I"),
        new SnmpOidRecord("dbpTransientFailureNotifEnabled", "1.3.6.1.2.1.119.1.7.2", "I"),
        new SnmpOidRecord("dbpProtocolErrorNotifEnabled", "1.3.6.1.2.1.119.1.7.1", "I"),
        new SnmpOidRecord("dbpPermanentFailureNotif", "1.3.6.1.2.1.119.0.3", "NT"),
        new SnmpOidRecord("dbpPeerCfgSkippedGroup", "1.3.6.1.2.1.119.2.2.8", "OBG"),
        new SnmpOidRecord("dbpNotifCfgGroup", "1.3.6.1.2.1.119.2.2.5", "OBG"),
        new SnmpOidRecord("dbpRealmStatsSkippedGroup", "1.3.6.1.2.1.119.2.2.11", "OBG"),
        new SnmpOidRecord("dbpLocalCfgGroup", "1.3.6.1.2.1.119.2.2.1", "OBG"),
        new SnmpOidRecord("dbpProtocolErrorNotif", "1.3.6.1.2.1.119.0.1", "NT"),
        new SnmpOidRecord("dbpPeerStatsGroup", "1.3.6.1.2.1.119.2.2.3", "OBG"),
        new SnmpOidRecord("dbpPeerCfgGroup", "1.3.6.1.2.1.119.2.2.2", "OBG"),
        new SnmpOidRecord("dbpNotificationsGroup", "1.3.6.1.2.1.119.2.2.4", "OBG"),
        new SnmpOidRecord("dbpPeerConnectionDownNotif", "1.3.6.1.2.1.119.0.4", "NT"),
        new SnmpOidRecord("dbpPeerConnectionUpNotif", "1.3.6.1.2.1.119.0.5", "NT"),
        new SnmpOidRecord("dbpTransientFailureNotif", "1.3.6.1.2.1.119.0.2", "NT"),
        new SnmpOidRecord("dbpPeerStatsSkippedGroup", "1.3.6.1.2.1.119.2.2.9", "OBG"),
        new SnmpOidRecord("dbpRealmCfgSkippedGroup", "1.3.6.1.2.1.119.2.2.10", "OBG"),
        new SnmpOidRecord("dbpLocalStatsSkippedGroup", "1.3.6.1.2.1.119.2.2.7", "OBG"),
        new SnmpOidRecord("dbpLocalCfgSkippedGroup", "1.3.6.1.2.1.119.2.2.6", "OBG")    };
}
