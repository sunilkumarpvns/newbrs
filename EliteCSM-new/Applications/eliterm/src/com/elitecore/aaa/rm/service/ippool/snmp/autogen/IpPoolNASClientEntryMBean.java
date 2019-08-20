package com.elitecore.aaa.rm.service.ippool.snmp.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling IP-POOL-SERVICE-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "IpPoolNASClientEntry" MBean.
 */
public interface IpPoolNASClientEntryMBean {

    /**
     * Getter for the "NasIPAddressOfferResponse" variable.
     */
    public Long getNasIPAddressOfferResponse() throws SnmpStatusException;

    /**
     * Getter for the "NasIPAddressDiscoverRequest" variable.
     */
    public Long getNasIPAddressDiscoverRequest() throws SnmpStatusException;

    /**
     * Getter for the "NasIPAddressInvalidRequest" variable.
     */
    public Long getNasIPAddressInvalidRequest() throws SnmpStatusException;

    /**
     * Getter for the "NasIPAddressUnknownPacket" variable.
     */
    public Long getNasIPAddressUnknownPacket() throws SnmpStatusException;

    /**
     * Getter for the "NasIPAddressRequestDropped" variable.
     */
    public Long getNasIPAddressRequestDropped() throws SnmpStatusException;

    /**
     * Getter for the "NasIPAddressUpdateRequest" variable.
     */
    public Long getNasIPAddressUpdateRequest() throws SnmpStatusException;

    /**
     * Getter for the "NasIPAddressResponses" variable.
     */
    public Long getNasIPAddressResponses() throws SnmpStatusException;

    /**
     * Getter for the "NasIPAddressRequest" variable.
     */
    public Long getNasIPAddressRequest() throws SnmpStatusException;

    /**
     * Getter for the "NasIPAddressReleaseRequest" variable.
     */
    public Long getNasIPAddressReleaseRequest() throws SnmpStatusException;

    /**
     * Getter for the "NasID" variable.
     */
    public String getNasID() throws SnmpStatusException;

    /**
     * Getter for the "NasIPAddressAllocationRequest" variable.
     */
    public Long getNasIPAddressAllocationRequest() throws SnmpStatusException;

    /**
     * Getter for the "NasIPAddressDeclineResponse" variable.
     */
    public Long getNasIPAddressDeclineResponse() throws SnmpStatusException;

    /**
     * Getter for the "IpPoolNASClientIndex" variable.
     */
    public Integer getIpPoolNASClientIndex() throws SnmpStatusException;

}
