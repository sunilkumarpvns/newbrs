package com.elitecore.core.imdg.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling IMDG-MIB.
//

// java imports
//
import java.io.Serializable;

// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

// jdmk imports
//
import com.sun.management.snmp.agent.SnmpMib;

/**
 * The class is used for implementing the "MapDetailEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.6.10.1.1.
 */
public class MapDetailEntry implements MapDetailEntryMBean, Serializable {

    /**
     * Variable for storing the value of "TotalGetOperations".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.10.1.1.9".
     */
    protected Long TotalGetOperations = new Long(1);

    /**
     * Variable for storing the value of "TotalPutOperations".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.10.1.1.8".
     */
    protected Long TotalPutOperations = new Long(1);

    /**
     * Variable for storing the value of "NoOfLockedSessions".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.10.1.1.7".
     */
    protected Long NoOfLockedSessions = new Long(1);

    /**
     * Variable for storing the value of "MemoryUsedForBackUp".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.10.1.1.6".
     */
    protected Long MemoryUsedForBackUp = new Long(1);

    /**
     * Variable for storing the value of "MemoryUsedbyMap".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.10.1.1.5".
     */
    protected Long MemoryUsedbyMap = new Long(1);

    /**
     * Variable for storing the value of "AvgRemoveLatency".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.10.1.1.13".
     */
    protected Long AvgRemoveLatency = new Long(1);

    /**
     * Variable for storing the value of "BackupSessions".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.10.1.1.4".
     */
    protected Long BackupSessions = new Long(1);

    /**
     * Variable for storing the value of "AvgGetLatency".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.10.1.1.12".
     */
    protected Long AvgGetLatency = new Long(1);

    /**
     * Variable for storing the value of "ActiveSessions".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.10.1.1.3".
     */
    protected Long ActiveSessions = new Long(1);

    /**
     * Variable for storing the value of "AvgPutLatency".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.10.1.1.11".
     */
    protected Long AvgPutLatency = new Long(1);

    /**
     * Variable for storing the value of "MapName".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.10.1.1.2".
     */
    protected String MapName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "TotalRemoveOperations".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.10.1.1.10".
     */
    protected Long TotalRemoveOperations = new Long(1);

    /**
     * Variable for storing the value of "MapIndex".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.10.1.1.1".
     */
    protected Long MapIndex = new Long(1);


    /**
     * Constructor for the "MapDetailEntry" group.
     */
    public MapDetailEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "TotalGetOperations" variable.
     */
    public Long getTotalGetOperations() throws SnmpStatusException {
        return TotalGetOperations;
    }

    /**
     * Getter for the "TotalPutOperations" variable.
     */
    public Long getTotalPutOperations() throws SnmpStatusException {
        return TotalPutOperations;
    }

    /**
     * Getter for the "NoOfLockedSessions" variable.
     */
    public Long getNoOfLockedSessions() throws SnmpStatusException {
        return NoOfLockedSessions;
    }

    /**
     * Getter for the "MemoryUsedForBackUp" variable.
     */
    public Long getMemoryUsedForBackUp() throws SnmpStatusException {
        return MemoryUsedForBackUp;
    }

    /**
     * Getter for the "MemoryUsedbyMap" variable.
     */
    public Long getMemoryUsedbyMap() throws SnmpStatusException {
        return MemoryUsedbyMap;
    }

    /**
     * Getter for the "AvgRemoveLatency" variable.
     */
    public Long getAvgRemoveLatency() throws SnmpStatusException {
        return AvgRemoveLatency;
    }

    /**
     * Getter for the "BackupSessions" variable.
     */
    public Long getBackupSessions() throws SnmpStatusException {
        return BackupSessions;
    }

    /**
     * Getter for the "AvgGetLatency" variable.
     */
    public Long getAvgGetLatency() throws SnmpStatusException {
        return AvgGetLatency;
    }

    /**
     * Getter for the "ActiveSessions" variable.
     */
    public Long getActiveSessions() throws SnmpStatusException {
        return ActiveSessions;
    }

    /**
     * Getter for the "AvgPutLatency" variable.
     */
    public Long getAvgPutLatency() throws SnmpStatusException {
        return AvgPutLatency;
    }

    /**
     * Getter for the "MapName" variable.
     */
    public String getMapName() throws SnmpStatusException {
        return MapName;
    }

    /**
     * Getter for the "TotalRemoveOperations" variable.
     */
    public Long getTotalRemoveOperations() throws SnmpStatusException {
        return TotalRemoveOperations;
    }

    /**
     * Getter for the "MapIndex" variable.
     */
    public Long getMapIndex() throws SnmpStatusException {
        return MapIndex;
    }

}
