package com.elitecore.core.serverx.snmp.mib.mib2.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RFC1213-MIB in standard metadata mode.
//

// java imports
//
import java.io.Serializable;

import com.sun.management.snmp.SnmpCounter;
import com.sun.management.snmp.SnmpGauge;
import com.sun.management.snmp.SnmpInt;
import com.sun.management.snmp.SnmpOid;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.SnmpString;
import com.sun.management.snmp.SnmpTimeticks;
import com.sun.management.snmp.SnmpValue;
import com.sun.management.snmp.agent.SnmpMib;
import com.sun.management.snmp.agent.SnmpMibEntry;
import com.sun.management.snmp.agent.SnmpMibNode;
import com.sun.management.snmp.agent.SnmpMibSubRequest;
import com.sun.management.snmp.agent.SnmpStandardMetaServer;
import com.sun.management.snmp.agent.SnmpStandardObjectServer;

/**
 * The class is used for representing SNMP metadata for the "IfEntry" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.2.2.1.
 */
public class IfEntryMeta extends SnmpMibEntry
     implements Serializable, SnmpStandardMetaServer {

    /**
     * Constructor for the metadata associated to "IfEntry".
     */
    public IfEntryMeta(SnmpMib myMib, SnmpStandardObjectServer objserv) {
        objectserver = objserv;
        varList = new int[22];
        varList[0] = 19;
        varList[1] = 18;
        varList[2] = 17;
        varList[3] = 16;
        varList[4] = 15;
        varList[5] = 14;
        varList[6] = 13;
        varList[7] = 12;
        varList[8] = 11;
        varList[9] = 10;
        varList[10] = 9;
        varList[11] = 8;
        varList[12] = 7;
        varList[13] = 6;
        varList[14] = 5;
        varList[15] = 4;
        varList[16] = 3;
        varList[17] = 2;
        varList[18] = 1;
        varList[19] = 22;
        varList[20] = 21;
        varList[21] = 20;
        SnmpMibNode.sort(varList);
    }

    /**
     * Get the value of a scalar variable
     */
    public SnmpValue get(long var, Object data)
        throws SnmpStatusException {
        switch((int)var) {
            case 19:
                return new SnmpCounter(node.getIfOutDiscards());

            case 18:
                return new SnmpCounter(node.getIfOutNUcastPkts());

            case 17:
                return new SnmpCounter(node.getIfOutUcastPkts());

            case 16:
                return new SnmpCounter(node.getIfOutOctets());

            case 15:
                return new SnmpCounter(node.getIfInUnknownProtos());

            case 14:
                return new SnmpCounter(node.getIfInErrors());

            case 13:
                return new SnmpCounter(node.getIfInDiscards());

            case 12:
                return new SnmpCounter(node.getIfInNUcastPkts());

            case 11:
                return new SnmpCounter(node.getIfInUcastPkts());

            case 10:
                return new SnmpCounter(node.getIfInOctets());

            case 9:
                return new SnmpTimeticks(node.getIfLastChange());

            case 8:
                return new SnmpInt(node.getIfOperStatus());

            case 7:
                return new SnmpInt(node.getIfAdminStatus());

            case 6:
                return new SnmpString(node.getIfPhysAddress());

            case 5:
                return new SnmpGauge(node.getIfSpeed());

            case 4:
                return new SnmpInt(node.getIfMtu());

            case 3:
                return new SnmpInt(node.getIfType());

            case 2:
                return new SnmpString(node.getIfDescr());

            case 1:
                return new SnmpInt(node.getIfIndex());

            case 22:
                return new SnmpOid(node.getIfSpecific());

            case 21:
                return new SnmpGauge(node.getIfOutQLen());

            case 20:
                return new SnmpCounter(node.getIfOutErrors());

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
            case 19:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 18:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 17:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 16:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 15:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 14:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 13:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 12:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 11:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 10:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 9:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 8:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 7:
                if (x instanceof SnmpInt) {
                    try  {
                        node.setIfAdminStatus( new EnumIfAdminStatus (((SnmpInt)x).toInteger()));
                    } catch(IllegalArgumentException e)  {
                        throw new SnmpStatusException(SnmpStatusException.snmpRspWrongValue);
                    }
                    return new SnmpInt(node.getIfAdminStatus());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 6:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 4:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 1:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 22:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 21:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 20:
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
            case 19:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 18:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 17:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 16:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 15:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 14:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 13:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 12:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 11:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 10:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 9:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 8:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 7:
                if (x instanceof SnmpInt) {
                    try  {
                        node.checkIfAdminStatus( new EnumIfAdminStatus (((SnmpInt)x).toInteger()));
                    } catch(IllegalArgumentException e)  {
                        throw new SnmpStatusException(SnmpStatusException.snmpRspWrongValue);
                    }
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 6:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 4:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 1:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 22:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 21:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 20:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            default:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
        }
    }

    /**
     * Allow to bind the metadata description to a specific object.
     */
    protected void setInstance(IfEntryMBean var) {
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
            case 19:
            case 18:
            case 17:
            case 16:
            case 15:
            case 14:
            case 13:
            case 12:
            case 11:
            case 10:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
            case 22:
            case 21:
            case 20:
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
            case 19:
            case 18:
            case 17:
            case 16:
            case 15:
            case 14:
            case 13:
            case 12:
            case 11:
            case 10:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
            case 22:
            case 21:
            case 20:
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
            case 19:
                return "IfOutDiscards";

            case 18:
                return "IfOutNUcastPkts";

            case 17:
                return "IfOutUcastPkts";

            case 16:
                return "IfOutOctets";

            case 15:
                return "IfInUnknownProtos";

            case 14:
                return "IfInErrors";

            case 13:
                return "IfInDiscards";

            case 12:
                return "IfInNUcastPkts";

            case 11:
                return "IfInUcastPkts";

            case 10:
                return "IfInOctets";

            case 9:
                return "IfLastChange";

            case 8:
                return "IfOperStatus";

            case 7:
                return "IfAdminStatus";

            case 6:
                return "IfPhysAddress";

            case 5:
                return "IfSpeed";

            case 4:
                return "IfMtu";

            case 3:
                return "IfType";

            case 2:
                return "IfDescr";

            case 1:
                return "IfIndex";

            case 22:
                return "IfSpecific";

            case 21:
                return "IfOutQLen";

            case 20:
                return "IfOutErrors";

            default:
                break;
        }
        throw new SnmpStatusException(SnmpStatusException.noSuchObject);
    }

    protected IfEntryMBean node;
    protected SnmpStandardObjectServer objectserver = null;
}
