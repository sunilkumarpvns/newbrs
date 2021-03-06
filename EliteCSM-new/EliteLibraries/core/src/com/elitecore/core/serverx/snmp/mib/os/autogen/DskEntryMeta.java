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
 * The class is used for representing SNMP metadata for the "DskEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.2021.9.1.
 */
public class DskEntryMeta extends SnmpMibEntry
     implements Serializable, SnmpStandardMetaServer {

    /**
     * Constructor for the metadata associated to "DskEntry".
     */
    public DskEntryMeta(SnmpMib myMib, SnmpStandardObjectServer objserv) {
        objectserver = objserv;
        varList = new int[18];
        varList[0] = 101;
        varList[1] = 100;
        varList[2] = 16;
        varList[3] = 15;
        varList[4] = 14;
        varList[5] = 13;
        varList[6] = 12;
        varList[7] = 11;
        varList[8] = 10;
        varList[9] = 9;
        varList[10] = 8;
        varList[11] = 7;
        varList[12] = 6;
        varList[13] = 5;
        varList[14] = 4;
        varList[15] = 3;
        varList[16] = 2;
        varList[17] = 1;
        SnmpMibNode.sort(varList);
    }

    /**
     * Get the value of a scalar variable
     */
    public SnmpValue get(long var, Object data)
        throws SnmpStatusException {
        switch((int)var) {
            case 101:
                return new SnmpString(node.getDskErrorMsg());

            case 100:
                return new SnmpInt(node.getDskErrorFlag());

            case 16:
                return new SnmpGauge(node.getDskUsedHigh());

            case 15:
                return new SnmpGauge(node.getDskUsedLow());

            case 14:
                return new SnmpGauge(node.getDskAvailHigh());

            case 13:
                return new SnmpGauge(node.getDskAvailLow());

            case 12:
                return new SnmpGauge(node.getDskTotalHigh());

            case 11:
                return new SnmpGauge(node.getDskTotalLow());

            case 10:
                return new SnmpInt(node.getDskPercentNode());

            case 9:
                return new SnmpInt(node.getDskPercent());

            case 8:
                return new SnmpInt(node.getDskUsed());

            case 7:
                return new SnmpInt(node.getDskAvail());

            case 6:
                return new SnmpInt(node.getDskTotal());

            case 5:
                return new SnmpInt(node.getDskMinPercent());

            case 4:
                return new SnmpInt(node.getDskMinimum());

            case 3:
                return new SnmpString(node.getDskDevice());

            case 2:
                return new SnmpString(node.getDskPath());

            case 1:
                return new SnmpInt(node.getDskIndex());

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
            case 101:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 100:
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
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

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
            case 101:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 100:
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
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

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

            default:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
        }
    }

    /**
     * Allow to bind the metadata description to a specific object.
     */
    protected void setInstance(DskEntryMBean var) {
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
            case 101:
            case 100:
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
            case 101:
            case 100:
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
            case 101:
                return "DskErrorMsg";

            case 100:
                return "DskErrorFlag";

            case 16:
                return "DskUsedHigh";

            case 15:
                return "DskUsedLow";

            case 14:
                return "DskAvailHigh";

            case 13:
                return "DskAvailLow";

            case 12:
                return "DskTotalHigh";

            case 11:
                return "DskTotalLow";

            case 10:
                return "DskPercentNode";

            case 9:
                return "DskPercent";

            case 8:
                return "DskUsed";

            case 7:
                return "DskAvail";

            case 6:
                return "DskTotal";

            case 5:
                return "DskMinPercent";

            case 4:
                return "DskMinimum";

            case 3:
                return "DskDevice";

            case 2:
                return "DskPath";

            case 1:
                return "DskIndex";

            default:
                break;
        }
        throw new SnmpStatusException(SnmpStatusException.noSuchObject);
    }

    protected DskEntryMBean node;
    protected SnmpStandardObjectServer objectserver = null;
}
