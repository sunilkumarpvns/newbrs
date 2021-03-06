package com.elitecore.aaa.mibs.rm.charging.client.snmp.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling CHARGING-SERVICE-CLIENT-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "ChargingServerEntry" MBean.
 */
public interface ChargingServerEntryMBean {

    /**
     * Getter for the "ChargingRequestRetransmission" variable.
     */
    public Long getChargingRequestRetransmission() throws SnmpStatusException;

    /**
     * Getter for the "ChargingRequestTimeout" variable.
     */
    public Long getChargingRequestTimeout() throws SnmpStatusException;

    /**
     * Getter for the "ChargingAcctUpdateRequest" variable.
     */
    public Long getChargingAcctUpdateRequest() throws SnmpStatusException;

    /**
     * Getter for the "ChargingUnknownTypes" variable.
     */
    public Long getChargingUnknownTypes() throws SnmpStatusException;

    /**
     * Getter for the "ChargingAcctStopRequest" variable.
     */
    public Long getChargingAcctStopRequest() throws SnmpStatusException;

    /**
     * Getter for the "ChargingAcctStartRequest" variable.
     */
    public Long getChargingAcctStartRequest() throws SnmpStatusException;

    /**
     * Getter for the "ChargingPacketsDropped" variable.
     */
    public Long getChargingPacketsDropped() throws SnmpStatusException;

    /**
     * Getter for the "ChargingResponses" variable.
     */
    public Long getChargingResponses() throws SnmpStatusException;

    /**
     * Getter for the "ChargingAcctResponse" variable.
     */
    public Long getChargingAcctResponse() throws SnmpStatusException;

    /**
     * Getter for the "ChargingAcctRequest" variable.
     */
    public Long getChargingAcctRequest() throws SnmpStatusException;

    /**
     * Getter for the "ChargingRequests" variable.
     */
    public Long getChargingRequests() throws SnmpStatusException;

    /**
     * Getter for the "ChargingAccessReject" variable.
     */
    public Long getChargingAccessReject() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServerAddress" variable.
     */
    public String getChargingServerAddress() throws SnmpStatusException;

    /**
     * Getter for the "ChargingAccessAccept" variable.
     */
    public Long getChargingAccessAccept() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServerName" variable.
     */
    public String getChargingServerName() throws SnmpStatusException;

    /**
     * Getter for the "ChargingAccessRequest" variable.
     */
    public Long getChargingAccessRequest() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServerIndex" variable.
     */
    public Integer getChargingServerIndex() throws SnmpStatusException;

}
