package com.elitecore.core.serverx.snmp.mib.jvm.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling JVM-MANAGEMENT-MIB.
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
 * The class is used for implementing the "JvmMemory" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.42.2.145.3.163.1.1.2.
 */
public class JvmMemory implements JvmMemoryMBean, Serializable {

    /**
     * Variable for storing the value of "JvmMemMgrPoolRelTable".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.2.120".
     *
     * "The Memory Manager-Pool Relation Table shows the
     * Memory Manager / Memory Pool relations, as returned by
     * MemoryPoolMXBean.getMemoryManagerNames() and
     * MemoryManagerMXBean.getMemoryPoolNames().
     * This table imports the indexes from the jvmMemManagerTable table
     * and jvmMemPoolTable table. The jvmMemMgrRelManagerName and
     * jvmMemMgrRelPoolName objects are not actually necessary since
     * the indexes are self-sufficient to express the relationship -
     * but the names  will make the table more understandable when displayed
     * in a management console.
     * "
     *
     */
    protected TableJvmMemMgrPoolRelTable JvmMemMgrPoolRelTable;

    /**
     * Variable for storing the value of "JvmMemoryNonHeapMaxSize".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.2.23".
     *
     * "
     * Total maximum size of memory (in bytes) for all non heap memory pools.
     * 
     * See java.lang.management.MemoryMXBean.getNonHeapMemoryUsage().getMax()
     * "
     *
     */
    protected Long JvmMemoryNonHeapMaxSize = new Long(1);

    /**
     * Variable for storing the value of "JvmMemoryNonHeapCommitted".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.2.22".
     *
     * "
     * Total amount of memory (in bytes) committed by non heap memory pools.
     * 
     * See java.lang.management.MemoryMXBean.
     * getNonHeapMemoryUsage().getCommitted()
     * "
     *
     */
    protected Long JvmMemoryNonHeapCommitted = new Long(1);

    /**
     * Variable for storing the value of "JvmMemoryNonHeapUsed".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.2.21".
     *
     * "
     * Total amount of used memory (in bytes) from non heap memory pools.
     * 
     * See java.lang.management.MemoryMXBean.getNonHeapMemoryUsage().getUsed()
     * "
     *
     */
    protected Long JvmMemoryNonHeapUsed = new Long(1);

    /**
     * Variable for storing the value of "JvmMemPoolTable".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.2.110".
     *
     * "The Memory Pool Table contains the whole list of MemoryPools
     * as returned by ManagementFactory.getMemoryPoolMXBeans().
     * "
     *
     */
    protected TableJvmMemPoolTable JvmMemPoolTable;

    /**
     * Variable for storing the value of "JvmMemoryNonHeapInitSize".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.2.20".
     *
     * "
     * Total amount of memory (in bytes) that the Java virtual machine
     * initially requests from the operating system for memory management
     * for non heap memory pools.
     * 
     * See java.lang.management.MemoryMXBean.getNonHeapMemoryUsage().getInit()
     * "
     *
     */
    protected Long JvmMemoryNonHeapInitSize = new Long(1);

    /**
     * Variable for storing the value of "JvmMemoryHeapMaxSize".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.2.13".
     *
     * "
     * Total maximum size of memory (in bytes) for all heap memory pools.
     * 
     * See java.lang.management.MemoryMXBean.getHeapMemoryUsage().getMax()
     * "
     *
     */
    protected Long JvmMemoryHeapMaxSize = new Long(1);

    /**
     * Variable for storing the value of "JvmMemoryHeapCommitted".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.2.12".
     *
     * "
     * Total amount of memory (in bytes) committed by heap memory pools.
     * 
     * See java.lang.management.MemoryMXBean.getHeapMemoryUsage().
     * getCommitted()
     * "
     *
     */
    protected Long JvmMemoryHeapCommitted = new Long(1);

    /**
     * Variable for storing the value of "JvmMemoryGCCall".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.2.3".
     *
     * "This object makes it possible to remotelly trigger the
     * Garbage Collector in the JVM.
     * 
     * This object's syntax is an enumeration which defines:
     * 
     * * Two state values, that can be returned from a GET request:
     * 
     * unsupported(1): means that remote invocation of gc() is not
     * supported by the SNMP agent.
     * supported(2)  : means that remote invocation of gc() is supported
     * by the SNMP agent.
     * 
     * * One action value, that can be provided in a SET request to
     * trigger the garbage collector:
     * 
     * start(3)      : means that a manager wishes to trigger
     * garbage collection.
     * 
     * * Two result value, that will be returned in the response to a
     * SET request when remote invocation of gc is supported
     * by the SNMP agent:
     * 
     * started(4)    : means that garbage collection was
     * successfully triggered. It does not mean
     * however that the action was successfullly
     * completed: gc might still be running when
     * this value is returned.
     * failed(5)     : means that garbage collection couldn't be
     * triggered.
     * 
     * * If remote invocation is not supported by the SNMP agent, then
     * unsupported(1) will always be returned as a result of either
     * a GET request, or a SET request with start(3) as input value.
     * 
     * * If a SET request with anything but start(3) is received, then
     * the agent will return a wrongValue error.
     * 
     * See java.lang.management.MemoryMXBean.gc()
     * "
     *
     */
    protected EnumJvmMemoryGCCall JvmMemoryGCCall = new EnumJvmMemoryGCCall();

