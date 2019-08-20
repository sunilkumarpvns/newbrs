package com.elitecore.aaa.rm.service.ippool.snmp.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling IP-POOL-SERVICE-MIB.
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
 * The class contains metadata definitions for "IP-POOL-SERVICE-MIB".
 * Call SnmpOid.setSnmpOidTable(new IP_POOL_SERVICE_MIBOidTable()) to load the metadata in the SnmpOidTable.
 */
public class IP_POOL_SERVICE_MIBOidTable extends SnmpOidTableSupport implements Serializable {

    /**
     * Default constructor. Initialize the Mib tree.
     */
    public IP_POOL_SERVICE_MIBOidTable() {
        super("IP_POOL_SERVICE_MIB");
        loadMib(varList);
    }

    static SnmpOidRecord varList [] = {
        new SnmpOidRecord("ipPoolServTotalInvalidRequest", "1.3.6.1.4.1.21067.1.7.1.9", "C"),
        new SnmpOidRecord("ipPoolServTotalUnknownPacket", "1.3.6.1.4.1.21067.1.7.1.8", "C"),
        new SnmpOidRecord("ipPoolServTotalDuplicateRequest", "1.3.6.1.4.1.21067.1.7.1.7", "C"),
        new SnmpOidRecord("ipPoolServTotalRequestDropped", "1.3.6.1.4.1.21067.1.7.1.6", "C"),
        new SnmpOidRecord("ipPoolServTotalUpdateRequest", "1.3.6.1.4.1.21067.1.7.1.15", "C"),
        new SnmpOidRecord("ipPoolServTotalReleaseRequest", "1.3.6.1.4.1.21067.1.7.1.14", "C"),
        new SnmpOidRecord("ipPoolServTotalResponses", "1.3.6.1.4.1.21067.1.7.1.5", "C"),
        new SnmpOidRecord("ipPoolServTotalAllocationRequest", "1.3.6.1.4.1.21067.1.7.1.13", "C"),
        new SnmpOidRecord("ipPoolServTotalRequest", "1.3.6.1.4.1.21067.1.7.1.4", "C"),
        new SnmpOidRecord("ipPoolServiceResetTime", "1.3.6.1.4.1.21067.1.7.1.3", "T"),
        new SnmpOidRecord("ipPoolServTotalDeclineResponse", "1.3.6.1.4.1.21067.1.7.1.12", "C"),
        new SnmpOidRecord("ipPoolNASClientStatisticsTable", "1.3.6.1.4.1.21067.1.7.1.102", "TA"),
        new SnmpOidRecord("ipPoolNASClientEntry", "1.3.6.1.4.1.21067.1.7.1.102.1", "EN"),
        new SnmpOidRecord("nasIPAddressOfferResponse", "1.3.6.1.4.1.21067.1.7.1.102.1.9", "C"),
        new SnmpOidRecord("nasIPAddressDiscoverRequest", "1.3.6.1.4.1.21067.1.7.1.102.1.8", "C"),
        new SnmpOidRecord("nasIPAddressInvalidRequest", "1.3.6.1.4.1.21067.1.7.1.102.1.7", "C"),
        new SnmpOidRecord("nasIPAddressUnknownPacket", "1.3.6.1.4.1.21067.1.7.1.102.1.6", "C"),
        new SnmpOidRecord("nasIPAddressRequestDropped", "1.3.6.1.4.1.21067.1.7.1.102.1.5", "C"),
        new SnmpOidRecord("nasIPAddressUpdateRequest", "1.3.6.1.4.1.21067.1.7.1.102.1.13", "C"),
        new SnmpOidRecord("nasIPAddressResponses", "1.3.6.1.4.1.21067.1.7.1.102.1.4", "C"),
        new SnmpOidRecord("nasIPAddressRequest", "1.3.6.1.4.1.21067.1.7.1.102.1.3", "C"),
        new SnmpOidRecord("nasIPAddressReleaseRequest", "1.3.6.1.4.1.21067.1.7.1.102.1.12", "C"),
        new SnmpOidRecord("nasID", "1.3.6.1.4.1.21067.1.7.1.102.1.2", "S"),
        new SnmpOidRecord("nasIPAddressAllocationRequest", "1.3.6.1.4.1.21067.1.7.1.102.1.11", "C"),
        new SnmpOidRecord("nasIPAddressDeclineResponse", "1.3.6.1.4.1.21067.1.7.1.102.1.10", "C"),
        new SnmpOidRecord("ipPoolNASClientIndex", "1.3.6.1.4.1.21067.1.7.1.102.1.1", "I"),
        new SnmpOidRecord("ipPoolServiceReset", "1.3.6.1.4.1.21067.1.7.1.2", "I"),
        new SnmpOidRecord("ipPoolAAAClientStatisticsTable", "1.3.6.1.4.1.21067.1.7.1.101", "TA"),
        new SnmpOidRecord("ipPoolAAAClientEntry", "1.3.6.1.4.1.21067.1.7.1.101.1", "EN"),
        new SnmpOidRecord("aaaIPAddressInvalidRequest", "1.3.6.1.4.1.21067.1.7.1.101.1.9", "C"),
        new SnmpOidRecord("aaaIPAddressUnknownPacket", "1.3.6.1.4.1.21067.1.7.1.101.1.8", "C"),
        new SnmpOidRecord("aaaIPAddressDuplicateRequest", "1.3.6.1.4.1.21067.1.7.1.101.1.7", "C"),
        new SnmpOidRecord("aaaIPAddressRequestDropped", "1.3.6.1.4.1.21067.1.7.1.101.1.6", "C"),
        new SnmpOidRecord("aaaIPAddressUpdateRequest", "1.3.6.1.4.1.21067.1.7.1.101.1.15", "C"),
        new SnmpOidRecord("aaaIPAddressResponses", "1.3.6.1.4.1.21067.1.7.1.101.1.5", "C"),
        new SnmpOidRecord("aaaIPAddressReleaseRequest", "1.3.6.1.4.1.21067.1.7.1.101.1.14", "C"),
        new SnmpOidRecord("aaaIPAddressAllocationRequest", "1.3.6.1.4.1.21067.1.7.1.101.1.13", "C"),
        new SnmpOidRecord("aaaIPAddressRequest", "1.3.6.1.4.1.21067.1.7.1.101.1.4", "C"),
        new SnmpOidRecord("aaaIPAddressDeclineResponse", "1.3.6.1.4.1.21067.1.7.1.101.1.12", "C"),
        new SnmpOidRecord("aaaIPAddress", "1.3.6.1.4.1.21067.1.7.1.101.1.3", "IP"),
        new SnmpOidRecord("aaaIPAddressOfferResponse", "1.3.6.1.4.1.21067.1.7.1.101.1.11", "C"),
        new SnmpOidRecord("aaaID", "1.3.6.1.4.1.21067.1.7.1.101.1.2", "S"),
        new SnmpOidRecord("aaaIPAddressDiscoverRequest", "1.3.6.1.4.1.21067.1.7.1.101.1.10", "C"),
        new SnmpOidRecord("ipPoolAAAClientIndex", "1.3.6.1.4.1.21067.1.7.1.101.1.1", "I"),
        new SnmpOidRecord("ipPoolServTotalOfferResponse", "1.3.6.1.4.1.21067.1.7.1.11", "C"),
        new SnmpOidRecord("ipPoolServiceUpTime", "1.3.6.1.4.1.21067.1.7.1.1", "T"),
        new SnmpOidRecord("ipPoolServTotalDiscoverRequest", "1.3.6.1.4.1.21067.1.7.1.10", "C")    };
}