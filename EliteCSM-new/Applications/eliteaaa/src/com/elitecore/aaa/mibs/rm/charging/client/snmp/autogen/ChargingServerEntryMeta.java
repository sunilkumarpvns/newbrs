package com.elitecore.aaa.mibs.rm.charging.client.snmp.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling CHARGING-SERVICE-CLIENT-MIB in standard metadata mode.
//

// java imports
//
import java.io.Serializable;

import com.sun.management.snmp.SnmpCounter;
import com.sun.management.snmp.SnmpIpAddress;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.SnmpString;
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
 * The class is used for representing SNMP metadata for the "ChargingServerEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.1.8.2.2.1.1.
 */
public class ChargingServerEntryMeta extends SnmpMibEntry
     implements Serializable, SnmpStandardMetaServer {

    /**
     * Constructor for the metadata associated to "ChargingServerEntry".
     */
    public ChargingServerEntryMeta(SnmpMib myMib, SnmpStandardObjectServer objserv) {
        objectserver = objserv;
        varList = new int[16];
        varList[0] = 9;
        varList[1] = 8;
        varList[2] = 17;
        varList[3] = 7;
        varList[4] = 16;
        varList[5] = 15;
        varList[6] = 6;
        varList[7] = 5;
        varList[8] = 14;
        varList[9] = 13;
        varList[10] = 4;
        varList[11] = 12;
        varList[12] = 3;
        varList[13] = 11;
        varList[14] = 2;
        varList[15] = 10;
        SnmpMibNode.sort(varList);
    }

    /**
     * Get the value of a scalar variable
     */
    public SnmpValue get(long var, Object data)
        throws SnmpStatusException {
        switch((int)var) {
            case 9:
                return new SnmpCounter(node.getChargingRequestRetransmission());

            case 8:
                return new SnmpCounter(node.getChargingRequestTimeout());

            case 17:
                return new SnmpCounter(node.getChargingAcctUpdateRequest());

            case 7:
                return new SnmpCounter(node.getChargingUnknownTypes());

            case 16:
                return new SnmpCounter(node.getChargingAcctStopRequest());

            case 15:
                return new SnmpCounter(node.getChargingAcctStartRequest());

            case 6:
                return new SnmpCounter(node.getChargingPacketsDropped());

            case 5:
                return new SnmpCounter(node.getChargingResponses());

            case 14:
                return new SnmpCounter(node.getChargingAcctResponse());

            case 13:
                return new SnmpCounter(node.getChargingAcctRequest());

            case 4:
                return new SnmpCounter(node.getChargingRequests());

            case 12:
                return new SnmpCounter(node.getChargingAccessReject());

            case 3:
                return new SnmpIpAddress(node.getChargingServerAddress());

            case 11:
                return new SnmpCounter(node.getChargingAccessAccept());

            case 2:
                return new SnmpString(node.getChargingServerName());

            case 10:
                return new SnmpCounter(node.getChargingAccessRequest());

            case 1:
                throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
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

            case 17:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 7:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 16:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 15:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 6:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 14:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 13:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 4:
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

            case 17:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 7:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 16:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 15:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 6:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 14:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 13:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 4:
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
    protected void setInstance(ChargingServerEntryMBean var) {
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
            case 17:
            case 7:
            case 16:
            case 15:
            case 6:
            case 5:
            case 14:
            case 13:
            case 4:
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
            case 17:
            case 7:
            case 16:
            case 15:
            case 6:
            case 5:
            case 14:
            case 13:
            case 4:
            case 12:
            case 3:
            case 11:
            case 2:
            case 10:
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
            case 9:
                return "ChargingRequestRetransmission";

            case 8:
                return "ChargingRequestTimeout";

            case 17:
                return "ChargingAcctUpdateRequest";

            case 7:
                return "ChargingUnknownTypes";

            case 16:
                return "ChargingAcctStopRequest";

            case 15:
                return "ChargingAcctStartRequest";

            case 6:
                return "ChargingPacketsDropped";

            case 5:
                return "ChargingResponses";

            case 14:
                return "ChargingAcctResponse";

            case 13:
                return "ChargingAcctRequest";

            case 4:
                return "ChargingRequests";

            case 12:
                return "ChargingAccessReject";

            case 3:
                return "ChargingServerAddress";

            case 11:
                return "ChargingAccessAccept";

            case 2:
                return "ChargingServerName";

            case 10:
                return "ChargingAccessRequest";

            case 1:
                return "ChargingServerIndex";

            default:
                break;
        }
        throw new SnmpStatusException(SnmpStatusException.noSuchObject);
    }

    protected ChargingServerEntryMBean node;
    protected SnmpStandardObjectServer objectserver = null;
}