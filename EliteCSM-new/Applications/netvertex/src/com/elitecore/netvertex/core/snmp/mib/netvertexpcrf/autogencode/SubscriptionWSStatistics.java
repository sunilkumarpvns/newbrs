package com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode;

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
 * The class is used for implementing the "SubscriptionWSStatistics" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.4.1.3.9.
 */
public class SubscriptionWSStatistics implements SubscriptionWSStatisticsMBean, Serializable {

    /**
     * Variable for storing the value of "SubscriptionWSListBoDPackagesTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.30".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsListBoDPackages ResultCode statistics."
     *
     */
    protected TableSubscriptionWSListBoDPackagesTable SubscriptionWSListBoDPackagesTable;

    /**
     * Variable for storing the value of "SubscriptionWSGetRnCBalanceTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.19".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsGetRnCBalance ResultCode statistics."
     *
     */
    protected TableSubscriptionWSGetRnCBalanceTable SubscriptionWSGetRnCBalanceTable;

    /**
     * Variable for storing the value of "SubscriptionWSAddMonetaryBalanceTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.18".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsAddMonetaryBalance ResultCode statistics."
     *
     */
    protected TableSubscriptionWSAddMonetaryBalanceTable SubscriptionWSAddMonetaryBalanceTable;

    /**
     * Variable for storing the value of "SubscriptionWSChangeTopUpSubscriptionTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.17".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsChangeTopUpSubscription ResultCode statistics."
     *
     */
    protected TableSubscriptionWSChangeTopUpSubscriptionTable SubscriptionWSChangeTopUpSubscriptionTable;

    /**
     * Variable for storing the value of "SubscriptionWSSubscribeTopUpTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.16".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsSubscribeTopUp ResultCode statistics."
     *
     */
    protected TableSubscriptionWSSubscribeTopUpTable SubscriptionWSSubscribeTopUpTable;

    /**
     * Variable for storing the value of "SubscriptionWSListTopUpPackagesTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.15".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsListTopUpPackages ResultCode statistics."
     *
     */
    protected TableSubscriptionWSListTopUpPackagesTable SubscriptionWSListTopUpPackagesTable;

    /**
     * Variable for storing the value of "SubscriptionWSGetBalanceTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.14".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsGetBalance ResultCode statistics."
     *
     */
    protected TableSubscriptionWSGetBalanceTable SubscriptionWSGetBalanceTable;

    /**
     * Variable for storing the value of "SubscriptionWSListUsageMonitoringInformationTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.13".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsListUsageMonitoringInformation ResultCode statistics."
     *
     */
    protected TableSubscriptionWSListUsageMonitoringInformationTable SubscriptionWSListUsageMonitoringInformationTable;

    /**
     * Variable for storing the value of "SubscriptionWSSubscribeAddOnProductOfferTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.12".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsSubscribeAddOnProductOffer ResultCode statistics."
     *
     */
    protected TableSubscriptionWSSubscribeAddOnProductOfferTable SubscriptionWSSubscribeAddOnProductOfferTable;

    /**
     * Variable for storing the value of "SubscriptionWSChangeDataAddOnSubscriptionTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.11".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsChangeDataAddOnSubscription ResultCode statistics."
     *
     */
    protected TableSubscriptionWSChangeDataAddOnSubscriptionTable SubscriptionWSChangeDataAddOnSubscriptionTable;

    /**
     * Variable for storing the value of "SubscriptionWSListTopUpSubscriptionsTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.10".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsListTopUpSubscriptions ResultCode statistics."
     *
     */
    protected TableSubscriptionWSListTopUpSubscriptionsTable SubscriptionWSListTopUpSubscriptionsTable;

    /**
     * Variable for storing the value of "SubscriptionWSListAddOnSubscriptionsTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.9".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsListAddOnSubscriptions ResultCode statistics."
     *
     */
    protected TableSubscriptionWSListAddOnSubscriptionsTable SubscriptionWSListAddOnSubscriptionsTable;

    /**
     * Variable for storing the value of "SubscriptionWSListPackagesTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.8".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsListPackages ResultCode statistics."
     *
     */
    protected TableSubscriptionWSListPackagesTable SubscriptionWSListPackagesTable;

