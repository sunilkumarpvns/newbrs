package com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "NotificationStatistics" MBean.
 */
public interface NotificationStatisticsMBean {

    /**
     * Getter for the "TotalSMSFailuresYesterday" variable.
     */
    public Long getTotalSMSFailuresYesterday() throws SnmpStatusException;

    /**
     * Getter for the "TotalSMSSuccessYesterday" variable.
     */
    public Long getTotalSMSSuccessYesterday() throws SnmpStatusException;

    /**
     * Getter for the "TotalSMSProcessedYesterday" variable.
     */
    public Long getTotalSMSProcessedYesterday() throws SnmpStatusException;

    /**
     * Getter for the "TotalEmailFailuresYesterday" variable.
     */
    public Long getTotalEmailFailuresYesterday() throws SnmpStatusException;

    /**
     * Getter for the "TotalEmailSuccessYesterday" variable.
     */
    public Long getTotalEmailSuccessYesterday() throws SnmpStatusException;

    /**
     * Getter for the "TotalEmailProcessedYesterday" variable.
     */
    public Long getTotalEmailProcessedYesterday() throws SnmpStatusException;

    /**
     * Getter for the "TotalSMSFailures" variable.
     */
    public Long getTotalSMSFailures() throws SnmpStatusException;

    /**
     * Getter for the "TotalNotificationProcessedYesterday" variable.
     */
    public Long getTotalNotificationProcessedYesterday() throws SnmpStatusException;

    /**
     * Getter for the "TotalSMSSuccess" variable.
     */
    public Long getTotalSMSSuccess() throws SnmpStatusException;

    /**
     * Getter for the "TotalSMSProcessed" variable.
     */
    public Long getTotalSMSProcessed() throws SnmpStatusException;

    /**
     * Getter for the "TotalEmailFailures" variable.
     */
    public Long getTotalEmailFailures() throws SnmpStatusException;

    /**
     * Getter for the "TotalSMSFailuresToday" variable.
     */
    public Long getTotalSMSFailuresToday() throws SnmpStatusException;

    /**
     * Getter for the "TotalEmailSuccess" variable.
     */
    public Long getTotalEmailSuccess() throws SnmpStatusException;

    /**
     * Getter for the "TotalSMSSuccessToday" variable.
     */
    public Long getTotalSMSSuccessToday() throws SnmpStatusException;

    /**
     * Getter for the "TotalEmailProcessed" variable.
     */
    public Long getTotalEmailProcessed() throws SnmpStatusException;

    /**
     * Getter for the "TotalNotificationProcessed" variable.
     */
    public Long getTotalNotificationProcessed() throws SnmpStatusException;

    /**
     * Getter for the "TotalSMSProcessedToday" variable.
     */
    public Long getTotalSMSProcessedToday() throws SnmpStatusException;

    /**
     * Getter for the "NotificationStatisticsReset" variable.
     */
    public EnumNotificationStatisticsReset getNotificationStatisticsReset() throws SnmpStatusException;

    /**
     * Setter for the "NotificationStatisticsReset" variable.
     */
    public void setNotificationStatisticsReset(EnumNotificationStatisticsReset x) throws SnmpStatusException;

    /**
     * Checker for the "NotificationStatisticsReset" variable.
     */
    public void checkNotificationStatisticsReset(EnumNotificationStatisticsReset x) throws SnmpStatusException;

    /**
     * Getter for the "TotalEmailFailuresToday" variable.
     */
    public Long getTotalEmailFailuresToday() throws SnmpStatusException;

    /**
     * Getter for the "NotificationStatisticsResetTime" variable.
     */
    public Long getNotificationStatisticsResetTime() throws SnmpStatusException;

    /**
     * Getter for the "TotalEmailSuccessToday" variable.
     */
    public Long getTotalEmailSuccessToday() throws SnmpStatusException;

    /**
     * Getter for the "NotificationServiceUpTime" variable.
     */
    public Long getNotificationServiceUpTime() throws SnmpStatusException;

    /**
     * Getter for the "TotalEmailProcessedToday" variable.
     */
    public Long getTotalEmailProcessedToday() throws SnmpStatusException;

    /**
     * Getter for the "TotalNotificationProcessedToday" variable.
     */
    public Long getTotalNotificationProcessedToday() throws SnmpStatusException;

}
