package com.elitecore.diameterapi.mibs.base.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-BASE-PROTOCOL-MIB in standard metadata mode.
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
 * The class is used for implementing the "DbpPeerTable" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.119.1.3.1.
 */
public class DbpPeerTableMeta extends SnmpMibTable implements Serializable {

    /**
     * Constructor for the table. Initialize metadata for "DbpPeerTableMeta".
     * The reference on the MBean server is updated so the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public DbpPeerTableMeta(SnmpMib myMib, SnmpStandardObjectServer objserv) {
        super(myMib);
        objectserver = objserv;
    }


    /**
     * Factory method for "DbpPeerEntry" entry metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param snmpEntryName Name of the SNMP Entry object (conceptual row) ("DbpPeerEntry")
     * @param tableName Name of the table in which the entries are registered ("DbpPeerTable")
     * @param mib The SnmpMib object in which this table is registered
     * @param server MBeanServer for this table entries (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "DbpPeerEntry" conceptual row (DbpPeerEntryMeta)
     * 
     **/
    protected DbpPeerEntryMeta createDbpPeerEntryMetaNode(String snmpEntryName, String tableName, SnmpMib mib, MBeanServer server)  {
        return new DbpPeerEntryMeta(mib, objectserver);
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
        node = createDbpPeerEntryMetaNode("DbpPeerEntry", "DbpPeerTable", mib, server);
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
        if (! (entry instanceof DbpPeerEntryMBean) )
            throw new ClassCastException("Entries for Table \"" + 
                           "DbpPeerTable" + "\" must implement the \"" + 
                           "DbpPeerEntryMBean" + "\" interface.");
        super.addEntry(rowOid, objname, entry);
    }


    // ------------------------------------------------------------
    // 
    // Implements the "isRowStatus" method defined in "SnmpMibTable".
    // See the "SnmpMibTable" Javadoc API for more details.
    // 
    // ------------------------------------------------------------


    public  boolean isRowStatus(SnmpOid rowOid, long var, Object userData)  {
        return (var == 9);
    }


    // ------------------------------------------------------------
    // 
    // Implements the "hasRowStatus" method defined in "SnmpMibTable".
    // See the "SnmpMibTable" Javadoc API for more details.
    // 
    // ------------------------------------------------------------


    public  boolean hasRowStatus()  {
        return true;
    }


    // ------------------------------------------------------------
    // 
    // Implements the "setRowStatus" method defined in "SnmpMibTable".
    // See the "SnmpMibTable" Javadoc API for more details.
    // 
    // ------------------------------------------------------------


    public  SnmpValue setRowStatus(SnmpOid rowOid, int status, Object userData)
            throws SnmpStatusException {
        DbpPeerEntryMBean entry = (DbpPeerEntryMBean) getEntry(rowOid);
        synchronized (this) {
            node.setInstance(entry);
            return node.setRowStatus(status, userData);
        }
    }


    // ------------------------------------------------------------
    // 
    // Implements the "isRowReady" method defined in "SnmpMibTable".
    // See the "SnmpMibTable" Javadoc API for more details.
    // 
    // ------------------------------------------------------------


    public  boolean isRowReady(SnmpOid rowOid, Object userData)
            throws SnmpStatusException {
        DbpPeerEntryMBean entry = (DbpPeerEntryMBean) getEntry(rowOid);
        synchronized (this) {
            node.setInstance(entry);
            return node.isRowReady(userData);
        }
    }


    // ------------------------------------------------------------
    // 
    // Implements the "mapRowStatus" method defined in "SnmpMibTable".
    // See the "SnmpMibTable" Javadoc API for more details.
    // 
    // ------------------------------------------------------------


    public  int mapRowStatus(SnmpOid rowOid, SnmpVarBind vbstatus, Object userData)
            throws SnmpStatusException {
        DbpPeerEntryMBean entry = null;
        try  {
            entry = (DbpPeerEntryMBean) getEntry(rowOid);
        } catch (SnmpStatusException x)  {
            entry = null;
        }
        synchronized (this) {
            node.setInstance(entry);
            return node.mapRowStatus(vbstatus, userData);
        }
    }


    public  int getRowStatus(SnmpOid rowOid, Object userData)
            throws SnmpStatusException {
        DbpPeerEntryMBean entry = (DbpPeerEntryMBean) getEntry(rowOid);
        synchronized (this) {
            node.setInstance(entry);
            return node.getRowStatus(userData);
        }
    }


    // ------------------------------------------------------------
    // 
    // Implements the "get" method defined in "SnmpMibTable".
    // See the "SnmpMibTable" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void get(SnmpMibSubRequest req, SnmpOid rowOid, int depth)
        throws SnmpStatusException {
        DbpPeerEntryMBean entry = (DbpPeerEntryMBean) getEntry(rowOid);
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

        DbpPeerEntryMBean entry = (DbpPeerEntryMBean) getEntry(rowOid);
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

        DbpPeerEntryMBean entry = (DbpPeerEntryMBean) getEntry(rowOid);
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
            DbpPeerEntryMBean entry = (DbpPeerEntryMBean) getEntry(rowOid);
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
    private DbpPeerEntryMeta node;

    /**
     * Reference to the object server.
     */
    protected SnmpStandardObjectServer objectserver;

}