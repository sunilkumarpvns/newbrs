package com.elitecore.aaa.radius.sessionx.snmp.remotesm.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling REMOTE-SESSION-MANAGER-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "RemoteSessionManagerEntry" MBean.
 */
public interface RemoteSessionManagerEntryMBean {

    /**
     * Getter for the "RemoteSMAccessRequestRx" variable.
     */
    public Long getRemoteSMAccessRequestRx() throws SnmpStatusException;

    /**
     * Getter for the "RemoteSMTimeoutRequest" variable.
     */
    public Long getRemoteSMTimeoutRequest() throws SnmpStatusException;

    /**
     * Getter for the "RemoteSMUnknownRequestType" variable.
     */
    public Long getRemoteSMUnknownRequestType() throws SnmpStatusException;

    /**
     * Getter for the "RemoteSMAcctStopRequestRx" variable.
     */
    public Long getRemoteSMAcctStopRequestRx() throws SnmpStatusException;

    /**
     * Getter for the "RemoteSMAcctUpdateRequestRx" variable.
     */
    public Long getRemoteSMAcctUpdateRequestRx() throws SnmpStatusException;

    /**
     * Getter for the "RemoteSMRequestDropped" variable.
     */
    public Long getRemoteSMRequestDropped() throws SnmpStatusException;

    /**
     * Getter for the "RemoteSMAcctStartRequestRx" variable.
     */
    public Long getRemoteSMAcctStartRequestRx() throws SnmpStatusException;

    /**
     * Getter for the "RemoteSMResponsesTx" variable.
     */
    public Long getRemoteSMResponsesTx() throws SnmpStatusException;

    /**
     * Getter for the "RemoteSMAcctResponseTx" variable.
     */
    public Long getRemoteSMAcctResponseTx() throws SnmpStatusException;

    /**
     * Getter for the "RemoteSMRequestRx" variable.
     */
    public Long getRemoteSMRequestRx() throws SnmpStatusException;

    /**
     * Getter for the "RemoteSMIPAddress" variable.
     */
    public String getRemoteSMIPAddress() throws SnmpStatusException;

    /**
     * Getter for the "RemoteSMAcctRequestRx" variable.
     */
    public Long getRemoteSMAcctRequestRx() throws SnmpStatusException;

    /**
     * Getter for the "RemoteSMName" variable.
     */
    public String getRemoteSMName() throws SnmpStatusException;

    /**
     * Getter for the "RemoteSMAccessRejectTx" variable.
     */
    public Long getRemoteSMAccessRejectTx() throws SnmpStatusException;

    /**
     * Getter for the "RemoteSMAccessAcceptTx" variable.
     */
    public Long getRemoteSMAccessAcceptTx() throws SnmpStatusException;

    /**
     * Getter for the "ClientIndex" variable.
     */
    public Integer getClientIndex() throws SnmpStatusException;

}
