package com.elitecore.diameterapi.mibs.custom.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-STACK-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "CommandCodeStatisticsEntry" MBean.
 */
public interface CommandCodeStatisticsEntryMBean {

    /**
     * Getter for the "CcwCompstIndexValue" variable.
     */
    public String getCcwCompstIndexValue() throws SnmpStatusException;

    /**
     * Getter for the "CcwApplicationName" variable.
     */
    public String getCcwApplicationName() throws SnmpStatusException;

    /**
     * Getter for the "CcwPeerIdentity" variable.
     */
    public String getCcwPeerIdentity() throws SnmpStatusException;

    /**
     * Getter for the "CcwRequestPn" variable.
     */
    public Long getCcwRequestPn() throws SnmpStatusException;

    /**
     * Getter for the "CcwMalformedPacketRx" variable.
     */
    public Long getCcwMalformedPacketRx() throws SnmpStatusException;

    /**
     * Getter for the "CcwDuplicateEtEAnswer" variable.
     */
    public Long getCcwDuplicateEtEAnswer() throws SnmpStatusException;

    /**
     * Getter for the "CcwDuplicateRequest" variable.
     */
    public Long getCcwDuplicateRequest() throws SnmpStatusException;

    /**
     * Getter for the "CcwUnknownHbHAnswerDropped" variable.
     */
    public Long getCcwUnknownHbHAnswerDropped() throws SnmpStatusException;

    /**
     * Getter for the "CcwAnswerDr" variable.
     */
    public Long getCcwAnswerDr() throws SnmpStatusException;

    /**
     * Getter for the "CcwRequestDr" variable.
     */
    public Long getCcwRequestDr() throws SnmpStatusException;

    /**
     * Getter for the "CcwRequestTimeOut" variable.
     */
    public Long getCcwRequestTimeOut() throws SnmpStatusException;

    /**
     * Getter for the "CcwRequestRetransmitted" variable.
     */
    public Long getCcwRequestRetransmitted() throws SnmpStatusException;

    /**
     * Getter for the "CcwAnswerRx" variable.
     */
    public Long getCcwAnswerRx() throws SnmpStatusException;

    /**
     * Getter for the "CcwRequestTx" variable.
     */
    public Long getCcwRequestTx() throws SnmpStatusException;

    /**
     * Getter for the "CcwAnswerTx" variable.
     */
    public Long getCcwAnswerTx() throws SnmpStatusException;

    /**
     * Getter for the "CcwRequestRx" variable.
     */
    public Long getCcwRequestRx() throws SnmpStatusException;

    /**
     * Getter for the "CommandCodeName" variable.
     */
    public String getCommandCodeName() throws SnmpStatusException;

    /**
     * Getter for the "CommandCode" variable.
     */
    public Long getCommandCode() throws SnmpStatusException;

    /**
     * Getter for the "PeerIndex" variable.
     */
    public Long getPeerIndex() throws SnmpStatusException;

    /**
     * Getter for the "ApplicationID" variable.
     */
    public Long getApplicationID() throws SnmpStatusException;

}