    /**
     * Variable for storing the value of "SubscriptionWSSubscribeBoDTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.29".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsSubscribeBoD ResultCode statistics."
     *
     */
    protected TableSubscriptionWSSubscribeBoDTable SubscriptionWSSubscribeBoDTable;

    /**
     * Variable for storing the value of "SubscriptionWSListAddOnPackagesTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.7".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wise ResultCode statistics."
     *
     */
    protected TableSubscriptionWSListAddOnPackagesTable SubscriptionWSListAddOnPackagesTable;

    /**
     * Variable for storing the value of "SubscriptionWSChangeBillDayTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.28".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsChangeBillDay ResultCode statistics."
     *
     */
    protected TableSubscriptionWSChangeBillDayTable SubscriptionWSChangeBillDayTable;

    /**
     * Variable for storing the value of "SubscriptionWSResultCodeWiseStatisticsTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.6".
     *
     * "The (conceptual) table listing the SubscriptionWS ResultCodes statistics."
     *
     */
    protected TableSubscriptionWSResultCodeWiseStatisticsTable SubscriptionWSResultCodeWiseStatisticsTable;

    /**
     * Variable for storing the value of "SubscriptionWSListDataPackagesTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.27".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsListDataPackages ResultCode statistics."
     *
     */
    protected TableSubscriptionWSListDataPackagesTable SubscriptionWSListDataPackagesTable;

    /**
     * Variable for storing the value of "SubscriptionWSMethodWiseStatisticsTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.5".
     *
     * "The (conceptual) table listing the SubscriptionWS method wise statistics."
     *
     */
    protected TableSubscriptionWSMethodWiseStatisticsTable SubscriptionWSMethodWiseStatisticsTable;

    /**
     * Variable for storing the value of "SubscriptionWSUpdateCreditLimitTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.26".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsUpdateCreditLimit ResultCode statistics."
     *
     */
    protected TableSubscriptionWSUpdateCreditLimitTable SubscriptionWSUpdateCreditLimitTable;

    /**
     * Variable for storing the value of "SubscriptionWSAvgTPS".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.4".
     *
     * "The average TPS in SubscriptionWS for last one minute."
     *
     */
    protected Long SubscriptionWSAvgTPS = new Long(1);

    /**
     * Variable for storing the value of "SubscriptionWSSubscribeMonetaryRechargePlanTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.25".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsSubscribeMonetaryRechargePlan ResultCode statistics."
     *
     */
    protected TableSubscriptionWSSubscribeMonetaryRechargePlanTable SubscriptionWSSubscribeMonetaryRechargePlanTable;

    /**
     * Variable for storing the value of "SubscriptionWSListRnCPackagesTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.24".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsListRnCPackages ResultCode statistics."
     *
     */
    protected TableSubscriptionWSListRnCPackagesTable SubscriptionWSListRnCPackagesTable;

    /**
     * Variable for storing the value of "SubscriptionWSLastMinuteTotalRequests".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.3".
     *
     * "Total number of SubscriptionWS requests received in Last Minute by Policy Designer."
     *
     */
    protected Long SubscriptionWSLastMinuteTotalRequests = new Long(1);

    /**
     * Variable for storing the value of "SubscriptionWSTotalResponses".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.2".
     *
     * "Total number of SubscriptionWS responses given by Policy Designer."
     *
     */
    protected Long SubscriptionWSTotalResponses = new Long(1);

    /**
     * Variable for storing the value of "SubscriptionWSGetNonMonetaryBalanceTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.23".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsGetNonMonetaryBalance ResultCode statistics."
     *
     */
    protected TableSubscriptionWSGetNonMonetaryBalanceTable SubscriptionWSGetNonMonetaryBalanceTable;

    /**
     * Variable for storing the value of "SubscriptionWSTotalRequests".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.1".
     *
     * "Total number of SubscriptionWS requests received by Policy Designer."
     *
     */
    protected Long SubscriptionWSTotalRequests = new Long(1);

    /**
     * Variable for storing the value of "SubscriptionWSUpdateMonetaryBalanceTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.22".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsUpdateMonetaryBalance ResultCode statistics."
     *
     */
    protected TableSubscriptionWSUpdateMonetaryBalanceTable SubscriptionWSUpdateMonetaryBalanceTable;

