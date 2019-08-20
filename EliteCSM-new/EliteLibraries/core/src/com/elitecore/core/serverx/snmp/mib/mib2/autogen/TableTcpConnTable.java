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
 * The class is used for implementing the "TcpConnTable" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.6.13.
 */
public class TableTcpConnTable extends SnmpTableSupport implements Serializable {

    /**
     * Constructor for the table. Initialize metadata for "TableTcpConnTable".
     * The reference on the MBean server is not updated so the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public TableTcpConnTable(SnmpMib myMib) {
        super(myMib);
    }

    /**
     * Constructor for the table. Initialize metadata for "TableTcpConnTable".
     * The reference on the MBean server is updated so the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public TableTcpConnTable(SnmpMib myMib, MBeanServer server) {
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
            final SnmpOid oid2 = (SnmpOid) v.elementAt(2);
            final SnmpOid oid3 = (SnmpOid) v.elementAt(3);
            ObjectName objname = null;
            if (server != null)
                objname = buildNameFromIndex( index );

            // Note that when using standard metadata,
            // the returned object must implement the "TcpConnEntryMBean"
            // interface.
            //
            final Object entry =
                 createTcpConnEntryMBean(req, rowOid, depth, objname, meta,
                    oid0.toString(),
                    oid1.toInteger(),
                    oid2.toString(),
                    oid3.toInteger());
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
        return mib.getRegisteredTableMeta("TcpConnTable");
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

    public synchronized void addEntry(TcpConnEntryMBean entry)
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

    public synchronized void addEntry(TcpConnEntryMBean entry, ObjectName name)
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

    public synchronized TcpConnEntryMBean[] getEntries() {
        Object[] array = getBasicEntries();
        TcpConnEntryMBean[] result = new TcpConnEntryMBean[array.length];
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

    public void removeEntry(TcpConnEntryMBean entry)
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
            String _keyTcpConnLocalAddress = oid.toString().toString();
            oid = (SnmpOid) v.elementAt(1);
            String _keyTcpConnLocalPort = oid.toInteger().toString();
            oid = (SnmpOid) v.elementAt(2);
            String _keyTcpConnRemAddress = oid.toString().toString();
            oid = (SnmpOid) v.elementAt(3);
            String _keyTcpConnRemPort = oid.toInteger().toString();
            return new ObjectName("TableTcpConnTable:name=com.elitecore.core.serverx.snmp.mib.mib2.autogen.TcpConnEntry" + ",TcpConnLocalAddress=" + _keyTcpConnLocalAddress + ",TcpConnLocalPort=" + _keyTcpConnLocalPort + ",TcpConnRemAddress=" + _keyTcpConnRemAddress + ",TcpConnRemPort=" + _keyTcpConnRemPort);
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new SnmpStatusException(SnmpStatusException.snmpRspWrongValue);
        } catch(Exception e) {
            throw new SnmpStatusException(e.getMessage());
        }
    }

    /**
     * Build index for "TcpConnEntry".
     */
    public SnmpIndex buildSnmpIndex(TcpConnEntryMBean entry)
        throws SnmpStatusException {
        SnmpOid[] oids = new SnmpOid[4];
        SnmpValue val = null;
        val = new SnmpIpAddress(entry.getTcpConnLocalAddress());
        oids[0] = val.toOid();
        val = new SnmpInt(entry.getTcpConnLocalPort());
        oids[1] = val.toOid();
        val = new SnmpIpAddress(entry.getTcpConnRemAddress());
        oids[2] = val.toOid();
        val = new SnmpInt(entry.getTcpConnRemPort());
        oids[3] = val.toOid();
        return new SnmpIndex(oids);
    }

    /**
     * Build index for "TcpConnEntry".
     */
    public SnmpOid buildOidFromIndex(SnmpIndex index)
        throws SnmpStatusException {
        SnmpOid oid = new SnmpOid();
        if (index.getNbComponents() != 4)
            throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
        try {
            Vector v = index.getComponents();
            SnmpIpAddress.appendToOid((SnmpOid)v.elementAt(0), oid);
            SnmpInt.appendToOid((SnmpOid)v.elementAt(1), oid);
            SnmpIpAddress.appendToOid((SnmpOid)v.elementAt(2), oid);
            SnmpInt.appendToOid((SnmpOid)v.elementAt(3), oid);
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
        }
        return oid;
    }

    /**
     * Build index for "TcpConnEntry".
     */
    public SnmpOid buildOidFromIndexVal(String aTcpConnLocalAddress, Integer aTcpConnLocalPort, String aTcpConnRemAddress, Integer aTcpConnRemPort)
        throws SnmpStatusException  {
        SnmpOid oid = new SnmpOid();
        try {
            SnmpIpAddress.appendToOid(new SnmpIpAddress(aTcpConnLocalAddress).toOid(), oid);
            SnmpInt.appendToOid(new SnmpInt(aTcpConnLocalPort).toOid(), oid);
            SnmpIpAddress.appendToOid(new SnmpIpAddress(aTcpConnRemAddress).toOid(), oid);
            SnmpInt.appendToOid(new SnmpInt(aTcpConnRemPort).toOid(), oid);
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
        }
        return oid;
    }

    /**
     * Build index for "TcpConnEntry".
     */
    public SnmpIndex buildSnmpIndex(long[] index, int start)
        throws SnmpStatusException {
        SnmpOid[] oids = new SnmpOid[4];
        int pos = start;
        oids[0] = SnmpIpAddress.toOid(index, pos);
        pos = SnmpIpAddress.nextOid(index, pos);
        oids[1] = SnmpInt.toOid(index, pos);
        pos = SnmpInt.nextOid(index, pos);
        oids[2] = SnmpIpAddress.toOid(index, pos);
        pos = SnmpIpAddress.nextOid(index, pos);
        oids[3] = SnmpInt.toOid(index, pos);
        return new SnmpIndex(oids);
    }


    /**
     * Factory method for "TcpConnEntry" entry MBean class.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @return An instance of the MBean class generated for the
     *         "TcpConnEntry" conceptual row.
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "TcpConnEntryMBean"
     * interface.
     */

    public Object createTcpConnEntryMBean(SnmpMibSubRequest req,
                SnmpOid rowOid, int depth, ObjectName entryObjName,
                SnmpMibTable meta, String  aTcpConnLocalAddress, Integer  aTcpConnLocalPort, String  aTcpConnRemAddress, Integer  aTcpConnRemPort)
            throws SnmpStatusException  {

        // Note that when using standard metadata,
        // the returned object must implement the "TcpConnEntryMBean"
        // interface.
        //
        TcpConnEntry entry = new TcpConnEntry(theMib);
        entry.TcpConnLocalAddress = aTcpConnLocalAddress;
        entry.TcpConnLocalPort = aTcpConnLocalPort;
        entry.TcpConnRemAddress = aTcpConnRemAddress;
        entry.TcpConnRemPort = aTcpConnRemPort;
        return entry;
    }


    /**
     * Reference to the MBean server.
     */
    protected MBeanServer server;

}
