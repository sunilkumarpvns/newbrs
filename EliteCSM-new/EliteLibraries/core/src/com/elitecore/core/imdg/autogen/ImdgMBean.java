package com.elitecore.core.imdg.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling IMDG-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "Imdg" MBean.
 */
public interface ImdgMBean {

    /**
     * Getter for the "OwnState" variable.
     */
    public String getOwnState() throws SnmpStatusException;

    /**
     * Getter for the "OwnPort" variable.
     */
    public Integer getOwnPort() throws SnmpStatusException;

    /**
     * Getter for the "OwnIpAddress" variable.
     */
    public String getOwnIpAddress() throws SnmpStatusException;

    /**
     * Getter for the "ClusterState" variable.
     */
    public String getClusterState() throws SnmpStatusException;

    /**
     * Getter for the "ClusterMembersCount" variable.
     */
    public Long getClusterMembersCount() throws SnmpStatusException;

    /**
     * Getter for the "ClusterName" variable.
     */
    public String getClusterName() throws SnmpStatusException;

    /**
     * Getter for the "ClusterVersion" variable.
     */
    public String getClusterVersion() throws SnmpStatusException;

}
