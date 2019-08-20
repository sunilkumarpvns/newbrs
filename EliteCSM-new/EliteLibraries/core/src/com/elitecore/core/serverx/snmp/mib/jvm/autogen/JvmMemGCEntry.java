package com.elitecore.core.serverx.snmp.mib.jvm.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling JVM-MANAGEMENT-MIB.
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
 * The class is used for implementing the "JvmMemGCEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.42.2.145.3.163.1.1.2.101.1.
 */
public class JvmMemGCEntry implements JvmMemGCEntryMBean, Serializable {

    /**
     * Variable for storing the value of "JvmMemGCTimeMs".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.2.101.1.3".
     *
     * "The approximate accumulated collection elapsed time in
     * milliseconds, since the Java virtual machine has started.
     * This object is set to 0 if the collection elapsed time is
     * undefined for this collector.
     * 
     * See java.lang.management.GarbageCollectorMXBean.getCollectionTime()
     * "
     *
     */
    protected Long JvmMemGCTimeMs = new Long(0L);

    /**
     * Variable for storing the value of "JvmMemGCCount".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.2.101.1.2".
     *
     * "The total number of collections that have occurred,
     * as returned by GarbageCollectorMXBean.getCollectionCount().
     * 
     * If garbage collection statistics are not available, this
     * object is set to 0.
     * 
     * See java.lang.management.GarbageCollectorMXBean.getCollectionCount()
     * "
     *
     */
    protected Long JvmMemGCCount = new Long(1);

    /**
     * Variable for storing the value of "JvmMemManagerIndex".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.2.100.1.1".
     *
     * "An index opaquely computed by the agent and which uniquely
     * identifies a Memory Manager.
     * 
     * The jvmMemManagerIndex index is opaquely computed by the agent,
     * from e.g the hash code of the MemoryManager (or MemoryManager name).
     * The agent is responsible for allocating a free index when it needs
     * one (e.g. if two objects have the same hash, then it may increment
     * one of the values until the conflict is resolved). As a result a
     * manager must not depend on the value of that index across,
     * e.g. reboot of the agent, as this value is not guaranteed to
     * stay identical after the agent restarts.
     * "
     *
     */
    protected Integer JvmMemManagerIndex = new Integer(1);


    /**
     * Constructor for the "JvmMemGCEntry" group.
     */
    public JvmMemGCEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "JvmMemGCTimeMs" variable.
     */
    public Long getJvmMemGCTimeMs() throws SnmpStatusException {
        return JvmMemGCTimeMs;
    }

    /**
     * Getter for the "JvmMemGCCount" variable.
     */
    public Long getJvmMemGCCount() throws SnmpStatusException {
        return JvmMemGCCount;
    }

    /**
     * Getter for the "JvmMemManagerIndex" variable.
     */
    public Integer getJvmMemManagerIndex() throws SnmpStatusException {
        return JvmMemManagerIndex;
    }

}