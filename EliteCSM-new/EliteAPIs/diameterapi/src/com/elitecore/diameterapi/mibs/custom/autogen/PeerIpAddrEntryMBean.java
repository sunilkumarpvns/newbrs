package com.elitecore.diameterapi.mibs.custom.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-STACK-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "PeerIpAddrEntry" MBean.
 */
public interface PeerIpAddrEntryMBean {

    /**
     * Getter for the "PeerIpAddrPeerIdentity" variable.
     */
    public String getPeerIpAddrPeerIdentity() throws SnmpStatusException;

    /**
     * Getter for the "ReconnectionCount" variable.
     */
    public Long getReconnectionCount() throws SnmpStatusException;

    /**
     * Getter for the "PeerStatus" variable.
     */
    public EnumPeerStatus getPeerStatus() throws SnmpStatusException;

    /**
     * Getter for the "PeerLocalIpAddress" variable.
     */
    public String getPeerLocalIpAddress() throws SnmpStatusException;

    /**
     * Getter for the "PeerRemoteIpAddress" variable.
     */
    public String getPeerRemoteIpAddress() throws SnmpStatusException;

    /**
     * Getter for the "PeerIpAddressIndex" variable.
     */
    public Long getPeerIpAddressIndex() throws SnmpStatusException;

    /**
     * Getter for the "PeerIpAddrCompsIndexValue" variable.
     */
    public String getPeerIpAddrCompsIndexValue() throws SnmpStatusException;

    /**
     * Getter for the "PeerIndex" variable.
     */
    public Long getPeerIndex() throws SnmpStatusException;

}