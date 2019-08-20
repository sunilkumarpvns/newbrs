package com.elitecore.core.serverx.snmp.mib.mib2.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RFC1213-MIB.
//

// java imports
//
import java.io.Serializable;
import java.util.Vector;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.sun.management.snmp.SnmpInt;
import com.sun.management.snmp.SnmpIpAddress;
import com.sun.management.snmp.SnmpOid;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.SnmpValue;
import com.sun.management.snmp.agent.SnmpIndex;
import com.sun.management.snmp.agent.SnmpMib;
import com.sun.management.snmp.agent.SnmpMibSubRequest;
import com.sun.management.snmp.agent.SnmpMibTable;
import com.sun.management.snmp.agent.SnmpTableSupport;

/**
 * The class is used for implementing the "IpNetToMediaTable" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.4.22.
 */
public class TableIpNetToMediaTable extends SnmpTableSupport implements Serializable {

    /**
     * Constructor for the table. Initialize metadata for "TableIpNetToMediaTable".
     * The reference on the MBean server is not updated so the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public TableIpNetToMediaTable(SnmpMib myMib) {
        super(myMib);
    }

    /**
     * Constructor for the table. Initialize metadata for "TableIpNetToMediaTable".
     * The reference on the MBean server is updated so the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public TableIpNetToMediaTable(SnmpMib myMib, MBeanServer server) {
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
            // the returned object must implement the "IpNetToMediaEntryMBean"
            // interface.
            //
            final Object entry =
                 createIpNetToMediaEntryMBean(req, rowOid, depth, objname, meta,
                    oid0.toInteger(),
                    oid1.toString());
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
        return mib.getRegisteredTableMeta("IpNetToMediaTable");
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

    public synchronized void addEntry(IpNetToMediaEntryMBean entry)
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

    public synchronized void addEntry(IpNetToMediaEntryMBean entry, ObjectName name)
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

    public synchronized IpNetToMediaEntryMBean[] getEntries() {
        Object[] array = getBasicEntries();
        IpNetToMediaEntryMBean[] result = new IpNetToMediaEntryMBean[array.length];
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

    public void removeEntry(IpNetToMediaEntryMBean entry)
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
            String _keyIpNetToMediaIfIndex = oid.toInteger().toString();
            oid = (SnmpOid) v.elementAt(1);
            String _keyIpNetToMediaNetAddress = oid.toString().toString();
            return new ObjectName("TableIpNetToMediaTable:name=com.elitecore.core.serverx.snmp.mib.mib2.autogen.IpNetToMediaEntry" + ",IpNetToMediaIfIndex=" + _keyIpNetToMediaIfIndex + ",IpNetToMediaNetAddress=" + _keyIpNetToMediaNetAddress);
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new SnmpStatusException(SnmpStatusException.snmpRspWrongValue);
        } catch(Exception e) {
            throw new SnmpStatusException(e.getMessage());
        }
    }

    /**
     * Build index for "IpNetToMediaEntry".
     */
    public SnmpIndex buildSnmpIndex(IpNetToMediaEntryMBean entry)
        throws SnmpStatusException {
        SnmpOid[] oids = new SnmpOid[2];
        SnmpValue val = null;
        val = new SnmpInt(entry.getIpNetToMediaIfIndex());
        oids[0] = val.toOid();
        val = new SnmpIpAddress(entry.getIpNetToMediaNetAddress());
        oids[1] = val.toOid();
        return new SnmpIndex(oids);
    }

    /**
     * Build index for "IpNetToMediaEntry".
     */
    public SnmpOid buildOidFromIndex(SnmpIndex index)
        throws SnmpStatusException {
        SnmpOid oid = new SnmpOid();
        if (index.getNbComponents() != 2)
            throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
        try {
            Vector v = index.getComponents();
            SnmpInt.appendToOid((SnmpOid)v.elementAt(0), oid);
            SnmpIpAddress.appendToOid((SnmpOid)v.elementAt(1), oid);
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
        }
        return oid;
    }

    /**
     * Build index for "IpNetToMediaEntry".
     */
    public SnmpOid buildOidFromIndexVal(Integer aIpNetToMediaIfIndex, String aIpNetToMediaNetAddress)
        throws SnmpStatusException  {
        SnmpOid oid = new SnmpOid();
        try {
            SnmpInt.appendToOid(new SnmpInt(aIpNetToMediaIfIndex).toOid(), oid);
            SnmpIpAddress.appendToOid(new SnmpIpAddress(aIpNetToMediaNetAddress).toOid(), oid);
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
        }
        return oid;
    }

    /**
     * Build index for "IpNetToMediaEntry".
     */
    public SnmpIndex buildSnmpIndex(long[] index, int start)
        throws SnmpStatusException {
        SnmpOid[] oids = new SnmpOid[2];
        int pos = start;
        oids[0] = SnmpInt.toOid(index, pos);
        pos = SnmpInt.nextOid(index, pos);
        oids[1] = SnmpIpAddress.toOid(index, pos);
        return new SnmpIndex(oids);
    }


    /**
     * Factory method for "IpNetToMediaEntry" entry MBean class.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @return An instance of the MBean class generated for the
     *         "IpNetToMediaEntry" conceptual row.
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "IpNetToMediaEntryMBean"
     * interface.
     */

    public Object createIpNetToMediaEntryMBean(SnmpMibSubRequest req,
                SnmpOid rowOid, int depth, ObjectName entryObjName,
                SnmpMibTable meta, Integer  aIpNetToMediaIfIndex, String  aIpNetToMediaNetAddress)
            throws SnmpStatusException  {

        // Note that when using standard metadata,
        // the returned object must implement the "IpNetToMediaEntryMBean"
        // interface.
        //
        IpNetToMediaEntry entry = new IpNetToMediaEntry(theMib);
        entry.IpNetToMediaIfIndex = aIpNetToMediaIfIndex;
        entry.IpNetToMediaNetAddress = aIpNetToMediaNetAddress;
        return entry;
    }


    /**
     * Reference to the MBean server.
     */
    protected MBeanServer server;

}
