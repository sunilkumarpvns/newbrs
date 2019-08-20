package com.elitecore.diameterapi.mibs.base.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-BASE-PROTOCOL-MIB in standard metadata mode.
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
 * The class is used for representing SNMP metadata for the "DbpPerPeerInfoEntry" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.119.1.4.5.1.1.
 */
public class DbpPerPeerInfoEntryMeta extends SnmpMibEntry
     implements Serializable, SnmpStandardMetaServer {

    /**
     * Constructor for the metadata associated to "DbpPerPeerInfoEntry".
     */
    public DbpPerPeerInfoEntryMeta(SnmpMib myMib, SnmpStandardObjectServer objserv) {
        objectserver = objserv;
        varList = new int[51];
        varList[0] = 29;
        varList[1] = 28;
        varList[2] = 27;
        varList[3] = 26;
        varList[4] = 25;
        varList[5] = 24;
        varList[6] = 23;
        varList[7] = 22;
        varList[8] = 21;
        varList[9] = 20;
        varList[10] = 51;
        varList[11] = 50;
        varList[12] = 9;
        varList[13] = 8;
        varList[14] = 19;
        varList[15] = 7;
        varList[16] = 18;
        varList[17] = 49;
        varList[18] = 6;
        varList[19] = 17;
        varList[20] = 5;
        varList[21] = 16;
        varList[22] = 48;
        varList[23] = 4;
        varList[24] = 15;
        varList[25] = 47;
        varList[26] = 3;
        varList[27] = 14;
        varList[28] = 46;
        varList[29] = 2;
        varList[30] = 13;
        varList[31] = 45;
        varList[32] = 1;
        varList[33] = 12;
        varList[34] = 44;
        varList[35] = 11;
        varList[36] = 43;
        varList[37] = 10;
        varList[38] = 42;
        varList[39] = 41;
        varList[40] = 40;
        varList[41] = 39;
        varList[42] = 38;
        varList[43] = 37;
        varList[44] = 36;
        varList[45] = 35;
        varList[46] = 34;
        varList[47] = 33;
        varList[48] = 32;
        varList[49] = 31;
        varList[50] = 30;
        SnmpMibNode.sort(varList);
    }

    /**
     * Get the value of a scalar variable
     */
    public SnmpValue get(long var, Object data)
        throws SnmpStatusException {
        switch((int)var) {
            case 29:
                return new SnmpCounter(node.getDbpPerPeerStatsRAAsIn());

            case 28:
                return new SnmpCounter(node.getDbpPerPeerStatsRARsOut());

            case 27:
                return new SnmpCounter(node.getDbpPerPeerStatsRARsIn());

            case 26:
                return new SnmpCounter(node.getDbpPerPeerStatsDPAsOut());

            case 25:
                return new SnmpCounter(node.getDbpPerPeerStatsDPAsIn());

            case 24:
                return new SnmpCounter(node.getDbpPerPeerStatsDPRsOut());

            case 23:
                return new SnmpCounter(node.getDbpPerPeerStatsDPRsIn());

            case 22:
                return new SnmpCounter(node.getDbpPerPeerStatsDWAsOut());

            case 21:
                return new SnmpCounter(node.getDbpPerPeerStatsDWAsIn());

            case 20:
                return new SnmpCounter(node.getDbpPerPeerStatsDWRsOut());

            case 51:
                return new SnmpString(node.getDbpPeerIdentity());

            case 50:
                return new SnmpCounter(node.getDbpPerPeerStatsTransportDown());

            case 9:
                return new SnmpCounter(node.getDbpPerPeerStatsASAsIn());

            case 8:
                return new SnmpCounter(node.getDbpPerPeerStatsASRsOut());

            case 19:
                return new SnmpCounter(node.getDbpPerPeerStatsDWRsIn());

            case 7:
                return new SnmpCounter(node.getDbpPerPeerStatsASRsIn());

            case 18:
                return new SnmpCounter(node.getDbpPerPeerStatsCEAsOut());

            case 49:
                return new SnmpCounter(node.getDbpPerPeerStatsPermanentFailures());

            case 6:
                return new SnmpCounter(node.getDbpPerPeerStatsTimeoutConnAtmpts());

            case 17:
                return new SnmpCounter(node.getDbpPerPeerStatsCEAsIn());

            case 5:
                return new SnmpInt(node.getDbpPerPeerStatsDWCurrentStatus());

            case 16:
                return new SnmpCounter(node.getDbpPerPeerStatsCERsOut());

            case 48:
                return new SnmpCounter(node.getDbpPerPeerStatsTransientFailures());

            case 4:
                return new SnmpInt(node.getDbpPerPeerInfoWhoInitDisconnect());

            case 15:
                return new SnmpCounter(node.getDbpPerPeerStatsCERsIn());

            case 47:
                return new SnmpCounter(node.getDbpPerPeerStatsProtocolErrors());

            case 3:
                return new SnmpInt(node.getDbpPerPeerInfoLastDiscCause());

            case 14:
                return new SnmpCounter(node.getDbpPerPeerStatsACAsOut());

            case 46:
                return new SnmpCounter(node.getDbpPerPeerStatsUnknownTypes());

            case 2:
                return new SnmpTimeticks(node.getDbpPerPeerInfoStateDuration());

            case 13:
                return new SnmpCounter(node.getDbpPerPeerStatsACAsIn());

            case 45:
                return new SnmpCounter(node.getDbpPerPeerStatsEToEDupMessages());

            case 1:
                return new SnmpInt(node.getDbpPerPeerInfoState());

            case 12:
                return new SnmpCounter(node.getDbpPerPeerStatsACRsOut());

            case 44:
                return new SnmpCounter(node.getDbpPerPeerStatsHByHDropMessages());

            case 11:
                return new SnmpCounter(node.getDbpPerPeerStatsACRsIn());

            case 43:
                return new SnmpCounter(node.getDbpPerPeerStatsAccReqstsDropped());

            case 10:
                return new SnmpCounter(node.getDbpPerPeerStatsASAsOut());

            case 42:
                return new SnmpGauge(node.getDbpPerPeerStatsAccPendReqstsOut());

            case 41:
                return new SnmpCounter(node.getDbpPerPeerStatsTotalRetrans());

            case 40:
                return new SnmpCounter(node.getDbpPerPeerStatsAccRetrans());

            case 39:
                return new SnmpCounter(node.getDbpPerPeerStatsAccsNotRecorded());

            case 38:
                return new SnmpCounter(node.getDbpPerPeerStatsMalformedReqsts());

            case 37:
                return new SnmpCounter(node.getDbpPerPeerStatsAccDupRequests());

            case 36:
                return new SnmpCounter(node.getDbpPerPeerStatsRedirectEvents());

            case 35:
                return new SnmpTimeticks(node.getDbpPerPeerInfoDWReqTimer());

            case 34:
                return new SnmpCounter(node.getDbpPerPeerStatsSTAsOut());

            case 33:
                return new SnmpCounter(node.getDbpPerPeerStatsSTAsIn());

            case 32:
                return new SnmpCounter(node.getDbpPerPeerStatsSTRsOut());

            case 31:
                return new SnmpCounter(node.getDbpPerPeerStatsSTRsIn());

            case 30:
                return new SnmpCounter(node.getDbpPerPeerStatsRAAsOut());

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
            case 29:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 28:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 27:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 26:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 25:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 24:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 23:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 22:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 21:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 20:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 51:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 50:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 9:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 8:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 19:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 7:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 18:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 49:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 6:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 17:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 16:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 48:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 4:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 15:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 47:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 14:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 46:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 13:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 45:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 1:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 12:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 44:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 11:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 43:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 10:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 42:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 41:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 40:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 39:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 38:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 37:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 36:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 35:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 34:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 33:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 32:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 31:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 30:
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
            case 29:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 28:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 27:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 26:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 25:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 24:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 23:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 22:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 21:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 20:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 51:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 50:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 9:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 8:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 19:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 7:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 18:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 49:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 6:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 17:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 16:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 48:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 4:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 15:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 47:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 14:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 46:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 13:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 45:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 1:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 12:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 44:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 11:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 43:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 10:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 42:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 41:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 40:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 39:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 38:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 37:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 36:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 35:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 34:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 33:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 32:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 31:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 30:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            default:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
        }
    }

    /**
     * Allow to bind the metadata description to a specific object.
     */
    protected void setInstance(DbpPerPeerInfoEntryMBean var) {
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
            case 29:
            case 28:
            case 27:
            case 26:
            case 25:
            case 24:
            case 23:
            case 22:
            case 21:
            case 20:
            case 51:
            case 50:
            case 9:
            case 8:
            case 19:
            case 7:
            case 18:
            case 49:
            case 6:
            case 17:
            case 5:
            case 16:
            case 48:
            case 4:
            case 15:
            case 47:
            case 3:
            case 14:
            case 46:
            case 2:
            case 13:
            case 45:
            case 1:
            case 12:
            case 44:
            case 11:
            case 43:
            case 10:
            case 42:
            case 41:
            case 40:
            case 39:
            case 38:
            case 37:
            case 36:
            case 35:
            case 34:
            case 33:
            case 32:
            case 31:
            case 30:
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
            case 29:
            case 28:
            case 27:
            case 26:
            case 25:
            case 24:
            case 23:
            case 22:
            case 21:
            case 20:
            case 51:
            case 50:
            case 9:
            case 8:
            case 19:
            case 7:
            case 18:
            case 49:
            case 6:
            case 17:
            case 5:
            case 16:
            case 48:
            case 4:
            case 15:
            case 47:
            case 3:
            case 14:
            case 46:
            case 2:
            case 13:
            case 45:
            case 1:
            case 12:
            case 44:
            case 11:
            case 43:
            case 10:
            case 42:
            case 41:
            case 40:
            case 39:
            case 38:
            case 37:
            case 36:
            case 35:
            case 34:
            case 33:
            case 32:
            case 31:
            case 30:
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
            case 29:
                return "DbpPerPeerStatsRAAsIn";

            case 28:
                return "DbpPerPeerStatsRARsOut";

            case 27:
                return "DbpPerPeerStatsRARsIn";

            case 26:
                return "DbpPerPeerStatsDPAsOut";

            case 25:
                return "DbpPerPeerStatsDPAsIn";

            case 24:
                return "DbpPerPeerStatsDPRsOut";

            case 23:
                return "DbpPerPeerStatsDPRsIn";

            case 22:
                return "DbpPerPeerStatsDWAsOut";

            case 21:
                return "DbpPerPeerStatsDWAsIn";

            case 20:
                return "DbpPerPeerStatsDWRsOut";

            case 51:
                return "DbpPeerIdentity";

            case 50:
                return "DbpPerPeerStatsTransportDown";

            case 9:
                return "DbpPerPeerStatsASAsIn";

            case 8:
                return "DbpPerPeerStatsASRsOut";

            case 19:
                return "DbpPerPeerStatsDWRsIn";

            case 7:
                return "DbpPerPeerStatsASRsIn";

            case 18:
                return "DbpPerPeerStatsCEAsOut";

            case 49:
                return "DbpPerPeerStatsPermanentFailures";

            case 6:
                return "DbpPerPeerStatsTimeoutConnAtmpts";

            case 17:
                return "DbpPerPeerStatsCEAsIn";

            case 5:
                return "DbpPerPeerStatsDWCurrentStatus";

            case 16:
                return "DbpPerPeerStatsCERsOut";

            case 48:
                return "DbpPerPeerStatsTransientFailures";

            case 4:
                return "DbpPerPeerInfoWhoInitDisconnect";

            case 15:
                return "DbpPerPeerStatsCERsIn";

            case 47:
                return "DbpPerPeerStatsProtocolErrors";

            case 3:
                return "DbpPerPeerInfoLastDiscCause";

            case 14:
                return "DbpPerPeerStatsACAsOut";

            case 46:
                return "DbpPerPeerStatsUnknownTypes";

            case 2:
                return "DbpPerPeerInfoStateDuration";

            case 13:
                return "DbpPerPeerStatsACAsIn";

            case 45:
                return "DbpPerPeerStatsEToEDupMessages";

            case 1:
                return "DbpPerPeerInfoState";

            case 12:
                return "DbpPerPeerStatsACRsOut";

            case 44:
                return "DbpPerPeerStatsHByHDropMessages";

            case 11:
                return "DbpPerPeerStatsACRsIn";

            case 43:
                return "DbpPerPeerStatsAccReqstsDropped";

            case 10:
                return "DbpPerPeerStatsASAsOut";

            case 42:
                return "DbpPerPeerStatsAccPendReqstsOut";

            case 41:
                return "DbpPerPeerStatsTotalRetrans";

            case 40:
                return "DbpPerPeerStatsAccRetrans";

            case 39:
                return "DbpPerPeerStatsAccsNotRecorded";

            case 38:
                return "DbpPerPeerStatsMalformedReqsts";

            case 37:
                return "DbpPerPeerStatsAccDupRequests";

            case 36:
                return "DbpPerPeerStatsRedirectEvents";

            case 35:
                return "DbpPerPeerInfoDWReqTimer";

            case 34:
                return "DbpPerPeerStatsSTAsOut";

            case 33:
                return "DbpPerPeerStatsSTAsIn";

            case 32:
                return "DbpPerPeerStatsSTRsOut";

            case 31:
                return "DbpPerPeerStatsSTRsIn";

            case 30:
                return "DbpPerPeerStatsRAAsOut";

            default:
                break;
        }
        throw new SnmpStatusException(SnmpStatusException.noSuchObject);
    }

    protected DbpPerPeerInfoEntryMBean node;
    protected SnmpStandardObjectServer objectserver = null;
}
