package com.elitecore.diameterapi.mibs.cc.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-CC-APPLICATION-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "DccaPeerVendorEntry" MBean.
 */
public interface DccaPeerVendorEntryMBean {

    /**
     * Getter for the "DccaPeerVendorRowStatus" variable.
     */
    public EnumDccaPeerVendorRowStatus getDccaPeerVendorRowStatus() throws SnmpStatusException;

    /**
     * Setter for the "DccaPeerVendorRowStatus" variable.
     * NB: There is no check method generated for RowStatus.
     *      Override checkRowStatusChange on SnmpMibTable if needed.
     */
    public void setDccaPeerVendorRowStatus(EnumDccaPeerVendorRowStatus x) throws SnmpStatusException;

    /**
     * Getter for the "DccaPeerVendorStorageType" variable.
     */
    public EnumDccaPeerVendorStorageType getDccaPeerVendorStorageType() throws SnmpStatusException;

    /**
     * Setter for the "DccaPeerVendorStorageType" variable.
     */
    public void setDccaPeerVendorStorageType(EnumDccaPeerVendorStorageType x) throws SnmpStatusException;

    /**
     * Checker for the "DccaPeerVendorStorageType" variable.
     */
    public void checkDccaPeerVendorStorageType(EnumDccaPeerVendorStorageType x) throws SnmpStatusException;

    /**
     * Getter for the "DccaPeerVendorId" variable.
     */
    public Long getDccaPeerVendorId() throws SnmpStatusException;

    /**
     * Setter for the "DccaPeerVendorId" variable.
     */
    public void setDccaPeerVendorId(Long x) throws SnmpStatusException;

    /**
     * Checker for the "DccaPeerVendorId" variable.
     */
    public void checkDccaPeerVendorId(Long x) throws SnmpStatusException;

    /**
     * Getter for the "DccaPeerVendorIndex" variable.
     */
    public Long getDccaPeerVendorIndex() throws SnmpStatusException;

    /**
     * Getter for the "DccaPeerIndex" variable.
     */
    public Long getDccaPeerIndex() throws SnmpStatusException;

}