    /**
     * Variable for storing the value of "JvmMemoryHeapUsed".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.2.11".
     *
     * "
     * Total amount of used memory (in bytes) from heap memory pools.
     * 
     * See java.lang.management.MemoryMXBean.getHeapMemoryUsage().getUsed()
     * "
     *
     */
    protected Long JvmMemoryHeapUsed = new Long(1);

    /**
     * Variable for storing the value of "JvmMemoryGCVerboseLevel".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.2.2".
     *
     * "Enables or disables verbose output for the memory system.
     * The verbose output information and the output stream to which
     * the verbose information is emitted are implementation dependent.
     * Typically, a Java virtual machine implementation prints a
     * message whenever it frees memory at garbage collection.
     * 
     * verbose: if the verbose output is enabled,
     * silent:  otherwise.
     * 
     * See java.lang.management.MemoryMXBean.isVerbose(),
     * java.lang.management.MemoryMXBean.setVerbose()
     * "
     *
     */
    protected EnumJvmMemoryGCVerboseLevel JvmMemoryGCVerboseLevel = new EnumJvmMemoryGCVerboseLevel();

    /**
     * Variable for storing the value of "JvmMemGCTable".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.2.101".
     *
     * "The Garbage Collector table provides additional information
     * on those MemoryManagers which are also GarbageCollectors.
     * This table extends the  jvmMemManagerTable table. The index
     * used in the jvmMemGCTable table is imported from the
     * jvmMemManagerTable table. If a row from the jvmMemManagerTable
     * table is deleted, and if it has an extension in the jvmMemGCTable
     * table, then the extension row will also be deleted.
     * "
     *
     */
    protected TableJvmMemGCTable JvmMemGCTable;

    /**
     * Variable for storing the value of "JvmMemoryHeapInitSize".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.2.10".
     *
     * "
     * Total amount of memory (in bytes) that the Java virtual machine
     * initially requests from the operating system for memory management
     * for heap memory pools.
     * 
     * See java.lang.management.MemoryMXBean.getHeapMemoryUsage().getInit()
     * "
     *
     */
    protected Long JvmMemoryHeapInitSize = new Long(1);

    /**
     * Variable for storing the value of "JvmMemoryPendingFinalCount".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.2.1".
     *
     * "The approximate number objects that are pending for finalization.
     * 
     * See java.lang.management.MemoryMXBean.
     * getObjectPendingFinalizationCount()
     * "
     *
     */
    protected Long JvmMemoryPendingFinalCount = new Long(1);

    /**
     * Variable for storing the value of "JvmMemManagerTable".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.2.100".
     *
     * "The Memory Manager Table contains the whole list of Memory
     * Managers  as returned by ManagementFactory.getMemoryManagerMXBeans().
     * 
     * When a MemoryManagerMXBean object is an instance of
     * GarbageCollectorMXBean, then additional information specific to
     * the GarbageCollectorMXBean class will be found in the
     * jvmGCTable, at the same index.
     * 
     * Relationships between MemoryManagers and MemoryPools are shown
     * by the Memory Manager-Pool Relation table (jvmMemMgrPoolRelTable).
     * "
     *
     */
    protected TableJvmMemManagerTable JvmMemManagerTable;


    /**
     * Constructor for the "JvmMemory" group.
     * If the group contains a table, the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public JvmMemory(SnmpMib myMib) {
        JvmMemMgrPoolRelTable = new TableJvmMemMgrPoolRelTable (myMib);
        JvmMemPoolTable = new TableJvmMemPoolTable (myMib);
        JvmMemGCTable = new TableJvmMemGCTable (myMib);
        JvmMemManagerTable = new TableJvmMemManagerTable (myMib);
    }


    /**
     * Constructor for the "JvmMemory" group.
     * If the group contains a table, the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public JvmMemory(SnmpMib myMib, MBeanServer server) {
        JvmMemMgrPoolRelTable = new TableJvmMemMgrPoolRelTable (myMib, server);
        JvmMemPoolTable = new TableJvmMemPoolTable (myMib, server);
        JvmMemGCTable = new TableJvmMemGCTable (myMib, server);
        JvmMemManagerTable = new TableJvmMemManagerTable (myMib, server);
    }

    /**
     * Access the "JvmMemMgrPoolRelTable" variable.
     */
    public TableJvmMemMgrPoolRelTable accessJvmMemMgrPoolRelTable() throws SnmpStatusException {
        return JvmMemMgrPoolRelTable;
    }

    /**
     * Access the "JvmMemMgrPoolRelTable" variable as a bean indexed property.
     */
    public JvmMemMgrPoolRelEntryMBean[] getJvmMemMgrPoolRelTable() throws SnmpStatusException {
        return JvmMemMgrPoolRelTable.getEntries();
    }

