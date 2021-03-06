package com.elitecore.nvsmx.snmp.mib.webservice.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB.
//

// java imports
//
import java.io.Serializable;

// jmx imports
//
import javax.management.MBeanServer;
import com.sun.management.snmp.SnmpString;
import com.sun.management.snmp.SnmpStatusException;

// jdmk imports
//
import com.sun.management.snmp.agent.SnmpMib;

/**
 * The class is used for implementing the "NotificationStatistics" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.2.1.2.
 */
public class NotificationStatistics implements NotificationStatisticsMBean, Serializable {

    /**
     * Variable for storing the value of "TotalSMSFailuresYesterday".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.47".
     *
     * "The total number of SMS notification sending failed yesterday by notification service."
     *
     */
    protected Long TotalSMSFailuresYesterday = new Long(1);

    /**
     * Variable for storing the value of "TotalSMSSuccessYesterday".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.46".
     *
     * "The total number of SMS notification successfully sent yesterday by notification service."
     *
     */
    protected Long TotalSMSSuccessYesterday = new Long(1);

    /**
     * Variable for storing the value of "TotalSMSProcessedYesterday".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.45".
     *
     * "The total number of SMS notification processed yesterday by notification service."
     *
     */
    protected Long TotalSMSProcessedYesterday = new Long(1);

    /**
     * Variable for storing the value of "TotalEmailFailuresYesterday".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.44".
     *
     * "The total number of Email notification sending failed yesterday by notification service."
     *
     */
    protected Long TotalEmailFailuresYesterday = new Long(1);

    /**
     * Variable for storing the value of "TotalEmailSuccessYesterday".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.43".
     *
     * "The total number of Email notification successfully sent yesterday by notification service."
     *
     */
    protected Long TotalEmailSuccessYesterday = new Long(1);

    /**
     * Variable for storing the value of "TotalEmailProcessedYesterday".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.42".
     *
     * "The total number of Email notification processed yesterday by notification service."
     *
     */
    protected Long TotalEmailProcessedYesterday = new Long(1);

    /**
     * Variable for storing the value of "TotalSMSFailures".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.10".
     *
     * "The total number of SMS notification sending failed by notification service."
     *
     */
    protected Long TotalSMSFailures = new Long(1);

    /**
     * Variable for storing the value of "TotalNotificationProcessedYesterday".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.41".
     *
     * "The total number of notification processed yesterday by notification service."
     *
     */
    protected Long TotalNotificationProcessedYesterday = new Long(1);

    /**
     * Variable for storing the value of "TotalSMSSuccess".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.9".
     *
     * "The total number of SMS notification successfully sent by notification service."
     *
     */
    protected Long TotalSMSSuccess = new Long(1);

    /**
     * Variable for storing the value of "TotalSMSProcessed".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.8".
     *
     * "The total number of SMS notification processed by notification service."
     *
     */
    protected Long TotalSMSProcessed = new Long(1);

    /**
     * Variable for storing the value of "TotalEmailFailures".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.7".
     *
     * "The total number of Email notification sending failed by notification service."
     *
     */
    protected Long TotalEmailFailures = new Long(1);

    /**
     * Variable for storing the value of "TotalSMSFailuresToday".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.27".
     *
     * "The total number of SMS notification sending failed today by notification service."
     *
     */
    protected Long TotalSMSFailuresToday = new Long(1);

    /**
     * Variable for storing the value of "TotalEmailSuccess".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.6".
     *
     * "The total number of Email notification successfully sent by notification service."
     *
     */
    protected Long TotalEmailSuccess = new Long(1);

    /**
     * Variable for storing the value of "TotalSMSSuccessToday".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.26".
     *
     * "The total number of SMS notification successfully sent today by notification service."
     *
     */
    protected Long TotalSMSSuccessToday = new Long(1);

    /**
     * Variable for storing the value of "TotalEmailProcessed".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.5".
     *
     * "The total number of Email notification processed by notification service."
     *
     */
    protected Long TotalEmailProcessed = new Long(1);

