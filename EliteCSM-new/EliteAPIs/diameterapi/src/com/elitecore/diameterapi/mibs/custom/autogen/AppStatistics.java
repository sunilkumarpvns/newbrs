package com.elitecore.diameterapi.mibs.custom.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-STACK-MIB.
//

// java imports
//
import java.io.Serializable;

import javax.management.MBeanServer;

import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;
// jmx imports
//
// jdmk imports
//

/**
 * The class is used for implementing the "AppStatistics" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.5.101.
 */
public class AppStatistics implements AppStatisticsMBean, Serializable {

    /**
     * Variable for storing the value of "AppStatisticsTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.5.101.1".
     *
     * " The table contains the application specific statistics "
     *
     */
    protected TableAppStatisticsTable AppStatisticsTable;


    public AppStatistics() {
    }
    /**
     * Constructor for the "AppStatistics" group.
     * If the group contains a table, the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public AppStatistics(SnmpMib myMib) {
        AppStatisticsTable = new TableAppStatisticsTable (myMib);
    }


    /**
     * Constructor for the "AppStatistics" group.
     * If the group contains a table, the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public AppStatistics(SnmpMib myMib, MBeanServer server) {
        AppStatisticsTable = new TableAppStatisticsTable (myMib, server);
    }

    /**
     * Access the "AppStatisticsTable" variable.
     */
    public TableAppStatisticsTable accessAppStatisticsTable() throws SnmpStatusException {
        return AppStatisticsTable;
    }

    /**
     * Access the "AppStatisticsTable" variable as a bean indexed property.
     */
    public ApplicationStatisticsEntryMBean[] getAppStatisticsTable() throws SnmpStatusException {
        return AppStatisticsTable.getEntries();
    }

}
