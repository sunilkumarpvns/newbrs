package com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "UsageStatistics" MBean.
 */
public interface UsageStatisticsMBean {

    /**
     * Getter for the "UsageStatisticsReset" variable.
     */
    public EnumUsageStatisticsReset getUsageStatisticsReset() throws SnmpStatusException;

    /**
     * Setter for the "UsageStatisticsReset" variable.
     */
    public void setUsageStatisticsReset(EnumUsageStatisticsReset x) throws SnmpStatusException;

    /**
     * Checker for the "UsageStatisticsReset" variable.
     */
    public void checkUsageStatisticsReset(EnumUsageStatisticsReset x) throws SnmpStatusException;

    /**
     * Getter for the "UsageStatisticsResetTime" variable.
     */
    public Long getUsageStatisticsResetTime() throws SnmpStatusException;

    /**
     * Getter for the "TotalUsageReportedInLast24Hours" variable.
     */
    public Long getTotalUsageReportedInLast24Hours() throws SnmpStatusException;

    /**
     * Getter for the "TotalUsageReportedInYesterday" variable.
     */
    public Long getTotalUsageReportedInYesterday() throws SnmpStatusException;

    /**
     * Getter for the "TotalUsageReportedInCurrentDay" variable.
     */
    public Long getTotalUsageReportedInCurrentDay() throws SnmpStatusException;

    /**
     * Getter for the "TotalUsageReportedInLastHour" variable.
     */
    public Long getTotalUsageReportedInLastHour() throws SnmpStatusException;

}