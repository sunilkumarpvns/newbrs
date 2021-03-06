package com.elitecore.aaa.rm.service.ippool.snmp.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling IP-POOL-SERVICE-MIB in standard metadata mode.
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
 * The class is used for representing SNMP metadata for the "IpPoolServMIBObjects" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.1.7.1.
 */
public class IpPoolServMIBObjectsMeta extends SnmpMibGroup
     implements Serializable, SnmpStandardMetaServer {

    /**
     * Constructor for the metadata associated to "IpPoolServMIBObjects".
     */
    public IpPoolServMIBObjectsMeta(SnmpMib myMib, SnmpStandardObjectServer objserv) {
        objectserver = objserv;
        try {
            registerObject(9);
            registerObject(8);
            registerObject(7);
            registerObject(6);
            registerObject(15);
            registerObject(14);
            registerObject(5);
            registerObject(13);
            registerObject(4);
            registerObject(3);
            registerObject(12);
            registerObject(102);
            registerObject(2);
            registerObject(101);
            registerObject(11);
            registerObject(1);
            registerObject(10);
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
                return new SnmpCounter(node.getIpPoolServTotalInvalidRequest());

            case 8:
                return new SnmpCounter(node.getIpPoolServTotalUnknownPacket());

            case 7:
                return new SnmpCounter(node.getIpPoolServTotalDuplicateRequest());

            case 6:
                return new SnmpCounter(node.getIpPoolServTotalRequestDropped());

            case 15:
                return new SnmpCounter(node.getIpPoolServTotalUpdateRequest());

            case 14:
                return new SnmpCounter(node.getIpPoolServTotalReleaseRequest());

            case 5:
                return new SnmpCounter(node.getIpPoolServTotalResponses());

            case 13:
                return new SnmpCounter(node.getIpPoolServTotalAllocationRequest());

            case 4:
                return new SnmpCounter(node.getIpPoolServTotalRequest());

            case 3:
                return new SnmpTimeticks(node.getIpPoolServiceResetTime());

            case 12:
                return new SnmpCounter(node.getIpPoolServTotalDeclineResponse());

            case 102: {
                throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
                }

            case 2:
                return new SnmpInt(node.getIpPoolServiceReset());

            case 101: {
                throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
                }

            case 11:
                return new SnmpCounter(node.getIpPoolServTotalOfferResponse());

            case 1:
                return new SnmpTimeticks(node.getIpPoolServiceUpTime());

            case 10:
                return new SnmpCounter(node.getIpPoolServTotalDiscoverRequest());

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

            case 15:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 14:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 13:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 4:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 12:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 102: {
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
                }

            case 2:
                if (x instanceof SnmpInt) {
                    try  {
                        node.setIpPoolServiceReset( new EnumIpPoolServiceReset (((SnmpInt)x).toInteger()));
                    } catch(IllegalArgumentException e)  {
                        throw new SnmpStatusException(SnmpStatusException.snmpRspWrongValue);
                    }
                    return new SnmpInt(node.getIpPoolServiceReset());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 101: {
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
                }

            case 11:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 1:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 10:
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

            case 15:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 14:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 13:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 4:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 12:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 102: {
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
                }

            case 2:
                if (x instanceof SnmpInt) {
                    try  {
                        node.checkIpPoolServiceReset( new EnumIpPoolServiceReset (((SnmpInt)x).toInteger()));
                    } catch(IllegalArgumentException e)  {
                        throw new SnmpStatusException(SnmpStatusException.snmpRspWrongValue);
                    }
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 101: {
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
                }

            case 11:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 1:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 10:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            default:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
        }
    }

    /**
     * Allow to bind the metadata description to a specific object.
     */
    protected void setInstance(IpPoolServMIBObjectsMBean var) {
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
            case 15:
            case 14:
            case 5:
            case 13:
            case 4:
            case 3:
            case 12:
            case 2:
            case 11:
            case 1:
            case 10:
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
            case 15:
            case 14:
            case 5:
            case 13:
            case 4:
            case 3:
            case 12:
            case 2:
            case 11:
            case 1:
            case 10:
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
                return "IpPoolServTotalInvalidRequest";

            case 8:
                return "IpPoolServTotalUnknownPacket";

            case 7:
                return "IpPoolServTotalDuplicateRequest";

            case 6:
                return "IpPoolServTotalRequestDropped";

            case 15:
                return "IpPoolServTotalUpdateRequest";

            case 14:
                return "IpPoolServTotalReleaseRequest";

            case 5:
                return "IpPoolServTotalResponses";

            case 13:
                return "IpPoolServTotalAllocationRequest";

            case 4:
                return "IpPoolServTotalRequest";

            case 3:
                return "IpPoolServiceResetTime";

            case 12:
                return "IpPoolServTotalDeclineResponse";

            case 102: {
                throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
                }

            case 2:
                return "IpPoolServiceReset";

            case 101: {
                throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
                }

            case 11:
                return "IpPoolServTotalOfferResponse";

            case 1:
                return "IpPoolServiceUpTime";

            case 10:
                return "IpPoolServTotalDiscoverRequest";

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
            case 102:
                return true;
            case 101:
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
            case 102:
                return tableIpPoolNASClientStatisticsTable;
            case 101:
                return tableIpPoolAAAClientStatisticsTable;
        default:
            break;
        }
        return null;
    }

    /**
     * Register the group's SnmpMibTable objects with the meta-data.
     */
    public void registerTableNodes(SnmpMib mib, MBeanServer server) {
        tableIpPoolNASClientStatisticsTable = createIpPoolNASClientStatisticsTableMetaNode("IpPoolNASClientStatisticsTable", "IpPoolServMIBObjects", mib, server);
        if ( tableIpPoolNASClientStatisticsTable != null)  {
            tableIpPoolNASClientStatisticsTable.registerEntryNode(mib,server);
            mib.registerTableMeta("IpPoolNASClientStatisticsTable", tableIpPoolNASClientStatisticsTable);
        }

        tableIpPoolAAAClientStatisticsTable = createIpPoolAAAClientStatisticsTableMetaNode("IpPoolAAAClientStatisticsTable", "IpPoolServMIBObjects", mib, server);
        if ( tableIpPoolAAAClientStatisticsTable != null)  {
            tableIpPoolAAAClientStatisticsTable.registerEntryNode(mib,server);
            mib.registerTableMeta("IpPoolAAAClientStatisticsTable", tableIpPoolAAAClientStatisticsTable);
        }

    }


    /**
     * Factory method for "IpPoolNASClientStatisticsTable" table metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param tableName Name of the table object ("IpPoolNASClientStatisticsTable")
     * @param groupName Name of the group to which this table belong ("IpPoolServMIBObjects")
     * @param mib The SnmpMib object in which this table is registered
     * @param server MBeanServer for this table entries (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "IpPoolNASClientStatisticsTable" table (IpPoolNASClientStatisticsTableMeta)
     * 
     **/
    protected IpPoolNASClientStatisticsTableMeta createIpPoolNASClientStatisticsTableMetaNode(String tableName, String groupName, SnmpMib mib, MBeanServer server)  {
        return new IpPoolNASClientStatisticsTableMeta(mib, objectserver);
    }


    /**
     * Factory method for "IpPoolAAAClientStatisticsTable" table metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param tableName Name of the table object ("IpPoolAAAClientStatisticsTable")
     * @param groupName Name of the group to which this table belong ("IpPoolServMIBObjects")
     * @param mib The SnmpMib object in which this table is registered
     * @param server MBeanServer for this table entries (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "IpPoolAAAClientStatisticsTable" table (IpPoolAAAClientStatisticsTableMeta)
     * 
     **/
    protected IpPoolAAAClientStatisticsTableMeta createIpPoolAAAClientStatisticsTableMetaNode(String tableName, String groupName, SnmpMib mib, MBeanServer server)  {
        return new IpPoolAAAClientStatisticsTableMeta(mib, objectserver);
    }

    protected IpPoolServMIBObjectsMBean node;
    protected SnmpStandardObjectServer objectserver = null;
    protected IpPoolNASClientStatisticsTableMeta tableIpPoolNASClientStatisticsTable = null;
    protected IpPoolAAAClientStatisticsTableMeta tableIpPoolAAAClientStatisticsTable = null;
}
