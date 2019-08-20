package com.elitecore.aaa.mibs.radius.dynauth.client.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RADIUS-DYNAUTH-CLIENT-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "RadiusDynAuthClientScalars" MBean.
 */
public interface RadiusDynAuthClientScalarsMBean {

    /**
     * Getter for the "RadiusDynAuthClientCoAInvalidServerAddresses" variable.
     */
    public Long getRadiusDynAuthClientCoAInvalidServerAddresses() throws SnmpStatusException;

    /**
     * Getter for the "RadiusDynAuthClientDisconInvalidServerAddresses" variable.
     */
    public Long getRadiusDynAuthClientDisconInvalidServerAddresses() throws SnmpStatusException;

}
