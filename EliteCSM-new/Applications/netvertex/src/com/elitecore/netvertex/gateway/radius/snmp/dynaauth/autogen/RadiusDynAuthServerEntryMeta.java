package com.elitecore.netvertex.gateway.radius.snmp.dynaauth.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RADIUS-DYNAUTH-CLIENT-MIB in standard metadata mode.
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
 * The class is used for representing SNMP metadata for the "RadiusDynAuthServerEntry" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.145.1.2.1.
 */
public class RadiusDynAuthServerEntryMeta extends SnmpMibEntry
     implements Serializable, SnmpStandardMetaServer {

    /**
     * Constructor for the metadata associated to "RadiusDynAuthServerEntry".
     */
    public RadiusDynAuthServerEntryMeta(SnmpMib myMib, SnmpStandardObjectServer objserv) {
        objectserver = objserv;
        varList = new int[31];
        varList[0] = 32;
        varList[1] = 31;
        varList[2] = 30;
        varList[3] = 19;
        varList[4] = 18;
        varList[5] = 17;
        varList[6] = 16;
        varList[7] = 15;
        varList[8] = 14;
        varList[9] = 13;
        varList[10] = 12;
        varList[11] = 11;
        varList[12] = 10;
        varList[13] = 9;
        varList[14] = 8;
        varList[15] = 29;
        varList[16] = 7;
        varList[17] = 28;
        varList[18] = 6;
        varList[19] = 27;
        varList[20] = 5;
        varList[21] = 26;
        varList[22] = 4;
        varList[23] = 25;
        varList[24] = 3;
        varList[25] = 24;
        varList[26] = 2;
        varList[27] = 23;
        varList[28] = 22;
        varList[29] = 21;
        varList[30] = 20;
        SnmpMibNode.sort(varList);
    }

    /**
     * Get the value of a scalar variable
     */
    public SnmpValue get(long var, Object data)
        throws SnmpStatusException {
        switch((int)var) {
            case 32:
                return new SnmpTimeticks(node.getRadiusDynAuthClientCounterDiscontinuity());

            case 31:
                return new SnmpCounter(node.getRadiusDynAuthClientUnknownTypes());

            case 30:
                return new SnmpCounter(node.getRadiusDynAuthClientCoAPacketsDropped());

            case 19:
                return new SnmpCounter(node.getRadiusDynAuthClientCoARequests());

            case 18:
                return new SnmpCounter(node.getRadiusDynAuthClientDisconPacketsDropped());

            case 17:
                return new SnmpCounter(node.getRadiusDynAuthClientDisconTimeouts());

            case 16:
                return new SnmpGauge(node.getRadiusDynAuthClientDisconPendingRequests());

            case 15:
                return new SnmpCounter(node.getRadiusDynAuthClientDisconBadAuthenticators());

            case 14:
                return new SnmpCounter(node.getRadiusDynAuthClientMalformedDisconResponses());

            case 13:
                return new SnmpCounter(node.getRadiusDynAuthClientDisconNakSessNoContext());

            case 12:
                return new SnmpCounter(node.getRadiusDynAuthClientDisconNakAuthOnlyRequest());

            case 11:
                return new SnmpCounter(node.getRadiusDynAuthClientDisconNaks());

            case 10:
                return new SnmpCounter(node.getRadiusDynAuthClientDisconAcks());

            case 9:
                return new SnmpCounter(node.getRadiusDynAuthClientDisconRetransmissions());

            case 8:
                return new SnmpCounter(node.getRadiusDynAuthClientDisconAuthOnlyRequests());

            case 29:
                return new SnmpCounter(node.getRadiusDynAuthClientCoATimeouts());

            case 7:
                return new SnmpCounter(node.getRadiusDynAuthClientDisconRequests());

            case 28:
                return new SnmpGauge(node.getRadiusDynAuthClientCoAPendingRequests());

            case 6:
                return new SnmpTimeticks(node.getRadiusDynAuthClientRoundTripTime());

            case 27:
                return new SnmpCounter(node.getRadiusDynAuthClientCoABadAuthenticators());

            case 5:
                return new SnmpString(node.getRadiusDynAuthServerID());

            case 26:
                return new SnmpCounter(node.getRadiusDynAuthClientMalformedCoAResponses());

            case 4:
                return new SnmpGauge(node.getRadiusDynAuthServerClientPortNumber());

            case 25:
                return new SnmpCounter(node.getRadiusDynAuthClientCoANakSessNoContext());

            case 3:
                return new SnmpString(node.getRadiusDynAuthServerAddress());

            case 24:
                return new SnmpCounter(node.getRadiusDynAuthClientCoANakAuthOnlyRequest());

            case 2:
                return new SnmpInt(node.getRadiusDynAuthServerAddressType());

            case 23:
                return new SnmpCounter(node.getRadiusDynAuthClientCoANaks());

            case 1:
                throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
            case 22:
                return new SnmpCounter(node.getRadiusDynAuthClientCoAAcks());

            case 21:
                return new SnmpCounter(node.getRadiusDynAuthClientCoARetransmissions());

            case 20:
                return new SnmpCounter(node.getRadiusDynAuthClientCoAAuthOnlyRequest());

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
            case 32:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 31:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 30:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

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

            case 29:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 7:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 28:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 6:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 27:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 26:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 4:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 25:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 24:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 23:
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
            case 32:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 31:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 30:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

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

            case 29:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 7:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 28:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 6:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 27:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 26:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 4:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 25:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 24:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 23:
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
    protected void setInstance(RadiusDynAuthServerEntryMBean var) {
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
            case 32:
            case 31:
            case 30:
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
            case 29:
            case 7:
            case 28:
            case 6:
            case 27:
            case 5:
            case 26:
            case 4:
            case 25:
            case 3:
            case 24:
            case 2:
            case 23:
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
            case 32:
            case 31:
            case 30:
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
            case 29:
            case 7:
            case 28:
            case 6:
            case 27:
            case 5:
            case 26:
            case 4:
            case 25:
            case 3:
            case 24:
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
            case 32:
                return "RadiusDynAuthClientCounterDiscontinuity";

            case 31:
                return "RadiusDynAuthClientUnknownTypes";

            case 30:
                return "RadiusDynAuthClientCoAPacketsDropped";

            case 19:
                return "RadiusDynAuthClientCoARequests";

            case 18:
                return "RadiusDynAuthClientDisconPacketsDropped";

            case 17:
                return "RadiusDynAuthClientDisconTimeouts";

            case 16:
                return "RadiusDynAuthClientDisconPendingRequests";

            case 15:
                return "RadiusDynAuthClientDisconBadAuthenticators";

            case 14:
                return "RadiusDynAuthClientMalformedDisconResponses";

            case 13:
                return "RadiusDynAuthClientDisconNakSessNoContext";

            case 12:
                return "RadiusDynAuthClientDisconNakAuthOnlyRequest";

            case 11:
                return "RadiusDynAuthClientDisconNaks";

            case 10:
                return "RadiusDynAuthClientDisconAcks";

            case 9:
                return "RadiusDynAuthClientDisconRetransmissions";

            case 8:
                return "RadiusDynAuthClientDisconAuthOnlyRequests";

            case 29:
                return "RadiusDynAuthClientCoATimeouts";

            case 7:
                return "RadiusDynAuthClientDisconRequests";

            case 28:
                return "RadiusDynAuthClientCoAPendingRequests";

            case 6:
                return "RadiusDynAuthClientRoundTripTime";

            case 27:
                return "RadiusDynAuthClientCoABadAuthenticators";

            case 5:
                return "RadiusDynAuthServerID";

            case 26:
                return "RadiusDynAuthClientMalformedCoAResponses";

            case 4:
                return "RadiusDynAuthServerClientPortNumber";

            case 25:
                return "RadiusDynAuthClientCoANakSessNoContext";

            case 3:
                return "RadiusDynAuthServerAddress";

            case 24:
                return "RadiusDynAuthClientCoANakAuthOnlyRequest";

            case 2:
                return "RadiusDynAuthServerAddressType";

            case 23:
                return "RadiusDynAuthClientCoANaks";

            case 1:
                return "RadiusDynAuthServerIndex";

            case 22:
                return "RadiusDynAuthClientCoAAcks";

            case 21:
                return "RadiusDynAuthClientCoARetransmissions";

            case 20:
                return "RadiusDynAuthClientCoAAuthOnlyRequest";

            default:
                break;
        }
        throw new SnmpStatusException(SnmpStatusException.noSuchObject);
    }

    protected RadiusDynAuthServerEntryMBean node;
    protected SnmpStandardObjectServer objectserver = null;
}
