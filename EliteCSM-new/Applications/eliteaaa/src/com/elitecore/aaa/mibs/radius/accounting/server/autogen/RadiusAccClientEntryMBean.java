package com.elitecore.aaa.mibs.radius.accounting.server.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RADIUS-ACC-SERVER-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "RadiusAccClientEntry" MBean.
 */
public interface RadiusAccClientEntryMBean {

    /**
     * Getter for the "RadiusAccServMalformedRequests" variable.
     */
    public Long getRadiusAccServMalformedRequests() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccServBadAuthenticators" variable.
     */
    public Long getRadiusAccServBadAuthenticators() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccServResponses" variable.
     */
    public Long getRadiusAccServResponses() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccServDupRequests" variable.
     */
    public Long getRadiusAccServDupRequests() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccServRequests" variable.
     */
    public Long getRadiusAccServRequests() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccServPacketsDropped" variable.
     */
    public Long getRadiusAccServPacketsDropped() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccClientID" variable.
     */
    public String getRadiusAccClientID() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccClientAddress" variable.
     */
    public String getRadiusAccClientAddress() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccServUnknownTypes" variable.
     */
    public Long getRadiusAccServUnknownTypes() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccServNoRecords" variable.
     */
    public Long getRadiusAccServNoRecords() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccClientIndex" variable.
     */
    public Integer getRadiusAccClientIndex() throws SnmpStatusException;

}