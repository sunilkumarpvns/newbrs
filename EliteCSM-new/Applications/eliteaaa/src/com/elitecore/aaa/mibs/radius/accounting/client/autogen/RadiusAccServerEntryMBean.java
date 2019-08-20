package com.elitecore.aaa.mibs.radius.accounting.client.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RADIUS-ACC-CLIENT-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "RadiusAccServerEntry" MBean.
 */
public interface RadiusAccServerEntryMBean {

    /**
     * Getter for the "RadiusAccClientBadAuthenticators" variable.
     */
    public Long getRadiusAccClientBadAuthenticators() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccClientMalformedResponses" variable.
     */
    public Long getRadiusAccClientMalformedResponses() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccClientResponses" variable.
     */
    public Long getRadiusAccClientResponses() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccClientRetransmissions" variable.
     */
    public Long getRadiusAccClientRetransmissions() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccClientRequests" variable.
     */
    public Long getRadiusAccClientRequests() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccClientPacketsDropped" variable.
     */
    public Long getRadiusAccClientPacketsDropped() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccClientRoundTripTime" variable.
     */
    public Long getRadiusAccClientRoundTripTime() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccClientServerPortNumber" variable.
     */
    public Integer getRadiusAccClientServerPortNumber() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccClientUnknownTypes" variable.
     */
    public Long getRadiusAccClientUnknownTypes() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccClientTimeouts" variable.
     */
    public Long getRadiusAccClientTimeouts() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccServerAddress" variable.
     */
    public String getRadiusAccServerAddress() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccServerIndex" variable.
     */
    public Integer getRadiusAccServerIndex() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAccClientPendingRequests" variable.
     */
    public Long getRadiusAccClientPendingRequests() throws SnmpStatusException;

}