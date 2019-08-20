package com.elitecore.aaa.mibs.radius.dynauth.server.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RADIUS-DYNAUTH-SERVER-MIB in standard metadata mode.
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
 * The class is used for representing SNMP metadata for the "RadiusDynAuthServerMIBObjects" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.146.1.
 */
public class RadiusDynAuthServerMIBObjectsMeta extends SnmpMibGroup
     implements Serializable, SnmpStandardMetaServer {

    /**
     * Constructor for the metadata associated to "RadiusDynAuthServerMIBObjects".
     */
    public RadiusDynAuthServerMIBObjectsMeta(SnmpMib myMib, SnmpStandardObjectServer objserv) {
        objectserver = objserv;
        try {
            registerObject(2);
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
            case 2: {
                throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
                }

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
            case 2: {
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
                }

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
            case 2: {
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
                }

            default:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
        }
    }

    /**
     * Allow to bind the metadata description to a specific object.
     */
    protected void setInstance(RadiusDynAuthServerMIBObjectsMBean var) {
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

        return false;
    }

    /**
     * Returns true if "arc" identifies a readable scalar object.
     */
    public boolean isReadable(long arc) {

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
            case 2: {
                throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
                }

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
            case 2:
                return true;
            default:
                break;
        }
        return false;
    }

    /**
     * Returns the table object identified by "arc".
     */
    public SnmpMibTable getTable(long arc) {

        switch((int)arc) {
            case 2:
                return tableRadiusDynAuthClientTable;
        default:
            break;
        }
        return null;
    }

    /**
     * Register the group's SnmpMibTable objects with the meta-data.
     */
    public void registerTableNodes(SnmpMib mib, MBeanServer server) {
        tableRadiusDynAuthClientTable = createRadiusDynAuthClientTableMetaNode("RadiusDynAuthClientTable", "RadiusDynAuthServerMIBObjects", mib, server);
        if ( tableRadiusDynAuthClientTable != null)  {
            tableRadiusDynAuthClientTable.registerEntryNode(mib,server);
            mib.registerTableMeta("RadiusDynAuthClientTable", tableRadiusDynAuthClientTable);
        }

    }


    /**
     * Factory method for "RadiusDynAuthClientTable" table metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param tableName Name of the table object ("RadiusDynAuthClientTable")
     * @param groupName Name of the group to which this table belong ("RadiusDynAuthServerMIBObjects")
     * @param mib The SnmpMib object in which this table is registered
     * @param server MBeanServer for this table entries (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "RadiusDynAuthClientTable" table (RadiusDynAuthClientTableMeta)
     * 
     **/
    protected RadiusDynAuthClientTableMeta createRadiusDynAuthClientTableMetaNode(String tableName, String groupName, SnmpMib mib, MBeanServer server)  {
        return new RadiusDynAuthClientTableMeta(mib, objectserver);
    }

    protected RadiusDynAuthServerMIBObjectsMBean node;
    protected SnmpStandardObjectServer objectserver = null;
    protected RadiusDynAuthClientTableMeta tableRadiusDynAuthClientTable = null;
}