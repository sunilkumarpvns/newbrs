package com.elitecore.aaa.radius.sessionx.snmp.localsm.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling LOCAL-SESSION-MANAGER-MIB in standard metadata mode.
//

// java imports
//
import java.io.Serializable;

import com.sun.management.snmp.SnmpCounter;
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
// jmx imports
//
// jdmk imports
//

/**
 * The class is used for representing SNMP metadata for the "LocalSessionManagerEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.1.6.1.1.1.
 */
public class LocalSessionManagerEntryMeta extends SnmpMibEntry
     implements Serializable, SnmpStandardMetaServer {

    /**
     * Constructor for the metadata associated to "LocalSessionManagerEntry".
     */
    public LocalSessionManagerEntryMeta(SnmpMib myMib, SnmpStandardObjectServer objserv) {
        objectserver = objserv;
        varList = new int[40];
        varList[0] = 203;
        varList[1] = 202;
        varList[2] = 201;
        varList[3] = 108;
        varList[4] = 107;
        varList[5] = 106;
        varList[6] = 105;
        varList[7] = 104;
        varList[8] = 103;
        varList[9] = 102;
        varList[10] = 101;
        varList[11] = 224;
        varList[12] = 223;
        varList[13] = 222;
        varList[14] = 221;
        varList[15] = 220;
        varList[16] = 9;
        varList[17] = 8;
        varList[18] = 7;
        varList[19] = 6;
        varList[20] = 5;
        varList[21] = 4;
        varList[22] = 3;
        varList[23] = 2;
        varList[24] = 219;
        varList[25] = 218;
        varList[26] = 217;
        varList[27] = 216;
        varList[28] = 215;
        varList[29] = 214;
        varList[30] = 213;
        varList[31] = 212;
        varList[32] = 211;
        varList[33] = 210;
        varList[34] = 209;
        varList[35] = 208;
        varList[36] = 207;
        varList[37] = 206;
        varList[38] = 205;
        varList[39] = 204;
        SnmpMibNode.sort(varList);
    }

    /**
     * Get the value of a scalar variable
     */
    public SnmpValue get(long var, Object data)
        throws SnmpStatusException {
        switch((int)var) {
            case 203:
                return new SnmpCounter(node.getSmTotalAcctStopRequestTimeouts());

            case 202:
                return new SnmpCounter(node.getSmTotalAcctStopResponseRx());

            case 201:
                return new SnmpCounter(node.getSmTotalAcctStopRequestTx());

            case 108:
                return new SnmpCounter(node.getSmTotalAcctUpdateRequestRx());

            case 107:
                return new SnmpCounter(node.getSmTotalAcctStopRequestRx());

            case 106:
                return new SnmpCounter(node.getSmTotalAcctStartRequestRx());

            case 105:
                return new SnmpCounter(node.getSmTotalAcctRequestRx());

            case 104:
                return new SnmpCounter(node.getSmTotalAccessRejectTx());

            case 103:
                return new SnmpCounter(node.getSmTotalAccessRequestRx());

            case 102:
                return new SnmpCounter(node.getSmTotalRequestRx());

            case 101:
                return new SnmpTimeticks(node.getSmStatsResetTime());

            case 224:
                return new SnmpCounter(node.getSmTotalDisconnectNAckRequestInitiated());

            case 223:
                return new SnmpCounter(node.getSmTotalDisconnectNAckResourcesUnavailable());

            case 222:
                return new SnmpCounter(node.getSmTotalDisconnectNAckOtherProxyProcessingError());

            case 221:
                return new SnmpCounter(node.getSmTotalDisconnectNAckSessionCtxNotRemovable());

            case 220:
                return new SnmpCounter(node.getSmTotalDisconnectNAckSessionCtxNotFound());

            case 9:
                return new SnmpCounter(node.getSmTotalTimedOutSession());

            case 8:
                return new SnmpCounter(node.getSmTotalOverridedSession());

            case 7:
                return new SnmpCounter(node.getSmTotalInActiveSession());

            case 6:
                return new SnmpCounter(node.getSmTotalActiveSession());

            case 5:
                return new SnmpCounter(node.getSmTotalSessionDeletionFailure());

            case 4:
                return new SnmpCounter(node.getSmTotalSessionUpdationFailure());

            case 3:
                return new SnmpCounter(node.getSmTotalSessionCreationFailure());

            case 2:
                return new SnmpString(node.getSmName());

            case 1:
                throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
            case 219:
                return new SnmpCounter(node.getSmTotalDisconnectNAckRequestNotRoutable());

            case 218:
                return new SnmpCounter(node.getSmTotalDisconnectNAckAdministrativelyProhibited());

            case 217:
                return new SnmpCounter(node.getSmTotalDisconnectNAckUnsupportedExtension());

            case 216:
                return new SnmpCounter(node.getSmTotalDisconnectNAckUnsupportedService());

            case 215:
                return new SnmpCounter(node.getSmTotalDisconnectNAckInvalidRequest());

            case 214:
                return new SnmpCounter(node.getSmTotalDisconnectNAckNASIdentificationMismatch());

            case 213:
                return new SnmpCounter(node.getSmTotalDisconnectNAckMissingAttribute());

            case 212:
                return new SnmpCounter(node.getSmTotalDisconnectNAckUnsupporteAttribute());

            case 211:
                return new SnmpCounter(node.getSmTotalDisconnectNAckInvalidEAPPacket());

            case 210:
                return new SnmpCounter(node.getSmTotalDisconnectNAckResidualSessCtxRemoved());

            case 209:
                return new SnmpCounter(node.getSmTotalDisconnectResponseDropped());

            case 208:
                return new SnmpCounter(node.getSmTotalDisconnectRequestTimeouts());

            case 207:
                return new SnmpCounter(node.getSmTotalDisconnectNAck());

            case 206:
                return new SnmpCounter(node.getSmTotalDisconnectAck());

            case 205:
                return new SnmpCounter(node.getSmTotalDisconnectRequest());

            case 204:
                return new SnmpCounter(node.getSmTotalAcctStopResponseDropped());

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
            case 203:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 202:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 201:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 108:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 107:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 106:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 105:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 104:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 103:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 102:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 101:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 224:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 223:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 222:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 221:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 220:
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

            case 219:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 218:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 217:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 216:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 215:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 214:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 213:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 212:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 211:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 210:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 209:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 208:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 207:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 206:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 205:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 204:
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
            case 203:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 202:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 201:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 108:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 107:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 106:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 105:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 104:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 103:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 102:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 101:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 224:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 223:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 222:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 221:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 220:
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

            case 219:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 218:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 217:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 216:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 215:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 214:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 213:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 212:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 211:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 210:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 209:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 208:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 207:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 206:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 205:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 204:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            default:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
        }
    }

    /**
     * Allow to bind the metadata description to a specific object.
     */
    protected void setInstance(LocalSessionManagerEntryMBean var) {
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
            case 203:
            case 202:
            case 201:
            case 108:
            case 107:
            case 106:
            case 105:
            case 104:
            case 103:
            case 102:
            case 101:
            case 224:
            case 223:
            case 222:
            case 221:
            case 220:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
            case 219:
            case 218:
            case 217:
            case 216:
            case 215:
            case 214:
            case 213:
            case 212:
            case 211:
            case 210:
            case 209:
            case 208:
            case 207:
            case 206:
            case 205:
            case 204:
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
            case 203:
            case 202:
            case 201:
            case 108:
            case 107:
            case 106:
            case 105:
            case 104:
            case 103:
            case 102:
            case 101:
            case 224:
            case 223:
            case 222:
            case 221:
            case 220:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 4:
            case 3:
            case 2:
            case 219:
            case 218:
            case 217:
            case 216:
            case 215:
            case 214:
            case 213:
            case 212:
            case 211:
            case 210:
            case 209:
            case 208:
            case 207:
            case 206:
            case 205:
            case 204:
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
        switch((int)var) {
            case 1:
                return true;
            default:
                break;
        }
        return false;
    }

    /**
     * Return the name of the attribute corresponding to the SNMP variable identified by "id".
     */
    public String getAttributeName(long id)
        throws SnmpStatusException {
        switch((int)id) {
            case 203:
                return "SmTotalAcctStopRequestTimeouts";

            case 202:
                return "SmTotalAcctStopResponseRx";

            case 201:
                return "SmTotalAcctStopRequestTx";

            case 108:
                return "SmTotalAcctUpdateRequestRx";

            case 107:
                return "SmTotalAcctStopRequestRx";

            case 106:
                return "SmTotalAcctStartRequestRx";

            case 105:
                return "SmTotalAcctRequestRx";

            case 104:
                return "SmTotalAccessRejectTx";

            case 103:
                return "SmTotalAccessRequestRx";

            case 102:
                return "SmTotalRequestRx";

            case 101:
                return "SmStatsResetTime";

            case 224:
                return "SmTotalDisconnectNAckRequestInitiated";

            case 223:
                return "SmTotalDisconnectNAckResourcesUnavailable";

            case 222:
                return "SmTotalDisconnectNAckOtherProxyProcessingError";

            case 221:
                return "SmTotalDisconnectNAckSessionCtxNotRemovable";

            case 220:
                return "SmTotalDisconnectNAckSessionCtxNotFound";

            case 9:
                return "SmTotalTimedOutSession";

            case 8:
                return "SmTotalOverridedSession";

            case 7:
                return "SmTotalInActiveSession";

            case 6:
                return "SmTotalActiveSession";

            case 5:
                return "SmTotalSessionDeletionFailure";

            case 4:
                return "SmTotalSessionUpdationFailure";

            case 3:
                return "SmTotalSessionCreationFailure";

            case 2:
                return "SmName";

            case 1:
                return "SmIndex";

            case 219:
                return "SmTotalDisconnectNAckRequestNotRoutable";

            case 218:
                return "SmTotalDisconnectNAckAdministrativelyProhibited";

            case 217:
                return "SmTotalDisconnectNAckUnsupportedExtension";

            case 216:
                return "SmTotalDisconnectNAckUnsupportedService";

            case 215:
                return "SmTotalDisconnectNAckInvalidRequest";

            case 214:
                return "SmTotalDisconnectNAckNASIdentificationMismatch";

            case 213:
                return "SmTotalDisconnectNAckMissingAttribute";

            case 212:
                return "SmTotalDisconnectNAckUnsupporteAttribute";

            case 211:
                return "SmTotalDisconnectNAckInvalidEAPPacket";

            case 210:
                return "SmTotalDisconnectNAckResidualSessCtxRemoved";

            case 209:
                return "SmTotalDisconnectResponseDropped";

            case 208:
                return "SmTotalDisconnectRequestTimeouts";

            case 207:
                return "SmTotalDisconnectNAck";

            case 206:
                return "SmTotalDisconnectAck";

            case 205:
                return "SmTotalDisconnectRequest";

            case 204:
                return "SmTotalAcctStopResponseDropped";

            default:
                break;
        }
        throw new SnmpStatusException(SnmpStatusException.noSuchObject);
    }

    protected LocalSessionManagerEntryMBean node;
    protected SnmpStandardObjectServer objectserver = null;
}
