package com.elitecore.aaa.rm.service.ippool.snmp.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling IP-POOL-SERVICE-MIB.
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
 * The class is used for implementing the "IpPoolServMIBObjects" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.1.7.1.
 */
public class IpPoolServMIBObjects implements IpPoolServMIBObjectsMBean, Serializable {

    /**
     * Variable for storing the value of "IpPoolServTotalInvalidRequest".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.7.1.9".
     *
     * "Total No. of Radius request received from Unknown Client by IP-Pool service"
     *
     */
    protected Long IpPoolServTotalInvalidRequest = new Long(1);

    /**
     * Variable for storing the value of "IpPoolServTotalUnknownPacket".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.7.1.8".
     *
     * "Total No. of Unknown Radius Packet received by IP-Pool service"
     *
     */
    protected Long IpPoolServTotalUnknownPacket = new Long(1);

    /**
     * Variable for storing the value of "IpPoolServTotalDuplicateRequest".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.7.1.7".
     *
     * "Total No. of duplicate packets received by IP-Pool service"
     *
     */
    protected Long IpPoolServTotalDuplicateRequest = new Long(1);

    /**
     * Variable for storing the value of "IpPoolServTotalRequestDropped".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.7.1.6".
     *
     * "Total No. of packets dropped by IP-Pool service"
     *
     */
    protected Long IpPoolServTotalRequestDropped = new Long(1);

    /**
     * Variable for storing the value of "IpPoolServTotalUpdateRequest".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.7.1.15".
     *
     * "Total No. of Accounting Update received by IP-Pool service"
     *
     */
    protected Long IpPoolServTotalUpdateRequest = new Long(1);

    /**
     * Variable for storing the value of "IpPoolServTotalReleaseRequest".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.7.1.14".
     *
     * "Total No. Accounting Stop reponse sent by IP-Pool service"
     *
     */
    protected Long IpPoolServTotalReleaseRequest = new Long(1);

    /**
     * Variable for storing the value of "IpPoolServTotalResponses".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.7.1.5".
     *
     * "Total No. of response packet sent by IP-Pool service"
     *
     */
    protected Long IpPoolServTotalResponses = new Long(1);

    /**
     * Variable for storing the value of "IpPoolServTotalAllocationRequest".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.7.1.13".
     *
     * "Total No. of Accounting Start Request received by IP-Pool service"
     *
     */
    protected Long IpPoolServTotalAllocationRequest = new Long(1);

    /**
     * Variable for storing the value of "IpPoolServTotalRequest".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.7.1.4".
     *
     * "Total No. of packets received by IP-Pool service"
     *
     */
    protected Long IpPoolServTotalRequest = new Long(1);

    /**
     * Variable for storing the value of "IpPoolServiceResetTime".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.7.1.3".
     *
     * "Last Reset Time for IP-Pool Service"
     *
     */
    protected Long IpPoolServiceResetTime = new Long(1);

    /**
     * Variable for storing the value of "IpPoolServTotalDeclineResponse".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.7.1.12".
     *
     * "Total No. of Access Reject Response sent by IP-Pool service"
     *
     */
    protected Long IpPoolServTotalDeclineResponse = new Long(1);

    /**
     * Variable for storing the value of "IpPoolNASClientStatisticsTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.7.1.102".
     *
     * "Table defines number of NAS Client Communicate with RM server"
     *
     */
    protected TableIpPoolNASClientStatisticsTable IpPoolNASClientStatisticsTable;

    /**
     * Variable for storing the value of "IpPoolServiceReset".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.7.1.2".
     *
     * "When set to reset(2), IP-Pool service statistics is reinitialized.
     * When read, one of the following values will be returned:
     * other(1) - service in some unknown state;
     * initializing(3) - service initializing;
     * running(4) - service currently running"
     *
     */
    protected EnumIpPoolServiceReset IpPoolServiceReset = new EnumIpPoolServiceReset();

    /**
     * Variable for storing the value of "IpPoolAAAClientStatisticsTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.7.1.101".
     *
     * "Table defines number of configured AAA Client for the RM server"
     *
     */
    protected TableIpPoolAAAClientStatisticsTable IpPoolAAAClientStatisticsTable;

    /**
     * Variable for storing the value of "IpPoolServTotalOfferResponse".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.7.1.11".
     *
     * "Total No. of Access Accept sent by IP-Pool service"
     *
     */
    protected Long IpPoolServTotalOfferResponse = new Long(1);

    /**
     * Variable for storing the value of "IpPoolServiceUpTime".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.7.1.1".
     *
     * "Service Up time for IP-Pool Service"
     *
     */
    protected Long IpPoolServiceUpTime = new Long(1);

    /**
     * Variable for storing the value of "IpPoolServTotalDiscoverRequest".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.7.1.10".
     *
     * "Total No. of Access Request received by IP-Pool service"
     *
     */
    protected Long IpPoolServTotalDiscoverRequest = new Long(1);

    public IpPoolServMIBObjects() {
		// TODO Auto-generated constructor stub
	}
    
