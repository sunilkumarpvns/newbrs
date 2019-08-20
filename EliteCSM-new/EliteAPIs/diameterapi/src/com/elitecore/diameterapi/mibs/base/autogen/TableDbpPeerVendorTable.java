package com.elitecore.diameterapi.mibs.base.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-BASE-PROTOCOL-MIB.
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
import com.sun.management.snmp.agent.SnmpTableEntryFactory;
import com.sun.management.snmp.agent.SnmpTableCallbackHandler;
import com.sun.management.snmp.agent.SnmpTableSupport;

/**
 * The class is used for implementing the "DbpPeerVendorTable" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.119.1.3.4.
 */
public class TableDbpPeerVendorTable extends SnmpTableSupport implements Serializable {

    /**
     * Constructor for the table. Initialize metadata for "TableDbpPeerVendorTable".
     * The reference on the MBean server is not updated so the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public TableDbpPeerVendorTable(SnmpMib myMib) {
        super(myMib);
    }

    /**
     * Constructor for the table. Initialize metadata for "TableDbpPeerVendorTable".
     * The reference on the MBean server is updated so the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public TableDbpPeerVendorTable(SnmpMib myMib, MBeanServer server) {
        this(myMib);
        this.server = server;
    }


    // ------------------------------------------------------------
    // 
    // Implements the "createNewEntry" method defined in "SnmpTableSupport".
    // See the "SnmpTableSupport" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void createNewEntry(SnmpMibSubRequest req, SnmpOid rowOid,
                 int depth, SnmpMibTable meta)
        throws SnmpStatusException {
        final SnmpIndex index = buildSnmpIndex(rowOid);
        final Vector v = index.getComponents();
        SnmpOid oid;
        try  {
            final SnmpOid oid0 = (SnmpOid) v.elementAt(0);
            final SnmpOid oid1 = (SnmpOid) v.elementAt(1);
            ObjectName objname = null;
            if (server != null)
                objname = buildNameFromIndex( index );

            // Note that when using standard metadata,
            // the returned object must implement the "DbpPeerVendorEntryMBean"
            // interface.
            //
            final Object entry =
                 createDbpPeerVendorEntryMBean(req, rowOid, depth, objname, meta,
                    oid0.toLong(),
                    oid1.toLong());
            if (server != null) {
                server.registerMBean(entry, objname);
            }
            meta.addEntry(rowOid,objname,entry);
        } catch(SnmpStatusException e) {
            throw e;
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new SnmpStatusException(SnmpStatusException.snmpRspWrongValue);
        } catch(Exception e) {
            throw new SnmpStatusException(e.getMessage());
        }
    }



    // ------------------------------------------------------------
    // 
    // Implements the "getRegisteredTableMeta" method defined in "SnmpTableSupport".
    // See the "SnmpTableSupport" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    protected SnmpMibTable getRegisteredTableMeta(SnmpMib mib)  {
        return mib.getRegisteredTableMeta("DbpPeerVendorTable");
    }


    // ------------------------------------------------------------
    // 
    // Implements the "removeEntryCb" method defined in "SnmpTableSupport".
    // See the "SnmpTableSupport" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void removeEntryCb(int pos, SnmpOid row, ObjectName name,
                Object entry, SnmpMibTable meta)
            throws SnmpStatusException {
        try  {
            super.removeEntryCb(pos,row,name,entry,meta);
            if (server != null && name != null)
                server.unregisterMBean(name);
        } catch (Exception x) { }
    }


    /**
     * Add a new entry to the table.
     * 
     * If the associated metadata requires ObjectNames
     * a new ObjectName will be generated using "buildNameFromIndex".
     * 
     * This method calls "addEntry" from "SnmpTableSupport".
     * See the "SnmpTableSupport" Javadoc API for more details.
     * 
     **/

    public synchronized void addEntry(DbpPeerVendorEntryMBean entry)
        throws SnmpStatusException {
        SnmpIndex index = buildSnmpIndex(entry);
        super.addEntry(index, (Object) entry);
    }


    /**
     * Add a new entry to the table.
     * 
     * This method calls "addEntry" from "SnmpTableSupport".
     * See the "SnmpTableSupport" Javadoc API for more details.
     * 
     **/

    public synchronized void addEntry(DbpPeerVendorEntryMBean entry, ObjectName name)
        throws SnmpStatusException {
        SnmpIndex index = buildSnmpIndex(entry);
        super.addEntry(index, name, (Object) entry);
    }


    /**
     * Return the entries stored in the table.
     * 
     * This method calls "getBasicEntries" from "SnmpTableSupport".
     * See the "SnmpTableSupport" Javadoc API for more details.
     * 
     **/

    public synchronized DbpPeerVendorEntryMBean[] getEntries() {
        Object[] array = getBasicEntries();
        DbpPeerVendorEntryMBean[] result = new DbpPeerVendorEntryMBean[array.length];
        java.lang.System.arraycopy(array,0, result,0, array.length);
        return result;
    }


