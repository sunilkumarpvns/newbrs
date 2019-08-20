package com.elitecore.diameterapi.mibs.cc.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-CC-APPLICATION-MIB in standard metadata mode.
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
 * The class is used for representing SNMP metadata for the "DccaPerPeerStatsEntry" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.4006.2.1.3.1.1.
 */
public class DccaPerPeerStatsEntryMeta extends SnmpMibEntry
     implements Serializable, SnmpStandardMetaServer {

    /**
     * Constructor for the metadata associated to "DccaPerPeerStatsEntry".
     */
    public DccaPerPeerStatsEntryMeta(SnmpMib myMib, SnmpStandardObjectServer objserv) {
        objectserver = objserv;
        varList = new int[23];
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
        varList[16] = 24;
        varList[17] = 3;
        varList[18] = 2;
        varList[19] = 23;
        varList[20] = 22;
        varList[21] = 21;
        varList[22] = 20;
        SnmpMibNode.sort(varList);
    }

    /**
     * Get the value of a scalar variable
     */
    public SnmpValue get(long var, Object data)
        throws SnmpStatusException {
        switch((int)var) {
            case 19:
                return new SnmpCounter(node.getDccaPerPeerStatsAAADropped());

            case 18:
                return new SnmpCounter(node.getDccaPerPeerStatsAAAIn());

            case 17:
                return new SnmpCounter(node.getDccaPerPeerStatsAARDropped());

            case 16:
                return new SnmpCounter(node.getDccaPerPeerStatsAAROut());

            case 15:
                return new SnmpCounter(node.getDccaPerPeerStatsSTADropped());

            case 14:
                return new SnmpCounter(node.getDccaPerPeerStatsSTAIn());

            case 13:
                return new SnmpCounter(node.getDccaPerPeerStatsSTRDropped());

            case 12:
                return new SnmpCounter(node.getDccaPerPeerStatsSTROut());

            case 11:
                return new SnmpCounter(node.getDccaPerPeerStatsRAADropped());

            case 10:
                return new SnmpCounter(node.getDccaPerPeerStatsRAAOut());

            case 9:
                return new SnmpCounter(node.getDccaPerPeerStatsRARDropped());

            case 8:
                return new SnmpCounter(node.getDccaPerPeerStatsRARIn());

            case 7:
                return new SnmpCounter(node.getDccaPerPeerStatsCCADropped());

            case 6:
                return new SnmpCounter(node.getDccaPerPeerStatsCCAOut());

            case 5:
                return new SnmpCounter(node.getDccaPerPeerStatsCCAIn());

            case 4:
                return new SnmpCounter(node.getDccaPerPeerStatsCCRDropped());

            case 24:
                return new SnmpString(node.getDccaPeerIdentity());

            case 3:
                return new SnmpCounter(node.getDccaPerPeerStatsCCROut());

            case 2:
                return new SnmpCounter(node.getDccaPerPeerStatsCCRIn());

            case 23:
                return new SnmpCounter(node.getDccaPerPeerStatsASADropped());

            case 22:
                return new SnmpCounter(node.getDccaPerPeerStatsASAOut());

            case 21:
                return new SnmpCounter(node.getDccaPerPeerStatsASRDropped());

            case 20:
                return new SnmpCounter(node.getDccaPerPeerStatsASRIn());

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
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 6:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 4:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 24:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 23:
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
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 6:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 4:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 24:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 23:
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
    protected void setInstance(DccaPerPeerStatsEntryMBean var) {
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
            case 24:
            case 3:
            case 2:
            case 23:
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
            case 24:
            case 3:
            case 2:
            case 23:
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
                return "DccaPerPeerStatsAAADropped";

            case 18:
                return "DccaPerPeerStatsAAAIn";

            case 17:
                return "DccaPerPeerStatsAARDropped";

            case 16:
                return "DccaPerPeerStatsAAROut";

            case 15:
                return "DccaPerPeerStatsSTADropped";

            case 14:
                return "DccaPerPeerStatsSTAIn";

            case 13:
                return "DccaPerPeerStatsSTRDropped";

            case 12:
                return "DccaPerPeerStatsSTROut";

            case 11:
                return "DccaPerPeerStatsRAADropped";

            case 10:
                return "DccaPerPeerStatsRAAOut";

            case 9:
                return "DccaPerPeerStatsRARDropped";

            case 8:
                return "DccaPerPeerStatsRARIn";

            case 7:
                return "DccaPerPeerStatsCCADropped";

            case 6:
                return "DccaPerPeerStatsCCAOut";

            case 5:
                return "DccaPerPeerStatsCCAIn";

            case 4:
                return "DccaPerPeerStatsCCRDropped";

            case 24:
                return "DccaPeerIdentity";

            case 3:
                return "DccaPerPeerStatsCCROut";

            case 2:
                return "DccaPerPeerStatsCCRIn";

            case 23:
                return "DccaPerPeerStatsASADropped";

            case 22:
                return "DccaPerPeerStatsASAOut";

            case 21:
                return "DccaPerPeerStatsASRDropped";

            case 20:
                return "DccaPerPeerStatsASRIn";

            default:
                break;
        }
        throw new SnmpStatusException(SnmpStatusException.noSuchObject);
    }

    protected DccaPerPeerStatsEntryMBean node;
    protected SnmpStandardObjectServer objectserver = null;
}
