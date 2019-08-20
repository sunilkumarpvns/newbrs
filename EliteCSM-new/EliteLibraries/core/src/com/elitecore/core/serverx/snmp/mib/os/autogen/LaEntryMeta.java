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
import com.sun.management.snmp.agent.SnmpMibNode;
import com.sun.management.snmp.agent.SnmpMib;
import com.sun.management.snmp.agent.SnmpMibEntry;
import com.sun.management.snmp.agent.SnmpStandardObjectServer;
import com.sun.management.snmp.agent.SnmpStandardMetaServer;
import com.sun.management.snmp.agent.SnmpMibSubRequest;
import com.sun.management.snmp.agent.SnmpMibTable;
import com.sun.management.snmp.EnumRowStatus;
import com.sun.management.snmp.SnmpDefinitions;

/**
 * The class is used for representing SNMP metadata for the "LaEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.2021.10.1.
 */
public class LaEntryMeta extends SnmpMibEntry
     implements Serializable, SnmpStandardMetaServer {

    /**
     * Constructor for the metadata associated to "LaEntry".
     */
    public LaEntryMeta(SnmpMib myMib, SnmpStandardObjectServer objserv) {
        objectserver = objserv;
        varList = new int[8];
        varList[0] = 6;
        varList[1] = 5;
        varList[2] = 4;
        varList[3] = 3;
        varList[4] = 101;
        varList[5] = 2;
        varList[6] = 1;
        varList[7] = 100;
        SnmpMibNode.sort(varList);
    }

    /**
     * Get the value of a scalar variable
     */
    public SnmpValue get(long var, Object data)
        throws SnmpStatusException {
        switch((int)var) {
            case 6:
                return new SnmpOpaque(node.getLaLoadFloat());

            case 5:
                return new SnmpInt(node.getLaLoadInt());

            case 4:
                return new SnmpString(node.getLaConfig());

            case 3:
                return new SnmpString(node.getLaLoad());

            case 101:
                return new SnmpString(node.getLaErrMessage());

            case 2:
                return new SnmpString(node.getLaNames());

            case 1:
                return new SnmpInt(node.getLaIndex());

            case 100:
                return new SnmpInt(node.getLaErrorFlag());

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
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 4:
                if (x instanceof SnmpString) {
                    node.setLaConfig(((SnmpString)x).toString());
                    return new SnmpString(node.getLaConfig());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 101:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 1:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 100:
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
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 4:
                if (x instanceof SnmpString) {
                    node.checkLaConfig(((SnmpString)x).toString());
                } else {
                    throw new SnmpStatusException(SnmpStatusException.snmpRspWrongType);
                }
                break;

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 101:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 1:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 100:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            default:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
        }
    }

    /**
     * Allow to bind the metadata description to a specific object.
     */
    protected void setInstance(LaEntryMBean var) {
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
            case 6:
            case 5:
            case 4:
            case 3:
            case 101:
            case 2:
            case 1:
            case 100:
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
            case 101:
            case 2:
            case 1:
            case 100:
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
            case 6:
                return "LaLoadFloat";

            case 5:
                return "LaLoadInt";

            case 4:
                return "LaConfig";

            case 3:
                return "LaLoad";

            case 101:
                return "LaErrMessage";

            case 2:
                return "LaNames";

            case 1:
                return "LaIndex";

            case 100:
                return "LaErrorFlag";

            default:
                break;
        }
        throw new SnmpStatusException(SnmpStatusException.noSuchObject);
    }

    protected LaEntryMBean node;
    protected SnmpStandardObjectServer objectserver = null;
}
