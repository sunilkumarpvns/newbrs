package com.elitecore.core.serverx.snmp.mib.os.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling UCD-SNMP-MIB in standard metadata mode.
//

// java imports
//
import java.io.Serializable;

// jmx imports
//
import javax.management.MBeanServer;
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
import com.sun.management.snmp.agent.SnmpMib;
import com.sun.management.snmp.agent.SnmpMibGroup;
import com.sun.management.snmp.agent.SnmpStandardObjectServer;
import com.sun.management.snmp.agent.SnmpStandardMetaServer;
import com.sun.management.snmp.agent.SnmpMibSubRequest;
import com.sun.management.snmp.agent.SnmpMibTable;
import com.sun.management.snmp.EnumRowStatus;
import com.sun.management.snmp.SnmpDefinitions;

/**
 * The class is used for representing SNMP metadata for the "Version" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.2021.100.
 */
public class VersionMeta extends SnmpMibGroup
     implements Serializable, SnmpStandardMetaServer {

    /**
     * Constructor for the metadata associated to "Version".
     */
    public VersionMeta(SnmpMib myMib, SnmpStandardObjectServer objserv) {
        objectserver = objserv;
        try {
            registerObject(20);
            registerObject(6);
            registerObject(5);
            registerObject(13);
            registerObject(4);
            registerObject(12);
            registerObject(3);
            registerObject(2);
            registerObject(11);
            registerObject(10);
            registerObject(1);
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
            case 20:
                return new SnmpInt(node.getVersionDoDebugging());

            case 6:
                return new SnmpString(node.getVersionConfigureOptions());

            case 5:
                return new SnmpString(node.getVersionIdent());

            case 13:
                return new SnmpInt(node.getVersionSavePersistentData());

            case 4:
                return new SnmpString(node.getVersionCDate());

            case 12:
                return new SnmpInt(node.getVersionRestartAgent());

            case 3:
                return new SnmpString(node.getVersionDate());

            case 2:
                return new SnmpString(node.getVersionTag());

            case 11:
                return new SnmpInt(node.getVersionUpdateConfig());

            case 10:
                return new SnmpInt(node.getVersionClearCache());

            case 1:
                return new SnmpInt(node.getVersionIndex());

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
            case 20:
                if (x instanceof SnmpInt) {
                    node.setVersionDoDebugging(((SnmpInt)x).toInteger());
                    return new SnmpInt(node.getVersionDoDebugging());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 6:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 13:
                if (x instanceof SnmpInt) {
                    node.setVersionSavePersistentData(((SnmpInt)x).toInteger());
                    return new SnmpInt(node.getVersionSavePersistentData());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 4:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 12:
                if (x instanceof SnmpInt) {
                    node.setVersionRestartAgent(((SnmpInt)x).toInteger());
                    return new SnmpInt(node.getVersionRestartAgent());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 11:
                if (x instanceof SnmpInt) {
                    node.setVersionUpdateConfig(((SnmpInt)x).toInteger());
                    return new SnmpInt(node.getVersionUpdateConfig());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 10:
                if (x instanceof SnmpInt) {
                    node.setVersionClearCache(((SnmpInt)x).toInteger());
                    return new SnmpInt(node.getVersionClearCache());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 1:
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
            case 20:
                if (x instanceof SnmpInt) {
                    node.checkVersionDoDebugging(((SnmpInt)x).toInteger());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 6:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 13:
                if (x instanceof SnmpInt) {
                    node.checkVersionSavePersistentData(((SnmpInt)x).toInteger());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 4:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 12:
                if (x instanceof SnmpInt) {
                    node.checkVersionRestartAgent(((SnmpInt)x).toInteger());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 11:
                if (x instanceof SnmpInt) {
                    node.checkVersionUpdateConfig(((SnmpInt)x).toInteger());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 10:
                if (x instanceof SnmpInt) {
                    node.checkVersionClearCache(((SnmpInt)x).toInteger());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 1:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            default:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
        }
    }

    /**
     * Allow to bind the metadata description to a specific object.
     */
    protected void setInstance(VersionMBean var) {
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
            case 20:
            case 6:
            case 5:
            case 13:
            case 4:
            case 12:
            case 3:
            case 2:
            case 11:
            case 10:
            case 1:
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
            case 20:
            case 6:
            case 5:
            case 13:
            case 4:
            case 12:
            case 3:
            case 2:
            case 11:
            case 10:
            case 1:
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
            case 20:
                return "VersionDoDebugging";

            case 6:
                return "VersionConfigureOptions";

            case 5:
                return "VersionIdent";

            case 13:
                return "VersionSavePersistentData";

            case 4:
                return "VersionCDate";

            case 12:
                return "VersionRestartAgent";

            case 3:
                return "VersionDate";

            case 2:
                return "VersionTag";

            case 11:
                return "VersionUpdateConfig";

            case 10:
                return "VersionClearCache";

            case 1:
                return "VersionIndex";

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

    protected VersionMBean node;
    protected SnmpStandardObjectServer objectserver = null;
}
