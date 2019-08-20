package com.elitecore.core.serverx.snmp.mib.mib2.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RFC1213-MIB in standard metadata mode.
//

// java imports
//
import java.io.Serializable;

import javax.management.MBeanServer;

import com.sun.management.snmp.SnmpInt;
import com.sun.management.snmp.SnmpOid;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.SnmpString;
import com.sun.management.snmp.SnmpTimeticks;
import com.sun.management.snmp.SnmpValue;
import com.sun.management.snmp.agent.SnmpMib;
import com.sun.management.snmp.agent.SnmpMibGroup;
import com.sun.management.snmp.agent.SnmpMibSubRequest;
import com.sun.management.snmp.agent.SnmpMibTable;
import com.sun.management.snmp.agent.SnmpStandardMetaServer;
import com.sun.management.snmp.agent.SnmpStandardObjectServer;

/**
 * The class is used for representing SNMP metadata for the "System" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.1.
 */
public class SystemMeta extends SnmpMibGroup
     implements Serializable, SnmpStandardMetaServer {

    /**
     * Constructor for the metadata associated to "System".
     */
    public SystemMeta(SnmpMib myMib, SnmpStandardObjectServer objserv) {
        objectserver = objserv;
        try {
            registerObject(6);
            registerObject(5);
            registerObject(4);
            registerObject(3);
            registerObject(2);
            registerObject(1);
            registerObject(7);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Get the value of a scalar variable
     */
    public SnmpValue get(long var, Object data)
        throws SnmpStatusException {
        switch((int)var) {
            case 6:
                return new SnmpString(node.getSysLocation());

            case 5:
                return new SnmpString(node.getSysName());

            case 4:
                return new SnmpString(node.getSysContact());

            case 3:
                return new SnmpTimeticks(node.getSysUpTime());

            case 2:
                return new SnmpOid(node.getSysObjectID());

            case 1:
                return new SnmpString(node.getSysDescr());

            case 7:
                return new SnmpInt(node.getSysServices());

            default:
                break;
        }
        throw new SnmpStatusException(SnmpStatusException.noSuchObject);
    }

    /**
     * Set the value of a scalar variable
     */
    public SnmpValue set(SnmpValue x, long var, Object data)
        throws SnmpStatusException {
        switch((int)var) {
            case 6:
                if (x instanceof SnmpString) {
                    node.setSysLocation(((SnmpString)x).toString());
                    return new SnmpString(node.getSysLocation());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 5:
                if (x instanceof SnmpString) {
                    node.setSysName(((SnmpString)x).toString());
                    return new SnmpString(node.getSysName());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 4:
                if (x instanceof SnmpString) {
                    node.setSysContact(((SnmpString)x).toString());
                    return new SnmpString(node.getSysContact());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 1:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 7:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            default:
                break;
        }
        throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
    }

    /**
     * Check the value of a scalar variable
     */
    public void check(SnmpValue x, long var, Object data)
        throws SnmpStatusException {
        switch((int) var) {
            case 6:
                if (x instanceof SnmpString) {
                    node.checkSysLocation(((SnmpString)x).toString());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 5:
                if (x instanceof SnmpString) {
                    node.checkSysName(((SnmpString)x).toString());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 4:
                if (x instanceof SnmpString) {
                    node.checkSysContact(((SnmpString)x).toString());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 1:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 7:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            default:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
        }
    }

    /**
     * Allow to bind the metadata description to a specific object.
     */
    protected void setInstance(SystemMBean var) {
        node = var;
    }


    // ------------------------------------------------------------
    // 
    // Implements the "get" method defined in "SnmpMibGroup".
    // See the "SnmpMibGroup" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void get(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException {
        objectserver.get(this,req,depth);
    }


    // ------------------------------------------------------------
    // 
    // Implements the "set" method defined in "SnmpMibGroup".
    // See the "SnmpMibGroup" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void set(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException {
        objectserver.set(this,req,depth);
    }


    // ------------------------------------------------------------
    // 
    // Implements the "check" method defined in "SnmpMibGroup".
    // See the "SnmpMibGroup" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void check(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException {
        objectserver.check(this,req,depth);
    }

    /**
     * Returns true if "arc" identifies a scalar object.
     */
    public boolean isVariable(long arc) {

        switch((int)arc) {
            case 6:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
            case 7:
                return true;
            default:
                break;
        }
        return false;
    }

    /**
     * Returns true if "arc" identifies a readable scalar object.
     */
    public boolean isReadable(long arc) {

        switch((int)arc) {
            case 6:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
            case 7:
                return true;
            default:
                break;
        }
        return false;
    }


    // ------------------------------------------------------------
    // 
    // Implements the "skipVariable" method defined in "SnmpMibGroup".
    // See the "SnmpMibGroup" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public boolean  skipVariable(long var, Object data, int pduVersion) {
        return false;
    }

    /**
     * Return the name of the attribute corresponding to the SNMP variable identified by "id".
     */
    public String getAttributeName(long id)
        throws SnmpStatusException {
        switch((int)id) {
            case 6:
                return "SysLocation";

            case 5:
                return "SysName";

            case 4:
                return "SysContact";

            case 3:
                return "SysUpTime";

            case 2:
                return "SysObjectID";

            case 1:
                return "SysDescr";

            case 7:
                return "SysServices";

            default:
                break;
        }
        throw new SnmpStatusException(SnmpStatusException.noSuchObject);
    }

    /**
     * Returns true if "arc" identifies a table object.
     */
    public boolean isTable(long arc) {

        switch((int)arc) {
            default:
                break;
        }
        return false;
    }

    /**
     * Returns the table object identified by "arc".
     */
    public SnmpMibTable getTable(long arc) {
        return null;
    }

    /**
     * Register the group's SnmpMibTable objects with the meta-data.
     */
    public void registerTableNodes(SnmpMib mib, MBeanServer server) {
    }

    protected SystemMBean node;
    protected SnmpStandardObjectServer objectserver = null;
}
