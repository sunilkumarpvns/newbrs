package com.elitecore.diameterapi.mibs.base.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-BASE-PROTOCOL-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "DbpPeerVendorEntry" MBean.
 */
public interface DbpPeerVendorEntryMBean {

    /**
     * Getter for the "DbpPeerVendorRowStatus" variable.
     */
    public EnumDbpPeerVendorRowStatus getDbpPeerVendorRowStatus() throws SnmpStatusException;

    /**
     * Setter for the "DbpPeerVendorRowStatus" variable.
     * NB: There is no check method generated for RowStatus.
     *      Override checkRowStatusChange on SnmpMibTable if needed.
     */
    public void setDbpPeerVendorRowStatus(EnumDbpPeerVendorRowStatus x) throws SnmpStatusException;

    /**
     * Getter for the "DbpPeerVendorStorageType" variable.
     */
    public EnumDbpPeerVendorStorageType getDbpPeerVendorStorageType() throws SnmpStatusException;

    /**
     * Setter for the "DbpPeerVendorStorageType" variable.
     */
    public void setDbpPeerVendorStorageType(EnumDbpPeerVendorStorageType x) throws SnmpStatusException;

    /**
     * Checker for the "DbpPeerVendorStorageType" variable.
     */
    public void checkDbpPeerVendorStorageType(EnumDbpPeerVendorStorageType x) throws SnmpStatusException;

    /**
     * Getter for the "DbpPeerVendorId" variable.
     */
    public EnumDbpPeerVendorId getDbpPeerVendorId() throws SnmpStatusException;

    /**
     * Setter for the "DbpPeerVendorId" variable.
     */
    public void setDbpPeerVendorId(EnumDbpPeerVendorId x) throws SnmpStatusException;

    /**
     * Checker for the "DbpPeerVendorId" variable.
     */
    public void checkDbpPeerVendorId(EnumDbpPeerVendorId x) throws SnmpStatusException;

    /**
     * Getter for the "DbpPeerVendorIndex" variable.
     */
    public Long getDbpPeerVendorIndex() throws SnmpStatusException;

    /**
     * Getter for the "DbpPeerIndex" variable.
     */
    public Long getDbpPeerIndex() throws SnmpStatusException;

}
