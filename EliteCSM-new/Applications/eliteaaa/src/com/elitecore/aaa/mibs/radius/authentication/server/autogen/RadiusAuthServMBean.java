package com.elitecore.aaa.mibs.radius.authentication.server.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RADIUS-AUTH-SERVER-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "RadiusAuthServ" MBean.
 */
public interface RadiusAuthServMBean {

    /**
     * Getter for the "RadiusAuthServTotalAccessRejects" variable.
     */
    public Long getRadiusAuthServTotalAccessRejects() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAuthServTotalAccessAccepts" variable.
     */
    public Long getRadiusAuthServTotalAccessAccepts() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAuthServTotalDupAccessRequests" variable.
     */
    public Long getRadiusAuthServTotalDupAccessRequests() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAuthServTotalInvalidRequests" variable.
     */
    public Long getRadiusAuthServTotalInvalidRequests() throws SnmpStatusException;

    /**
     * Access the "RadiusAuthClientTable" variable.
     */
    public TableRadiusAuthClientTable accessRadiusAuthClientTable() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAuthServTotalUnknownTypes" variable.
     */
    public Long getRadiusAuthServTotalUnknownTypes() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAuthServTotalAccessRequests" variable.
     */
    public Long getRadiusAuthServTotalAccessRequests() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAuthServConfigReset" variable.
     */
    public EnumRadiusAuthServConfigReset getRadiusAuthServConfigReset() throws SnmpStatusException;

    /**
     * Setter for the "RadiusAuthServConfigReset" variable.
     */
    public void setRadiusAuthServConfigReset(EnumRadiusAuthServConfigReset x) throws SnmpStatusException;

    /**
     * Checker for the "RadiusAuthServConfigReset" variable.
     */
    public void checkRadiusAuthServConfigReset(EnumRadiusAuthServConfigReset x) throws SnmpStatusException;

    /**
     * Getter for the "RadiusAuthServTotalPacketsDropped" variable.
     */
    public Long getRadiusAuthServTotalPacketsDropped() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAuthServResetTime" variable.
     */
    public Long getRadiusAuthServResetTime() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAuthServTotalBadAuthenticators" variable.
     */
    public Long getRadiusAuthServTotalBadAuthenticators() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAuthServTotalMalformedAccessRequests" variable.
     */
    public Long getRadiusAuthServTotalMalformedAccessRequests() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAuthServUpTime" variable.
     */
    public Long getRadiusAuthServUpTime() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAuthServIdent" variable.
     */
    public String getRadiusAuthServIdent() throws SnmpStatusException;

    /**
     * Getter for the "RadiusAuthServTotalAccessChallenges" variable.
     */
    public Long getRadiusAuthServTotalAccessChallenges() throws SnmpStatusException;

}