    /**
     * Variable for storing the value of "SubscriptionWSGetMonetaryBalanceTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.21".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsGetMonetaryBalance ResultCode statistics."
     *
     */
    protected TableSubscriptionWSGetMonetaryBalanceTable SubscriptionWSGetMonetaryBalanceTable;

    /**
     * Variable for storing the value of "SubscriptionWSChangeRnCAddOnSubscriptionTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.4.1.3.9.20".
     *
     * "The (conceptual) table listing the SubscriptionWS Method wsChangeRnCAddOnSubscription ResultCode statistics."
     *
     */
    protected TableSubscriptionWSChangeRnCAddOnSubscriptionTable SubscriptionWSChangeRnCAddOnSubscriptionTable;


    /**
     * Constructor for the "SubscriptionWSStatistics" group.
     * If the group contains a table, the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public SubscriptionWSStatistics(SnmpMib myMib) {
        SubscriptionWSListBoDPackagesTable = new TableSubscriptionWSListBoDPackagesTable (myMib);
        SubscriptionWSGetRnCBalanceTable = new TableSubscriptionWSGetRnCBalanceTable (myMib);
        SubscriptionWSAddMonetaryBalanceTable = new TableSubscriptionWSAddMonetaryBalanceTable (myMib);
        SubscriptionWSChangeTopUpSubscriptionTable = new TableSubscriptionWSChangeTopUpSubscriptionTable (myMib);
        SubscriptionWSSubscribeTopUpTable = new TableSubscriptionWSSubscribeTopUpTable (myMib);
        SubscriptionWSListTopUpPackagesTable = new TableSubscriptionWSListTopUpPackagesTable (myMib);
        SubscriptionWSGetBalanceTable = new TableSubscriptionWSGetBalanceTable (myMib);
        SubscriptionWSListUsageMonitoringInformationTable = new TableSubscriptionWSListUsageMonitoringInformationTable (myMib);
        SubscriptionWSSubscribeAddOnProductOfferTable = new TableSubscriptionWSSubscribeAddOnProductOfferTable (myMib);
        SubscriptionWSChangeDataAddOnSubscriptionTable = new TableSubscriptionWSChangeDataAddOnSubscriptionTable (myMib);
        SubscriptionWSListTopUpSubscriptionsTable = new TableSubscriptionWSListTopUpSubscriptionsTable (myMib);
        SubscriptionWSListAddOnSubscriptionsTable = new TableSubscriptionWSListAddOnSubscriptionsTable (myMib);
        SubscriptionWSListPackagesTable = new TableSubscriptionWSListPackagesTable (myMib);
        SubscriptionWSSubscribeBoDTable = new TableSubscriptionWSSubscribeBoDTable (myMib);
        SubscriptionWSListAddOnPackagesTable = new TableSubscriptionWSListAddOnPackagesTable (myMib);
        SubscriptionWSChangeBillDayTable = new TableSubscriptionWSChangeBillDayTable (myMib);
        SubscriptionWSResultCodeWiseStatisticsTable = new TableSubscriptionWSResultCodeWiseStatisticsTable (myMib);
        SubscriptionWSListDataPackagesTable = new TableSubscriptionWSListDataPackagesTable (myMib);
        SubscriptionWSMethodWiseStatisticsTable = new TableSubscriptionWSMethodWiseStatisticsTable (myMib);
        SubscriptionWSUpdateCreditLimitTable = new TableSubscriptionWSUpdateCreditLimitTable (myMib);
        SubscriptionWSSubscribeMonetaryRechargePlanTable = new TableSubscriptionWSSubscribeMonetaryRechargePlanTable (myMib);
        SubscriptionWSListRnCPackagesTable = new TableSubscriptionWSListRnCPackagesTable (myMib);
        SubscriptionWSGetNonMonetaryBalanceTable = new TableSubscriptionWSGetNonMonetaryBalanceTable (myMib);
        SubscriptionWSUpdateMonetaryBalanceTable = new TableSubscriptionWSUpdateMonetaryBalanceTable (myMib);
        SubscriptionWSGetMonetaryBalanceTable = new TableSubscriptionWSGetMonetaryBalanceTable (myMib);
        SubscriptionWSChangeRnCAddOnSubscriptionTable = new TableSubscriptionWSChangeRnCAddOnSubscriptionTable (myMib);
    }


    /**
     * Constructor for the "SubscriptionWSStatistics" group.
     * If the group contains a table, the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public SubscriptionWSStatistics(SnmpMib myMib, MBeanServer server) {
        SubscriptionWSListBoDPackagesTable = new TableSubscriptionWSListBoDPackagesTable (myMib, server);
        SubscriptionWSGetRnCBalanceTable = new TableSubscriptionWSGetRnCBalanceTable (myMib, server);
        SubscriptionWSAddMonetaryBalanceTable = new TableSubscriptionWSAddMonetaryBalanceTable (myMib, server);
        SubscriptionWSChangeTopUpSubscriptionTable = new TableSubscriptionWSChangeTopUpSubscriptionTable (myMib, server);
        SubscriptionWSSubscribeTopUpTable = new TableSubscriptionWSSubscribeTopUpTable (myMib, server);
        SubscriptionWSListTopUpPackagesTable = new TableSubscriptionWSListTopUpPackagesTable (myMib, server);
        SubscriptionWSGetBalanceTable = new TableSubscriptionWSGetBalanceTable (myMib, server);
        SubscriptionWSListUsageMonitoringInformationTable = new TableSubscriptionWSListUsageMonitoringInformationTable (myMib, server);
        SubscriptionWSSubscribeAddOnProductOfferTable = new TableSubscriptionWSSubscribeAddOnProductOfferTable (myMib, server);
        SubscriptionWSChangeDataAddOnSubscriptionTable = new TableSubscriptionWSChangeDataAddOnSubscriptionTable (myMib, server);
        SubscriptionWSListTopUpSubscriptionsTable = new TableSubscriptionWSListTopUpSubscriptionsTable (myMib, server);
        SubscriptionWSListAddOnSubscriptionsTable = new TableSubscriptionWSListAddOnSubscriptionsTable (myMib, server);
        SubscriptionWSListPackagesTable = new TableSubscriptionWSListPackagesTable (myMib, server);
        SubscriptionWSSubscribeBoDTable = new TableSubscriptionWSSubscribeBoDTable (myMib, server);
        SubscriptionWSListAddOnPackagesTable = new TableSubscriptionWSListAddOnPackagesTable (myMib, server);
        SubscriptionWSChangeBillDayTable = new TableSubscriptionWSChangeBillDayTable (myMib, server);
        SubscriptionWSResultCodeWiseStatisticsTable = new TableSubscriptionWSResultCodeWiseStatisticsTable (myMib, server);
        SubscriptionWSListDataPackagesTable = new TableSubscriptionWSListDataPackagesTable (myMib, server);
        SubscriptionWSMethodWiseStatisticsTable = new TableSubscriptionWSMethodWiseStatisticsTable (myMib, server);
        SubscriptionWSUpdateCreditLimitTable = new TableSubscriptionWSUpdateCreditLimitTable (myMib, server);
        SubscriptionWSSubscribeMonetaryRechargePlanTable = new TableSubscriptionWSSubscribeMonetaryRechargePlanTable (myMib, server);
        SubscriptionWSListRnCPackagesTable = new TableSubscriptionWSListRnCPackagesTable (myMib, server);
        SubscriptionWSGetNonMonetaryBalanceTable = new TableSubscriptionWSGetNonMonetaryBalanceTable (myMib, server);
        SubscriptionWSUpdateMonetaryBalanceTable = new TableSubscriptionWSUpdateMonetaryBalanceTable (myMib, server);
        SubscriptionWSGetMonetaryBalanceTable = new TableSubscriptionWSGetMonetaryBalanceTable (myMib, server);
        SubscriptionWSChangeRnCAddOnSubscriptionTable = new TableSubscriptionWSChangeRnCAddOnSubscriptionTable (myMib, server);
    }

    /**
     * Access the "SubscriptionWSListBoDPackagesTable" variable.
     */
    public TableSubscriptionWSListBoDPackagesTable accessSubscriptionWSListBoDPackagesTable() throws SnmpStatusException {
        return SubscriptionWSListBoDPackagesTable;
    }