    /**
     * Variable for storing the value of "TotalNotificationProcessed".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.4".
     *
     * "The total number of notification processed by notification service."
     *
     */
    protected Long TotalNotificationProcessed = new Long(1);

    /**
     * Variable for storing the value of "TotalSMSProcessedToday".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.25".
     *
     * "The total number of SMS notification processed today by notification service."
     *
     */
    protected Long TotalSMSProcessedToday = new Long(1);

    /**
     * Variable for storing the value of "NotificationStatisticsReset".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.3".
     *
     * "When read, one of the following values will be returned:
     * other(1) - service in some unknown state;
     * initializing(3) - service (einitializing;
     * running(4) - service currently running.
     * When set to reset(2),  Notification service statistics is reinitialized."
     *
     */
    protected EnumNotificationStatisticsReset NotificationStatisticsReset = new EnumNotificationStatisticsReset();

    /**
     * Variable for storing the value of "TotalEmailFailuresToday".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.24".
     *
     * "The total number of Email notification sending failed today by notification service."
     *
     */
    protected Long TotalEmailFailuresToday = new Long(1);

    /**
     * Variable for storing the value of "NotificationStatisticsResetTime".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.2".
     *
     * "This value will be the time elapsed (in hundredths of a second)
     * since the Notification service  statistics was `reset.'"
     *
     */
    protected Long NotificationStatisticsResetTime = new Long(1);

    /**
     * Variable for storing the value of "TotalEmailSuccessToday".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.23".
     *
     * "The total number of Email notification successfully sent today by notification service."
     *
     */
    protected Long TotalEmailSuccessToday = new Long(1);

    /**
     * Variable for storing the value of "NotificationServiceUpTime".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.1".
     *
     * "This value will be the time elapsed (in hundredths of a second)
     * since the Notification service started."
     *
     */
    protected Long NotificationServiceUpTime = new Long(1);

    /**
     * Variable for storing the value of "TotalEmailProcessedToday".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.22".
     *
     * "The total number of Email notification processed today by notification service."
     *
     */
    protected Long TotalEmailProcessedToday = new Long(1);

    /**
     * Variable for storing the value of "TotalNotificationProcessedToday".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.2.1.2.21".
     *
     * "The total number of notification processed today by notification service."
     *
     */
    protected Long TotalNotificationProcessedToday = new Long(1);


    /**
     * Constructor for the "NotificationStatistics" group.
     * If the group contains a table, the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public NotificationStatistics(SnmpMib myMib) {
    }


    /**
     * Constructor for the "NotificationStatistics" group.
     * If the group contains a table, the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public NotificationStatistics(SnmpMib myMib, MBeanServer server) {
    }

    /**
     * Getter for the "TotalSMSFailuresYesterday" variable.
     */
    public Long getTotalSMSFailuresYesterday() throws SnmpStatusException {
        return TotalSMSFailuresYesterday;
    }

    /**
     * Getter for the "TotalSMSSuccessYesterday" variable.
     */
    public Long getTotalSMSSuccessYesterday() throws SnmpStatusException {
        return TotalSMSSuccessYesterday;
    }

    /**
     * Getter for the "TotalSMSProcessedYesterday" variable.
     */
    public Long getTotalSMSProcessedYesterday() throws SnmpStatusException {
        return TotalSMSProcessedYesterday;
    }

    /**
     * Getter for the "TotalEmailFailuresYesterday" variable.
     */
    public Long getTotalEmailFailuresYesterday() throws SnmpStatusException {
        return TotalEmailFailuresYesterday;
    }

    /**
     * Getter for the "TotalEmailSuccessYesterday" variable.
     */
    public Long getTotalEmailSuccessYesterday() throws SnmpStatusException {
        return TotalEmailSuccessYesterday;
    }

    /**
     * Getter for the "TotalEmailProcessedYesterday" variable.
     */
    public Long getTotalEmailProcessedYesterday() throws SnmpStatusException {
        return TotalEmailProcessedYesterday;
    }