    /**
     * Remove the specified entry from the table.
     * 
     * This method calls "removeEntry" from "SnmpTableSupport".
     * See the "SnmpTableSupport" Javadoc API for more details.
     * 
     **/

    public void removeEntry(DbpPeerVendorEntryMBean entry)
        throws SnmpStatusException {
        SnmpIndex index = buildSnmpIndex(entry);
        super.removeEntry(index, entry) ;
    }


    // ------------------------------------------------------------
    // 
    // Implements the "buildNameFromIndex" method defined in "SnmpTableSupport".
    // See the "SnmpTableSupport" Javadoc API for more details.
    // 
    // ------------------------------------------------------------


    public ObjectName buildNameFromIndex(SnmpIndex index)
        throws SnmpStatusException {
        Vector v = index.getComponents();
        SnmpOid oid;
        try  {
            oid = (SnmpOid) v.elementAt(0);
            String _keyDbpPeerIndex = oid.toLong().toString();
            oid = (SnmpOid) v.elementAt(1);
            String _keyDbpPeerVendorIndex = oid.toLong().toString();
            return new ObjectName("TableDbpPeerVendorTable:name=com.elitecore.diameterapi.mibs.base.autogen.DbpPeerVendorEntry" + ",DbpPeerIndex=" + _keyDbpPeerIndex + ",DbpPeerVendorIndex=" + _keyDbpPeerVendorIndex);
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new SnmpStatusException(SnmpStatusException.snmpRspWrongValue);
        } catch(Exception e) {
            throw new SnmpStatusException(e.getMessage());
        }
    }

    /**
     * Build index for "DbpPeerVendorEntry".
     */
    public SnmpIndex buildSnmpIndex(DbpPeerVendorEntryMBean entry)
        throws SnmpStatusException {
        SnmpOid[] oids = new SnmpOid[2];
        SnmpValue val = null;
        val = new SnmpGauge(entry.getDbpPeerIndex());
        oids[0] = val.toOid();
        val = new SnmpGauge(entry.getDbpPeerVendorIndex());
        oids[1] = val.toOid();
        return new SnmpIndex(oids);
    }

    /**
     * Build index for "DbpPeerVendorEntry".
     */
    public SnmpOid buildOidFromIndex(SnmpIndex index)
        throws SnmpStatusException {
        SnmpOid oid = new SnmpOid();
        if (index.getNbComponents() != 2)
            throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
        try {
            Vector v = index.getComponents();
            SnmpGauge.appendToOid((SnmpOid)v.elementAt(0), oid);
            SnmpGauge.appendToOid((SnmpOid)v.elementAt(1), oid);
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
        }
        return oid;
    }

    /**
     * Build index for "DbpPeerVendorEntry".
     */
    public SnmpOid buildOidFromIndexVal(Long aDbpPeerIndex, Long aDbpPeerVendorIndex)
        throws SnmpStatusException  {
        SnmpOid oid = new SnmpOid();
        try {
            SnmpGauge.appendToOid(new SnmpGauge(aDbpPeerIndex).toOid(), oid);
            SnmpGauge.appendToOid(new SnmpGauge(aDbpPeerVendorIndex).toOid(), oid);
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
        }
        return oid;
    }

    /**
     * Build index for "DbpPeerVendorEntry".
     */
    public SnmpIndex buildSnmpIndex(long[] index, int start)
        throws SnmpStatusException {
        SnmpOid[] oids = new SnmpOid[2];
        int pos = start;
        oids[0] = SnmpGauge.toOid(index, pos);
        pos = SnmpGauge.nextOid(index, pos);
        oids[1] = SnmpGauge.toOid(index, pos);
        return new SnmpIndex(oids);
    }


    /**
     * Factory method for "DbpPeerVendorEntry" entry MBean class.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @return An instance of the MBean class generated for the
     *         "DbpPeerVendorEntry" conceptual row.
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "DbpPeerVendorEntryMBean"
     * interface.
     */

    public Object createDbpPeerVendorEntryMBean(SnmpMibSubRequest req,
                SnmpOid rowOid, int depth, ObjectName entryObjName,
                SnmpMibTable meta, Long  aDbpPeerIndex, Long  aDbpPeerVendorIndex)
            throws SnmpStatusException  {

        // Note that when using standard metadata,
        // the returned object must implement the "DbpPeerVendorEntryMBean"
        // interface.
        //
        DbpPeerVendorEntry entry = new DbpPeerVendorEntry(theMib);
        entry.DbpPeerIndex = aDbpPeerIndex;
        entry.DbpPeerVendorIndex = aDbpPeerVendorIndex;
        return entry;
    }


    /**
     * Reference to the MBean server.
     */
    protected MBeanServer server;

}