    /**
     * Access the "SubscriptionWSListBoDPackagesTable" variable as a bean indexed property.
     */
    public SubscriptionWSListBoDPackagesEntryMBean[] getSubscriptionWSListBoDPackagesTable() throws SnmpStatusException {
        return SubscriptionWSListBoDPackagesTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSGetRnCBalanceTable" variable.
     */
    public TableSubscriptionWSGetRnCBalanceTable accessSubscriptionWSGetRnCBalanceTable() throws SnmpStatusException {
        return SubscriptionWSGetRnCBalanceTable;
    }

    /**
     * Access the "SubscriptionWSGetRnCBalanceTable" variable as a bean indexed property.
     */
    public SubscriptionWSGetRnCBalanceEntryMBean[] getSubscriptionWSGetRnCBalanceTable() throws SnmpStatusException {
        return SubscriptionWSGetRnCBalanceTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSAddMonetaryBalanceTable" variable.
     */
    public TableSubscriptionWSAddMonetaryBalanceTable accessSubscriptionWSAddMonetaryBalanceTable() throws SnmpStatusException {
        return SubscriptionWSAddMonetaryBalanceTable;
    }

    /**
     * Access the "SubscriptionWSAddMonetaryBalanceTable" variable as a bean indexed property.
     */
    public SubscriptionWSAddMonetaryBalanceEntryMBean[] getSubscriptionWSAddMonetaryBalanceTable() throws SnmpStatusException {
        return SubscriptionWSAddMonetaryBalanceTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSChangeTopUpSubscriptionTable" variable.
     */
    public TableSubscriptionWSChangeTopUpSubscriptionTable accessSubscriptionWSChangeTopUpSubscriptionTable() throws SnmpStatusException {
        return SubscriptionWSChangeTopUpSubscriptionTable;
    }

    /**
     * Access the "SubscriptionWSChangeTopUpSubscriptionTable" variable as a bean indexed property.
     */
    public SubscriptionWSChangeTopUpSubscriptionEntryMBean[] getSubscriptionWSChangeTopUpSubscriptionTable() throws SnmpStatusException {
        return SubscriptionWSChangeTopUpSubscriptionTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSSubscribeTopUpTable" variable.
     */
    public TableSubscriptionWSSubscribeTopUpTable accessSubscriptionWSSubscribeTopUpTable() throws SnmpStatusException {
        return SubscriptionWSSubscribeTopUpTable;
    }

    /**
     * Access the "SubscriptionWSSubscribeTopUpTable" variable as a bean indexed property.
     */
    public SubscriptionWSSubscribeTopUpEntryMBean[] getSubscriptionWSSubscribeTopUpTable() throws SnmpStatusException {
        return SubscriptionWSSubscribeTopUpTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSListTopUpPackagesTable" variable.
     */
    public TableSubscriptionWSListTopUpPackagesTable accessSubscriptionWSListTopUpPackagesTable() throws SnmpStatusException {
        return SubscriptionWSListTopUpPackagesTable;
    }

    /**
     * Access the "SubscriptionWSListTopUpPackagesTable" variable as a bean indexed property.
     */
    public SubscriptionWSListTopUpPackagesEntryMBean[] getSubscriptionWSListTopUpPackagesTable() throws SnmpStatusException {
        return SubscriptionWSListTopUpPackagesTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSGetBalanceTable" variable.
     */
    public TableSubscriptionWSGetBalanceTable accessSubscriptionWSGetBalanceTable() throws SnmpStatusException {
        return SubscriptionWSGetBalanceTable;
    }

    /**
     * Access the "SubscriptionWSGetBalanceTable" variable as a bean indexed property.
     */
    public SubscriptionWSGetBalanceEntryMBean[] getSubscriptionWSGetBalanceTable() throws SnmpStatusException {
        return SubscriptionWSGetBalanceTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSListUsageMonitoringInformationTable" variable.
     */
    public TableSubscriptionWSListUsageMonitoringInformationTable accessSubscriptionWSListUsageMonitoringInformationTable() throws SnmpStatusException {
        return SubscriptionWSListUsageMonitoringInformationTable;
    }

    /**
     * Access the "SubscriptionWSListUsageMonitoringInformationTable" variable as a bean indexed property.
     */
    public SubscriptionWSListUsageMonitoringInformationEntryMBean[] getSubscriptionWSListUsageMonitoringInformationTable() throws SnmpStatusException {
        return SubscriptionWSListUsageMonitoringInformationTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSSubscribeAddOnProductOfferTable" variable.
     */
    public TableSubscriptionWSSubscribeAddOnProductOfferTable accessSubscriptionWSSubscribeAddOnProductOfferTable() throws SnmpStatusException {
        return SubscriptionWSSubscribeAddOnProductOfferTable;
    }

    /**
     * Access the "SubscriptionWSSubscribeAddOnProductOfferTable" variable as a bean indexed property.
     */
    public SubscriptionWSSubscribeAddOnProductOfferEntryMBean[] getSubscriptionWSSubscribeAddOnProductOfferTable() throws SnmpStatusException {
        return SubscriptionWSSubscribeAddOnProductOfferTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSChangeDataAddOnSubscriptionTable" variable.
     */
    public TableSubscriptionWSChangeDataAddOnSubscriptionTable accessSubscriptionWSChangeDataAddOnSubscriptionTable() throws SnmpStatusException {
        return SubscriptionWSChangeDataAddOnSubscriptionTable;
    }

    /**
     * Access the "SubscriptionWSChangeDataAddOnSubscriptionTable" variable as a bean indexed property.
     */
    public SubscriptionWSChangeDataAddOnSubscriptionEntryMBean[] getSubscriptionWSChangeDataAddOnSubscriptionTable() throws SnmpStatusException {
        return SubscriptionWSChangeDataAddOnSubscriptionTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSListTopUpSubscriptionsTable" variable.
     */
    public TableSubscriptionWSListTopUpSubscriptionsTable accessSubscriptionWSListTopUpSubscriptionsTable() throws SnmpStatusException {
        return SubscriptionWSListTopUpSubscriptionsTable;
    }

    /**
     * Access the "SubscriptionWSListTopUpSubscriptionsTable" variable as a bean indexed property.
     */
    public SubscriptionWSListTopUpSubscriptionsEntryMBean[] getSubscriptionWSListTopUpSubscriptionsTable() throws SnmpStatusException {
        return SubscriptionWSListTopUpSubscriptionsTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSListAddOnSubscriptionsTable" variable.
     */
    public TableSubscriptionWSListAddOnSubscriptionsTable accessSubscriptionWSListAddOnSubscriptionsTable() throws SnmpStatusException {
        return SubscriptionWSListAddOnSubscriptionsTable;
    }

    /**
     * Access the "SubscriptionWSListAddOnSubscriptionsTable" variable as a bean indexed property.
     */
    public SubscriptionWSListAddOnSubscriptionsEntryMBean[] getSubscriptionWSListAddOnSubscriptionsTable() throws SnmpStatusException {
        return SubscriptionWSListAddOnSubscriptionsTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSListPackagesTable" variable.
     */
    public TableSubscriptionWSListPackagesTable accessSubscriptionWSListPackagesTable() throws SnmpStatusException {
        return SubscriptionWSListPackagesTable;
    }

    /**
     * Access the "SubscriptionWSListPackagesTable" variable as a bean indexed property.
     */
    public SubscriptionWSListPackagesEntryMBean[] getSubscriptionWSListPackagesTable() throws SnmpStatusException {
        return SubscriptionWSListPackagesTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSSubscribeBoDTable" variable.
     */
    public TableSubscriptionWSSubscribeBoDTable accessSubscriptionWSSubscribeBoDTable() throws SnmpStatusException {
        return SubscriptionWSSubscribeBoDTable;
    }

    /**
     * Access the "SubscriptionWSSubscribeBoDTable" variable as a bean indexed property.
     */
    public SubscriptionWSSubscribeBoDEntryMBean[] getSubscriptionWSSubscribeBoDTable() throws SnmpStatusException {
        return SubscriptionWSSubscribeBoDTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSListAddOnPackagesTable" variable.
     */
    public TableSubscriptionWSListAddOnPackagesTable accessSubscriptionWSListAddOnPackagesTable() throws SnmpStatusException {
        return SubscriptionWSListAddOnPackagesTable;
    }

    /**
     * Access the "SubscriptionWSListAddOnPackagesTable" variable as a bean indexed property.
     */
    public SubscriptionWSListAddOnPackagesEntryMBean[] getSubscriptionWSListAddOnPackagesTable() throws SnmpStatusException {
        return SubscriptionWSListAddOnPackagesTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSChangeBillDayTable" variable.
     */
    public TableSubscriptionWSChangeBillDayTable accessSubscriptionWSChangeBillDayTable() throws SnmpStatusException {
        return SubscriptionWSChangeBillDayTable;
    }

    /**
     * Access the "SubscriptionWSChangeBillDayTable" variable as a bean indexed property.
     */
    public SubscriptionWSChangeBillDayEntryMBean[] getSubscriptionWSChangeBillDayTable() throws SnmpStatusException {
        return SubscriptionWSChangeBillDayTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSResultCodeWiseStatisticsTable" variable.
     */
    public TableSubscriptionWSResultCodeWiseStatisticsTable accessSubscriptionWSResultCodeWiseStatisticsTable() throws SnmpStatusException {
        return SubscriptionWSResultCodeWiseStatisticsTable;
    }

    /**
     * Access the "SubscriptionWSResultCodeWiseStatisticsTable" variable as a bean indexed property.
     */
    public SubscriptionWSResultCodeWiseEntryMBean[] getSubscriptionWSResultCodeWiseStatisticsTable() throws SnmpStatusException {
        return SubscriptionWSResultCodeWiseStatisticsTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSListDataPackagesTable" variable.
     */
    public TableSubscriptionWSListDataPackagesTable accessSubscriptionWSListDataPackagesTable() throws SnmpStatusException {
        return SubscriptionWSListDataPackagesTable;
    }

    /**
     * Access the "SubscriptionWSListDataPackagesTable" variable as a bean indexed property.
     */
    public SubscriptionWSListDataPackagesEntryMBean[] getSubscriptionWSListDataPackagesTable() throws SnmpStatusException {
        return SubscriptionWSListDataPackagesTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSMethodWiseStatisticsTable" variable.
     */
    public TableSubscriptionWSMethodWiseStatisticsTable accessSubscriptionWSMethodWiseStatisticsTable() throws SnmpStatusException {
        return SubscriptionWSMethodWiseStatisticsTable;
    }

    /**
     * Access the "SubscriptionWSMethodWiseStatisticsTable" variable as a bean indexed property.
     */
    public SubscriptionWSMethodWiseEntryMBean[] getSubscriptionWSMethodWiseStatisticsTable() throws SnmpStatusException {
        return SubscriptionWSMethodWiseStatisticsTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSUpdateCreditLimitTable" variable.
     */
    public TableSubscriptionWSUpdateCreditLimitTable accessSubscriptionWSUpdateCreditLimitTable() throws SnmpStatusException {
        return SubscriptionWSUpdateCreditLimitTable;
    }

    /**
     * Access the "SubscriptionWSUpdateCreditLimitTable" variable as a bean indexed property.
     */
    public SubscriptionWSUpdateCreditLimitEntryMBean[] getSubscriptionWSUpdateCreditLimitTable() throws SnmpStatusException {
        return SubscriptionWSUpdateCreditLimitTable.getEntries();
    }

    /**
     * Getter for the "SubscriptionWSAvgTPS" variable.
     */
    public Long getSubscriptionWSAvgTPS() throws SnmpStatusException {
        return SubscriptionWSAvgTPS;
    }

    /**
     * Access the "SubscriptionWSSubscribeMonetaryRechargePlanTable" variable.
     */
    public TableSubscriptionWSSubscribeMonetaryRechargePlanTable accessSubscriptionWSSubscribeMonetaryRechargePlanTable() throws SnmpStatusException {
        return SubscriptionWSSubscribeMonetaryRechargePlanTable;
    }

    /**
     * Access the "SubscriptionWSSubscribeMonetaryRechargePlanTable" variable as a bean indexed property.
     */
    public SubscriptionWSSubscribeMonetaryRechargePlanEntryMBean[] getSubscriptionWSSubscribeMonetaryRechargePlanTable() throws SnmpStatusException {
        return SubscriptionWSSubscribeMonetaryRechargePlanTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSListRnCPackagesTable" variable.
     */
    public TableSubscriptionWSListRnCPackagesTable accessSubscriptionWSListRnCPackagesTable() throws SnmpStatusException {
        return SubscriptionWSListRnCPackagesTable;
    }

    /**
     * Access the "SubscriptionWSListRnCPackagesTable" variable as a bean indexed property.
     */
    public SubscriptionWSListRnCPackagesEntryMBean[] getSubscriptionWSListRnCPackagesTable() throws SnmpStatusException {
        return SubscriptionWSListRnCPackagesTable.getEntries();
    }

    /**
     * Getter for the "SubscriptionWSLastMinuteTotalRequests" variable.
     */
    public Long getSubscriptionWSLastMinuteTotalRequests() throws SnmpStatusException {
        return SubscriptionWSLastMinuteTotalRequests;
    }

    /**
     * Getter for the "SubscriptionWSTotalResponses" variable.
     */
    public Long getSubscriptionWSTotalResponses() throws SnmpStatusException {
        return SubscriptionWSTotalResponses;
    }

    /**
     * Access the "SubscriptionWSGetNonMonetaryBalanceTable" variable.
     */
    public TableSubscriptionWSGetNonMonetaryBalanceTable accessSubscriptionWSGetNonMonetaryBalanceTable() throws SnmpStatusException {
        return SubscriptionWSGetNonMonetaryBalanceTable;
    }

    /**
     * Access the "SubscriptionWSGetNonMonetaryBalanceTable" variable as a bean indexed property.
     */
    public SubscriptionWSGetNonMonetaryBalanceEntryMBean[] getSubscriptionWSGetNonMonetaryBalanceTable() throws SnmpStatusException {
        return SubscriptionWSGetNonMonetaryBalanceTable.getEntries();
    }

    /**
     * Getter for the "SubscriptionWSTotalRequests" variable.
     */
    public Long getSubscriptionWSTotalRequests() throws SnmpStatusException {
        return SubscriptionWSTotalRequests;
    }

    /**
     * Access the "SubscriptionWSUpdateMonetaryBalanceTable" variable.
     */
    public TableSubscriptionWSUpdateMonetaryBalanceTable accessSubscriptionWSUpdateMonetaryBalanceTable() throws SnmpStatusException {
        return SubscriptionWSUpdateMonetaryBalanceTable;
    }

    /**
     * Access the "SubscriptionWSUpdateMonetaryBalanceTable" variable as a bean indexed property.
     */
    public SubscriptionWSUpdateMonetaryBalanceEntryMBean[] getSubscriptionWSUpdateMonetaryBalanceTable() throws SnmpStatusException {
        return SubscriptionWSUpdateMonetaryBalanceTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSGetMonetaryBalanceTable" variable.
     */
    public TableSubscriptionWSGetMonetaryBalanceTable accessSubscriptionWSGetMonetaryBalanceTable() throws SnmpStatusException {
        return SubscriptionWSGetMonetaryBalanceTable;
    }

    /**
     * Access the "SubscriptionWSGetMonetaryBalanceTable" variable as a bean indexed property.
     */
    public SubscriptionWSGetMonetaryBalanceEntryMBean[] getSubscriptionWSGetMonetaryBalanceTable() throws SnmpStatusException {
        return SubscriptionWSGetMonetaryBalanceTable.getEntries();
    }

    /**
     * Access the "SubscriptionWSChangeRnCAddOnSubscriptionTable" variable.
     */
    public TableSubscriptionWSChangeRnCAddOnSubscriptionTable accessSubscriptionWSChangeRnCAddOnSubscriptionTable() throws SnmpStatusException {
        return SubscriptionWSChangeRnCAddOnSubscriptionTable;
    }

    /**
     * Access the "SubscriptionWSChangeRnCAddOnSubscriptionTable" variable as a bean indexed property.
     */
    public SubscriptionWSChangeRnCAddOnSubscriptionEntryMBean[] getSubscriptionWSChangeRnCAddOnSubscriptionTable() throws SnmpStatusException {
        return SubscriptionWSChangeRnCAddOnSubscriptionTable.getEntries();
    }

}