    /**
     * Getter for the "TotalSMSFailures" variable.
     */
    public Long getTotalSMSFailures() throws SnmpStatusException {
        return TotalSMSFailures;
    }

    /**
     * Getter for the "TotalNotificationProcessedYesterday" variable.
     */
    public Long getTotalNotificationProcessedYesterday() throws SnmpStatusException {
        return TotalNotificationProcessedYesterday;
    }

    /**
     * Getter for the "TotalSMSSuccess" variable.
     */
    public Long getTotalSMSSuccess() throws SnmpStatusException {
        return TotalSMSSuccess;
    }

    /**
     * Getter for the "TotalSMSProcessed" variable.
     */
    public Long getTotalSMSProcessed() throws SnmpStatusException {
        return TotalSMSProcessed;
    }

    /**
     * Getter for the "TotalEmailFailures" variable.
     */
    public Long getTotalEmailFailures() throws SnmpStatusException {
        return TotalEmailFailures;
    }

    /**
     * Getter for the "TotalSMSFailuresToday" variable.
     */
    public Long getTotalSMSFailuresToday() throws SnmpStatusException {
        return TotalSMSFailuresToday;
    }

    /**
     * Getter for the "TotalEmailSuccess" variable.
     */
    public Long getTotalEmailSuccess() throws SnmpStatusException {
        return TotalEmailSuccess;
    }

    /**
     * Getter for the "TotalSMSSuccessToday" variable.
     */
    public Long getTotalSMSSuccessToday() throws SnmpStatusException {
        return TotalSMSSuccessToday;
    }

    /**
     * Getter for the "TotalEmailProcessed" variable.
     */
    public Long getTotalEmailProcessed() throws SnmpStatusException {
        return TotalEmailProcessed;
    }

    /**
     * Getter for the "TotalNotificationProcessed" variable.
     */
    public Long getTotalNotificationProcessed() throws SnmpStatusException {
        return TotalNotificationProcessed;
    }

    /**
     * Getter for the "TotalSMSProcessedToday" variable.
     */
    public Long getTotalSMSProcessedToday() throws SnmpStatusException {
        return TotalSMSProcessedToday;
    }

    /**
     * Getter for the "NotificationStatisticsReset" variable.
     */
    public EnumNotificationStatisticsReset getNotificationStatisticsReset() throws SnmpStatusException {
        return NotificationStatisticsReset;
    }

    /**
     * Setter for the "NotificationStatisticsReset" variable.
     */
    public void setNotificationStatisticsReset(EnumNotificationStatisticsReset x) throws SnmpStatusException {
        NotificationStatisticsReset = x;
    }

    /**
     * Checker for the "NotificationStatisticsReset" variable.
     */
    public void checkNotificationStatisticsReset(EnumNotificationStatisticsReset x) throws SnmpStatusException {
        //
        // Add your own checking policy.
        //
    }

    /**
     * Getter for the "TotalEmailFailuresToday" variable.
     */
    public Long getTotalEmailFailuresToday() throws SnmpStatusException {
        return TotalEmailFailuresToday;
    }

    /**
     * Getter for the "NotificationStatisticsResetTime" variable.
     */
    public Long getNotificationStatisticsResetTime() throws SnmpStatusException {
        return NotificationStatisticsResetTime;
    }

    /**
     * Getter for the "TotalEmailSuccessToday" variable.
     */
    public Long getTotalEmailSuccessToday() throws SnmpStatusException {
        return TotalEmailSuccessToday;
    }

    /**
     * Getter for the "NotificationServiceUpTime" variable.
     */
    public Long getNotificationServiceUpTime() throws SnmpStatusException {
        return NotificationServiceUpTime;
    }

    /**
     * Getter for the "TotalEmailProcessedToday" variable.
     */
    public Long getTotalEmailProcessedToday() throws SnmpStatusException {
        return TotalEmailProcessedToday;
    }

    /**
     * Getter for the "TotalNotificationProcessedToday" variable.
     */
    public Long getTotalNotificationProcessedToday() throws SnmpStatusException {
        return TotalNotificationProcessedToday;
    }

}