    /**
     * Constructor for the "IpPoolServMIBObjects" group.
     * If the group contains a table, the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public IpPoolServMIBObjects(SnmpMib myMib) {
        IpPoolNASClientStatisticsTable = new TableIpPoolNASClientStatisticsTable (myMib);
        IpPoolAAAClientStatisticsTable = new TableIpPoolAAAClientStatisticsTable (myMib);
    }


    /**
     * Constructor for the "IpPoolServMIBObjects" group.
     * If the group contains a table, the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public IpPoolServMIBObjects(SnmpMib myMib, MBeanServer server) {
        IpPoolNASClientStatisticsTable = new TableIpPoolNASClientStatisticsTable (myMib, server);
        IpPoolAAAClientStatisticsTable = new TableIpPoolAAAClientStatisticsTable (myMib, server);
    }

    /**
     * Getter for the "IpPoolServTotalInvalidRequest" variable.
     */
    public Long getIpPoolServTotalInvalidRequest() throws SnmpStatusException {
        return IpPoolServTotalInvalidRequest;
    }

    /**
     * Getter for the "IpPoolServTotalUnknownPacket" variable.
     */
    public Long getIpPoolServTotalUnknownPacket() throws SnmpStatusException {
        return IpPoolServTotalUnknownPacket;
    }

    /**
     * Getter for the "IpPoolServTotalDuplicateRequest" variable.
     */
    public Long getIpPoolServTotalDuplicateRequest() throws SnmpStatusException {
        return IpPoolServTotalDuplicateRequest;
    }

    /**
     * Getter for the "IpPoolServTotalRequestDropped" variable.
     */
    public Long getIpPoolServTotalRequestDropped() throws SnmpStatusException {
        return IpPoolServTotalRequestDropped;
    }

    /**
     * Getter for the "IpPoolServTotalUpdateRequest" variable.
     */
    public Long getIpPoolServTotalUpdateRequest() throws SnmpStatusException {
        return IpPoolServTotalUpdateRequest;
    }

    /**
     * Getter for the "IpPoolServTotalReleaseRequest" variable.
     */
    public Long getIpPoolServTotalReleaseRequest() throws SnmpStatusException {
        return IpPoolServTotalReleaseRequest;
    }

    /**
     * Getter for the "IpPoolServTotalResponses" variable.
     */
    public Long getIpPoolServTotalResponses() throws SnmpStatusException {
        return IpPoolServTotalResponses;
    }

    /**
     * Getter for the "IpPoolServTotalAllocationRequest" variable.
     */
    public Long getIpPoolServTotalAllocationRequest() throws SnmpStatusException {
        return IpPoolServTotalAllocationRequest;
    }

    /**
     * Getter for the "IpPoolServTotalRequest" variable.
     */
    public Long getIpPoolServTotalRequest() throws SnmpStatusException {
        return IpPoolServTotalRequest;
    }

    /**
     * Getter for the "IpPoolServiceResetTime" variable.
     */
    public Long getIpPoolServiceResetTime() throws SnmpStatusException {
        return IpPoolServiceResetTime;
    }

    /**
     * Getter for the "IpPoolServTotalDeclineResponse" variable.
     */
    public Long getIpPoolServTotalDeclineResponse() throws SnmpStatusException {
        return IpPoolServTotalDeclineResponse;
    }

    /**
     * Access the "IpPoolNASClientStatisticsTable" variable.
     */
    public TableIpPoolNASClientStatisticsTable accessIpPoolNASClientStatisticsTable() throws SnmpStatusException {
        return IpPoolNASClientStatisticsTable;
    }

    /**
     * Access the "IpPoolNASClientStatisticsTable" variable as a bean indexed property.
     */
    public IpPoolNASClientEntryMBean[] getIpPoolNASClientStatisticsTable() throws SnmpStatusException {
        return IpPoolNASClientStatisticsTable.getEntries();
    }

    /**
     * Getter for the "IpPoolServiceReset" variable.
     */
    public EnumIpPoolServiceReset getIpPoolServiceReset() throws SnmpStatusException {
        return IpPoolServiceReset;
    }

    /**
     * Setter for the "IpPoolServiceReset" variable.
     */
    public void setIpPoolServiceReset(EnumIpPoolServiceReset x) throws SnmpStatusException {
        IpPoolServiceReset = x;
    }

    /**
     * Checker for the "IpPoolServiceReset" variable.
     */
    public void checkIpPoolServiceReset(EnumIpPoolServiceReset x) throws SnmpStatusException {
        //
        // Add your own checking policy.
        //
    }

    /**
     * Access the "IpPoolAAAClientStatisticsTable" variable.
     */
    public TableIpPoolAAAClientStatisticsTable accessIpPoolAAAClientStatisticsTable() throws SnmpStatusException {
        return IpPoolAAAClientStatisticsTable;
    }

    /**
     * Access the "IpPoolAAAClientStatisticsTable" variable as a bean indexed property.
     */
    public IpPoolAAAClientEntryMBean[] getIpPoolAAAClientStatisticsTable() throws SnmpStatusException {
        return IpPoolAAAClientStatisticsTable.getEntries();
    }

    /**
     * Getter for the "IpPoolServTotalOfferResponse" variable.
     */
    public Long getIpPoolServTotalOfferResponse() throws SnmpStatusException {
        return IpPoolServTotalOfferResponse;
    }

    /**
     * Getter for the "IpPoolServiceUpTime" variable.
     */
    public Long getIpPoolServiceUpTime() throws SnmpStatusException {
        return IpPoolServiceUpTime;
    }

    /**
     * Getter for the "IpPoolServTotalDiscoverRequest" variable.
     */
    public Long getIpPoolServTotalDiscoverRequest() throws SnmpStatusException {
        return IpPoolServTotalDiscoverRequest;
    }

}
