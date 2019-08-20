package com.elitecore.netvertex.gateway.radius.snmp.acct.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RADIUS-ACC-SERVER-MIB in standard metadata mode.
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
 * The class is used for representing SNMP metadata for the "RadiusAccServ" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.67.2.1.1.1.
 */
public class RadiusAccServMeta extends SnmpMibGroup
     implements Serializable, SnmpStandardMetaServer {

    /**
     * Constructor for the metadata associated to "RadiusAccServ".
     */
    public RadiusAccServMeta(SnmpMib myMib, SnmpStandardObjectServer objserv) {
        objectserver = objserv;
        try {
            registerObject(9);
            registerObject(8);
            registerObject(7);
            registerObject(6);
            registerObject(5);
            registerObject(14);
            registerObject(4);
            registerObject(13);
            registerObject(12);
            registerObject(3);
            registerObject(11);
            registerObject(2);
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
            case 9:
                return new SnmpCounter(node.getRadiusAccServTotalMalformedRequests());

            case 8:
                return new SnmpCounter(node.getRadiusAccServTotalResponses());

            case 7:
                return new SnmpCounter(node.getRadiusAccServTotalDupRequests());

            case 6:
                return new SnmpCounter(node.getRadiusAccServTotalInvalidRequests());

            case 5:
                return new SnmpCounter(node.getRadiusAccServTotalRequests());

            case 14: {
                throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
                }

            case 4:
                return new SnmpInt(node.getRadiusAccServConfigReset());

            case 13:
                return new SnmpCounter(node.getRadiusAccServTotalUnknownTypes());

            case 12:
                return new SnmpCounter(node.getRadiusAccServTotalNoRecords());

            case 3:
                return new SnmpTimeticks(node.getRadiusAccServResetTime());

            case 11:
                return new SnmpCounter(node.getRadiusAccServTotalPacketsDropped());

            case 2:
                return new SnmpTimeticks(node.getRadiusAccServUpTime());

            case 10:
                return new SnmpCounter(node.getRadiusAccServTotalBadAuthenticators());

            case 1:
                return new SnmpString(node.getRadiusAccServIdent());

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
            case 9:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 8:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 7:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 6:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 14: {
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
                }

            case 4:
                if (x instanceof SnmpInt) {
                    try  {
                        node.setRadiusAccServConfigReset( new EnumRadiusAccServConfigReset (((SnmpInt)x).toInteger()));
                    } catch(IllegalArgumentException e)  {
                        throw new SnmpStatusException(SnmpStatusException.snmpRspWrongValue);
                    }
                    return new SnmpInt(node.getRadiusAccServConfigReset());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 13:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 12:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 11:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 10:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

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
            case 9:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 8:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 7:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 6:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 14: {
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
                }

            case 4:
                if (x instanceof SnmpInt) {
                    try  {
                        node.checkRadiusAccServConfigReset( new EnumRadiusAccServConfigReset (((SnmpInt)x).toInteger()));
                    } catch(IllegalArgumentException e)  {
                        throw new SnmpStatusException(SnmpStatusException.snmpRspWrongValue);
                    }
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 13:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 12:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 11:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 10:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 1:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            default:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
        }
    }

    /**
     * Allow to bind the metadata description to a specific object.
     */
    protected void setInstance(RadiusAccServMBean var) {
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
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 4:
            case 13:
            case 12:
            case 3:
            case 11:
            case 2:
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
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 4:
            case 13:
            case 12:
            case 3:
            case 11:
            case 2:
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
            case 9:
                return "RadiusAccServTotalMalformedRequests";

            case 8:
                return "RadiusAccServTotalResponses";

            case 7:
                return "RadiusAccServTotalDupRequests";

            case 6:
                return "RadiusAccServTotalInvalidRequests";

            case 5:
                return "RadiusAccServTotalRequests";

            case 14: {
                throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
                }

            case 4:
                return "RadiusAccServConfigReset";

            case 13:
                return "RadiusAccServTotalUnknownTypes";

            case 12:
                return "RadiusAccServTotalNoRecords";

            case 3:
                return "RadiusAccServResetTime";

            case 11:
                return "RadiusAccServTotalPacketsDropped";

            case 2:
                return "RadiusAccServUpTime";

            case 10:
                return "RadiusAccServTotalBadAuthenticators";

            case 1:
                return "RadiusAccServIdent";

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
            case 14:
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
            case 14:
                return tableRadiusAccClientTable;
        default:
            break;
        }
        return null;
    }

    /**
     * Register the group's SnmpMibTable objects with the meta-data.
     */
    public void registerTableNodes(SnmpMib mib, MBeanServer server) {
        tableRadiusAccClientTable = createRadiusAccClientTableMetaNode("RadiusAccClientTable", "RadiusAccServ", mib, server);
        if ( tableRadiusAccClientTable != null)  {
            tableRadiusAccClientTable.registerEntryNode(mib,server);
            mib.registerTableMeta("RadiusAccClientTable", tableRadiusAccClientTable);
        }

    }


    /**
     * Factory method for "RadiusAccClientTable" table metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param tableName Name of the table object ("RadiusAccClientTable")
     * @param groupName Name of the group to which this table belong ("RadiusAccServ")
     * @param mib The SnmpMib object in which this table is registered
     * @param server MBeanServer for this table entries (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "RadiusAccClientTable" table (RadiusAccClientTableMeta)
     * 
     **/
    protected RadiusAccClientTableMeta createRadiusAccClientTableMetaNode(String tableName, String groupName, SnmpMib mib, MBeanServer server)  {
        return new RadiusAccClientTableMeta(mib, objectserver);
    }

    protected RadiusAccServMBean node;
    protected SnmpStandardObjectServer objectserver = null;
    protected RadiusAccClientTableMeta tableRadiusAccClientTable = null;
}