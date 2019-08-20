package com.elitecore.aaa.rm.service.chargingservice.snmp.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling CHARGING-SERVICE-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "ChargingServMIBObjects" MBean.
 */
public interface ChargingServMIBObjectsMBean {

    /**
     * Access the "ChargingClientStatsTable" variable.
     */
    public TableChargingClientStatsTable accessChargingClientStatsTable() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServTotalAcctUpdateRequest" variable.
     */
    public Long getChargingServTotalAcctUpdateRequest() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServTotalAcctStopRequest" variable.
     */
    public Long getChargingServTotalAcctStopRequest() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServTotalAcctStartRequest" variable.
     */
    public Long getChargingServTotalAcctStartRequest() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServTotalAcctResponse" variable.
     */
    public Long getChargingServTotalAcctResponse() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServTotalAcctRequest" variable.
     */
    public Long getChargingServTotalAcctRequest() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServTotalAccessReject" variable.
     */
    public Long getChargingServTotalAccessReject() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServTotalAccessAccept" variable.
     */
    public Long getChargingServTotalAccessAccept() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServTotalAccessRequest" variable.
     */
    public Long getChargingServTotalAccessRequest() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServTotalUnknownTypes" variable.
     */
    public Long getChargingServTotalUnknownTypes() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServTotalPacketsDropped" variable.
     */
    public Long getChargingServTotalPacketsDropped() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServTotalBadAuthenticators" variable.
     */
    public Long getChargingServTotalBadAuthenticators() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServTotalMalformedRequests" variable.
     */
    public Long getChargingServTotalMalformedRequests() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServTotalDupRequests" variable.
     */
    public Long getChargingServTotalDupRequests() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServTotalInvalidRequests" variable.
     */
    public Long getChargingServTotalInvalidRequests() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServTotalResponses" variable.
     */
    public Long getChargingServTotalResponses() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServTotalRequests" variable.
     */
    public Long getChargingServTotalRequests() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServResetTime" variable.
     */
    public Long getChargingServResetTime() throws SnmpStatusException;

    /**
     * Getter for the "ChargingServiceReset" variable.
     */
    public EnumChargingServiceReset getChargingServiceReset() throws SnmpStatusException;

    /**
     * Setter for the "ChargingServiceReset" variable.
     */
    public void setChargingServiceReset(EnumChargingServiceReset x) throws SnmpStatusException;

    /**
     * Checker for the "ChargingServiceReset" variable.
     */
    public void checkChargingServiceReset(EnumChargingServiceReset x) throws SnmpStatusException;

    /**
     * Getter for the "ChargingServUpTime" variable.
     */
    public Long getChargingServUpTime() throws SnmpStatusException;

}
