package com.elitecore.diameterapi.mibs.base.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-BASE-PROTOCOL-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "DbpPeerIpAddrEntry" MBean.
 */
public interface DbpPeerIpAddrEntryMBean {

    /**
     * Getter for the "DbpPeerIpAddress" variable.
     */
    public Byte[] getDbpPeerIpAddress() throws SnmpStatusException;

    /**
     * Getter for the "DbpPeerIpAddressType" variable.
     */
    public EnumDbpPeerIpAddressType getDbpPeerIpAddressType() throws SnmpStatusException;

    /**
     * Getter for the "DbpPeerIpAddressIndex" variable.
     */
    public Long getDbpPeerIpAddressIndex() throws SnmpStatusException;

    /**
     * Getter for the "DbpPeerIndex" variable.
     */
    public Long getDbpPeerIndex() throws SnmpStatusException;

}
