package com.elitecore.core.serverx.snmp.mib.jvm.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling JVM-MANAGEMENT-MIB in standard metadata mode.
//

// java imports
//
import java.io.Serializable;
import java.util.Vector;

// jmx imports
//
import javax.management.MBeanServer;
import javax.management.ObjectName;
import com.sun.management.snmp.SnmpCounter;
import com.sun.management.snmp.SnmpCounter64;
import com.sun.management.snmp.SnmpGauge;
import com.sun.management.snmp.SnmpInt;
import com.sun.management.snmp.SnmpUnsignedInt;
import com.sun.management.snmp.SnmpIpAddress;
import com.sun.management.snmp.SnmpTimeticks;
import com.sun.management.snmp.SnmpOpaque;
import com.sun.management.snmp.SnmpString;
import com.sun.management.snmp.SnmpStringFixed;
import com.sun.management.snmp.SnmpOid;
import com.sun.management.snmp.SnmpNull;
import com.sun.management.snmp.SnmpValue;
import com.sun.management.snmp.SnmpVarBind;
import com.sun.management.snmp.SnmpStatusException;

// jdmk imports
//
import com.sun.management.snmp.agent.SnmpIndex;
import com.sun.management.snmp.agent.SnmpMib;
import com.sun.management.snmp.agent.SnmpMibTable;
import com.sun.management.snmp.agent.SnmpMibSubRequest;
import com.sun.management.snmp.agent.SnmpStandardObjectServer;

/**
 * The class is used for implementing the "JvmRTBootClassPathTable" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.42.2.145.3.163.1.1.4.21.
 */
public class JvmRTBootClassPathTableMeta extends SnmpMibTable implements Serializable {

    /**
     * Constructor for the table. Initialize metadata for "JvmRTBootClassPathTableMeta".
     * The reference on the MBean server is updated so the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public JvmRTBootClassPathTableMeta(SnmpMib myMib, SnmpStandardObjectServer objserv) {
        super(myMib);
        objectserver = objserv;
    }


    /**
     * Factory method for "JvmRTBootClassPathEntry" entry metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param snmpEntryName Name of the SNMP Entry object (conceptual row) ("JvmRTBootClassPathEntry")
     * @param tableName Name of the table in which the entries are registered ("JvmRTBootClassPathTable")
     * @param mib The SnmpMib object in which this table is registered
     * @param server MBeanServer for this table entries (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "JvmRTBootClassPathEntry" conceptual row (JvmRTBootClassPathEntryMeta)
     * 
     **/
    protected JvmRTBootClassPathEntryMeta createJvmRTBootClassPathEntryMetaNode(String snmpEntryName, String tableName, SnmpMib mib, MBeanServer server)  {
        return new JvmRTBootClassPathEntryMeta(mib, objectserver);
    }


    // ------------------------------------------------------------
    // 
    // Implements the "createNewEntry" method defined in "SnmpMibTable".
    // See the "SnmpMibTable" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void createNewEntry(SnmpMibSubRequest req, SnmpOid rowOid, int depth)
        throws SnmpStatusException {
        if (factory != null)
            factory.createNewEntry(req, rowOid, depth, this);
        else
            throw new SnmpStatusException(
                SnmpStatusException.snmpRspNoAccess);
    }



    // ------------------------------------------------------------
    // 
    // Implements the "isRegistrationRequired" method defined in "SnmpMibTable".
    // See the "SnmpMibTable" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public boolean isRegistrationRequired()  {
        return false;
    }



    public void registerEntryNode(SnmpMib mib, MBeanServer server)  {
        node = createJvmRTBootClassPathEntryMetaNode("JvmRTBootClassPathEntry", "JvmRTBootClassPathTable", mib, server);
    }


    // ------------------------------------------------------------
    // 
    // Implements the "addEntry" method defined in "SnmpMibTable".
    // See the "SnmpMibTable" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public synchronized void addEntry(SnmpOid rowOid, ObjectName objname,
                 Object entry)
        throws SnmpStatusException {
        if (! (entry instanceof JvmRTBootClassPathEntryMBean) )
            throw new ClassCastException("Entries for Table \"" + 
                           "JvmRTBootClassPathTable" + "\" must implement the \"" + 
                           "JvmRTBootClassPathEntryMBean" + "\" interface.");
        super.addEntry(rowOid, objname, entry);
    }


    // ------------------------------------------------------------
    // 
    // Implements the "get" method defined in "SnmpMibTable".
    // See the "SnmpMibTable" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void get(SnmpMibSubRequest req, SnmpOid rowOid, int depth)
        throws SnmpStatusException {
        JvmRTBootClassPathEntryMBean entry = (JvmRTBootClassPathEntryMBean) getEntry(rowOid);
        synchronized (this) {
            node.setInstance(entry);
            node.get(req,depth);
        }
    }

    // ------------------------------------------------------------
    // 
    // Implements the "set" method defined in "SnmpMibTable".
    // See the "SnmpMibTable" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void set(SnmpMibSubRequest req, SnmpOid rowOid, int depth)
        throws SnmpStatusException {
        if (req.getSize() == 0) return;

        JvmRTBootClassPathEntryMBean entry = (JvmRTBootClassPathEntryMBean) getEntry(rowOid);
        synchronized (this) {
            node.setInstance(entry);
            node.set(req,depth);
        }
    }

    // ------------------------------------------------------------
    // 
    // Implements the "check" method defined in "SnmpMibTable".
    // See the "SnmpMibTable" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void check(SnmpMibSubRequest req, SnmpOid rowOid, int depth)
        throws SnmpStatusException {
        if (req.getSize() == 0) return;

        JvmRTBootClassPathEntryMBean entry = (JvmRTBootClassPathEntryMBean) getEntry(rowOid);
        synchronized (this) {
            node.setInstance(entry);
            node.check(req,depth);
        }
    }

    /**
     * check that the given "var" identifies a columnar object.
     */
    public void validateVarEntryId( SnmpOid rowOid, long var, Object data )
        throws SnmpStatusException {
        node.validateVarId(var, data);
    }

    /**
     * Returns true if "var" identifies a readable scalar object.
     */
    public boolean isReadableEntryId( SnmpOid rowOid, long var, Object data )
        throws SnmpStatusException {
        return node.isReadable(var);
    }

    /**
     * Returns the arc of the next columnar object following "var".
     */
    public long getNextVarEntryId( SnmpOid rowOid, long var, Object data )
        throws SnmpStatusException {
        long nextvar = node.getNextVarId(var, data);
        while (!isReadableEntryId(rowOid, nextvar, data))
            nextvar = node.getNextVarId(nextvar, data);
        return nextvar;
    }

    // ------------------------------------------------------------
    // 
    // Implements the "skipEntryVariable" method defined in "SnmpMibTable".
    // See the "SnmpMibTable" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public boolean skipEntryVariable( SnmpOid rowOid, long var, Object data, int pduVersion) {
        try {
            JvmRTBootClassPathEntryMBean entry = (JvmRTBootClassPathEntryMBean) getEntry(rowOid);
            synchronized (this) {
                node.setInstance(entry);
                return node.skipVariable(var, data, pduVersion);
            }
        } catch (SnmpStatusException x) {
            return false;
        }
    }


    /**
     * Reference to the entry metadata.
     */
    private JvmRTBootClassPathEntryMeta node;

    /**
     * Reference to the object server.
     */
    protected SnmpStandardObjectServer objectserver;

}
