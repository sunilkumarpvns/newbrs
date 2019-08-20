package com.elitecore.aaa.mibs.radius.dynauth.server.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RADIUS-DYNAUTH-SERVER-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "RadiusDynAuthServerMIBObjects" MBean.
 */
public interface RadiusDynAuthServerMIBObjectsMBean {

    /**
     * Access the "RadiusDynAuthClientTable" variable.
     */
    public TableRadiusDynAuthClientTable accessRadiusDynAuthClientTable() throws SnmpStatusException;

}
