package com.elitecore.diameterapi.mibs.custom.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-STACK-MIB.
//

// java imports
//
import java.io.Serializable;

import com.sun.management.snmp.SnmpOidRecord;
import com.sun.management.snmp.SnmpOidTableSupport;
// jmx imports
//
// jdmk imports
//

/**
 * The class contains metadata definitions for "DIAMETER-STACK-MIB".
 * Call SnmpOid.setSnmpOidTable(new DIAMETER_STACK_MIBOidTable()) to load the metadata in the SnmpOidTable.
 */
public class DIAMETER_STACK_MIBOidTable extends SnmpOidTableSupport implements Serializable {

    /**
     * Default constructor. Initialize the Mib tree.
     */
    public DIAMETER_STACK_MIBOidTable() {
        super("DIAMETER_STACK_MIB");
        loadMib(varList);
    }

    static SnmpOidRecord varList [] = {
        new SnmpOidRecord("stackListeningPort", "1.3.6.1.4.1.21067.5.8", "I"),
        new SnmpOidRecord("stackIPAddress", "1.3.6.1.4.1.21067.5.7", "S"),
        new SnmpOidRecord("stackConfigReset", "1.3.6.1.4.1.21067.5.6", "I"),
        new SnmpOidRecord("stackResetTime", "1.3.6.1.4.1.21067.5.5", "T"),
        new SnmpOidRecord("stackUpTime", "1.3.6.1.4.1.21067.5.4", "T"),
        new SnmpOidRecord("resultCodeStatisticsTable", "1.3.6.1.4.1.21067.5.102.3.3.1", "TA"),
        new SnmpOidRecord("resultCodeStatisticsEntry", "1.3.6.1.4.1.21067.5.102.3.3.1.1", "EN"),
        new SnmpOidRecord("rcwCompstIndexValue", "1.3.6.1.4.1.21067.5.102.3.3.1.1.6", "S"),
        new SnmpOidRecord("rcwApplicationName", "1.3.6.1.4.1.21067.5.102.3.3.1.1.5", "S"),
        new SnmpOidRecord("rcwPeerIdentity", "1.3.6.1.4.1.21067.5.102.3.3.1.1.4", "S"),
        new SnmpOidRecord("rcwTx", "1.3.6.1.4.1.21067.5.102.3.3.1.1.3", "C"),
        new SnmpOidRecord("rcwRx", "1.3.6.1.4.1.21067.5.102.3.3.1.1.2", "C"),
        new SnmpOidRecord("resultCode", "1.3.6.1.4.1.21067.5.102.3.3.1.1.1", "G"),
        new SnmpOidRecord("commandCodeStatisticsTable", "1.3.6.1.4.1.21067.5.102.3.2.1", "TA"),
        new SnmpOidRecord("commandCodeStatisticsEntry", "1.3.6.1.4.1.21067.5.102.3.2.1.1", "EN"),
        new SnmpOidRecord("ccwCompstIndexValue", "1.3.6.1.4.1.21067.5.102.3.2.1.1.18", "S"),
        new SnmpOidRecord("ccwApplicationName", "1.3.6.1.4.1.21067.5.102.3.2.1.1.17", "S"),
        new SnmpOidRecord("ccwPeerIdentity", "1.3.6.1.4.1.21067.5.102.3.2.1.1.16", "S"),
        new SnmpOidRecord("ccwRequestPn", "1.3.6.1.4.1.21067.5.102.3.2.1.1.15", "C"),
        new SnmpOidRecord("ccwMalformedPacketRx", "1.3.6.1.4.1.21067.5.102.3.2.1.1.14", "C"),
        new SnmpOidRecord("ccwDuplicateEtEAnswer", "1.3.6.1.4.1.21067.5.102.3.2.1.1.13", "C"),
        new SnmpOidRecord("ccwDuplicateRequest", "1.3.6.1.4.1.21067.5.102.3.2.1.1.12", "C"),
        new SnmpOidRecord("ccwUnknownHbHAnswerDropped", "1.3.6.1.4.1.21067.5.102.3.2.1.1.11", "C"),
        new SnmpOidRecord("ccwAnswerDr", "1.3.6.1.4.1.21067.5.102.3.2.1.1.10", "C"),
        new SnmpOidRecord("ccwRequestDr", "1.3.6.1.4.1.21067.5.102.3.2.1.1.9", "C"),
        new SnmpOidRecord("ccwRequestTimeOut", "1.3.6.1.4.1.21067.5.102.3.2.1.1.8", "C"),
        new SnmpOidRecord("ccwRequestRetransmitted", "1.3.6.1.4.1.21067.5.102.3.2.1.1.7", "C"),
        new SnmpOidRecord("ccwAnswerRx", "1.3.6.1.4.1.21067.5.102.3.2.1.1.6", "C"),
        new SnmpOidRecord("ccwRequestTx", "1.3.6.1.4.1.21067.5.102.3.2.1.1.5", "C"),
        new SnmpOidRecord("ccwAnswerTx", "1.3.6.1.4.1.21067.5.102.3.2.1.1.4", "C"),
        new SnmpOidRecord("ccwRequestRx", "1.3.6.1.4.1.21067.5.102.3.2.1.1.3", "C"),
        new SnmpOidRecord("commandCodeName", "1.3.6.1.4.1.21067.5.102.3.2.1.1.2", "S"),
        new SnmpOidRecord("commandCode", "1.3.6.1.4.1.21067.5.102.3.2.1.1.1", "G"),
        new SnmpOidRecord("appWiseStatisticsTable", "1.3.6.1.4.1.21067.5.102.3.1", "TA"),
        new SnmpOidRecord("appWiseStatisticsEntry", "1.3.6.1.4.1.21067.5.102.3.1.1", "EN"),
        new SnmpOidRecord("appwUnknownHbHAnswerDropped", "1.3.6.1.4.1.21067.5.102.3.1.1.9", "C"),
        new SnmpOidRecord("appwAnswerDr", "1.3.6.1.4.1.21067.5.102.3.1.1.8", "C"),
        new SnmpOidRecord("appwCompstIndexValue", "1.3.6.1.4.1.21067.5.102.3.1.1.16", "S"),
        new SnmpOidRecord("appwRequestDr", "1.3.6.1.4.1.21067.5.102.3.1.1.7", "C"),
        new SnmpOidRecord("appwRequestTimeOut", "1.3.6.1.4.1.21067.5.102.3.1.1.6", "C"),
        new SnmpOidRecord("appwApplicationName", "1.3.6.1.4.1.21067.5.102.3.1.1.15", "S"),
        new SnmpOidRecord("appwPeerIdentity", "1.3.6.1.4.1.21067.5.102.3.1.1.14", "S"),
        new SnmpOidRecord("appwRequestRetransmitted", "1.3.6.1.4.1.21067.5.102.3.1.1.5", "C"),
        new SnmpOidRecord("appwAnswerRx", "1.3.6.1.4.1.21067.5.102.3.1.1.4", "C"),
        new SnmpOidRecord("appwRequestPn", "1.3.6.1.4.1.21067.5.102.3.1.1.13", "C"),
        new SnmpOidRecord("appwRequestTx", "1.3.6.1.4.1.21067.5.102.3.1.1.3", "C"),
        new SnmpOidRecord("appwMalformedPacketRx", "1.3.6.1.4.1.21067.5.102.3.1.1.12", "C"),
        new SnmpOidRecord("appwDuplicateEtEAnswer", "1.3.6.1.4.1.21067.5.102.3.1.1.11", "C"),
        new SnmpOidRecord("appwAnswerTx", "1.3.6.1.4.1.21067.5.102.3.1.1.2", "C"),
        new SnmpOidRecord("appwRequestRx", "1.3.6.1.4.1.21067.5.102.3.1.1.1", "C"),
        new SnmpOidRecord("appwDuplicateRequest", "1.3.6.1.4.1.21067.5.102.3.1.1.10", "C"),
        new SnmpOidRecord("peerIpAddrTable", "1.3.6.1.4.1.21067.5.102.2.1", "TA"),
        new SnmpOidRecord("peerIpAddrEntry", "1.3.6.1.4.1.21067.5.102.2.1.1", "EN"),
        new SnmpOidRecord("peerIpAddrPeerIdentity", "1.3.6.1.4.1.21067.5.102.2.1.1.6", "S"),
        new SnmpOidRecord("reconnectionCount", "1.3.6.1.4.1.21067.5.102.2.1.1.5", "C"),
        new SnmpOidRecord("peerStatus", "1.3.6.1.4.1.21067.5.102.2.1.1.4", "I"),
        new SnmpOidRecord("peerLocalIpAddress", "1.3.6.1.4.1.21067.5.102.2.1.1.3", "S"),
        new SnmpOidRecord("peerRemoteIpAddress", "1.3.6.1.4.1.21067.5.102.2.1.1.2", "S"),
        new SnmpOidRecord("peerIpAddressIndex", "1.3.6.1.4.1.21067.5.102.2.1.1.1", "C64"),
        new SnmpOidRecord("peerIpAddrCompsIndexValue", "1.3.6.1.4.1.21067.5.102.2.1.1.7", "S"),
        new SnmpOidRecord("peerInfoTable", "1.3.6.1.4.1.21067.5.102.1", "TA"),
        new SnmpOidRecord("peerInfoEntry", "1.3.6.1.4.1.21067.5.102.1.1", "EN"),
        new SnmpOidRecord("peerIndexValue", "1.3.6.1.4.1.21067.5.102.1.1.6", "S"),
        new SnmpOidRecord("connectionInitByPeer", "1.3.6.1.4.1.21067.5.102.1.1.5", "S"),
        new SnmpOidRecord("peerSecurity", "1.3.6.1.4.1.21067.5.102.1.1.4", "I"),
        new SnmpOidRecord("peerTransportProtocol", "1.3.6.1.4.1.21067.5.102.1.1.3", "S"),
        new SnmpOidRecord("peerIdentity", "1.3.6.1.4.1.21067.5.102.1.1.2", "S"),
        new SnmpOidRecord("peerIndex", "1.3.6.1.4.1.21067.5.102.1.1.1", "G"),
        new SnmpOidRecord("stackRealm", "1.3.6.1.4.1.21067.5.3", "S"),
        new SnmpOidRecord("stackURI", "1.3.6.1.4.1.21067.5.2", "S"),
        new SnmpOidRecord("appStatisticsTable", "1.3.6.1.4.1.21067.5.101.1", "TA"),
        new SnmpOidRecord("applicationStatisticsEntry", "1.3.6.1.4.1.21067.5.101.1.1", "EN"),
        new SnmpOidRecord("appStatsAnswerDr", "1.3.6.1.4.1.21067.5.101.1.1.9", "C"),
        new SnmpOidRecord("appStatsRequestDr", "1.3.6.1.4.1.21067.5.101.1.1.8", "C"),
        new SnmpOidRecord("applicationName", "1.3.6.1.4.1.21067.5.101.1.1.16", "S"),
        new SnmpOidRecord("appStatsRequestTimeOut", "1.3.6.1.4.1.21067.5.101.1.1.7", "C"),
        new SnmpOidRecord("applicationIDIndexValue", "1.3.6.1.4.1.21067.5.101.1.1.15", "S"),
        new SnmpOidRecord("appStatsRequestRetransmitted", "1.3.6.1.4.1.21067.5.101.1.1.6", "C"),
        new SnmpOidRecord("appStatsRequestPn", "1.3.6.1.4.1.21067.5.101.1.1.14", "C"),
        new SnmpOidRecord("appStatsAnswerRx", "1.3.6.1.4.1.21067.5.101.1.1.5", "C"),
        new SnmpOidRecord("appStatsMalformedPacketRx", "1.3.6.1.4.1.21067.5.101.1.1.13", "C"),
        new SnmpOidRecord("appStatsRequestTx", "1.3.6.1.4.1.21067.5.101.1.1.4", "C"),
        new SnmpOidRecord("appStatsAnswerTx", "1.3.6.1.4.1.21067.5.101.1.1.3", "C"),
        new SnmpOidRecord("appStatsDuplicateEtEAnswer", "1.3.6.1.4.1.21067.5.101.1.1.12", "C"),
        new SnmpOidRecord("appStatsDuplicateRequest", "1.3.6.1.4.1.21067.5.101.1.1.11", "C"),
        new SnmpOidRecord("appStatsRequestRx", "1.3.6.1.4.1.21067.5.101.1.1.2", "C"),
        new SnmpOidRecord("applicationID", "1.3.6.1.4.1.21067.5.101.1.1.1", "G"),
        new SnmpOidRecord("appStatsUnknownHbHAnswerDropped", "1.3.6.1.4.1.21067.5.101.1.1.10", "C"),
        new SnmpOidRecord("stackIdentity", "1.3.6.1.4.1.21067.5.1", "S"),
        new SnmpOidRecord("totalDuplicateRequest", "1.3.6.1.4.1.21067.5.100.9", "C"),
        new SnmpOidRecord("totalUnknownHbHAnswerDropped", "1.3.6.1.4.1.21067.5.100.8", "C"),
        new SnmpOidRecord("totalRequestRetransmitted", "1.3.6.1.4.1.21067.5.100.7", "C"),
        new SnmpOidRecord("totalRequestDr", "1.3.6.1.4.1.21067.5.100.6", "C"),
        new SnmpOidRecord("totalAnswerDr", "1.3.6.1.4.1.21067.5.100.5", "C"),
        new SnmpOidRecord("totalRequestPn", "1.3.6.1.4.1.21067.5.100.13", "C"),
        new SnmpOidRecord("totalAnswerTx", "1.3.6.1.4.1.21067.5.100.4", "C"),
        new SnmpOidRecord("totalRequestTimeOut", "1.3.6.1.4.1.21067.5.100.12", "C"),
        new SnmpOidRecord("totalAnswerRx", "1.3.6.1.4.1.21067.5.100.3", "C"),
        new SnmpOidRecord("totalMalformedPacketRx", "1.3.6.1.4.1.21067.5.100.11", "C"),
        new SnmpOidRecord("totalRequestTx", "1.3.6.1.4.1.21067.5.100.2", "C"),
        new SnmpOidRecord("totalDuplicateEtEAnswer", "1.3.6.1.4.1.21067.5.100.10", "C"),
        new SnmpOidRecord("totalRequestRx", "1.3.6.1.4.1.21067.5.100.1", "C")    };
}