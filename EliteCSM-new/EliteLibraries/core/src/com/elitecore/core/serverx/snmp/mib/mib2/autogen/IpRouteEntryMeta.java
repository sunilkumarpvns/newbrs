package com.elitecore.core.serverx.snmp.mib.mib2.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RFC1213-MIB in standard metadata mode.
//

// java imports
//
import java.io.Serializable;

import com.sun.management.snmp.SnmpInt;
import com.sun.management.snmp.SnmpIpAddress;
import com.sun.management.snmp.SnmpOid;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.SnmpValue;
import com.sun.management.snmp.agent.SnmpMib;
import com.sun.management.snmp.agent.SnmpMibEntry;
import com.sun.management.snmp.agent.SnmpMibNode;
import com.sun.management.snmp.agent.SnmpMibSubRequest;
import com.sun.management.snmp.agent.SnmpStandardMetaServer;
import com.sun.management.snmp.agent.SnmpStandardObjectServer;

/**
 * The class is used for representing SNMP metadata for the "IpRouteEntry" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.4.21.1.
 */
public class IpRouteEntryMeta extends SnmpMibEntry
     implements Serializable, SnmpStandardMetaServer {

    /**
     * Constructor for the metadata associated to "IpRouteEntry".
     */
    public IpRouteEntryMeta(SnmpMib myMib, SnmpStandardObjectServer objserv) {
        objectserver = objserv;
        varList = new int[13];
        varList[0] = 9;
        varList[1] = 8;
        varList[2] = 7;
        varList[3] = 6;
        varList[4] = 5;
        varList[5] = 4;
        varList[6] = 13;
        varList[7] = 3;
        varList[8] = 12;
        varList[9] = 2;
        varList[10] = 11;
        varList[11] = 10;
        varList[12] = 1;
        SnmpMibNode.sort(varList);
    }

    /**
     * Get the value of a scalar variable
     */
    public SnmpValue get(long var, Object data)
        throws SnmpStatusException {
        switch((int)var) {
            case 9:
                return new SnmpInt(node.getIpRouteProto());

            case 8:
                return new SnmpInt(node.getIpRouteType());

            case 7:
                return new SnmpIpAddress(node.getIpRouteNextHop());

            case 6:
                return new SnmpInt(node.getIpRouteMetric4());

            case 5:
                return new SnmpInt(node.getIpRouteMetric3());

            case 4:
                return new SnmpInt(node.getIpRouteMetric2());

            case 13:
                return new SnmpOid(node.getIpRouteInfo());

            case 3:
                return new SnmpInt(node.getIpRouteMetric1());

            case 12:
                return new SnmpInt(node.getIpRouteMetric5());

            case 2:
                return new SnmpInt(node.getIpRouteIfIndex());

            case 11:
                return new SnmpIpAddress(node.getIpRouteMask());

            case 10:
                return new SnmpInt(node.getIpRouteAge());

            case 1:
                return new SnmpIpAddress(node.getIpRouteDest());

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
                if (x instanceof SnmpInt) {
                    try  {
                        node.setIpRouteType( new EnumIpRouteType (((SnmpInt)x).toInteger()));
                    } catch(IllegalArgumentException e)  {
                        throw new SnmpStatusException(SnmpStatusException.snmpRspWrongValue);
                    }
                    return new SnmpInt(node.getIpRouteType());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 7:
                if (x instanceof SnmpIpAddress) {
                    node.setIpRouteNextHop(((SnmpIpAddress)x).toString());
                    return new SnmpIpAddress(node.getIpRouteNextHop());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 6:
                if (x instanceof SnmpInt) {
                    node.setIpRouteMetric4(((SnmpInt)x).toInteger());
                    return new SnmpInt(node.getIpRouteMetric4());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 5:
                if (x instanceof SnmpInt) {
                    node.setIpRouteMetric3(((SnmpInt)x).toInteger());
                    return new SnmpInt(node.getIpRouteMetric3());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 4:
                if (x instanceof SnmpInt) {
                    node.setIpRouteMetric2(((SnmpInt)x).toInteger());
                    return new SnmpInt(node.getIpRouteMetric2());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 13:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 3:
                if (x instanceof SnmpInt) {
                    node.setIpRouteMetric1(((SnmpInt)x).toInteger());
                    return new SnmpInt(node.getIpRouteMetric1());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 12:
                if (x instanceof SnmpInt) {
                    node.setIpRouteMetric5(((SnmpInt)x).toInteger());
                    return new SnmpInt(node.getIpRouteMetric5());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 2:
                if (x instanceof SnmpInt) {
                    node.setIpRouteIfIndex(((SnmpInt)x).toInteger());
                    return new SnmpInt(node.getIpRouteIfIndex());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 11:
                if (x instanceof SnmpIpAddress) {
                    node.setIpRouteMask(((SnmpIpAddress)x).toString());
                    return new SnmpIpAddress(node.getIpRouteMask());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 10:
                if (x instanceof SnmpInt) {
                    node.setIpRouteAge(((SnmpInt)x).toInteger());
                    return new SnmpInt(node.getIpRouteAge());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 1:
                if (x instanceof SnmpIpAddress) {
                    node.setIpRouteDest(((SnmpIpAddress)x).toString());
                    return new SnmpIpAddress(node.getIpRouteDest());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
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
            case 9:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 8:
                if (x instanceof SnmpInt) {
                    try  {
                        node.checkIpRouteType( new EnumIpRouteType (((SnmpInt)x).toInteger()));
                    } catch(IllegalArgumentException e)  {
                        throw new SnmpStatusException(SnmpStatusException.snmpRspWrongValue);
                    }
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 7:
                if (x instanceof SnmpIpAddress) {
                    node.checkIpRouteNextHop(((SnmpIpAddress)x).toString());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 6:
                if (x instanceof SnmpInt) {
                    node.checkIpRouteMetric4(((SnmpInt)x).toInteger());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 5:
                if (x instanceof SnmpInt) {
                    node.checkIpRouteMetric3(((SnmpInt)x).toInteger());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 4:
                if (x instanceof SnmpInt) {
                    node.checkIpRouteMetric2(((SnmpInt)x).toInteger());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 13:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 3:
                if (x instanceof SnmpInt) {
                    node.checkIpRouteMetric1(((SnmpInt)x).toInteger());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 12:
                if (x instanceof SnmpInt) {
                    node.checkIpRouteMetric5(((SnmpInt)x).toInteger());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 2:
                if (x instanceof SnmpInt) {
                    node.checkIpRouteIfIndex(((SnmpInt)x).toInteger());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 11:
                if (x instanceof SnmpIpAddress) {
                    node.checkIpRouteMask(((SnmpIpAddress)x).toString());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 10:
                if (x instanceof SnmpInt) {
                    node.checkIpRouteAge(((SnmpInt)x).toInteger());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 1:
                if (x instanceof SnmpIpAddress) {
                    node.checkIpRouteDest(((SnmpIpAddress)x).toString());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            default:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
        }
    }

    /**
     * Allow to bind the metadata description to a specific object.
     */
    protected void setInstance(IpRouteEntryMBean var) {
        node = var;
    }


    // ------------------------------------------------------------
    // 
    // Implements the "get" method defined in "SnmpMibEntry".
    // See the "SnmpMibEntry" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void get(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException {
        objectserver.get(this,req,depth);
    }


    // ------------------------------------------------------------
    // 
    // Implements the "set" method defined in "SnmpMibEntry".
    // See the "SnmpMibEntry" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void set(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException {
        objectserver.set(this,req,depth);
    }


    // ------------------------------------------------------------
    // 
    // Implements the "check" method defined in "SnmpMibEntry".
    // See the "SnmpMibEntry" Javadoc API for more details.
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
            case 3:
            case 12:
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
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 4:
            case 13:
            case 3:
            case 12:
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
    // Implements the "skipVariable" method defined in "SnmpMibEntry".
    // See the "SnmpMibEntry" Javadoc API for more details.
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
                return "IpRouteProto";

            case 8:
                return "IpRouteType";

            case 7:
                return "IpRouteNextHop";

            case 6:
                return "IpRouteMetric4";

            case 5:
                return "IpRouteMetric3";

            case 4:
                return "IpRouteMetric2";

            case 13:
                return "IpRouteInfo";

            case 3:
                return "IpRouteMetric1";

            case 12:
                return "IpRouteMetric5";

            case 2:
                return "IpRouteIfIndex";

            case 11:
                return "IpRouteMask";

            case 10:
                return "IpRouteAge";

            case 1:
                return "IpRouteDest";

            default:
                break;
        }
        throw new SnmpStatusException(SnmpStatusException.noSuchObject);
    }

    protected IpRouteEntryMBean node;
    protected SnmpStandardObjectServer objectserver = null;
}