    /**
     * Getter for the "JvmMemoryNonHeapMaxSize" variable.
     */
    public Long getJvmMemoryNonHeapMaxSize() throws SnmpStatusException {
        return JvmMemoryNonHeapMaxSize;
    }

    /**
     * Getter for the "JvmMemoryNonHeapCommitted" variable.
     */
    public Long getJvmMemoryNonHeapCommitted() throws SnmpStatusException {
        return JvmMemoryNonHeapCommitted;
    }

    /**
     * Getter for the "JvmMemoryNonHeapUsed" variable.
     */
    public Long getJvmMemoryNonHeapUsed() throws SnmpStatusException {
        return JvmMemoryNonHeapUsed;
    }

    /**
     * Access the "JvmMemPoolTable" variable.
     */
    public TableJvmMemPoolTable accessJvmMemPoolTable() throws SnmpStatusException {
        return JvmMemPoolTable;
    }

    /**
     * Access the "JvmMemPoolTable" variable as a bean indexed property.
     */
    public JvmMemPoolEntryMBean[] getJvmMemPoolTable() throws SnmpStatusException {
        return JvmMemPoolTable.getEntries();
    }

    /**
     * Getter for the "JvmMemoryNonHeapInitSize" variable.
     */
    public Long getJvmMemoryNonHeapInitSize() throws SnmpStatusException {
        return JvmMemoryNonHeapInitSize;
    }

    /**
     * Getter for the "JvmMemoryHeapMaxSize" variable.
     */
    public Long getJvmMemoryHeapMaxSize() throws SnmpStatusException {
        return JvmMemoryHeapMaxSize;
    }

    /**
     * Getter for the "JvmMemoryHeapCommitted" variable.
     */
    public Long getJvmMemoryHeapCommitted() throws SnmpStatusException {
        return JvmMemoryHeapCommitted;
    }

    /**
     * Getter for the "JvmMemoryGCCall" variable.
     */
    public EnumJvmMemoryGCCall getJvmMemoryGCCall() throws SnmpStatusException {
        return JvmMemoryGCCall;
    }

    /**
     * Setter for the "JvmMemoryGCCall" variable.
     */
    public void setJvmMemoryGCCall(EnumJvmMemoryGCCall x) throws SnmpStatusException {
        JvmMemoryGCCall = x;
    }

    /**
     * Checker for the "JvmMemoryGCCall" variable.
     */
    public void checkJvmMemoryGCCall(EnumJvmMemoryGCCall x) throws SnmpStatusException {
        //
        // Add your own checking policy.
        //
    }

    /**
     * Getter for the "JvmMemoryHeapUsed" variable.
     */
    public Long getJvmMemoryHeapUsed() throws SnmpStatusException {
        return JvmMemoryHeapUsed;
    }

    /**
     * Getter for the "JvmMemoryGCVerboseLevel" variable.
     */
    public EnumJvmMemoryGCVerboseLevel getJvmMemoryGCVerboseLevel() throws SnmpStatusException {
        return JvmMemoryGCVerboseLevel;
    }

    /**
     * Setter for the "JvmMemoryGCVerboseLevel" variable.
     */
    public void setJvmMemoryGCVerboseLevel(EnumJvmMemoryGCVerboseLevel x) throws SnmpStatusException {
        JvmMemoryGCVerboseLevel = x;
    }

    /**
     * Checker for the "JvmMemoryGCVerboseLevel" variable.
     */
    public void checkJvmMemoryGCVerboseLevel(EnumJvmMemoryGCVerboseLevel x) throws SnmpStatusException {
        //
        // Add your own checking policy.
        //
    }

    /**
     * Access the "JvmMemGCTable" variable.
     */
    public TableJvmMemGCTable accessJvmMemGCTable() throws SnmpStatusException {
        return JvmMemGCTable;
    }

    /**
     * Access the "JvmMemGCTable" variable as a bean indexed property.
     */
    public JvmMemGCEntryMBean[] getJvmMemGCTable() throws SnmpStatusException {
        return JvmMemGCTable.getEntries();
    }

    /**
     * Getter for the "JvmMemoryHeapInitSize" variable.
     */
    public Long getJvmMemoryHeapInitSize() throws SnmpStatusException {
        return JvmMemoryHeapInitSize;
    }

    /**
     * Getter for the "JvmMemoryPendingFinalCount" variable.
     */
    public Long getJvmMemoryPendingFinalCount() throws SnmpStatusException {
        return JvmMemoryPendingFinalCount;
    }

    /**
     * Access the "JvmMemManagerTable" variable.
     */
    public TableJvmMemManagerTable accessJvmMemManagerTable() throws SnmpStatusException {
        return JvmMemManagerTable;
    }

    /**
     * Access the "JvmMemManagerTable" variable as a bean indexed property.
     */
    public JvmMemManagerEntryMBean[] getJvmMemManagerTable() throws SnmpStatusException {
        return JvmMemManagerTable.getEntries();
    }

}