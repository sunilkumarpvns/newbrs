package com.elitecore.core.imdg.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling IMDG-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "AlertInfoGroup" MBean.
 */
public interface AlertInfoGroupMBean {

    /**
     * Getter for the "MigrationStatus" variable.
     */
    public EnumMigrationStatus getMigrationStatus() throws SnmpStatusException;

    /**
     * Getter for the "MemberStatus" variable.
     */
    public EnumMemberStatus getMemberStatus() throws SnmpStatusException;

    /**
     * Getter for the "Member" variable.
     */
    public String getMember() throws SnmpStatusException;

    /**
     * Getter for the "InstanceStatus" variable.
     */
    public EnumInstanceStatus getInstanceStatus() throws SnmpStatusException;

}
